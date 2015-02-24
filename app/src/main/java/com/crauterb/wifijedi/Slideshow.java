package com.crauterb.wifijedi;

import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Slideshow extends ActionBarActivity {

    private static Random rand = new Random();

    final private static int[] movementList = new int[]{R.drawable.swipeleft, R.drawable.swiperight, R.drawable.towards, R.drawable.away};
    final private static int[] stop = new int[]{R.drawable.stop};
    final private static int[] imageList = new int[] {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5, R.drawable.image6};
    private  int currentImageID = 0;
    private int currentMovementImageID = 0;

    public static int MOVEMENT_SWIPELFT = 0;
    public static int MOVEMENT_SWIPERIGHT = 1;
    public static int MOVEMENT_TOWARDS = 2;
    public static int MOVEMENT_AWAY = 3;

    public int pos;
    int[] classesToBeTrained  = {MOVEMENT_SWIPELFT, MOVEMENT_TOWARDS};
    int numberOfTimeSlices = 5;

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
        this.isCaptureActive = false;
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

        int timeslicetime = 2;

        final ImageView image;
        image = (ImageView) findViewById(R.id.slideshowView);
        System.out.println("Capturing disturbed data");
        System.out.println(image.toString());
        //image.setImageResource(movementList[currentMovementImageID]);

        Capture myCapture;
        double time = getSysTime();
        System.out.println("START TRAINING");
        isCaptureActive = true;
        new TrainingThisTask(5,classesToBeTrained, timeslicetime, 0,0).execute();
        System.out.println("Done here");
        System.out.println("Training data successfully created");

    }

    public void startSlideshow(View view) {
        isCaptureActive = true;
        ImageView image;
        image = (ImageView) findViewById(R.id.slideshowView);
        System.out.println(image.toString());
        image.setImageResource(imageList[0]);
        new AnalyzeTask(0).execute();
    }

    public void stopCapture(View view){
        System.out.println("Stopping capture");
        isCaptureActive = false;
    }

    public int getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(int captureTime) {
        this.captureTime = captureTime;
    }

    private void recordIntoFile(String filename) {
        new StartTcpdumpTask().record(captureTime,filename);
        try {
            System.out.println("WE SHOULD SLEEP HERE");
            TimeUnit.SECONDS.sleep(captureTime + 1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        scanTask.cancel(true);
    }

    private double getSysTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSSS");
        //get current date time with Date()
        Date date = new Date();
        String time = dateFormat.format(date);
        return red.formatTime(time,true);
    }

    private int randInt(int min, int max) {

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt((max - min) + 1) + min;
    }

    private class TrainingThisTask extends AsyncTask<Void, Void, Integer> {

        private int timeSliceDuration;

        private int numberOfTimeSlices;

        private int[] classesToBeTrained;

        int pos = 0;

        int count;

        public TrainingThisTask( int numberOfTimeSlices, int [] classesToBeTrained, int timeSliceDuration, int pos, int count){
            this.timeSliceDuration = timeSliceDuration;
            this.numberOfTimeSlices = numberOfTimeSlices;
            this.classesToBeTrained = classesToBeTrained;
            this.pos = pos;
            this.count = count;
        }

        @Override
        protected void onPreExecute() {
            ImageView image = (ImageView) findViewById(R.id.slideshowView);
            if ( count < numberOfTimeSlices*classesToBeTrained.length && isCaptureActive) {
                image.setImageResource(movementList[classesToBeTrained[pos++ % classesToBeTrained.length]]);
            }
        }


        @Override
        protected Integer doInBackground(Void... params) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                // RECORD
                System.out.println("WE SHOULD SLEEP HERE");
                TimeUnit.SECONDS.sleep(timeSliceDuration);
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return 1;
        }

        public void onPostExecute(Integer result) {

            if ( count < numberOfTimeSlices*classesToBeTrained.length && isCaptureActive) {
                System.out.println("Run new task");
                System.out.println(count);
                new TrainingThisTask(this.numberOfTimeSlices,this.classesToBeTrained, this.timeSliceDuration, this.pos++%this.classesToBeTrained.length,  this.count+1).execute();
            } else {
                System.out.println("Done for the day");
                ImageView image = (ImageView) findViewById(R.id.slideshowView);
                image.setImageResource(android.R.color.transparent);
                return;
            }
        }
    }

    private class AnalyzeTask extends AsyncTask<Void, Void, Integer> {

        private int timeSliceDuration = 2;

        private int pos;

        @Override
        protected void onPreExecute() {

        }

        public AnalyzeTask(int pos) {
            this.pos = pos;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                // RECORD
                System.out.println("WE SHOULD SLEEP HERE");
                TimeUnit.SECONDS.sleep(timeSliceDuration);
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return -1;
            }
            return 1;
        }

        @Override
        public void onPostExecute(Integer result) {
            ImageView image = (ImageView) findViewById(R.id.slideshowView);
            int label;

            // EVALUATE THE READ VALUES
            label = randInt(1,10);
            System.out.println("Label = " + label);
            if  ( label > 8 ) {
                image.setImageResource(imageList[(pos+1)%imageList.length]);
            }
            if ( isCaptureActive )
                new AnalyzeTask(pos+1).execute();
            else {
                System.out.println("Done for the day with the slideshow");
                image.setImageResource(stop[0]);
            }
        }


    }
}
