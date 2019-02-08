package trannex.ukkoteknik.com.singleton;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
public class MyApp extends MultiDexApplication {
    public static APIInterface apiInterface;
    public static Gson gson;
    public static MyApp instance;
    public static DatabaseHelper mDatabaseHelper;

    public static Timestamp getCurrentTimeStamp() {
        Timestamp timestamp = new Timestamp(Calendar.getInstance(Locale.ENGLISH).getTimeInMillis());
        return timestamp;
    }

    public static Date getDate() {
        Calendar c = Calendar.getInstance();
        /*c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);*/
        return new Date(c.getTimeInMillis());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        gson = new GsonBuilder().setExclusionStrategies(new AnnotationExclusionStrategy())
                .serializeNulls().setDateFormat(Constants.TIMESTAMP_FORMAT).create();

        apiInterface = APIClient.getClient().create(APIInterface.class);

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
