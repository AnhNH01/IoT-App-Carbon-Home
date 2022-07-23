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

public class LightAdapter extends RecyclerView.Adapter<LightAdapter.LightViewHolder> {
    private List<Device> mLights;
    private OnLightClickedListener mOnLightClickedListener;

    public LightAdapter(List<Device> mLights, OnLightClickedListener mOnLightClickedListener) {
        this.mLights = mLights;
        this.mOnLightClickedListener = mOnLightClickedListener;
    }

    @NonNull
    @Override
    public LightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_light, parent, false);
        return new LightViewHolder(view, mOnLightClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LightViewHolder holder, int position) {
        Device light = mLights.get(position);
        if(light == null)
            return;
        holder.textViewLightName.setText(light.getDeviceType().getName());

    }

    @Override
    public int getItemCount() {
        if(mLights != null)
            return mLights.size();
        return 0;
    }


    public class LightViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewLightName;
        private OnLightClickedListener onLightClickedListener;

        public LightViewHolder(@NonNull View itemView, OnLightClickedListener onLightClickedListener) {
            super(itemView);
            textViewLightName = itemView.findViewById(R.id.txt_view_light_name);
            this.onLightClickedListener = onLightClickedListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onLightClickedListener.onLightClicked(getAbsoluteAdapterPosition());

        }
    }


    public interface OnLightClickedListener {
        void onLightClicked(int position);
    }
}
