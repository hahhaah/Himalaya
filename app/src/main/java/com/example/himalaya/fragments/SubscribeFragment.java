package com.example.himalaya.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseFragment;

public class SubscribeFragment extends BaseFragment {
    private static final String TAG = "SubscribeFragment";

    @Override
    public View onSubCreateView(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView = layoutInflater.inflate(R.layout.fragment_subscribe,container,false);
        return rootView;
    }
}
