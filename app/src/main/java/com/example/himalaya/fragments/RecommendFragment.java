package com.example.himalaya.fragments;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.adapters.RecommendListAdapter;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.presenters.RecommendPresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

//Ctrl+Alt+O清除无效引用
public class RecommendFragment extends BaseFragment implements IRecommendViewCallback {
    private static final String TAG = "RecommendFragment";
    private View mRootView;
    private RecyclerView mRecyclerView;
    private RecommendListAdapter mAdapter;
    private RecommendPresenter mRecommendPresenter;

    @Override
    public View onSubCreateView(LayoutInflater layoutInflater, ViewGroup container) {
        //View加载完成 并返回

        mRootView = layoutInflater.inflate(R.layout.fragment_recommend,container,false);

        //使用recyclerview步骤1
        mRecyclerView = mRootView.findViewById(R.id.recommend_list);

        //2.设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(mRootView.getContext(),6);
                outRect.bottom = UIUtil.dip2px(mRootView.getContext(),5);
                outRect.left = UIUtil.dip2px(mRootView.getContext(),5);
            }
        });

        //设置适配器

        mAdapter = new RecommendListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        getRecommendData();

        //获取到逻辑层的对象
        mRecommendPresenter = RecommendPresenter.getInstance();
        mRecommendPresenter.registerViewCallback(this);

        mRecommendPresenter.getRecommendList();

        return mRootView;
    }

    //获取到推荐内容
    private void getRecommendData() {
        //封装参数 获取到的推荐专辑数量

    }


    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //当获取到推荐内容时，该方法会被调用
        mAdapter.setAlbumList(result);
    }

    @Override
    public void onPullAndRefresh() {

    }

    @Override
    public void onLoadMore(List<Album> result) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mRecommendPresenter != null)mRecommendPresenter.unRegisterViewCallback(this);
    }
}
