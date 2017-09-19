package top.creeperdch.geturelayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by CREEPERDCH on 2017/9/18.
 * HINT:
 */

public class GetureButton extends View {

    Paint mPaint;
    int preColor = Color.BLUE;
    int nextColor = Color.GREEN;
    boolean isSelected = false;
    int view_w = 0, view_h = 0;
    int stroke_width = 4;

    public GetureButton(Context context) {
        this(context, null);
    }

    public GetureButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(preColor);
    }

    public void select() {
        isSelected = true;
        invalidate();
    }

    public void back2Normal() {
        isSelected = false;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        view_w = w;
        view_h = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isSelected) mPaint.setColor(nextColor);
        else mPaint.setColor(preColor);

        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(view_w / 2, view_h / 2, 20, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(stroke_width);
        canvas.drawCircle(view_w / 2, view_h / 2, view_w / 2 - stroke_width / 2, mPaint);
    }
}
