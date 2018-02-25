package axeleration.com.finalproject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ShowAllTasks extends AppCompatActivity {

    private Cursor cursor;
    private ListView listView;
    private SQLiteDatabase db;
    private TaskCursorAdapter adapter;
    private int client_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_tasks);

        client_id = getIntent().getIntExtra("client_id",-1);

        listView = findViewById(R.id.listViewTaskes);
        db = DBHelperSingleton.getInstanceDBHelper(this).getReadableDatabase();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(client_id == -1){
            cursor = db.query(Constants.TASKS.TABLE_NAME,null,Constants.TASKS.IS_SIGN+"=0",null,null,null,null);
        }else {
            cursor = db.query(Constants.TASKS.TABLE_NAME, null, Constants.TASKS.CLIENT_ID+"=?" +" AND "+Constants.TASKS.IS_SIGN+"=0", new String[] {String.valueOf(client_id)}, null, null, null);
        }
        adapter = new TaskCursorAdapter(this, cursor);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.sort_by_date:
                    cursor = db.query(Constants.TASKS.TABLE_NAME,null,Constants.TASKS.IS_SIGN + "=0",null,null,null,Constants.TASKS.DATETIME + " ASC");
                    break;
                case R.id.sort_by_name:
                    cursor = db.query(Constants.TASKS.TABLE_NAME,null,Constants.TASKS.IS_SIGN+"=0",null,null,null,Constants.TASKS.FULL_NAME + " ASC");
                    break;
                case R.id.sort_by_location:
                    Log.d("temp","cccccccccccc");
                    updateAllDBLocation();
                    cursor = db.query(Constants.TASKS.TABLE_NAME,null,Constants.TASKS.IS_SIGN + "=0",null,null,null,Constants.TASKS.LOCATION + " ASC");
                    break;
                default:
                    onBackPressed();
        }
        adapter = new TaskCursorAdapter(this,cursor);
        listView.setAdapter(adapter);
        return true;
    }

    private void updateAllDBLocation() {
        cursor = db.query(Constants.TASKS.TABLE_NAME,null,Constants.TASKS.IS_SIGN + "=0",null,null,null,null);
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++) {    // for each row in DB.
            int ID = cursor.getInt(cursor.getColumnIndex(Constants.TASKS._ID)); // get the ID.
            String address = cursor.getString(cursor.getColumnIndex(Constants.TASKS.ADDRESS));  // get the ADDRESS.
            Location location = getLocation(address);
            ContentValues values = getContentValues(location);
            if(values == null) {
                continue;
            }
            db.update(Constants.TASKS.TABLE_NAME, values, Constants.TASKS._ID + "=?", new String[] {String.valueOf(ID)});   // update the DB ADDRESS field.
            cursor.moveToNext();    // go to the next row.
        }
    }

    private Location getLocation(String address) {  // This function returns a new location object from address string.
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Address addressObj = null;
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            addressObj = addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addressObj == null) {
            Toast.makeText(this, "The address is invalid!", Toast.LENGTH_SHORT).show();
            return null;
        }
        double longitude = addressObj.getLongitude();
        double latitude = addressObj.getLatitude();

        Location location = new Location("");
        location.setLongitude(longitude);
        location.setLatitude(latitude);

        return location;
    }

    private ContentValues getContentValues(Location location) {    // This function will return a new contentValues object with the new location of the address from current location.
        if(location == null)
            return null;
        ContentValues values = new ContentValues();
        Location myLocation = MainActivity.myCurrentLocation;

        values.put(Constants.TASKS.LOCATION, myLocation.distanceTo(location));
        return values;
    }
}
