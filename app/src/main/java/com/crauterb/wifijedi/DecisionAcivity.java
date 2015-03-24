package com.crauterb.wifijedi;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class DecisionAcivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decision_acivity);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_decision_acivity, menu);
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

    public void startSnapshot(View view) {
        //StartTcpdumpTask scanTask = new StartTcpdumpTask();
        //scanTask.record(4,"initialScan");
        Intent intent = new Intent(this, Snapshot.class);
        startActivity(intent);
    }

    public void goEvaluation(View view) {
        Intent intent = new Intent(this, EvaluationAcitivity.class);
        startActivity(intent);
    }

    public void goBack(View view) {
        //StartTcpdumpTask scanTask = new StartTcpdumpTask();
        //scanTask.record(4,"initialScan");
        Intent intent = new Intent(this, NetworkChecker.class);
        startActivity(intent);
    }

    public void startSlideshow(View view) {
        //StartTcpdumpTask scanTask = new StartTcpdumpTask();
        //scanTask.record(4,"initialScan");
        Intent intent = new Intent(this, Slideshow.class);
        startActivity(intent);
    }
}
