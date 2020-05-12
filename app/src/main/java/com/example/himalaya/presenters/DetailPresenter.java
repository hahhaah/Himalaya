package com.example.himalaya.presenters;

import com.example.himalaya.interfaces.IDetailPresenter;
import com.example.himalaya.interfaces.IDetailViewCallback;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailPresenter implements IDetailPresenter {

    private static  DetailPresenter sInstance = null;
    private static final String TAG = "DetailPresenter";
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
        //根据id和页码获取列表
        Map<String,String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID,albumId+"");
        map.put(DTransferConstants.SORT,"asc");
        map.put(DTransferConstants.PAGE,page+"");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT+"");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if(trackList != null){
                    List<Track> tracks = trackList.getTracks();
                    LogUtil.d(TAG,"tracks size-->"+tracks.size());
                    handleTracksLoaded(trackList);
                }
            }

            @Override
            public void onError(int i, String msg) {
                LogUtil.e(TAG,"erorCode-->"+i);
                LogUtil.e(TAG,"errorMsg-->"+msg);
            }
        });

    }

    private void handleTracksLoaded(TrackList trackList) {
        for (IDetailViewCallback callback : mCallbacks) {
            callback.onDetailListLoaded(trackList.getTracks());
        }

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
