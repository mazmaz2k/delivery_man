package axeleration.com.finalproject;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class NewCustomer extends AppCompatActivity {

    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_customer);

        checkBox = findViewById(R.id.addNewTaskCheckBox);
        final EditText fullName = findViewById(R.id.fullNameEditText);
        final EditText phoneNumber = findViewById(R.id.phoneNumberEditText);
        final EditText cityField = findViewById(R.id.cityAddress);
        final EditText streetField = findViewById(R.id.streetAddress);
        final EditText apartmentNumber = findViewById(R.id.apartmentNumber);

        final EditText receiverName = findViewById(R.id.fullNameEditTextReceiver);
        final EditText receiverPhone = findViewById(R.id.phoneNumberEditTextReceiver);
        final EditText receiverCity = findViewById(R.id.cityAddressReceiver);
        final EditText receiverStreet = findViewById(R.id.streetAddressReceiver);
        final EditText receiverApartment = findViewById(R.id.apartmentNumberReceiver);
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
                }
            }
        });

        Button submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, phone, city, street, apartment, nameReceiver, phoneReceiver, cityReceiver, streetReceiver, apartmentReceiver, timeReceiver, dateReceiver;
                long client_id;
                name = fullName.getText().toString();
                phone = phoneNumber.getText().toString();
                city = cityField.getText().toString();
                street = streetField.getText().toString();
                apartment = apartmentNumber.getText().toString();

                if(checkBox.isChecked()) {
                    nameReceiver = receiverName.getText().toString();
                    phoneReceiver = receiverPhone.getText().toString();
                    cityReceiver = receiverCity.getText().toString();
                    streetReceiver = receiverStreet.getText().toString();
                    apartmentReceiver = receiverApartment.getText().toString();
                    timeReceiver = receiverTime.getText().toString();
                    dateReceiver = receiverDate.getText().toString();

                    if(!checkIfAnyEmpty(name, phone, city, street, apartment) && !checkIfAnyEmpty(nameReceiver, phoneReceiver, cityReceiver, streetReceiver, apartmentReceiver, timeReceiver, dateReceiver)) {
                        client_id = postToDB(name, phone, city, street, apartment, Constants.CLIENTS.TABLE_NAME, -1);
                        if(client_id != -1) {
                            postToDB(nameReceiver, phoneReceiver, cityReceiver, streetReceiver, apartmentReceiver, Constants.TASKS.TABLE_NAME, client_id);
                            //todo:  post check
                            Toast.makeText(NewCustomer.this, "new user added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(NewCustomer.this, "user already exists!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(NewCustomer.this, "Please make sure all the fields are filled.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if(!checkIfAnyEmpty(name, phone, city, street, apartment)) {
                        client_id = postToDB(name, phone, city, street, apartment, Constants.CLIENTS.TABLE_NAME, -1);
                        if(client_id != -1) {
                            Toast.makeText(NewCustomer.this, "new user added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(NewCustomer.this, "user already exists!", Toast.LENGTH_SHORT).show();
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

    private long postToDB(String name, String phone, String city, String street, String aparement, String dbName, long client_id) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(dbName.equals(Constants.CLIENTS.TABLE_NAME)) {
            values.put(Constants.CLIENTS.FULL_NAME, name);
            values.put(Constants.CLIENTS.PHONE_NUMBER, phone);
            values.put(Constants.CLIENTS.ADDRESS, "Israel " + city + " " + street + " " + aparement);
        } else {
            values.put(Constants.TASKS.FULL_NAME, name);
            values.put(Constants.TASKS.PHONE_NUMBER, phone);
            values.put(Constants.TASKS.CLIENT_ID, client_id);
            values.put(Constants.TASKS.ADDRESS, "Israel " + city + " " + street + " " + aparement);
        }
        return db.insert(dbName, null, values);
    }
}
