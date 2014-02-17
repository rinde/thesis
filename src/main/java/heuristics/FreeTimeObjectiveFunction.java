package heuristics;

import rinde.sim.pdptw.common.StatisticsDTO;
import rinde.sim.pdptw.gendreau06.Gendreau06ObjectiveFunction;

public class FreeTimeObjectiveFunction extends Gendreau06ObjectiveFunction implements AddedObjective{

  public FreeTimeObjectiveFunction() {
    // TODO Auto-generated constructor stub
    
  }
  private double freeTimeCoefficient=1;
  private double speed=30;
  private double max = 10000000000.0;
  private double power = 6;
  

  public double computeCost(StatisticsDTO stats) {
    // TODO Auto-generated method stub
    double regularCost = super.computeCost(stats);
    double addedObjectiveCost = getAddedObjectiveCost(stats);
    return regularCost+addedObjectiveCost;
    
  }

  public String printHumanReadableFormat(StatisticsDTO stats) {
    // TODO Auto-generated method stub
    String parentString = super.printHumanReadableFormat(stats);
    return new StringBuilder().append(parentString).append("workloadCost: "+getAddedObjectiveCost(stats)).toString();
  }

  public double getAddedObjectiveCost(StatisticsDTO stats) {
    
    double freeTime = stats.simulationTime/3600000-stats.totalDistance/speed;
    return Math.max(0, freeTimeCoefficient*(-Math.pow(freeTime, power)+max));
  }

}
