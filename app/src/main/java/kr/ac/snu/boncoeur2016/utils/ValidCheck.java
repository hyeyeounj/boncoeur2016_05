package kr.ac.snu.boncoeur2016.utils;

import android.text.TextUtils;

/**
 * Created by hyes on 2016. 3. 16..
 */
public class ValidCheck {

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
