package axeleration.com.finalproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        final EditText fullName = findViewById(R.id.fullNameEditText);
        final EditText phoneNumber = findViewById(R.id.phoneNumberEditText);
        final EditText cityField = findViewById(R.id.cityAddress);
        final EditText streetField = findViewById(R.id.streetAddress);
        final EditText apartmentNumber = findViewById(R.id.apartmentNumber);

        Button submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, phone, city, street, apartment;
                name = fullName.getText().toString();
                phone = phoneNumber.getText().toString();
                city = cityField.getText().toString();
                street = streetField.getText().toString();
                apartment = apartmentNumber.getText().toString();
                postToDB(name, phone, city, street, apartment);
            }
        });
    }

    private void postToDB(String name, String phone, String city, String street, String aparement) {

    }
}
