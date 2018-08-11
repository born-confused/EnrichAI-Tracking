package product.enrichai.com.enrichaitracking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by WELCOME on 6/13/2018.
 */

public class AutostartReceiver extends WakefulBroadcastReceiver  {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.getBoolean(MainFragment.KEY_STATUS, false)) {
            //startWakefulForegroundService(context, new Intent(context, TrackingService.class));
        }
    }
}
