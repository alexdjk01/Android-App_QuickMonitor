package ro.ase.dma.connectinfluxdb;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

// trebuie invatat ce face exact.
public class SemiCircularProgressBar extends View {
    private Paint backgroundPaint;
    private Paint progressPaint;
    private RectF rectF;

    private Paint textPaint;

    private double minimumInterval = 0.0; // Minimum value of the progress bar
    private double maximumInterval = 100.0; // Maximum value of the progress bar
    private double progress = 0.0; // Progress as a double value
    private String progressText;


    public SemiCircularProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        int blueEditext = ContextCompat.getColor(getContext(), R.color.blue_ediText);
        // Initialize the background paint
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(blueEditext); // color defined in @colors
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(20);

        int greenAvocado = ContextCompat.getColor(getContext(), R.color.green_measurement);
        // Initialize the progress paint
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(greenAvocado); // green from @colors
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(45);


        // Initialize the text paint
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.green_measurement)); // Set your text color
        textPaint.setTextSize(80); // Set the text size
        textPaint.setTextAlign(Paint.Align.CENTER); // Center the text horizontally


        // Initialize the RectF object
        rectF = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Define the bounds for the semi-circle
        float padding = progressPaint.getStrokeWidth()/2;
        rectF.set(padding, padding, w - padding, h * 2 - padding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw the background semi-circle
        canvas.drawArc(rectF, 180, 180, false, backgroundPaint);

        // Calculate the sweep angle based on the progress within the range
        float sweepAngle = (float) (180 * ((progress - minimumInterval) / (maximumInterval - minimumInterval)));

        // Draw the progress semi-circle
        canvas.drawArc(rectF, 180, sweepAngle, false, progressPaint);

        progressText = String.format("%.2f", progress) ; // Format the progress
        // Calculate the vertical center position for the text
        float centerY = getHeight() - textPaint.descent() -20;
        canvas.drawText(progressText, getWidth() / 2f, centerY, textPaint);
    }

    public void setProgress(double progress) {
        if (progress < minimumInterval) {
            this.progress = minimumInterval;
        } else if (progress > maximumInterval) {
            this.progress = maximumInterval;
        } else {
            this.progress = progress;
        }
        invalidate(); // Invalidate the view to trigger a redraw
    }

    //changes the color of the progress from green(OK) to yellow(MID) to red(BAD)
    public void setColor(int color)
    {
        progressPaint.setColor(color);
    }
    public void setMinMax(double min, double max)
    {
        minimumInterval = min;
        maximumInterval = max;
    }


}