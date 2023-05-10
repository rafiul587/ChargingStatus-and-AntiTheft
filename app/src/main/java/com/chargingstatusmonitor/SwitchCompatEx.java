package com.chargingstatusmonitor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.res.ResourcesCompat;

import com.chargingstatusmonitor.souhadev.R;

public class SwitchCompatEx extends SwitchCompat {

    private static final int TRACK_COLOR = 0xFFFFFFFF;
    private static final int TRACK_STROKE_COLOR = 0xFF00A1FF;
    private static final int TRACK_LABEL_COLOR = 0xFFFFFFFF;
    private static final int THUMB_COLOR = 0xFFFFFFFF;
    private static final int GREEN_COLOR = 0xFF3E963F;
    private static final int GREY_COLOR = 0xFF534E53;
    private static final int THUMB_LABEL_COLOR = 0xFF00A1FF;
    private static final float TRACK_STROKE_WIDTH = dp2Px(2f);
    private static final float TRACK_LABEL_SIZE = sp2Px(14f);
    private static final float THUMB_LABEL_SIZE = sp2Px(14f);

    private final Paint trackLabelPaint = new Paint();
    private final Paint thumbLabelPaint = new Paint();
    private String thumbLabel;

    public SwitchCompatEx(Context context) {
        super(context);
        thumbLabel = isChecked() ? getTextOn().toString() : getTextOff().toString();
        init();
    }

    public SwitchCompatEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        thumbLabel = isChecked() ? getTextOn().toString() : getTextOff().toString();
        init();
    }

    public SwitchCompatEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        thumbLabel = isChecked() ? getTextOn().toString() : getTextOff().toString();
        init();
    }

    private void init() {
        trackLabelPaint.setAntiAlias(true);
        trackLabelPaint.setTextSize(TRACK_LABEL_SIZE);
        trackLabelPaint.setColor(TRACK_LABEL_COLOR);
        trackLabelPaint.setTypeface(Typeface.DEFAULT_BOLD);

        thumbLabelPaint.setAntiAlias(true);
        thumbLabelPaint.setTextSize(THUMB_LABEL_SIZE);
        thumbLabelPaint.setTypeface(Typeface.DEFAULT_BOLD);

        setTrackDrawable(new TrackDrawable());
        setThumbDrawable(new ThumbDrawable());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        ((GradientDrawable) getTrackDrawable()).setSize(w, h);
        ((GradientDrawable) getThumbDrawable()).setSize(w / 2, h);
    }

    private static void drawLabel(Canvas canvas, Rect bounds, Paint paint, CharSequence text) {
        if (text == null) return;
        RectF tb = new RectF();
        tb.right = paint.measureText(text, 0, text.length());
        tb.bottom = paint.descent() - paint.ascent();
        tb.left += bounds.centerX() - tb.centerX();
        tb.top += bounds.centerY() - tb.centerY() - paint.ascent();
        canvas.drawText(text.toString(), tb.left, tb.top, paint);
    }

    private static float sp2Px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }

    private static float dp2Px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    class TrackDrawable extends GradientDrawable {

        private final Rect textOffBounds = new Rect();
        private final Rect textOnBounds = new Rect();

        public TrackDrawable() {
            //setColor(TRACK_COLOR);

        }

        @Override
        protected void onBoundsChange(Rect r) {
            super.onBoundsChange(r);

            setCornerRadius(r.height() / 2f);

            textOffBounds.set(r);
            textOffBounds.right /= 2;

            textOnBounds.set(textOffBounds);
            textOnBounds.offset(textOffBounds.right, 0);
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);

            drawLabel(canvas, textOffBounds, trackLabelPaint, getTextOff().toString());
            drawLabel(canvas, textOnBounds, trackLabelPaint, getTextOn().toString());
            if (isChecked()) {
                setBackgroundResource(R.drawable.bg_button_gradient_greenish);
                setStroke((int) TRACK_STROKE_WIDTH, GREEN_COLOR);
                thumbLabelPaint.setColor(GREEN_COLOR);
            }
            else {
                setBackgroundResource(R.drawable.bg_button_gradient_pink);
                setStroke((int) TRACK_STROKE_WIDTH, GREY_COLOR);
                thumbLabelPaint.setColor(GREY_COLOR);
            }
            invalidate();
        }
    }

    class ThumbDrawable extends GradientDrawable {

        private final Rect thumbLabelBounds = new Rect();

        public ThumbDrawable() {
            setColor(THUMB_COLOR);
        }

        @Override
        protected void onBoundsChange(Rect r) {
            super.onBoundsChange(r);
            setCornerRadius(r.height() / 2f);
            r.inset(4, 4);
            thumbLabelBounds.set(r);
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            thumbLabel = isChecked() ? getTextOn().toString() : getTextOff().toString();
            drawLabel(canvas, thumbLabelBounds, thumbLabelPaint, thumbLabel);
            invalidate();
            requestLayout();
        }
    }
}
