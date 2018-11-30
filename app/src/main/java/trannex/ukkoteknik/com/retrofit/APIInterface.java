package trannex.ukkoteknik.com.retrofit;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;


public interface APIInterface {
    @GET("api/trannex/user_batches")
    Call<JsonObject> getBatches(@Query("user_id") String userId);

    @GET("api/mediaserver/assets")
    Call<JsonObject> getAssets(@Query("asset_id") String userId);

    @POST("api/trannex/device_reports")
    Call<JsonObject> postData(@Body JsonObject syncObject);

    @GET("mediaserver/download/{assetName}")
    @Streaming
    Call<ResponseBody> downloadZip(@Path(value = "assetName") String assetName);

}
