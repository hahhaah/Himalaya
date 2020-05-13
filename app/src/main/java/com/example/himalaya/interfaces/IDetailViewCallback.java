package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IDetailViewCallback {

    //专辑列表详情加载
    void onDetailListLoaded(List<Track> tracks);

    void onAlbumLoaded(Album album);

    void onNetworkError(int code, String msg);
}
