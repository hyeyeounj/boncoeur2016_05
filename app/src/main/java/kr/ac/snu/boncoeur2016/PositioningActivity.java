package kr.ac.snu.boncoeur2016;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kr.ac.snu.boncoeur2016.utils.Define;

/**
 * Created by hyes on 2016. 3. 17..
 */
public class PositioningActivity extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener {

    ImageView t, p, a, m, record;
    RelativeLayout back;
    RelativeLayout container;
    TextView pos_m, pos_p, pos_a, pos_t, patient_name;
    String name;
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

        t.setOnClickListener(this);
        p.setOnClickListener(this);
        a.setOnClickListener(this);
        m.setOnClickListener(this);

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
                if(container.getVisibility() == View.VISIBLE){
                    container.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

        Intent intent = getIntent();
        name =  intent.getStringExtra("name");
        age = intent.getIntExtra("age", 0);
        patient_name.setText(name + ", " + age);

        //selected_position = intent.getStringExtra("selectedPos");
    }



    @Override
    public boolean onLongClick(View v) {
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
        ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);

        View.DragShadowBuilder myShadow = new DragShadow(v);
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
                intent.putExtra("name", name);
                intent.putExtra("age", age);
              //  startActivityForResult(intent, TARGET_NAME);
                startActivity(intent);
        }


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

    }
}
