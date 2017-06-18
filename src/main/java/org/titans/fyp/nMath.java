package org.titans.fyp;

/**
 * Created by Nisansa on 6/3/2017.
 */
public class nMath {

    private static nMath nm = new nMath();

    private nMath() {
        // double[] vecA=new double[]{1,1};
        // double[] vecB=new double[]{2,3};
        //  double[] diff=nMath.vectorDiff(vecA,vecB);
        //  diff=nMath.negate(diff);
        //  double[] vecC=nMath.vectorSum(vecB,diff);
        //  nMath.printVec(vecC);
    }

    public static nMath getInstance() {
        return nm;
    }

    public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public static double[] vectorDiff(double[] vectorA, double[] vectorB) {
        double[] vectorDiff = new double[vectorA.length];
        for (int i = 0; i < vectorDiff.length; i++) {
            vectorDiff[i] = vectorA[i] - vectorB[i];
        }
        return vectorDiff;
    }

    public static double[] vectorSum(double[] vectorA, double[] vectorB) {
        double[] vectorDiff = new double[vectorA.length];
        for (int i = 0; i < vectorDiff.length; i++) {
            vectorDiff[i] = vectorA[i] + vectorB[i];
        }
        return vectorDiff;
    }

    public static double[] negate(double[] vec) {
        return scalarMul(vec, -1);
    }

    public static double[] scalarMul(double[] vec, double a) {
        for (int i = 0; i < vec.length; i++) {
            vec[i] *= a;
        }
        return vec;
    }


    public static double dist(double[] vectorA, double[] vectorB) {
        double[] diff = vectorDiff(vectorA, vectorB);
        double dist = 0;
        for (int i = 0; i < diff.length; i++) {
            dist += Math.pow(diff[i], 2);
        }
        return Math.sqrt(dist);
    }

    public static String vecToString(double[] vec) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(" ");
        for (int i = 0; i < vec.length; i++) {
            sb.append(vec[i]);
            sb.append(" ");
        }
        sb.append("]");
        return sb.toString();
    }
}
