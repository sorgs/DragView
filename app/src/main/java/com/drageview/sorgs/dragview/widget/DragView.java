package com.drageview.sorgs.dragview.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
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


    private Paint mChildPaint;
    private int mPX1;
    private int mPY1;
    private int mPX2;
    private int mPY2;
    private int mC1X1;
    private int mC1Y1;
    private int mC1X2;
    private int mC1Y2;
    private int mC2X1;
    private int mC2Y1;
    private int mC2X2;
    private int mC2Y2;
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
    private float mParentWidth;
    /**
     * 子空间的高度
     */
    private float mChildHeight;
    /**
     * 控件顶部高度
     */
    private float mTopHeight = 100f;
    /**
     * 控件底部高度
     */
    private float mBottomHeight;
    /**
     * 屏幕宽度
     */
    private float mScreenWidth;

    private Bitmap mParentBg;

    private Context mContext;

    public DragView(Context context) {
        super(context);
        init(context);

    }

    /**
     * 初始化
     */
    private void init(Context context) {

        mContext = context;

        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;

        mParentWidth = mScreenWidth - 2 * dp2px(context, 79f);
        mParentHeight = mParentWidth * 360 / 203f;

        mChildHeight = mParentWidth * 9 / 16f;


        mChildPaint = new Paint();
        mChildPaint.setColor(context.getResources().getColor(R.color.colorT));
        mChildPaint.setStyle(Paint.Style.FILL);
        initCalc();

    }

    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 坐标点的计算
     * X轴基本不变，变化的是Y轴
     */
    private void initCalc() {
        //计算父控件的位置点
        mPX1 = (int) (mScreenWidth / 2f - mParentWidth / 2f);
        mPY1 = (int) (mTopHeight);
        mPX2 = (int) (mScreenWidth / 2f + mParentWidth / 2f);
        mPY2 = (int) (mTopHeight + mParentHeight);

        //刚开始子控件1的位置点
        mC1X1 = mPX1;
        mC1Y1 = mPY1;
        mC1X2 = mPX2;
        mC1Y2 = mPY1;

        //刚开始子控件2的位置点
        mC2X1 = mPX1;
        mC2Y1 = (int) (mTopHeight + mChildHeight);
        mC2X2 = mPX2;
        mC2Y2 = (int) (mTopHeight + mParentHeight);


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
        //有效触控范围
        if (mC1Y2 <= event.getRawY() && event.getRawY() <= mC1Y2 + mChildHeight &&
                mC1X1 <= event.getRawX() && event.getRawX() <= mC1X1 + mParentWidth) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //手指按下
                    //记录按下的位置
                    beginY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //手指移动
                    float y = event.getY() - beginY;
                    //if (Math.abs(y) > EFFECTIVE_RANGE) {
                    //mC1Y1和mC2Y2始终不变
                    //更改子控件坐标
                    mC1Y2 = (int) y;

                    mC2Y1 = (int) (y + mChildHeight);
                    if (mC1Y2 < mTopHeight) {
                        //防止顶部超过出

                        //子控件1为0
                        mC1Y2 = (int) (mTopHeight);

                        //子控件2为最大
                        mC2Y1 = (int) (mTopHeight + mChildHeight);

                    } else if (mC1Y2 > mParentHeight + mTopHeight - mChildHeight) {
                        //防止底部超过

                        //子控件1为最大
                        mC1Y2 = (int) (mTopHeight + mParentHeight - mChildHeight);
                        //子控件2为0
                        mC2Y1 = (int) (mTopHeight + mParentHeight);
                    }
                    //重新绘制
                    invalidate();
                    //}
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setMeasuredDimension((int) mParentWidth, (int) mParentHeight);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制父控件

        // 指定图片绘制区域(左上角的四分之一)
        Rect src = new Rect(0, 0, mParentBg.getWidth(), mParentBg.getHeight());
        // 指定图片在屏幕上显示的区域
        Rect dst = new Rect(mPX1, mPY1, mPX2, mPY2);
        canvas.drawBitmap(mParentBg, src, dst, null);

        //绘制子控件1
        canvas.drawRect(mC1X1, mC1Y1, mC1X2, mC1Y2, mChildPaint);


        //绘制子控件2
        canvas.drawRect(mC2X1, mC2Y1, mC2X2, mC2Y2, mChildPaint);
    }

    /**
     * 设置图片
     */
    public void setData(Bitmap bitmap) {
        if (bitmap == null) {
            throw new RuntimeException("bitmap can't null");
        }
        mParentBg = bitmap;
        invalidate();
    }

    public Bitmap getBitmap(Activity activity) {
        View screenView = activity.getWindow().getDecorView();
        screenView.setDrawingCacheEnabled(true);
        screenView.buildDrawingCache();

        //获取屏幕整张图
        Bitmap bitmap = screenView.getDrawingCache();
        //截图指定部分
        if (bitmap != null) {
            bitmap = Bitmap.createBitmap(bitmap, mC1X1, mC1Y2 + dp2px(mContext, 23f),
                    (int) mParentWidth, (int) mChildHeight);
        }
        return bitmap;
    }
}
