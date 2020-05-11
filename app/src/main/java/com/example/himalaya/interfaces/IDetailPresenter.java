package com.example.himalaya.interfaces;

public interface IDetailPresenter {

    /**
     * 下拉刷新更多内容
     */
    void pullToRefresh();

    /**
     * 上滑加载更多
     */
    void LoadMore();

    //获取专辑详情
    void getAlbumDetail(int albumId, int page);

    void registerViewCallback(IDetailViewCallback callback);

    void unRegisterViewCallback(IDetailViewCallback callback);
}
