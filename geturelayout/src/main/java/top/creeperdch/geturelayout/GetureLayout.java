package top.creeperdch.geturelayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by CREEPERDCH on 2017/9/18.
 * HINT:
 */

public class GetureLayout extends ViewGroup {

    Paint mPaint;
    LinkedHashMap<Integer, GetureCircle> select;
    ArrayList<GetureCircle> all_position;

    int link_count = 3;
    int space;
    int padding;

    Path mPath = new Path();

    public GetureLayout(Context context) {
        this(context, null);
    }

    public GetureLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);

        select = new LinkedHashMap<>();
        all_position = new ArrayList<>();
        int total = link_count * link_count;

        for (int i = 0; i < total; i++) {
            GetureButton button = new GetureButton(getContext());
            addView(button);
            GetureCircle circle = new GetureCircle();
            circle.setIndex(i);
            all_position.add(circle);
        }
    }

    //用来放置子控件,只要你显示在界面的控件都在要在个方法内部进行放置
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int total = getChildCount();
        for (int i = 0; i < total; i++) {
            View view = getChildAt(i);

            int line = i / link_count;//行数
            int row = i % link_count;//列数

            int left = row * space + padding;
            int top = line * space + padding;
            int right = left + view.getMeasuredWidth();
            int bottom = top + view.getMeasuredHeight();

            GetureCircle circle = all_position.get(i);
            view.layout(left, top, right, bottom);
            circle.setRange(left, top, right, bottom);
            //设置选中的按钮的中心点
            circle.setCenter(left + padding, top + padding);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w_result = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(w_result, w_result);

        space = w_result / 3;
        padding = space / 4;

        int childSize = padding * 2;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            int childMeasure = MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY);
            measureChild(view, childMeasure, childMeasure);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                selectCircle(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                float move_x = event.getX();
                float move_y = event.getY();
                selectCircle(move_x, move_y);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                back2Normal();
                break;
        }
        invalidate();
        return true;
    }

    private void back2Normal() {
        for (Map.Entry<Integer, GetureCircle> tmp : select.entrySet()) {
            int index = tmp.getKey();
            GetureButton btn = (GetureButton) getChildAt(index);
            btn.back2Normal();
        }
        select.clear();
    }

    private void selectCircle(float x, float y) {
        for (int i = 0; i < all_position.size(); i++) {
            GetureCircle circle = all_position.get(i);
            Boolean isInside = circle.isInside(x, y);
            if (isInside) {
                int index = circle.getIndex();
                GetureButton btn = (GetureButton) getChildAt(index);
                btn.select();
                select.put(index, circle);
                break;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Iterator<Map.Entry<Integer, GetureCircle>> iterator = select.entrySet().iterator();
        if (iterator.hasNext()) {
            mPath.reset();
            Map.Entry<Integer, GetureCircle> first = iterator.next();
            GetureCircle first_data = first.getValue();
            mPath.moveTo(first_data.x, first_data.y);
            while (iterator.hasNext()) {
                GetureCircle other_data = iterator.next().getValue();
                mPath.lineTo(other_data.x, other_data.y);
            }
            canvas.drawPath(mPath, mPaint);
        }
    }

    class GetureCircle {
        //子控件的坐标范围
        RectF range;
        //子控件的角标
        int index;
        //中心点的坐标
        int x, y;

        void setCenter(int x, int y) {
            this.x = x;
            this.y = y;
        }

        GetureCircle() {
            this.range = new RectF();
        }

        int getIndex() {
            return index;
        }

        void setIndex(int index) {
            this.index = index;
        }

        void setRange(int left, int top, int right, int bottom) {
            range.set(left, top, right, bottom);
        }

        //判断是否点击到了按钮
        boolean isInside(float x, float y) {
            return range.contains(x, y);
        }
    }
}
