package failures;

import rinde.sim.core.TimeLapse;
import rinde.sim.core.model.Model;

public interface FailureModel extends Model<FallibleEntity> {
//	public void initializeFailures(long randomseed);
//	public Map<FallibleEntity, FailureDTO> failingEntities;
//	public Set<FallibleEntity> getCurrentFailures();
	public boolean isFailing(TimeLapse time,FallibleEntity entity);
	public boolean isFailing(long time, FallibleEntity entity);
	public void setMaxFailures(int maxFailures);
	public void setFailureMeanPerDay(double meanFailures);
	public void initializeFailureDurationDistribution(double mean, double standardDeviation);


//	public void setFailureRate(double failureRate);
}
