package com.example.xuxingjia.classificationofstyle.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.xuxingjia.classificationofstyle.R;
import com.example.xuxingjia.classificationofstyle.utils.PxUtil;

import java.util.ArrayList;
import java.util.List;

import static com.example.xuxingjia.classificationofstyle.utils.Constant.DURATION;


/**
 * 自定义竖直的tablayout
 */

public class VerticalTabLayout extends ScrollView implements View.OnClickListener {
    private Context context;
    private View view;
    private LinearLayout mChildTextParentView;
    private int selctColor;
    private int unselctColor;

    private List<String> leftStrings = null;
    private LinearLayout mIndicatorView;
    private int childViewHeight;

    private int lastPosition;
    private Scroller mScroller;


    public VerticalTabLayout(Context context) {
        this(context, null);
    }

    public VerticalTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        mScroller = new Scroller(context);
        selctColor = getResources().getColor(R.color.left_selct_color);
        unselctColor = getResources().getColor(R.color.left_unselct_color);
        childViewHeight = PxUtil.dip2px(context, 67);
    }

    /**
     * @param dy       y移动距离
     * @param duration 移动时间
     */
    protected void smoothScrollBySlow(int dy, int duration) {
        mScroller.startScroll(getScrollX(), getScrollY(), 0, dy, duration);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            //这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * @param data 获取传递过来数据左侧
     */
    public void setData(List<String> data) {
        if (leftStrings == null) {
            leftStrings = new ArrayList<>();
        } else {
            leftStrings.clear();
        }
        leftStrings.addAll(data);
        addLeftChildView(leftStrings);
    }

    /**
     * 添加左侧的子控件
     *
     * @param leftStrings 左侧文字资源
     */
    private void addLeftChildView(List<String> leftStrings) {
        mChildTextParentView.removeAllViews();
        for (int i = 0; i < leftStrings.size(); i++) {
            TextView textView = new TextView(context);

            if (mChildTextParentView != null) {
                mChildTextParentView.addView(textView, i);
            }

            textView.setTag(i);
            textView.setOnClickListener(this);
            //设置textView属性
            ViewGroup.LayoutParams layoutParams = textView.getLayoutParams();
            layoutParams.height = childViewHeight;
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            textView.setLayoutParams(layoutParams);
            textView.setGravity(Gravity.CENTER);
            textView.setText(leftStrings.get(i));

            //设置默认选中效果
            if (i == 0) {
                textView.setTextColor(selctColor);
            } else {
                textView.setTextColor(unselctColor);
            }
        }
    }

    /**
     * @param position 选中左侧的索引
     *                 用来设置左侧选中
     */
    public void setLeftSelect(int position) {
        leftSelectResult(position);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.vertical_tablayout_child_view,
                    this,
                    false);
        }
        if (getChildCount() > 0) {
            this.removeAllViews();
        }
        addView(view);

        //获取子控件
        LinearLayout mLlChildParent = (LinearLayout) getChildAt(0);
        mIndicatorView = (LinearLayout) mLlChildParent.getChildAt(0);
        mChildTextParentView = (LinearLayout) mLlChildParent.getChildAt(1);

    }

    @Override
    public void onClick(View v) {
        Integer position = (Integer) v.getTag();
        leftSelectResult(position);
    }

    private LeftSelectListener leftSelectListener = null;

    public interface LeftSelectListener {
        void leftSelectListener(int postion);
    }

    public void setLeftSelectListener(LeftSelectListener leftSelectListener) {
        this.leftSelectListener = leftSelectListener;
    }

    /**
     * @param position 选中的索引值
     *                 导航指针滑动
     */
    private void indicatorSlide(Integer position) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mIndicatorView, "translationY",
                lastPosition * childViewHeight, position * childViewHeight);
        animator.setDuration(DURATION);
        animator.start();
        lastPosition = position;
    }

    /**
     * @param position 选中的索引
     *                 左侧点击选中效果
     */
    private void leftSelectResult(Integer position) {

        View selectChild = mChildTextParentView.getChildAt(position);
        //设置选中效果
        if (mChildTextParentView == null) {
            return;
        }
        if (leftSelectListener != null) {
            leftSelectListener.leftSelectListener(position);
        }

        int[] outLocation = new int[2];
        selectChild.getLocationInWindow(outLocation);


        Rect rect = new Rect();
        boolean globalVisibleRect = selectChild.getGlobalVisibleRect(rect);
        if (globalVisibleRect) {
            //判断滑动的方向
            if (position > lastPosition) {
                //向上滑动
                //判断是否滑动到了底部
                if (getChildAt(0).getHeight() - getHeight() != getScrollY()) {
                    int bottomLine = getScrollY() + getBottom() - (childViewHeight +
                            (childViewHeight / 2));
                    if (selectChild.getBottom() > bottomLine) {
                        smoothScrollBySlow(childViewHeight, DURATION);
                    }
                }
            } else if (position < lastPosition) {
                //向下滑动
                if (getScrollY() != 0) {
                    //设置一个坐标
                    int upLine = childViewHeight + (childViewHeight / 2) + getScrollY();
                    if (selectChild.getTop() < upLine) {
                        smoothScrollBySlow(-childViewHeight, DURATION);
                    }
                }
            } else {
                return;
            }
        } else {
            smoothScrollBySlow(-(getScrollY() - ((position - 2) * childViewHeight)), DURATION);
        }
        indicatorSlide(position);
        int childCount = mChildTextParentView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (position == i) {
                TextView selectTextView = (TextView) mChildTextParentView.getChildAt(position);
                selectTextView.setTextColor(selctColor);
            } else {
                TextView unselectTextView = (TextView) mChildTextParentView.getChildAt(i);
                unselectTextView.setTextColor(unselctColor);
            }
        }
    }
}
