package com.crauterb.wifijedi.rrsiLearning;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.List;

//import weka.classifiers.Classifier;
//import weka.core.Instance;

/**
 * Created by christoph on 10.02.15.
 *
 */



public class RSSILearner {

    public static String FILENAME_CLASS_SWIPE_LEFT = "train_swipe_left";
    public static String FILENAME_CLASS_SWIPE_RIGHT = "train_swipe_right";
    public static String FILENAME_CLASS_TOWARDS = "train_towards";
    public static String FILENAME_CLASS_AWAY = "train_away";

    public static String FILENAME_CLASS_ADD_ONE = "train_additional_class01";
    public static String FILENAME_CLASS_ADD_TWO = "train_additional_class02";

    /** Variable to count for undisturbed data*/
    public static final int UNDISTURBED = 4;
    /** Variable to count for Movement of a hand from left to right*/
    public static final int MOV_LEFTTORIGHT = 0;
    /** Varibale to count for Movement of a hand from right to left*/
    public static final int MOV_RIGHTTOLEFT = 1;
    /** Variable to count for Movement of a hand down towards the phone*/
    public static final int MOV_DOWNTOWARDS = 2;
    /** Variable to count for Movement of a hand upwards from the phone*/
    public static final int MOV_UPWARDSFROM = 3;
    /** Variable to account for the number of classes*/
    public static final int NUMBER_OF_CLASSES = 6;
    /** Varuable to indicate a label to be determined*/
    public static final int LABEL_NOT_DETERMINED = -1;

    /** Position of the Mean of the RSSI Values in a Learning Data Point*/
    public static final int POS_RSSIMEAN = 0;
    /** Position of the Standard Deviation in a Learning Data Point*/
    public static final int POS_RSSISTD = 1;
    /** Position of the Maximum RSSI Value in a Learning Data Point*/
    public static final int POS_RSSIMAX = 2;
    /** Position of the Minimum RSSI Value in a Learning Data Point*/
    public static final int POS_RSSIMIN = 3;
    /** Position of the Number of RSSI-Values*/
    public static final int POS_NUMBEROFRSSI = 4;
    /** Position to count for the representation of the MAC-Address*/
    public static final int POS_MAC = 5;
    /** Position to count for the Class Label*/
    public static final int POS_LABEL = 6;
    /** Number of features */
    public static final int NUMBER_OF_FEATURES = 5;

    private Classifier knn = new KNearestNeighbors(7);

    //private Classifier svm;

    //private Classifier[] netSVMS;
    private Dataset[] netLearningData;

    private Dataset learningData;

    private double timeSlice;

    private int noun;

    public RSSILearner(double timeslice) {
        this.learningData = new DefaultDataset();
        //this.svm = new LibSVM();
        this.timeSlice = timeslice;
        this.noun = 1;
    }

    public void addLearningData(List<double[]> data, int label ) {
        System.out.println("Trying to add learning data... ");
        for (double[] l : data) {
            Instance tmpInstance = new DenseInstance(l,label);
            this.learningData.add(tmpInstance);
        }
    }

    public void addLearningDataToNet(List<double[]> data, int label, int network){
        for (double[] l : data) {
            Instance tmpInstance = new DenseInstance(l,label);
            this.netLearningData[network].add(tmpInstance);
        }
    }

    public void trainClassifier() {
        knn.buildClassifier(this.learningData);
    }


    public int classify(List<double[]> data) {
        Dataset DataToClassify = new DefaultDataset();
        int[] numbers = new int[NUMBER_OF_CLASSES];

        for( double[] d : data) {
            System.out.print("feature[");
            for( int j = 0; j < d.length; j++) {
                System.out.print(d[j] + " ,");
            }
            System.out.println("]");
            DataToClassify.add(new DenseInstance(d));
        }
        try {
            for( Instance i : DataToClassify) {
                Object predictedClassValue = knn.classify(i);
                System.out.println("Prediction is: " + predictedClassValue);
                numbers[(Integer) this.knn.classify(i)]++;
            }
        } catch ( Exception e ) {
            System.out.println("ERROR WHILST CLASSIFYING");
            e.printStackTrace();
            return -1;
        }
        int m = max(numbers);
        for( int i = 0; i < numbers.length; i++) {
            if ( numbers[i] == m ) {
                return i;
            }
        }
        return -1;
    }

    /**
     * REALLY pissed, that Java does not support finding max value in field of native type...
     * @param fuckingFeld Field where max value is to be extracted from
     * @return The fucking maximum value
     */
    private int max(int[] fuckingFeld) {
        int max = 0;
        for( int i : fuckingFeld ) {
            max = ( i > max ? i : max);
        }
        return max;
    }

    public void setUsedNetworks( int noun) {
        this.noun = noun;
        //this.netSVMS = new Classifier[noun];
        this.netLearningData = new Dataset[noun];
        for( int i = 0; i < netLearningData.length; i++) {
            //netSVMS[i] = new LibSVM();
            netLearningData[i] = new DefaultDataset();
        }
    }

    public void constructLearningData( Capture cap, int label, String[] macs ) {
        List<double[]> newData;
        List<Integer> tmpRSSI;
        Network newNet;
        DescriptiveStatistics stats = new DescriptiveStatistics();
        int numberOfTimeSlots = (int) ((cap.endTime - cap.startTime) / timeSlice);
        double[] tmp;
        double t_start = cap.startTime;
        double t_end = cap.startTime + timeSlice;
        if ( noun != macs.length)
            System.out.println("FEHLER BEI IRGENDWO");
        int i = 0;
        for( String mac : macs) {
            newData = new ArrayList<double[]>();
            newNet = cap.getNetworkByMAC(mac);
            while ( t_start < cap.endTime ) {
                tmp = new double[5];
                tmpRSSI = newNet.getTimeSlot(t_start,t_end);
                for ( int r : tmpRSSI ) {
                    stats.addValue((double) r);
                }
                tmp[POS_RSSIMEAN] = stats.getMean();
                tmp[POS_RSSIMAX] = stats.getMax();
                tmp[POS_RSSIMIN] = stats.getMin();
                tmp[POS_RSSISTD] = stats.getStandardDeviation();
                tmp[POS_NUMBEROFRSSI] = tmpRSSI.size();
                newData.add(tmp);

                t_start += timeSlice;
                t_end += timeSlice;
            }
            // Now, one network is complete
            addLearningDataToNet(newData, label, i);

        }

        return;
    }
}
