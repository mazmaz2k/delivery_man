package axeleration.com.finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int REQUEST_CODE=1234;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},REQUEST_CODE);

        }
        ImageButton addTask = findViewById(R.id.addNewTaskBtn);
        ImageButton allClients = findViewById(R.id.allClients);
        ImageButton allTaskes = findViewById(R.id.allTasksBtn);
        allTaskes.setOnClickListener(this);
        allClients.setOnClickListener(this);
        addTask.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.addNewTaskBtn:
                i = new Intent(MainActivity.this, NewCustomer.class);
                startActivity(i);
                break;
            case R.id.allClients:
                i = new Intent(MainActivity.this, AllClients.class);
                startActivity(i);
                break;
            case R.id.allTasksBtn:
                i = new Intent(MainActivity.this, ShowAllTasks.class);
                startActivity(i);
                break;
        }
    }
}
