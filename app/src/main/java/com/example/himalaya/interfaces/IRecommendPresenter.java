package com.example.himalaya.interfaces;

public interface IRecommendPresenter {
    /**
     * 获取推荐内容
     */
    void getRecommendList();

    /**
     * 注册UI的回调
     * @param callback
     */
    void registerViewCallback(IRecommendViewCallback callback);

    void unRegisterViewCallback(IRecommendViewCallback callback);

}
