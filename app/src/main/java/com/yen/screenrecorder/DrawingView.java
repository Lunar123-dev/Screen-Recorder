package com.yen.screenrecorder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    private Paint drawPaint;
    private Path currentPath;
    private List<Path> paths = new ArrayList<>();
    private List<Paint> paints = new ArrayList<>();

    public DrawingView(Context context) {
        super(context);
        init();
    }

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        currentPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(Color.parseColor("#D32F2F"));
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(16f);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < paths.size(); i++) {
            canvas.drawPath(paths.get(i), paints.get(i));
        }
        canvas.drawPath(currentPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                currentPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                currentPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                currentPath.lineTo(touchX, touchY);
                paths.add(currentPath);
                paints.add(new Paint(drawPaint));
                currentPath = new Path();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void clearCanvas() {
        paths.clear();
        paints.clear();
        currentPath.reset();
        invalidate();
    }
}
