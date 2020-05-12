package com.example.himalaya.presenters;

import com.example.himalaya.interfaces.IRecommendPresenter;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendPresenter implements IRecommendPresenter {

    private static final String TAG = "RecommendPresenter";
    public static RecommendPresenter sInstance  = null;
    private List<IRecommendViewCallback> mCallbacks = new ArrayList<>();
    private RecommendPresenter(){ }

    /**
     * 获取单例对象
     */
    public static RecommendPresenter getInstance(){
        if(sInstance == null){
            synchronized (RecommendPresenter.class){
                if(sInstance == null){
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void getRecommendList() {
        Map<String, String> map = new HashMap<>();
        updateLoading();
        map.put(DTransferConstants.LIKE_COUNT, Constants.COUNT_RECOMMEND +"");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    handleRecommendResult(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.e(TAG,i+ "---> 获取推荐失败");
                LogUtil.e(TAG,s+ "---> 获取推荐失败");

                handleError();
            }
        });
    }

    private void handleError() {
        if (mCallbacks != null) {
            for(IRecommendViewCallback callback : mCallbacks){
                callback.onNetworkError();
            }
        }
    }


    private void handleRecommendResult(List<Album> albumList) {
        //通知UI
        if (albumList != null){
            if(albumList.size() == 0){
                for(IRecommendViewCallback callback : mCallbacks){
                    callback.onContentEmpty();
                }
            }else{
                for(IRecommendViewCallback callback : mCallbacks){
                    callback.onRecommendListLoaded(albumList);
                }
            }
        }
    }

    private void updateLoading(){
        for(IRecommendViewCallback callback : mCallbacks){
            callback.onLoading();
        }
    }

    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        if(mCallbacks != null && ! mCallbacks.contains(callback)){
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }
}
