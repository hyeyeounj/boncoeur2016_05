package kr.ac.snu.boncoeur2016.utils;

import android.os.Environment;

/**
 * Created by hyes on 2016. 3. 17..
 */
public class Define {
    public static final String POS_TAG_A = "aortic";
    public static final String POS_TAG_P = "pulmonic";
    public static final String POS_TAG_M = "mitral";
    public static final String POS_TAG_T = "tricuspid";
    public static final String[] POS_TAG = {POS_TAG_A, POS_TAG_P, POS_TAG_T, POS_TAG_M};
    public static final int REVISION = 8888;
    public static final String RECORDED_FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BonCoeur/";
    public static final int COMPRESSED_AUDIO_SAMPLE_RATE = 8000;
    public static final int SAMPLE_RATE = 8000;
    public static final String COMPRESSED_AUDIO_FILE_MIME_TYPE = "audio/mp4a-latm";
    public static final int COMPRESSED_AUDIO_FILE_BIT_RATE = 48000; // 48kbps
    public static final int SHORT_TIME = 5000;
    public static final int PAUSE = 9999;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
}
