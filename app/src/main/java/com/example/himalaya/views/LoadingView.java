package com.example.himalaya.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.himalaya.R;


public class LoadingView extends AppCompatImageView {
    //旋转角度
    private int degree = 0;
    private boolean mKeepRotate;

    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //设置图标
        setImageResource(R.mipmap.loading);

    }

    @Override
    protected void onAttachedToWindow() {   //绑定到window的时候，改变角度
        super.onAttachedToWindow();
        mKeepRotate = true;
        post(new Runnable() {
            @Override
            public void run() {
                degree = (degree-30 ) % 360 ;
                invalidate();
                if(mKeepRotate){
                    postDelayed(this, 150);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        mKeepRotate = false;
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /**
         * 第1个参数为旋转角度
         * 第2个参数为旋转的x坐标
         * 第3个参数为旋转的y坐标
         */
        canvas.rotate(degree,getWidth() /2,getHeight() /2);
        super.onDraw(canvas);
    }
}
