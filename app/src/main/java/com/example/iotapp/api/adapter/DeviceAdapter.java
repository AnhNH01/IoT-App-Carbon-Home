package com.example.iotapp.api.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iotapp.R;
import com.example.iotapp.models.Device;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>  {
    private final List<Device> mDevices;
    private OnDeviceClickedListener mOnDeviceClickedListener;

    public DeviceAdapter(List<Device> mDevices, OnDeviceClickedListener onDeviceClickedListener) {
        this.mDevices = mDevices;
        this.mOnDeviceClickedListener = onDeviceClickedListener;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        return new DeviceViewHolder(view, mOnDeviceClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device device = mDevices.get(position);
        if(device == null)
            return;
        holder.textViewDeviceName.setText(device.getDeviceType().getName());
    }

    @Override
    public int getItemCount() {
        if(mDevices != null)
            return mDevices.size();
        return 0;
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewDeviceName;
        private OnDeviceClickedListener onDeviceClickedListener;

        public DeviceViewHolder(@NonNull View itemView, OnDeviceClickedListener onDeviceClickedListener) {
            super(itemView);
            textViewDeviceName = itemView.findViewById(R.id.txt_view_device_name);
            this.onDeviceClickedListener = onDeviceClickedListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onDeviceClickedListener.onDeviceClicked(getAbsoluteAdapterPosition());
        }
    }

    public interface OnDeviceClickedListener {
        void onDeviceClicked(int position);
    }

}
