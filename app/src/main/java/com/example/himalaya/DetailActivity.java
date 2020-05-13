package com.example.himalaya;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.adapters.DetailListAdapter;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.IDetailViewCallback;
import com.example.himalaya.presenters.DetailPresenter;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.utils.BlurImage;
import com.example.himalaya.views.UILoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class DetailActivity extends BaseActivity {

    //Ctrl + Alt + F 将局部变量抽取为成员变量
    private ImageView mLargeCover;
    private ImageView mSmallCover;
    private TextView mTitleView;
    private TextView mAuthorView;
    private DetailPresenter mDetailPresenter;
    private RecyclerView mRecyclerView;
    private int mCurPage = 1;
    private DetailListAdapter mAdapter;
    private FrameLayout mDetailListContainer;
    private UILoader mUiLoader;
    private long mCurId;

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
                //获取到专辑下声音列表后 进行recyclerview的设置

                if (tracks == null || tracks.size() == 0) {
                    if (mUiLoader != null) {
                        mUiLoader.updateStatus(UILoader.UI_STATUS.CONTENT_EMPTY);
                    }
                }

                mAdapter.setData(tracks);
                mUiLoader.updateStatus(UILoader.UI_STATUS.SUCCESS);
            }

            @Override
            public void onAlbumLoaded(Album album) {
                //获取到专辑详情
                mCurId = album.getId();
                if (mDetailPresenter != null) {
                    mDetailPresenter.getAlbumDetail((int)album.getId(),mCurPage);
                }
                //显示加载中Loading界面
                if (mUiLoader != null) {
                    mUiLoader.updateStatus(UILoader.UI_STATUS.LOADING);
                }

                if(mTitleView != null)mTitleView.setText(album.getAlbumTitle());
                if(mAuthorView != null)mAuthorView.setText(album.getAnnouncer().getNickname());


                if(mLargeCover != null){
                    Picasso.with(DetailActivity.this).load(album.getCoverUrlLarge()).into(mLargeCover, new Callback() {
                        @Override
                        public void onSuccess() {
                            Drawable drawable = mLargeCover.getDrawable();
                            if(drawable != null){
                                BlurImage.makeBlur(mLargeCover,DetailActivity.this);
                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });

                }
                if(mSmallCover != null){
                    Picasso.with(DetailActivity.this).load(album.getCoverUrlSmall()).into(mSmallCover);
                }
            }

            @Override
            public void onNetworkError(int code, String msg) {
                mUiLoader.updateStatus(UILoader.UI_STATUS.NETWORK_ERROR);
            }
        });


    }

    private void initView() {
        mDetailListContainer = findViewById(R.id.detail_list_container);

        if(mUiLoader == null){
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
            mDetailListContainer.removeAllViews();
            mDetailListContainer.addView(mUiLoader);
            mUiLoader.setOnRetryListener(new UILoader.OnRetryClickListener() {
                @Override
                public void onRetryClick() {
                    if (mDetailPresenter != null) {
                        mDetailPresenter.getAlbumDetail((int)mCurId,mCurPage);
                    }
                }
            });
        }

        mLargeCover = findViewById(R.id.cover_large_bg);
        mSmallCover = findViewById(R.id.cover_small);
        mTitleView = findViewById(R.id.detail_title);
        mAuthorView = findViewById(R.id.detail_author);
        //RecyclerView的相关操作

    }

    private View createSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list,container,false);
        mRecyclerView = detailListView.findViewById(R.id.album_tracks_container);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        //设置适配器
        mAdapter = new DetailListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        //设置item的上下间距
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),5);
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
            }
        });

        mAdapter.setOnItemClick(new DetailListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(List<Track> tracks, int pos) {
                PlayerPresenter playerPresenter = PlayerPresenter.getInstance();
                playerPresenter.setPlayList(tracks,pos);
                Intent intent  = new Intent(DetailActivity.this,PlayerActivity.class);
                //intent.putExtra("INDEX",pos);
                startActivity(intent);
            }

        });

        return detailListView;
    }
}
