package kr.ac.snu.boncoeur2016;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.snu.boncoeur2016.utils.Define;

/**
 * Created by hyes on 2016. 4. 25..
 */
public class RecordTargetInfoActivity extends AppCompatActivity {
    private static final int TARGET_NAME = 1;
    String target_name = null, temp_age;
    int target_age;
    EditText name_edit_text, age_edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_target_info);
        name_edit_text = (EditText) findViewById(R.id.target_name);
        age_edit_text = (EditText) findViewById(R.id.target_age);
        Button start_btn = (Button) findViewById(R.id.start_btn);

        age_edit_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            private Button b;

            private TextView.OnEditorActionListener init(Button b) {

                this.b = b;
                return this;
            }

            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
//                Log.i("RecordTargetInfoActivity", "OnEditorAction : " + actionId + ", " + event);
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    b.callOnClick();
                }
                return true;
            }
        }.init(start_btn));

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                target_name = name_edit_text.getText().toString();
                temp_age = age_edit_text.getText().toString();


                if (target_name.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter name", Toast.LENGTH_SHORT).show();

                } else if(temp_age.equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter age", Toast.LENGTH_SHORT).show();

                } else{

                    try {
                        target_age = Integer.parseInt(temp_age);

                        Dao dao = new Dao(getApplicationContext());
                        dao.targetInsert(target_name, target_age);
                        dao.positionInsert(target_name, target_age);

                        Intent intent = new Intent(getApplicationContext(), PositioningActivity.class);
                        intent.putExtra("id", Define.REVISION);
                        startActivityForResult(intent, TARGET_NAME);


                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Age should be numerical number only", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });
    }
}

