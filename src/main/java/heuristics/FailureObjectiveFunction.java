package heuristics;

import java.util.HashMap;
import java.util.Map;

import rinde.sim.pdptw.common.StatisticsDTO;
import rinde.sim.pdptw.gendreau06.Gendreau06ObjectiveFunction;

public class FailureObjectiveFunction extends Gendreau06ObjectiveFunction {

  public FailureObjectiveFunction() {
    // TODO Auto-generated constructor stub
    this.objectives.put(new FreeTimeObjectiveFunction(),Math.pow(10, 0));
    this.objectives.put(new WorkloadObjectiveFunction(),1.0);
  }
  private Map<AddedObjective,Double> objectives = new HashMap<AddedObjective,Double>();
  public double computeCost(StatisticsDTO stats) {
    // TODO Auto-generated method stub
    double regularCost = super.computeCost(stats);
    double addedCost = calculateAddedCost(stats);
    return regularCost+addedCost;
    
  }

  private double calculateAddedCost(StatisticsDTO stats) {
    double addedCost = 0;
    
    for (AddedObjective objective : this.objectives.keySet()) {
      double cost = this.objectives.get(objective)*objective.getAddedObjectiveCost(stats);
      addedCost+=cost;
//      System.out.println(cost);
    }
    return addedCost;
  }

  public String printHumanReadableFormat(StatisticsDTO stats) {
    // TODO Auto-generated method stub
    String parentString = super.printHumanReadableFormat(stats);
    return new StringBuilder().append(parentString).append("workloadCost: "+calculateAddedCost(stats)).toString();
  }

}
