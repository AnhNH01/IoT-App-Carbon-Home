package com.example.iotapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iotapp.api.ApiService;
import com.example.iotapp.api.adapter.RoomAdapter;
import com.example.iotapp.models.Room;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity implements RoomAdapter.OnRoomClickedListener {

    private static final String TAG = "MenuActivity";
    // layout components
    private RecyclerView recyclerViewRoom;
    private TextView textViewName;
    private TextView textViewHomeId;
    private Button buttonCallApi;

    private List<Room> mRoomList;
    private static String apiToken = "";
    private String name = "";
    private static String homeId = "";
    private static String brokerPassword = "abc";
    private static String brokerHost = "";

    public static String getBrokerHost() {
        return brokerHost;
    }

    public static String getBrokerPassword() {
        return brokerPassword;
    }


    private MqttAndroidClient alert_client;

    public static String getApiToken() {
        return apiToken;
    }

    public static String getHomeId() {
        return homeId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        // get value from bundle
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = (String) bundle.get("name");
            homeId = (String) bundle.get("home_id");
            apiToken = (String) bundle.get("api_token");
            brokerPassword = bundle.getString("broker_password");
            brokerHost = bundle.getString("broker_host");
        }

        // set up value for display
        buttonCallApi = findViewById(R.id.btn_call_api);
        textViewName = findViewById(R.id.txt_view_name);
        textViewHomeId = findViewById(R.id.txt_view_home_id);
        textViewName.setText(name);
        textViewHomeId.setText(homeId);

        Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();

        // mqtt client
        String clientId = MqttClient.generateClientId();
        alert_client = new MqttAndroidClient(this.getApplicationContext(), "tcp://" + MenuActivity.getBrokerHost() + ":1883", clientId);
        //alert_client = new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.202.139:1883", clientId);
        recyclerViewRoom = findViewById(R.id.rcv_room);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewRoom.setLayoutManager(linearLayoutManager);

        mRoomList = new ArrayList<>();
        recyclerViewRoom.setAdapter(new RoomAdapter(mRoomList, this));


        callApiGetAllRoom();
        connectToBroker();
        alert();
        Log.d("HomeId", homeId);
        buttonCallApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callApiGetAllRoom();
            }
        });


    }


    private void callApiGetAllRoom() {
        ApiService.apiService.getAllRooms("Bearer " + apiToken).enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                mRoomList = response.body();


                RoomAdapter roomAdapter = new RoomAdapter(mRoomList, MenuActivity.this);
                recyclerViewRoom.setAdapter(roomAdapter);
//                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MenuActivity.this);
//                recyclerViewRoom.setLayoutManager(linearLayoutManager);

                if (mRoomList == null)
                    Log.d("Room", "call api return null");
                Log.d("Room", mRoomList.get(0).toString());
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Toast.makeText(MenuActivity.this, "Get rooms failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRoomClicked(int position) {
        Intent intent = new Intent(this, RoomActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("api_token", apiToken);
        bundle.putString("room_id", mRoomList.get(position).getId());
        bundle.putString("room_name", mRoomList.get(position).getName());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void connectToBroker() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(homeId);
            options.setPassword(brokerPassword.toCharArray());
            IMqttToken token = alert_client.connect(options);
            //IMqttToken token = alert_client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "connect onSuccess");

                    String topic = "CarbonIoT/" + MenuActivity.getHomeId() + "/data";
                    Log.d(TAG, "topic: " + topic);
                    int qos = 1;
                    try {
                        IMqttToken subToken = alert_client.subscribe(topic, qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // The message was published
                                Log.d(TAG, "onSuccess: subscribed");
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                // The subscription could not be performed, maybe the user was not
                                // authorized to subscribe on the specified topic e.g. using wildcards
                                Log.d(TAG, "onFailure: subscribe " + exception.getMessage());

                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "connect onFailure " + exception.getMessage());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void alert() {
        alert_client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = new String(message.getPayload());
                Log.d(TAG, "messageArrived: " + msg);
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    if (homeId.equals(jsonObject.getString("homeId"))) {
                        if (jsonObject.getString("name").equals("temperature")) {
                            if (jsonObject.getDouble("value") >= 40) {
                                Toast.makeText(getApplicationContext(), "High Temperature Alert", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

}