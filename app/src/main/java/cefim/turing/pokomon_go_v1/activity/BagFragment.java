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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cefim.turing.pokomon_go_v1.R;
import cefim.turing.pokomon_go_v1.interfaces.APICallback;
import cefim.turing.pokomon_go_v1.models.Bag;
import cefim.turing.pokomon_go_v1.utils.UtilsAPI;
import cefim.turing.pokomon_go_v1.utils.UtilsPreferences;
import okhttp3.Response;

/**
 * Created by crespeau on 13/04/2018.
 */

public class BagFragment extends Fragment implements APICallback {


    private RecyclerView mRecyclerView;

    private ArrayList<Bag> mBags;

    private Handler mHandler;

    private BagAdapter bagAdapter;


    public BagFragment() {
    }

    public static BagFragment newInstance() {
        return new BagFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("lol","onCreate Frag");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("lol","onCreateView Frag");
        Log.d("lol", "MapFragment : onCreateView");

        mBags = new ArrayList<>();
        mHandler = new Handler();

        View view = inflater.inflate(R.layout.fragment_bag, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewBag);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        bagAdapter = new BagAdapter(getContext(), mBags);

        mRecyclerView.setAdapter(bagAdapter);

        UtilsAPI.getInstance().get(this, UtilsAPI.URL_BAG,"{}", UtilsPreferences.getPreferences(getContext()).getString("token"),0);

        return view;
    }

    @Override
    public void successCallback(Response response, int code) throws IOException {

        final String stringJson = response.body().string();
        try {

            JSONArray jsonArrayBags = new JSONArray(stringJson);

            for (int i = 0; i < jsonArrayBags.length(); i++) {

                JSONObject jsonObjectItem = jsonArrayBags.getJSONObject(i);

                Bag bag = new Bag(jsonObjectItem);
                //Gson gson = new Gson();
                //Bag bag = gson.fromJson(jsonObjectItem.toString(), Bag.class);
                mBags.add(bag);
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    bagAdapter.notifyDataSetChanged();
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
