package com.crauterb.wifijedi.rrsiLearning;

/**
 * Created by christoph on 09.02.15.
 */
public class Node {

    protected double timestamp;

    protected int RSSI;

    protected String MAC;

    public Node() {
        this.timestamp = -1;
        this.RSSI = -100;
    }

    public Node(double timestamp, int RSSI, String MAC) {
        this.timestamp = timestamp;
        this.RSSI = RSSI;
        this.MAC = MAC;
    }

    public String toString() {
        return "" + this.timestamp +": " + this.RSSI;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getRSSI() {
        return RSSI;
    }

    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }
}
