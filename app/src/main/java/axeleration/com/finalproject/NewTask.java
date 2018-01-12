package axeleration.com.finalproject;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewTask extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        final int client_id = getIntent().getIntExtra("client_id",0);
        Log.d("temp",""+client_id);
        final EditText name = findViewById(R.id.fullNameEditTextNT);
        final EditText phone = findViewById(R.id.phoneNumberEditTextNT);
        final EditText city = findViewById(R.id.cityAddressNT);
        final EditText street = findViewById(R.id.streetAddressNT);
        final EditText aparement = findViewById(R.id.apartmentNumberNT);
        final EditText time = findViewById(R.id.timeToDeliverNT);
        final EditText date = findViewById(R.id.dateToDeliverNT);

        Button sumBtn=findViewById(R.id.sum_new_task_btn);
        sumBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String nameReceiver = name.getText().toString();
                String phoneReceiver = phone.getText().toString();
                String cityReceiver = city.getText().toString();
                String streetReceiver = street.getText().toString();
                String apartmentReceiver = aparement.getText().toString();
                String timeReceiver = time.getText().toString();
                String dateReceiver = date.getText().toString();
//                Log.d("temp","1 "+nameReceiver+"2 "+phoneReceiver+"3 "+cityReceiver+"4 "+streetReceiver+"5 "+apartmentReceiver+"6 "+timeReceiver+"7 "+dateReceiver);

                if(!checkIfAnyEmpty(nameReceiver,phoneReceiver,cityReceiver,streetReceiver,apartmentReceiver,timeReceiver,dateReceiver)){
//                    Log.d("temp","2222222222222");
                    postToDB(nameReceiver,phoneReceiver,cityReceiver,streetReceiver,apartmentReceiver,client_id);
                }
            }
        });

    }


    private boolean checkIfAnyEmpty(String name, String phone, String city, String street, String apartment, String time, String date) {
        return name.equals("") || phone.equals("") || city.equals("") || street.equals("") || apartment.equals("") || time.equals("") || date.equals("");
    }
    private void postToDB(String name, String phone, String city, String street, String aparement, long client_id) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.TASKS.FULL_NAME, name);
        values.put(Constants.TASKS.PHONE_NUMBER, phone);
        values.put(Constants.TASKS.CLIENT_ID, client_id);
        values.put(Constants.TASKS.ADDRESS, "Israel " + city + " " + street + " " + aparement);
        long x = db.insert(Constants.TASKS.TABLE_NAME, null, values);
        if(x != -1) {
            Toast.makeText(NewTask.this, "new user added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(NewTask.this, "pakage already exists!", Toast.LENGTH_SHORT).show();
        }
    }
}
