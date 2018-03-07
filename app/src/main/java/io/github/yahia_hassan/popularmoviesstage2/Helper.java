package io.github.yahia_hassan.popularmoviesstage2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.CONNECTIVITY_SERVICE;


public class Helper {

    /**
     * Helper method that creates a new JSONObject from String.
     * @param jsonString
     * @return JSONObject or null if JSONException was thrown.
     */
    public static JSONObject createJSONObjectFromString (String TAG, String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException caught at createJSONObjectFromString: " + e);
            e.printStackTrace();
        }
        return jsonObject;
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
