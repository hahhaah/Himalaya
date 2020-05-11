package com.example.himalaya.presenters;

import com.example.himalaya.interfaces.IDetailPresenter;
import com.example.himalaya.interfaces.IDetailViewCallback;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

public class DetailPresenter implements IDetailPresenter {

    private static  DetailPresenter sInstance = null;
    private Album mAlbum ;
    private List<IDetailViewCallback> mCallbacks = new ArrayList<>();

    public static DetailPresenter getInstance(){
        if(sInstance == null){
            synchronized (RecommendPresenter.class){
                if(sInstance == null){
                    sInstance = new DetailPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void pullToRefresh() {

    }

    @Override
    public void LoadMore() {

    }

    @Override
    public void getAlbumDetail(int albumId, int page) {

    }

    @Override
    public void registerViewCallback(IDetailViewCallback callback) {
        if(! mCallbacks.contains(callback)){
            mCallbacks.add(callback);
            if(mAlbum != null)callback.onAlbumLoaded(mAlbum);
        }
    }

    @Override
    public void unRegisterViewCallback(IDetailViewCallback callback) {
        if(mCallbacks.contains(callback)){
            mCallbacks.remove(callback);
        }
    }


    public void setTargetAlbum(Album target){
        mAlbum = target;
    }
}
