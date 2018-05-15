package com.drageview.sorgs.dragview.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.drageview.sorgs.dragview.R;

/**
 * description: 拖拽View.
 *
 * @author Sorgs.
 */
public class DragView extends View {
    /**
     * 有效拖拽值
     */
    private static final int EFFECTIVE_RANGE = 10;


    private Paint mParentPaint;
    private Paint mChildPaint;
    private int mPX1;
    private int mPY1;
    private int mPX2;
    private int mPY2;
    private int mCX1;
    private int mCY1;
    private int mCX2;
    private int mCY2;
    /**
     * 屏幕高度
     */
    private float mScreenHeight;
    /**
     * 整体控件高度
     */
    private float mParentHeight;
    /**
     * 整体控件宽度
     */
    private float mParentWidth = 700f;
    /**
     * 子空间的高度
     */
    private float mChildHeight = 300f;
    /**
     * 控件顶部高度
     */
    private float mTopHeight = 300f;
    /**
     * 控件底部高度
     */
    private float mBottomHeight = 300f;
    /**
     * 屏幕宽度
     */
    private float mScreenWidth;

    public DragView(Context context) {
        super(context);
        init(context);

    }

    /**
     * 初始化
     */
    private void init(Context context) {
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mParentHeight = mScreenHeight - mBottomHeight - mTopHeight;

        mParentPaint = new Paint();
        mParentPaint.setColor(context.getResources().getColor(R.color.colorE));
        mParentPaint.setStyle(Paint.Style.FILL);

        mChildPaint = new Paint();
        mChildPaint.setColor(context.getResources().getColor(R.color.colorT));
        mChildPaint.setStyle(Paint.Style.FILL);

        initCalc();

    }

    /**
     * 坐标点的计算
     */
    private void initCalc() {
        //计算父控件的位置点
        mPX1 = (int) (mScreenWidth / 2 - mParentWidth / 2);
        mPY1 = (int) (mTopHeight);
        mPX2 = (int) (mPX1 + mParentWidth);
        mPY2 = (int) (mPY1 + mParentHeight);

        //刚开始子控件的位置点
        mCX1 = (int) (mScreenWidth / 2 - mParentWidth / 2);
        mCY1 = (int) (mTopHeight);
        mCX2 = (int) (mCX1 + mParentWidth);
        mCY2 = (int) (mCY1 + mChildHeight);

        invalidate();
    }

    public DragView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float beginY = 0;
        if (mCY1 <= event.getRawY() && event.getRawY() <= mCY1 + mChildHeight &&
                mCX1 <= event.getRawX() && event.getRawX() <= mCX1 + mParentWidth) {
            //有效触控范围
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //手指按下
                    //记录按下的位置
                    beginY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //手指移动
                    float y = event.getY() - beginY;
                    if (Math.abs(y) > EFFECTIVE_RANGE) {
                        //更改子控件坐标
                        mCY1 = (int) (0 + y);
                        mCY2 = (int) (mCY1 + mChildHeight);
                        if (mCY1 < mTopHeight) {
                            //Y1小于原定的坐标，防止顶部超过出
                            mCY1 = (int) (mTopHeight);
                            mCY2 = (int) (mCY1 + mChildHeight);
                        } else if (mCY2 > mParentHeight + mTopHeight) {
                            //Y2大于原定的坐标，防止底部超过
                            mCY2 = (int) (mParentHeight + mTopHeight);
                            mCY1 = (int) (mCY2 - mChildHeight);
                        }
                        //重新绘制
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    //手指抬起

                    break;
                case MotionEvent.ACTION_CANCEL:
                    //事件取消
                    break;
                default:
                    break;
            }
        }

        return true;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制父控件
        canvas.drawRect(mPX1, mPY1, mPX2, mPY2, mParentPaint);

        //绘制子空间
        canvas.drawRect(mCX1, mCY1, mCX2, mCY2, mChildPaint);
    }

    /**
     * 设置数据
     */
    public void setData(float parentHeight, float parentWidth, float childHeight, float topHeight, float bottomHeight) {
        mParentHeight = parentHeight;
        mParentWidth = parentWidth;
        mChildHeight = childHeight;
        mTopHeight = topHeight;
        mBottomHeight = bottomHeight;
        initCalc();
    }

    public Picture getPic() {
        Picture picture = new Picture();
        // 开始录制 (接收返回值Canvas)
        Canvas canvas = picture.beginRecording((int) mParentWidth, (int) mChildHeight);
        return picture;
    }
}
