package kr.ac.snu.boncoeur2016;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by hyes on 2016. 4. 25..
 */
public class NameListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final int TARGET = 1;
    private ArrayList<NameItem> nameList;
    private ListView listView;
    private TextView tv, empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_list);
        listView = (ListView)findViewById(R.id.nameListView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }


    private void listView(){
        Dao dao = new Dao(getApplicationContext());
        nameList = dao.getArticleList();

        if(nameList.isEmpty()){
            //nameList.add(new NameItem(0, "EMPTY"));
//            nameList.clear();
            Toast.makeText(getApplicationContext(), "Empty List", Toast.LENGTH_SHORT).show();

        }

        NameAdapter nameAdapter = new NameAdapter(this, R.layout.custom_name_list, nameList);
        listView.setAdapter(nameAdapter);
        listView.setOnItemClickListener(this);
    }

    private final Handler handler = new Handler();

    private void refreshData(){
        new Thread(){
            public void run(){

                handler.post(new Runnable(){
                    public void run(){

                        Log.i("test", "handler ON!!!");
                        listView();

                    }
                });
            }
        }.start();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//        Intent intent = new Intent(this, RecordingDataList.class);
//
//        intent.putExtra("Name", nameList.get(position).getName());
//        intent.putExtra("id", nameList.get(position).getId());
//        Log.i("test", "intent " + nameList.get(position).getId());
//        startActivity(intent);

    }


}
