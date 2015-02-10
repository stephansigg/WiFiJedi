package com.crauterb.wifijedi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.crauterb.wifijedi.com.crauterb.wifijedi.tasks.StartTcpdumpTask;
import com.crauterb.wifijedi.rrsiLearning.Capture;
import com.crauterb.wifijedi.rrsiLearning.RSSILearner;
import com.crauterb.wifijedi.rssiReader.RSSIFileReader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class Slideshow extends ActionBarActivity {

    final private static int[] movementList = new int[]{R.drawable.swipeleft, R.drawable.swiperight, R.drawable.towards, R.drawable.away};
    final private static int[] imageList = new int[] {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5, R.drawable.image6};
    private  int currentImageID = 0;
    private int currentMovementImageID = 0;

    public static int MOVEMENT_SWIPELFT = 0;
    public static int MOVEMENT_SWIPERIGHT = 1;
    public static int MOVEMENT_TOWARDS = 2;
    public static int MOVEMENT_AWAY = 3;

    private boolean isCaptureActive = false;
    private StartTcpdumpTask scanTask = new StartTcpdumpTask();
    private RSSIFileReader red = new RSSIFileReader();

    private int captureTime = 5;
    public RSSILearner myLearner = new RSSILearner(0.2);;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        myLearner = new RSSILearner(0.2);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_slideshow, menu);
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

    public void trainClassifier(View view) {

        // @TODO: LOAD FROM OPTIONS HERE,WHICH CLASSES ARE TO BE LEARNED

        ImageView image;
        image = (ImageView) findViewById(R.id.imageView);
        System.out.println("Capturing disturbed data");
        image.setImageResource(movementList[0]);

        Capture myCapture;
        double time = getSysTime();
        recordIntoFile(RSSILearner.FILENAME_CLASS_SWIPE_LEFT);

        myCapture = red.readFile("/sdcard/wifiJedi_data/" + RSSILearner.FILENAME_CLASS_SWIPE_LEFT + ".rssi", time, time+captureTime);

        System.out.println("Training data successfully created");

    }

    public void startSlideshow(View view) {
        ImageView image;
        image = (ImageView) findViewById(R.id.imageView);

        image.setImageResource(imageList[currentImageID++%6]);
    }

    public void stopCapture(View view){
        System.out.println("Stopping capture");
        isCaptureActive = true;
    }

    public int getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(int captureTime) {
        this.captureTime = captureTime;
    }

    private void recordIntoFile(String filename) {
        scanTask.record(captureTime,filename);
        try {
            System.out.println("WE SHOULD SLEEP HERE");
            TimeUnit.SECONDS.sleep(captureTime + 1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private double getSysTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSSS");
        //get current date time with Date()
        Date date = new Date();
        String time = dateFormat.format(date);
        return red.formatTime(time,true);
    }
}
