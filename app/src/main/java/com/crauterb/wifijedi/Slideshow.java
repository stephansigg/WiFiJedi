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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class Slideshow extends ActionBarActivity {

    private static Random rand = new Random();

    final private static int[] movementList = new int[]{R.drawable.swipeleft, R.drawable.swiperight, R.drawable.towards, R.drawable.away, R.drawable.x};
    final private static int[] stop = new int[]{R.drawable.stop};
    final private static int[] imageList = new int[] {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5, R.drawable.image6};
    private  int currentImageID = 0;
    private int currentMovementImageID = 0;

    public static int MOVEMENT_SWIPELFT = 0;
    public static int MOVEMENT_SWIPERIGHT = 1;
    public static int MOVEMENT_TOWARDS = 2;
    public static int MOVEMENT_AWAY = 3;
    public static int MOVEMENT_NONE = 4;

    public int pos;
    int[] classesToBeTrained  = {MOVEMENT_SWIPELFT, MOVEMENT_TOWARDS, MOVEMENT_NONE};
    int numberOfTimeSlices = 5;

    Set<String> macsToBeUsed = new HashSet<String>();

    ArrayList<Capture> trainingCaptures;

    ArrayList<Double[]> trainingFeatures = new ArrayList<Double[]>();


    private boolean isCaptureActive = false;

    private RSSIFileReader red = new RSSIFileReader();

    double sliceDuration;
    int k;
    private int captureTime = 5;
    public RSSILearner myLearner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        myLearner = new RSSILearner(0.2);
        trainingCaptures = new ArrayList<Capture>();
        this.isCaptureActive = false;

        // get checked networks
        if ( settings.getBoolean("UseNet1", false)) {
            macsToBeUsed.add(settings.getString("MAC01", "ee:ee:ee:ee:ee:ee"));
        }
        if ( settings.getBoolean("UseNet2", false)) {
            macsToBeUsed.add(settings.getString("MAC02", "ee:ee:ee:ee:ee:ee"));
        }
        if ( settings.getBoolean("UseNet3", false)) {
            macsToBeUsed.add(settings.getString("MAC03", "ee:ee:ee:ee:ee:ee"));
        }
        if ( settings.getBoolean("UseNet4", false)) {
            macsToBeUsed.add(settings.getString("MAC04", "ee:ee:ee:ee:ee:ee"));
        }
        if ( settings.getBoolean("UseNet5", false)) {
            macsToBeUsed.add(settings.getString("MAC05", "ee:ee:ee:ee:ee:ee"));
        }
        System.out.println("Following macs are to be used");
        System.out.println(macsToBeUsed);
        for( String s : macsToBeUsed) {
            System.out.println(s);
        }

        this.numberOfTimeSlices = settings.getInt("Parameter_cicle", 5);
        this.sliceDuration = (double) settings.getFloat("Parameter_duration", (float) 0.2);
        this.k = settings.getInt("Parameter_k", 7);

        myLearner = new RSSILearner(sliceDuration,k);
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

        System.out.println(image.toString());
        //image.setImageResource(movementList[currentMovementImageID]);

        Capture myCapture;
        double time = getSysTime();
        System.out.println("START TRAINING");
        isCaptureActive = true;
        new TrainingTask(numberOfTimeSlices,classesToBeTrained, timeslicetime, 0,0).execute();
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

    private class TrainingTask extends AsyncTask<Void, Void, Integer> {

        private int timeSliceDuration;

        private int numberOfTimeSlices;

        private int[] classesToBeTrained;

        int pos = 0;

        int count;

        public TrainingTask(int numberOfTimeSlices, int[] classesToBeTrained, int timeSliceDuration, int pos, int count){
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
                image.setImageResource(movementList[classesToBeTrained[pos % classesToBeTrained.length]]);
            }
        }


        @Override
        protected Integer doInBackground(Void... params) {
            Capture cap;
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                double time = getSysTime();
                System.out.println("Previous task should have been finished");
                new StartTcpdumpTask().record(timeSliceDuration, "TRAINING" + count);
                System.out.println("WE SHOULD SLEEP HERE");
                TimeUnit.SECONDS.sleep(timeSliceDuration+1);
                cap = red.readFile("/sdcard/wifiJedi_data/TRAINING" + count + ".rssi",time,time+timeSliceDuration, macsToBeUsed);
                trainingCaptures.add(cap);
                if ( cap.isEmpty() ) {
                    System.out.println("LEEEEER");
                }
                Double[] new_F;
                int currentLabel = classesToBeTrained[pos++ % classesToBeTrained.length];
                List<double[]> feat = new ArrayList<double[]>();
                double[] fe;
                System.out.println("Converting features");
                for( Double[] f : cap.splitData(0.2)) {
                    fe = new double[RSSILearner.NUMBER_OF_FEATURES];
                    System.out.print("[");
                    for( int i = 0; i < f.length; i++) {
                        System.out.print(f[i] + " ");
                        if ( i != f.length - 1 )
                            System.out.print(", ");
                    }
                    System.out.print("]");
                    /*trainingFeatures.add(f);
                    int count = 0;
                    for( Double d : f ) {
                        fe[count] = f[count];

                        count++;
                    }
                    System.out.print("[");
                    for( int i = 0; i < fe.length; i++) {
                        System.out.print(fe[i]);
                    }
                    System.out.println("]");*/
                    //System.out.println(fe);
                    feat.add(new double[]{f[0],f[1],0.0,0.0,f[4]});
                }
                /*for( int i = 0; i < feat.size(); i++ ) {
                    System.out.print("[");
                    for( int j = 0; j < feat.get(i).length; j++) {
                        System.out.print(feat.get(i)[j] +" ,");
                    }
                    System.out.println("]");
                }*/
                myLearner.addLearningData(feat,currentLabel);
                System.out.println("Succesfully added learning data with label: " + currentLabel);

                //System.out.println(cap);
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
                System.out.println("Number of training rounds: " + numberOfTimeSlices);
                new TrainingTask(this.numberOfTimeSlices,this.classesToBeTrained, this.timeSliceDuration, this.pos++%this.classesToBeTrained.length,  this.count+1).execute();
            } else {
                System.out.println("Done for the day");
                ImageView image = (ImageView) findViewById(R.id.slideshowView);
                image.setImageResource(android.R.color.transparent);
                //@TODO: TMP
                double t_1 = getSysTime();

                //System.out.println("Time: " + (getSysTime() - t_1));
                //trainingCaptures.get(trainingCaptures.size()-1).printNodes();
                return;
            }
        }
    }

    private class AnalyzeTask extends AsyncTask<Void, Void, Integer> {

        private int timeSliceDuration = 2;

        private int pos;

        @Override
        protected void onPreExecute() {
            myLearner.trainClassifier();

        }

        public AnalyzeTask(int pos) {
            this.pos = pos;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            Capture cap;
            int result;
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                double time = getSysTime();
                System.out.println("Previous task should have been finished");
                new StartTcpdumpTask().record(timeSliceDuration, "SLIDESHOW");
                System.out.println("WE SHOULD SLEEP HERE");
                TimeUnit.SECONDS.sleep(timeSliceDuration+1);
                cap = red.readFile("/sdcard/wifiJedi_data/SLIDESHOW.rssi",time,time+timeSliceDuration, macsToBeUsed);
                //trainingCaptures.add(cap);
                if ( cap.isEmpty() ) {
                    System.out.println("LEEEEER");
                }
                Double[] new_F;
                //int currentLabel = numberOfTimeSlices*classesToBeTrained.length;
                List<double[]> feat = new ArrayList<double[]>();
                double[] fe = new double[RSSILearner.NUMBER_OF_FEATURES];
                for( Double[] f : cap.splitData(0.2)) {
                    fe = new double[RSSILearner.NUMBER_OF_FEATURES];
                    //trainingFeatures.add(f);
                    /*int count = 0;
                    System.out.print("[");
                    for( int i = 0; i < f.length; i++) {
                        if ( i == 2 && i == 3)
                            System.out.print(f[i]);
                        else
                            System.out.print("0.0");
                        if ( i != f.length - 1 )
                            System.out.print(", ");
                    }/*
                    for( Double d : f ) {
                        fe[count] = f[count];
                        count++;
                    }
                    System.out.print("[");
                    for( int i = 0; i < fe.length; i++) {
                        System.out.print(fe[i]);
                    }*/
                    System.out.println("]");

                    feat.add(new double[]{f[0],f[1],0.0,0.0,f[4]});
                }
                result = myLearner.classify(feat);
                System.out.println("Classification says: " +result);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return -1;
            }
            return result;
        }

        @Override
        public void onPostExecute(Integer result) {
            ImageView image = (ImageView) findViewById(R.id.slideshowView);
            //int label;

            // EVALUATE THE READ VALUES
            int label = randInt(1,10);
            //System.out.println("Label = " + label);
            if  (result == RSSILearner.MOV_LEFTTORIGHT) {
                image.setImageResource(imageList[(pos+1)%imageList.length]);
            } else if ( result == RSSILearner.MOV_DOWNTOWARDS) {
                System.out.println("Stopping capture");
                isCaptureActive = false;
            } else if ( result == RSSILearner.UNDISTURBED) {
                System.out.println("No movement. Do nothing");
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
