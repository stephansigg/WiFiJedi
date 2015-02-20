package com.crauterb.wifijedi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.crauterb.wifijedi.com.crauterb.wifijedi.tasks.StartTcpdumpTask;

import java.util.concurrent.TimeUnit;


public class RecordThingy extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_thingy);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record_thingy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void record(View view) {
        EditText name = (EditText) findViewById(R.id.tf_recordName);
        EditText time = (EditText) findViewById(R.id.tf_recordTime);

        StartTcpdumpTask scanTask = new StartTcpdumpTask();
        scanTask.record(Integer.parseInt(time.getText().toString()),name.getText().toString());
        try {
            System.out.println("WE SHOULD SLEEP HERE");
            TimeUnit.SECONDS.sleep( Integer.parseInt(time.getText().toString())+ 1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
