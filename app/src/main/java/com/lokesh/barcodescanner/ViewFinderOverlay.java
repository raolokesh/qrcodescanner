package com.lokesh.barcodescanner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

public  class ViewFinderOverlay extends View {

    private final Paint boxPaint;
    private final Paint scrimPaint;
    private final Paint eraserPaint;
    private final float boxCornerRadius;
    private RectF boxRect;

    public ViewFinderOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);

        boxPaint = new Paint();
        boxPaint.setColor(ContextCompat.getColor(context, R.color.barcode_reticle_stroke));
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(context.getResources().getDimensionPixelOffset(R.dimen.barcode_reticle_stroke_width));

        scrimPaint = new Paint();
        scrimPaint.setColor(ContextCompat.getColor(context, R.color.barcode_reticle_background));

        eraserPaint = new Paint();
        eraserPaint.setStrokeWidth(boxPaint.getStrokeWidth());
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        boxCornerRadius = context.getResources().getDimensionPixelOffset(R.dimen.barcode_reticle_corner_radius);
    }

    public void setViewFinder() {
        float overlayWidth = (float) getWidth();
        float overlayHeight = (float) getHeight();
        float boxWidth = overlayWidth * 80 / 100;
        float boxHeight = overlayHeight * 36 / 100;
        float cx = overlayWidth / 2;
        float cy = overlayHeight / 2;
        boxRect = new RectF(cx - boxWidth / 2, cy - boxHeight / 2, cx + boxWidth / 2, cy + boxHeight / 2);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (boxRect != null) {
            // Draws the dark background scrim and leaves the box area clear.
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), scrimPaint);

            // As the stroke is always centered, so erase twice with FILL and STROKE respectively to clear
            // all area that the box rect would occupy.
            eraserPaint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(boxRect, boxCornerRadius, boxCornerRadius, eraserPaint);
            eraserPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(boxRect, boxCornerRadius, boxCornerRadius, eraserPaint);

            // Draws the box.
            canvas.drawRoundRect(boxRect, boxCornerRadius, boxCornerRadius, boxPaint);
        }
    }
}

