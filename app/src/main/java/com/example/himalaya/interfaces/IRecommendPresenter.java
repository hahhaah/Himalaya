package com.example.himalaya.interfaces;

public interface IRecommendPresenter {
    /**
     * 获取推荐内容
     */
    void getRecommendList();

    /**
     * 下拉刷新更多内容
     */
    void pullAndRefresh();

    /**
     * 上拉加载更多
     */
    void loadMore();

    /**
     * 注册UI的回调
     * @param callback
     */
    void registerViewCallback(IRecommendViewCallback callback);

    void unRegisterViewCallback(IRecommendViewCallback callback);

}
