package pj.nearme;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.ahmadrosid.lib.drawroutemap.DrawRouteMaps;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<Status> {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private final int REQ_PERMISSION =1;
    Double  LATITUDE, LONGITUDE; float RADIUS;
    private Location lastLocation;
    String[] name_ar,sex_ar,lat,lng;
    String[] name_500,sex_500,dist_500,lat_500,lng_500;
    String[] name_a,sex_a,dist_a,lat_a,lng_a;
    static ArrayList data500;
    static ArrayList data;
    LatLng my_loc;
    ActionBar t;
    ProgressBar pb;
    String name,sex;
    int cnt=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        t = getSupportActionBar();
        Log.d(TAG, "createGoogleApi()");
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        pb = (ProgressBar) findViewById(R.id.pbar);
        Intent i = getIntent();
        name = i.getStringExtra("name");
        sex = i.getStringExtra("sex");
        if(name==null)
        {
            final SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            name = sharedPreferences.getString("name","NOT_PRESENT");
            sex = sharedPreferences.getString("sex","NOT_PRESENT");
        }
        t.setTitle(" Near");
        t.setSubtitle(" "+name);

        if(sex.equals("M"))
            t.setLogo(R.mipmap.ic_male_logo);
        else if(sex.equals("F"))
            t.setLogo(R.mipmap.ic_female_logo);

        t.setDisplayUseLogoEnabled(true);
        t.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.geo500: {
                startGeofence();
                return true;
            }
            case R.id.menudata:
            {
                Intent i = new Intent(MapsActivity.this,ShowDetails.class);
                i.putExtra("data500",data500);
                i.putExtra("data",data);
                startActivity(i);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "onMapClick("+latLng +")");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClickListener: " + marker.getPosition() );
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();


        googleApiClient.connect();
    }

        @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected()");

        int off = 0;
        try {
            off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (off == 0) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                final String message = "Please enable GPS to continue.";

                builder.setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
                builder.create().show();

            }
            else
            {
                getLastKnownLocation();
            }


        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation()");
        if ( checkPermission() ) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if ( lastLocation != null ) {
                Log.i(TAG, "LasKnown location. " +
                        "Long: " + lastLocation.getLongitude() +
                        " | Lat: " + lastLocation.getLatitude());
                writeLastLocation();
                startLocationUpdates();
            } else {
                Log.w(TAG, "No location retrieved yet");
                getLastKnownLocation();
                startLocationUpdates();
            }
        }
        else askPermission();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed()");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged ["+location+"]");
        lastLocation = location;
        writeActualLocation(location);
    }


    private LocationRequest locationRequest;
    private final int UPDATE_INTERVAL =  3 * 60 * 1000;;
    private final int FASTEST_INTERVAL = 30 * 1000;;

    private void startLocationUpdates(){
        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if ( checkPermission() )
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }


    private void writeActualLocation(Location location) {
        //Toast.makeText(this,"Lat: " + location.getLatitude(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this,"Long: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        LATITUDE = location.getLatitude();
        LONGITUDE = location.getLongitude();

        Send_Loci s = new Send_Loci(MapsActivity.this);
        s.execute(name,sex,LATITUDE.toString(),LONGITUDE.toString());
        my_loc = new LatLng( LATITUDE, LONGITUDE);

        if(cnt==0)
        {
            float zoom = 14.5f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(my_loc, zoom);
            mMap.animateCamera(cameraUpdate);
        }

        markerLocation(my_loc);
    }


    private Marker locationMarker;
    private void markerLocation(LatLng latLng) {
        Log.i(TAG, "markerLocation("+latLng+")");
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person))
                .title(name);
        if ( mMap!=null ) {
            if ( locationMarker != null )
                locationMarker.remove();
            locationMarker = mMap.addMarker(markerOptions);

        }
    }

    private Marker geoFenceMarker;

    private void markerForGeofence(LatLng latLng,String title,String sex) {
        Log.i(TAG, "markerForGeofence("+latLng+")");
        Double distance = distance(LATITUDE,LONGITUDE,latLng.latitude,latLng.longitude);distance = distance * 1000;
        //Toast.makeText(this,distance+"mtrs", Toast.LENGTH_SHORT).show();



        MarkerOptions markerOptions;
        DrawRouteMaps.getInstance(MapsActivity.this).draw(my_loc,latLng,mMap);
        BitmapDescriptor icon = null;

        if(distance<=500 && sex.equals("F")) {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_female_img);

        }
        else if(distance<=500 && sex.equals("M"))
        {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_male_img);
        }
        else if(sex.equals("F"))
        {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_female_out_img);
        }
        else if(sex.equals("M"))
        {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_male_out_img);
        }
        markerOptions = new MarkerOptions().position(latLng).icon(icon).title(title);



        if ( mMap!=null ) {
            // Remove last geoFenceMarker
//            if (geoFenceMarker != null)
//                geoFenceMarker.remove();

            geoFenceMarker = mMap.addMarker(markerOptions);
        }
    }

    private void startGeofence() {
        Log.i(TAG, "startGeofence()");
        if( locationMarker != null ) {
            Geofence geofence = createGeofence( locationMarker.getPosition(), GEOFENCE_RADIUS );
            GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
            addGeofence( geofenceRequest );
        } else {
            Log.e(TAG, "Geofence marker is null");
        }
    }

    PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 1;
    private PendingIntent createGeofencePendingIntent() {

        Log.d(TAG, "createGeofencePendingIntent");
        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;

        Intent intent = new Intent( this, GeofenceTrasitionService.class);
        intent.putExtra("name",name);
        intent.putExtra("data500",data500);
        intent.putExtra("data",data);
        return PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }



    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        if (checkPermission())
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(this);

    }


    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    private static final float GEOFENCE_RADIUS = (float)804.0d;

    private Geofence createGeofence(LatLng latLng, float radius ) {
        Log.d(TAG, "createGeofence");
        return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion( latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration( GEO_DURATION )
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
    }

    private GeofencingRequest createGeofenceRequest(Geofence geofence ) {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofence( geofence )
                .build();
    }


    @Override
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);
        if ( status.isSuccess() ) {
            drawGeofence();
        } else {

        }
    }

    private Circle geoFenceLimits;
    private void drawGeofence() {
        Log.d(TAG, "drawGeofence()");

        if ( geoFenceLimits != null )
            geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()
                .center( locationMarker.getPosition())
                .strokeColor(Color.argb(50, 0,119,181))
                .fillColor(Color.argb(20, 0,119,181))
                .radius( GEOFENCE_RADIUS );
        geoFenceLimits = mMap.addCircle( circleOptions );
    }



    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }

    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    private void askPermission() {

        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION }, REQ_PERMISSION
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch ( requestCode ) {
            case REQ_PERMISSION: {
                if ( grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    getLastKnownLocation();

                } else {
                    permissionsDenied();
                }
                break;
            }
        }
    }

    // App cannot work without the permissions
    private void permissionsDenied() {
        Log.w(TAG, "permissionsDenied()");
    }

    private static final String NOTIFICATION_MSG = "NOTIFICATION MSG";
    // Create a Intent send by the notification
    public static Intent makeNotificationIntent(Context context, String msg) {
        Intent intent = new Intent( context, ShowDetails.class );
        intent.putExtra( NOTIFICATION_MSG, msg );
        intent.putExtra("data500",data500);
        intent.putExtra("data",data);
        return intent;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    public class Send_Loci extends AsyncTask<String, Void, String> {

        Context context;
        String server;


        public Send_Loci(Context context)
        {
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setIndeterminate(true);
        }

        @Override
        protected String doInBackground(String... params) {

            server = context.getString(R.string.server_url);

            String data, link, result;
            BufferedReader bufferedReader;
            try
            {
                link = server+"signup.php";
                String name,sex,lat,lng;
                name = params[0];
                sex = params[1];
                lat = params[2];
                lng = params[3];

                data = "?"+ URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name,"UTF-8");
                data += "&" + URLEncoder.encode("sex", "UTF-8") + "=" + URLEncoder.encode(sex, "UTF-8");
                data += "&" + URLEncoder.encode("lat", "UTF-8") + "=" + URLEncoder.encode(lat, "UTF-8");
                data += "&" + URLEncoder.encode("lng", "UTF-8") + "=" + URLEncoder.encode(lng, "UTF-8");
                link += data;
                URL url = new URL(link);
                Log.i("url",link);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);



                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                result = bufferedReader.readLine();
                bufferedReader.close();
                return result;
            }
            catch (Exception ex)
            {
                //Toast.makeText(context,"Contact developer:"+ex.getMessage(),Toast.LENGTH_LONG).show();
                return new String("Exception : "+ex.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String s) {

            LatLng[] latLngs;
            int sizea=0,size500=0;
            super.onPostExecute(s);
            //Toast.makeText(context,s+"", Toast.LENGTH_SHORT).show();
            String res = s;
            JSONObject jsonObject;
            if(res != null)
            {
                try {
                    Double dist[];Double distance=0.0;
                    jsonObject = new JSONObject(res);
                    JSONArray jsonArray = (JSONArray) jsonObject.get("data");
                    name_ar = new String[jsonArray.length()];
                    sex_ar = new String[jsonArray.length()];
                    lat = new String[jsonArray.length()];
                    lng = new String[jsonArray.length()];
                    dist = new Double[jsonArray.length()];
                    latLngs = new LatLng[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        name_ar[i] = jsonArray.getJSONObject(i).getString("name");
                        sex_ar[i] = jsonArray.getJSONObject(i).getString("sex");
                        lat[i] = jsonArray.getJSONObject(i).getString("lat");
                        lng[i] = jsonArray.getJSONObject(i).getString("lng");
                        latLngs[i] = new LatLng(Double.valueOf(lat[i]),Double.valueOf(lng[i]));
                        distance = distance(LATITUDE,LONGITUDE,Double.valueOf(lat[i]),Double.valueOf(lng[i]));
                        distance = distance * 1000;
                        dist[i]=distance;

                        if(distance<=500)
                        size500++;
                        else
                        sizea++;

                        markerForGeofence(latLngs[i],name_ar[i],sex_ar[i]);
                    }
                    name_500 = new String[size500];
                    dist_500 = new String[size500];
                    lat_500 = new String[size500];
                    lng_500 = new String[size500];
                    sex_500 = new String[size500];

                    name_a = new String[sizea];
                    dist_a = new String[sizea];
                    lat_a = new String[sizea];
                    lng_a = new String[sizea];
                    sex_a = new String[sizea];

                    int j=0,k=0;
                    for(int i=0;i<dist.length;i++)
                    {
                        if(dist[i]<=500)
                        {
                            name_500[j]=name_ar[i];
                            dist_500[j]=dist[i].toString();
                            lat_500[j]=lat[i];
                            lng_500[j]=lng[i];
                            sex_500[j]=sex_ar[i];
                            j++;
                        }
                        else
                        {
                            name_a[k]=name_ar[i];
                            dist_a[k]=dist[i].toString();
                            lat_a[k]=lat[i];
                            lng_a[k]=lng[i];
                            sex_a[k]=sex_ar[i];
                            k++;
                        }
                    }


                    data = new ArrayList<String[]>();
                    data.add(name_a);
                    data.add(dist_a);
                    data.add(lat_a);
                    data.add(lng_a);
                    data.add(sex_a);

                    data500 = new ArrayList<String[]>();
                    data500.add(name_500);
                    data500.add(dist_500);
                    data500.add(lat_500);
                    data500.add(lng_500);
                    data500.add(sex_500);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            pb.setIndeterminate(false);
            pb.setProgress(100);
        }
    }
}
