package trannex.ukkoteknik.com.retrofit;


import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import trannex.ukkoteknik.com.singleton.MyApp;

/**
 * Created by ManojSadhu on 05/01/17.
 */

public class APIClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                //.baseUrl("http://192.168.22.23:8081/ut_devops/utpf/public/")
                .baseUrl("https://stage-all.utpfapps.com/public/")
                .addConverterFactory(GsonConverterFactory.create(MyApp.gson))
                .client(client)
                .build();

        return retrofit;
    }

}
