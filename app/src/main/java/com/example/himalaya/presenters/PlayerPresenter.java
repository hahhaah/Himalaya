package com.example.himalaya.presenters;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.interfaces.IPlayerPresenter;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {
    private static PlayerPresenter sInstance = null;
    private static final String TAG = "PlayerPresenter";
    private final XmPlayerManager sPlayerManager;

    private List<IPlayerCallback> mCallbacks = new ArrayList<>();
    private Track mTrack;

    private int mCurrentIndex = 0;
    private final SharedPreferences mPlayMode;

    private XmPlayListControl.PlayMode mCurMode = PLAY_MODEL_LIST;

    public static final int PLAY_MODE_LIST_INT =0;
    public static final int PLAY_MODE_LIST_LOOP_INT =1;
    public static final int PLAY_MODE_RANDOM_INT =2;
    public static final int PLAY_MODE_SINGLE_LOOP_INT =3;

//    private boolean mIsReversed = false;

    //sp key and name
    public static final String SP_NAME = "PlayMode";
    public static final String SP_KEY = "CurrentMode";

    private PlayerPresenter(){
        sPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //广告相关的接口
        sPlayerManager.addAdsStatusListener(this);
        //注册播放器状态相关的接口
        sPlayerManager.addPlayerStatusListener(this);

        //需要记录当前用户设定的播放模式 SharedPreferences
        mPlayMode = BaseApplication.getAppContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

    }


    public static PlayerPresenter getInstance(){
        if(sInstance == null){
            synchronized (RecommendPresenter.class){
                if(sInstance == null){
                    sInstance = new PlayerPresenter();
                }
            }
        }
        return sInstance;
    }

    private boolean isPlayListSet = false;

    public void setPlayList(List<Track> list, int index){
        isPlayListSet = true;
        if(sPlayerManager != null){
            sPlayerManager.setPlayList(list,index);
            mTrack = list.get(index);
            mCurrentIndex = index;
        }else{
            LogUtil.d(TAG,"didn't get the play manager");
        }

    }
    @Override
    public void play() {
        if(isPlayListSet)sPlayerManager.play();

    }

    @Override
    public void pause() {
        if (sPlayerManager != null) {
            sPlayerManager.pause();
        }
    }

    @Override
    public void playNext() {
        if (sPlayerManager != null) {
            sPlayerManager.playNext();
        }
    }

    @Override
    public void playPrev() {
        if (sPlayerManager != null) {
            sPlayerManager.playPre();
        }
    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        if (sPlayerManager != null) {
            mCurMode = mode;
            sPlayerManager.setPlayMode(mode);
            //通知UI更新界面
            for (IPlayerCallback callback : mCallbacks) {
                callback.onPlayModeChange(mode);
            }
            SharedPreferences.Editor edit = mPlayMode.edit();
            edit.putInt(SP_KEY,getIntValByMode(mode));
            edit.commit();
        }
    }

    private int getIntValByMode(XmPlayListControl.PlayMode playMode){
        switch (playMode){
            case PLAY_MODEL_LIST:return PLAY_MODE_LIST_INT;
            case PLAY_MODEL_LIST_LOOP:return PLAY_MODE_LIST_LOOP_INT;
            case PLAY_MODEL_RANDOM:return PLAY_MODE_RANDOM_INT;
            case PLAY_MODEL_SINGLE_LOOP:return PLAY_MODE_SINGLE_LOOP_INT;
        }
        return 0;
    }

    @Override
    public void getPlayList() {
        //CommonTrackList commonTrackList = sPlayerManager.getCommonTrackList();/
        if(sPlayerManager != null){
            List<Track> playList = sPlayerManager.getPlayList();
            for (IPlayerCallback callback : mCallbacks) {
                callback.onListLoaded(playList);
            }
        }
    }


    @Override
    public void playByIndex(int index) {
        if (sPlayerManager != null) {
            sPlayerManager.play(index);
        }
    }

    @Override
    public void seekTo(int progress) {
        sPlayerManager.seekTo(progress);
        for (IPlayerCallback callback : mCallbacks) {
            callback.onProgressChange(progress,mTrack.getDuration());
        }
    }

    @Override
    public boolean isPlaying() {
        LogUtil.d(TAG,sPlayerManager.isPlaying()+" the player status");
        return sPlayerManager.isPlaying();

    }

    //TODO:认真揣摩，重构复用的优美
    @Override
    public void reverseOrder() {
        List<Track> playList = sPlayerManager.getPlayList();
        Collections.reverse(playList);

        mCurrentIndex = playList.size()-mCurrentIndex-1;
        sPlayerManager.setPlayList(playList,mCurrentIndex);

        mTrack = (Track)sPlayerManager.getCurrSound();
        for (IPlayerCallback callback : mCallbacks) {
            callback.onListLoaded(playList);
            callback.onTrackUpdate(mTrack,mCurrentIndex);
        }
    }

    @Override
    public boolean hasPlayList() {
        return isPlayListSet;
    }

    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        iPlayerCallback.onTrackUpdate(mTrack,mCurrentIndex);

        //从sharedPreference中获取
        int val = mPlayMode.getInt(SP_KEY, PLAY_MODE_LIST_INT);
        mCurMode = getPlayModeByIntVal(val);

        iPlayerCallback.onPlayModeChange(mCurMode);
        if(!mCallbacks.contains(iPlayerCallback)){
            mCallbacks.add(iPlayerCallback);
        }
    }

    private XmPlayListControl.PlayMode getPlayModeByIntVal(int val) {

        switch (val){
            case PLAY_MODE_LIST_INT:return PLAY_MODEL_LIST;
            case PLAY_MODE_LIST_LOOP_INT:return PLAY_MODEL_LIST_LOOP;
            case PLAY_MODE_RANDOM_INT:return PLAY_MODEL_RANDOM;
            case PLAY_MODE_SINGLE_LOOP_INT:return PLAY_MODEL_SINGLE_LOOP;
        }
        return PLAY_MODEL_LIST;
    }

    @Override
    public void unRegisterViewCallback(IPlayerCallback iPlayerCallback) {
        if(mCallbacks.contains(iPlayerCallback)){
            mCallbacks.remove(iPlayerCallback);
        }
    }


    //------------广告相关的回调----------
    @Override
    public void onStartGetAdsInfo() {
        LogUtil.d(TAG,"onStartGetAdsInfo");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.d(TAG,"onGetAdsInfo");
    }

    @Override
    public void onAdsStartBuffering() {
        LogUtil.d(TAG,"onAdsStartBuffering");
    }

    @Override
    public void onAdsStopBuffering() {
        LogUtil.d(TAG,"onAdsStopBuffering");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtil.d(TAG,"onStartPlayAds");
    }

    @Override
    public void onCompletePlayAds() {
        LogUtil.d(TAG,"onCompletePlayAds");
    }

    @Override
    public void onError(int what, int extra) {
        LogUtil.e(TAG,"code:"+what +"msg:"+extra);
    }

    //================广告相关的回调=======end========


    //=============播放器相关的回调===============start
    @Override
    public void onPlayStart() {
        LogUtil.e(TAG,"content:onPlayStart");
        for (IPlayerCallback callback : mCallbacks) {
            callback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        for (IPlayerCallback callback : mCallbacks) {
            callback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        for (IPlayerCallback callback : mCallbacks) {
            callback.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {

    }

    @Override
    public void onSoundPrepared() {
        LogUtil.d(TAG,"onSoundPrepared---");
        sPlayerManager.setPlayMode(mCurMode);
        if (sPlayerManager.getPlayerStatus() == PlayerConstants.STATE_PREPARED) {
            sPlayerManager.play();
        }
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        LogUtil.d(TAG,"onSoundSwitch---");
        //出错原因在于跳转至playerActivity时尚未注册callback
        //此处仅用于之后的切换更新
        mCurrentIndex = sPlayerManager.getCurrentIndex();
        if( curModel instanceof Track){
            mTrack = (Track)curModel;
            for(IPlayerCallback callback : mCallbacks){
                LogUtil.d(TAG,mTrack.getTrackTitle());
                callback.onTrackUpdate(mTrack,mCurrentIndex);
            }
        }
    }

    @Override
    public void onBufferingStart() {

    }

    @Override
    public void onBufferingStop() {
//        LogUtil.d(TAG,"onBufferingStop---");
    }

    @Override
    public void onBufferProgress(int i) {

    }

    @Override
    public void onPlayProgress(int curPos, int duration) {
        for(IPlayerCallback callback : mCallbacks){
            callback.onProgressChange(curPos,duration);
        }
    }

    @Override
    public boolean onError(XmPlayerException e) {
        return false;
    }
}
