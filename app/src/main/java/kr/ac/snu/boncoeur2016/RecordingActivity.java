package kr.ac.snu.boncoeur2016;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


/**
 * Created by hyes on 2016. 3. 23..
 */
public class RecordingActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    String name;
    int age;
    TextView record_btn, patient_info;
    private WaveFormView waveformView, waveformView2;
    private RecordingThread recordingThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        context = this;

        patient_info = (TextView)findViewById(R.id.patient_info);

        record_btn = (TextView)findViewById(R.id.record_btn);
        record_btn.setOnClickListener(this);
        waveformView = (WaveFormView)findViewById(R.id.waveformView);
        waveformView2 = (WaveFormView) findViewById(R.id.waveformView2);
        waveformView.setPlotType(0);
        waveformView2.setPlotType(1);
        waveformView.setPlotMethod(1);
        waveformView2.setPlotMethod(0);

        recordingThread =new RecordingThread(context, new AudioDataReceivedListener() {
            @Override
            public void onAudioDataReceived(short[] data) {
//                Log.i( "RecordingActivity", "data.length : " + data.length );
                waveformView.setSamples(data);
                waveformView2.setSamples(data);
            }
        });

        Intent intent = getIntent();
        name =  intent.getStringExtra("name");
        age = intent.getIntExtra("age", 0);
        patient_info.setText(name + " , " + age);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeButtonEnabled(true);
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
            recordingThread.startRecording();
        } else {
            Log.d("test", "qweqweq");
            //requestMicrophonePermission();
        }
    }
    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.record_btn:

                if (!recordingThread.recording()) {
                    startAudioRecordingSafe();
                    Log.d("test", "record start");
                } else {
                    recordingThread.stopRecording();
                    Log.d("test", "record stop");
                }
                break;
        }

    }
}

//    private static final String LOG_TAG = "AudioRecordTest";
//    private static String mFileName = null;
//
//    private RecordButton mRecordButton = null;
//    private MediaRecorder mRecorder = null;
//
//    private PlayButton   mPlayButton = null;
//    private MediaPlayer mPlayer = null;
//
//    protected void onRecord(boolean start) {
//        if (start) {
//            startRecording();
//        } else {
//            stopRecording();
//        }
//    }
//
//    protected void onPlay(boolean start) {
//        if (start) {
//            startPlaying();
//        } else {
//            stopPlaying();
//        }
//    }
//
//    private void startPlaying() {
//        mPlayer = new MediaPlayer();
//        try {
//            mPlayer.setDataSource(mFileName);
//            mPlayer.prepare();
//            mPlayer.start();
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
//        }
//    }
//
//    private void stopPlaying() {
//        mPlayer.release();
//        mPlayer = null;
//    }
//
//    private void startRecording() {
//        mRecorder = new MediaRecorder();
//        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        mRecorder.setOutputFile(mFileName);
//        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//
//        try {
//            mRecorder.prepare();
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
//        }
//
//        mRecorder.start();
//        Log.d("test", "MaxAmplitude: " + mRecorder.getMaxAmplitude());
//    }
//
//    private void stopRecording() {
//        mRecorder.stop();
//        mRecorder.release();
//        mRecorder = null;
//    }
//
//    public RecordingActivity() {
//        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
//        mFileName += "/audiorecordtest.3gp";
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//       // setContentView(R.layout.activity_recording);
//
//        LinearLayout ll = new LinearLayout(this);
//        mRecordButton = new RecordButton(this);
//        ll.addView(mRecordButton,
//                new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        0));
//        mPlayButton = new PlayButton(this);
//        ll.addView(mPlayButton,
//                new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        0));
//        setContentView(ll);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (mRecorder != null) {
//            mRecorder.release();
//            mRecorder = null;
//        }
//
//        if (mPlayer != null) {
//            mPlayer.release();
//            mPlayer = null;
//        }
//    }
//
//}
