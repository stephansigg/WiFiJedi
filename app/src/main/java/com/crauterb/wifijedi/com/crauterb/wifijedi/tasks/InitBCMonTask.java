package com.crauterb.wifijedi.com.crauterb.wifijedi.tasks;

/**
 * Created by christoph on 26.01.15.
 */

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;

import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by christoph on 26.01.15.
 */
public class InitBCMonTask  extends AsyncTask<Void, Void, Void> {

    /** Need to keep a variable here for context*/
    private Context context;

    public InitBCMonTask(Context newContext ) {
        // Parse Context to Task
        this.context = newContext;
    }

    @Override
    public Void doInBackground(Void... params) {

        Shell.SU.run("mkdir -p /sdcard/bcmon");
        Shell.SU.run("rm /sdcard/bcmon/*");

        System.out.println("Copying Files");
        copyFileFromAssetsToSD("/sdcard/bcmon/", "setup.sh");
        copyFileFromAssetsToSD("/sdcard/bcmon/", "bcm4329.ko");
        copyFileFromAssetsToSD("/sdcard/bcmon/", "fw_bcm4329.bcmon.bin");


        System.out.println("Initializing bcmon");
        Shell.SU.run("cd /sdcard/bcmon; sh setup.sh");
        Shell.SU.run("pwd");
        Shell.SU.run("rm /sdcard/*.rssi");
        System.out.println("Done initializing!");

        return null;
    }

    /**
     * This method is merely used to copy the files from the assets to the SD-Card via a Byte-Stream
     * @param path The path where file should be copied to
     * @param filename The file that is to be opened from the assets
     */
    private void copyFileFromAssetsToSD (String path, String filename) {
        Resources resources;
        File file = new File(path, filename);
        FileOutputStream fos;
        InputStream is;
        byte[] data = null;
        resources = this.context.getResources();

        try {
            is = resources.getAssets().open(filename);
            data = ByteStreams.toByteArray(is);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

