package com.crauterb.wifijedi.rssiReader;

/**
 * Created by christoph on 08.02.15.
 */

/**
 **************** Imports ****************
 */

import com.crauterb.wifijedi.rrsiLearning.Capture;
import com.crauterb.wifijedi.rrsiLearning.Node;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Christoph Rauterberg on 02.12.14.
 */
public class RSSIFileReader {

    public ArrayList<String> MACs;

    public ArrayList<String> SSIDs;

    public ArrayList<Integer> nums;

    public String[] text = {"","","","",""};

    /**
     * This method reads the information provided by TCPDUMP and written into a file into a list of DataNodes
     * @param file The name of the file containing the read tcpdump lines
     * @return A list containing DataNodes, that provide the information form the TCPDUMP-file
     */
    public Capture readFile(String file, double start, double end) {

        // Start a capture
        Capture newCapture = new Capture(start,end);

        System.out.println("Now reading file: " + file);
        // We will first check, if we can locate the file.
        File f = new File(file);
        boolean empty =  f.length() == 0;
        if( ! f.exists() ) {
            System.out.println("The file does not exist");
            //TODO: Implement a good way --> Exceptions
            return null;
        }
        if (empty) {
            System.out.println("The read file is empty!");
            //TODO: Implement a good way --> Exceptions
            return null;
        }
        // We can now assume, that the file does exist and that it is not empty.
        // This additional catch-Block is only here because it has to be here.
        FileReader fr = null;
        try{
            fr = new FileReader(file);

        } catch ( FileNotFoundException e) {
            System.out.println("The file " + file + " could not be found!");
            System.out.println(e.getStackTrace());
            //TODO: Implement a good way --> Exceptions
            return null;
        }
        BufferedReader br = new BufferedReader(fr);
        // Now simply read the line and process everything accordingly:
        String line;
        int rssi;
        String beacon;
        String mac ;
        int index;
        int pos;
        Node tmpNode;
        double time;

        try {
            while( (line = br.readLine()) != null ) {
                //TODO: All offset for the parsing of the tcpdump values is hardcoded ---> Change that?
                //System.out.println(line);
                //
                // Read the Source MAC Address
                //
                if ( line.contains("SA:") ) {
                    pos = line.indexOf("SA:");
                    mac = line.substring(pos+3,pos+20);
                } else {
                    //System.out.println("No MAC-address recorded");
                    mac = "";
                }
                //
                // Then, try to read the SSID
                //
                if ( line.contains("Beacon")) {
                    pos = line.indexOf("Beacon");
                    pos = pos + 8;
                    if ( line.charAt(pos) == ')' || line.charAt(pos) == ']') {
                        //System.out.println("ERROR: NO BEACON SET");
                        beacon = "";
                    } else {
                        int s;
                        for ( s = pos; line.charAt(s) != ')' && line.charAt(s) != ']'; s++);
                        beacon = line.substring(pos, s);

                    }
                } else {
                    //System.out.println("ERROR. NO BEACON RECORDED");
                    beacon = "";
                }

                //
                // Now read the RSSI value
                //
                pos = line.indexOf("dB");
                if ( pos != -1 ) {
                    rssi = Integer.parseInt(line.substring((pos-3),pos));
                } else {
                    continue;
                }

                //
                // Finally, read the time stamp
                //
                time = 0.0;
                time += (Double.parseDouble(line.substring(0,2)) * 60.0 * 60.0);
                time += (Double.parseDouble(line.substring(3,5)) * 60.0);
                time += (Double.parseDouble(line.substring(6,15)));
                //System.out.println("Check this out");
                //System.out.println(line);
                //System.out.println(time + " : " + rssi + " [ " + mac + " : " + beacon + " ]");


                // We now have mac, beacon, rssi and ti me set.
                tmpNode = new Node(time,rssi,mac);
                newCapture.addNode(tmpNode);
                if ( ! newCapture.isNetworkRecorded(mac)) {
                    newCapture.addNetwork(mac,beacon);

                } else if ( newCapture.isNetworkRecorded(mac) && ! newCapture.isSSIDRecorded(beacon)) {
                    newCapture.getNetworkByMAC(mac).setSSID(beacon);
                }
                newCapture.getNetworkByMAC(mac).addNode(tmpNode);

            }
        } catch ( IOException e ) {
            System.out.println("The lines within the given file could not be read");
            System.out.println(e.getStackTrace());
            //TODO: Implement a good way --> Exceptions
            return null;
        }
        System.out.println(newCapture);
        return newCapture;
    }


    public int findMax(ArrayList<Integer> list ) {
        int max = 0;
        int maxIn = 0;
        for( int i = 0; i < list.size(); i++) {
            if ( list.get(i) >= list.get(maxIn)) {
                max = list.get(i);
                maxIn = i;
            }
        }
        if ( max == 0) {
            return -1;
        }
        return maxIn;
    }

    public double formatTime( String time, boolean systime) {
        double starttime = 0.0;
        starttime += (Double.parseDouble(time.substring(0,2)) * 60.0 * 60.0);
        starttime += (Double.parseDouble(time.substring(3,5)) * 60.0);
        if ( systime) {
            starttime += (Double.parseDouble(time.substring(6)));
        } else {
            starttime += (Double.parseDouble(time.substring(6,15)));
        }


        return starttime;
    }




}

