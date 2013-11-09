package experiment;

import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.pdptw.common.StatisticsDTO;

public class FailureObjectiveFunction implements ObjectiveFunction{

	public boolean isValidResult(StatisticsDTO stats) {
		// TODO Auto-generated method stub
		return false;
	}

	public double computeCost(StatisticsDTO stats) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String printHumanReadableFormat(StatisticsDTO stats) {
		// TODO Auto-generated method stub
		return null;
	}

}
