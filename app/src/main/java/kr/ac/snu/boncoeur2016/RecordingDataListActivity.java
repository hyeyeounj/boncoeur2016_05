//package kr.ac.snu.boncoeur2016;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.media.MediaPlayer;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
///**
// * Created by hyes on 2016. 4. 25..
// */
//public class RecordingDataListActivity extends AppCompatActivity{
//private ArrayList<RecordItem> recordList;
//private ListView listView;
//private String name;
//private int id;
//private TextView name_tv;
//private Button send;
//private RecordItem record, data;
//private static final int REVISECODE1 = 1;
//private static final int REVISECODE2 = 2;
//private static final int REVISECODE3 = 3;
//private static final int REVISECODE4 = 4;
//private static final int REVISECODE5 = 5;
//private static final int REVISECODE6 = 6;
//public ImageView pic1, pic2, pic3, pic4, pic5, pic6;
//private Button play1, play2, play3, play4, play5, play6;
//private MediaPlayer player;
//private boolean p1, p2, p3, p4, p5, p6;
//
//private static final int TYPE_WIFI = 1;
//private static final int TYPE_MOBILE = 2;
//private static final int TYPE_NOT_CONNECTED = 0;
//
//private Spinner spinner, spinner2;
//private String[] positions = {"Optional", "Mitral valve", "Triscuspid valve", "Pulmonic valve", "Aortic valve"};
//private String selected_position, selected_position2;
//
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.custom_record_data_list);
//
//
//            Intent intent = getIntent();
//            id = intent.getIntExtra("id", 0);
//            name = intent.getStringExtra("Name");
////        Dao dao = new Dao(getApplicationContext());
////        record = dao.getRecordingDataByName(id, name);
////        Log.i("test", "from nameList" + record.getName()+ " , " + record.getRecordFile1());
//
//            ArrayAdapter<String> list;
//            list = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, positions);
//
//            spinner = (Spinner) findViewById(R.id.spinner);
//            spinner.setPrompt("Choose the recording position");
//            spinner.setAdapter(list);
//            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    selected_position = (String)spinner.getAdapter().getItem(spinner.getSelectedItemPosition());
//                    Log.i("test", selected_position);
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
//
//            spinner2 = (Spinner) findViewById(R.id.spinner2);
//            spinner2.setPrompt("Choose the recording position");
//            spinner2.setAdapter(list);
//            spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    selected_position2 = (String)spinner2.getAdapter().getItem(spinner2.getSelectedItemPosition());
//                    Log.i("test", selected_position2);
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
//
//            p1=true;
//            p2=true;
//            p3=true;
//            p4=true;
//            p5=true;
//            p6=true;
//
//            name_tv = (TextView) findViewById(R.id.name);
//            pic1 = (ImageView) findViewById(R.id.cap1);
//            pic2 = (ImageView) findViewById(R.id.cap2);
//            pic3 = (ImageView) findViewById(R.id.cap3);
//            pic4 = (ImageView) findViewById(R.id.cap4);
//            pic5 = (ImageView) findViewById(R.id.cap5);
//            pic6 = (ImageView) findViewById(R.id.cap6);
//
//            play1 = (Button) findViewById(R.id.audio1);
//            play2 = (Button) findViewById(R.id.audio2);
//            play3 = (Button) findViewById(R.id.audio3);
//            play4 = (Button) findViewById(R.id.audio4);
//            play5 = (Button) findViewById(R.id.audio5);
//            play6 = (Button) findViewById(R.id.audio6);
//
////        name_tv.setText(record.getName());
//
//            send = (Button) findViewById(R.id.send_btn);
//
////        pic1.setImageBitmap(loadCaptureView(record.getCaptureFile1()));
////        pic2.setImageBitmap(loadCaptureView(record.getCaptureFile2()));
////        pic3.setImageBitmap(loadCaptureView(record.getCaptureFile3()));
////        pic4.setImageBitmap(loadCaptureView(record.getCaptureFile4()));
////        pic5.setImageBitmap(loadCaptureView(record.getCaptureFile5()));
////        pic6.setImageBitmap(loadCaptureView(record.getCaptureFile6()));
//
//
//            send.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//
//                    int status = NetworkState.getConnectivityStatus(getApplicationContext());
//
//                    switch (status) {
//                        case TYPE_WIFI:
//                        case TYPE_MOBILE:
//
//                            Toast.makeText(getApplicationContext(), "데이터 전송 가능 상태", Toast.LENGTH_SHORT).show();
//                            alertDialog();
//
//                            break;
//                        case TYPE_NOT_CONNECTED:
//                            Toast.makeText(getApplicationContext(), "데이터 전송 불가 상태", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(getApplicationContext(), Ending2.class);
//                            startActivity(intent);
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            });
//
//
//            Button step1 = (Button) findViewById(R.id.step1);
//            step1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(RecordingDataList.this, RecordForRevision.class);
//                    intent.putExtra("id", id);
//                    intent.putExtra("IDX", 1);
//                    intent.putExtra("name", name);
//                    intent.putExtra("selectedPos", "Mitral valve");
//                    startActivityForResult(intent, REVISECODE1);
//
//                }
//            });
//
//            Button step2 = (Button) findViewById(R.id.step2);
//            step2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(RecordingDataList.this, RecordForRevision.class);
//                    intent.putExtra("id", id);
//                    intent.putExtra("IDX", 2);
//                    intent.putExtra("name", name);
//                    intent.putExtra("selectedPos", "Triscuspid valve");
//                    startActivityForResult(intent, REVISECODE2);
//                }
//            });
//
//            Button step3 = (Button) findViewById(R.id.step3);
//            step3.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(RecordingDataList.this, RecordForRevision.class);
//                    intent.putExtra("id", id);
//                    intent.putExtra("IDX", 3);
//                    intent.putExtra("name", name);
//                    intent.putExtra("selectedPos", "Pulmonic valve");
//                    startActivityForResult(intent, REVISECODE3);
//                }
//            });
//
//            Button step4 = (Button) findViewById(R.id.step4);
//            step4.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(RecordingDataList.this, RecordForRevision.class);
//                    intent.putExtra("id", id);
//                    intent.putExtra("IDX", 4);
//                    intent.putExtra("name", name);
//                    intent.putExtra("selectedPos", "Aortic valve");
//                    startActivityForResult(intent, REVISECODE4);
//                }
//            });
//
//            Button step5 = (Button) findViewById(R.id.step5);
//            step5.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (selected_position != "Optional") {
//                        Intent intent = new Intent(RecordingDataList.this, RecordForRevision.class);
//                        intent.putExtra("id", id);
//                        intent.putExtra("IDX", 5);
//                        intent.putExtra("name", name);
//                        intent.putExtra("selectedPos", selected_position);
//                        startActivityForResult(intent, REVISECODE5);
//                    }
//                }
//            });
//            Button step6 = (Button) findViewById(R.id.step6);
//            step6.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (selected_position2 != "Optional") {
//                        Intent intent = new Intent(RecordingDataList.this, RecordForRevision.class);
//                        intent.putExtra("id", id);
//                        intent.putExtra("IDX", 6);
//                        intent.putExtra("name", name);
//                        intent.putExtra("selectedPos", selected_position2);
//                        startActivityForResult(intent, REVISECODE6);
//                    }
//                }
//            });
//
//        }
//
//        private void alertDialog() {
//
//            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(RecordingDataList.this);
//            alert_confirm.setMessage("Are you sure you want to send your data to BonCoeur?").setCancelable(false).setPositiveButton("CONFIRM",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                            Intent intent = new Intent(RecordingDataList.this, ConfirmEmail.class);
//                            intent.putExtra("id", id);
//                            intent.putExtra("name", name);
//                            startActivityForResult(intent, RESULT_OK);
//
//
//                        }
//                    }).setNegativeButton("CANCEL",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // 'No'
//                            finish();
//                            return;
//                        }
//                    });
//
//            AlertDialog alert = alert_confirm.create();
//            alert.show();
//
//        }
//
//
//        public void Play1Clicked(View v) throws IOException {
//
//
//            if (p1) {
//                if(playingCheck()) {
//                    playAudio(record.getRecordFile1(), 1);
//                    Log.i("test", record.getRecordFile1());
//                    p1 = !p1;
//                    stopText(play1);
//                }
//            } else {
//                p1 = !p1;
//                killPlayer();
//                playText(play1);
//            }
//        }
//
//        public void Play2Clicked(View v) throws IOException {
//            if (p2) {
//                if(playingCheck()) {
//                    playAudio(record.getRecordFile2(), 2);
//                    Log.i("test", record.getRecordFile2());
//                    p2 = !p2;
//                    stopText(play2);
//                }
//            } else {
//                p2 = !p2;
//                killPlayer();
//                playText(play2);
//            }
//        }
//        public void Play3Clicked(View v) throws IOException {
//            if(p3){
//                if(playingCheck()) {
//                    playAudio(record.getRecordFile3(), 3);
//                    Log.i("test", record.getRecordFile3());
//                    p3 = !p3;
//                    stopText(play3);
//                }
//            }else{
//                killPlayer();
//                p3 = !p3;
//                playText(play3);
//            }
//        }
//        public void Play4Clicked(View v) throws IOException {
//            if(p4) {
//                if(playingCheck()) {
//                    playAudio(record.getRecordFile4(), 4);
//                    Log.i("test", record.getRecordFile4());
//                    p4 = !p4;
//                    stopText(play4);
//                }
//            }else{
//                killPlayer();
//                p4 =!p4;
//                playText(play4);
//            }
//        }
//        public void Play5Clicked(View v) throws IOException {
//            if(p5) {
//                if(playingCheck()) {
//                    playAudio(record.getRecordFile5(), 5);
//                    Log.i("test", record.getRecordFile5());
//                    p5 = !p5;
//                    stopText(play5);
//                }
//            }else{
//                killPlayer();
//                p5 = !p5;
//                playText(play5);
//            }
//        }
//        public void Play6Clicked(View v) throws IOException {
//            if(p6) {
//                if(playingCheck()) {
//                    playAudio(record.getRecordFile6(), 6);
//                    Log.i("test", record.getRecordFile6());
//                    p6 = !p6;
//                    stopText(play6);
//                }
//            }else{
//                killPlayer();
//                p6 =!p6;
//                playText(play6);
//            }
//        }
//
//
//
//
//        public void playAudio(String file, final int chk) throws IOException {
//
//
//            player = new MediaPlayer();
//            try {
//                player.setDataSource(file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                player.prepare();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//            }
//            try {
//                player.start();
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//            }
//
//            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//
//                    switch (chk) {
//                        case 1:
//                            playText(play1);
//                            p1 = !p1;
//                            break;
//                        case 2:
//                            playText(play2);
//                            p2 = !p2;
//                            break;
//                        case 3:
//                            playText(play3);
//                            p3 = !p3;
//                            break;
//                        case 4:
//                            playText(play4);
//                            p4 = !p4;
//                            break;
//                        case 5:
//                            playText(play5);
//                            p5 = !p5;
//                            break;
//                        case 6:
//                            playText(play6);
//                            p6 = !p6;
//                            break;
//                        default:
//                            break;
//
//                    }
//                }
//            });
//        }
//
//        private boolean playingCheck() {
//            int i =0;
//            if(!p1){
//                i++;
//            }
//            if(!p2){
//                i++;
//            }
//            if(!p3){
//                i++;
//            }
//            if(!p4){
//                i++;
//            }
//            if(!p5){
//                i++;
//            }
//            if(!p6){
//                i++;
//            }
//
//            if(i < 1){
//                Log.i("test", " i<=1  i " + i);
//                return true;
//            }else{
//                Toast.makeText(getApplicationContext(), "stop playing first~", Toast.LENGTH_SHORT).show();
//                Log.i("test", "else i " + i);
//                return false;
//            }
//        }
//
//        private Bitmap loadCaptureView(String uri) {
//
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inDither=false;
//            options.inSampleSize = 8;
//            Bitmap bitmap = BitmapFactory.decodeFile(uri, options);
//            return bitmap;
//        }
//
//        @Override
//        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//            switch (requestCode) {
//                case REVISECODE1:
//                    if (resultCode == RESULT_OK) {
//                        String name = data.getStringExtra("name");
//                        String audio1 = data.getStringExtra("file1");
//                        String pic11 = data.getStringExtra("pic1");
//                    } else {
//                        finish();
//                    }
//                    break;
//
//                case REVISECODE2:
//                    if (resultCode == RESULT_OK) {
//                        String name = data.getStringExtra("name");
//                        String audio2 = data.getStringExtra("file2");
//                        String pic22 = data.getStringExtra("pic2");
//                    } else {
//                        finish();
//                    }
//                    break;
//
//                case REVISECODE3:
//                    if (resultCode == RESULT_OK) {
//                        String name = data.getStringExtra("name");
//                        String audio3 = data.getStringExtra("file3");
//                        String pic33 = data.getStringExtra("pic3");
//                    } else {
//                        finish();
//                    }
//                    break;
//
//                case REVISECODE4:
//                    if (resultCode == RESULT_OK) {
//                        String name = data.getStringExtra("name");
//                        String audio4 = data.getStringExtra("file4");
//                        String pic44 = data.getStringExtra("pic4");
//                    } else {
//                        finish();
//                    }
//                    break;
//
//                case REVISECODE5:
//                    if (resultCode == RESULT_OK) {
//                        String name = data.getStringExtra("name");
//                        String audio5 = data.getStringExtra("file5");
//                    } else {
//                        finish();
//                    }
//                    break;
//
//                case REVISECODE6:
//                    if (resultCode == RESULT_OK) {
//                        String name = data.getStringExtra("name");
//                        String audio6 = data.getStringExtra("file6");
//                        String pic66 = data.getStringExtra("pic6");
//
//                    } else {
//                        finish();
//                    }
//                    break;
//            }
//        }
//
//
//
//        @Override
//        protected void onResume() {
//            super.onResume();
//            Dao dao = new Dao(this);
//            record =  dao.getRcordById(id);
//            Log.i("test", "List~onResume~~~:" + record.getName() + ", " + record.getPos1());
//
//            pic1.setImageBitmap(loadCaptureView(record.getCaptureFile1()));
//            pic2.setImageBitmap(loadCaptureView(record.getCaptureFile2()));
//            pic3.setImageBitmap(loadCaptureView(record.getCaptureFile3()));
//            pic4.setImageBitmap(loadCaptureView(record.getCaptureFile4()));
//            pic5.setImageBitmap(loadCaptureView(record.getCaptureFile5()));
//            pic6.setImageBitmap(loadCaptureView(record.getCaptureFile6()));
//
//            if(record.getRecordFile1().contains("mp4")) {
//                play1.setVisibility(View.VISIBLE);
//            }
//            if(record.getRecordFile2().contains("mp4")) {
//                play2.setVisibility(View.VISIBLE);
//            }
//            if(record.getRecordFile3().contains("mp4")) {
//                play3.setVisibility(View.VISIBLE);
//            }
//            if(record.getRecordFile4().contains("mp4")) {
//                play4.setVisibility(View.VISIBLE);
//            }
//            if(record.getRecordFile5().contains("mp4")) {
//                play5.setVisibility(View.VISIBLE);
//            }
//            if(record.getRecordFile6().contains("mp4")) {
//                play6.setVisibility(View.VISIBLE);
//            }
//
//            name_tv.setText(record.getName());
//
//
//        }
//
//        private void killPlayer() {
//            if(player != null){
//                player.stop();
//                player.release();
//                player = null;
//            }
//        }
//
//        private void playText(Button stopButton) {
//            stopButton.setText("PLAY");
//            stopButton.setTextColor(Color.BLACK);
//
//        }
//
//        private void stopText(Button playButton) {
//            playButton.setText("STOP");
//            playButton.setTextColor(Color.parseColor("#ff8a65"));
//        }
//
//}
//}
