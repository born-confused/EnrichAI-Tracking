package product.enrichai.com.enrichaitracking;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by WELCOME on 6/19/2018.
 */

public class APIResponse implements Serializable {
    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "EndTripResponse{" +
                "status='" + status + '\'' +
                '}';
    }
}
