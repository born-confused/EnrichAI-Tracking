package product.enrichai.com.enrichaitracking;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by WELCOME on 6/13/2018.
 */

public class TrackingService extends Service {


    private static final String TAG = TrackingService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1;

    private TrackingController trackingController;

    private static Notification createNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.drawable.ic_stat_notify)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(Notification.CATEGORY_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        builder.setContentTitle(context.getString(R.string.settings_status_on_summary))
                .setTicker(context.getString(R.string.settings_status_on_summary))
                .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

        builder.setContentIntent(PendingIntent.getActivity(context, 0, intent, 0));
        return builder.build();
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "service create");
        //StatusActivity.addMessage(getString(R.string.status_service_create));

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            trackingController = new TrackingController(this);
            trackingController.start();

        }

        startForeground(NOTIFICATION_ID, createNotification(this));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            AutostartReceiver.completeWakefulIntent(intent);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "service destroy");
        stopForeground(true);
        if (trackingController != null) {
            trackingController.stop();
        }
    }
}
