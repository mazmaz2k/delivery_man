package axeleration.com.finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int REQUEST_CODE = 1234;
    private Cursor cursor;
    private ListView listView;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE },REQUEST_CODE);

        }
        ImageButton dailyAssignments= findViewById(R.id.daily_assignments);
        ImageButton addTask = findViewById(R.id.addNewTaskBtn);
        ImageButton allClients = findViewById(R.id.allClients);
        ImageButton allTask = findViewById(R.id.allTasksBtn);
        ImageButton finishTasks =findViewById(R.id.finishedTaskBtn);
        finishTasks.setOnClickListener(this);
        allTask.setOnClickListener(this);
        allClients.setOnClickListener(this);
        addTask.setOnClickListener(this);
        dailyAssignments.setOnClickListener(this);

        listView = findViewById(R.id.listViewMain);
        db = DBHelperSingleton.getInstanceDBHelper(this).getReadableDatabase();

    }

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
                i = new Intent(MainActivity.this,FinishTasks.class);
                break;
            case  R.id.daily_assignments:
                i = new Intent(MainActivity.this,DailyAssignments.class);
                break;
            default:
                Toast.makeText(this, "Activity was not found!", Toast.LENGTH_SHORT).show();
                break;
        }
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
                        grantResults[3] == PackageManager.PERMISSION_GRANTED) {
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
        cursor = db.query(Constants.TASKS.TABLE_NAME,null,Constants.TASKS.IS_SIGN+"=0 AND " + Constants.TASKS.DATETIME + ">=? AND " + Constants.TASKS.DATETIME + "<?",new String[]{getCurrentDate() ,getDate()},null ,null,null,null);
        cursor.moveToFirst();
        TaskCursorAdapter adapter = new TaskCursorAdapter(this, cursor);
        listView.setAdapter(adapter);

    }

    private String getDate (){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, 3);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(c.getTime());
    }

    private String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(c.getTime());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }
}
