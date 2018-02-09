package axeleration.com.finalproject;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;

public class NewCustomer extends AppCompatActivity {

    private CheckBox checkBox;
    private int selectedDate, selectedMonth, selectedYear;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_customer);

        db = DBHelperSingleton.getInstanceDBHelper(this).getReadableDatabase();

        checkBox = findViewById(R.id.addNewTaskCheckBox);
        final EditText clientName = findViewById(R.id.fullNameEditText);
        final EditText clientPhone = findViewById(R.id.phoneNumberEditText);
        final EditText clientCity = findViewById(R.id.cityAddress);
        final EditText clientStreet = findViewById(R.id.streetAddress);
        final EditText clientApartmentNumber = findViewById(R.id.apartmentNumber);

        final EditText receiverName = findViewById(R.id.fullNameEditTextReceiver);
        final EditText receiverPhone = findViewById(R.id.phoneNumberEditTextReceiver);
        final EditText receiverCity = findViewById(R.id.cityAddressReceiver);
        final EditText receiverStreet = findViewById(R.id.streetAddressReceiver);
        final EditText receiverApartmentNumber = findViewById(R.id.apartmentNumberReceiver);
        final EditText receiverTime = findViewById(R.id.timeToDeliver);
        final EditText receiverDate = findViewById(R.id.dateToDeliver);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LinearLayout view = findViewById(R.id.receiverLayout);
                if(!isChecked)
                    view.setVisibility(View.INVISIBLE);
                else {
                    view.setVisibility(View.VISIBLE);
                    receiverTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Dialog dialog = new Dialog(NewCustomer.this);
                            dialog.setContentView(R.layout.timepicker);
                            dialog.show();
                            final TimePicker timePicker = dialog.findViewById(R.id.timePicker1);
                            timePicker.setIs24HourView(false);
                            Button buttonOk = dialog.findViewById(R.id.okBtn);

                            buttonOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int minutes = timePicker.getCurrentMinute();
                                    String strTime;
                                    if(minutes < 10) {
                                        strTime = timePicker.getCurrentHour() + ":0" + minutes;
                                    } else {
                                        strTime = timePicker.getCurrentHour() + ":" + minutes;
                                    }
                                    receiverTime.setText(strTime);
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                    receiverDate.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            // custom dialog

                            final Dialog dialog = new Dialog(NewCustomer.this);
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

                                    if(selectedDate == dayOfMonth && selectedMonth == month && selectedYear == year) {

                                        receiverDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                                        dialog.dismiss();
                                    }else {

                                        if(selectedDate != dayOfMonth){
                                            receiverDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                                            dialog.dismiss();
                                        }else {
                                            if(selectedMonth != month){
                                                receiverDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
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
                }
            }
        });

        Button submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameClient, phone, city, street, apartment, nameReceiver, phoneReceiver, cityReceiver, streetReceiver, apartmentReceiver,timeReceiver, dateReceiver;
                long client_id;
                nameClient = clientName.getText().toString();
                phone = clientPhone.getText().toString();
                city = clientCity.getText().toString();
                street = clientStreet.getText().toString();
                apartment = clientApartmentNumber.getText().toString();

                if(checkBox.isChecked()) {
                    nameReceiver = receiverName.getText().toString();
                    phoneReceiver = receiverPhone.getText().toString();
                    cityReceiver = receiverCity.getText().toString();
                    streetReceiver = receiverStreet.getText().toString();
                    apartmentReceiver = receiverApartmentNumber.getText().toString();
                    timeReceiver = receiverTime.getText().toString();
                    dateReceiver = receiverDate.getText().toString();

                    if(!checkIfAnyEmpty(nameClient, phone, city, street, apartment) && !checkIfAnyEmpty(nameReceiver, phoneReceiver, cityReceiver, streetReceiver, apartmentReceiver, timeReceiver, dateReceiver)) {
                        client_id = postToDB(nameClient, phone, city, street, apartment, Constants.CLIENTS.TABLE_NAME, -1, "", "" , "");
                        if(client_id != -1) {
                            if(postToDB(nameReceiver, phoneReceiver, cityReceiver, streetReceiver, apartmentReceiver, Constants.TASKS.TABLE_NAME, client_id, nameClient, dateReceiver, timeReceiver) == -1) {
                                Toast.makeText(NewCustomer.this, "Cannot add the task, Some error occurred!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(NewCustomer.this, "User and task added successfully", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        } else {
                            Toast.makeText(NewCustomer.this, "User with this phone number already exists!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(NewCustomer.this, "Please make sure all the fields are filled.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if(!checkIfAnyEmpty(nameClient, phone, city, street, apartment)) {
                        client_id = postToDB(nameClient, phone, city, street, apartment, Constants.CLIENTS.TABLE_NAME, -1, "", "" , "");
                        if(client_id != -1) {
                            Toast.makeText(NewCustomer.this, "User added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(NewCustomer.this, "User with this phone number already exists!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(NewCustomer.this, "Please make sure all the fields are filled.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean checkIfAnyEmpty(String name, String phone, String city, String street, String apartment) {
        return name.equals("") || phone.equals("") || city.equals("") || street.equals("") || apartment.equals("") ;
    }

    private boolean checkIfAnyEmpty(String name, String phone, String city, String street, String apartment, String time, String date) {
        return name.equals("") || phone.equals("") || city.equals("") || street.equals("") || apartment.equals("") || time.equals("") || date.equals("");
    }

    private long postToDB(String name, String phone, String city, String street, String apartment, String dbName, long client_id, String client_name, String date, String time) {
        ContentValues values = new ContentValues();
        if(dbName.equals(Constants.CLIENTS.TABLE_NAME)) {
            values.put(Constants.CLIENTS.FULL_NAME, name);
            values.put(Constants.CLIENTS.PHONE_NUMBER, phone);
            values.put(Constants.CLIENTS.ADDRESS, "Israel " + city + " " + street + " " + apartment);
        } else {
            values.put(Constants.TASKS.FULL_NAME, name);
            values.put(Constants.TASKS.PHONE_NUMBER, phone);
            values.put(Constants.TASKS.CLIENT_ID, client_id);
            values.put(Constants.TASKS.IS_SIGN, 0);
            values.put(Constants.TASKS.CLIENT_NAME, client_name);
            values.put(Constants.TASKS.DATE, date);
            values.put(Constants.TASKS.TIME, time);
            values.put(Constants.TASKS.ADDRESS, "Israel " + city + " " + street + " " + apartment);
        }
        return db.insert(dbName, null, values);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
