package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerCallback {

    void onPlayStart();     //开始播放

    void onPlayPause();     //播放暂停

    void onPlayStop();      //播放停止

    void onPlayNext(Track track);      ///播放下一首

    void onPlayPrev(Track track);

    void onPlayError();     //播放错误

    void onListLoaded(List<Track> tracks);      //显示列表

    //播放模式改变
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);

    void onProgressChange(int cur,int total);

    void onAdLoading();     //广告加载

    void onAdFinished();

    //更新当前节目的标题
    void onTrackUpdate(Track track,int pos);
}
