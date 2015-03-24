package com.crauterb.wifijedi;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.crauterb.wifijedi.com.crauterb.wifijedi.tasks.StartTcpdumpTask;
import com.crauterb.wifijedi.rrsiLearning.Capture;
import com.crauterb.wifijedi.rrsiLearning.RSSILearner;
import com.crauterb.wifijedi.rssiReader.RSSIFileReader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Snapshot extends ActionBarActivity {

    private static Random rand = new Random();
    private boolean isCaptureActive = false;
    private StartTcpdumpTask scanTask = new StartTcpdumpTask();
    private RSSIFileReader red = new RSSIFileReader();
    private int captureTime = 5;
    public static int count = 0;
    int TAKE_PHOTO_CODE = 0;

    ArrayList<Capture> trainingCaptures;
    int numberOfTimeSlices = 5;
    final private static int[] movementList = new int[]{R.drawable.swipeleft, R.drawable.swiperight, R.drawable.towards, R.drawable.away, R.drawable.x, R.drawable.q};
    final private static int[] stop = new int[]{R.drawable.stop};

    private Camera camera;
    private int cameraId = 0;

    public static int MOVEMENT_NONE = 4;

    public static int MOVEMENT_YES = 5;
    int[] classesToBeTrained  = {MOVEMENT_YES, MOVEMENT_NONE};

    double sliceDuration;
    int k;

    public RSSILearner myLearner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snapshot);

        // do we have a camera?
        if (!getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
                    .show();
        } else {
            /*cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                safeCameraOpen(cameraId);
            }*/
        }
        camera = Camera.open();

        // Create surface for camera
        SurfaceView view = new SurfaceView(this);
        try {
            System.out.println("Try to set view!");
            camera.setPreviewDisplay(view.getHolder());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        camera.startPreview();
        Camera.Parameters params = camera.getParameters();
        params.setJpegQuality(100);
        camera.setParameters(params);
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        this.numberOfTimeSlices = settings.getInt("Parameter_cicle", 5);
        this.sliceDuration = (double) settings.getFloat("Parameter_duration", (float) 0.2);
        this.k = settings.getInt("Parameter_k", 7);

        myLearner = new RSSILearner(sliceDuration,k);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_snapshot, menu);
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

    public void takeSnapshot(View view) {
        //dispatchTakePictureIntent();

        System.out.println("SMILE!");

        camera.takePicture(null, null, mCall);

    }

    protected void takePicture(){
        camera.takePicture(null, null, mCall);
    }



    Camera.PictureCallback mCall = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //decode the data obtained by the camera into a Bitmap
            final ImageView display;

            Bitmap bitmapPicture
                    = BitmapFactory.decodeByteArray(data, 0, data.length);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            //File image = new File("/sdcard/wifiJedi_data/" + timeStamp + "_picture.jpg");
            display = (ImageView) findViewById(R.id.snapshotImage);
            display.setImageBitmap(bitmapPicture);
            OutputStream stream = null;
            try {
                stream = new FileOutputStream("/sdcard/wifiJedi_data/" + timeStamp + "_picture.jpg");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("Errorwhie savong picture to SDCard");
            }
    /* Write bitmap to file using JPEG and 80% quality hint for JPEG. */
            bitmapPicture.compress(Bitmap.CompressFormat.JPEG, 80, stream);

        }
    };

    public void train(View view) {

        // @TODO: LOAD FROM OPTIONS HERE,WHICH CLASSES ARE TO BE LEARNED

        int timeslicetime = 2;

        final ImageView image;
        image = (ImageView) findViewById(R.id.snapshotImage);

        System.out.println(image.toString());
        //image.setImageResource(movementList[currentMovementImageID]);

        Capture myCapture;
        double time = getSysTime();
        System.out.println("START TRAINING");
        isCaptureActive = true;
        new TrainingTask(5,classesToBeTrained, timeslicetime, 0,0).execute();
        System.out.println("Done here");
        System.out.println("Training data successfully created");

    }


    public void startShowcase(View view) {

        myLearner.trainClassifier();
        isCaptureActive = true;

        new AnalyzeTask(0).execute();

    }

    public void stopCapture(View view){
        System.out.println("Stopping capture");
        isCaptureActive = false;
    }


    private int randInt(int min, int max) {

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt((max - min) + 1) + min;
    }




    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private double getSysTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSSS");
        //get current date time with Date()
        Date date = new Date();
        String time = dateFormat.format(date);
        return red.formatTime(time,true);
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
            ImageView image = (ImageView) findViewById(R.id.snapshotImage);
            if ( count % 2 == 0) {
                image.setImageResource(movementList[4]);
            } else {
                image.setImageResource(movementList[5]);
            }

        }


        @Override
        protected Integer doInBackground(Void... params) {
            Capture cap;
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                double time = getSysTime();
                System.out.println("Previous task should have been finished");
                new StartTcpdumpTask().record(timeSliceDuration, "TRAININGSNAP" + count);
                System.out.println("WE SHOULD SLEEP HERE");
                TimeUnit.SECONDS.sleep(timeSliceDuration+1);
                cap = red.readFile("/sdcard/wifiJedi_data/TRAININGSNAP" + count + ".rssi",time,time+timeSliceDuration);
                System.out.println("File read successfully");
                //trainingCaptures.add(cap);
                System.out.println("Here is stuff");
                int currentLabel;
                System.out.println(count);
                if ( count % 2 == 0) {
                    currentLabel = 4;
                } else {
                    currentLabel = 5;
                }
                List<double[]> feat = new ArrayList<double[]>();
                double[] fe;
                System.out.println("Converting features");
                for( Double[] f : cap.splitData(0.2)) {

                    feat.add(new double[]{f[0],f[1],f[2],f[3],f[4]});
                }
                /*for( int i = 0; i < feat.size(); i++ ) {
                    System.out.print("[");
                    for( int j = 0; j < feat.get(i).length; j++) {
                        System.out.print(feat.get(i)[j] +" ,");
                    }
                    System.out.println("]");
                }*/
                System.out.println("Here is stuff");
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
                new TrainingTask(this.numberOfTimeSlices,this.classesToBeTrained, this.timeSliceDuration, this.pos++%this.classesToBeTrained.length,  this.count+1).execute();
            } else {
                System.out.println("Done for the day");
                ImageView image = (ImageView) findViewById(R.id.snapshotImage);
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
                new StartTcpdumpTask().record(timeSliceDuration, "SNAPSHOT");
                System.out.println("WE SHOULD SLEEP HERE");
                TimeUnit.SECONDS.sleep(timeSliceDuration+1);
                cap = red.readFile("/sdcard/wifiJedi_data/SNAPSHOT.rssi",time,time+timeSliceDuration);
                //trainingCaptures.add(cap);
                Double[] new_F;
                //int currentLabel = numberOfTimeSlices*classesToBeTrained.length;
                List<double[]> feat = new ArrayList<double[]>();
                for( Double[] f : cap.splitData(0.2)) {
                    feat.add(new double[]{f[0],f[1],f[2],f[3],f[4]});
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
            ImageView image = (ImageView) findViewById(R.id.snapshotImage);
            //int label;

            // EVALUATE THE READ VALUES
            int label = randInt(1,10);
            //System.out.println("Label = " + label);
            if  (result == 5) {
                takePicture();
            }  else if ( result == RSSILearner.UNDISTURBED) {
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
