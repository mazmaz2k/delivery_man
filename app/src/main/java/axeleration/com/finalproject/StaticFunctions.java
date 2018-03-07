package axeleration.com.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/* This class for statics functions */
class StaticFunctions {

    /* Return string of the current date with format */
    static String getCurrentDate(String format) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(c.getTime());
    }

    /* Return the future string of the date with format and hours to add */
    static String getFutureDate (String format, int hourFromNow){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, hourFromNow);
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(c.getTime());
    }

    /* This function returns a new location object from address string */
    static Location getLocation(Context context, String address) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        Address addressObj = null;
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1); // get address list from string
            addressObj = addresses.get(0);  // get first object address
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addressObj == null) {    // the address is invalid or not found in map.
            Toast.makeText(context, "The address is invalid!", Toast.LENGTH_SHORT).show();
            return null;
        }
        double longitude = addressObj.getLongitude();   // get longitude of the address.
        double latitude = addressObj.getLatitude(); // get latitude of the address.

        Location location = new Location("");   // new location object.
        location.setLongitude(longitude);   // set location longitude.
        location.setLatitude(latitude);     // set location latitude.

        return location;
    }

    /* This function will return a new contentValues object with the new location of the address from current location */
    static ContentValues getContentValues(Location location) {
        if(location == null)    // location not found.
            return null;
        ContentValues values = new ContentValues();
        Location myLocation = MainActivity.myCurrentLocation;   // get my current location from static function in main activity.
        if(myLocation == null) {    // my location is null on emulators.
            return null;
        }
        values.put(Constants.TASKS.LOCATION, myLocation.distanceTo(location));  // put location values in content values.
        return values;
    }
}
