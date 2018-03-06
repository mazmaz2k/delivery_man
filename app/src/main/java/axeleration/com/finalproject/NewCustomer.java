package axeleration.com.finalproject;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
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
import java.util.Date;

/* This class for creating a new customer */
public class NewCustomer extends AppCompatActivity {

    private CheckBox newTaskRegister;   // if checked we can add a new task to this new customer.
    private int selectedDate, selectedMonth, selectedYear;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_customer);

        db = DBHelperSingleton.getInstanceDBHelper(this).getReadableDatabase(); // open the db.

        newTaskRegister = findViewById(R.id.addNewTaskCheckBox);
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

        newTaskRegister.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {    // set on check listener to the checkbox.
                LinearLayout view = findViewById(R.id.receiverLayout);  // the view of new task.
                if(!isChecked)          // if not checked the view is invisible.
                    view.setVisibility(View.INVISIBLE);
                else {  // the view is visible.
                    view.setVisibility(View.VISIBLE);
                    receiverTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {   // receiver time picker custom dialog creation.
                            final Dialog dialog = new Dialog(NewCustomer.this);
                            dialog.setContentView(R.layout.timepicker);
                            dialog.show();
                            final TimePicker timePicker = dialog.findViewById(R.id.timePicker1);
                            timePicker.setIs24HourView(true);
                            Button buttonOk = dialog.findViewById(R.id.okBtn);  // Ok button

                            buttonOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {   // on click listener
                                    /* After click ok we parse the date that can be match to the format of db date field */
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
                                    receiverTime.setText(strTime);
                                    dialog.dismiss();   // close the dialog.
                                }
                            });
                        }
                    });
                    receiverDate.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) { // receiver date picker custom dialog creation.
                            final Dialog dialog = new Dialog(NewCustomer.this);
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
                                    String dateText = dayOfMonth + "-" + (month + 1) + "-" + year;
                                    if(selectedDate == dayOfMonth && selectedMonth == month && selectedYear == year) {
                                        receiverDate.setText(dateText);
                                        dialog.dismiss();
                                    }else {
                                        if(selectedDate != dayOfMonth){
                                            receiverDate.setText(dateText);
                                            dialog.dismiss();
                                        }else {
                                            if(selectedMonth != month){
                                                receiverDate.setText(dateText);
                                                dialog.dismiss();
                                            }
                                        }
                                    }
                                    selectedDate = dayOfMonth;
                                    selectedMonth = month;
                                    selectedYear = year;
                                }
                            });
                            dialog.show();  // show the dialog.
                        }

                    });
                }
            }
        });

        Button submitBtn = findViewById(R.id.submitBtn);    // submit button.
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   // button on click listener.
                /* Getting all the data from all the view's fields */
                String nameClient, phone, city, street, apartment, nameReceiver, phoneReceiver, cityReceiver, streetReceiver, apartmentReceiver,timeReceiver, dateReceiver;
                long client_id;
                nameClient = clientName.getText().toString();
                phone = clientPhone.getText().toString();
                city = clientCity.getText().toString();
                street = clientStreet.getText().toString();
                apartment = clientApartmentNumber.getText().toString();

                /* if the checkbox is checked so we gain the data also from the new task view's field */
                if(newTaskRegister.isChecked()) {
                    nameReceiver = receiverName.getText().toString();
                    phoneReceiver = receiverPhone.getText().toString();
                    cityReceiver = receiverCity.getText().toString();
                    streetReceiver = receiverStreet.getText().toString();
                    apartmentReceiver = receiverApartmentNumber.getText().toString();
                    timeReceiver = receiverTime.getText().toString();
                    dateReceiver = receiverDate.getText().toString();

                    /* Checking if all the fields of the new task are not empty */
                    if(!checkIfAnyEmpty(nameClient, phone, city, street, apartment) && !checkIfAnyEmpty(nameReceiver, phoneReceiver, cityReceiver, streetReceiver, apartmentReceiver, timeReceiver, dateReceiver)) {
                        client_id = postToDB(nameClient, phone, city, street, apartment, Constants.CLIENTS.TABLE_NAME, -1, "", "" , ""); // post to db.
                        if(client_id != -1) {   // added new client successfully.
                            /* trying to upload a new task for that specific client. */
                            if(postToDB(nameReceiver, phoneReceiver, cityReceiver, streetReceiver, apartmentReceiver, Constants.TASKS.TABLE_NAME, client_id, nameClient, dateReceiver, createDateTime(dateReceiver,timeReceiver)) == -1) {
                                Toast.makeText(NewCustomer.this, "Cannot add the task, Some error occurred!", Toast.LENGTH_LONG).show();
                            } else { // user and task added successfully
                                Toast.makeText(NewCustomer.this, "User and task added successfully", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        } else {    // client_id == -1 => we did not upload the data to the db.
                            Toast.makeText(NewCustomer.this, "User with this phone number already exists!", Toast.LENGTH_SHORT).show();
                        }
                    } else {    // some fields are empty.
                        Toast.makeText(NewCustomer.this, "Please make sure all the fields are filled.", Toast.LENGTH_SHORT).show();
                    }

                } else {    // the checkbox is not checked.
                    /* check if the user fields are not empty */
                    if(!checkIfAnyEmpty(nameClient, phone, city, street, apartment)) {
                        client_id = postToDB(nameClient, phone, city, street, apartment, Constants.CLIENTS.TABLE_NAME, -1, "", "" , "");    // post to db.
                        if(client_id != -1) {   // the db updated successfully.
                            Toast.makeText(NewCustomer.this, "User added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {    // something gone wrong and the db did not updated.
                            Toast.makeText(NewCustomer.this, "User with this phone number already exists!", Toast.LENGTH_SHORT).show();
                        }
                    } else {    // some field are empty.
                        Toast.makeText(NewCustomer.this, "Please make sure all the fields are filled.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /* Parse the date and the time to the format of the DB format and return the good format */
    private String createDateTime(String dateReceiver, String timeReceiver) {
        String[] strings = dateReceiver.split("-");
        if(!(Integer.parseInt(strings[1]) < 10) && Integer.parseInt(strings[0]) < 10) {
            return strings[2] + "-" + strings[1] + "-0" + strings[0] + " " + timeReceiver + ":00";
        }else  if((Integer.parseInt(strings[1]) < 10) && !(Integer.parseInt(strings[0]) < 10)){
            return strings[2] + "-0" + strings[1] + "-" + strings[0] + " " + timeReceiver + ":00";
        }else if((Integer.parseInt(strings[1]) < 10) && Integer.parseInt(strings[0]) < 10) {
            return strings[2] + "-0" + strings[1] + "-0" + strings[0] + " " + timeReceiver + ":00";
        }
        return strings[2] + "-" + strings[1] + "-" + strings[0] + " " + timeReceiver + ":00";
    }

    /* return true or false if any of string are empty */
    private boolean checkIfAnyEmpty(String name, String phone, String city, String street, String apartment) {
        return name.equals("") || phone.equals("") || city.equals("") || street.equals("") || apartment.equals("") ;
    }

    /* return true or false if any of string are empty */
    private boolean checkIfAnyEmpty(String name, String phone, String city, String street, String apartment, String time, String date) {
        return name.equals("") || phone.equals("") || city.equals("") || street.equals("") || apartment.equals("") || time.equals("") || date.equals("");
    }

    /* Post the data to the db and return the field number or -1 for error */
    private long postToDB(String name, String phone, String city, String street, String apartment, String dbName, long client_id, String client_name, String date, String dateTime) {
        ContentValues values = new ContentValues();
        if(dbName.equals(Constants.CLIENTS.TABLE_NAME)) {   // insert to the db of clients.
            values.put(Constants.CLIENTS.FULL_NAME, name);
            values.put(Constants.CLIENTS.PHONE_NUMBER, phone);
            values.put(Constants.CLIENTS.ADDRESS, "Israel " + city + " " + street + " " + apartment);
        } else {    // insert to the db of tasks.
            values.put(Constants.TASKS.FULL_NAME, name);
            values.put(Constants.TASKS.PHONE_NUMBER, phone);
            values.put(Constants.TASKS.CLIENT_ID, client_id);
            values.put(Constants.TASKS.IS_SIGN, 0);
            values.put(Constants.TASKS.CLIENT_NAME, client_name);
            values.put(Constants.TASKS.DATE, date);
            values.put(Constants.TASKS.DATETIME, dateTime);
            values.put(Constants.TASKS.ADDRESS, "Israel " + city + " " + street + " " + apartment);
        }
        return db.insert(dbName, null, values);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();    // go back to the previous screen.
        return true;
    }

}
