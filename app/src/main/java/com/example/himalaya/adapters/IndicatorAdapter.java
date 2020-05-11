package com.example.himalaya.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.example.himalaya.R;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;


public class IndicatorAdapter extends CommonNavigatorAdapter {
    String[] titles;
    private OnIndicatorTapClickListener mOnTabClickListener;

    public IndicatorAdapter(Context context){
        titles = context.getResources().getStringArray(R.array.indicator_name);
    }
    @Override
    public int getCount() {
        if(titles != null){
            return titles.length;
        }
        return 0;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
        titleView.setNormalColor(Color.GRAY);
        titleView.setSelectedColor(Color.BLACK);
        titleView.setText(titles[index]);
        titleView.setTextSize(18);
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //TODO:切换下方页面
                if (mOnTabClickListener != null) {
                    mOnTabClickListener.onTabClick(index);
                }
            }
        });
        return titleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
        linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        linePagerIndicator.setColors(Color.WHITE);
        return linePagerIndicator;
    }

    //暴露接口，依赖注入
    public void setIndicatorClickListener(OnIndicatorTapClickListener listener){
        mOnTabClickListener= listener;
    }

    public interface OnIndicatorTapClickListener{
        void onTabClick(int index);
    }
}
