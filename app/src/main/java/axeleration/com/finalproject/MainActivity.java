package axeleration.com.finalproject;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton addTask = findViewById(R.id.addNewTaskBtn);
        addTask.setOnClickListener(this);

//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        try {
//            List<Address> list = geocoder.getFromLocationName("Israel Jerusalem Gilo tirosh 9 ", 1);
//            Log.d("temp", list.get(0).getLatitude() + "");
//            Log.d("temp", list.get(0).getLongitude() + "");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addNewTaskBtn:
                Intent i = new Intent(MainActivity.this, NewTask.class);
                startActivity(i);
                break;
        }
    }
}
