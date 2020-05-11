package com.example.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.utils.LogUtil;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.InnerHolder> {

    private static final String TAG = "RecommendListAdapter";
    private static onRecommendItemClick mItemClickListener = null;
    private List<Album> mData = new ArrayList<>();

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {
        //设置数据
        holder.itemView.setTag(position);
        holder.setData(mData.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(position,mData.get(position));
                }
                LogUtil.d(TAG,"itemview clicked at position-->"+position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mData != null)return mData.size();
        return 0;
    }

    public void setAlbumList(List<Album> albumList) {
        if (mData != null) {
            mData.clear();
        }
        mData.addAll(albumList);
        notifyDataSetChanged();   //更新UI
    }

    public class InnerHolder extends RecyclerView.ViewHolder{

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //专辑封面
            ImageView albumCoverIv = (ImageView)itemView.findViewById(R.id.album_cover);
            //标题
            TextView titleTv = itemView.findViewById(R.id.album_title_tv);
            //描述
            TextView subTitleTv = itemView.findViewById(R.id.album_subTitle_tv);
            //播放量
            TextView playCountTv = itemView.findViewById(R.id.view_counts_tv);
            //内容数量
            TextView contentSizeTv = itemView.findViewById(R.id.content_size_tv);

            titleTv.setText(album.getAlbumTitle());
            subTitleTv.setText(album.getAlbumIntro());
            playCountTv.setText(album.getPlayCount()+"");
            contentSizeTv.setText(album.getIncludeTrackCount()+"");

            Picasso.with(itemView.getContext()).load(album.getCoverUrlLarge()).into(albumCoverIv);

        }
    }

    public interface onRecommendItemClick{
        void onItemClick(int position,Album album);
    }

    public void setOnItemClickListener(onRecommendItemClick listener){
        mItemClickListener = listener;
    }

}
