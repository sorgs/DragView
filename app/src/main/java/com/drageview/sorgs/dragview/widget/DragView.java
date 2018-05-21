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
 * @date 2018.5.14
 */
public class DragView extends View {


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
     * 屏幕宽度
     */
    private float mScreenWidth;

    private Bitmap mParentBg;

    private Context mContext;
    private float mDistanceY;

    public DragView(Context context) {
        super(context);
        init(context);

    }

    public DragView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    /**
     * 初始化
     */
    private void init(Context context) {

        mContext = context;

        mChildPaint = new Paint();
        mChildPaint.setColor(context.getResources().getColor(R.color.colorT));
        mChildPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mParentHeight = MeasureSpec.getSize(heightMeasureSpec);
        mScreenWidth = MeasureSpec.getSize(widthMeasureSpec);
        //父控件宽度 16:9的宽
        mParentWidth = mParentHeight * 9 / 16f;
        //选中区域高度
        mChildHeight = mParentWidth * 9 / 16f;
        initCalc();
    }

    /**
     * 坐标点的计算
     * X轴基本不变，变化的是Y轴
     */
    private void initCalc() {
        //计算父控件的位置点
        mPX1 = (int) (mScreenWidth / 2f - mParentWidth / 2f);
        mPY1 = 0;
        mPX2 = (int) (mScreenWidth / 2f + mParentWidth / 2f);
        mPY2 = (int) (mParentHeight);

        //刚开始子控件1的位置点
        mC1X1 = mPX1;
        mC1Y1 = mPY1;
        mC1X2 = mPX2;
        mC1Y2 = mPY1;

        //刚开始子控件2的位置点
        mC2X1 = mPX1;
        mC2Y1 = (int) (mChildHeight);
        mC2X2 = mPX2;
        mC2Y2 = mPY2;


    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //有效触控范围(X轴，Y轴另外判断)
        if (mC1X1 <= event.getRawX() && event.getRawX() <= mC1X1 + mParentWidth) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //手指按下
                    //记录按下的距离
                    float beginY = event.getY();

                    if (beginY < mC1Y2) {
                        //起始点在选择框上部，不做反应
                        return false;
                    } else if (beginY > mC1Y2 + mChildHeight) {
                        //起始点在选择框下部，不做反应
                        return false;
                    }

                    //记录按下的位置和选择区域的上边距的差
                    mDistanceY = beginY - mC1Y2;
                    break;
                case MotionEvent.ACTION_MOVE:

                    //mC1Y1和mC2Y2始终不变
                    //更改子控件坐标
                    mC1Y2 = (int) (event.getY() - mDistanceY);

                    mC2Y1 = (int) (event.getY() - mDistanceY + mChildHeight);

                    //往上滑动
                    if (mC1Y2 < 0) {
                        //防止顶部超过出

                        //子控件1为0
                        mC1Y2 = 0;

                        //子控件2为最大
                        mC2Y1 = (int) (mChildHeight);

                    } else if (mC1Y2 > mParentHeight - mChildHeight) {
                        //防止底部超过

                        //子控件1为最大
                        mC1Y2 = (int) (mParentHeight - mChildHeight);
                        //子控件2为0
                        mC2Y1 = (int) (mParentHeight);
                    }
                    //重新绘制
                    invalidate();
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
            bitmap = Bitmap.createBitmap(bitmap, mC1X1, mC1Y2 + getStatusBarHeight(),
                    (int) mParentWidth, (int) mChildHeight);
        }
        invalidate();
        return bitmap;
    }

    /**
     * 获取状态栏高度
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
