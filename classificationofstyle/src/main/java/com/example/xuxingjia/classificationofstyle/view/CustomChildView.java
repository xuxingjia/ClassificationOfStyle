package com.example.xuxingjia.classificationofstyle.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.TranslateAnimation;

import static com.example.xuxingjia.classificationofstyle.utils.Constant.DURATION;
import static com.example.xuxingjia.classificationofstyle.utils.Constant.SIZE;


/**
 * 自定义子控件
 */

public class CustomChildView extends NestedScrollView {

    private Context context;
    private View mChildView;
    private float startY;

    private Rect rect = new Rect();
    private float downY;

    public CustomChildView(Context context) {
        this(context, null);
    }

    public CustomChildView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomChildView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    /**
     * 是否把滚动事件交给父scrollview
     * @param flag 是否拦截事件
     */
    private void setParentScrollAble(boolean flag) {
        ViewParent parent = getParent();
        parent.requestDisallowInterceptTouchEvent(flag);
    }

    /**
     * 初始化添加控件
     */
    public void initChildView(View view) {
        if (view != null) {
            addView(view);
            if (getChildCount() > 0) {
                mChildView = getChildAt(0);
            }
        } else {
            throw new NullPointerException("view is not null");
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mChildView != null) {
            disposeOnTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    /**
     * @param ev 点击事件
     */
    private void disposeOnTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                float moveY = ev.getY();
                int offsetY = (int) ((moveY - startY) * SIZE);
                if (isNeedMove(offsetY)) {
                    if (rect.isEmpty()) {
                        rect.set(mChildView.getLeft(), mChildView.getTop(), mChildView.getRight()
                                , mChildView.getBottom());
                        return;
                    }
                    mChildView.layout(mChildView.getLeft(), mChildView.getTop() + offsetY,
                            mChildView.getRight(), mChildView.getBottom() + offsetY);
                    setParentScrollAble(false);
                } else {
                    setParentScrollAble(true);
                }
                startY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                float upY = ev.getY();
                float offset = upY - downY;
                int mixOffset = ViewConfiguration.get(context).getScaledDoubleTapSlop();
                if (Math.abs(offset) > mixOffset) {
                    //判断是否是首尾
                    if (!rect.isEmpty()) {
                        returnAnimation();
                    }
                } else {
                    returnAnimation();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 复原动画
     */
    private void returnAnimation() {
        TranslateAnimation animation = new TranslateAnimation(0, 0, mChildView.getTop(),
                rect.top);
        animation.setDuration(DURATION);
        mChildView.startAnimation(animation);
        mChildView.layout(rect.left, rect.top, rect.right, rect.bottom);
    }

    /**
     * @param isMove 是由移动
     * @return 当前控件是否在顶部或者底部
     */
    private Boolean isNeedMove(float isMove) {
        if (mChildView != null) {
            int scrollY = getScrollY();
            int offset = mChildView.getMeasuredHeight() - getHeight();
            if (isMove < 0) {
                return offset <= scrollY;
            } else {
                return scrollY == 0;
            }
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                downY = startY;
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
