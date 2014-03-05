package heuristics;

import java.util.HashMap;
import java.util.Map;

import rinde.sim.pdptw.common.StatisticsDTO;
import rinde.sim.pdptw.gendreau06.Gendreau06ObjectiveFunction;

public class FailureObjectiveFunction extends Gendreau06ObjectiveFunction {

  public FailureObjectiveFunction() {
    // TODO Auto-generated constructor stub
    this.objectiveWeights.put(new FreeTimeObjectiveFunction(),100.0);
    this.objectiveWeights.put(new WorkloadObjectiveFunction(),10000.0);
  }
  private Map<AddedObjective,Double> objectiveWeights = new HashMap<AddedObjective,Double>();
  public double computeCost(StatisticsDTO stats) {
    // TODO Auto-generated method stub
    double regularCost = super.computeCost(stats);
    double addedCost = calculateAddedCost(stats);
    return regularCost+addedCost;
    
  }

  private double calculateAddedCost(StatisticsDTO stats) {
    double addedCost = 0;
    
    for (AddedObjective objective : this.objectiveWeights.keySet()) {
      double cost = this.objectiveWeights.get(objective)*(objective.getAddedObjectiveCost(stats)/objective.getMax());
//      System.out.println(objective.toString()+ " : "+ cost);
      addedCost+=cost;
    }
    return addedCost;
  }

  public String printHumanReadableFormat(StatisticsDTO stats) {
    // TODO Auto-generated method stub
    String parentString = super.printHumanReadableFormat(stats);
    return new StringBuilder().append(parentString).append("workloadCost: "+calculateAddedCost(stats)).toString();
  }

}
