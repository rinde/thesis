package failures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

import rinde.sim.core.TimeLapse;
import rinde.sim.util.SupplierRng;
import rinde.sim.util.SupplierRng.DefaultSupplierRng;
import rinde.sim.util.TimeWindow;

public class DefaultFailureModel implements FailureModel {
	private int maxFailuresPerVehicle;
	private double failureMean; //per day
	private	long lengthOfDay = 14400000l; //4uur
							   
	private HashMap<FallibleEntity,HashSet<FailureDTO>> failures = new HashMap<FallibleEntity, HashSet<FailureDTO>>();
	private long currentTime;
	private ArrayList<Integer> failuresPerScenario = new ArrayList<Integer>();
	private int currentScenario=-1;
	private int currentTrucksInScenario;
	private int totalTrucksPerScenario=10;
	private int numberFailures=0;
	public void getFailuresPerScenario(){
//	  System.out.println(numberFailures);
	}

	public boolean register(FallibleEntity element) {
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

//		if(currentTrucksInScenario==totalTrucksPerScenario){
//		  currentTrucksInScenario=0;
//		  currentScenario++;
//		  failuresPerScenario.add(0);
//		}
//		currentTrucksInScenario++;
//		Integer current = failuresPerScenario.get(currentScenario)+amountOfFailures;
//		this.numberFailures+=amountOfFailures;
//		failuresPerScenario.add(currentScenario, current);
//    if(currentTrucksInScenario==totalTrucksPerScenario){
//      getFailuresPerScenario();
//    }
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
		this.normalDistribution=new NormalDistribution(this.random,mean, standardDeviation,NormalDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
	}
	public void setFailureMeanPerDay(double meanFailures){
		this.failureMean=meanFailures;
		this.poisssonDistribution = new PoissonDistribution(this.random, failureMean, PoissonDistribution.DEFAULT_EPSILON, PoissonDistribution.DEFAULT_MAX_ITERATIONS);
	}
	public void setMaxFailures(int maxFailures){
		this.maxFailuresPerVehicle=maxFailures;
	}

	public Class<FallibleEntity> getSupportedType() {
		// TODO Auto-generated method stub
		return FallibleEntity.class;
	}
	private RandomGenerator random;


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
		this.random=new JDKRandomGenerator();
		this.random.setSeed(seed);
		this.setFailureMeanPerDay(failureMeanPerDay);
		this.setMaxFailures(maxFailures);
		this.initializeFailureDurationDistribution(meanDuration, stdDuration);
		this.totalTrucksPerScenario=10;
		this.currentTrucksInScenario=this.totalTrucksPerScenario;

		
	}
	//TODO: make configurable
	public static SupplierRng<DefaultFailureModel> supplier() {
		return new DefaultSupplierRng<DefaultFailureModel>() {
			public DefaultFailureModel get (long seed) {
				DefaultFailureModel failureModel = new DefaultFailureModel(seed,0.4,15,3600000,600000);
				return failureModel;
				
			}
		};
	}
	public boolean isFailing(long time, FallibleEntity entity) {
		currentTime = time;
	    return isCurrentlyFailing(entity);
	}
	
	

}
