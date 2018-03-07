package axeleration.com.finalproject;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/* This Activity create JPEG file of the signature and saves it in the device */
public class SignaturePad extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_SMS = 2;
    private static String[] PERMISSIONS_STORAGE = { Manifest.permission.WRITE_EXTERNAL_STORAGE };
    private com.github.gcacace.signaturepad.views.SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;
    private int receiver_id;
    private String receiver_name, client_name, receiver_phone_number;
    private SimpleDateFormat date;
    private SQLiteDatabase db;
    private Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(this); // check for permission and if not granted ask for permissions.
        setContentView(R.layout.activity_signature_pad);

        // receive data from another Intent ,receiver name and phone client Id.
        receiver_name = getIntent().getStringExtra("receiver_name");
        int client_id = getIntent().getIntExtra("client_id", -1);
        receiver_phone_number = getIntent().getStringExtra("phone_number");
        receiver_id = getIntent().getIntExtra("receiver_id", -1);

        db = DBHelperSingleton.getInstanceDBHelper(this).getReadableDatabase(); // open the db.

        c = db.query(Constants.CLIENTS.TABLE_NAME,      // query to find specific client by given ID
                null
                ,Constants.CLIENTS._ID + "=?"
                ,  new String[]{String.valueOf(client_id)}
                , null
                , null
                ,null );
        c.moveToFirst();
        client_name = c.getString(c.getColumnIndex(Constants.CLIENTS.FULL_NAME));   // get client name from cursor.
        date = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

        mSignaturePad = findViewById(R.id.signature_pad); // find pad view.
        mSignaturePad.setOnSignedListener(new com.github.gcacace.signaturepad.views.SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                Toast.makeText(SignaturePad.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {    // enable clear and save button after the signed.
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {     // disable clear and save button on clear pad.
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mClearButton = findViewById(R.id.clear_button); // find button to clear pad.
        mSaveButton = findViewById(R.id.save_button);   // find button to save pad.
        mClearButton.setOnClickListener(new View.OnClickListener() {       // Listener to clear the pad when press
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();  // clear the pad.
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // Listener to save the pad when press
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                if (addJpgSignatureToGallery(signatureBitmap)) {
                    Toast.makeText(SignaturePad.this, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignaturePad.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
                }
                putToDoneTask();    // the signed task goes marks as finished.
                sendSMS(c.getString(c.getColumnIndex(Constants.CLIENTS.PHONE_NUMBER)), receiver_name + " just receive the package you sent!");  // send SMS so client.
                sendSMS(receiver_phone_number, "Dear customer, thank you for receiving the package!");  // send SMS to receiver.
                finish();
            }
        });
    }

    /* put values in Task table and update it */
    private void putToDoneTask() {
        ContentValues cv = new ContentValues();
        cv.put(Constants.TASKS.IS_SIGN, 1);
        db.update(Constants.TASKS.TABLE_NAME,cv,Constants.TASKS._ID + "=?", new String[]{String.valueOf(receiver_id)});
    }

    /* send SMS to destination */
    private void sendSMS(String number, String message) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED || // check if there is permission to send SMS.
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{   // if the permission not granted ask for permissions.
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_PHONE_STATE
            }, REQUEST_SMS);
            Toast.makeText(this,"No permission for SMS", Toast.LENGTH_SHORT).show();
                return;
        }
        SmsManager.getDefault().sendTextMessage(number, null, message, null, null );
    }

    /* callback of permission request result */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SignaturePad.this, "Cannot write images to external storage", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case REQUEST_SMS: {
                if (grantResults.length <= 0
                        || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SignaturePad.this, "Cannot send the SMS", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /* create album directory in the gallery */
    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Toast.makeText(this,"SignaturePad Directory not created", Toast.LENGTH_SHORT).show();
        }
        return file;
    }

    /* save pic from layout and create JPG */
    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    /* add and save Pic do storage device */
    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        String today = date.format(Calendar.getInstance().getTime());
        try {
            File photo = new File(getAlbumStorageDir(today), String.format("From: " + client_name + " To: " + receiver_name + ".jpg", System.currentTimeMillis())); // set pic name
            saveBitmapToJPG(signature, photo); // save pic on device
            scanMediaFile(photo);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /* check if pic is ok */
    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        SignaturePad.this.sendBroadcast(mediaScanIntent);
    }

    /* Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        c.close();
    }
}