package kr.ac.snu.boncoeur2016.utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

import kr.ac.snu.boncoeur2016.RecordItem;

/**
 * Created by hyes on 2016. 4. 28..
 */
public class ProxyUp {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void uploadRecordData(RecordItem record, String[] filePaths,
                                        AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();

        params.put("subject_name", record.getName());
        params.put("age", record.getAge());
        params.put("time1", record.getDate());
        params.put("position1", record.getPos1());
        params.put("position2", record.getPos2());
        params.put("position3", record.getPos3());
        params.put("position4", record.getPos4());

        try {
            params.put("recording1", new File(record.getRecordFile1()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            params.put("recording2", new File(record.getRecordFile2()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            params.put("recording3", new File(record.getRecordFile3()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            params.put("recording4", new File(record.getRecordFile4()));
            // Log.i("test", "recording file" + new File(record.getRecordFile4()).getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // client.post("http://172.30.52.77:8000/submit", params, responseHandler);
        // client.post("http://192.168.2.72:8000/submit", params, responseHandler);
        // client.post("http://192.168.56.1:8000/submit", params, responseHandler);
        client.post("Http://147.46.151.128:8000/submit", params, responseHandler);
    }
}
