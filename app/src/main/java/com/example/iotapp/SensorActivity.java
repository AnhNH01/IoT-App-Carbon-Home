package com.example.iotapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iotapp.api.ApiService;
import com.example.iotapp.models.DeviceData;
import com.example.iotapp.models.DeviceDataResult;
import com.example.iotapp.models.RequestData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SensorActivity extends AppCompatActivity {
    private TextView textViewMaxValue, textViewMinValue;
    Button button;
    private LineChart lineChart;
    private List<DeviceData> deviceDatas;
    private String deviceId = "";
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;
    String date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = simpleDateFormat.format(calendar.getTime());

        textViewMaxValue = findViewById(R.id.sensor_layout_max_value);
        textViewMinValue = findViewById(R.id.sensor_layout_min_value);

        lineChart = findViewById(R.id.sensor_layout_temperature_line_chart);
        button = findViewById(R.id.sensor_layout_btn_refresh);
        button.setOnClickListener(view -> {
            setChartData();
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            deviceId = bundle.getString("deviceId");
        deviceDatas = new ArrayList<>();
//        Toast.makeText(this, deviceId, Toast.LENGTH_SHORT).show();
        callApiGetDeviceData();

        List<Entry> entries = new ArrayList<>();

        float max = -99;
        float min = 100;
        if (deviceDatas.size() > 0) {

            for (int i = 0; i < deviceDatas.size(); i++) {
                if (max < deviceDatas.get(i).getValue())
                    max = (float) deviceDatas.get(i).getValue();
                if (min > deviceDatas.get(i).getValue())
                    min = (float) deviceDatas.get(i).getValue();
                entries.add(new Entry(i + 1, (float) deviceDatas.get(i).getValue()));
            }

            LineDataSet dataSet = new LineDataSet(entries, deviceDatas.get(0).getName().toUpperCase(Locale.ROOT));
            dataSet.setColor(R.color.darkgreen);
            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);
            lineChart.invalidate();
            textViewMaxValue.setText(String.valueOf(max));
            textViewMinValue.setText(String.valueOf(min));
        }


    }

    private void setChartData() {

        callApiGetDeviceData();
        float max = -99;
        float min = 100;
        List<Entry> entries = new ArrayList<>();
        if (deviceDatas.size() > 0) {
            for (int i = 0; i < deviceDatas.size(); i++) {
                if (max < deviceDatas.get(i).getValue())
                    max = (float) deviceDatas.get(i).getValue();
                if (min > deviceDatas.get(i).getValue())
                    min = (float) deviceDatas.get(i).getValue();
                entries.add(new Entry(i + 1, (float) deviceDatas.get(i).getValue()));
            }

            LineDataSet dataSet = new LineDataSet(entries, deviceDatas.get(0).getName().toUpperCase(Locale.ROOT));
            dataSet.setColor(R.color.darkgreen);
            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);
            lineChart.invalidate();
            textViewMaxValue.setText(String.valueOf(max));
            textViewMinValue.setText(String.valueOf(min));

        }
    }


    private void callApiGetDeviceData() {
        String startTime = date.substring(0, date.indexOf(" ")) + " 00:00:00";
        String endTime = date;
        RequestData requestData = new RequestData(deviceId, startTime, endTime);
        Log.d("Sensor", "callApiGetDeviceData: " + requestData.toString());
        ApiService.apiService.getDeviceData("Bearer " + MenuActivity.getApiToken(), requestData).enqueue(new Callback<DeviceDataResult>() {
            @Override
            public void onResponse(Call<DeviceDataResult> call, Response<DeviceDataResult> response) {
                deviceDatas = response.body().getResult();
                if (deviceDatas.size() != 0)
                    Log.d("Sensor", deviceDatas.get(0).toString());
            }

            @Override
            public void onFailure(Call<DeviceDataResult> call, Throwable t) {
                Toast.makeText(SensorActivity.this, "Cant get data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}