package com.example.healthalert2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.List;

public class LineGraph extends View {

    private List<Float> datapoints;
    private Paint linePaint;
    private Paint pointPaint;
    private Paint textPaint;

    public LineGraph (Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        linePaint = new Paint();
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(5f);
        linePaint.setStyle(Paint.Style.STROKE);

        pointPaint = new Paint();
        pointPaint.setColor(Color.BLACK);
        pointPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30f);
    }
    public void setData(List<Float> data)
    {
        this.datapoints = data;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (datapoints == null || datapoints.size() < 2)
        {
            return;
        }

        float width = getWidth();
        float height = getHeight();
        int n = datapoints.size();

        float min = Float.MIN_VALUE;
        float max = Float.MAX_VALUE;

        for (float v : datapoints)
        {
            if (v > max)
            {
                max = v;
            }
            if (v < min)
            {
                min = v;
            }
        }

        float gap = width / (n-1);

        for (int i = 0; i < n - 1; i++)
        {
            float startX = i * gap;
            float startY = height - ((datapoints.get(i) - min) / (max - min)) * height;
            float endX = (i +1 ) * gap;
            float endY = height - ((datapoints.get(i+1) - min) / (max - min)) * height;

            canvas.drawLine(startX, startY, endX, endY, linePaint);
            canvas.drawCircle(startX, startY, 8, pointPaint);
        }

        float lastX = (n-1)*gap;
        float lastY = height - ((datapoints.get(n - 1) - min) / (max - min)) *height;
        canvas.drawCircle(lastX,lastY, 8, pointPaint);

    }


}
