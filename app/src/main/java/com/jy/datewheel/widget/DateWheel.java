package com.jy.datewheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.jy.datewheel.vo.DateItemVO;

import java.util.ArrayList;

/**
 *
 * 仿QQ查看历史消息控件
 * Created by jing on 26/03/16.
 * @author jing
 */
public class DateWheel extends View {
    private static final String TAG = "TuneWheel";
    private static final int COLOR_HIG_LIGHT = Color.GREEN;
    private static final int COLOR_LOW_LIGHT = Color.DKGRAY;
    private static final int COLOR_BACKEGROUND = Color.WHITE;

    private ArrayList<DateItemVO> mDatas;
    private float mTextWidth;

    public interface OnValueChangeListener {
        void onValueChange(float value);
    }

    public static final int MOD_TYPE_ONE = 15;//draw interval num
    private static final int ITEM_HALF_DIVIDER = 40;
    private static final int ITEM_ONE_DIVIDER = 10;
    private static final int ITEM_MAX_HEIGHT = 40;
    //small line length
    private static final int ITEM_MIN_HEIGHT = 20;
    private static final int TEXT_SIZE = 12;
    private float mDensity;
    private float mLineWidth;
    private int mValue = 50,//init dot count
            mMaxValue = 100,
            mModType = MOD_TYPE_ONE,
            mLineDivider = ITEM_HALF_DIVIDER;//line space dp
    private int mLastX, mMove;
    private int mWidth, mHeight;
    private int mMinVelocity;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private OnValueChangeListener mListener;

    private boolean isScrollToRight;

    private Paint mLinePaint;
    private TextPaint mTextPaint;

    private boolean isFling;

    public DateWheel(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(getContext());
        mDensity = getContext().getResources().getDisplayMetrics().density;
        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
        mLineWidth = 2 * mDensity;
        setBackgroundColor(COLOR_BACKEGROUND);
    }

    /***
     * @param defaultValue
     * @param maxValue
     * @param model
     * @param datas
     */
    public void initViewParam(int defaultValue, int maxValue, int model, ArrayList<DateItemVO> datas) {

        mModType = MOD_TYPE_ONE;
        mLineDivider = ITEM_ONE_DIVIDER;
        mValue = defaultValue;
        mMaxValue = maxValue;
        mDatas = datas;

        mLinePaint = new Paint();
        mLinePaint.setStrokeWidth(mLineWidth);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(TEXT_SIZE * mDensity);

        mTextWidth = Layout.getDesiredWidth("0", mTextPaint);

        invalidate();
        mLastX = 0;
        mMove = 0;
        notifyValueChange();
    }

    /**
     * 设置用于接收结果的监听器
     *
     * @param listener
     */
    public void setValueChangeListener(OnValueChangeListener listener) {
        mListener = listener;
    }

    /**
     * 获取当前刻度值
     *
     * @return
     */
    public float getValue() {
        return mValue;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mWidth = getWidth();
        mHeight = getHeight();
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScaleLine(canvas);
        drawMiddleLine(canvas);
    }

    /**
     * 从中间往两边开始画刻度线
     *
     * @param canvas
     */
    private void drawScaleLine(Canvas canvas) {
        canvas.save();

        //right
        int width = mWidth, drawCount = 0;
        float xPosition;
        for (int i = 0; drawCount <= width; i++) {
            DateItemVO vo;
            if (mValue + i >= mDatas.size() || mValue + i < 0) {
                vo = new DateItemVO();
            } else {
                vo = mDatas.get(mValue + i);
            }

            bindLineColor(vo, mLinePaint);

            int numSize = String.valueOf(vo.showLine).length();
            xPosition = (width / 2 - mMove) + i * mLineDivider * mDensity;
            if (xPosition + getPaddingRight() < mWidth) {
                if (vo.isLineLong) {//show long line
                    //length line
                    canvas.drawLine(xPosition, mDensity * 23 + getPaddingTop(), xPosition, mDensity * ITEM_MAX_HEIGHT, mLinePaint);
                    if (mValue + i <= mMaxValue) {
                        canvas.drawText(vo.showLine, xPosition - (mTextWidth * numSize / 2), getPaddingTop() + 15 * mDensity, mTextPaint);
                    }
                } else {
                    if (vo.showToast != null) {
                        //short line
                        canvas.drawLine(xPosition, mDensity * 30 + getPaddingTop(), xPosition, mDensity * ITEM_MAX_HEIGHT, mLinePaint);
                    }
                }
            }

            int interval = mValue - i;

            if (interval < 0 || interval >= mDatas.size()) {
                vo = new DateItemVO();
            } else {
                vo = mDatas.get(interval);
            }

            numSize = String.valueOf(vo.showLine).length();
            //left
            xPosition = (width / 2 - mMove) - i * mLineDivider * mDensity;
            if (xPosition > getPaddingLeft()) {
                bindLineColor(vo, mLinePaint);
                if (vo.isLineLong) {//show long line
                    canvas.drawLine(xPosition, mDensity * 23 + getPaddingTop(), xPosition, mDensity * ITEM_MAX_HEIGHT, mLinePaint);
                    if (mValue - i >= 0) {
                        canvas.drawText(vo.showLine, xPosition - (mTextWidth * numSize / 2), getPaddingTop() + 15 * mDensity, mTextPaint);
                    }
                } else {
                    if (interval == -1 && vo.showToast != null) {
                        canvas.drawLine(xPosition + mDensity * mLineDivider, mDensity * 30 + getPaddingTop(), xPosition + mDensity * mLineDivider, mDensity * ITEM_MAX_HEIGHT, mLinePaint);
                    } else if (vo.showToast != null) {
                        canvas.drawLine(xPosition, mDensity * 30 + getPaddingTop(), xPosition, mDensity * ITEM_MAX_HEIGHT, mLinePaint);
                    }
                }
            }
            drawCount += 2 * mLineDivider * mDensity;
        }
        canvas.restore();
    }

