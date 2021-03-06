package eurUsd;

import org.kevoree.brain.util.PolynomialCompressor;
import org.kevoree.brain.util.Prioritization;
import org.kevoree.brain.util.TimeStamp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

/**
 * Created by assaa_000 on 07/10/2014.
 */
public class Preparation {

    public static void main(String[] args) {

       /* Date d=new Date();
        d.setTime(Long.parseLong("991949460000"));*/

        long starttime;
        long endtime;
        double res;

        long timeOrigine = TimeStamp.getTimeStamp(2000, 5, 30, 17, 27);
        int degradeFactor = 60000;
        double toleratedError = 0.0001;
        int maxDegree = 20;

        TreeMap<Long, Double> eurUsd = new TreeMap<Long, Double>();
        PolynomialCompressor pt = new PolynomialCompressor(timeOrigine, degradeFactor, toleratedError, maxDegree);
        pt.setContinous(true);
        pt.setPrioritization(Prioritization.LOWDEGREES);

        ArrayList<Long> timestamps = new ArrayList<Long>();
        ArrayList<Double> valss = new ArrayList<Double>();

        starttime = System.nanoTime();
       // String csvFile = "D:\\workspace\\Github\\kevoree-brain\\org.kevoree.brain.learning\\src\\main\\resources\\EURUSD.csv";
        String csvFile =  "/Users/assaad/work/github/eurusd/Eur USD database/EURUSD_";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        String dateSplit = "\\.";
        String hourSplit =":";


        try {
            FileWriter outFile = new FileWriter("newEurUsd.csv");
            PrintWriter out = new PrintWriter(outFile);
            for(int year=2000;year<2016;year++) {

                br = new BufferedReader(new FileReader(csvFile + year + ".csv"));

                while ((line = br.readLine()) != null) {

                    // use comma as separator 2000.05.30,17:35
                    String[] values = line.split(cvsSplitBy);
                    String[] dates = values[0].split(dateSplit);
                    String[] hours = values[1].split(hourSplit);
                    Long timestamp = TimeStamp.getTimeStamp(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]), Integer.parseInt(hours[0]), Integer.parseInt(hours[1]));
                    Double val = Double.parseDouble(values[2]);
                    eurUsd.put(timestamp, val);
                    pt.feed(timestamp, val);
                    timestamps.add(timestamp);
                    valss.add(val);
                    out.println(values[0] + "," + values[1] + "," + timestamp + "," + val);
                }
            }
            out.close();
            outFile.close();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }


        pt.finalsave();
        endtime = System.nanoTime();
        res = ((double) (endtime - starttime)) / (1000000000);
        System.out.println("Loaded :" + eurUsd.size() + " values in " + res + " s!");

        try{
            FileWriter outFile = new FileWriter("compare.csv");
            PrintWriter out = new PrintWriter(outFile);
            for(int i=0;i<timestamps.size();i++){
                out.println(i+","+timestamps.get(i) + "," + valss.get(i) + "," + pt.fastReconstruct(timestamps.get(i)) + "," + (pt.fastReconstruct(timestamps.get(i))-valss.get(i)));
            }
            out.close();
            outFile.close();

    } catch (Exception ex) {
        System.out.println(ex.getMessage());
    }

        double err=0;
        double maxerr=0;
        double currerr=0;
        int counter=0;
        long indec=0;
        double v;
        int l=0;
        starttime = System.nanoTime();

        double min=2;
        double max=0;
        for(int i=0; i<valss.size();i++){
            long t=timestamps.get(i);
            double val=valss.get(i);
            if(val>max){
                max=val;
            }
            if(val<min){
                if(min<0.7){
                    System.out.println(t);
                }
                min=val;
            }
            v=pt.fastReconstruct(t);
            currerr=Math.abs(v-val);
            if(currerr>maxerr){
                maxerr=currerr;
                indec=t;
                l=i;
            }
            err+=currerr;
            counter++;
        }
        System.out.println("Min: "+min+" , Max: "+max);
        err=err/counter;
        endtime = System.nanoTime();
        res=((double)(endtime-starttime))/(1000000000);
        System.out.println("Number of polynoms: "+pt.polynomTree.size());
        System.out.println("Number of Val: " + pt.globalCounter);
        System.out.println("Average degrees: "+ ((double)pt.degrees)/pt.polynomTree.size());
        System.out.println("Reconstructed in: "+res+" s!");
        System.out.println("Max error: "+maxerr +" at: "+ indec+" line: "+l);
        System.out.println("Avg err: "+err);

        ArrayList<Double> times=new ArrayList<Double>();
        ArrayList<Double> values = new ArrayList<Double>();

        Long initTimeStamp = TimeStamp.getTimeStamp(2001, 01, 01, 00, 00);
        Long finalTimeStamp= TimeStamp.getTimeStamp(2014, 9, 26, 00, 00);
       // int sampleRate=24*60; //5min

        starttime = System.nanoTime();
        for(long i=initTimeStamp; i<finalTimeStamp;i+=degradeFactor){
           // double t= ((double) (i-initTimeStamp))/degradeFactor;
            double val = eurUsd.get(eurUsd.floorKey(i));
           /* times.add(t);
            values.add(val);*/
        }
       endtime = System.nanoTime();
        res=((double)(endtime-starttime))/(1000000);
        System.out.println("Number of val: "+ ((finalTimeStamp-initTimeStamp)/degradeFactor));
        System.out.println("normal chain in: "+res+" ms!");


        starttime = System.nanoTime();
        for(long i=initTimeStamp; i<finalTimeStamp;i+=degradeFactor){
           // double t= ((double) (i-initTimeStamp))/degradeFactor;
            double val = pt.fastReconstruct(i);
           /* times.add(t);
            values.add(val);*/
        }
       endtime = System.nanoTime();
        res=((double)(endtime-starttime))/(1000000);
        System.out.println("Polynomial chain in: "+res+" ms!");

  /*   if(times.size()%2!=0){
            times.remove(times.get(times.size()-1));
        }

        double[] tt= new double[times.size()];
        double[] vv=new double[times.size()];

        for(int i=0; i<times.size();i++){
            tt[i]=times.get(i);
            vv[i]=values.get(i);
        }

        starttime = System.nanoTime();
       // Autocorrelation.fftAutoCorrelation(tt, vv);
      //  Autocorrelation.normalize(vv);
        endtime = System.nanoTime();
        res=((double)(endtime-starttime))/(1000000);*/



       // System.out.println("FFT Autocorrelation On signal " +res+" ms!");
      //  System.out.println("Period "+Autocorrelation.detectPeriod(vv)*sampleRate);




    }
}



