package axeleration.com.finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import static axeleration.com.finalproject.StaticFunctions.*;

/* Main activity class connects to google api location listener */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    public static Location myCurrentLocation;   // Current location of the user.
    public static final int REQUEST_CODE = 1234;
    public final int QUERY_INTERVAL = 10000;  // Milliseconds
    public final int QUERY_FAST_INTERVAL = 10000;    // Milliseconds
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Cursor cursor;
    private ListView listView;
    private SQLiteDatabase db;
    private Intent intent;  // Intent for switching between activities in the application.
    private TextView noTaskYet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        noTaskYet=findViewById(R.id.noTaskYet);
        /* Request for permissions if the permissions is not granted. */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

        }

        myCurrentLocation = null;

        /* Init each image button in the main activity */
        ImageButton dailyAssignments = findViewById(R.id.daily_assignments);
        ImageButton addTask = findViewById(R.id.addNewTaskBtn);
        ImageButton allClients = findViewById(R.id.allClients);
        ImageButton allTask = findViewById(R.id.allTasksBtn);
        ImageButton finishTasks = findViewById(R.id.finishedTaskBtn);
        ImageButton statisticBtn = findViewById(R.id.statistic);
        listView = findViewById(R.id.listViewMain);

        /* Init to each button on click listener */
        finishTasks.setOnClickListener(this);
        allTask.setOnClickListener(this);
        allClients.setOnClickListener(this);
        addTask.setOnClickListener(this);
        dailyAssignments.setOnClickListener(this);
        statisticBtn.setOnClickListener(this);

        db = DBHelperSingleton.getInstanceDBHelper(this).getReadableDatabase(); // open the db.

        /* Start the service */
        intent = new Intent(MainActivity.this, NotificationService.class);
        startService(intent);

        /* Connect to google API services. */
        if (googleApiClient == null) {
            buildGoogleApi();
        }
        createLocationRequest();    // Location connection settings.
    }

    /* Location connection settings. */
    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(QUERY_INTERVAL);                        // Update the location every 60 seconds.
        locationRequest.setFastestInterval(QUERY_FAST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);        // High priority.
    }

    /* Connect to google API. */
    private void buildGoogleApi() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /* On click listener function */
    @Override
    public void onClick(View v) {
        Intent i = null;
        switch (v.getId()) {
            case R.id.addNewTaskBtn:
                i = new Intent(MainActivity.this, NewCustomer.class);
                break;
            case R.id.allClients:
                i = new Intent(MainActivity.this, AllClients.class);
                break;
            case R.id.allTasksBtn:
                i = new Intent(MainActivity.this, ShowAllTasks.class);
                break;
            case R.id.finishedTaskBtn:
                i = new Intent(MainActivity.this, FinishTasks.class);
                break;
            case R.id.daily_assignments:
                i = new Intent(MainActivity.this, DailyAssignments.class);
                break;
            case R.id.statistic :
                i = new Intent(MainActivity.this, Statistic.class);
                break;
            default:
                Toast.makeText(this, "Activity was not found!", Toast.LENGTH_SHORT).show();
                break;
        }
        if(i != null)
            startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);   // Initialize the location update google api
                    Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        cursor = db.query(Constants.TASKS.TABLE_NAME,   // show the most important task in the activity for today in the range of 3 hours.
                null,
                Constants.TASKS.IS_SIGN + "=? AND " + Constants.TASKS.DATETIME + ">=? AND " +  Constants.TASKS.DATETIME + "<=?",
                new String[] {"0", getCurrentDate("yyyy-MM-dd HH:mm:ss"), getFutureDate("yyyy-MM-dd HH:mm:ss", 3)},
                null,
                null,
                Constants.TASKS.DATETIME + " ASC");
        cursor.moveToFirst();
        TaskCursorAdapter adapter = new TaskCursorAdapter(this, cursor);
        if(cursor.getCount()==0){
            noTaskYet.setVisibility(View.VISIBLE);
        }
        else {
            noTaskYet.setVisibility(View.INVISIBLE);

        }
        listView.setAdapter(adapter);
    }

    /* After google API successfully connected.*/
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);   // start the location listener.
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection error!", Toast.LENGTH_SHORT).show();
    }

    /* This functions call each 60sec and update the current location */
    @Override
    public void onLocationChanged(Location location) {
        myCurrentLocation = location;
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();  // connect to google api.
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this); // stop the location listener.
        }
        googleApiClient.disconnect();   // disconnect google api.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);    // stop the service.
        cursor.close(); // close the cursor.
        db.close(); // close the db.
    }
}
