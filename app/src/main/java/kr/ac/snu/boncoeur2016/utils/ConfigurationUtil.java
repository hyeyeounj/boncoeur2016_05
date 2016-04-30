package kr.ac.snu.boncoeur2016.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by hyes on 2016. 4. 30..
 */
public class ConfigurationUtil {
    private static final String TAG = "test";

    public static int getH(Context context){
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static int getW(Context context){
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }


    public static void check(Context context){
        Display display = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        String displayName = display.getName();  // minSdkVersion=17+
        Log.i(TAG, "displayName  = " + displayName);

// display size in pixels
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Log.i(TAG, "width        = " + width);
        Log.i(TAG, "height       = " + height);

// pixels, dpi
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        int heightPixels = metrics.heightPixels;
        int widthPixels = metrics.widthPixels;
        int densityDpi = metrics.densityDpi;
        float xdpi = metrics.xdpi;
        float ydpi = metrics.ydpi;
        Log.i(TAG, "widthPixels  = " + widthPixels);
        Log.i(TAG, "heightPixels = " + heightPixels);
        Log.i(TAG, "densityDpi   = " + densityDpi);
        Log.i(TAG, "xdpi         = " + xdpi);
        Log.i(TAG, "ydpi         = " + ydpi);

// deprecated
        int screenHeight = display.getHeight();
        int screenWidth = display.getWidth();
        Log.i(TAG, "screenHeight = " + screenHeight);
        Log.i(TAG, "screenWidth  = " + screenWidth);

//// orientation (either ORIENTATION_LANDSCAPE, ORIENTATION_PORTRAIT)
//        int orientation = getResources().getConfiguration().orientation;
//        Log.i(TAG, "orientation  = " + orientation);
    }
}
