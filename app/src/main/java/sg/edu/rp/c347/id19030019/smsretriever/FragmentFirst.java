package sg.edu.rp.c347.id19030019.smsretriever;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FragmentFirst extends Fragment {

    Button btnAddtext;
    TextView tvFrag1;
    EditText etFrag1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_first, container, false);
        etFrag1 = view.findViewById(R.id.etFrag1);
        btnAddtext = view.findViewById(R.id.btnFrag1);
        tvFrag1 = view.findViewById(R.id.tvFrag1);

        Context applicationContext = MainActivity.getContextOfApplication();

        btnAddtext.setOnClickListener(v -> {
            Uri uri = Uri.parse("content://sms");
            String[] reqCols = new String[]{"date", "address", "body", "type"};
            ContentResolver cr = applicationContext.getContentResolver();

            if ((!etFrag1.getText().toString().trim().isEmpty()) && isNumeric(etFrag1.getText().toString().trim())) {
                String filter = "address LIKE ?";
                String[] filterArgs = {"%"+etFrag1.getText().toString().trim()+"%"};

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
                tvFrag1.setText(smsBody);
            } else
                return;
        });
        return view;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}