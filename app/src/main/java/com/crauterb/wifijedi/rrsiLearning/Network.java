package com.crauterb.wifijedi.rrsiLearning;

import java.util.ArrayList;

/**
 * Created by christoph on 09.02.15.
 */
public class Network {

    private String MACAdress;

    private String SSID;

    public ArrayList<Node> capturedData = new ArrayList<Node>();

    public Capture accordingCapture;

    public void addNode(Node node) {
        this.capturedData.add(node);
    }

    public int getNumberOfDataPoints() {
        return this.capturedData.size();
    }

    public Network(String mac, String ssid) {
        this.setMACAdress(mac);
        this.setSSID(ssid);
    }


    public String getMACAdress() {
        return MACAdress;
    }

    public void setMACAdress(String MACAdress) {
        this.MACAdress = MACAdress;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String toString() {
        String name = "";
        int perc =  (int) (( (double) getNumberOfDataPoints() / (double) this.accordingCapture.getTotalNumberOfDataPoints()) * 100 );
        name += this.SSID + " (" + this.MACAdress + ")\n" + getNumberOfDataPoints() + " packets (" + perc + "%)";
        return name;
    }

    public ArrayList<Integer> getTimeSlot(double start, double end) {
        ArrayList<Integer> slot = new ArrayList<Integer>();
        for( Node n : capturedData ) {
            if ( n.timestamp >= start && n.timestamp < end)
                slot.add(n.getRSSI());
            else if ( n.timestamp >= end )
                return slot;
        }
        return null;
    }
}
