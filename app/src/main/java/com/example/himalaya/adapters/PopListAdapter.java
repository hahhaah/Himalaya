package com.example.himalaya.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.views.PopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class PopListAdapter extends RecyclerView.Adapter<PopListAdapter.InnerHolder> {

    private List<Track> mTracks = new ArrayList<>();
    private int mPlayingIndex = 0;
    private PopWindow.PopListItemClickListener mItemClickListener;

    @NonNull
    @Override
    public PopListAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pop_list,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PopListAdapter.InnerHolder holder, final int position) {
        View view = holder.itemView;
        TextView titleView = view.findViewById(R.id.pop_item_title);

        Track track = mTracks.get(position);
        titleView.setText(track.getTrackTitle());
        //设置当前正在播放歌曲的标题颜色
        titleView.setTextColor(BaseApplication.getAppContext().
                getResources().getColor(position==mPlayingIndex?R.color.mainColor:R.color.listTextColor));

        ImageView playIcon = view.findViewById(R.id.playing_icon);
        //设置播放状态对应的图标
        playIcon.setVisibility(position==mPlayingIndex?View.VISIBLE:View.GONE);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTracks.size();
    }

    public void setData(List<Track> tracks) {
        mTracks.clear();
        mTracks.addAll(tracks);
        notifyDataSetChanged();
    }

    public void setCurrentPlayPos(int index) {
        mPlayingIndex = index;
        notifyDataSetChanged();
    }

    public void setOnItemClick(PopWindow.PopListItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public class InnerHolder extends RecyclerView.ViewHolder{

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
