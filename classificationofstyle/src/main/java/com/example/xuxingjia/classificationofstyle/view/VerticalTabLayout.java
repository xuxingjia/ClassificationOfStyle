package com.example.xuxingjia.classificationofstyle.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
    private int selctColor = Color.RED;
    private int unselctColor = Color.BLACK;

    private List<String> leftStrings = null;
    private LinearLayout mIndicatorView;
    private int childViewHeight;
    private int lastPosition;
    private Scroller mScroller;
    private int textSize = 49;
    private int indicatorWidth = 0;
    private int indicatorHeight = 0;
    private int indicatorColor = Color.GREEN;


    public VerticalTabLayout(Context context) {
        this(context, null);
    }

    public VerticalTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        indicatorWidth = PxUtil.dip2px(context, 5);
        indicatorHeight = PxUtil.dip2px(context, 24);
        mScroller = new Scroller(context);
        childViewHeight = PxUtil.dip2px(context, 67);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable
                .VerticalTabLayout);
        if (typedArray != null) {
            int indexCount = typedArray.getIndexCount();
            for (int i = 0; i < indexCount; i++) {
                int index = typedArray.getIndex(i);
                if (index == R.styleable.VerticalTabLayout_select_text_color) {
                    selctColor = typedArray.getColor(index, selctColor);
                } else if (index == R.styleable.VerticalTabLayout_text_color) {
                    unselctColor = typedArray.getColor(index, unselctColor);
                } else if (index == R.styleable.VerticalTabLayout_tab_text_size) {
                    textSize = typedArray.getDimensionPixelSize(index, textSize);
                } else if (index == R.styleable.VerticalTabLayout_indicator_width) {
                    indicatorWidth = typedArray.getDimensionPixelSize(index, indicatorWidth);
                } else if (index == R.styleable.VerticalTabLayout_indicator_height) {
                    indicatorHeight = typedArray.getDimensionPixelSize(index, indicatorHeight);
                } else if (index == R.styleable.VerticalTabLayout_indicator_color) {
                    indicatorColor = typedArray.getColor(index, indicatorColor);
                }
            }
            typedArray.recycle();
        }
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
    @SuppressLint("RtlHardcoded")
    private void addLeftChildView(List<String> leftStrings) {
        mChildTextParentView.removeAllViews();
        for (int i = 0; i < leftStrings.size(); i++) {
            TextView textView = new TextView(context);
            textView.setTextSize(PxUtil.px2sp(context, textSize));
            if (mChildTextParentView != null) {
                mChildTextParentView.addView(textView, i);
            }
            textView.setTag(i);
            textView.setOnClickListener(this);
            //设置textView属性
            ViewGroup.LayoutParams layoutParams = textView.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            textView.setLayoutParams(layoutParams);
            textView.setGravity(Gravity.LEFT);
            textView.setText(leftStrings.get(i));
            textView.setPadding(15, 25, 8, 25);
            textView.setSingleLine();
            //设置默认选中效果
            if (i == 0) {
                textView.setTextColor(selctColor);
            } else {
                textView.setTextColor(unselctColor);
            }
        }

        if (mChildTextParentView.getChildCount() > 0) {
            final TextView childView = (TextView) mChildTextParentView.getChildAt(0);
            ViewTreeObserver treeObserver = childView.getViewTreeObserver();
            treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        childView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int height = childView.getHeight();
                        if (height > 0) {
                            childViewHeight = height;
                        }
                        if (mIndicatorView != null) {
                            ViewGroup.LayoutParams layoutParams = mIndicatorView.getLayoutParams();
                            layoutParams.height = childViewHeight;
                            mIndicatorView.setLayoutParams(layoutParams);
                        }
                    }
                }
            });
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
        if (mIndicatorView != null) {
            View mIndicator = mIndicatorView.getChildAt(0);
            Drawable drawable = new ColorDrawable(indicatorColor);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mIndicator.setBackground(drawable);
            }
            ViewGroup.LayoutParams layoutParams = mIndicator.getLayoutParams();
            layoutParams.width = indicatorWidth;
            layoutParams.height = indicatorHeight;
            mIndicator.setLayoutParams(layoutParams);
        }
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
