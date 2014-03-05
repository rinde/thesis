package heuristics;

import rinde.sim.pdptw.common.StatisticsDTO;

public interface AddedObjective {
  public double getAddedObjectiveCost(StatisticsDTO stats);
  public double getMax();
}
