package kr.ac.snu.boncoeur2016;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import kr.ac.snu.boncoeur2016.utils.Define;

/**
 * Created by hyes on 2016. 4. 25..
 */
public class Dao {

    private Context context;
    private SQLiteDatabase database;

    public Dao(Context context) {
        this.context = context;

        database = context.openOrCreateDatabase(Define.RECORDED_FILEPATH + "BonCoeurDATA.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);

        try{
            String sql = "CREATE TABLE IF NOT EXISTS Record(ID integer primary key,"
                    + "Name text not null,"
                    + "Date text not null, "
                    + "Age text not null,"
                    + "email text not null,"
                    + "audio1 text,"
                    + "pos1 text,"
                    + "audio2 text,"
                    + "pos2 text,"
                    + "audio3 text,"
                    + "pos3 text,"
                    + "audio4 text,"
                    + "pos4 text);";

            database.execSQL(sql);

        }catch(Exception e){
            Log.e("test", "CREATE TABLE FAILED! - " + e);
            e.printStackTrace();
        }
    }


    public ArrayList<NameItem> getArticleList(){

        ArrayList<NameItem>nameList = new ArrayList<NameItem>();
        int id;
        String name;

        String sql = "SELECT*FROM Record;";
        Cursor cursor = database.rawQuery(sql, null);


        while(cursor.moveToNext()){

            id = cursor.getInt(0);
            Log.i("test", id+"");
            name = cursor.getString(1);
            Log.i("test", name+"");
            nameList.add(new NameItem(id, name));
        }
        cursor.close();

        return nameList;
    }


    public RecordItem getRecordingDataByName(int id, String name_selector){

        RecordItem record = null;
        String name;
        String date;
        int age;
        String email;
        String audio1;
        String pos1;
        String audio2;
        String pos2;
        String audio3;
        String pos3;
        String audio4;
        String pos4;

        String sql = "SELECT*FROM Record where id = " + id + " and Name = '" + name_selector + "';";

        Log.i("test", sql);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        cursor.moveToNext();

        name = cursor.getString(1);
        date = cursor.getString(2);
        age = cursor.getInt(3);
        email = cursor.getString(4);
        audio1 = cursor.getString(5);
        pos1 = cursor.getString(6);
        audio2 = cursor.getString(7);
        pos2 = cursor.getString(8);
        audio3 = cursor.getString(9);
        pos3 = cursor.getString(10);
        audio4 = cursor.getString(11);
        pos4 = cursor.getString(12);


        record = new RecordItem(name, date, age, email, audio1, pos1, audio2, pos2, audio3, pos3, audio4, pos4);
//        Log.i("test", "called data: " + record.getName() + record.getRecordFile1() + record.getCaptureFile1());

        cursor.close();

        return record;
    }


//    public void insertJsonData(String jsonData) {
//        String id;
//        String name;
//        String date;
//        String audio1;
//        String capture1;
//        String audio2;
//        String capture2;
//        String audio3;
//        String capture3;
//        String audio4;
//        String capture4;
//
////        FileDownloader fileDownloader = new FileDownloader(context);
//
//        try{
//            JSONArray jArr = new JSONArray(jsonData);
//
//            for(int i=0; i <jArr.length(); ++i){
//                JSONObject jObj = jArr.getJSONObject(i);
//
//                id = jObj.getString("ID");
//                name = jObj.getString("Name");
//                date = jObj.getString("date");
//                audio1 =jObj.getString("audio1");
//                capture1 = jObj.getString("capture1");
//                audio2 = jObj.getString("audio2");
//                capture2 = jObj.getString("capture2");
//                audio3 = jObj.getString("audio3");
//                capture3 = jObj.getString("capture3");
//                audio4 = jObj.getString("audio4");
//                capture4 = jObj.getString("capture4");
//
//                String sql = "INSERT INTO Record (ID, Name, Date, audio1, capture1, audio2, capture2, audio3, capture3, audio4, capture4)"
//                        + "VALUES('" + id +"','" + name +  "','" + date + "','" + audio1 + "','" + capture1 + "','"
//                        + audio2 + "','" + capture2 + "','" + audio3 + "','" + capture3 + "','"
//                        + audio4 + "','" + capture4 + "');";
//
//                try{
//                    database.execSQL(sql);
//                    Log.i("test", sql);
//                }catch(Exception e){
//                    Log.e("test", "DB error! - "+e);
//                    e.printStackTrace();
//                }
//
//                fileDownloader.downFile("http://192.168.56.1:8899/uploads/"+ audio1, audio1);
////                fileDownloader.downFile("http://192.168.56.1:8899/uploads/"+ audio2, audio2);
////                fileDownloader.downFile("http://192.168.56.1:8899/uploads/"+ audio3, audio3);
////                fileDownloader.downFile("http://192.168.56.1:8899/uploads/"+ audio4, audio4);
////                fileDownloader.downFile("http://192.168.56.1:8899/uploads/"+ capture1, capture1);
////                fileDownloader.downFile("http://192.168.56.1:8899/uploads/"+ capture2, capture2);
////                fileDownloader.downFile("http://192.168.56.1:8899/uploads/"+ capture3, capture3);
////                fileDownloader.downFile("http://192.168.56.1:8899/uploads/"+ capture4, capture4);
//
//
//
//            }
//        }catch(JSONException e){
//            Log.e("test", "JSON ERROR! - " + e);
//            e.printStackTrace();
//        }
//    }

    public int getRecentId(){

        String sql = "SELECT * FROM Record ORDER BY id desc limit 1;";

        int id = 0;

        try {
            database.execSQL(sql);
            Log.i("test", sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Cursor cursor = database.rawQuery(sql, null);


        while(cursor.moveToNext()){

            id = cursor.getInt(0);

        }
        cursor.close();

        return id;
    }

    public RecordItem getRecordById(int i) {

        String sql = "SELECT * FROM Record where id = " + i + ";";
        RecordItem record =null;
        String name =null;
        String date = null;
        int age =0;
        String email=null, recordFile1=null, captureFile1=null, pos1=null, recordFile2=null, captureFile2=null, pos2=null, recordFile3=null, captureFile3=null, pos3=null, recordFile4=null, captureFile4=null, pos4=null, recordFile5=null, captureFile5=null, pos5=null, recordFile6=null, captureFile6=null, pos6=null;


        try {
            database.execSQL(sql);
            Log.i("test", sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Cursor cursor = database.rawQuery(sql, null);


        while(cursor.moveToNext()){

            name = cursor.getString(1);
            Log.d("test", "1" + name);
            date = cursor.getString(2);
            Log.d("test", "2" + date);
            age = cursor.getInt(3);
            Log.d("test", "3" + age);
            email = cursor.getString(4);
            Log.d("test", "4" + email);
            recordFile1 = cursor.getString(5);
            Log.d("test", "5" + recordFile1);
            pos1 = cursor.getString(6);
            Log.d("test", "6" + pos1);
            recordFile2 = cursor.getString(7);
            Log.d("test", "7" + recordFile2);
            pos2 = cursor.getString(8);
            Log.d("test", "8" + pos2);
            recordFile3 = cursor.getString(9);
            Log.d("test", "9" + recordFile3);
            pos3 = cursor.getString(10);
            Log.d("test", "10" + pos3);
            recordFile4 = cursor.getString(11);
            Log.d("test", "11" + recordFile4);
            pos4 = cursor.getString(12);
            Log.d("test", "12" + pos4);
        }

        record = new RecordItem(name, date, age, email, recordFile1, pos1, recordFile2, pos2, recordFile3, pos3, recordFile4, pos4);

        cursor.close();

        return record;
    }

    public void updateData(String position, String audio, String name, int id) {

        int pos = -1;
        for (int i = 0; i < 4; i++)
            if (position.equals(Define.POS_TAG[i])) {
                pos = i + 1;
                break;
            }
        if (pos == -1) {
            Log.e("Dao", "Undefined Position : " + position);
            // Otherwise...save on 5, 6, etc...
            return;
        }

        String sql = "UPDATE Record SET audio" + pos + "='" + audio + "' where name = '" + name + "' and id = " + id + ";";

        try {
            database.execSQL(sql);
            Log.i("test", sql);
        } catch (Exception e) {
            Log.e("test", "DB error! - " + e);
            e.printStackTrace();
        }
    }
//
//    public void updateData1(String audio, String name, int id) {
//
//
//        String sql = "UPDATE Record SET audio1='" +  audio +  "' where name = '" + name +"' and id = "+ id +";";
//
//        try {
//            database.execSQL(sql);
//            Log.i("test", sql);
//        } catch (Exception e) {
//            Log.e("test", "DB error! - " + e);
//            e.printStackTrace();
//        }
//    }
//
//    public void updateData2(String audio, String name, int id) {
//
//
//        String sql = "UPDATE Record SET audio2= '" +  audio +  "'where name = '"+ name +"' and id = "+ id +";";
//
//        try {
//            database.execSQL(sql);
//            Log.i("test", sql);
//        } catch (Exception e) {
//            Log.e("test", "DB error! - " + e);
//            e.printStackTrace();
//        }
//    }
//
//    public void updateData3(String audio, String name, int id) {
//
//
//        String sql = "UPDATE Record SET audio3= '" +  audio +  "' where name = '"+ name+ "' and id = "+ id +";";
//
//        try {
//            database.execSQL(sql);
//            Log.i("test", sql);
//        } catch (Exception e) {
//            Log.e("test", "DB error! - " + e);
//            e.printStackTrace();
//        }
//    }
//
//    public void updateData4(String audio, String name, int id) {
//
//
//        String sql = "UPDATE Record SET audio4= '" +  audio +  "' where name = '"+ name +"' and id = "+ id +";";
//
//        try {
//            database.execSQL(sql);
//            Log.i("test", sql);
//        } catch (Exception e) {
//            Log.e("test", "DB error! - " + e);
//            e.printStackTrace();
//        }
//    }

    public void updateEmail(String email, String name, int id) {


        String sql = "UPDATE Record SET email= '" +  email +  "' where name = '"+ name +"' and id = "+ id +";";

        try {
            database.execSQL(sql);
            Log.i("test", sql);
        } catch (Exception e) {
            Log.e("test", "DB error! - " + e);
            e.printStackTrace();
        }
    }

    public void targetInsert(String name, int age) {


        String sql = "INSERT INTO Record (Name, Date, Age, email, audio1, pos1, audio2, pos2, audio3, pos3, audio4, pos4)"
                + "VALUES('" + name +  "','" + new Date().toString() + "'," + age + ",'" + "" + "','" + "" + "','" + "A" + "','"
                + "" + "','" + "P" + "','" + "" + "','" + "T" + "','" + "" + "','" + "M" + "');";

        try {
            database.execSQL(sql);
            Log.i("test", sql);
        } catch (Exception e) {
            Log.e("test", "DB error! - " + e);
            e.printStackTrace();
        }
    }
}