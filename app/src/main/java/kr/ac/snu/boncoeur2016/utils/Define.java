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

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    public static final int REVISION = 8888;

    public static final String RECORDED_FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BonCoeur/";

    public static final int SHORT_TIME = 5000;
    public static final int PAUSE = 9999;
}
