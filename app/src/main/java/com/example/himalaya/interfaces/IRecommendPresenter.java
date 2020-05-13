package com.example.himalaya.interfaces;

import com.example.himalaya.base.IBasePresenter;

public interface IRecommendPresenter extends IBasePresenter<IRecommendViewCallback> {
    /**
     * 获取推荐内容
     */
    void getRecommendList();

    /**
     * 注册UI的回调
     * @param callback
     */


}
