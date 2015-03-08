package com.crauterb.wifijedi.rrsiLearning;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christoph on 09.02.15.
 */
public class Capture {



    public double startTime;

    public double endTime;

    public ArrayList<Network> networks = new ArrayList<Network>();

    public ArrayList<Node> data = new ArrayList<Node>();

    public Capture(double startTime, double endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean isNetworkRecorded(String mac ) {
        if ( this.networks == null)
            return false;
        for( Network n : this.networks) {
            if ( n.getMACAdress().equals(mac))
                return true;
        }
        return false;
    }

    public boolean isSSIDRecorded(String ssid) {
        if ( this.networks == null)
            return false;
        for( Network n : this.networks) {
            if ( n.getSSID().equals(ssid))
                return true;
        }
        return false;
    }

    public Network getNetworkByMAC( String mac) {
        if ( isNetworkRecorded(mac)) {
            for( Network n : this.networks) {
                if ( n.getMACAdress().equals(mac))
                    return n;
            }
        }
        return null;
    }

    public Network getNetworkBySSID( String ssid) {
        if ( isSSIDRecorded(ssid)) {
            for( Network n : this.networks) {
                if ( n.getSSID().equals(ssid))
                    return n;
            }
        }
        return null;
    }

    public void addNode(Node node) {
        this.data.add(node);
    }

    public void addNetwork(Network network) {
        this.networks.add(network);
        network.accordingCapture = this;
    }

    public void addNetwork(String mac) {
        addNetwork(new Network(mac, ""));
    }

    public void addNetwork(String mac, String ssid) {
        addNetwork(new Network(mac,ssid));
    }

    public int getTotalNumberOfDataPoints() {
        int sum = 0;
        for( Network n : this.networks) {
            sum += n.getNumberOfDataPoints();
        }
        return sum;
    }

    public ArrayList<Network> getFiveMostActiveNetworks() {
        ArrayList<Network> fiveNet = new ArrayList<Network>();
        int max = 0;
        for ( int i = 0; i < 5 && i < this.networks.size(); i++) {
            for( Network n : this.networks) {
                if (!fiveNet.contains(n) && n.getNumberOfDataPoints() > max ) {
                    max = n.getNumberOfDataPoints();
                    fiveNet.add(n);
                }
            }
            max = 0;
        }
        return fiveNet;
    }

    public List<double[]>  getNetworkFeatures(String mac, double timeSliceDuration) {
        ArrayList<double[]> netFeatures = new ArrayList<double[]>();
        Network n = getNetworkByMAC(mac);
        double recordingTime = this.endTime - this.startTime;
        for( double i = this.startTime; i < this.endTime; i += timeSliceDuration) {
            netFeatures.add(n.computeFeaturesForSlot(i,i+timeSliceDuration));
        }
        return netFeatures;
    }

    public String toString() {
        String t = "";
        t += "Started at " + this.startTime + " and ended at " + this.endTime + "\n";
        t += "Recorded " + this.networks.size() + " networks\n";
        int si = 0;
        for( Network n : this.networks) {
            si += n.getNumberOfDataPoints();
        }
        t += "Recorded " + si + " data points\n";
        return t;
    }

    public void printNodes() {
        System.out.println("Printing data of capture...");
        System.out.println("Started this capture at: " + this.startTime);
        System.out.println("Ended with this capture at: " + this.endTime);
        for( Node n : this.data) {
            System.out.println(n);
        }
    }

    public void splitData(double timeSlotLength) {
        ArrayList<Integer> slot = new ArrayList<Integer>();
        ArrayList<ArrayList<Integer>> allSlots = new ArrayList<ArrayList<Integer>>();
        double time = this.startTime + timeSlotLength;
        System.out.println("Start! "+ this.startTime );
        String t = "[ ";
        double newStart = data.get(0).timestamp;
        time = newStart;
        for( Node n : data) {
            if ( n.timestamp <= time ) {
                slot.add(n.RSSI);
                t += n.timestamp + ",";
            } else {
                allSlots.add(slot);
                t += "]";
                System.out.println(t);
                slot = new ArrayList<Integer>();
                slot.add(n.RSSI);
                time += timeSlotLength;
                t = "[ ";
            }
        }
        // COmputing features
        DescriptiveStatistics stats = new DescriptiveStatistics();
        double[] features = new double[RSSILearner.NUMBER_OF_FEATURES];
        for( ArrayList<Integer> li : allSlots){
            for( int x : li) {
                stats.addValue((double) x);
            }
            features[RSSILearner.POS_RSSIMEAN] = stats.getMean();
            features[RSSILearner.POS_RSSIMAX] = stats.getMax();
            features[RSSILearner.POS_RSSIMIN] = stats.getMin();
            features[RSSILearner.POS_RSSISTD] = stats.getStandardDeviation();
            features[RSSILearner.POS_NUMBEROFRSSI] = li.size();
            System.out.println("Computed Feature");
            t = "f_[";
            for( int i = 0; i < features.length; i++) {
                t += features[i] + ",";
            }
            t += "]";
            System.out.println(t);
        }


        System.out.println("Ende! " + this.endTime);
        double num = (this.endTime - this.startTime) / timeSlotLength;
        System.out.println(num);
        System.out.println(allSlots.size());
    }




    

}
