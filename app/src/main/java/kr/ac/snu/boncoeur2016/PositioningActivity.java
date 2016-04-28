package kr.ac.snu.boncoeur2016;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kr.ac.snu.boncoeur2016.utils.CustomDragShadowBuilder;
import kr.ac.snu.boncoeur2016.utils.Define;

/**
 * Created by hyes on 2016. 3. 17..
 */
public class PositioningActivity extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener, View.OnTouchListener {

    ImageView t, p, a, m, record;
    Point offset = new Point(0, 0);
    RelativeLayout back;
    RelativeLayout container;
    TextView pos_m, pos_p, pos_a, pos_t, patient_name;
    String name, position;
    int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        back = (RelativeLayout)findViewById(R.id.back);

        patient_name = (TextView)findViewById(R.id.patient_info);

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
                double factor = 0;
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
        pos_m = (TextView)findViewById(R.id.pos_message_m);
        pos_p = (TextView)findViewById(R.id.pos_message_p);
        pos_t = (TextView)findViewById(R.id.pos_message_t);
        pos_a = (TextView)findViewById(R.id.pos_message_a);
        record = (ImageView)findViewById(R.id.pos_record);
        record.setOnClickListener(this);
        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (container.getVisibility() == View.VISIBLE) {
                    container.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

//        Intent intent = getIntent();
//        name =  intent.getStringExtra("name");
//        age = intent.getIntExtra("age", 0);
//        patient_name.setText("name: " + name + " (" + age + ")");
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
                pos_m.setVisibility(View.VISIBLE);
                break;
            case R.id.pos_p:
                container.layout(x, y, x + 300, y + 300);
                container.setVisibility(View.VISIBLE);
                checkVisibility();
                pos_p.setVisibility(View.VISIBLE);
                break;
            case R.id.pos_a:
                container.layout(x, y, x + 300, y + 300);
                container.setVisibility(View.VISIBLE);
                checkVisibility();
                pos_a.setVisibility(View.VISIBLE);
                break;
            case R.id.pos_t:
                container.layout(x, y, x + 300, y + 300);
                container.setVisibility(View.VISIBLE);
                checkVisibility();
                pos_t.setVisibility(View.VISIBLE);
                break;
            case R.id.pos_record:
                Intent intent = new Intent(PositioningActivity.this, RecordingActivity.class);
                intent.putExtra("position", getPosition());
              //  startActivityForResult(intent, TARGET_NAME);
                startActivity(intent);
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

    }

    private void setContainer(int x, int y, String position) {


        Log.d("test", "before visibility touched x: " + container.getX() + ", y: " + container.getY());


        Log.d("test", "after touched x: " + container.getX() + ", y: " + container.getY());

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
        Dao dao = new Dao(this);
        RecordItem record = dao.getRcordById(dao.getRecentId());
        patient_name.setText("name: " + record.getName() + " (" + record.getAge() + ")");
        if(!record.getRecordFile1().equals("")){
            changeColor(a);
        }
        if(!record.getRecordFile2().equals("")){
            changeColor(p);
        }
        if(!record.getRecordFile3().equals("")){
            changeColor(t);
        }
        if(!record.getRecordFile4().equals("")){
            changeColor(m);
        }
    }

    private void changeColor(ImageView iv) {
        iv.setBackgroundColor(Color.parseColor("#aed581"));
    }


}
