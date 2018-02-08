package axeleration.com.finalproject;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;

public class NewTask extends AppCompatActivity {

    private int selectedDate, selectedMonth, selectedYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        final int client_id = getIntent().getIntExtra("client_id",0);

        final EditText name = findViewById(R.id.fullNameEditTextNT);
        final EditText phone = findViewById(R.id.phoneNumberEditTextNT);
        final EditText city = findViewById(R.id.cityAddressNT);
        final EditText street = findViewById(R.id.streetAddressNT);
        final EditText apartment = findViewById(R.id.apartmentNumberNT);
        final EditText time = findViewById(R.id.timeToDeliverNT);
        final EditText date = findViewById(R.id.dateToDeliverNT);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(NewTask.this);
                dialog.setContentView(R.layout.timepicker);
                dialog.show();
                final TimePicker timePicker = dialog.findViewById(R.id.timePicker1);
                timePicker.setIs24HourView(false);
                Button buttonOk = dialog.findViewById(R.id.okBtn);

                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            String strTime = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
                            time.setText(strTime);
                            dialog.dismiss();
                    }
                });
            }
        });

        date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(NewTask.this);
                dialog.setContentView(R.layout.datepicker);
                dialog.setTitle("");

                DatePicker datePicker = dialog.findViewById(R.id.datePicker1);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                selectedDate = calendar.get(Calendar.DAY_OF_MONTH);
                selectedMonth = calendar.get(Calendar.MONTH);
                selectedYear = calendar.get(Calendar.YEAR);
                datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {

                        if(selectedDate ==dayOfMonth && selectedMonth==month && selectedYear==year) {

                            date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            dialog.dismiss();
                        }else {

                            if(selectedDate !=dayOfMonth){
                                date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                                dialog.dismiss();
                            }else {
                                if(selectedMonth !=month){
                                    date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                                    dialog.dismiss();
                                }
                            }
                        }
                        selectedDate=dayOfMonth;
                        selectedMonth=(month);
                        selectedYear=year;
                    }
                });


                dialog.show();
            }

        });
        Button sumBtn=findViewById(R.id.sum_new_task_btn);
        sumBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String nameReceiver = name.getText().toString();
                String phoneReceiver = phone.getText().toString();
                String cityReceiver = city.getText().toString();
                String streetReceiver = street.getText().toString();
                String apartmentReceiver = apartment.getText().toString();
                String timeReceiver = time.getText().toString();


                if(!checkIfAnyEmpty(nameReceiver,phoneReceiver,cityReceiver,streetReceiver,apartmentReceiver,timeReceiver,date.toString())){
                    postToDB(nameReceiver,phoneReceiver,cityReceiver,streetReceiver,apartmentReceiver,client_id);
                }
            }
        });

    }


    private boolean checkIfAnyEmpty(String name, String phone, String city, String street, String apartment, String time, String date) {
        return name.equals("") || phone.equals("") || city.equals("") || street.equals("") || apartment.equals("") || time.equals("") || date.equals("");
    }
    private void postToDB(String name, String phone, String city, String street, String apartment, long client_id) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.TASKS.FULL_NAME, name);
        values.put(Constants.TASKS.PHONE_NUMBER, phone);
        values.put(Constants.TASKS.CLIENT_ID, client_id);
        values.put(Constants.TASKS.IS_SIGN, 0);
        values.put(Constants.TASKS.ADDRESS, "Israel " + city + " " + street + " " + apartment);
        long x = db.insert(Constants.TASKS.TABLE_NAME, null, values);
        if(x != -1) {
            Toast.makeText(NewTask.this, "new task added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(NewTask.this, "package already exists!", Toast.LENGTH_SHORT).show();
        }
    }
}
