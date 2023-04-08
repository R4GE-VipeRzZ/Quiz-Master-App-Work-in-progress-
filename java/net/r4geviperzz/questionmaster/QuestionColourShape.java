package net.r4geviperzz.questionmaster;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class QuestionColourShape extends View {

    private Paint paint;

    public QuestionColourShape(Context context) {
        super(context);
        init();
    }

    public QuestionColourShape(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuestionColourShape(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // Initialize the paint object
    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
    }

    // Override the onDraw method to draw the rounded square
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Calculate the dimensions of the rounded square
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int cornerRadius = Math.min(width, height) / 4;

        // Draw the rounded square using the paint object
        canvas.drawRoundRect(0, 0, width, height, cornerRadius, cornerRadius, paint);
    }

    // Set the colour of the rounded square and invalidate the view to redraw it
    public void setColour(int color) {
        paint.setColor(color);
        invalidate();
    }
}

