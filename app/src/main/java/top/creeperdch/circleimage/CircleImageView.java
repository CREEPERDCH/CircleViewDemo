package top.creeperdch.circleimage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by CREEPERDCH on 2017/9/18.
 * HINT:
 */

public class CircleImageView extends View {
    int bitmapHeight;
    int bitmapWidth;
    int mStrokeWidth = 0;
    int mStrokeColor;
    boolean hasBorder = false;
    Bitmap mBitmap;
    BitmapShader mShader;
    Paint mPaint;
    Paint bPaint;

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView);
        mStrokeWidth = array.getInt(R.styleable.CircleImageView_stroke_width, 0);
        mStrokeColor = array.getColor(R.styleable.CircleImageView_stroke_color, Color.WHITE);
        if (mStrokeWidth > 0) hasBorder = true;
        init();
        array.recycle();
    }

    private void init() {
        //获取圆形的背景
        getBitmap();
        mShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setShader(mShader);
        if (hasBorder) {
            bPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            bPaint.setStyle(Paint.Style.STROKE);
            bPaint.setColor(mStrokeColor);
            bPaint.setStrokeWidth(mStrokeWidth);
        }
        //取消背景
        setBackground(null);
    }

    /**
     * 获取背景图片,将背景图片转换为bitmap
     */
    private void getBitmap() {
        //获取图片的原宽度
        Drawable background = getBackground();
        if (null == background) return;
        int w = background.getIntrinsicWidth();
        int h = background.getIntrinsicHeight();

        //使用全局变量保存宽度和高度
        bitmapWidth = w;
        bitmapHeight = h;

        //生成一张同等宽度大小的bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
        //使用一张空白的画布加载这个bitmap
        Canvas canvas = new Canvas(mBitmap);
        //设置图片的输出边界
        background.setBounds(0, 0, w, h);
        background.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w_mode = MeasureSpec.getMode(widthMeasureSpec);
        int w_size = MeasureSpec.getSize(widthMeasureSpec);
        int h_mode = MeasureSpec.getMode(heightMeasureSpec);
        int h_size = MeasureSpec.getSize(heightMeasureSpec);

        int resultWidth = 0, resultHeight = 0;
        switch (w_mode) {
            //根据控件的内控进行测量,最大不能大于父控件的大小,根据我们控件的内容来决定
            case MeasureSpec.AT_MOST:
                resultWidth = Math.min(bitmapWidth, w_size);
                break;
            //EXACTLY,直接获取到赋值
            case MeasureSpec.EXACTLY:
                resultWidth = w_size;
                break;
            //对子控件的宽度不受限制
            case MeasureSpec.UNSPECIFIED:
                resultWidth = bitmapWidth;
                break;
        }
        switch (h_mode) {
            //根据控件的内控进行测量,最大不能大于父控件的大小,根据我们控件的内容来决定
            case MeasureSpec.AT_MOST:
                resultHeight = Math.min(bitmapHeight, h_size);
                break;
            //EXACTLY,直接获取到赋值
            case MeasureSpec.EXACTLY:
                resultHeight = h_size;
                break;
            //对子控件的宽度不受限制
            case MeasureSpec.UNSPECIFIED:
                resultHeight = bitmapHeight;
                break;
        }
        int result = Math.min(resultWidth, resultHeight);
        setMeasuredDimension(result, result);
    }

    //控件大小发生改变的时候调用
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        float scaleW = (w * 1.0f) / (bitmapWidth * 1.0f);
        float scaleH = (h * 1.0f) / (bitmapHeight * 1.0f);
        float result = Math.max(scaleW, scaleH);

        Matrix matrix = new Matrix();
        matrix.setScale(result, result);
        mShader.setLocalMatrix(matrix);
        mPaint.setShader(mShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, getMeasuredWidth() / 2, mPaint);
        if (hasBorder)
            canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, getMeasuredHeight() / 2 - mStrokeWidth / 2, bPaint);
    }
}
