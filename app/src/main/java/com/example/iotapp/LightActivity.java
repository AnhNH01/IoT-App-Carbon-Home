package com.example.iotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

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

import java.io.UnsupportedEncodingException;

public class LightActivity extends AppCompatActivity {
    private static final String TAG = "LightActivity";
    private MqttAndroidClient light_client;
    private String status;
    private String lightId = "";

    private TextView textViewLightStatus, textViewLightId;
    private Button buttonTurnOn, buttonTurnOff;
    private ImageView imageViewLight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        Bundle bundle = getIntent().getExtras();
        imageViewLight = findViewById(R.id.light_layout_img_view_light);
        textViewLightStatus = findViewById(R.id.light_layout_txt_view_status);
        textViewLightId = findViewById(R.id.light_layout_txt_view_light_id);
        buttonTurnOn = findViewById(R.id.light_layout_btn_turn_on);
        buttonTurnOff = findViewById(R.id.light_layout_btn_turn_off);

        lightId = bundle.getString("light_id");
        textViewLightId.setText(lightId);

        String clientId = MqttClient.generateClientId();
        light_client = new MqttAndroidClient(this.getApplicationContext(), "tcp://"+MenuActivity.getBrokerHost()+":1883", clientId);
        connectToBroker();
        getLightStatus();

        buttonTurnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishCommand("ON");
            }
        });

        buttonTurnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishCommand("OFF");
            }
        });

    }

    private void connectToBroker() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(MenuActivity.getHomeId());
            options.setPassword(MenuActivity.getBrokerPassword().toCharArray());
            IMqttToken token = light_client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "connect onSuccess");

                    String topic = "CarbonIoT/" + MenuActivity.getHomeId() + "/data";
                    Log.d(TAG, "topic: " + topic);
                    int qos = 1;
                    try {
                        IMqttToken subToken = light_client.subscribe(topic, qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // The message was published
                                Log.d(TAG, "onSuccess: subscribed");
                                Toast.makeText(LightActivity.this, "Connected to broker", Toast.LENGTH_SHORT).show();
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

    private void getLightStatus() {
        light_client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(LightActivity.this, "Lost connection to broker", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = new String(message.getPayload());
                Log.d(TAG, "messageArrived: " + msg);
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    if (lightId.equals(jsonObject.getString("deviceId"))) {
                        status = jsonObject.getString("status");
                        Log.d(TAG, "messageArrived: " + status);
                        if(status.equals("ON")) {
                            textViewLightStatus.setText(status);
                            imageViewLight.setImageResource(R.drawable.light_on);
                        }
                        else if(status.equals("OFF")) {
                            textViewLightStatus.setText(status);
                            imageViewLight.setImageResource(R.drawable.light_off);
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

    private void publishCommand(String command) {
        String topic = "CarbonIoT/" + MenuActivity.getHomeId() + "/command";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("homeId", MenuActivity.getHomeId());
        jsonObject.addProperty("deviceId", lightId);
        jsonObject.addProperty("name", "status");
        jsonObject.addProperty("value", command);
        String payload = jsonObject.toString();

        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            message.setRetained(true);
            light_client.publish(topic, message);
        } catch (MqttException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Sent Command", Toast.LENGTH_SHORT).show();
    }

}