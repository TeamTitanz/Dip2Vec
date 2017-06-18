package org.titans.fyp;

import java.io.Serializable;

/**
 * Created by Nisansa on 6/3/2017.
 * Initially based on https://kunuk.wordpress.com/2010/10/11/neural-network-backpropagation-with-java/
 */
public class Connection implements Serializable {
    double weight = 0;
    double prevDeltaWeight = 0; // for momentum
    double deltaWeight = 0;

    final Neuron leftNeuron;
    final Neuron rightNeuron;
    static int counter = 0;
    final public int id; // auto increment, starts at 0

    public Connection(Neuron fromN, Neuron toN) {
        leftNeuron = fromN;
        rightNeuron = toN;
        id = counter;
        counter++;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double w) {
        weight = w;
    }

    public void setDeltaWeight(double w) {
        prevDeltaWeight = deltaWeight;
        deltaWeight = w;
    }

    public double getPrevDeltaWeight() {
        return prevDeltaWeight;
    }

    public Neuron getFromNeuron() {
        return leftNeuron;
    }

    public Neuron getToNeuron() {
        return rightNeuron;
    }
}