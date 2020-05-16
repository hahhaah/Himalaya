package com.example.himalaya.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.adapters.PopListAdapter;
import com.example.himalaya.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public class PopWindow extends PopupWindow {

    private final View mPopView;
    private TextView mCloseListBtn;
    private RecyclerView mListView;
    private PopListAdapter mListAdapter;
    private TextView mPlayModeText;
    private ImageView mPlayModeIcon;
    private View mPlayModeContainer;
    private XmPlayListControl.PlayMode mCurPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
    private PopViewActionListener mActionListener;
    private ImageView mOrderIcon;
    private TextView mOrderText;
    private View mOrderContainer;

    public PopWindow( ) {
        //设置宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        setOutsideTouchable(true);

        setTouchable(true);
        setFocusable(true);

        //加载View
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
        //设置内容
        setContentView(mPopView);

        //设置弹入和弹出动画
        setAnimationStyle(R.style.pop_animation);

        initView();
        
        initEvent();
    }


    private void initView(){
        mCloseListBtn = mPopView.findViewById(R.id.close_pop_list);
        mListView = mPopView.findViewById(R.id.pop_play_list);

        LinearLayoutManager manager = new LinearLayoutManager(BaseApplication.getAppContext());
        mListView.setLayoutManager(manager);

        mListAdapter = new PopListAdapter();
        mListView.setAdapter(mListAdapter);

        //播放模式
        mPlayModeText = mPopView.findViewById(R.id.poplist_play_mode);
        mPlayModeIcon = mPopView.findViewById(R.id.poplist_play_mode_icon);

        mPlayModeText.setText(getTextByPlayMode(mCurPlayMode));
        mPlayModeContainer = mPopView.findViewById(R.id.poplist_play_mode_container);

        mOrderContainer = mPopView.findViewById(R.id.play_order_container);
        mOrderIcon = mPopView.findViewById(R.id.poplist_direction_icon);
        mOrderText = mPopView.findViewById(R.id.poplist_direction);

    }

    private void initEvent() {
        //点击关闭按钮 窗口消失
        mCloseListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopWindow.this.dismiss();
            }
        });

        mPlayModeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放模式
                if (mActionListener != null) {
                    mActionListener.onPlayModeClick();
                }
            }
        });

        mOrderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:切换播放列表的顺序
                mActionListener.onPlayOrderClick();
            }
        });
    }

    //更新 播放列表顺序 UI显示
    public void updateOrderIcon(boolean isOrder){
        if(isOrder){
            mOrderIcon.setImageResource(R.mipmap.down);
            mOrderText.setText("正序");
        }else{
            mOrderIcon.setImageResource(R.mipmap.up);
            mOrderText.setText("倒序");
        }
    }
    //给适配器设置数据
    public void setListData(List<Track> data){
        if (mListAdapter != null) {
            mListAdapter.setData(data);
        }
    }

    public void setCurrentPlayIndex(int index){
        if (mListAdapter != null) {
            //设置播放列表当前播放的位置
            mListAdapter.setCurrentPlayPos(index);
            mListView.scrollToPosition(index);
        }
    }

    private String getTextByPlayMode(XmPlayListControl.PlayMode mode){
        String res = "顺序播放";
        switch (mode){
            case PLAY_MODEL_LIST:
                return "顺序播放";
            case PLAY_MODEL_RANDOM:
                return "随机播放";
            case PLAY_MODEL_SINGLE_LOOP:
                return "单曲循环";
            case PLAY_MODEL_LIST_LOOP:
                return "列表循环";
        }
        return res;
    }

    public void updatePlayMode(XmPlayListControl.PlayMode curMode) {
        mPlayModeText.setText(getTextByPlayMode(curMode));
        updatePlayModeBtnImg(curMode);

    }

    private void updatePlayModeBtnImg(XmPlayListControl.PlayMode mode) {

        //根据当前状态更新播放模式图标
        switch (mode){
            case PLAY_MODEL_LIST:
                mPlayModeIcon.setImageResource(R.mipmap.mode);
                break;
            case PLAY_MODEL_LIST_LOOP:
                mPlayModeIcon.setImageResource(R.mipmap.list_loop);
                break;
            case PLAY_MODEL_RANDOM:
                mPlayModeIcon.setImageResource(R.mipmap.random);
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                mPlayModeIcon.setImageResource(R.mipmap.single_loop);
                break;
        }
    }

    public interface PopListItemClickListener{
        void onItemClick(int position);
    }

    public void setPopListItemClickListener(PopListItemClickListener listener){
        mListAdapter.setOnItemClick(listener);
    }

    public interface PopViewActionListener {
        void onPlayModeClick();         //播放模式相关被点击
        void onPlayOrderClick();        //播放顺序相关被点击
    }

    public void setPopViewActionListener(PopViewActionListener listener){
        mActionListener = listener;
    }


}
