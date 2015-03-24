package com.crauterb.wifijedi.rrsiLearning;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

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

    private Classifier knn;

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
        this.knn  = new KNearestNeighbors(7);
    }

    public RSSILearner(double timeslice, int k) {
        this.learningData = new DefaultDataset();
        //this.svm = new LibSVM();
        this.timeSlice = timeslice;
        this.noun = 1;
        this.knn  = new KNearestNeighbors(k);
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
        int numberOfSlices = data.size();
        System.out.println("I got " + numberOfSlices + " slices over here...");
        int[] predClass = new int[numberOfSlices];
        int count = 0;
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
                predClass[count] = (Integer) predictedClassValue;
                count++;
                numbers[(Integer) this.knn.classify(i)]++;
            }
        } catch ( Exception e ) {
            System.out.println("ERROR WHILST CLASSIFYING");
            e.printStackTrace();
            return -1;
        }
        int m = max(numbers);
        int max_class = -1;
        for( int i = 0; i < numbers.length; i++) {
            if ( numbers[i] == m ) {
                max_class = i;
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

    private int findStreak(int[] field, int streaklength) {

        int max = field.length - streaklength;
        boolean found = false;
        for( int i = 0 ; i < max; i++ ) {

        }
        return -1;
    }
}
