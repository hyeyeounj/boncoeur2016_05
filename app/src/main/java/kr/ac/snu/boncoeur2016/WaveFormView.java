package kr.ac.snu.boncoeur2016;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;
/**
 * Created by hyes on 2016. 3. 25..
 */
public class WaveFormView extends View {

    private static final int nPlots = 3;

    private TextPaint mTextPaint;
    private Paint mStrokePaint, mFillPaint, mMarkerPaint;

    // Used in draw
    private int plotType = 0;
    private int plotMethod = 1;
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

        mSamples = new short[8000 * nPlots * 1 / 1];
    }

    public void setPlotType(int type) {

        this.plotType = type;
    }

    public void setPlotMethod(int method) {

        this.plotMethod = method;
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
            borders[i * 4 + 0] = 0;
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

    public void setSamples(short[] samples) {
        if (mSamplesLastPos + samples.length <= mSamples.length) {
            System.arraycopy(samples, 0, mSamples, mSamplesLastPos, samples.length);
            mSamplesLastPos = mSamplesLastPos + samples.length;
        } else {
            System.arraycopy(samples, 0, mSamples, mSamplesLastPos, mSamples.length - mSamplesLastPos);
            System.arraycopy(samples, mSamples.length - mSamplesLastPos, mSamples, 0, samples.length - (mSamples.length - mSamplesLastPos));
            mSamplesLastPos = samples.length - (mSamples.length - mSamplesLastPos);
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
        float max = Short.MAX_VALUE;

        // For efficiency, we don't draw all of the samples in the buffer, but only the ones
        // that align with pixel boundaries.
        for (int x = 0; x < width * nPlots; x++) {
            int index1 = 0;
            if (x != 0)
                index1 = (int) (((x * 1.0f) / (width * nPlots)) * buffer.length);
            int index2 = (int) (((x + 1 * 1.0f) / (width * nPlots)) * buffer.length);
            short[] samples;
            if (index1 < index2)
                samples = Arrays.copyOfRange(buffer, index1, index2);
            else {

                if (index1 == 0)
                    samples = Arrays.copyOfRange(buffer, index1, index2 + 1);
                else
                    samples = Arrays.copyOfRange(buffer, index1 - 1, index2);
            }
            int maxS = Short.MIN_VALUE, minS = Short.MAX_VALUE, minpos = 0, maxpos = 0;
            for (int i = 0; i < samples.length; i++) {
                if (maxS < samples[i]) {
                    maxS = samples[i];
                    maxpos = i;
                }
                if (minS > samples[i]) {
                    minS = samples[i];
                    minpos = i;
                }
            }
            float y = 0;
            if (plotType == 0) {
                if (Math.abs(maxS) > Math.abs(minS))
                    maxS = Math.abs(maxS);
                else
                    maxS = Math.abs(minS);
                y = centerY - ((maxS / max) * centerY);
            } else if (plotType == 1) {
                if (minS >= 0 && maxS >= 0 || maxS >= 0 && maxS >= -minS)
                    y = centerY - ((maxS / max) * centerY);
                else
                    y = centerY - ((minS / max) * centerY);
            }

            y = y / nPlots + centerY / nPlots * (x / width) * 2;

            if (lastX != -1) {
                if (index1 <= mSamplesLastPos && mSamplesLastPos <= index2) {

                    waveformPoints[pointIndex++] = x % width;
                    waveformPoints[pointIndex++] = centerY / nPlots * (x / width) * 2;
                    waveformPoints[pointIndex++] = x % width;
                    waveformPoints[pointIndex++] = centerY * 2 / nPlots + centerY / nPlots * (x / width) * 2;
                } else {

                    if (x % width != 0) {

                        if (plotMethod == 0) {

                            waveformPoints[pointIndex++] = lastX;
                            waveformPoints[pointIndex++] = lastY;
                            waveformPoints[pointIndex++] = x % width;
                            waveformPoints[pointIndex++] = y;
                        } else if (plotMethod == 1) {

                            if (minpos < maxpos) {

                                waveformPoints[pointIndex++] = lastX;
                                waveformPoints[pointIndex++] = (centerY - ((minS / max) * centerY)) / nPlots + centerY / nPlots * (x / width) * 2;
                                waveformPoints[pointIndex++] = x % width;
                                waveformPoints[pointIndex++] = (centerY - ((maxS / max) * centerY)) / nPlots + centerY / nPlots * (x / width) * 2;
                            } else {

                                waveformPoints[pointIndex++] = lastX;
                                waveformPoints[pointIndex++] = (centerY - ((maxS / max) * centerY)) / nPlots + centerY / nPlots * (x / width) * 2;
                                waveformPoints[pointIndex++] = x % width;
                                waveformPoints[pointIndex++] = (centerY - ((minS / max) * centerY)) / nPlots + centerY / nPlots * (x / width) * 2;
                            }
                        }
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
