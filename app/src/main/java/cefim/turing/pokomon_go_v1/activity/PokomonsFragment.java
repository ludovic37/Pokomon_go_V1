package cefim.turing.pokomon_go_v1.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cefim.turing.pokomon_go_v1.R;
import cefim.turing.pokomon_go_v1.interfaces.APICallback;
import cefim.turing.pokomon_go_v1.models.Pokomon;
import cefim.turing.pokomon_go_v1.utils.UtilsAPI;
import cefim.turing.pokomon_go_v1.utils.UtilsPreferences;
import okhttp3.Response;

/**
 * Created by crespeau on 13/04/2018.
 */

public class PokomonsFragment extends Fragment implements APICallback{

    private RecyclerView mRecyclerView;

    private ArrayList<Pokomon> mPokomons;

    private Handler mHandler;

    private MyPokomonAdapter myPokomonAdapter;

    public PokomonsFragment() {
    }

    public static PokomonsFragment newInstance() {
        return new PokomonsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("> PokomonFragment","onCreate Frag");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("> OnCreateView","onCreateView Frag");
        Log.d("lol", "MapFragment : onCreateView");

        mPokomons = new ArrayList<>();
        mHandler = new Handler();

        View view = inflater.inflate(R.layout.fragment_pokomon_grid, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(layoutManager);

        myPokomonAdapter = new MyPokomonAdapter(getContext(), mPokomons);

        mRecyclerView.setAdapter(myPokomonAdapter);

        UtilsAPI.getInstance().get(this, UtilsAPI.URL_MY_POKOMONS, "{}", UtilsPreferences.getPreferences(getContext()).getString("token"), 0);

        return view;
    }

    @Override
    public void successCallback(Response response, int code) throws IOException {
        Log.d("> ", "success Calback" );

        final String stringJSON = response.body().string();

        Log.d("DETAIL","POKOMON : "+stringJSON);

        try {
            JSONArray jsonArrayAllPokomons = new JSONArray(stringJSON);

            for (int i = 0; i < jsonArrayAllPokomons.length(); i++){
                JSONObject jsonObjectItem = jsonArrayAllPokomons.getJSONObject(i);
                Gson gson = new Gson();
                Pokomon pokomon = gson.fromJson(jsonObjectItem.toString(), Pokomon.class);
                mPokomons.add(pokomon);

            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    myPokomonAdapter.notifyDataSetChanged();

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    @Override
    public void failCallback(Response response, int code) {

    }
}