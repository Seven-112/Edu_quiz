package com.brightfuture.eduquiz.helper;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.brightfuture.eduquiz.Constant;
import com.brightfuture.eduquiz.R;

@SuppressWarnings("FieldCanBeLocal")
public class CircularProgressIndicator2 extends View {

    public static final int DIRECTION_CLOCKWISE = 0;
    public static final int DIRECTION_COUNTERCLOCKWISE = 1;

    private static final int DEFAULT_PROGRESS_START_ANGLE = 270;
    private static final int ANGLE_START_PROGRESS_BACKGROUND = 0;
    private static final int ANGLE_END_PROGRESS_BACKGROUND = 360;

    private static final int DESIRED_WIDTH_DP = 150;

    private static final String DEFAULT_PROGRESS_COLOR = Constant.AUD_PROGRESS_COLOR;
    private static final int DEFAULT_TEXT_SIZE_SP = Constant.RESULT_PROGRESS_TEXT_SIZE;
    private static final int DEFAULT_STROKE_WIDTH_DP = Constant.RESULT_PROGRESS_STROKE_WIDTH;
    private static final String DEFAULT_PROGRESS_BACKGROUND_COLOR = Constant.AUD_PROGRESS_BG_COLOR;

    private static final int DEFAULT_ANIMATION_DURATION = 1_000;

    private static final String PROPERTY_ANGLE = "angle";


    private Paint progressPaint;
    private Paint progressBackgroundPaint;
    private Paint dotPaint;
    private Paint textPaint;

    private int startAngle = DEFAULT_PROGRESS_START_ANGLE;
    private int sweepAngle = 0;

    private RectF circleBounds;

    private String progressText;
    private float textX;
    private float textY;

    private float radius;

    private boolean shouldDrawDot = true;

    private double maxProgressValue = 100.0;
    private double progressValue = 0.0;

    @Direction
    private int direction = DIRECTION_COUNTERCLOCKWISE;

    private ValueAnimator progressAnimator;

    @NonNull
    private ProgressTextAdapter progressTextAdapter;

    public CircularProgressIndicator2(Context context) {
        super(context);
        init(context, null);
    }

    public CircularProgressIndicator2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircularProgressIndicator2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircularProgressIndicator2(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {

        int progressColor = Color.parseColor(DEFAULT_PROGRESS_COLOR);
        int progressBackgroundColor = Color.parseColor(DEFAULT_PROGRESS_BACKGROUND_COLOR);
        int progressStrokeWidth = dp2px(DEFAULT_STROKE_WIDTH_DP);
        int textColor = progressColor;
        int textSize = sp2px(DEFAULT_TEXT_SIZE_SP);

        shouldDrawDot = true;
        int dotColor = progressColor;
        int dotWidth = progressStrokeWidth;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressIndicator);

            progressColor = a.getColor(R.styleable.CircularProgressIndicator_progressColor, progressColor);
            progressBackgroundColor = a.getColor(R.styleable.CircularProgressIndicator_progressBackgroundColor, progressBackgroundColor);
            progressStrokeWidth = a.getDimensionPixelSize(R.styleable.CircularProgressIndicator_progressStrokeWidth, progressStrokeWidth);
            textColor = a.getColor(R.styleable.CircularProgressIndicator_textColor, progressColor);
            textSize = a.getDimensionPixelSize(R.styleable.CircularProgressIndicator_textSize, textSize);

            shouldDrawDot = a.getBoolean(R.styleable.CircularProgressIndicator_drawDot, shouldDrawDot);
            dotColor = a.getColor(R.styleable.CircularProgressIndicator_dotColor, progressColor);
            dotWidth = a.getDimensionPixelSize(R.styleable.CircularProgressIndicator_dotWidth, progressStrokeWidth);

            startAngle = a.getInt(R.styleable.CircularProgressIndicator_startAngle, DEFAULT_PROGRESS_START_ANGLE);
            if (startAngle < 0 || startAngle > 360) {
                startAngle = DEFAULT_PROGRESS_START_ANGLE;
            }

            direction = a.getInt(R.styleable.CircularProgressIndicator_direction, DIRECTION_COUNTERCLOCKWISE);

            String formattingPattern = a.getString(R.styleable.CircularProgressIndicator_formattingPattern);
            if (formattingPattern != null) {
                progressTextAdapter = new PatternProgressTextAdapter(formattingPattern);
            } else {
                progressTextAdapter = new DefaultProgressTextAdapter();
            }

            reformatProgressText();

            a.recycle();
        }

