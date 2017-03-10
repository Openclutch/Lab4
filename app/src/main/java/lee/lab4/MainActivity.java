package lee.lab4;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ArrayList<String> messages;
    private MyAdapter adapter;
    public SQLiteDatabase db;

    private static class MyAdapter extends BaseAdapter {
        private Context context;
        private List<String> data;
        private Drawable outgoing;
        private Drawable incoming;

        public MyAdapter(Context context, List<String> data) {
            this.context = context;
            this.data = data;
            this.outgoing = context.getResources().getDrawable(android.R.drawable.sym_call_outgoing,
                    context.getTheme());
            this.incoming = context.getResources().getDrawable(android.R.drawable.sym_call_incoming,
                    context.getTheme());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Create the objects needed
            TextView text = new TextView(context);
            ImageView img = new ImageView(context);
            LinearLayout layout = new LinearLayout(context);

            // Layout settings
            layout.setOrientation(LinearLayout.HORIZONTAL);

            // Set the text box
            text.setText(data.get(position));
            text.setTextSize(30);

            LinearLayout.LayoutParams imgSettings = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imgSettings.gravity = Gravity.BOTTOM;
            img.setLayoutParams(imgSettings);

            // Check if position is odd or even
            // Set pic and positioning accordingly
            if ((position % 2) == 0) {
                img.setImageDrawable(outgoing);
                layout.addView(img);
                layout.addView(text);
            } else {
                img.setImageDrawable(incoming);
                // Format text for right align
                text.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_END);
                text.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_END);
                LinearLayout.LayoutParams textSettings = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                textSettings.weight = 1;
                text.setLayoutParams(textSettings);

                layout.addView(text);
                layout.addView(img);
            }

            return layout;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLite helper = new SQLite(getApplicationContext());
        this.db = helper.getWritableDatabase();

        // Disable pop up keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        this.messages = new ArrayList<String>();

        Log.i("cursor", "Starting the cursor");

        // Initialize new model here with sqlite
        Cursor cursor = db.rawQuery("SELECT message FROM DBMessages", null);
        while(cursor.moveToNext()) {
            Log.i("cursor", "in the loop");
            Log.i("cursor", cursor.getString(cursor.getColumnIndex("message")));
            this.messages.add(cursor.getString(cursor.getColumnIndex("message")));
        }

        Log.i("cursor", "Ending the cursor");

        /*
        // Commenting out for Lab5
        // Initialize Model
        if (savedInstanceState == null) {
            // Create new messages object if nothing is saved
            this.messages = new ArrayList<String>();
        } else {
            // Restore state members from saved instance
            this.messages = savedInstanceState.getStringArrayList("msg");
        }
        */

        // Let the Adapter know about the Model, and the ListView about the Adapter
        this.adapter = new MyAdapter(this, messages);

        ListView listView = (ListView) findViewById(R.id.listView); // Get list from layout
        listView.setAdapter(this.adapter); // where listView is the ListView in your layout
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL); // Auto scroll
    }

    public void onSend(View view) {
        EditText message = (EditText) findViewById(R.id.editText);
        this.messages.add(message.getText().toString());
        String insertQuery = "insert into DBMessages (message) values ('" + message.getText().toString() + "');";
        Log.i("insert", insertQuery);
        db.execSQL(insertQuery);
        adapter.notifyDataSetChanged();
        message.setText("");
    }

    /*
    // Commenting out for Lab5
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putStringArrayList("msg", this.messages);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    */
}
