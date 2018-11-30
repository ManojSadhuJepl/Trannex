package trannex.ukkoteknik.com.singleton;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;

import trannex.ukkoteknik.com.helper.DatabaseHelper;
import trannex.ukkoteknik.com.retrofit.APIClient;
import trannex.ukkoteknik.com.retrofit.APIInterface;
import trannex.ukkoteknik.com.utils.AnnotationExclusionStrategy;

/**
 * Created by  Manoj Sadhu on 7/16/2018.
 */
public class MyApp extends Application {
    public static APIInterface apiInterface;
    public static Gson gson;
    public static MyApp instance;
    public static DatabaseHelper mDatabaseHelper;
    public JsonArray batches;

    public static Timestamp getCurrentTimeStamp() {
        Timestamp timestamp = new Timestamp(Calendar.getInstance(Locale.ENGLISH).getTimeInMillis());
        return timestamp;
    }

    public static Date getDate() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 10);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return new Date(c.getTimeInMillis());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        gson = new GsonBuilder().setExclusionStrategies(new AnnotationExclusionStrategy())
                .setDateFormat("MM/dd/yyyy hh:mm:ss").create();

        apiInterface = APIClient.getClient().create(APIInterface.class);

        batches = new JsonParser().parse(loadJSONFromAsset("sample.json")).getAsJsonArray();
        mDatabaseHelper = new DatabaseHelper(this, "Trannex.db");

    }

    public String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }


}
