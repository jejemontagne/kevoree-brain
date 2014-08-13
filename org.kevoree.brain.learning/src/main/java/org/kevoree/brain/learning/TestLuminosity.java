package org.kevoree.brain.learning;


import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;
import org.kevoree.brain.statistic.StatLibrary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by assaa_000 on 8/12/2014.
 */
public class TestLuminosity {
    public static void main (String[] args){
        GaussianKernelLearning gkl = new GaussianKernelLearning();
        String csvFile = "D:\\workspace\\Github\\kevoree-brain\\org.kevoree.brain.learning\\src\\main\\resources\\luminosity.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<Double[]> x=new ArrayList<Double[]>();
        ArrayList<Integer> y= new ArrayList<Integer>();

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] values = line.split(cvsSplitBy);

                Double[] px= new Double[1];
                px[0]= Double.parseDouble(values[0]);
                Integer py= Integer.parseInt(values[1]);
                x.add(px);
                y.add(py);
            }

        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }

        for(int i=0;i<x.size()*3/4;i++){
            gkl.addTrainingSet(x.get(i),y.get(i));
        }

        ArrayList<Object[]> xTest= new ArrayList<Object[]>();
        ArrayList<Integer> yTest= new ArrayList<Integer>();

        for(int i=x.size()*3/4;i<x.size();i++){
            xTest.add(x.get(i));
            yTest.add(y.get(i));
        }



        long startTime = System.nanoTime();
        for(int i=0; i<1;i++)
            try {
                gkl.train();
            } catch (Exception e) {
                e.printStackTrace();
            }
        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        gkl.printState();
        StatLibrary.testClassifier(xTest,yTest,gkl);
        //gkl.testAccuracy();
        System.out.println("Duration: "+(double)duration / 1000000000.0+" seconds");









    }
}
