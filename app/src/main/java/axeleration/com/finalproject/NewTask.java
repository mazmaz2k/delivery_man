package axeleration.com.finalproject;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;

public class NewTask extends AppCompatActivity {

    private int selectedDate, selectedMonth, selectedYear;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        db = DBHelperSingleton.getInstanceDBHelper(this).getReadableDatabase();
        final int clientId = getIntent().getIntExtra("client_id",0);
        final String clientName = getIntent().getStringExtra("clientName");
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
                timePicker.setIs24HourView(true);
                Button buttonOk = dialog.findViewById(R.id.okBtn);

                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int minutes = timePicker.getCurrentMinute();
                        int hours = timePicker.getCurrentHour();
                        String strTime;
                        String strHour;
                        if(hours < 10) {
                            strHour = "0" + hours;
                        } else {
                            strHour = String.valueOf(hours);
                        }
                        if(minutes < 10) {
                            strTime = strHour + ":0" + minutes;
                        } else {
                            strTime = strHour + ":" + minutes;
                        }
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
                datePicker.setMinDate(new Date().getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                selectedDate = calendar.get(Calendar.DAY_OF_MONTH);
                selectedMonth = calendar.get(Calendar.MONTH);
                selectedYear = calendar.get(Calendar.YEAR);
                datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                String dateString = dayOfMonth + "-" + (month + 1) + "-" + year;
                        if(selectedDate == dayOfMonth && selectedMonth == month && selectedYear == year) {
                            date.setText(dateString);
                            dialog.dismiss();
                        }else {

                            if(selectedDate != dayOfMonth){
                                date.setText(dateString);
                                dialog.dismiss();
                            }else {
                                if(selectedMonth != month){
                                    date.setText(dateString);
                                    dialog.dismiss();
                                }
                            }
                        }
                        selectedDate = dayOfMonth;
                        selectedMonth = month;
                        selectedYear = year;
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
                String dateReceiver= date.getText().toString();
                
                String dateTime=createDateTime(dateReceiver,timeReceiver);
                if(!checkIfAnyEmpty(nameReceiver,phoneReceiver,cityReceiver,streetReceiver,apartmentReceiver,timeReceiver,dateReceiver)){
                    postToDB(nameReceiver,phoneReceiver,cityReceiver,streetReceiver,apartmentReceiver,clientId, clientName, dateReceiver, dateTime);
                }
            }
        });

    }

    private String createDateTime(String dateReceiver, String timeReceiver) {
        String[] strings = dateReceiver.split("-");
        if(!(Integer.parseInt(strings[1])<10)&&Integer.parseInt(strings[0])<10) {
            return strings[2] + "-" + strings[1] + "-0" + strings[0] + " " + timeReceiver + ":00";
        }else  if((Integer.parseInt(strings[1])<10)&&!(Integer.parseInt(strings[0])<10)){
            return strings[2]+"-0"+strings[1]+"-"+strings[0]+" "+timeReceiver+":00";

        }else if((Integer.parseInt(strings[1])<10)&&Integer.parseInt(strings[0])<10) {

            return strings[2]+"-0"+strings[1]+"-0"+strings[0]+" "+timeReceiver+":00";
        }
        return strings[2]+"-"+strings[1]+"-"+strings[0]+" "+timeReceiver+":00";
    }


    private boolean checkIfAnyEmpty(String name, String phone, String city, String street, String apartment, String time, String date) {
        return name.equals("") || phone.equals("") || city.equals("") || street.equals("") || apartment.equals("") || time.equals("") || date.equals("");
    }
    private void postToDB(String name, String phone, String city, String street, String apartment, long client_id, String clientName, String date, String dateTime) {
        ContentValues values = new ContentValues();
        values.put(Constants.TASKS.FULL_NAME, name);
        values.put(Constants.TASKS.PHONE_NUMBER, phone);
        values.put(Constants.TASKS.CLIENT_NAME, clientName);
        values.put(Constants.TASKS.DATE, date);
        values.put(Constants.TASKS.DATETIME, dateTime);
        values.put(Constants.TASKS.CLIENT_ID, client_id);
        values.put(Constants.TASKS.IS_SIGN, 0);
        values.put(Constants.TASKS.ADDRESS, "Israel " + city + " " + street + " " + apartment);
        long x = db.insert(Constants.TASKS.TABLE_NAME, null, values);
        if(x != -1) {
            Toast.makeText(NewTask.this, "New task added successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(NewTask.this, "Some error occurred", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
