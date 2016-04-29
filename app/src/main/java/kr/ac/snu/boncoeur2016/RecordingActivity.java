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
    String name, position;
    int age, id;
    TextView patient_info, next_btn;
    nayoso.staticprogressbar.CustomProgress record_btn, listen_btn;
    Handler handler;
    private WaveFormView waveformView;
    private SpectrumView spectrumView;
    private RecordingThread recordingThread;

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
        listen_btn = (nayoso.staticprogressbar.CustomProgress) findViewById(R.id.listen_btn);
        listen_btn.setOnClickListener(this);
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

        recordingThread.startAcquisition(position);
//        actionBar.setHomeButtonEnabled(true);
    }

    private void idCheck(int id) {
        if(id == Define.REVISION){
            this.id = Dao.getRecentId();
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

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    recordingThread.stopSave();
                    record_btn.setText("RECORD AGAIN");
                    record_btn.setEnabled(true);
                    record_btn.setFocusable(true);
                    next_btn.setVisibility(View.VISIBLE);
                    listen_btn.setVisibility(View.VISIBLE);
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
        intent.putExtra("id", id);
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


                    }
                }.init().run();

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("test", "pause");
        recordingThread.stopAcquisition(Define.PAUSE);
        goToPosition();
        Log.d("test", "pause 종료");
    }

    @Override
    protected void onPause() {
        super.onPause();
        recordingThread.stopAcquisition(id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recordingThread.startAcquisition(position);
        getPatientInfo();
    }

    private void getPatientInfo() {
        RecordItem record = Dao.getRcordById(id);
//        RecordItem record = dao.getRcordById(dao.getRecentId());
        name = record.getName();
        age = record.getAge();
        Log.d("test", "NAME" + name + "," + age);
        patient_info.setText("name: " + name + " (" + age + ") position: "+position);
    }
}