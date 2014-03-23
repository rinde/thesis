package analyse;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;


public class Analyser {
//  public static void main(String[] args){
//    new Analyser("test.txt");
//  }
  public Analyser(String fileName) {
    // TODO Auto-generated constructor stub
    //    getAverage();
    averageFailures(fileName);
  }



  private void getAverage() {
    readInput();
    System.out.println(amount);
    System.out.println(getMean());
  }

  private double sum;
  private int amount;
  private int sampleSize=20;
  private double getMean() {
    // TODO Auto-generated method stub
    return sum/amount;
  }
  private void averageFailures(String name){
    BufferedReader br = null;
    try {

      String sCurrentLine;

      br = new BufferedReader(new FileReader("result.txt"));
      br.readLine();
      double[] costsPerFailure = new double[10];
      int[] amountOfEventsPerFailure = new int[10];
      ArrayList<ArrayList<Double>> data = new ArrayList<ArrayList<Double>>();
      for (int i = 0; i < amountOfEventsPerFailure.length; i++) {
        data.add(new ArrayList<Double>());
      }
      while ((sCurrentLine = br.readLine()) != null) {
        String[] splitStrings = sCurrentLine.split(",");
        if(splitStrings.length<4){
          continue;
        }
        double cost = Double.valueOf(splitStrings[4]);
        int amountOfFailures = Integer.valueOf(splitStrings[splitStrings.length-1]);
        if(amountOfFailures<data.size()&&amountOfEventsPerFailure[amountOfFailures]<this.sampleSize){
          data.get(amountOfFailures).add(cost);
          costsPerFailure[amountOfFailures]+=cost;
          amountOfEventsPerFailure[amountOfFailures]++;
        }
      }
      printAverageFailures(costsPerFailure,amountOfEventsPerFailure,data,name);


    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (br != null)br.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
  private void printAverageFailures(double[] costsPerFailure,
      int[] amountOfEventsPerFailure, ArrayList<ArrayList<Double>> data, String name) throws FileNotFoundException, UnsupportedEncodingException {
    PrintWriter writer = new PrintWriter(name,"UTF-8");
    writer.println("amountoffailures cost std min lower middle upper max sum");
    for (int i = 1; i < amountOfEventsPerFailure.length; i++) {
      Double[] allCost = data.get(i).toArray(new Double[1]);
      int amount = amountOfEventsPerFailure[i];     
      if(amount<sampleSize){
        System.out.println(i+" not enough samples");
        continue;
      }
      else{
        amount=sampleSize;
      }
//      int step = amount/sampleSize;
      double[] costs = new double[amount];
      for (int j = 0; j < amount; j++) {
        try{
          costs[j] =allCost[j];
        }
        catch(NullPointerException e){

        }
      }
      Percentile p = new Percentile();
      double median = p.evaluate(costs);
      List<Double> asList = Arrays.asList(ArrayUtils.toObject(costs));
      double min = Collections.min(asList);
      double lower=p.evaluate(costs, 25);
      double upper = p.evaluate(costs,75);
      double max = Collections.max(asList);
      StandardDeviation standardDeviation= new StandardDeviation();
      double std = standardDeviation.evaluate(costs);
      double average = costsPerFailure[i]/amount;
      double addition = average+std;
      writer.println(i+" "+average + " " + std + " "+" "+min+" "+lower+" "+median+" "+upper+" "+max+" " + addition);
    }
    writer.close();

  }



  private void readInput() {
    BufferedReader br = null;
    try {

      String sCurrentLine;

      br = new BufferedReader(new FileReader("input.txt"));
      br.readLine();
      while ((sCurrentLine = br.readLine()) != null) {
        String[] splitStrings = sCurrentLine.split(",");
        String cost = splitStrings[4];
        this.sum+=Double.valueOf(cost);
        amount++;

      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (br != null)br.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

}
