package com.example.iotapp.api.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iotapp.R;
import com.example.iotapp.models.Room;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder>{

    private final List<Room> mRoomList;
    private OnRoomClickedListener mOnRoomClickedListener;

    public RoomAdapter(List<Room> mRoomList, OnRoomClickedListener onRoomClickedListener) {
        this.mRoomList = mRoomList;
        this.mOnRoomClickedListener = onRoomClickedListener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view, mOnRoomClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = mRoomList.get(position);
        if(room == null)
            return;

        holder.textViewRoomName.setText(room.getName());
        holder.textViewRoomId.setText(room.getId());
    }

    @Override
    public int getItemCount() {
        if(mRoomList != null)
            return mRoomList.size();
        return 0;
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textViewRoomName;
        private final TextView textViewRoomId;
        OnRoomClickedListener onRoomClickedListener;

        public RoomViewHolder(@NonNull View itemView, OnRoomClickedListener onRoomClickedListener) {
            super(itemView);
            textViewRoomName = itemView.findViewById(R.id.txt_view_room_name);
            textViewRoomId = itemView.findViewById(R.id.txt_view_room_id);

            this.onRoomClickedListener = onRoomClickedListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onRoomClickedListener.onRoomClicked(getAbsoluteAdapterPosition());
        }

    }
    public interface OnRoomClickedListener {
        void onRoomClicked(int position);
    }
}
