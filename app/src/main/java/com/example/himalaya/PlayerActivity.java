package com.example.himalaya;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.himalaya.adapters.PlayerPagerAdapter;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.PopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerActivity extends BaseActivity implements IPlayerCallback {

    private PlayerPresenter mPlayerPresenter;
    private ImageView mControlBtn;

    private static final String TAG = "PlayerActivity";

    private SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat= new SimpleDateFormat("hh:mm:ss");
    private TextView mDurationView;
    private SeekBar mSeekBar;
    private TextView mCurPosView;
    private int mCurProgress = 0;
    private boolean mIsUserTouch = false;
    private ImageView mPlayNext;
    private ImageView mPlayPrev;
    private TextView mTitleView;
    private String mCurTitle;
    private ViewPager mPlayerPager;
    private PlayerPagerAdapter mPagerAdapter;
    private int mCurPos;
    private boolean mIsUserSlidePage = false;
    private ImageView mPlayModeBtn;
    private static Map<XmPlayListControl.PlayMode,XmPlayListControl.PlayMode> sPlayModeRules = new HashMap<>();

    public final int ANIMATION_DURATION = 500;

    private XmPlayListControl.PlayMode mCurMode = PLAY_MODEL_LIST;
    /**
     * 默认为列表播放   PLAY_MODEL_LIST,
     * ->列表循环      PLAY_MODEL_LIST_LOOP,
     * ->随机循环      PLAY_MODEL_RANDOM;
     * ->单曲循环      PLAY_MODEL_SINGLE_LOOP,
     */
    static {
        sPlayModeRules.put(PLAY_MODEL_LIST,PLAY_MODEL_LIST_LOOP);
        sPlayModeRules.put(PLAY_MODEL_LIST_LOOP,PLAY_MODEL_RANDOM);
        sPlayModeRules.put(PLAY_MODEL_RANDOM,PLAY_MODEL_SINGLE_LOOP);
        sPlayModeRules.put(PLAY_MODEL_SINGLE_LOOP,PLAY_MODEL_LIST);
    }

    private ImageView mPlayListBtn;
    private PopWindow mPopWindow;
    private ValueAnimator mEnterAnimator;
    private ValueAnimator mExitAnimator;

    private boolean isOrder = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player);

        initView();

        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
        //顺序需要注意，部分组件还未初始化
        mPlayerPresenter.getPlayList();
        initBgAnimation();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPlayerPresenter != null){
            mPlayerPresenter.unRegisterViewCallback(this);
            mPlayerPresenter = null;
        }
    }



    private void initView() {
        mControlBtn = findViewById(R.id.play_or_pause);
        mDurationView = findViewById(R.id.track_end);
        mCurPosView = findViewById(R.id.current_position);
        mSeekBar = findViewById(R.id.track_seekbar);
        mPlayNext = findViewById(R.id.play_next);
        mPlayPrev = findViewById(R.id.play_previous);
        mTitleView = findViewById(R.id.player_title);           //存在id冲突

        mPlayModeBtn = findViewById(R.id.play_mode);

        if (!TextUtils.isEmpty(mCurTitle)) {
            LogUtil.d(TAG,mCurTitle);
            mTitleView.setText(mCurTitle);
        }

        mPlayerPager = findViewById(R.id.player_pager);
        //设置适配器
        mPagerAdapter = new PlayerPagerAdapter();
        mPlayerPager.setAdapter(mPagerAdapter);

        mPlayerPager.setCurrentItem(mCurPos,true);

        mPlayListBtn = findViewById(R.id.play_list);
        mPopWindow = new PopWindow();


    }

    //给控件设置相关的响应事件
    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        if(mPlayerPresenter!=null){
            mControlBtn.setImageResource(mPlayerPresenter.isPlaying()?R.mipmap.pause:R.mipmap.play_now);
        }

        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据当前播放状态来响应

                if(mPlayerPresenter.isPlaying()){
                    mPlayerPresenter.pause();
                }else{
                    mPlayerPresenter.play();
                }
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mCurProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //手离开拖动条的时候更新
                mIsUserTouch = false;
                onProgressChange(mCurProgress,mSeekBar.getMax());
                mPlayerPresenter.seekTo(mCurProgress);
            }
        });

        mPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerPresenter.playNext();
            }
        });

        mPlayPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerPresenter.playPrev();
            }
        });

        mPlayerPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //页面选中的时候去播放相应内容
                if (mPlayerPresenter != null &&mIsUserSlidePage) {
                    mPlayerPresenter.playByIndex(position);
                }
                mIsUserSlidePage=false;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mPlayerPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if(action ==MotionEvent.ACTION_DOWN){
                    mIsUserSlidePage = true;
                }
                return false;
            }
        });

        mPlayModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //处理播放模式的切换
                //根据当前mode获取下一mode
                switchPlayMode();
            }
        });

        mPlayListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出播放列表
                mPopWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
                //给背景加渐变
                mEnterAnimator.start();
            }
        });

        mPopWindow.setPopListItemClickListener(new PopWindow.PopListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mPlayerPresenter.playByIndex(position);
            }
        });

        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mExitAnimator.start();
            }
        });

        mPopWindow.setPopViewActionListener(new PopWindow.PopViewActionListener() {
            @Override
            public void onPlayModeClick() {
                switchPlayMode();
            }

            @Override
            public void onPlayOrderClick() {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.reverseOrder();

                }
                isOrder = !isOrder;
                mPopWindow.updateOrderIcon(isOrder);
            }
        });
    }

    private void switchPlayMode() {
        XmPlayListControl.PlayMode playMode = sPlayModeRules.get(mCurMode);
        LogUtil.d(TAG,"下一模式"+playMode);
        if (mPlayerPresenter != null) {
            mPlayerPresenter.switchPlayMode(playMode);
        }
    }

    private void initBgAnimation(){
        mEnterAnimator = ValueAnimator.ofFloat(1.0f,0.7f);
        mEnterAnimator.setDuration(ANIMATION_DURATION);
        mEnterAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                updateBgAlpha((float)animation.getAnimatedValue());
            }
        });

        mExitAnimator = ValueAnimator.ofFloat(0.7f,1.0f);
        mExitAnimator.setDuration(ANIMATION_DURATION);
        mExitAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                updateBgAlpha((float)animation.getAnimatedValue());
            }
        });
    }

    public void updateBgAlpha(float alpha){
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.alpha = alpha;
        window.setAttributes(params);

    }

    private void updatePlayModeBtnImg() {
        LogUtil.d(TAG,"当前模式为"+mCurMode);

        //根据当前状态更新播放模式图标
        switch (mCurMode){
            case PLAY_MODEL_LIST:
                mPlayModeBtn.setImageResource(R.mipmap.mode);
                break;
            case PLAY_MODEL_LIST_LOOP:
                mPlayModeBtn.setImageResource(R.mipmap.list_loop);
                break;
            case PLAY_MODEL_RANDOM:
                mPlayModeBtn.setImageResource(R.mipmap.random);
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                mPlayModeBtn.setImageResource(R.mipmap.single_loop);
                break;
        }
    }

    //View的callback
    @Override
    public void onPlayStart() {
        //修改按钮状态
        if(mControlBtn != null)
            mControlBtn.setImageResource(R.mipmap.pause);
    }

    @Override
    public void onPlayPause() {
        if(mControlBtn != null)mControlBtn.setImageResource(R.mipmap.play_now);
    }

    @Override
    public void onPlayStop() {
        if(mControlBtn != null)mControlBtn.setImageResource(R.mipmap.pause);
    }

    @Override
    public void onPlayNext(Track track) {

    }

    @Override
    public void onPlayPrev(Track track) {

    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void onListLoaded(List<Track> tracks) {
        if(mPagerAdapter != null){
            mPagerAdapter.setData(tracks);
        }
        //给到弹出的播放列表
        if(mPopWindow != null)mPopWindow.setListData(tracks);
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {
        mCurMode = playMode;
        //更新Popwindow里面的播放模式
        mPopWindow.updatePlayMode(mCurMode);
        updatePlayModeBtnImg();
    }

    //仅进度条显示更新，播放器进度更新需要调用seekTo
    @Override
    public void onProgressChange(int cur, int total) {
        mSeekBar.setMax(total);
        String totalDuration;
        String currentPos;
        //更新播放进度
        if( total > 1000* 60 * 60){     //时长超过1小时
            totalDuration = mHourFormat.format(total);
            currentPos = mHourFormat.format(cur);
        }else{
            totalDuration = mMinFormat.format(total);
            currentPos = mMinFormat.format(cur);
        }
        if(mDurationView!=null)mDurationView.setText(totalDuration);
        if(mCurPosView!=null)mCurPosView.setText(currentPos);
        //更新进度条,计算当前
        if(mSeekBar!=null && ! mIsUserTouch){
            mSeekBar.setProgress(cur);
        }
    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track,int pos) {
        mCurTitle = track.getTrackTitle();
        if(mTitleView != null){
            mTitleView.setText(track.getTrackTitle());
        }
        //当节目改变时，获取到在列表中所处位置

        mCurPos = pos;
        //修改播放界面的图片显示 推迟显示
        if (mPlayerPager != null) {
            mPlayerPager.setCurrentItem(pos,true);
        }

        if (mPopWindow != null) {
            mPopWindow.setCurrentPlayIndex(pos);
        }
    }
}
