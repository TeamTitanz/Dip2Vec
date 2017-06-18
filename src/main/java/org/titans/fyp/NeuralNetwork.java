package org.titans.fyp;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Nisansa on 6/3/2017.
 * Initially based on https://kunuk.wordpress.com/2010/10/11/neural-network-backpropagation-with-java/
 */
public class NeuralNetwork implements Serializable {
    static {
        Locale.setDefault(Locale.ENGLISH);
    }

    final boolean isTrained = false;
    final DecimalFormat df;
    final Random rand = new Random();
    final ArrayList<Neuron> inputLayer = new ArrayList<Neuron>();
    final ArrayList<Neuron> hiddenLayer = new ArrayList<Neuron>();
    final ArrayList<Neuron> outputLayer = new ArrayList<Neuron>();
    final Neuron bias = new Neuron();
    final int[] layers;
    final int randomWeightMultiplier = 1;

    final double epsilon = 0.00000000001;

    final double learningRate = 0.9f;
    final double momentum = 0.7f;

    // Inputs
    //final double inputs[][] = { { 1, 1 }, { 1, 0 }, { 0, 1 }, { 0, 0 } };
    private HashMap<double[][], Double> inputs = new HashMap<double[][], Double>();

    // Corresponding outputs
    //  final double expectedOutputs[][] = { { 0 }, { 1 }, { 1 }, { 0 } };
    //double resultOutputs[][] = { { -1 }, { -1 }, { -1 }, { -1 } }; // dummy init
    double sourceOutput[];
    double sinkOutput[];

    // for weight update all
    final HashMap<String, Double> weightUpdate = new HashMap<String, Double>();


    private double repelantFactor = 1.0;


    //  public static void main(String[] args) {
    //    NeuralNetwork nn = new NeuralNetwork(2, 4, 1);
    //  int maxRuns = 50000;
    //double minErrorCondition = 0.001;
    //   nn.run(maxRuns, minErrorCondition);
    //}


