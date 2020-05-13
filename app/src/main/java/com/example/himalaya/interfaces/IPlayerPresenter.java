package com.example.himalaya.interfaces;

import com.example.himalaya.base.IBasePresenter;


import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.*;

public interface IPlayerPresenter extends IBasePresenter<IPlayerCallback> {

    void play();  //播放

    void pause();  //暂停

    void playNext();  //下一首

    void playPrev();  //上一首

    void switchPlayMode(PlayMode mode);   //切换播放模式

    void getPlayList();

    void playByIndex(int index);     //根据节目在列表中的位置播放

    void seekTo(int progress);  //切换播放进度

    boolean isPlaying();        //判断当前是否正在播放
}
