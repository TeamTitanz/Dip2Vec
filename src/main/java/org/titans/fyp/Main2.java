package org.titans.fyp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Keetmalin on 6/17/2017
 * Project - Dip2Vec
 */
public class Main2 {

    HashMap<double[][], Double> trainingPairs = new HashMap<double[][], Double>();
    HashMap<String, double[]> nameTempVecMap = new HashMap<String, double[]>();
    HashMap<String, String[]> objMap = new HashMap<String, String[]>();

    public static void main(String[] args) {


        Main2 m = new Main2();

        int maxRuns = Integer.parseInt(args[0]);
        int hiddenLayerSize = Integer.parseInt(args[1]);
        int outputVecSize = Integer.parseInt(args[2]);

        m.createTrainingPairs();
        //m.createNegativeTrainingExamples();


        m.callNeuralNetwork(maxRuns , hiddenLayerSize, outputVecSize);


    }

    private void createNegativeTrainingExamples() {

        Iterator<String> keySet = objMap.keySet().iterator();
        while (keySet.hasNext()) {


        }

    }

    private void createTrainingPairs() {
        readFile("./LawIE/Dip2Vec/mapAll.txt");
//        readFile("mapAll.txt");
        int total = objMap.sizegi();
        int count = 1;
        Iterator<String> keySet = objMap.keySet().iterator();
        while (keySet.hasNext()) {

            String keyS = keySet.next();
            String[] values = objMap.get(keyS);
            nameTempVecMap.put(keyS, breakInput(keyS));

            for (int i = 0; i < values.length; i++) {

                double[][] newKey = new double[][]{breakInput(keyS), breakInput(values[i])};
                double[][] newKey2 = new double[][]{breakInput(values[i]), breakInput(keyS)};
//                double[] temp1 = breakInput(keyS);
//                double[] temp2 = breakInput(values[i]);
//                Collections.reverse(Arrays.asList(temp1));
//                Collections.reverse(Arrays.asList(temp2));
//                double[][] newKey2 = new double[][]{temp1, temp2};

                trainingPairs.put(newKey, 0.0);
                trainingPairs.put(newKey2, 0.0);
            }

            System.out.println("Positive Training Pairs for : " + count + " / " + total + " Done");
            count++;


        }

        System.out.println("Training Data Successfully Prepared");

    }

    private void callNeuralNetwork(int maxRuns, int hiddenLayerSize, int outputVecSize) {

        Model m = new Model(nameTempVecMap, trainingPairs, maxRuns, 0.001, 0.1, hiddenLayerSize, outputVecSize);
        System.out.println("Training Process Done");
        m.saveModel();
        //m.printModel();

        System.out.println("Serialized Model Saved");
//        m.printModelKNN(6);
    }

    private void readFile(String file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();

            while (line != null) {
                // System.out.println(line);
                String[] parts = line.split(";");
                String[] entries = new String[0];


                if (parts.length > 1) {
                    entries = parts[1].split(",");
                }
                objMap.put(parts[0], entries);
                line = br.readLine();
            }

            br.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    private double[] breakInput(String s) {
        String[] sParts = s.split("");
        double[] ip = new double[sParts.length];
        for (int i = 0; i < sParts.length; i++) {
            ip[i] = Integer.parseInt(sParts[i]);
        }
        return ip;
    }
}
