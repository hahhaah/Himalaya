package com.example.himalaya.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseApplication;

public abstract class UILoader extends FrameLayout {

    private OnRetryClickListener mRetryListener = null;

    public enum UI_STATUS {
        LOADING,SUCCESS,NETWORK_ERROR,CONTENT_EMPTY,NONE
    }

    View mLoadingView,mSuccessView,mNetworkErrorView,mEmptyView;
    public UI_STATUS mCurStatus = UI_STATUS.NONE;

    public UILoader(Context context) {
        this(context,null);
    }

    public UILoader(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UILoader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //保证唯一入口
        init();
    }

    //初始化UI
    private void init() {
        switchUIByCurrentStatus();
    }

    public void updateStatus(UI_STATUS status){
        mCurStatus = status;
        //更新UI
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                switchUIByCurrentStatus();
            }
        });
    }

    private void switchUIByCurrentStatus() {
        /**
         * 加载中界面
         */
        if (mLoadingView == null) {
            mLoadingView = getLoadingView();
            addView(mLoadingView);
        }
        //根据状态设置是否可见
        mLoadingView.setVisibility(mCurStatus==UI_STATUS.LOADING?VISIBLE:GONE);

        //成功
        if (mSuccessView == null) {
            mSuccessView = getSuccessView(this);
            addView(mSuccessView);
        }
        mSuccessView.setVisibility(mCurStatus==UI_STATUS.SUCCESS?VISIBLE:GONE);

        //网络错误
        if (mNetworkErrorView == null) {
            mNetworkErrorView = getNotworkErrorView();
            addView(mNetworkErrorView);
        }
        mNetworkErrorView.setVisibility(mCurStatus==UI_STATUS.NETWORK_ERROR?VISIBLE:GONE);

        if(mEmptyView == null){
            mEmptyView = getEmptyView();
            addView(mEmptyView);
        }
        mEmptyView.setVisibility(mCurStatus==UI_STATUS.CONTENT_EMPTY?VISIBLE:GONE);
    }

    private View getEmptyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view,this,false);
    }

    private View getNotworkErrorView() {
        View networkView =  LayoutInflater.from(getContext()).inflate(R.layout.fragment_error_view,this,false);

        networkView.findViewById(R.id.network_error_area).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRetryListener != null) {
                    mRetryListener.onRetryClick();
                }
            }
        });
        return networkView;
    }

    protected abstract View getSuccessView(ViewGroup container);

    private View getLoadingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_loading_view,this,false);
    }

    public void setOnRetryListener(OnRetryClickListener listener){
        this.mRetryListener = listener;
    }

    public interface OnRetryClickListener{
        void onRetryClick();
    }
}
