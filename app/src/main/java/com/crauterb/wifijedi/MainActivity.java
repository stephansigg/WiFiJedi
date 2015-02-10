package com.crauterb.wifijedi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.crauterb.wifijedi.com.crauterb.wifijedi.tasks.InitBCMonTask;

import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {

    protected static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView) findViewById(R.id.defautlText);
        tv.setText("Welcome young one. \n Please press the INIT-Button to start with the setup.");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /**
     * This method is called to initialze the BCMon-Firmware
     * @param view - General View
     */
    public void initStuff(View view){
        InitBCMonTask initTask = new InitBCMonTask(this.getApplicationContext());
        initTask.execute();
        try {
            TimeUnit.SECONDS.sleep(7);
        } catch ( Exception e ) {
            //DO SOMETHING
        }
        TextView tv = (TextView) findViewById(R.id.defautlText);
        tv.setText("BcMon set up \nReady for analysis \nPlease press SCAN for a detailed setup and SLIDESHOW for a direct start with the Slideshow");
    }

    /**
     * Method for starting the setup
     */
    public void startScan(View view) {
        //StartTcpdumpTask scanTask = new StartTcpdumpTask();
        //scanTask.record(4,"initialScan");
        Intent intent = new Intent(this, NetworkScanner.class);
        startActivity(intent);
    }
}
