package product.enrichai.com.enrichaitracking;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by WELCOME on 6/19/2018.
 */

public interface TrackingAPI {

    @POST("tracker")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
    })
    Call<APIResponse> sendPosition(@Body Position position);

    @POST("tracker/postData")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
    })
    Call<APIResponse> sendData(@Body Position position);

}
