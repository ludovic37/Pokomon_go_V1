package cefim.turing.pokomon_go_v1.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

import cefim.turing.pokomon_go_v1.R;
import cefim.turing.pokomon_go_v1.utils.UtilsPreferences;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    protected static final String TAG = "location-updates-sample";

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;


    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    /**
     * ME
     */
    private static final String EXTRA_ITEM_SELECTED = "extra_item_selected";

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private int mItemSelected;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO : Testez l'authentification de l'utilisateur
        UtilsPreferences utilsPreferences = UtilsPreferences.getPreferences(this);

        String token = utilsPreferences.getString("token");

        if (token == null){
            startActivity(new Intent(this, LoginActivity.class));
        }

        Log.d("lol","oncreate");


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mRequestingLocationUpdates = true;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices API.
        buildGoogleApiClient();

        // Update UI, create first MapFragment
        // TODO : Appelez la méthode de mise à jour du fragment
        updateUI();

    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {

        Log.d("lol","onLocationChanged");

        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        Toast.makeText(this, getResources().getString(R.string.location_updated_message), Toast.LENGTH_SHORT).show();

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout_content);
        if (fragment instanceof MapFragment)
            ((MapFragment) fragment).onLocationChanged(location);
    }


    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {

        Log.d("lol","onSaveInstanceState");

        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("lol","onRequestPermissionsResult");
        Log.d("lol","");
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults.length > 1){
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
                        Log.d("lol","onRequestPermissionsResult GOOD");
                        startLocationUpdates();
                    } else {
                        // Permission Denied
                        Log.d("lol","onRequestPermissionsResult NOT GOOD");
                        Toast.makeText(MainActivity.this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(this, LoginActivity.class));
                    }
                }else {
                    // Permission Denied
                    Log.d("lol","onRequestPermissionsResult NOT GOOD");
                    Toast.makeText(MainActivity.this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(this, LoginActivity.class));
                }


                Log.d("lol","COUCOU");
                break;
            default:

                Log.d("lol","onRequestPermissionsResult Default");
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("lol","onBackPressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1)
                finish();
            else {
                getSupportFragmentManager().popBackStack();
                mItemSelected = 0;
                setTitle(getResources().getStringArray(R.array.menu_item)[mItemSelected]);
                mNavigationView.getMenu().getItem(mItemSelected).setChecked(true);
            }
        }
    }

    /**
     * Method called when you click on Item from NavigationDrawer
     *
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d("lol","onNavigationItemSelected");
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_map_fragment:
                mItemSelected = 0;
                break;
            case R.id.nav_pokomons_fragment:
                mItemSelected = 1;
                break;
            case R.id.nav_pokedex_fragment:
                mItemSelected = 2;
                break;
            case R.id.nav_bag_fragment:
                mItemSelected = 3;
                break;
            case R.id.nav_profile_activity:
                mItemSelected = 4;
                break;
            default:
                mItemSelected = 0;
        }

        updateUI();

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void updateUI() {
        Log.d("lol","updateUI");

        setFragment();
        setTitle(getResources().getStringArray(R.array.menu_item)[mItemSelected]);
        mNavigationView.getMenu().getItem(mItemSelected).setChecked(true);
    }

    private void setFragment() {

        Log.d("lol","setFragment");
        Class fragmentClass = null;
        switch (mItemSelected) {
            case 0:
                fragmentClass = MapFragment.class;
                break;
            case 1:
                fragmentClass = PokomonsFragment.class;
                break;
            case 2:
                fragmentClass = PokodexFragment.class;
                break;
            case 3:
                fragmentClass = BagFragment.class;
                break;
            case 4:
                fragmentClass = ProfileFragment.class;
                break;
            default:
                fragmentClass = MapFragment.class;
        }

        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout_content, fragment);
            fragmentTransaction.commit();
            /*
            if (fragmentClass != null) {
                if (fragmentClass == MapFragment.class && getSupportFragmentManager().getBackStackEntryCount() == 2) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    Fragment fragment = (Fragment) fragmentClass.newInstance();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.add(R.id.frame_layout_content, fragment);
                    if (fragmentClass != MapFragment.class && getSupportFragmentManager().getBackStackEntryCount() == 2)
                        getSupportFragmentManager().popBackStack();
                    fragmentTransaction.addToBackStack(fragment.getClass().toString());
                    fragmentTransaction.commit();
                }
            }
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {

        Log.d("lol","updateValuesFromBundle");
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }

            if (savedInstanceState.keySet().contains(EXTRA_ITEM_SELECTED)) {
                mItemSelected = savedInstanceState.getInt(EXTRA_ITEM_SELECTED, 0);
            }
        } else {
            mItemSelected = 0;
        }

    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {

        Log.d("lol","buildGoogleApiClient");
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        Log.d("lol","createLocationRequest");
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        Log.d("lol","startLocationUpdates");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
            Log.d("lol","if");
        } else {
            Log.d("lol","else");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        Log.d("lol","stopLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // TODO : Connecter le google API Client
        mGoogleApiClient.connect();
        Log.d("lol","onStart");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("lol","onResume");
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            // TODO : Lancez les requêtes de mise à jour de la position
            startLocationUpdates();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("lol","onPause");
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            // TODO : Stopez les requêtes de mise à jour de la position
            stopLocationUpdates();

        }
    }

    @Override
    protected void onStop() {
        // TODO : Déconnecter le google API Client
        mGoogleApiClient.disconnect();
        Log.d("lol","onStop");

        super.onStop();
    }


    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
        Log.d("lol","onConnected");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
            } else {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            }
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }


    @Override
    public void onConnectionSuspended(int cause) {
        Log.d("lol","onConnectionSuspended");
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d("lol","onConnectionFailed");
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }
}


