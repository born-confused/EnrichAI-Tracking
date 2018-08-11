package product.enrichai.com.enrichaitracking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by WELCOME on 6/13/2018.
 */

public class TrackingController implements PositionProvider.PositionListener, NetworkManager.NetworkHandler  {

    private static final String TAG = TrackingController.class.getSimpleName();
    private static final int RETRY_DELAY = 30 * 1000; // 30 seconds for retry
    private static final int WAKE_LOCK_TIMEOUT = 120 * 1000; // Keeps CPU ON for 2 mins

    private boolean isOnline;
    private boolean isWaiting;

    private Context context;
    private Handler handler;
    private SharedPreferences preferences;

    private String url;

    private PositionProvider positionProvider;
    public DatabaseHelper databaseHelper;
    private NetworkManager networkManager;

    private PowerManager.WakeLock wakeLock;

    private void lock() {
        wakeLock.acquire(WAKE_LOCK_TIMEOUT);
    }

    private void unlock() {
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    public TrackingController(Context context) {

        this.context = context;
        handler = new Handler();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        positionProvider = new PositionProvider(context, this);
        databaseHelper = new DatabaseHelper(context);
        networkManager = new NetworkManager(context, this);
        isOnline = networkManager.isOnline();

        url = preferences.getString(MainFragment.KEY_URL, context.getString(R.string.settings_url_default_value));

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
    }

    public void start() {
        if (isOnline) {
            read();
        }
        try {
            positionProvider.startUpdates();
        } catch (SecurityException e) {
            Log.w(TAG, e);
        }
        networkManager.start();
    }

    public void stop() {
        Log.d(TAG, "File called ");
        networkManager.stop();
        try {
            positionProvider.stopUpdates();
        } catch (SecurityException e) {
            Log.w(TAG, e);
        }
        handler.removeCallbacksAndMessages(null);

    }

    private void log(String action, Position position) {
        if (position != null) {
            action += " (" +
                    "id:" + position.getId() +
                    " time:" + position.getTime().getTime() / 1000 +
                    " lat:" + position.getLatitude() +
                    " lon:" + position.getLongitude() + ")";
        }
        Log.d(TAG, action);
    }

    private void write(Position position) {
        log("write " + TAG, position);
        lock();
        databaseHelper.insertPositionAsync(position, new DatabaseHelper.DatabaseHandler<Void>() {
            @Override
            public void onComplete(boolean success, Void result) {
                if (success) {
                    if (isOnline && isWaiting) {
                        read();
                        isWaiting = false;
                    }
                }
                unlock();
            }
        });
    }

    private void read() {
        log("read " + TAG, null);
        lock();
        databaseHelper.selectPositionAsync(new DatabaseHelper.DatabaseHandler<Position>() {
            @Override
            public void onComplete(boolean success, Position result) {
                if (success) {
                    if (result != null) {
                        if (result.getDeviceId().equals(preferences.getString(MainFragment.KEY_DEVICE, null)))
                            send(result);
                    } else {
                        isWaiting = true;
                    }
                } else {
                    retry();
                }
                unlock();
            }
        });
    }

    private void delete(Position position) {
        log("delete " + TAG, position);
        lock();
        databaseHelper.deletePositionAsync(position.getId(), new DatabaseHelper.DatabaseHandler<Void>() {
            @Override
            public void onComplete(boolean success, Void result) {
                if (success) {
                    read();
                } else {
                    retry();
                }
                unlock();
            }
        });
    }

    private void send(final Position position) {
        log("send " + TAG, position);
        lock();
        APIClient.getSingletonApiClient().sendUpdate(position, new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                if(response.code() == 200)
                    delete(position);
                else
                    retry();
                unlock();
            }
            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Log.d(TAG, "Failed in connection");
                retry();
                unlock();
            }

        });
    }

    private void retry() {
        log("retry " + TAG, null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isOnline) {
                    read();
                }
            }
        }, RETRY_DELAY);
    }


    @Override
    public void onNetworkUpdate(boolean isOnline) {
        int message = isOnline ? R.string.status_network_online : R.string.status_network_offline;
        if (!this.isOnline && isOnline) {
            read();
        }
        this.isOnline = isOnline;
    }

    @Override
    public void onPositionUpdate(Position position) {
        if (position != null) {
            write(position);
        }
    }
}
