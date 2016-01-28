package com.example.syndarin.circlehole;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by syndarin on 3/5/15.
 */
public class HoleView extends ImageView implements View.OnTouchListener {

    private Paint mTransparentPaint;
    private Paint mOverlayPaint;
    private Paint mDefaultPaint = new Paint();
    private Paint mPercentPaint;

    private int mWidth;
    private int mHeight;

    private int mCircleCenterX;
    private int mCircleCenterY;

    private int mRadius;

    private int mStripeWidth = 50;

    private int mInnerCircleRadius;

    private Canvas mCanvas;
    private Bitmap mOverlayBitmap;

    private RectF mArcRect;

    private int[] mZeroCoords;

    public HoleView(Context context) {
        super(context);
        init();
    }

    public HoleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HoleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mOverlayPaint = new Paint();
        mOverlayPaint.setStyle(Paint.Style.FILL);
        mOverlayPaint.setColor(Color.argb(100, 255, 0, 0));
        mOverlayPaint.setAntiAlias(true);

        mPercentPaint = new Paint();
        mPercentPaint.setStyle(Paint.Style.FILL);
        mPercentPaint.setColor(Color.argb(255, 255, 0, 0));
        mPercentPaint.setAntiAlias(true);

        mTransparentPaint = new Paint();
        mTransparentPaint.setStyle(Paint.Style.FILL);
        mTransparentPaint.setColor(getResources().getColor(android.R.color.transparent));
        mTransparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mTransparentPaint.setAntiAlias(true);

        mArcRect = new RectF();

        setOnTouchListener(this);
    }

    private void refresh(){
        mWidth = getWidth();
        mHeight = getHeight();

        mCircleCenterX = mWidth / 2;
        mCircleCenterY = mHeight / 2;

        mRadius = mWidth > mHeight ? mWidth / 2 : mHeight / 2;

        mInnerCircleRadius = mRadius - mStripeWidth;

        mArcRect.set(0, 0, mWidth, mHeight);

        mZeroCoords = convertToInnerCoordSystem(mCircleCenterX, mStripeWidth / 2);

        if(mWidth > 0 && mHeight > 0) {
            mOverlayBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mOverlayBitmap);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        refresh();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(mCanvas != null){
            //mCanvas.drawRect(0, 0, mWidth, mHeight, mOverlayPaint);
            mCanvas.drawCircle(mCircleCenterX, mCircleCenterY, mRadius, mOverlayPaint);
            mCanvas.drawArc(mArcRect, 270, 235, true, mPercentPaint);
            mCanvas.drawCircle(mCircleCenterX, mCircleCenterY, mInnerCircleRadius, mTransparentPaint);
            canvas.drawBitmap(mOverlayBitmap, 0, 0, mDefaultPaint);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                double dist = getDistanceFromCenter(event.getX(), event.getY());
                if(isInStripe(dist)) {
                    Log.i("zzz", "In stripe");
                    return true;
                } else {
                    Log.i("zzz", "Missed");
                    return false;
                }
            default:
                return false;
        }
    }

    private boolean isInStripe(double distanceFromCenter){
        return distanceFromCenter > mInnerCircleRadius && distanceFromCenter < mRadius;
    }

    private double getDistanceFromCenter(float x, float y){
        float dx = Math.abs(mCircleCenterX - x);
        float dy = Math.abs(mCircleCenterY - y);
        return Math.hypot(dx, dy);
    }

    private int[] getProgressCircleCenter(int rotationAngle){
        double rad = (rotationAngle * Math.PI) / 180;
        double y = mZeroCoords[1] * Math.cos(rad) - mZeroCoords[0] * Math.sin(rad);
        double x = mZeroCoords[1] * Math.sin(rad) + mZeroCoords[0] * Math.cos(rad);
    }

    private int[] convertToInnerCoordSystem(int viewX, int viewY){
        return new int[]{viewX - mCircleCenterX, viewY - mCircleCenterY};
    }
}
