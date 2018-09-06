package com.example.xuxingjia.classificationofstyle.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import static com.example.xuxingjia.classificationofstyle.utils.Constant.DURATION;


/**
 * 自定义阻尼翻页效果
 */

public class CustomDampView extends RelativeLayout implements NestedScrollingParent {

    private Context context;
    private float downY;

    private int defaultIndex = 0;
    private List<CustomChildView> customChildViews;

    public CustomDampView(Context context) {
        this(context, null);
    }

    public CustomDampView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomDampView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * @param index 当前的子控件索引
     */
    public void selectReightChild(int index) {
        if (defaultIndex < index) {
            defaultIndex = index;
            //手指向上
            swipeup(index);
        } else if (defaultIndex > index) {
            //手指向下
            defaultIndex = index;
            swipedown(index);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                float upY = ev.getY();
                float offsetY = upY - downY;
                int mixOffset = ViewConfiguration.get(context).getScaledDoubleTapSlop();
                if (Math.abs(offsetY) > mixOffset) {
                    if (offsetY < 0) {
                        //手指向上
                        swipeup();
                    } else {
                        //手指向下
                        swipedown();
                    }
                }
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 向下滑屏幕
     */
    private void swipedown() {
        if (defaultIndex <= 0) {
            return;
        }
        defaultIndex--;
        CustomChildView selectChild = (CustomChildView) getChildAt(defaultIndex);
        smoothScrollTo(selectChild.getMeasuredHeight() * defaultIndex, DURATION);
    }

    /**
     * 向下滑屏幕
     */
    private void swipedown(int index) {
        if (index < 0) {
            return;
        }
        CustomChildView selectChild = (CustomChildView) getChildAt(index);
        smoothScrollTo(selectChild.getMeasuredHeight() * index, DURATION);
    }

    /**
     * 开始滑动
     *
     * @param y        滑动的距离
     * @param duration 需要的时间
     */
    private void smoothScrollTo(int y, int duration) {
        smoothScrollToAnimation(y, duration);
    }

    /**
     * 属性动画用来滑动子控件
     *
     * @param y        滑动距离
     * @param duration 需要的时间
     */
    private void smoothScrollToAnimation(int y, int duration) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(getScrollY(), y);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int sy = (int) animation.getAnimatedValue();
                CustomDampView.this.scrollTo(0, sy);
            }
        });
        valueAnimator.setDuration(duration);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (onReightSelectListener != null) {
                    onReightSelectListener.onReightSelectListener(defaultIndex);
                }
            }
        });
        valueAnimator.start();
    }



    /**
     * 向上滑屏幕
     */
    private void swipeup() {
        CustomChildView selectChlid = (CustomChildView) getChildAt(defaultIndex);
        if (defaultIndex >= customChildViews.size() - 1) {
            return;
        }
        if (defaultIndex < customChildViews.size() - 1) {
            defaultIndex++;
            smoothScrollTo(selectChlid.getMeasuredHeight() * defaultIndex, DURATION);
        }
    }

    /**
     * 向上滑屏幕
     */
    private void swipeup(int index) {
        CustomChildView selectChlid = (CustomChildView) getChildAt(index);
        if (index >= customChildViews.size()) {
            return;
        }
        smoothScrollTo(selectChlid.getMeasuredHeight() * index, DURATION);
    }

    /**
     * @param customChildViews 传递过来的子控件集合
     */
    public void setChildNumber(List<CustomChildView> customChildViews) {
        this.customChildViews = customChildViews;
        for (int i = 0; i < customChildViews.size(); i++) {
            CustomChildView customChildView = customChildViews.get(i);
            addView(customChildView, i);
            //设置子类控件的基本属性
            ViewGroup.LayoutParams layoutParams = customChildView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            customChildView.setLayoutParams(layoutParams);
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int childTop = t;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            childTop += lp.topMargin;
            child.layout(child.getLeft(), childTop, r, childTop + child.getMeasuredHeight());
            childTop += child.getMeasuredHeight() + lp.bottomMargin;
        }
    }

    private OnReightSelectListener onReightSelectListener = null;

    public interface OnReightSelectListener {
        void onReightSelectListener(int index);
    }

    public void setOnReightSelectListener(OnReightSelectListener onReightSelectListener) {
        this.onReightSelectListener = onReightSelectListener;
    }
}
