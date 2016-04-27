package kr.ac.snu.boncoeur2016;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import kr.ac.snu.boncoeur2016.utils.Define;


/**
 * Created by hyes on 2016. 3. 23..
 */
public class RecordingActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    String name;
    int age;
    TextView record_btn, patient_info;
    Handler handler;
    private WaveFormView waveformView, waveformView2;
    private RecordingThread recordingThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        context = this;

        patient_info = (TextView) findViewById(R.id.patient_info);

        record_btn = (TextView) findViewById(R.id.record_btn);
        record_btn.setOnClickListener(this);
        waveformView = (WaveFormView) findViewById(R.id.waveformView);
        waveformView2 = (WaveFormView) findViewById(R.id.waveformView2);
        waveformView.setPlotType(0);
        waveformView2.setPlotType(1);
        waveformView.setPlotMethod(1);
        waveformView2.setPlotMethod(0);

        recordingThread = new RecordingThread(context, new AudioDataReceivedListener() {
            @Override
            public void onAudioDataReceived(short[] data) {
//                Log.i( "RecordingActivity", "data.length : " + data.length );
                waveformView.setSamples(data);
                waveformView2.setSamples(data);
            }
        });

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        age = intent.getIntExtra("age", 0);
        patient_info.setText(name + " , " + age);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recordingThread.startAcquisition();
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
            recordingThread.startSave();

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    recordingThread.stopSave();
                    record_btn.setText("RECORD");
                    record_btn.setEnabled(true);
                    record_btn.setFocusable(true);
                    Log.d("test", "record stop");
//                    goToPosition();
                }
            };
            handler = new Handler();
            handler.postDelayed(runnable, Define.SHORT_TIME);

        } else {
            Log.d("test", "qweqweq");
            //requestMicrophonePermission();
        }
    }

    private void goToPosition() {
        Intent intent = new Intent(getApplicationContext(), PositioningActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.record_btn:

                if (!recordingThread.isRecording()) {
                    startAudioRecordingSafe();
                    record_btn.setText("RECORDING");
                    record_btn.setEnabled(false);
                    record_btn.setFocusable(false);
                    Log.d("test", "record start");
                } else {
//                    recordingThread.stopAcquisition();
//                    record_btn.setText("RECORD");
//                    Log.d("test", "record stop");
                }
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("test", "pause");
        recordingThread.stopAcquisition();
        Log.d("test", "pause 종료");
    }

    @Override
    protected void onPause() {

        recordingThread.stopAcquisition();
        super.onPause();
    }

    @Override
    protected void onResume() {

        recordingThread.startAcquisition();
        super.onResume();
    }
}