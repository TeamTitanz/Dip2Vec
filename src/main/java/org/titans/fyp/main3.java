package org.titans.fyp;

import java.io.*;
import java.util.HashMap;

/**
 * Created by Keetmalin on 6/18/2017
 * Project - Dip2Vec
 */
public class main3 {

    public static void main(String[] args) {

        HashMap<String, double[]> trainedModel = main3.loadSerialized();
        Model m = new Model(trainedModel);

    }


    public static HashMap<String, double[]> loadSerialized() {
        File file = new File("Serialize/trainedModel.ser");
        HashMap<String, double[]> temp = null;

        if (file.exists()) {
            FileInputStream fileIn = null;
            try {
                fileIn = new FileInputStream(file);

                ObjectInputStream in = new ObjectInputStream(fileIn);
                temp = (HashMap<String, double[]>) in.readObject();
                System.out.println("reading neuralNetwork object from file");
                in.close();
                fileIn.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        } else {
            System.out.println("Serialized Neural Network not found");

        }
        return temp;
    }

}