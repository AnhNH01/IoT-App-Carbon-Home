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
import com.example.iotapp.api.adapter.DeviceAdapter;
import com.example.iotapp.api.adapter.LightAdapter;
import com.example.iotapp.models.Device;
import com.example.iotapp.models.RoomDevices;

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

public class RoomActivity extends AppCompatActivity implements DeviceAdapter.OnDeviceClickedListener, LightAdapter.OnLightClickedListener {

    private static final String TAG = "RoomMqtt";
    private RecyclerView recyclerViewDevices;
    private RecyclerView recyclerViewLights;

    private TextView textViewRoomName;
    private Button buttonRefresh;

    private MqttAndroidClient client;

    private TextView textViewTemperatureValue;
    private TextView textViewHumidityValue;

    private List<Device> mDeviceList;

    private List<Device> mLightList;
    private static String roomId;
    private String roomName;
    //private String apiToken;

    //private String sensorType, sensorValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        textViewTemperatureValue = findViewById(R.id.room_layout_txt_view_temp_value);
        textViewHumidityValue = findViewById(R.id.room_layout_txt_view_humid_value);
        recyclerViewDevices = findViewById(R.id.rcv_devices);
        recyclerViewLights = findViewById(R.id.rcv_lights);
        textViewRoomName = findViewById(R.id.room_layout_txt_view_room_name);
        buttonRefresh = findViewById(R.id.room_layout_btn_refresh);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //apiToken = (String) bundle.get("api_token");
            roomId = (String) bundle.get("room_id");
            roomName = (String) bundle.get("room_name");
            textViewRoomName.setText(roomName);
        }
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://" + MenuActivity.getBrokerHost() + ":1883", clientId);

        mDeviceList = new ArrayList<>();
        mLightList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewDevices.setLayoutManager(linearLayoutManager);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        recyclerViewLights.setLayoutManager(linearLayoutManager1);
        recyclerViewLights.setAdapter(new LightAdapter(mLightList, this));

        recyclerViewDevices.setAdapter(new DeviceAdapter(mDeviceList, this));
        connectToBroker();
        callApiGetDevices();
        setData();

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callApiGetDevices();
            }
        });

    }

    private void callApiGetDevices() {
        ApiService.apiService.getDevicesInRoom("Bearer " + MenuActivity.getApiToken(), roomId).enqueue(new Callback<RoomDevices>() {
            @Override
            public void onResponse(Call<RoomDevices> call, Response<RoomDevices> response) {
                mDeviceList = response.body().getSensors();
                mLightList = response.body().getLeds();

                LightAdapter lightAdapter = new LightAdapter(mLightList, RoomActivity.this::onLightClicked);
                DeviceAdapter deviceAdapter = new DeviceAdapter(mDeviceList, RoomActivity.this::onDeviceClicked);
                recyclerViewDevices.setAdapter(deviceAdapter);
                recyclerViewLights.setAdapter(lightAdapter);

                if (mDeviceList == null) {
                    Log.d("Devices", "Devices null");
                }
                if (mDeviceList.size() == 0)
                    Toast.makeText(RoomActivity.this, "There are no sensors in this room", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<RoomDevices> call, Throwable t) {
                Toast.makeText(RoomActivity.this, "Cant get devices", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeviceClicked(int position) {
        Intent intent = new Intent(this, SensorActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", mDeviceList.get(position).getId());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void connectToBroker() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(MenuActivity.getHomeId());
            options.setPassword(MenuActivity.getBrokerPassword().toCharArray());
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "connect onSuccess");

                    String topic = "CarbonIoT/" + MenuActivity.getHomeId() + "/data";
                    Log.d(TAG, "topic: " + topic);
                    int qos = 1;
                    try {
                        IMqttToken subToken = client.subscribe(topic, qos);
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

    private void setData() {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(RoomActivity.this, "Lost connection to broker", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = new String(message.getPayload());
                Log.d(TAG, "messageArrived: " + msg);
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    if (MenuActivity.getHomeId().equals(jsonObject.getString("homeId"))) {
                        if (roomId.equals(jsonObject.getString("roomId"))) {
                            if (jsonObject.getString("name").equals("temperature"))
                                textViewTemperatureValue.setText(String.valueOf(jsonObject.getDouble("value")));

                            if (jsonObject.getString("name").equals("humidity"))
                                textViewHumidityValue.setText(String.valueOf(jsonObject.getDouble("value")));
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


    @Override
    public void onLightClicked(int position) {
        Intent intent = new Intent(this, LightActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("light_id", mLightList.get(position).getId());
        intent.putExtras(bundle);
        startActivity(intent);

    }
}