package com.example.healthalert2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LineGraph extends View {

    private List<Float> datapoints;
    private List<String> labels;
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
        textPaint.setTextSize(25f);

        datapoints = new ArrayList<>();
        labels = new ArrayList<>();
    }
    public void setData(List<Float> data, List<String> formattedTimeStamps)
    {
        this.datapoints = data;

        this.labels = new ArrayList<>(formattedTimeStamps);

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

        //paddinng
        float paddingTop = 40f;
        float paddingBottom = 40f;
        float paddingLeft = 50f;
        float paddingRight = 50f;

        float graphHeight = height - paddingTop - paddingBottom;
        float graphWidth = width - paddingLeft - paddingRight;

        //find min and max for Y
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
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

        //buff
        float buffer = (max - min) * 0.1f;
        if (buffer == 0)
        {
            max += buffer;
            min -= buffer;
        }

        float gap = graphWidth / (n-1);

        //draw lines
        for (int i = 0; i < n - 1; i++)
        {
            float startX = paddingLeft + i * gap;
            float startY = paddingTop + graphHeight * (1 - (datapoints.get(i) - min) / (max - min));

            float endX = paddingLeft + (i + 1) * gap;
            float endY = paddingTop + graphHeight * (1 - (datapoints.get(i + 1) - min) / (max - min));

            canvas.drawLine(startX, startY, endX, endY, linePaint);
            canvas.drawCircle(startX, startY, 8, pointPaint);
        }

        //last point
        float lastX = paddingLeft + (n-1) * gap;
        float lastY = paddingTop + graphHeight * (1 - (datapoints.get(n - 1) - min) / (max - min));
        canvas.drawCircle(lastX,lastY, 8, pointPaint);

        //x axis labels
        for (int i = 0; i < labels.size(); i++)
        {
            float x = paddingLeft + i * gap;
            float y = height - paddingBottom + 30;
            canvas.drawText(labels.get(i), x - 30, y, textPaint);
        }

        //min&max on Y
        canvas.drawText(String.valueOf((int) min), 5, height - paddingBottom, textPaint);
        canvas.drawText(String.valueOf((int) max), 5, paddingTop, textPaint);
    }


}
