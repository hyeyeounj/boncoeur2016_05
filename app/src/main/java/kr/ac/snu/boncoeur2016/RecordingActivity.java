package kr.ac.snu.boncoeur2016;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import kr.ac.snu.boncoeur2016.utils.Define;


/**
 * Created by hyes on 2016. 3. 23..
 */
public class RecordingActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    Context context;
    String name, position;
    int age, id;
    TextView patient_info, next_btn;
    nayoso.staticprogressbar.CustomProgress record_btn, listen_btn;
    private WaveFormView waveformView;
    private SpectrumView spectrumView;
    private RecordingThread recordingThread;
    private Handler handler = null;
    private Runnable recordStopThread = null;
    private Runnable playStopThread = null;
    private AudioDataReceivedListener adrListener = null;
    private AudioTrack at = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        context = this;

        Intent intent = getIntent();
        position = intent.getStringExtra("position");
        idCheck(intent.getIntExtra("id", 0));

        patient_info = (TextView) findViewById(R.id.patient_info);
        record_btn = (nayoso.staticprogressbar.CustomProgress) findViewById(R.id.record_btn);
        record_btn.setOnClickListener(this);
        record_btn.setOnLongClickListener(this);
        listen_btn = (nayoso.staticprogressbar.CustomProgress) findViewById(R.id.listen_btn);
        listen_btn.setOnClickListener(this);
        listen_btn.setOnLongClickListener(this);
        next_btn = (TextView) findViewById(R.id.next_btn);
        next_btn.setOnClickListener(this);
        waveformView = (WaveFormView) findViewById(R.id.waveformView);
        spectrumView = (SpectrumView) findViewById(R.id.spectrumView);

        adrListener = new AudioDataReceivedListener() {
            @Override
            public void onAudioDataReceived(short[] data, int offset, int size) {
                waveformView.setSamples(data, offset, size);
                spectrumView.setSamples(data, offset, size);
            }
        };
        recordingThread = new RecordingThread(context, adrListener, this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recordingThread.startAcquisition(id, position);
//        actionBar.setHomeButtonEnabled(true);
    }

    private void idCheck(int id) {
        if(id == Define.REVISION){
            Dao dao = new Dao(this);
            this.id = dao.getRecentId();
            Log.d("test", "수정시 id가 : "+ id);
        }else{
            this.id = id;
            Log.d("test", "새로운 id가 : "+ id);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startAudioRecordingSafe() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            recordingThread.startSave();

            recordStopThread = new Runnable() {
                @Override
                public void run() {
                    if (recordingThread.isRecording()) {
                        recordingThread.stopSave();
                        record_btn.setText("RECORD AGAIN");
                        record_btn.setEnabled(true);
                        record_btn.setFocusable(true);
                        next_btn.setVisibility(View.VISIBLE);
                        listen_btn.setVisibility(View.VISIBLE);
                        Log.d("test", "record stop");
                    } else
                        Log.d("test", "record already stopped");
//                    goToPosition();
                }
            };
            if (handler == null)
                handler = new Handler();
            handler.postDelayed(recordStopThread, Define.SHORT_TIME);

        } else {
            Log.d("test", "qweqweq");
            //requestMicrophonePermission();
        }
    }

    private void goToPosition() {
        Intent intent = new Intent(getApplicationContext(), PositioningActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {

        switch (v.getId()) {
            case R.id.record_btn:
                if (recordingThread.isRecording()) {

                    recordingThread.stopSave();

                    String filePath = new Dao(context).getRecordById(id).getRecordFile(position);
                    if (filePath != null && !filePath.equals((""))) {
                        File f = new File(filePath);
                        if (f.exists())
                            f.delete();
                    }
                    record_btn.setText("RECORD");
                    record_btn.setFocusable(true);
                    next_btn.setVisibility(View.GONE);
                    listen_btn.setVisibility(View.GONE);
                    handler.removeCallbacks(recordStopThread);
                    Log.d("test", "record stop");
                    return true;
                }
                break;
            case R.id.listen_btn:

                Log.d("RecordingActivity", "Stop Play");
        }
        return false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.record_btn:

                if (!recordingThread.isRecording()) {

                    String filePath = new Dao(context).getRecordById(id).getRecordFile(position);
                    if (filePath != null && !filePath.equals((""))) {
                        File f = new File(filePath);
                        if (f.exists())
                            f.delete();
                    }
                    startAudioRecordingSafe();
                    record_btn.setText("Long-press to Stop");
                    record_btn.setFocusable(false);
                    next_btn.setVisibility(View.GONE);
                    listen_btn.setVisibility(View.GONE);
                    Log.d("test", "record start");
                } else {
//                    recordingThread.stopAcquisition();
//                    record_btn.setText("RECORD");
//                    Log.d("test", "record stop");
                }
                break;
            case R.id.next_btn:
                goToPosition();
                break;
            case R.id.listen_btn:

                new Thread() {

                    public Thread init() {

                        return this;
                    }

                    @Override
                    public void run() {

//                        listen_btn.setCurrentPercentage(50000.0 * (totalBytesRead + numberOfShort) / (SAMPLE_RATE * Define.SHORT_TIME));
                        listen_btn.setCurrentPercentage(50);

                        recordingThread.stopAcquisition();

                        listen_btn.setText("PLAYING RECORDING...");
                        record_btn.setVisibility(View.GONE);
                        next_btn.setVisibility(View.GONE);

                        try {
                            MediaExtractor me = new MediaExtractor();
                            me.setDataSource(new Dao(context).getRecordById(id).getRecordFile(position));
                            me.selectTrack(0);

                            MediaFormat inputFormat = me.getTrackFormat(0);

                            MediaCodec codec = MediaCodec.createDecoderByType(Define.COMPRESSED_AUDIO_FILE_MIME_TYPE);
                            codec.configure(inputFormat, null, null, 0);
                            codec.start();

                            ByteBuffer[] codecInputBuffers = codec.getInputBuffers(); // Note: Array of buffers
                            ByteBuffer[] codecOutputBuffers = codec.getOutputBuffers();

                            MediaCodec.BufferInfo outBuffInfo = new MediaCodec.BufferInfo();

                            ByteBuffer buf = ByteBuffer.allocate(Define.COMPRESSED_AUDIO_SAMPLE_RATE * 2 * (Define.SHORT_TIME / 1000 + 1));
                            int bytesRead = 0;
                            while (true) {

                                int len = me.readSampleData(buf, bytesRead);
                                if (len == -1)
                                    break;
                                bytesRead += len;
                                me.advance();
                            }

                            Log.i("RecordingActivity", "File read finished.");
                            int processed = 0, total = bytesRead, size, totalBytesRead = 0;
                            long presentationTimeUs = 0;
                            boolean finished = false;

                            byte[] bData = new byte[buf.limit()];
                            buf.position(0);
                            buf.get(bData);
                            buf.clear();

                            Log.i("RecordingActivity", "Start Decoding.");
                            while (!finished) {

                                int ind = codec.dequeueInputBuffer(0);
                                Log.i("RecordingActivity", "Start while : " + ind);
                                while (ind >= 0 && processed != total) {

                                    Log.i("RecordingActivity", "Decoding Buffer enqueue");
                                    ByteBuffer dstBuf = codecInputBuffers[ind];
                                    dstBuf.clear();
                                    if (dstBuf.limit() > total - processed) {
                                        dstBuf.put(bData, processed, total - processed);
                                        size = total - processed;
                                    } else {

                                        dstBuf.put(bData, processed, dstBuf.limit());
                                        size = dstBuf.limit();
                                    }
                                    processed += size;
                                    if (processed == total)
                                        codec.queueInputBuffer(ind, 0, size, (long) presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                    else
                                        codec.queueInputBuffer(ind, 0, size, (long) presentationTimeUs, 0);
                                    Log.i("RecordingThread", "totalBytesRead : " + totalBytesRead);
                                    presentationTimeUs = 1000000L * (totalBytesRead / 2) / Define.SAMPLE_RATE;
                                    totalBytesRead += size;

                                    if (processed != total)
                                        ind = codec.dequeueInputBuffer(0);
                                }

                                ind = codec.dequeueOutputBuffer(outBuffInfo, 0);
                                Log.i("RecordingActivity", "Middle While : " + ind);

                                MediaFormat outputFormat = codec.getOutputFormat();

                                if (ind < 0) {
                                    if (ind == MediaCodec.INFO_TRY_AGAIN_LATER) {
                                        this.sleep(10);
                                    } else if (ind == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED)
                                        outputFormat = codec.getOutputFormat();
                                    else if (ind == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED)
                                        codecOutputBuffers = codec.getOutputBuffers();
                                    ind = codec.dequeueOutputBuffer(outBuffInfo, 0);
                                }

                                while (ind >= 0) {

                                    Log.i("RecordingActivity", "Decoding Buffer dequeue");
                                    try {
                                        ByteBuffer encodedData = codecOutputBuffers[ind];
                                        byte[] b = new byte[encodedData.limit()];
                                        encodedData.get(b);
                                        buf.put(b);

                                        codec.releaseOutputBuffer(ind, false);
                                        ind = codec.dequeueOutputBuffer(outBuffInfo, 0);
                                        if (outBuffInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                                            finished = true;
                                            break;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            me.release();
                            codec.stop();
                            codec.release();
                            Log.i("RecordingActivity", "Decoding finished.");
                            buf.limit(buf.position());
                            buf.position(0);
                            buf.order(ByteOrder.LITTLE_ENDIAN);
                            short[] waveform = new short[buf.limit() / 2];
                            for (int i = 0; i < buf.limit() / 2; i++)
                                waveform[i] = buf.getShort();

                            Log.i("RecordingActivity", "waveform.length : " + waveform.length);
                            try {
                                at = new AudioTrack(AudioManager.STREAM_MUSIC, Define.SAMPLE_RATE,
                                        AudioFormat.CHANNEL_OUT_MONO,
                                        AudioFormat.ENCODING_PCM_16BIT, waveform.length * 2,
                                        AudioTrack.MODE_STREAM);

                                at.setStereoVolume(1.0f, 1.0f);
                                at.write(waveform, 0, waveform.length);
                                at.write(waveform, 0, waveform.length);
                                at.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {

                                    private short[] waveform;
                                    private int lastPos = 0;

                                    public AudioTrack.OnPlaybackPositionUpdateListener init(short[] waveform) {

                                        this.waveform = waveform;
                                        return this;
                                    }

                                    @Override
                                    public void onMarkerReached(AudioTrack track) {

                                    }

                                    @Override
                                    public void onPeriodicNotification(AudioTrack track) {

                                        Log.i("RecordingActivity", "Position : " + track.getPlaybackHeadPosition());
                                        if (track.getPlaybackHeadPosition() > waveform.length)
                                            return;
                                        adrListener.onAudioDataReceived(waveform, lastPos, track.getPlaybackHeadPosition() - lastPos);
                                        listen_btn.setCurrentPercentage(100.0 * track.getPlaybackHeadPosition() / waveform.length);
                                        lastPos = track.getPlaybackHeadPosition();
                                    }
                                }.init(waveform));
                                at.setPositionNotificationPeriod(Define.SAMPLE_RATE / 30);
                                at.play();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.i("RecordingActivity", "AudioTrack started.");

                            playStopThread = new Runnable() {

                                @Override
                                public void run() {

                                    at.stop();
                                    at.release();
                                    recordingThread.startAcquisition(id, position);
                                    listen_btn.setCurrentPercentage(0);
                                    listen_btn.setText("LISTEN");
                                    record_btn.setVisibility(View.VISIBLE);
                                    next_btn.setVisibility(View.VISIBLE);
                                }
                            };
                            if (handler == null)
                                handler = new Handler();
                            handler.postDelayed(playStopThread, Define.SHORT_TIME);
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.init().run();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("test", "pause");
        recordingThread.stopAcquisition();
        goToPosition();
        Log.d("test", "pause 종료");
    }

    @Override
    protected void onPause() {
        super.onPause();
        recordingThread.stopAcquisition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        recordingThread.startAcquisition(id, position);
        getPatientInfo();
    }

    private void getPatientInfo() {
        Dao dao = new Dao(this);
        RecordItem record = dao.getRecordById(id);
//        RecordItem record = dao.getRecordById(dao.getRecentId());
        name = record.getName();
        age = record.getAge();
        Log.d("test", "NAME" + name + "," + age);
        patient_info.setText("name: " + name + " (" + age + ") position: "+position);
    }
}