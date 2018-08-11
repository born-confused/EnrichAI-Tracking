package product.enrichai.com.enrichaitracking;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by WELCOME on 6/19/2018.
 */

public class APIClient {

    private static final String BASE_URL = "http://192.168.43.122:8080/";

    private static APIClient apiClient;

    private static final String TAG = APIClient.class.getSimpleName();

    private static Retrofit retrofit = null;

    private static final Object mLock = new Object();

    public APIClient() {
    }

    public static APIClient getSingletonApiClient() {
        synchronized (mLock) {
            if (apiClient == null)
                apiClient = new APIClient();

            return apiClient;
        }
    }

    private static Retrofit getClient() {
        if (retrofit == null) {
            //OkHttpClient.Builder client = new OkHttpClient.Builder();

            OkHttpClient.Builder okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(60 * 2, TimeUnit.SECONDS)
                    .readTimeout(60 * 3, TimeUnit.SECONDS)
                    .writeTimeout(60 * 3, TimeUnit.SECONDS);

            GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient.build())
                    .addConverterFactory(gsonConverterFactory)
                    .build();

        }
        return retrofit;
    }

    public void sendUpdate(Position request, Callback<APIResponse> callback) {
        Call<APIResponse> call = null;
        try {
            TrackingAPI apiService =  APIClient.getClient().create(TrackingAPI.class);

            Log.d(TAG, request.toString());
            //call = apiService.sendPosition(request);
            call = apiService.sendData(request);
            call.enqueue(callback);
        } catch (Throwable e) {
            Log.e(TAG, e.toString(), e);
            Log.d("API Failure", e.getMessage());
            callback.onFailure(call, e);
        }
    }

}
