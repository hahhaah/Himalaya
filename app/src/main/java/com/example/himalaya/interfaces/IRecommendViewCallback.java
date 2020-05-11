package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface IRecommendViewCallback {

    //获取推荐内容的结果
    void onRecommendListLoaded(List<Album> result);

    //网络错误
    void onNetworkError();

    //内容为空
    void onContentEmpty();

    //正在加载
    void onLoading();
}
