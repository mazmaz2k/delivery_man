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
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int REQUEST_CODE = 1234;
    private Cursor cursor;
    private ListView listView;
    private SQLiteDatabase db;
    private TaskCursorAdapter adapter;
    private String[] dateStr;
    private Calendar dateTemp;
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
        getCurrentTime();
        listView = findViewById(R.id.listViewMain);
        db = DBHelperSingleton.getInstanceDBHelper(this).getReadableDatabase();
        cursor=db.query(Constants.TASKS.TABLE_NAME,null,Constants.TASKS.IS_SIGN+"=0 AND "+Constants.TASKS.DATE + "=?",new String[]{ getTodayDate()},null,null,Constants.TASKS.DATETIME+" ASC");
        adapter = new TaskCursorAdapter(this, cursor);
        listView.setAdapter(adapter);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat();
        c.add(Calendar.HOUR, 3);
        Log.d("temp","time is:-----------"+ s.format(c.getTime()));

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
        cursor=db.query(Constants.TASKS.TABLE_NAME,null,Constants.TASKS.IS_SIGN+"=0 AND "+Constants.TASKS.DATE + "=?"+" AND "+Constants.TASKS.DATETIME+"=?",new String[]{ getTodayDate(),getCurrentTime()},null,null,Constants.TASKS.DATETIME+" ASC");
        cursor.moveToFirst();
        adapter = new TaskCursorAdapter(this, cursor);
        listView.setAdapter(adapter);

    }

    private String getCurrentTime (){
        SimpleDateFormat sdf = new SimpleDateFormat("H:m");
//        Log.d("temp",""+sdf.format(new Date()));
        String[] array=sdf.format(new Date()).split(":");
        if(Integer.parseInt(array[0])+3>=24){
           array[0]=String.valueOf((Integer.parseInt(array[0])+3)%24);
//            dateTemp.setTime(new Date(dateStr[1]+"/"+dateStr[0]+"/"+dateStr[2]));
//            dateTemp.add(Calendar.DAY_OF_MONTH,1);
        }
        return sdf.format(new Date());

    }
    private String getTodayDate (){
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
        //Log.d("temp",""+sdf.format(new Date()));
        dateStr=sdf.format(new Date()).split("/");
        return sdf.format(new Date());
    }
}
