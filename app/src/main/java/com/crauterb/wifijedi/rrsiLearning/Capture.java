package com.crauterb.wifijedi.rrsiLearning;

import java.util.ArrayList;

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


    

}