    public NeuralNetwork(int input, int hidden, int output) {
        this.layers = new int[]{input, hidden, output};
        df = new DecimalFormat("#.0#");

        /**
         * Create all neurons and connections Connections are created in the
         * neuron class
         */
        for (int i = 0; i < layers.length; i++) {
            if (i == 0) { // input layer
                for (int j = 0; j < layers[i]; j++) {
                    Neuron neuron = new Neuron();
                    inputLayer.add(neuron);
                }
            } else if (i == 1) { // hidden layer
                for (int j = 0; j < layers[i]; j++) {
                    Neuron neuron = new Neuron();
                    neuron.addInConnectionsS(inputLayer);
                    neuron.addBiasConnection(bias);
                    hiddenLayer.add(neuron);
                }
            } else if (i == 2) { // output layer
                for (int j = 0; j < layers[i]; j++) {
                    Neuron neuron = new Neuron();
                    neuron.addInConnectionsS(hiddenLayer);
                    neuron.addBiasConnection(bias);
                    outputLayer.add(neuron);
                }
            } else {
                System.out.println("!Error NeuralNetwork init");
            }
        }

        // initialize random weights
        for (Neuron neuron : hiddenLayer) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) {
                double newWeight = getRandom();
                conn.setWeight(newWeight);
            }
        }
        for (Neuron neuron : outputLayer) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) {
                double newWeight = getRandom();
                conn.setWeight(newWeight);
            }
        }

        // reset id counters
        Neuron.counter = 0;
        Connection.counter = 0;

        if (isTrained) {
            trainedWeights();
            updateAllWeights();
        }
    }

    // random
    double getRandom() {
        return randomWeightMultiplier * (rand.nextDouble() * 2 - 1); // [-1;1[
    }

    /**
     * @param inputs There is equally many neurons in the input layer as there are
     *               in input variables
     */
    public void setInput(double inputs[]) {
        //System.out.println("Blaaaaa "+inputs.length+" "+inputLayer.size());
        for (int i = 0; i < inputLayer.size(); i++) {
            inputLayer.get(i).setOutput(inputs[i]);
        }
    }

    public double[] getOutput() {
        double[] outputs = new double[outputLayer.size()];
        for (int i = 0; i < outputLayer.size(); i++)
            outputs[i] = outputLayer.get(i).getOutput();
        return outputs;
    }

    /**
     * Calculate the output of the neural network based on the input The forward
     * operation
     */
    public void activate() {
        for (Neuron n : hiddenLayer)
            n.calculateOutput();
        for (Neuron n : outputLayer)
            n.calculateOutput();
    }

    /**
     * all output propagate back
     *
     * @param expectedOutput first calculate the partial derivative of the error with
     *                       respect to each of the weight leading into the output neurons
     *                       bias is also updated here
     */
    public void applyBackpropagation(double expectedOutput[]) {

        // error check, normalize value ]0;1[
        for (int i = 0; i < expectedOutput.length; i++) {
            double d = expectedOutput[i];
            if (d < 0 || d > 1) {
                if (d < 0)
                    expectedOutput[i] = 0 + epsilon;
                else
                    expectedOutput[i] = 1 - epsilon;
            }
        }

        int i = 0;
        for (Neuron n : outputLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                double ak = n.getOutput();
                double ai = con.leftNeuron.getOutput();
                double desiredOutput = expectedOutput[i];

                double partialDerivative = -ak * (1 - ak) * ai
                        * (desiredOutput - ak);
                double deltaWeight = -learningRate * partialDerivative;
                double newWeight = con.getWeight() + deltaWeight;
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
            }
            i++;
        }

        // update weights for the hidden layer
        for (Neuron n : hiddenLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                double aj = n.getOutput();
                double ai = con.leftNeuron.getOutput();
                double sumKoutputs = 0;
                int j = 0;
                for (Neuron out_neu : outputLayer) {
                    double wjk = out_neu.getConnection(n.id).getWeight();
                    double desiredOutput = (double) expectedOutput[j];
                    double ak = out_neu.getOutput();
                    j++;
                    sumKoutputs = sumKoutputs
                            + (-(desiredOutput - ak) * ak * (1 - ak) * wjk);
                }

                double partialDerivative = aj * (1 - aj) * ai * sumKoutputs;
                double deltaWeight = -learningRate * partialDerivative;
                double newWeight = con.getWeight() + deltaWeight;
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
            }
        }
    }


    void run(int maxSteps, double minError) {
        int i;
        // Train neural network until minError reached or maxSteps exceeded
        double error = 1;
        for (i = 0; i < maxSteps && error > minError; i++) {
            error = 0;

            Iterator<double[][]> keySet = inputs.keySet().iterator();

            while (keySet.hasNext()) {
                double[][] key = keySet.next();
                double expected = inputs.get(key);
                double goodLoopCount = -1;

//                do {
                //Activate NN for source
                setInput(key[0]);

                activate();

                sourceOutput = getOutput();

                //Activate NN for sink
                setInput(key[1]);

                activate();

                sinkOutput = getOutput();

                double[] expectedVector = new double[0];

                if (expected == 0) { //Vectors need to be closer
                    expectedVector = sourceOutput;

                    if (goodLoopCount < 0) { //now we need to loop
                        goodLoopCount = (1.0 / repelantFactor);
                    } else {
                        //System.out.println("Err loop! "+goodLoopCount);
                        goodLoopCount -= 1;
                    }


                } else { //Vectors need to be far
                    double[] diff = nMath.vectorDiff(sourceOutput, sinkOutput);
                    diff = nMath.negate(diff);
                    diff = nMath.scalarMul(diff, repelantFactor);
                    expectedVector = nMath.vectorSum(sinkOutput, diff);


                }

                for (int j = 0; j < expectedVector.length; j++) {
                    double err = Math.pow(sinkOutput[j] - expectedVector[j], 2);
                    error += err;
                }

                applyBackpropagation(expectedVector);


                // setInput(inputs[p]);

                // activate();

                // output = getOutput();
                // resultOutputs[p] = output;

                //  for (int j = 0; j < expectedOutputs[p].length; j++) {
                //      double err = Math.pow(output[j] - expectedOutputs[p][j], 2);
                //   error += err;
                //  }

                //  applyBackpropagation(expectedOutputs[p])
//                }while(goodLoopCount>0);
            }
            System.out.println("At epoch " + (i + 1) + " error is " + error);


        }


        //   printResult();

        System.out.println("Sum of squared errors = " + error);
        System.out.println("##### EPOCH " + i + "\n");
        if (i == maxSteps) {
            System.out.println("!Error training try again");
        } else {
            //    printAllWeights();
            //    printWeightUpdate();
        }
    }


    public double[] model(double[] input) {
        setInput(input);
        activate();
        return getOutput();
    }

    public double modelDist(double[] source, double[] dest) {
        setInput(source);
        activate();
        sourceOutput = getOutput();
        setInput(dest);
        activate();
        sinkOutput = getOutput();
        return nMath.dist(sourceOutput, sinkOutput);
    }


    String weightKey(int neuronId, int conId) {
        return "N" + neuronId + "_C" + conId;
    }

    /**
     * Take from hash table and put into all weights
     */
    public void updateAllWeights() {
        // update weights for the output layer
        for (Neuron n : outputLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                String key = weightKey(n.id, con.id);
                double newWeight = weightUpdate.get(key);
                con.setWeight(newWeight);
            }
        }
        // update weights for the hidden layer
        for (Neuron n : hiddenLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                String key = weightKey(n.id, con.id);
                double newWeight = weightUpdate.get(key);
                con.setWeight(newWeight);
            }
        }
    }

    // trained data
    void trainedWeights() {
        weightUpdate.clear();

        weightUpdate.put(weightKey(3, 0), 1.03);
        weightUpdate.put(weightKey(3, 1), 1.13);
        weightUpdate.put(weightKey(3, 2), -.97);
        weightUpdate.put(weightKey(4, 3), 7.24);
        weightUpdate.put(weightKey(4, 4), -3.71);
        weightUpdate.put(weightKey(4, 5), -.51);
        weightUpdate.put(weightKey(5, 6), -3.28);
        weightUpdate.put(weightKey(5, 7), 7.29);
        weightUpdate.put(weightKey(5, 8), -.05);
        weightUpdate.put(weightKey(6, 9), 5.86);
        weightUpdate.put(weightKey(6, 10), 6.03);
        weightUpdate.put(weightKey(6, 11), .71);
        weightUpdate.put(weightKey(7, 12), 2.19);
        weightUpdate.put(weightKey(7, 13), -8.82);
        weightUpdate.put(weightKey(7, 14), -8.84);
        weightUpdate.put(weightKey(7, 15), 11.81);
        weightUpdate.put(weightKey(7, 16), .44);
    }

    public void printWeightUpdate() {
        System.out.println("printWeightUpdate, put this i trainedWeights() and set isTrained to true");
        // weights for the hidden layer
        for (Neuron n : hiddenLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                String w = df.format(con.getWeight());
                System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
                        + con.id + "), " + w + ");");
            }
        }
        // weights for the output layer
        for (Neuron n : outputLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                String w = df.format(con.getWeight());
                System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
                        + con.id + "), " + w + ");");
            }
        }
        System.out.println();
    }

    public void printAllWeights() {
        System.out.println("printAllWeights");
        // weights for the hidden layer
        for (Neuron n : hiddenLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                double w = con.getWeight();
                System.out.println("n=" + n.id + " c=" + con.id + " w=" + w);
            }
        }
        // weights for the output layer
        for (Neuron n : outputLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                double w = con.getWeight();
                System.out.println("n=" + n.id + " c=" + con.id + " w=" + w);
            }
        }
        System.out.println();
    }

    public void setRepelantFactor(double repelantFactor) {
        this.repelantFactor = repelantFactor;
    }

    public void setInputs(HashMap<double[][], Double> inputs) {
        this.inputs = inputs;


    }
}
