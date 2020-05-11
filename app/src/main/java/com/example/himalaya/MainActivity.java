package com.example.himalaya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.ContentProvider;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.example.himalaya.adapters.ContentAdapter;
import com.example.himalaya.adapters.IndicatorAdapter;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";
    private MagicIndicator mMagicIndicator;
    private List<Category> mCategories;
    private ViewPager mViewPager;
    private IndicatorAdapter mIndicatorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    private void initEvent() {
        mIndicatorAdapter.setIndicatorClickListener(new IndicatorAdapter.OnIndicatorTapClickListener() {
            @Override
            public void onTabClick(int index) {
                LogUtil.d(TAG,"点击了栏-->"+index);
                if(mViewPager != null){
                    mViewPager.setCurrentItem(index);
                }
            }
        });
    }

    private void initView(){
        mMagicIndicator = findViewById(R.id.magic_indicator);
        mMagicIndicator.setBackgroundColor(this.getResources().getColor(R.color.mainColor));

        mViewPager = findViewById(R.id.view_pager);

        //创建indicator的适配器
        CommonNavigator commonNavigator = new CommonNavigator(this);
        mIndicatorAdapter = new IndicatorAdapter(this);

        //自我调节，平衡子项位置
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(mIndicatorAdapter);
        mMagicIndicator.setNavigator(commonNavigator);

        //创建内容适配器
        FragmentManager fragmentManager = getSupportFragmentManager();
        ContentAdapter contentAdapter = new ContentAdapter(fragmentManager);

        mViewPager.setAdapter(contentAdapter);
        //指示器和viewPager绑定在一起,使得上方显示响应下方变化
        ViewPagerHelper.bind(mMagicIndicator,mViewPager);
    }


}
