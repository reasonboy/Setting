package com.jzzh.tools;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ZhCheckBox extends View {

    private Bitmap mOff;
    private Bitmap mOn;
    private float mWidth;
    private float mHeight;
    private int mLayoutWidth;
    private int mLayoutHeight;

    private boolean mCheck = true;
    private OnZhCheckedChangeListener mChangeListener;

    public ZhCheckBox(Context context) {
        super(context);
    }

    public ZhCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZhCheckBox);
        int idOff = typedArray.getResourceId(R.styleable.ZhCheckBox_res_off, R.drawable.switch_off);
        Drawable offDrawable = context.getDrawable(idOff);
        mOff = drawableToBitamp(offDrawable);
        int idOn = typedArray.getResourceId(R.styleable.ZhCheckBox_res_on, R.drawable.switch_on);
        Drawable onDrawable = context.getDrawable(idOn);
        mOn = drawableToBitamp(onDrawable);
        mWidth = typedArray.getDimensionPixelOffset(R.styleable.ZhCheckBox_designated_width, mOff.getWidth());
        mHeight = typedArray.getDimensionPixelOffset(R.styleable.ZhCheckBox_designated_height, mOff.getHeight());
        Log.v("xml_log_ZhCheckBox","mWidth = " +mWidth+" ; mHeight = " +mHeight);
        setCheck(mCheck);
    }

    private Bitmap drawableToBitamp(Drawable drawable)
    {
        //声明将要创建的bitmap
        Bitmap bitmap = null;
        //获取图片宽度
        int width = drawable.getIntrinsicWidth();
        //获取图片高度
        int height = drawable.getIntrinsicHeight();
        //图片位深，PixelFormat.OPAQUE代表没有透明度，RGB_565就是没有透明度的位深，否则就用ARGB_8888。详细见下面图片编码知识。
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        //创建一个空的Bitmap
        bitmap = Bitmap.createBitmap(width,height,config);
        //在bitmap上创建一个画布
        Canvas canvas = new Canvas(bitmap);
        //设置画布的范围
        drawable.setBounds(0, 0, width, height);
        //将drawable绘制在canvas上
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap b = mCheck?mOn:mOff;
        Bitmap bitmap = resizeImage(b,(int)mWidth,(int)mHeight);
        if(bitmap != null) {
            //canvas.save();
            canvas.drawBitmap(bitmap, 0, 0, null);
            //canvas.restore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        float x = event.getX();
        float y = event.getY();
        //float x1 = event.getRawX();//相对于整屏的坐标
        //float y1 = event.getRawY();//
        //Log.v("xml_log_ZhCheckBox","onTouchEvent" +x+","+y+","+x1+","+y1);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(x <= mLayoutWidth && y <= mLayoutHeight) { //起来时在控件范围内
                    mCheck = !mCheck;
                    if(mChangeListener != null) {
                        mChangeListener.onCheckedChanged(mCheck);
                    }
                    invalidate();
                }
                break;
        }
        return true;
    }

    //Change bitmap size
    private Bitmap resizeImage(Bitmap bitmap, int width, int height) {
        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();

        float scaleWidth = ((float) width) / bmpWidth;
        float scaleHeight = ((float) height) / bmpHeight;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
    }

    public void setCheck(boolean checked) {
        mCheck = checked;
        invalidate();
    }

    public boolean getCheck() {
        return mCheck;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mLayoutWidth = (int)mWidth;
        mLayoutHeight = (int)mHeight;
        this.setMeasuredDimension(mLayoutWidth, mLayoutHeight);
    }

    public interface OnZhCheckedChangeListener {
        public void onCheckedChanged(boolean checked);
    }

    public void setOnZhCheckedChangeListener(OnZhCheckedChangeListener l) {
        mChangeListener = l;
    }

}
