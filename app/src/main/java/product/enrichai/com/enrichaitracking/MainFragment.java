package product.enrichai.com.enrichaitracking;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.TwoStatePreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by WELCOME on 6/13/2018.
 */

public class MainFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainFragment.class.getSimpleName();

    private static final int ALARM_MANAGER_INTERVAL = 15000;
    public static boolean STATUS_STARTED = false;

    public static final String KEY_DEVICE = "id";
    public static final String KEY_URL = "url";
    public static final String KEY_INTERVAL = "interval";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_ANGLE = "angle";
    public static final String KEY_ACCURACY = "accuracy";
    public static final String KEY_STATUS = "status";

    private static final int PERMISSIONS_REQUEST_LOCATION = 2;

    private SharedPreferences sharedPreferences;

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        addPreferencesFromResource(R.xml.preferences);
        initPreferences();

        findPreference(KEY_INTERVAL).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue != null) {
                    try {
                        int value = Integer.parseInt((String) newValue);
                        return value > 0;
                    } catch (NumberFormatException e) {
                        Log.w(TAG, e);
                    }
                }
                return false;
            }
        });

        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(getActivity(), AutostartReceiver.class), 0);

        if (sharedPreferences.getBoolean(KEY_STATUS, false)) {
            STATUS_STARTED = true;
            Log.d("STATUS_STARTED", STATUS_STARTED+"");
            startTrackingService(true, false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            startTrackingService(false, granted);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_STATUS)) {
            if (sharedPreferences.getBoolean(KEY_STATUS, false)) {
                startTrackingService(true, false);
            } else {
                stopTrackingService();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.trip) {
            Intent intent = new Intent(getActivity(), StatusActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void startTrackingService(boolean checkPermission, boolean permission) {
        if (checkPermission) {
            Set<String> missingPermissions = new HashSet<>();
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (missingPermissions.isEmpty()) {
                permission = true;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(missingPermissions.toArray(new String[missingPermissions.size()]),
                            PERMISSIONS_REQUEST_LOCATION);
                }
                return;
            }
        }

        if (permission) {
            setPreferencesEnabled(false);
            ContextCompat.startForegroundService(getActivity(), new Intent(getActivity(), TrackingService.class));
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,ALARM_MANAGER_INTERVAL, ALARM_MANAGER_INTERVAL, alarmIntent);
        } else {
            sharedPreferences.edit().putBoolean(KEY_STATUS, false).apply();
            TwoStatePreference preference = (TwoStatePreference) findPreference(KEY_STATUS);
            preference.setChecked(false);
        }
    }

    private void stopTrackingService() {
        STATUS_STARTED = false;
        Log.d("STATUS_STARTED", STATUS_STARTED+"");
        alarmManager.cancel(alarmIntent);
        getActivity().stopService(new Intent(getActivity(), TrackingService.class));
        setPreferencesEnabled(true);
    }

    private void setPreferencesEnabled(boolean enabled) {
        findPreference(KEY_INTERVAL).setEnabled(enabled);
        findPreference(KEY_ACCURACY).setEnabled(enabled);
    }

    private void initPreferences() {
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);

        if (!sharedPreferences.contains(KEY_DEVICE)) {
            //String id = String.valueOf(new Random().nextInt(900000) + 100000);
            String id = "358023072487895";
            sharedPreferences.edit().putString(KEY_DEVICE, id).apply();
            ((EditTextPreference) findPreference(KEY_DEVICE)).setText(id);
        }
        findPreference(KEY_DEVICE).setSummary(sharedPreferences.getString(KEY_DEVICE, null));
    }
}
