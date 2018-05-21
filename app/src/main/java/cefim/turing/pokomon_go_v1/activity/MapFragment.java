package cefim.turing.pokomon_go_v1.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cefim.turing.pokomon_go_v1.R;
import cefim.turing.pokomon_go_v1.interfaces.APICallback;
import cefim.turing.pokomon_go_v1.models.Pokomon;
import cefim.turing.pokomon_go_v1.models.Pokostop;
import cefim.turing.pokomon_go_v1.utils.Utils;
import cefim.turing.pokomon_go_v1.utils.UtilsAPI;
import cefim.turing.pokomon_go_v1.utils.UtilsPreferences;
import okhttp3.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback, APICallback {

    private static final int INTERVAL_REQUEST = 30000;

    private Date mLastUpdatedDate;
    private List<Pokomon> mPokomonList;
    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();

    private Location mCurrentLocation;

    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private Handler mHandler;
    private Date lastDate;

    private Marker markerPlayer;
    private List<Marker> markerList = new ArrayList<>();


    public MapFragment() {
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("lol", "onCreate Frag");

        lastDate = new Date();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("lol", "MapFragment : onCreateView");

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mHandler = new Handler();
        mPokomonList = new ArrayList<>();

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        getItem();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false); // desactive le zoom / dézoom
        Log.d("lol", "onMapReady Frag");
        // TODO : ...
        //Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(x, y).icon(BitmapDescriptorFactory.fromResource(R.drawable.profile_pokomon_48)));


    }

    public void onLocationChanged(Location location) {
        Log.d("lol", "onLocationChanged Frag");

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //clear all marker
        //mGoogleMap.clear();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        if (markerPlayer == null){
            markerPlayer = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.profile_pokomon_48)));
            markerPlayer.setTag("user");
        }else {
            markerPlayer.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        }

        //Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.profile_pokomon_48)));
        //marker.setTag("user");


        Log.d("lol", "---> onLocationChange : " + location.toString());

        if (isTimeToUpdate()) {

            Log.d("lol", "---> onLocationChange : On y va");

            Object[] paramsValues = {location.getLatitude(), location.getLongitude()};

            Log.d("LATITUDE", location.getLatitude() + "");
            Log.d("Longitude", location.getLongitude() + "");
            Log.d("token", UtilsPreferences.getPreferences(getContext()).getString("token"));

            //Object[] paramsValues = {String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude())};
            String params = String.format(UtilsAPI.URL_MAP_PARAMS, paramsValues);

            UtilsAPI.getInstance().get(this, UtilsAPI.URL_MAP, params, UtilsPreferences.getPreferences(getContext()).getString("token"),0);
            //Object[] paramsValues = {String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude())};
            // Object[] paramsValues = {String.valueOf(47.397026), String.valueOf(0.689860)};

        } else {
            Log.d("lol", "---> onLocationChange : On attend encore un peu");
        }
    }


    /**
     * @return true s'il y a plus de INTERVAL_REQUEST (30s) depuis la dernière mise à jour
     */
    public boolean isTimeToUpdate() {

        Log.d("lol", "isTimeToUpdate Frag");
        Date currentDate = new Date(System.currentTimeMillis());

        if ((currentDate.getTime() - lastDate.getTime()) >= 10000) {
            lastDate = currentDate;
            return true;
        }

        return false;
    }

    private void updateMapMarkers(final List<Pokomon> pokomons, final List<Pokostop> pokostops) {
        Log.d("lol", "updateMapMarkers Frag");
        // TODO : ...
        // Indice
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if (mGoogleMap != null){


                    for (Marker marker: markerList){
                        marker.remove();
                    }


                    for (Pokomon pokomon : pokomons) {
                        Marker markerpokomon = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(pokomon.coordinates[0], pokomon.coordinates[1]))
                                .title(pokomon._id)
                                .icon(BitmapDescriptorFactory.fromResource(Utils.TAB_IMAGE_POKOMON_S[pokomon.number])));

                        markerpokomon.setTag("pokomon");
                        markerList.add(markerpokomon);
                    }

                    for (Pokostop pokostop : pokostops) {
                        Marker markerpokostop = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(pokostop.coordinates[0], pokostop.coordinates[1]))
                                .title(pokostop._id)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pokestop_blue_48)));

                        markerpokostop.setTag("pokostop");
                        markerList.add(markerpokostop);
                    }


                    mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {

                            if (((String) marker.getTag()).equals("pokomon")) {

                                String idPokomon = marker.getTitle();
                                String url = String.format(UtilsAPI.URL_POKOMON, idPokomon);
                                UtilsAPI.getInstance().get(MapFragment.this, url, null, UtilsPreferences.getPreferences(getContext()).getString("token"),1);

                            } else if (((String) marker.getTag()).equals("pokostop")) {

                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<Pokostop>>() {}.getType();
                                String jsonResult = gson.toJson(pokostops, listType);

                                Intent intent = new Intent(getContext(), PokostopActivity.class);
                                intent.putExtra("idPokostop", marker.getTitle());
                                //intent.putExtra("pokostops", pokostops.toString());
                                intent.putExtra("pokostops", jsonResult);
                                startActivity(intent);
                            }
                            return false;
                        }
                    });
                }


            }
        });

    }


    @Override
    public void successCallback(final Response response, final int code) throws IOException {


        Log.d("lol", "successCallback Frag");

        final String stringJson = response.body().string();

        Log.d("lol", "---------> Success - " + stringJson);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {

                    if (code == 0) {
                        Toast.makeText(getContext(), "GET-MAP", Toast.LENGTH_SHORT).show();
                        JSONObject jsonObject = new JSONObject(stringJson);

                        Gson gson = new Gson();
                        Type founderListType = new TypeToken<ArrayList<Pokomon>>() {}.getType();
                        List<Pokomon> pokomonList = gson.fromJson(jsonObject.get("pokomons").toString(), founderListType);

                        Type founderListTypePokostop = new TypeToken<ArrayList<Pokostop>>() {
                        }.getType();
                        List<Pokostop> pokostopListList = gson.fromJson(jsonObject.get("pokostops").toString(), founderListTypePokostop);


                        //for (Pokomon pokomon: pokomonList) {
                        updateMapMarkers(pokomonList, pokostopListList);
                        //}


                        //List<Pokomon> pokomonList = jsonObject.get("pokomons");
                        //List<Pokomon> pokomonList = (Location) gson.fromJson(input.getJSONObject(i).toString(), Location.class);;


                        // TODO


                    } else if(code == 1) {

                        Toast.makeText(getContext(), "GET-POKOMON", Toast.LENGTH_SHORT).show();
                        JSONObject jsonObject = new JSONObject(stringJson);

                        Gson gson = new Gson();
                        Pokomon pokomon = gson.fromJson(jsonObject.toString(), Pokomon.class);


                        Intent intent = new Intent(getContext(), CatchActivity.class);
                        //intent.putExtra("idPokomon", pokomon._id);
                        intent.putExtra("pokomon", jsonObject.toString());
                        startActivity(intent);
                    }else {
                        //item
                        Toast.makeText(getContext(), "_____GET-ITEM_____", Toast.LENGTH_SHORT).show();

                        UtilsPreferences.getPreferences(getContext()).setKey("allitems",stringJson);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public void failCallback(final Response response, int code) {

        Log.d("lol", "failCallback Frag");

        if (response != null) {
            final int g = response.code();

            Log.d("lol", "---------> Fail - " + g + " - " + response.message());

            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    //TODO : ?
                    switch (g) {
                        case 401:
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            break;
                        default:
                    }
                }
            });
        }
    }

    public void getItem(){
        if (UtilsPreferences.getPreferences(getContext()).getString("token") != null)
            UtilsAPI.getInstance().get(this,UtilsAPI.URL_BAG,null,UtilsPreferences.getPreferences(getContext()).getString("token"),2);
    }
}
