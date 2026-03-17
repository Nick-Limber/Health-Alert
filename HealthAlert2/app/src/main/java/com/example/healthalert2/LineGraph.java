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

    private SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd");
    private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
    public void setData(List<Float> data, List<String> rawTimeStamp)
    {
        this.datapoints = data;

        labels.clear();
        for (String ts : rawTimeStamp)
        {
            try {
                Date date = dbFormat.parse(ts);
                labels.add(displayFormat.format(date));
            }
            catch (ParseException e)
            {
                labels.add(ts);
            }
        }

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

        float gap = width / (n-1);

        //draw lines
        for (int i = 0; i < n - 1; i++)
        {
            float startX = i * gap;
            float startY = height - ((datapoints.get(i) - min) / (max - min)) * height;

            float endX = (i +1 ) * gap;
            float endY = height - ((datapoints.get(i + 1) - min) / (max - min)) * height;

            canvas.drawLine(startX, startY, endX, endY, linePaint);
            canvas.drawCircle(startX, startY, 8, pointPaint);
        }

        //last point
        float lastX = (n-1)*gap;
        float lastY = height - ((datapoints.get(n - 1) - min) / (max - min)) *height;
        canvas.drawCircle(lastX,lastY, 8, pointPaint);

        //x axis labels
        for (int i = 0; i < labels.size(); i++)
        {
            float x = i * gap;
            float y = height + 30;
            canvas.drawText(labels.get(i), x - 30, y, textPaint);
        }

        //min&max on Y
        canvas.drawText(String.valueOf((int) min), 5, height - 5, textPaint);
        canvas.drawText(String.valueOf((int) max), 5, 30, textPaint);
    }


}
