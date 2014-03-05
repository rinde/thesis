package heuristics;

import rinde.sim.pdptw.common.StatisticsDTO;
import rinde.sim.pdptw.gendreau06.Gendreau06ObjectiveFunction;

public class WorkloadObjectiveFunction extends Gendreau06ObjectiveFunction implements AddedObjective{

  private double workLoadCoefficient=1;
  private double power= 2;
  private double maxParcels= 80;

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
    // TODO Auto-generated method stub
    int totalParcels = stats.totalParcels;
    if(totalParcels>maxParcels){
      throw new IllegalArgumentException("maximum parcels has been exceeded");
    }
    return (Math.pow(totalParcels,power))*workLoadCoefficient;
  }

  public double getMax() {
    // TODO Auto-generated method stub
    return Math.pow(maxParcels, power);
  }

}
