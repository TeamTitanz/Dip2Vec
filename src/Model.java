import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Nisansa on 6/3/2017.
 */
public class Model {
    NeuralNetwork nn=null;

    HashMap<String,double[]> trainedModel=new HashMap<String,double[]>();


    public Model(HashMap<String, double[]> nameTempVecMap, HashMap<double[][], Double> trainiingPairs,int maxRuns,  double minErrorCondition,double repelantFactor,int hiddenLayerSize,int outputVecSize ) {

         int inputNNSize=nameTempVecMap.values().iterator().next().length;

        NeuralNetwork nn = new NeuralNetwork(inputNNSize,hiddenLayerSize, outputVecSize);
        nn.setInputs(trainiingPairs);


        nn.setRepelantFactor(repelantFactor);
        nn.run(maxRuns, minErrorCondition);


        Iterator<String> itr=nameTempVecMap.keySet().iterator();
        while(itr.hasNext()){
            String name=itr.next();
            double[] arr=nameTempVecMap.get(name);
            double[] res=nn.model(arr);
            trainedModel.put(name,res);
        }


    }


    public String[] findKNN(String name,int k) throws Exception {
        return findKNN(name,k,0.5);
    }


    public String[] findKNN(String name,int k,double threshold) throws Exception {

        if(trainedModel.size()<k){
            throw new Exception("k is lager than the length of trainingSet!");
        }

        String[] resultsS=new String[k];
        double[] resultsD=new double[k];

        int i=0;
        Iterator<String>itr=trainedModel.keySet().iterator();
        while(itr.hasNext()){
            String key=itr.next();
            if(!key.equalsIgnoreCase(name)) {
                if (i < k) { //First we greedily fill
                    resultsS[i] = key;
                    resultsD[i] = modelDist(name, key);
                } else { //then we replace

                    double max = Double.MIN_VALUE;
                    int maxIndex = -1;

                    for (int j = 0; j < resultsD.length; j++) {
                        if (max < resultsD[j]) {
                            max = resultsD[j];
                            maxIndex = j;
                        }
                    }

                    double dist = modelDist(name, key);
                    if (dist < max && maxIndex >= 0) { //need to replace
                        resultsS[maxIndex] = key;
                        resultsD[maxIndex] = dist;
                    }
                }

                //System.out.println(key+" -> "+nMath.vecToString(trainedModel.get(key)));

                i++;
            }

        }

        String[] resultsSN=new String[k];
        for (int j = 0; j <resultsSN.length ; j++) {
            double min = Double.MAX_VALUE;
            int minIndex = -1;

            for (int l = 0; l <resultsS.length ; l++) {
                if(min>resultsD[l]){
                    min=resultsD[l];
                    minIndex=l;
                }
            }

            resultsSN[j]=resultsS[minIndex];
            resultsD[minIndex]=Double.MAX_VALUE;

        }



        return resultsSN;

    }


    public  double[] model(double[] input){
        return nn.model(input);
    }

    public double modelDist(double[] source,double[] dest){
        return nn.modelDist(source, dest);
    }


    public  double[] model(String input){
        return trainedModel.get(input);
    }

    public double modelDist(String source,String dest){
        return nMath.dist(trainedModel.get(source),trainedModel.get(dest));
    }



    public void printModel(){
        Iterator<String>itr=trainedModel.keySet().iterator();
        while(itr.hasNext()){
            String key=itr.next();
            System.out.println(key+" -> "+nMath.vecToString(trainedModel.get(key)));
        }
    }

    public void printModelKNN(int k){
        Iterator<String>itr=trainedModel.keySet().iterator();
        StringBuilder sb=new StringBuilder();
        while(itr.hasNext()){
            String key=itr.next();
            String[] neigh=new String[0];
            try {
                neigh=findKNN(key,k);
            } catch (Exception e) {
                e.printStackTrace();
            }


            sb.append(key);
            sb.append(" -> ");
            for (int i = 0; i <neigh.length ; i++) {
                sb.append(neigh[i]);
                sb.append(" ");
            }
            sb.append("\n");


        }

        System.out.println(sb.toString());
    }
}
