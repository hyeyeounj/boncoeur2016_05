package kr.ac.snu.boncoeur2016;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.IOException;

import kr.ac.snu.boncoeur2016.utils.CustomDragShadowBuilder;
import kr.ac.snu.boncoeur2016.utils.Define;
import kr.ac.snu.boncoeur2016.utils.NetworkUtil;
import kr.ac.snu.boncoeur2016.utils.ProxyUp;

/**
 * Created by hyes on 2016. 3. 17..
 */
public class PositioningActivity extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener, View.OnTouchListener {

    ImageView t, p, a, m, record, play;
    Point offset = new Point(0, 0);
    RelativeLayout back;
    RelativeLayout container, play_container;
    TextView pos_m, pos_p, pos_a, pos_t, patient_name, save;
    Dao dao;
    boolean playP, playT, playA, playM;
    Context context;
    String position;
    RecordItem recordItem;
    MediaPlayer player;
    ProgressDialog progressDialog;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);
        context = this;

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        Log.d("test", "onCreate() id: " + id);

        back = (RelativeLayout)findViewById(R.id.back);
        patient_name = (TextView)findViewById(R.id.patient_info);
        save = (TextView)findViewById(R.id.save_data_btn);
        save.setOnClickListener(this);

        dao = new Dao(this);
        t = (ImageView)findViewById(R.id.pos_t);
        p = (ImageView)findViewById(R.id.pos_p);
        a = (ImageView)findViewById(R.id.pos_a);
        m = (ImageView)findViewById(R.id.pos_m);

        ImageView[] pos = {t, p, a, m};
        double[][] rect = {{0.2, 0.2}, {0.3, 0.3}, {0.4, 0.4}, {0.5, 0.5}};

        back.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            ImageView[] pos;
            double[][] rect;

            public View.OnLayoutChangeListener init(ImageView[] pos, double[][] rect) {

                this.pos = pos;
                this.rect = rect;
                return this;
            }

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                int width = right - left;
                int height = bottom - top;

                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(getResources(), R.drawable.body, o);
                int imageWidth = o.outWidth;
                int imageHeight = o.outHeight;
                double factor;
                if (1.0 * width / imageWidth > 1.0 * height / imageHeight) {
                    factor = (1.0 * height / imageHeight);
                    imageWidth *= factor;
                    imageHeight *= factor;
                } else {
                    factor = (1.0 * width / imageWidth);
                    imageWidth *= factor;
                    imageHeight *= factor;
                }

                int offsetWidth = (width - imageWidth) / 2;
                int offsetHeight = (height - imageHeight) / 2;

                for (int i = 0; i < pos.length; i++) {

                    Log.i("PositioningActivity", "button size : " + getResources().getDimension(R.dimen.position_button_size));
                    pos[i].setLeft((int) (rect[i][0] * imageWidth - getResources().getDimension(R.dimen.position_button_size) / 2) + offsetWidth);
                    pos[i].setTop((int) (rect[i][1] * imageHeight - getResources().getDimension(R.dimen.position_button_size) / 2) + offsetHeight);
                    pos[i].setRight((int) (rect[i][0] * imageWidth + getResources().getDimension(R.dimen.position_button_size) / 2) + offsetWidth);
                    pos[i].setBottom((int) (rect[i][1] * imageHeight + getResources().getDimension(R.dimen.position_button_size) / 2) + offsetHeight);
                }

                Log.i("PositioningActivity", "view width : " + width);
                Log.i("PositioningActivity", "view height : " + height);
            }
        }.init(pos, rect));


        t.setOnClickListener(this);
        p.setOnClickListener(this);
        a.setOnClickListener(this);
        m.setOnClickListener(this);

        t.setOnTouchListener(this);
        p.setOnTouchListener(this);
        a.setOnTouchListener(this);
        m.setOnTouchListener(this);

        t.setTag(Define.POS_TAG_T);
        p.setTag(Define.POS_TAG_P);
        a.setTag(Define.POS_TAG_A);
        m.setTag(Define.POS_TAG_M);

        t.setOnLongClickListener(this);
        p.setOnLongClickListener(this);
        a.setOnLongClickListener(this);
        m.setOnLongClickListener(this);

        DragListener listener = new DragListener();
        back.setOnDragListener(listener);

        container = (RelativeLayout)findViewById(R.id.pos_message_container);
        play_container = (RelativeLayout)findViewById(R.id.play_container);
        play = (ImageView)findViewById(R.id.pos_play);
        pos_p = (TextView)findViewById(R.id.pos_message_p);
        pos_t = (TextView)findViewById(R.id.pos_message_t);
        pos_a = (TextView)findViewById(R.id.pos_message_a);
        pos_m = (TextView)findViewById(R.id.pos_message_m);
        record = (ImageView)findViewById(R.id.pos_record);

        record.setOnClickListener(this);
        play.setOnClickListener(this);

        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (container.getVisibility() == View.VISIBLE) {
                    container.setVisibility(View.INVISIBLE);
                }
                if(play_container.getVisibility() == View.VISIBLE){
                    play_container.setVisibility(View.INVISIBLE);
                }
                if(player != null){
                    if(player.isPlaying()){
                        player.stop();
                        player.release();
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                offset = new Point((int) event.getX(), (int) event.getY());
            }
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
        ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);

        View.DragShadowBuilder myShadow = new CustomDragShadowBuilder(v, offset);
        v.startDrag(dragData, myShadow, null, 0);
        return false;
    }

    @Override
    public void onClick(View v) {
        int x = (int)v.getX();
        int y = (int)v.getY();

        switch(v.getId()){
            case R.id.pos_m:
                container.layout(x, y, x + 300, y + 300);
                container.setVisibility(View.VISIBLE);
                checkVisibility();
                play_container.layout(x, y + 300, x + 300, y + 600);
                checkPlayData(Define.POS_TAG_M);
                pos_m.setVisibility(View.VISIBLE);
                position = Define.POS_TAG_M;
                break;
            case R.id.pos_p:
                container.layout(x, y, x + 300, y + 300);
                container.setVisibility(View.VISIBLE);
                checkVisibility();
                play_container.layout(x, y + 300, x + 300, y + 600);
                checkPlayData(Define.POS_TAG_P);
                pos_p.setVisibility(View.VISIBLE);
                position = Define.POS_TAG_P;
                break;
            case R.id.pos_a:
                container.layout(x, y, x + 300, y + 300);
                container.setVisibility(View.VISIBLE);
                checkVisibility();
                play_container.layout(x, y + 300, x + 300, y + 600);
                checkPlayData(Define.POS_TAG_A);
                pos_a.setVisibility(View.VISIBLE);
                position = Define.POS_TAG_A;
                break;
            case R.id.pos_t:
                container.layout(x, y, x + 300, y + 300);
                container.setVisibility(View.VISIBLE);
                checkVisibility();
                play_container.layout(x, y + 300, x + 300, y + 600);
                checkPlayData(Define.POS_TAG_T);
                pos_t.setVisibility(View.VISIBLE);
                position = Define.POS_TAG_T;
                break;
            case R.id.pos_record:
                Intent intent = new Intent(PositioningActivity.this, RecordingActivity.class);
                intent.putExtra("position", getPosition());
                intent.putExtra("id", id);
                startActivity(intent);
                break;
            case R.id.pos_play:
                playing(getDataToPlay());
                break;
            case R.id.save_data_btn:
                checkInternetConnection();
                break;
        }
    }

    private void playing(String fileName) {

        player = new MediaPlayer();

        try {
            player.setDataSource(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            player.prepare();
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        }
        try {
            player.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private String getDataToPlay() {
        if (position.equals(Define.POS_TAG_A)) {
            return recordItem.getRecordFile1();
        } else if (position.equals(Define.POS_TAG_P)) {
            return recordItem.getRecordFile2();
        } else if (position.equals(Define.POS_TAG_T)) {
            return recordItem.getRecordFile3();
        } else if (position.equals(Define.POS_TAG_M)) {
            return recordItem.getRecordFile4();
        }
        return null;
    }

    private void checkInternetConnection() {
        int status = NetworkUtil.getConnectivityStatus(context);
        if(status == Define.TYPE_WIFI || status == Define.TYPE_MOBILE){
            alertDialog();
        }else if(status == Define.TYPE_NOT_CONNECTED){
            Toast.makeText(getApplicationContext(), "inaccessible", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
            startActivity(intent);
        }
    }

    private void sendData() {

        try {
            progressDialog = ProgressDialog.show(PositioningActivity.this, "", "uploading...");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] filePaths = {recordItem.getRecordFile1(), recordItem.getRecordFile2(), recordItem.getRecordFile3(), recordItem.getRecordFile4()};

        ProxyUp.uploadRecordData(recordItem, filePaths, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] bytes) {
                Log.i("test", "up onSuccess:" + statusCode);
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "onSuccess", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("test", "up onFailure:" + statusCode);
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "onFailure", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        Intent intent = new Intent(PositioningActivity.this, IntroActivity.class);
        startActivity(intent);

    }
    private void alertDialog() {

    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(PositioningActivity.this);
    alert_confirm.setMessage("Are you sure you want to send your data to BonCoeur?").setCancelable(false).setPositiveButton("CONFIRM",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendData();

                }
            }).setNegativeButton("CANCEL",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 'No'
                    finish();
                    return;
                }
            });

    AlertDialog alert = alert_confirm.create();
    alert.show();

}

    private void checkPlayData(String pos) {
        if(pos.equals(Define.POS_TAG_M)){
            if(playM){
                play_container.setVisibility(View.VISIBLE);
            }
        }else if(pos.equals(Define.POS_TAG_P)){
            if(playP){
                play_container.setVisibility(View.VISIBLE);
            }
        }else if(pos.equals(Define.POS_TAG_T)){
            if(playT){
                play_container.setVisibility(View.VISIBLE);
            }
        }else if(pos.equals(Define.POS_TAG_A)){
            if(playA){
                play_container.setVisibility(View.VISIBLE);
            }
        }
    }

    private String getPosition() {
        if(pos_p.getVisibility() == View.VISIBLE){
            return Define.POS_TAG_P;
        }else if(pos_m.getVisibility() == View.VISIBLE){
            return Define.POS_TAG_M;
        }else if(pos_a.getVisibility() == View.VISIBLE){
            return Define.POS_TAG_A;
        }else if(pos_t.getVisibility() == View.VISIBLE){
            return Define.POS_TAG_T;
        }
        return null;
    }

    private void checkVisibility() {
        if(pos_p.getVisibility() == View.VISIBLE){
            pos_p.setVisibility(View.INVISIBLE);
        }else if(pos_m.getVisibility() == View.VISIBLE){
            pos_m.setVisibility(View.INVISIBLE);
        }else if(pos_a.getVisibility() == View.VISIBLE){
            pos_a.setVisibility(View.INVISIBLE);
        }else if(pos_t.getVisibility() == View.VISIBLE){
            pos_t.setVisibility(View.INVISIBLE);
        }
        if(play_container.getVisibility() == View.VISIBLE){
            play_container.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        //뒤로 못가게-동일인물 db계속 쌓임;
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataCheck();
    }

    private void dataCheck() {
        Log.d("test", "temp: id " + id + "recent " + dao.getRecentId());

        if(dao.getRecentId() > id){
            recordItem = dao.getRcordById(id);
            Log.d("test", "샌드에서 오는 id: " + id);
        }else if(dao.getRecentId() != id) {
            recordItem = dao.getRcordById(dao.getRecentId());
            Log.d("test", "최신 id(저장중): " + id);
        }else if(dao.getRecentId() == id){
            recordItem = dao.getRcordById(dao.getRecentId());
        }
            patient_name.setText("name: " + recordItem.getName() + " (" + recordItem.getAge() + ")");

        if(!recordItem.getRecordFile1().equals("")){
            changeColor(a);
            playA = true;
        }
        if(!recordItem.getRecordFile2().equals("")){
            changeColor(p);
            playP = true;
        }
        if(!recordItem.getRecordFile3().equals("")){
            changeColor(t);
            playT = true;
        }
        if(!recordItem.getRecordFile4().equals("")){
            changeColor(m);
            playM = true;
        }
    }

    private void changeColor(ImageView iv) {
        iv.setBackgroundColor(Color.parseColor("#fdd835"));
    }
}
