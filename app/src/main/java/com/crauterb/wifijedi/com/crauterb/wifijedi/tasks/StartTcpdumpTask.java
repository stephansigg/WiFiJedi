package com.crauterb.wifijedi.com.crauterb.wifijedi.tasks;

/**
 * Created by christoph on 26.01.15.
 */

import android.os.AsyncTask;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by christoph on 26.01.15.
 */
public class StartTcpdumpTask extends AsyncTask<String, Void, Integer> {

    /** Need parameter here for recording time */
    private int time;

    public void record(int time,String filename) {
        // Toggle time slice
        this.time = time;
        this.execute(filename);
    }
    @Override
    public void onPreExecute(){
        Shell.SU.run("mkdir -p /sdcard/wifiJedi_data");
    }
    @Override
    public Integer doInBackground(String... filename) {

        String delCommand = "rm /sdcard/wifiJedi_data/" + filename + ".rssi";
        Shell.SU.run(delCommand );
        String cdCommand = "cd /sdcard/wifiJedi_data";
        System.out.println("Write recorded stuff to file: " + filename[0]);
        String command = "tcpdump -vvv -i eth0  -e -s0 > ./";
        command += filename[0] + ".rssi ";
        command += "& sleep ";
        command += this.time;
        command += "; kill $! ";

        List<String> output =  Shell.SU.run(cdCommand + ";" + command);
        System.out.println("tcpdumpoutput: " + output);
        System.out.println(command);
        System.out.println("--->Done Here");
        return null;
    }
    
    public void onPostExecute(Integer result) {
        Shell.SU.run("pkill tcpdump");
        return;
    }
}