package sg.edu.rp.c347.id19030019.smsretriever;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;

public class FragmentSecond extends Fragment {

    private static final String TAG = FragmentSecond.class.getSimpleName();

    // Views
    private Button btnAddtext2, btnSendEmail;
    private TextView tvFrag2;
    private EditText etFrag2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        etFrag2 = view.findViewById(R.id.etFrag2);
        btnAddtext2 = view.findViewById(R.id.btnFrag2);
        tvFrag2 = view.findViewById(R.id.tvFrag2);
        btnSendEmail = view.findViewById(R.id.btnSendEmail);

        btnAddtext2.setOnClickListener(v-> {
            Uri uri = Uri.parse("content://sms");
            String[] reqCols = new String[]{"date", "address", "body", "type"};
            ContentResolver cr = getContext().getContentResolver();

            String words = etFrag2.getText().toString().trim();
            if (!words.isEmpty()) {

                // Filter
                String filter = "";
                String[] wordArray = words.split(" ", 0);
                String[] filterArgs = new String[wordArray.length];

                for (int i = 0; i < wordArray.length; i++) {
                    filter += "body LIKE ? ";
                    // add AND
                    if (i != wordArray.length - 1) {
                        filter += "OR ";
                    }
                    // Add Word
                    filterArgs[i] = "%" + wordArray[i] + "%";
                }

                Log.d(TAG, Arrays.toString(wordArray) + "\n" + filter + "\n" + Arrays.toString(filterArgs));

                // Ger Rows
                Cursor cursor = cr.query(
                        uri,
                        reqCols,
                        filter,
                        filterArgs,
                        null);

                Log.d(TAG, "Number of rows: " + cursor.getCount());

                // Get Each Row (Message)
                String smsBody = "";
                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1"))
                            type = "Inbox:";
                        else
                            type = "Sent:";
                        smsBody += type + " " + address + "\n at" + date + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tvFrag2.setText(smsBody);
            } else
                return;
        });

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date", "address", "body", "type"};
                ContentResolver cr = getContext().getContentResolver();

                if (!etFrag2.getText().toString().trim().isEmpty()) {
                    String filter = "body LIKE ?";
                    String[] filterArgs = {"%"+etFrag2.getText().toString().trim()+"%"};

                    Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);
                    String smsBody = "";
                    if (cursor.moveToFirst()) {
                        do {
                            long dateInMillis = cursor.getLong(0);
                            String date = (String) DateFormat.format("dd MM yyyy h:mm:ss aa", dateInMillis);
                            String address = cursor.getString(1);
                            String body = cursor.getString(2);
                            String type = cursor.getString(3);
                            if (type.equalsIgnoreCase("1"))
                                type = "Inbox:";
                            else
                                type = "Sent:";
                            smsBody += type + " " + address + "\n at" + date + "\n\"" + body + "\"\n\n";
                        } while (cursor.moveToNext());
                    }
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{"weeweiren@gmail.com"});
                    email.putExtra(Intent.EXTRA_SUBJECT, "Your past SMS");
                    email.putExtra(Intent.EXTRA_TEXT, smsBody);
                    email.setType("message/rfc822");
                    startActivity(Intent.createChooser(email, "Choose an Email client :"));
                } else
                    return;
            }
        });

        return view;

    }
}