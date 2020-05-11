package com.example.himalaya;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.IDetailViewCallback;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.presenters.DetailPresenter;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public class DetailActivity extends BaseActivity {

    //Ctrl + Alt + F 将局部变量抽取为成员变量
    private ImageView mLargeCover;
    private ImageView mSmallCover;
    private TextView mTitleView;
    private TextView mAuthorView;
    private DetailPresenter mDetailPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        mDetailPresenter = DetailPresenter.getInstance();
        mDetailPresenter.registerViewCallback(new IDetailViewCallback() {
            @Override
            public void onDetailListLoaded(List<Track> tracks) {

            }

            @Override
            public void onAlbumLoaded(Album album) {
                if(mTitleView != null)mTitleView.setText(album.getAlbumTitle());
                if(mAuthorView != null)mAuthorView.setText(album.getAnnouncer().getNickname());
                if(mLargeCover != null){
                    Picasso.with(DetailActivity.this).load(album.getCoverUrlLarge()).into(mLargeCover);
                }
                if(mSmallCover != null){
                    Picasso.with(DetailActivity.this).load(album.getCoverUrlSmall()).into(mSmallCover);
                }
            }
        });
    }

    private void initView() {
        mLargeCover = findViewById(R.id.cover_large_bg);
        mSmallCover = findViewById(R.id.cover_small);
        mTitleView = findViewById(R.id.detail_title);
        mAuthorView = findViewById(R.id.detail_author);

    }
}
