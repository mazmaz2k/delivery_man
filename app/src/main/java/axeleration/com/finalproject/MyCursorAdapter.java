package axeleration.com.finalproject;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class MyCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;
    public MyCursorAdapter(Context context, Cursor c) {
        super(context, c);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView text = view.findViewById(R.id.name);
        TextView phone = view.findViewById(R.id.phone);

        text.setText(cursor.getString(cursor.getColumnIndex(Constants.CLIENTS.FULL_NAME)));
        phone.setText(cursor.getString(cursor.getColumnIndex(Constants.CLIENTS.PHONE_NUMBER)));

    }
}
