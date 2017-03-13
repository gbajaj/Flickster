package codepath.gauravbajaj.com.flickster;

import android.app.Application;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;

/**
 * Created by gauravb on 3/12/17.
 */

public class Flickster extends Application {
    public OkHttpClient client;
    public Picasso picasso;
    private static Flickster instance;

    public Flickster() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        client = new OkHttpClient();
        picasso = new Picasso.Builder(this).downloader(new OkHttp3Downloader(client)).build();
    }

    public static Flickster instance() {
        return instance;
    }
}
