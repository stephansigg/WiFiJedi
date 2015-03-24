package com.crauterb.wifijedi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

public class NetworkChecker extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_checker);

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        int i = 0;
        String t = "";

        //Box 1
        t = settings.getString("NET01", "");
        CheckBox c = (CheckBox) findViewById(R.id.NetcheckBox1);
        if ( t == "") {
            c.setVisibility(View.GONE);
        } else {
            c.setVisibility(View.VISIBLE);
            c.setText(t);
        }


        //Box 2
        t = settings.getString("NET02", "");
        c = (CheckBox) findViewById(R.id.NetcheckBox2);
        if ( t == "") {
            c.setVisibility(View.GONE);
        } else {
            c.setVisibility(View.VISIBLE);
            c.setText(t);
        }

        //Box 3
        t = settings.getString("NET03", "");
        c = (CheckBox) findViewById(R.id.NetcheckBox3);
        if ( t == "") {
            c.setVisibility(View.GONE);
        } else {
            c.setVisibility(View.VISIBLE);
            c.setText(t);
        }

        //Box 4
        t = settings.getString("NET04", "");
        c = (CheckBox) findViewById(R.id.NetcheckBox4);
        if ( t == "") {
            c.setVisibility(View.GONE);
        } else {
            c.setVisibility(View.VISIBLE);
            c.setText(t);
        }

        //Box 5
        t = settings.getString("NET05", "");
        c = (CheckBox) findViewById(R.id.NetcheckBox5);
        if ( t == "") {
            c.setVisibility(View.GONE);
        } else {
            c.setVisibility(View.VISIBLE);
            c.setText(t);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_network_checker, menu);
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
        //StartTcpdumpTask scanTask = new StartTcpdumpTask();
        //scanTask.record(4,"initialScan");
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("UseNet1", ((CheckBox) findViewById(R.id.NetcheckBox1)).isChecked());

        editor.putBoolean("UseNet2", ((CheckBox) findViewById(R.id.NetcheckBox2)).isChecked());
        editor.putBoolean("UseNet3", ((CheckBox) findViewById(R.id.NetcheckBox3)).isChecked());
        editor.putBoolean("UseNet4", ((CheckBox) findViewById(R.id.NetcheckBox4)).isChecked());
        editor.putBoolean("UseNet5", ((CheckBox) findViewById(R.id.NetcheckBox5)).isChecked());
        editor.commit();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void goBack(View view) {
        //StartTcpdumpTask scanTask = new StartTcpdumpTask();
        //scanTask.record(4,"initialScan");
        Intent intent = new Intent(this, NetworkScanner.class);
        startActivity(intent);
    }

}
