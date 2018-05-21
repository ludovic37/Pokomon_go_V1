package cefim.turing.pokomon_go_v1.utils;

import android.util.Log;

import java.io.IOException;

import cefim.turing.pokomon_go_v1.interfaces.APICallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by peterbardu on 3/2/17.
 */

public class UtilsAPI {


    public static final String URL_BASE = "https://ludo-pokomon-go.herokuapp.com/";
    //public static final String URL_BASE = "https://jerome-pokomon.herokuapp.com/";

    public static final String URL_SIGN_UP = URL_BASE+"signup";
    public static final String URL_SIGN_UP_PARAMS = "{\"email\":\"%s\", \"password\":\"%s\", \"username\":\"%s\"}";

    public static final String URL_LOGIN = URL_BASE+"login";
    public static final String URL_LOGIN_PARAMS = "{\"email\":\"%s\", \"password\":\"%s\"}";

    public static final String URL_MAP = URL_BASE+"map";
    public static final String URL_MAP_PARAMS = "lat=%s&lng=%s";

    public static final String URL_POKOMON = URL_BASE+"pokomon/%s";
    public static final String URL_POKOMON_PARAMS = "{\"id\":\"%s\"}";

    public static final String URL_POKOSTOP = URL_BASE+"pokostop/%s";
    //public static final String URL_POKOSTOP_PARAMS = "{\"id\":\"%s\"}";


    public static final String URL_MY_POKOMONS = URL_BASE+"me/pokomon";
    public static final String URL_POKODEX = URL_BASE+"me/pokodex";

    public static final String URL_BAG = URL_BASE+"me/bag";
    public static final String URL_BAG_PARAMS = "{\"item\":{\"_id\":\"%s\",\"name\":\"%s\"},\"amount\":%s}";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient mOkHttpClient;

    private static UtilsAPI INSTANCE;

    private UtilsAPI() {
        mOkHttpClient = new OkHttpClient();
    }

    public static UtilsAPI getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new UtilsAPI();
        }

        return INSTANCE;
    }

    public void get(final APICallback activity, String url, String params, String token, final int code) {

        if (params != null)
            url += "?" + params;

        Log.d("lol", "-----> get url    : " + url);

        Request request = new Request.Builder()
                .addHeader("Authorization", token)
                .url(url)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                activity.failCallback(null,code);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (response.isSuccessful()) {
                    activity.successCallback(response, code);
                } else {
                    activity.failCallback(response, code);
                }
            }
        });
    }

    public void post(final APICallback activity, String url, String params, String token, final int code) throws IOException {

        Log.d("lol", "-----> post url    : " + url);
        Log.d("lol", "-----> post params : " + params);

        RequestBody body = RequestBody.create(JSON, params);
        Request request = new Request.Builder()
                .addHeader("Authorization", token)
                .url(url)
                .post(body)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                activity.failCallback(null, code);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (response.isSuccessful()) {
                    activity.successCallback(response, code);
                } else {
                    activity.failCallback(response, code);
                }
            }
        });
    }

    //private static final String URL_SIGN_UP = "{\"email\":\"peter.bardu@gmail.com\", \"password\":\"1234\", \"username\":\"pbardu\"}";
    //{"profile":{"_id":"58b81497a8ed0b0004061428","username":"pbardu","picture":"https://s.gravatar.com/avatar/a86d6484caed241d016c5e514673c9d6","email":"peter.bardu@gmail.com","candy":0},"token":"eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoMCIsIl9pZCI6IjU4YjgxNDk3YThlZDBiMDAwNDA2MTQyOCJ9.gK8bqpPxSYON9HuFA0muyvzp_KRbaYBxnjCJkPsnShs"}
}