        progressPaint = new Paint();
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(progressStrokeWidth);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setColor(progressColor);
        progressPaint.setAntiAlias(true);

        progressBackgroundPaint = new Paint();
        progressBackgroundPaint.setStyle(Paint.Style.STROKE);
        progressBackgroundPaint.setStrokeWidth(progressStrokeWidth);
        progressBackgroundPaint.setColor(progressBackgroundColor);
        progressBackgroundPaint.setAntiAlias(true);

        dotPaint = new Paint();
        dotPaint.setStrokeCap(Paint.Cap.ROUND);
        dotPaint.setStrokeWidth(dotWidth);
        dotPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        dotPaint.setColor(dotColor);
        dotPaint.setAntiAlias(true);


        textPaint = new TextPaint();
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setColor(Color.parseColor("#ffffff"));
        textPaint.setAntiAlias(true);
        circleBounds = new RectF();
    }


    public void SetAttributes(int progressColor, int dotColor, int progressBackgroundColor, int textColor, int textSize) {
        setProgressColor(progressColor);
        setDotColor(dotColor);
        setProgressBackgroundColor(progressBackgroundColor);
        setTextColor(progressColor);
        textPaint = new TextPaint();
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setColor(textColor);

        textPaint.setTextSize(sp2px(textSize));
        setProgressStrokeWidthDp(Constant.AUD_PROGRESS_STROKE_WIDTH);
        setDotWidthDp(Constant.AUD_PROGRESS_STROKE_WIDTH);
    }

    public void SetAttributes1() {

        textPaint = new TextPaint();
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.parseColor("#FFFFFF"));
        textPaint.setTypeface(Typeface.create(Typeface.SERIF,Typeface.NORMAL));
        textPaint.setTextSize(sp2px(25));


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        Rect textBoundsRect = new Rect();
        textPaint.getTextBounds(progressText, 0, progressText.length(), textBoundsRect);

        float strokeSizeOffset = (shouldDrawDot) ? Math.max(dotPaint.getStrokeWidth(), progressPaint.getStrokeWidth()) : progressPaint.getStrokeWidth(); // to prevent progress or dot from drawing over the bounds
        int desiredSize = ((int) strokeSizeOffset) + dp2px(DESIRED_WIDTH_DP) +
                Math.max(paddingBottom + paddingTop, paddingLeft + paddingRight);

        // multiply by .1f to have an extra space for small padding between text and circle
        desiredSize += Math.max(textBoundsRect.width(), textBoundsRect.height()) + desiredSize * .1f;

        int finalWidth;
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                finalWidth = measuredWidth;
                break;
            case MeasureSpec.AT_MOST:
                finalWidth = Math.min(desiredSize, measuredWidth);
                break;
            default:
                finalWidth = desiredSize;
                break;
        }

        int finalHeight;
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                finalHeight = measuredHeight;
                break;
            case MeasureSpec.AT_MOST:
                finalHeight = Math.min(desiredSize, measuredHeight);
                break;
            default:
                finalHeight = desiredSize;
                break;
        }

        int widthWithoutPadding = finalWidth - paddingLeft - paddingRight;
        int heightWithoutPadding = finalHeight - paddingTop - paddingBottom;

        int smallestSide = Math.min(heightWithoutPadding, widthWithoutPadding);
        setMeasuredDimension(smallestSide, smallestSide);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        calculateBounds(w, h);
    }

    private void calculateBounds(int w, int h) {
        radius = w / 2f;

        float strokeSizeOffset = (shouldDrawDot) ? Math.max(dotPaint.getStrokeWidth(), progressPaint.getStrokeWidth()) : progressPaint.getStrokeWidth(); // to prevent progress or dot from drawing over the bounds
        float halfOffset = strokeSizeOffset / 2f;

        circleBounds.left = halfOffset;
        circleBounds.top = halfOffset;
        circleBounds.right = w - halfOffset;
        circleBounds.bottom = h - halfOffset;

        radius = circleBounds.width() / 2f;

        calculateTextBounds();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawProgressBackground(canvas);
        drawProgress(canvas);
        if (shouldDrawDot) drawDot(canvas);
        drawText(canvas);
    }

    private void drawProgressBackground(Canvas canvas) {
        canvas.drawArc(circleBounds, ANGLE_START_PROGRESS_BACKGROUND, ANGLE_END_PROGRESS_BACKGROUND,
                false, progressBackgroundPaint);
    }

    private void drawProgress(Canvas canvas) {
        canvas.drawArc(circleBounds, startAngle, sweepAngle, false, progressPaint);
    }

    private void drawDot(Canvas canvas) {
        double angleRadians = Math.toRadians(startAngle + sweepAngle + 180);
        float cos = (float) Math.cos(angleRadians);
        float sin = (float) Math.sin(angleRadians);
        float x = circleBounds.centerX() - radius * cos;
        float y = circleBounds.centerY() - radius * sin;

        canvas.drawPoint(x, y, dotPaint);
    }

    private void drawText(Canvas canvas) {
        canvas.drawText(progressText, textX, textY, textPaint);
    }

    public void setMaxProgress(double maxProgress) {
        maxProgressValue = maxProgress;
        if (maxProgressValue < progressValue) {
            setCurrentProgress(maxProgress);
        }
        invalidate();
    }

    public void setCurrentProgress(double currentProgress) {
        if (currentProgress > maxProgressValue) {
            maxProgressValue = currentProgress;
        }

        setProgress(currentProgress, maxProgressValue);
    }

    public void setProgress(double current, double max) {
        final double finalAngle;

        if (direction == DIRECTION_COUNTERCLOCKWISE) {
            finalAngle = -(current / max * 360);
        } else {
            finalAngle = current / max * 360;
        }

        final PropertyValuesHolder angleProperty = PropertyValuesHolder.ofInt(PROPERTY_ANGLE, sweepAngle, (int) finalAngle);

        double oldCurrentProgress = progressValue;

        maxProgressValue = max;
        progressValue = Math.min(current, max);

        reformatProgressText();
        calculateTextBounds();

        if (progressAnimator != null) {
            progressAnimator.cancel();
        }

        progressAnimator = ValueAnimator.ofObject(new TypeEvaluator<Double>() {
            @Override
            public Double evaluate(float fraction, Double startValue, Double endValue) {
                return (startValue + (endValue - startValue) * fraction);
            }
        }, oldCurrentProgress, progressValue);
        progressAnimator.setDuration(DEFAULT_ANIMATION_DURATION);
        progressAnimator.setValues(angleProperty);
        progressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sweepAngle = (int) animation.getAnimatedValue(PROPERTY_ANGLE);
                invalidate();
            }
        });
        progressAnimator.addListener(new DefaultAnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
                sweepAngle = (int) finalAngle;

                progressAnimator = null;
            }
        });
        progressAnimator.start();
    }

    private void reformatProgressText() {
        progressText = progressTextAdapter.formatText(progressValue) + "%";
    }

    private Rect calculateTextBounds() {
        Rect textRect = new Rect();
        textPaint.getTextBounds(progressText, 0, progressText.length(), textRect);
        textX = circleBounds.centerX() - textRect.width() / 2f;
        textY = circleBounds.centerY() + textRect.height() / 2f;

        return textRect;
    }

    private int dp2px(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    private int sp2px(float sp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
    }

    // calculates circle bounds, view size and requests invalidation
    private void invalidateEverything() {
        calculateBounds(getWidth(), getHeight());
        requestLayout();
        invalidate();
    }

    public void setProgressColor(@ColorInt int color) {
        progressPaint.setColor(color);
        invalidate();
    }

    public void setProgressBackgroundColor(@ColorInt int color) {
        progressBackgroundPaint.setColor(color);
        invalidate();
    }

    public void setProgressStrokeWidthDp(@Dimension int strokeWidth) {
        setProgressStrokeWidthPx(dp2px(strokeWidth));
    }

    public void setProgressStrokeWidthPx(@Dimension final int strokeWidth) {
        progressPaint.setStrokeWidth(strokeWidth);
        progressBackgroundPaint.setStrokeWidth(strokeWidth);

        invalidateEverything();
    }

    public void setTextColor(@ColorInt int color) {
        textPaint.setColor(color);

        Rect textRect = new Rect();
        textPaint.getTextBounds(progressText, 0, progressText.length(), textRect);

        invalidate(textRect);
    }

    public void setTextSizeSp(@Dimension int size) {
        setTextSizePx(sp2px(size));
    }

    public void setTextSizePx(@Dimension int size) {
        float currentSize = textPaint.getTextSize();

        float factor = textPaint.measureText(progressText) / currentSize;

        float offset = (shouldDrawDot) ? Math.max(dotPaint.getStrokeWidth(), progressPaint.getStrokeWidth()) : progressPaint.getStrokeWidth();
        float maximumAvailableTextWidth = circleBounds.width() - offset;

        if (size * factor >= maximumAvailableTextWidth) {
            size = (int) (maximumAvailableTextWidth / factor);
        }

        textPaint.setTextSize(size);

        Rect textBounds = calculateTextBounds();
        invalidate(textBounds);
    }

    public void setShouldDrawDot(boolean shouldDrawDot) {
        this.shouldDrawDot = shouldDrawDot;

        if (dotPaint.getStrokeWidth() > progressPaint.getStrokeWidth()) {
            requestLayout();
            return;
        }

        invalidate();
    }

    public void setDotColor(@ColorInt int color) {
        dotPaint.setColor(color);

        invalidate();
    }

    public void setDotWidthDp(@Dimension int width) {
        setDotWidthPx(dp2px(width));
    }

    public void setDotWidthPx(@Dimension final int width) {
        dotPaint.setStrokeWidth(width);

        invalidateEverything();
    }

    public void setProgressTextAdapter(@Nullable ProgressTextAdapter progressTextAdapter) {

        if (progressTextAdapter != null) {
            this.progressTextAdapter = progressTextAdapter;
        } else {
            this.progressTextAdapter = new DefaultProgressTextAdapter();
        }

        reformatProgressText();

        invalidateEverything();
    }

    @NonNull
    public ProgressTextAdapter getProgressTextAdapter() {
        return progressTextAdapter;
    }

    @ColorInt
    public int getProgressColor() {
        return progressPaint.getColor();
    }

    @ColorInt
    public int getProgressBackgroundColor() {
        return progressBackgroundPaint.getColor();
    }

    public float getProgressStrokeWidth() {
        return progressPaint.getStrokeWidth();
    }


    @ColorInt
    public int getTextColor() {
        return textPaint.getColor();
    }

    public float getTextSize() {
        return textPaint.getTextSize();
    }


    public boolean isDotEnabled() {
        return shouldDrawDot;
    }

    @ColorInt
    public int getDotColor() {
        return dotPaint.getColor();
    }

    public float getDotWidth() {
        return dotPaint.getStrokeWidth();
    }


    public double getProgress() {
        return progressValue;
    }

    public double getMaxProgress() {
        return maxProgressValue;
    }

    public int getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(@IntRange(from = 0, to = 360) int startAngle) {
        this.startAngle = startAngle;
        invalidate();
    }

    @Direction
    public int getDirection() {
        return direction;
    }

    public void setDirection(@Direction int direction) {
        this.direction = direction;
        invalidate();
    }

    @IntDef({DIRECTION_CLOCKWISE, DIRECTION_COUNTERCLOCKWISE})
    private @interface Direction {
    }


    public interface ProgressTextAdapter {

        @NonNull
        String formatText(double currentProgress);
    }

    public final class PatternProgressTextAdapter implements CircularProgressIndicator2.ProgressTextAdapter {

        private String pattern;

        public PatternProgressTextAdapter(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public String formatText(double currentProgress) {
            return String.format(pattern, currentProgress);
        }
    }

    public final class DefaultProgressTextAdapter implements CircularProgressIndicator2.ProgressTextAdapter {

        @Override
        public String formatText(double currentProgress) {
            return String.valueOf((int) currentProgress);
        }
    }

    class DefaultAnimatorListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation, boolean isReverse) {

        }

        @Override
        public void onAnimationEnd(Animator animation, boolean isReverse) {

        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
