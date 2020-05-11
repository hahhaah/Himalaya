package com.example.himalaya.utils;

import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.fragments.HistoryFragment;
import com.example.himalaya.fragments.RecommendFragment;
import com.example.himalaya.fragments.SubscribeFragment;

import java.util.HashMap;
import java.util.Map;

public class FragmentCreator {

    private static Map<Integer, BaseFragment> sFragmentMap = new HashMap<>();

    public final static int INDEX_RECOMMEND = 0;
    public final static int INDEX_SUBSCRIBE = 1;
    public final static int INDEX_HISTORY = 2;

    public final static int PAGE_COUNT = 3;

    public static BaseFragment getFragment(int index){
        BaseFragment fragment = sFragmentMap.get(index);
        if (fragment != null) {
            return fragment;
        }

        switch (index){
            case INDEX_RECOMMEND:
                fragment = new RecommendFragment();break;
            case INDEX_SUBSCRIBE:
                fragment = new SubscribeFragment();break;
            case INDEX_HISTORY:
                fragment = new HistoryFragment();break;
        }
        sFragmentMap.put(index,fragment);
        return fragment;
    }
}
