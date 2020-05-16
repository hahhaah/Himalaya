package com.example.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.InnerHolder> {

    private List<Track> mDetailTracks = new ArrayList<>();

    private ItemClickListener mItemClickListener = null;
    //格式化时间
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mDurationFormat = new SimpleDateFormat("mm:ss");


    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {
        View itemView = holder.itemView;
        TextView indexTv = itemView.findViewById(R.id.track_index);
        TextView titleTv = itemView.findViewById(R.id.track_title);
        TextView playCountTv = itemView.findViewById(R.id.track_playcount);
        TextView durationTv = itemView.findViewById(R.id.track_duration);
        TextView updateTimeTv = itemView.findViewById(R.id.detail_update_time);

        Track track = mDetailTracks.get(position);
        indexTv.setText((position+1)+"");
        titleTv.setText(track.getTrackTitle());
        playCountTv.setText(track.getPlayCount()+"");

        String duration = mDurationFormat.format(track.getDuration()*1000);
        durationTv.setText(duration);

        String updateTimeText = mDateFormat.format(track.getUpdatedAt());
        updateTimeTv.setText(updateTimeText);

        //设置item的点击响应事件
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(mDetailTracks,position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDetailTracks.size();
    }

    public void setData(List<Track> tracks) {
        //清除原有数据
        mDetailTracks.clear();
        //添加新的数据
        mDetailTracks.addAll(tracks);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface ItemClickListener{
        void onItemClick(List<Track> tracks,int pos);
    }

    public void setOnItemClick(ItemClickListener listener){
        mItemClickListener = listener;
    }
}
