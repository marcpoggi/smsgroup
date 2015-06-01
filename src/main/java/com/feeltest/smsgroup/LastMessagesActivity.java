package com.feeltest.smsgroup;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class LastMessagesActivity extends Activity {

    ArrayList idtesteur;
    Cursor messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_messages);
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        ListView mlistview = (ListView) findViewById(R.id.listlastmsg);
        DBManager.sharedManager().open();
        messages = DBManager.sharedManager().getAllMessages();
        String[] columns = new String[]{
                "telnom",
                "body"
        };
        idtesteur = new ArrayList();
        while (messages.moveToNext()) {
            idtesteur.add(messages.getString(0)); // les id tel
        }

        if (messages.getCount() > 0) {
            SimpleCursorAdapter madapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                    messages,
                    columns,
                    new int[]{android.R.id.text1, android.R.id.text2});
            mlistview.setAdapter(madapter);
        }
        mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", idtesteur.get(i).toString());
                startActivity(smsIntent);
            }
        });
    }
}

