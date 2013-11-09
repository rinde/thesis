package failures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;

import rinde.sim.core.TimeLapse;
import rinde.sim.util.SupplierRng;
import rinde.sim.util.SupplierRng.DefaultSupplierRng;
import rinde.sim.util.TimeWindow;

public class DefaultFailureModel implements FailureModel {
	private int maxFailuresPerVehicle;
	private double failureMean; //per second? per day?
	private	long lengthOfDay = 29000000l;

	private HashMap<FallibleEntity,HashSet<FailureDTO>> failures = new HashMap<FallibleEntity, HashSet<FailureDTO>>();
	private long currentTime;
	public boolean register(FallibleEntity element) {
		// TODO Auto-generated method stub
		element.setFailureModel(this);
		this.failures.put(element,new HashSet<FailureDTO>());
		
		int amountOfFailures= this.poisssonDistribution.sample();
		if(amountOfFailures>this.maxFailuresPerVehicle){
			amountOfFailures=this.maxFailuresPerVehicle;
		}
		for(int i=0; i<amountOfFailures;i++){
			long start = (long) (random.nextDouble()*lengthOfDay);
			long duration = (long) this.normalDistribution.sample();
			this.failures.get(element).add(new FailureDTO (new TimeWindow(start, start+duration)));

		}
		return true;
	}
	private PoissonDistribution poisssonDistribution;
	private NormalDistribution normalDistribution;

	public boolean unregister(FallibleEntity element) {
		// TODO Auto-generated method stub
		this.failures.remove(element);
		return false;
	}
	public void initializeFailureDurationDistribution(double mean, double standardDeviation){
		this.normalDistribution=new NormalDistribution(mean, standardDeviation);
	}
	public void setFailureMeanPerDay(double meanFailures){
		this.failureMean=meanFailures;
		this.poisssonDistribution=new PoissonDistribution(this.failureMean);
	}
	public void setMaxFailures(int maxFailures){
		this.maxFailuresPerVehicle=maxFailures;
	}

	public Class<FallibleEntity> getSupportedType() {
		// TODO Auto-generated method stub
		return FallibleEntity.class;
	}
	private Random random;


	public Set<FallibleEntity> getCurrentFailures() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean isFailing(TimeLapse time, FallibleEntity entity){
	    currentTime = time.getStartTime();
	    return isCurrentlyFailing(entity);

	}
	private boolean isCurrentlyFailing(FallibleEntity entity) {
		if(failures.keySet().contains(entity)){
	    	HashSet<FailureDTO> failures = this.failures.get(entity);
	    	for(FailureDTO failure:failures){
		    	if(failure!=null && this.currentTime >= failure.failureTimeWindow.begin && this.currentTime <= failure.failureTimeWindow.end){
		    		return true;
		    	}

	    	}
	    }
	    return false;
	}
	public DefaultFailureModel(long seed, double failureMeanPerDay, int maxFailures, double meanDuration, double stdDuration){
		this.setFailureMeanPerDay(failureMeanPerDay);
		this.setMaxFailures(maxFailures);
		this.initializeFailureDurationDistribution(meanDuration, stdDuration);
		
		this.random=new Random(seed);
		
	}
	//TODO: make configurable
	public static SupplierRng<DefaultFailureModel> supplier() {
		return new DefaultSupplierRng<DefaultFailureModel>() {
			public DefaultFailureModel get (long seed) {
				DefaultFailureModel failureModel = new DefaultFailureModel(seed,0.5,3,40000,2000);
				return failureModel;
				
			}
		};
	}
	public boolean isFailing(long time, FallibleEntity entity) {
		currentTime = time;
	    return isCurrentlyFailing(entity);
	}
	
	
//	public void tick(TimeLapse timeLapse) {
//		// TODO Auto-generated method stub
//	    currentTime = timeLapse.getStartTime();
//	    for(FallibleEntity entity: failures.keySet()){
//	    	FailureDTO failure = this.failures.get(entity);
//	    	if(failure!=null && this.currentTime >= failure.failureTimeWindow.begin && this.currentTime <= failure.failureTimeWindow.end){
//	    		entity.notifyOfFailure();
//	    		this.roadModel.notifyOfFailure(entity);
//	    		
//	    	}
//	    }
//	    	
//		
//	}
//	
//	public void afterTick(TimeLapse timeLapse) {
//		// TODO Auto-generated method stub
//		this.roadModel.unregisterFailures();
//	}

}
