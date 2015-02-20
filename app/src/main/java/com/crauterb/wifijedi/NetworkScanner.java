package com.crauterb.wifijedi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.crauterb.wifijedi.com.crauterb.wifijedi.tasks.StartTcpdumpTask;
import com.crauterb.wifijedi.rrsiLearning.Capture;
import com.crauterb.wifijedi.rrsiLearning.Network;
import com.crauterb.wifijedi.rssiReader.RSSIFileReader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class NetworkScanner extends ActionBarActivity {

    /** Maximum number of networks than can be counted as relevant*/
    public static final int maxNetworks = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_scanner);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_network_scanner, menu);
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

    public void goNext(View view) {
        Intent intent = new Intent(this, NetworkChecker.class);
        startActivity(intent);
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void startScan(View view) {
        int captureTime = 10;
        int sleepTime = 2;
        RSSIFileReader red = new RSSIFileReader();
        StartTcpdumpTask scanTask = new StartTcpdumpTask();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSSS");
        //get current date time with Date()
        Date date = new Date();
        String time = dateFormat.format(date);
        scanTask.record(captureTime,"initialScan");
        try {
            System.out.println("WE SHOULD SLEEP HERE");
            TimeUnit.SECONDS.sleep(captureTime + sleepTime);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        double starttime = red.formatTime(time,true);
        Capture initCap = red.readFile("/sdcard/wifiJedi_data/initialScan.rssi", starttime, starttime+captureTime);

        if ( initCap == null) {
            System.out.println("OOOOOOOOMG; THERE HAS BEEN AN ERROR ");
            return;
        }
        TextView tv = (TextView) findViewById(R.id.scannerText);
        String t = "";
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear(); editor.commit();
        ArrayList<Network> ins = initCap.getFiveMostActiveNetworks();
        if ( ins == null || ins.isEmpty())
            System.out.println("ÖÖÖHM, FEHLER?");
        for( Network n : ins) {
            t += n.toString() + "\n";
        }
        System.out.println("SIZE = " + ins.size());
        if ( 0 < ins.size() ) {
            System.out.println(ins.get(0).toString());
            editor.putString("MAC01", ins.get(0).getMACAdress());
            editor.putString("NET01", ins.get(0).toString());
        }
        if ( 1 < ins.size() ) {
            System.out.println(ins.get(1).toString());
            editor.putString("MAC02", ins.get(1).getMACAdress());
            editor.putString("NET02", ins.get(1).toString());
        }
        if ( 2 < ins.size() ) {
            System.out.println(ins.get(2).toString());
            editor.putString("MAC03", ins.get(2).getMACAdress());
            editor.putString("NET03", ins.get(2).toString());
        }
        if ( 3 < ins.size() ) {
            System.out.println(ins.get(3).toString());
            editor.putString("MAC04", ins.get(3).getMACAdress());
            editor.putString("NET04", ins.get(3).toString());
        }
        if ( 4 < ins.size() ) {
            System.out.println(ins.get(4).toString());
            editor.putString("MAC05", ins.get(4).getMACAdress());
            editor.putString("NET05", ins.get(4).toString());
        }
        editor.commit();
        tv.setText(t);

    }
}
