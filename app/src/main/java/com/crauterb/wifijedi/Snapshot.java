package com.crauterb.wifijedi;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.crauterb.wifijedi.com.crauterb.wifijedi.tasks.StartTcpdumpTask;
import com.crauterb.wifijedi.rssiReader.RSSIFileReader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private Camera camera;
    private int cameraId = 0;

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

    private void takePhoto() {
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


    public void startSlideshow(View view) {


        int t;
        while(true) {
            // record
            recordIntoFile("toClassifyInSlideshow");
            // evaluate file within classifier
            //@TODO: Add correct classifier here
            t = randInt(1,10);
            if ( t <= 3 ) {
               // TAKE PICTURE
                System.out.println("SMILE! WE ARE TAKING A PICTURE!");
                //dispatchTakePictureIntent();
            }
            if ( !isCaptureActive ) {
                break;
            } else {
                continue;
            }

        }

    }

    public void stopCapture(View view){
        System.out.println("Stopping capture");
        isCaptureActive = false;
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



}