    private void bindLineColor(DateItemVO vo, Paint linePaint) {
        if (vo.isHasData) {
            linePaint.setColor(COLOR_HIG_LIGHT);
        } else {
            linePaint.setColor(COLOR_LOW_LIGHT);
        }
    }

    /**
     * 画中间的指示线
     *
     * @param canvas
     */
    private void drawMiddleLine(Canvas canvas) {
        canvas.save();
        Paint redPaint = new Paint();
        redPaint.setStrokeWidth(mLineWidth);
        redPaint.setColor(Color.BLUE);
        canvas.drawLine(mWidth / 2, 0, mWidth / 2, mDensity * ITEM_MAX_HEIGHT , redPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int xPosition = (int) event.getX();

        acquireVelocityTracker(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScroller.forceFinished(true);
                mLastX = xPosition;
                mMove = 0;
                isFling=false;
                break;
            case MotionEvent.ACTION_MOVE:
                mMove += (mLastX - xPosition);

                if (mMove > 0) {
                    isScrollToRight = true;
                } else {
                    isScrollToRight = false;
                }
                Log.i(TAG, "mMove:" + mMove + ":isScrollToRight" + isScrollToRight);
                changeMoveAndValue();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.i(TAG,"onTouchEvent:id:"+ Thread.currentThread().getId());

                countVelocityTracker(event);

                return false;
            // break;
            default:
                break;
        }
        mLastX = xPosition;
        return true;
    }

    /***
     * recylce widget
     */
    public void recycle() {
        releaseVelocityTracker();
    }

    private void countVelocityTracker(MotionEvent event) {
        mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = mVelocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > mMinVelocity) {
            countMoveOver(false);
            if (mValue > 0 && mValue < mMaxValue) {
                isFling = true;
                mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
            }
        }else {
            isFling = true;
            countMoveOver(true);
        }

    }

    /**
     *
     * @param event 向VelocityTracker添加MotionEvent
     *
     * @see VelocityTracker#obtain()
     * @see VelocityTracker#addMovement(MotionEvent)
     */
    private void acquireVelocityTracker(final MotionEvent event) {
        if(null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }


    private void releaseVelocityTracker(){
        if(null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
        }
    }


    private void changeMoveAndValue() {
        int tValue = (int) (mMove / (mLineDivider * mDensity));//move item count
        if (Math.abs(tValue) > 0) {
            mValue += tValue;//value change
            mMove -= tValue * mLineDivider * mDensity;
            if (mValue <= 0 || mValue > mMaxValue) {
                if(isFling){
                    if(mValue > mMaxValue){
                        mValue = mMaxValue;
                    }else if(mValue <= 0){
                        mValue = 0;
                    }
                }
                mMove = 0;
                mScroller.forceFinished(true);
            }
            notifyValueChange();
        }
        postInvalidate();
    }

    private void countMoveOver(boolean isStartScroll) {
        int roundMove = Math.round(mMove / (mLineDivider * mDensity));
        int preVlaue = mValue;

        int nowValue = mValue + roundMove;
        nowValue = nowValue <= 0 ? 0 : nowValue;
        nowValue = nowValue > mMaxValue ? mMaxValue : nowValue;
        mLastX = 0;
        mMove = 0;

        int hasDataIndex = findHasDataValue(isScrollToRight);


        if (hasDataIndex != -1) {
            nowValue = hasDataIndex;
        } else {
            mValue = nowValue;
//            mScroller.forceFinished(true);
            notifyValueChange();
            postInvalidate();
            return;
        }

        /*if(nowValue ==0 || nowValue == mMaxValue){
            mValue = nowValue;
            postInvalidate();
            return;
        }*/

        Log.i(TAG, "mVelocityTracker.getXVelocity():" + mVelocityTracker.getXVelocity() + ":nowValue:" + nowValue);

        if (isStartScroll && isFling) {
            int len = -(int) ((nowValue - preVlaue) * mLineDivider * mDensity);
            if (len > 0) {
                len += 1;
            } else {
                len -= 1;
            }
            isFling  = false;
            mScroller.startScroll(0, 0, len, 0, 500);
            invalidate();
        } else {
            if (mValue <= 0 || mValue >= mMaxValue) {
                mValue = mValue <= 0 ? 0 : mMaxValue;
                mMove = 0;
                mScroller.forceFinished(true);
            }
            notifyValueChange();
            invalidate();
        }
    }


    public int findHasDataValue(boolean isScrollToRight) {
        int len = mDatas.size();
        if (isScrollToRight) {
            for (int i = mValue; i < len; i++) {
                if (i < 0 || i >= len) {
                    return -1;
                }
                if (mDatas.get(i).isHasData) {
                    return i;
                }
            }
        } else {
            for (int i = mValue; i >= 0; i--) {
                if (i < 0 || i >= len) {
                    return -1;
                }
                if (mDatas.get(i).isHasData) {
                    return i;
                }
            }
        }
        return -1;
    }


    private void notifyValueChange() {
        if (null != mListener) {
            if (mModType == MOD_TYPE_ONE) {
                int result = mValue;
                if (mValue > mMaxValue) {
                    result = mMaxValue;
                }

                mListener.onValueChange(result < 0 ? 0 : result);
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            if (mScroller.getCurrX() == mScroller.getFinalX()) { // over
                countMoveOver(true);
            } else {
                int xPosition = mScroller.getCurrX();
                mMove += (mLastX - xPosition);
                changeMoveAndValue();
                mLastX = xPosition;
            }
        }
    }
}