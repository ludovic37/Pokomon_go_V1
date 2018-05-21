package cefim.turing.pokomon_go_v1.interfaces;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by peterbardu on 3/2/17.
 */

public interface APICallback {

    void successCallback(final Response response, int code) throws IOException;

    void failCallback(final Response response, int code);
}
