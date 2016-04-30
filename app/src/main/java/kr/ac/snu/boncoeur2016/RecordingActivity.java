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

import java.io.File;

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
    private Runnable stopRecordThread = null;

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

        recordingThread = new RecordingThread(context, new AudioDataReceivedListener() {
            @Override
            public void onAudioDataReceived(short[] data, int offset, int size) {
                waveformView.setSamples(data, offset, size);
                spectrumView.setSamples(data, offset, size);
            }
        }, this);


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

            stopRecordThread = new Runnable() {
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
            handler.postDelayed(stopRecordThread, Define.SHORT_TIME);

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
                    handler.removeCallbacks(stopRecordThread);
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

                        listen_btn.setText("PLAYING RECORDING...");
                        record_btn.setVisibility(View.GONE);
                        next_btn.setVisibility(View.GONE);
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {

                                listen_btn.setCurrentPercentage(0);
                                listen_btn.setText("LISTEN");
                                record_btn.setVisibility(View.VISIBLE);
                                next_btn.setVisibility(View.VISIBLE);
                            }
                        };
                        handler = new Handler();
                        handler.postDelayed(runnable, Define.SHORT_TIME);
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