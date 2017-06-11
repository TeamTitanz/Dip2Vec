import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class Main {

    //Test only variables
    HashMap<String,String[]> objMap=new HashMap<String,String[]>();
    HashMap<String,String[]> binMap=new HashMap<String,String[]>();
    HashMap<String,Double> tempTrainiingPairs=new  HashMap<String,Double>();


    //Common variables
    HashMap<double[][],Double> trainiingPairs=new  HashMap<double[][],Double>();
    HashMap<String,double[]> nameTempVecMap=new HashMap<String,double[]>();

    public static void main(String[] args) {
        Main m=new Main();
      m.run();

    }

    private void run(){

        //Start test specific code
        testSetUp();
        //Test specific code finished here



       Model m=new Model(nameTempVecMap,trainiingPairs,50000, 0.001,0.1,14,21);

        //Testing
     //   printModel(nn);
        m.printModel();
        m.printModelKNN(6);
        System.out.println(m.modelDist("A","B"));
        System.out.println(m.modelDist("A","T"));
    }




    private void testSetUp(){
        readFile("obGraph.txt");
        //System.out.println();
        //printMap(objMap);
        stringMapTobinMap();
        //printMap(binMap);
        buildTempTrainiingPairs();
        //printTempTrainiingPairs();
        //String s="10010012000001";
        //String[] p=breakNum(s);
        //System.out.println(s+" -> "+p[0]+" "+p[1]);
        //int[] num=breakInput(p[0]);
        // for (int i = 0; i <num.length ; i++) {
        //     System.out.println(num[i]);
        //}
        buildTrainingPairs();
    }




    private void buildTrainingPairs(){
        Iterator<String> keySet=tempTrainiingPairs.keySet().iterator();
        while(keySet.hasNext()) {
            String key = keySet.next();
            String[] keyS=breakNum(key);
            double[][] newKey=new double[][]{breakInput(keyS[0]),breakInput(keyS[1])};
            trainiingPairs.put(newKey,tempTrainiingPairs.get(key));
        }
    }

    private double[] breakInput(String s){
       String[] sParts=s.split("");
        double[] ip=new double[sParts.length];
        for (int i = 0; i <sParts.length ; i++) {
            ip[i]=Integer.parseInt(sParts[i]);
        }
        return ip;
    }

    private String[] breakNum(String s){
        String[] parts=new String[2];
        parts[0]=s.substring(0,s.length()/2);
        parts[1]=s.substring(s.length()/2,s.length());
        return parts;
    }


    private void buildTempTrainiingPairs(){
        ArrayList<String> candidates=new ArrayList<String>();
        Iterator<String> keySet=binMap.keySet().iterator();

        //First the true examples
        while(keySet.hasNext()) {
            String key = keySet.next();
            candidates.add(key);
            String[] values = binMap.get(key);
            for (int i = 0; i <values.length ; i++) {
                tempTrainiingPairs.put(key+values[i],0.0); //Should be close
                tempTrainiingPairs.put(values[i]+key,0.0); //Should be close
            }
        }

        //Now the false examples for X,Y only if the true exampes do not have X->Y OR Y->X. If at least one of them is there, no false example is added.
        for (int i = 0; i <candidates.size() ; i++) {
            for (int j = 0; j <candidates.size() ; j++) {
                String key1=candidates.get(i)+candidates.get(j);
                String key2=candidates.get(j)+candidates.get(i);
                Double val1=tempTrainiingPairs.get(key1);
                Double val2=tempTrainiingPairs.get(key2);
                if(val1==null && val2==null){
                    tempTrainiingPairs.put(key1,1.0); //should be far
                    tempTrainiingPairs.put(key2,1.0); //should be far
                }
            }
        }

    }


    private void readFile(String file){
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();

            while (line != null) {
               // System.out.println(line);
                String[] parts=line.split(";");
                String[] entries=new String[0];


                if(parts.length>1){
                    entries=parts[1].split(",");
                }
                objMap.put(parts[0],entries);
                line = br.readLine();
            }

            br.close();
        }catch(Exception e){
            System.err.println(e.getMessage());
        }

    }

    private void stringMapTobinMap(){
        Iterator<String> keySet=objMap.keySet().iterator();
        while(keySet.hasNext()) {
            String keyS = keySet.next();
            String[] values=objMap.get(keyS);
            String key=getBinaryString(keyS);
            String[] newVal=new String[values.length];
            for (int i = 0; i <values.length ; i++) {
                newVal[i]=getBinaryString(values[i]);
            }
            binMap.put(key,newVal);


            //Add to nameTempVecMap
            nameTempVecMap.put(keyS,breakInput(key));

        }
    }




    private double[] stringToVector(String s){
        return breakInput(getBinaryString(s));
    }

    private String getBinaryString(String s){
        return Integer.toBinaryString(s.hashCode());
    }


    private void printTempTrainiingPairs(){
        Iterator<String> keySet=tempTrainiingPairs.keySet().iterator();

        while(keySet.hasNext()) {
            String key = keySet.next();
            System.out.println(key+" "+tempTrainiingPairs.get(key));
        }
    }


    private void printMap(HashMap<String,String[]> m){
        StringBuilder sb=new StringBuilder();
        Iterator<String> keySet=m.keySet().iterator();
        while(keySet.hasNext()){
            String key=keySet.next();
            sb.append(key);
            sb.append(";");
            String[] values=m.get(key);
            for (int i = 0; i <values.length ; i++) {
                sb.append(values[i]);
                sb.append(" ");
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }

}
