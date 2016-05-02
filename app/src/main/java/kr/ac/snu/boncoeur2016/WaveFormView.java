package kr.ac.snu.boncoeur2016;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
/**
 * Created by hyes on 2016. 3. 25..
 */
public class WaveFormView extends View {

    private static final int nPlots = 6;

    private TextPaint mTextPaint;
    private Paint mStrokePaint, mFillPaint, mMarkerPaint;

    // Used in draw
    private int width, height;
    private float centerY;
    private short[] mSamples;
    private int mSamplesLastPos = 0;
    private float[] plot = null;
    private float[] borders = null;

    public WaveFormView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public WaveFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public WaveFormView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.WaveformView, defStyle, 0);

        float strokeThickness = a.getFloat(R.styleable.WaveformView_waveformStrokeThickness, 2f);
        int mStrokeColor = a.getColor(R.styleable.WaveformView_waveformColor,
                ContextCompat.getColor(context, R.color.default_waveform));

        a.recycle();

        mStrokePaint = new Paint();
        mStrokePaint.setColor(mStrokeColor);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(strokeThickness);
        mStrokePaint.setAntiAlias(true);

        mSamples = new short[4000 * nPlots];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = getMeasuredWidth();
        height = getMeasuredHeight();
        centerY = height / 2f;

        plot = new float[width * 4 * nPlots];
        borders = new float[4 * (nPlots + 1)];
        for (int i = 0; i <= nPlots; i++) {
            borders[i * 4] = 0;
            borders[i * 4 + 1] = height / nPlots * i;
            borders[i * 4 + 2] = width;
            borders[i * 4 + 3] = height / nPlots * i;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if (plot != null) {
            canvas.drawLines(plot, mStrokePaint);
            canvas.drawLines(borders, mStrokePaint);
        }
    }

    public void setSamples(short[] samples, int offset, int size) {

        if (size <= 0) {
            Log.i("WaveFormView", "Recording Error : " + size);
            return;
        }

        if (mSamplesLastPos + size <= mSamples.length) {
            System.arraycopy(samples, offset, mSamples, mSamplesLastPos, size);
            mSamplesLastPos = mSamplesLastPos + size;
        } else {
            System.arraycopy(samples, offset, mSamples, mSamplesLastPos, mSamples.length - mSamplesLastPos);
            System.arraycopy(samples, offset + mSamples.length - mSamplesLastPos, mSamples, 0, size - (mSamples.length - mSamplesLastPos));
            mSamplesLastPos = size - (mSamples.length - mSamplesLastPos);
        }
        onSamplesChanged();
    }

    private void onSamplesChanged() {

        drawRecordingWaveform(mSamples, plot);
        postInvalidate();
    }

    void drawRecordingWaveform(short[] buffer, float[] waveformPoints) {
        float lastX = -1;
        float lastY = -1;
        int pointIndex = 0;
        Short max = Short.MAX_VALUE;

        // For efficiency, we don't draw all of the samples in the buffer, but only the ones
        // that align with pixel boundaries.
        for (int x = 0; x < width * nPlots; x++) {
            int index1 = 0;
            if (x != 0)
                index1 = (int) (((x * 1.0f) / (width * nPlots)) * buffer.length);
            int index2 = (int) (((x + 1 * 1.0f) / (width * nPlots)) * buffer.length);
            if (index1 >= index2) {

                if (index1 == 0)
                    index2++;
                else
                    index1--;
            }
            int maxS = Short.MIN_VALUE, minS = Short.MAX_VALUE;
            for (int i = index1; i < index2; i++) {
                if (maxS < buffer[i])
                    maxS = buffer[i];
                if (minS > buffer[i])
                    minS = buffer[i];
            }
            float y;

            if (minS >= 0 && maxS >= 0 || maxS >= 0 && maxS >= -minS) {
                if (maxS > Short.MAX_VALUE / 2)
                    maxS = Short.MAX_VALUE / 2;
                maxS *= 2;
                y = centerY - maxS * centerY / max;
            } else {
                if (minS < Short.MIN_VALUE / 2)
                    minS = Short.MIN_VALUE / 2;
                minS *= 2;
                y = centerY - minS * centerY / max;
            }
//            }

            y = y / nPlots + centerY / nPlots * (x / width) * 2;

            if (lastX != -1) {
                if (index1 <= mSamplesLastPos && mSamplesLastPos <= index2) {

                    waveformPoints[pointIndex++] = x % width;
                    waveformPoints[pointIndex++] = centerY / nPlots * (x / width) * 2;
                    waveformPoints[pointIndex++] = x % width;
                    waveformPoints[pointIndex++] = centerY * 2 / nPlots + centerY / nPlots * (x / width) * 2;
                } else {

                    if (x % width != 0) {

//                        if (plotMethod == 0) {

                            waveformPoints[pointIndex++] = lastX;
                            waveformPoints[pointIndex++] = lastY;
                            waveformPoints[pointIndex++] = x % width;
                            waveformPoints[pointIndex++] = y;
//                        } else if (plotMethod == 1) {
//
//                            if (minpos < maxpos) {
//
//                                waveformPoints[pointIndex++] = lastX;
//                                waveformPoints[pointIndex++] = (centerY - ((minS / max) * centerY)) / nPlots + centerY / nPlots * (x / width) * 2;
//                                waveformPoints[pointIndex++] = x % width;
//                                waveformPoints[pointIndex++] = (centerY - ((maxS / max) * centerY)) / nPlots + centerY / nPlots * (x / width) * 2;
//                            } else {
//
//                                waveformPoints[pointIndex++] = lastX;
//                                waveformPoints[pointIndex++] = (centerY - ((maxS / max) * centerY)) / nPlots + centerY / nPlots * (x / width) * 2;
//                                waveformPoints[pointIndex++] = x % width;
//                                waveformPoints[pointIndex++] = (centerY - ((minS / max) * centerY)) / nPlots + centerY / nPlots * (x / width) * 2;
//                            }
//                        }
                    } else {
                        waveformPoints[pointIndex++] = x % width;
                        waveformPoints[pointIndex++] = y;
                        waveformPoints[pointIndex++] = x % width;
                        waveformPoints[pointIndex++] = y;
                    }
                }
            }

            lastX = x % width;
            lastY = y;
        }
    }
}
