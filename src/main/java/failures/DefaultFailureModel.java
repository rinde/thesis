package failures;

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
	private int numberFailures=0;
	private int actualNumberOfFailures=0;


	public boolean register(FallibleEntity element) {
		element.setFailureModel(this);
		this.failures.put(element,new HashSet<FailureDTO>());
		
		int amountOfFailures= this.poisssonDistribution.sample();
		if(amountOfFailures>this.maxFailuresPerVehicle){
			amountOfFailures=this.maxFailuresPerVehicle;
		}
		numberFailures+=amountOfFailures;
		for(int i=0; i<amountOfFailures;i++){
		  long duration = getFailureDuration();

			long start = (long) (random.nextDouble()*(lengthOfDay-duration));
			this.failures.get(element).add(new FailureDTO (new TimeWindow(start, start+duration)));

		}

		return true;
	}
  private long getFailureDuration() {
//    long duration = (long) this.normalDistribution.sample();
    long duration = (long) this.normalDistribution.getMean();

    return duration;
  }
	private PoissonDistribution poisssonDistribution;
	private NormalDistribution normalDistribution;
	private NormalDistribution travelTimeDistribution;

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

		this.travelTimeDistribution=new NormalDistribution(random, 1.0, 4.0, NormalDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
		
	}
	public static SupplierRng<DefaultFailureModel> supplier(final double failuremean) {
		return new DefaultSupplierRng<DefaultFailureModel>() {
			public DefaultFailureModel get (long seed) {
				DefaultFailureModel failureModel = new DefaultFailureModel(seed,failuremean,15,3600000,600000);
				return failureModel;
				
			}
		};
	}
	public boolean isFailing(long time, FallibleEntity entity) {
		currentTime = time;
	    return isCurrentlyFailing(entity);
	}



  public long computeTravelTime(long time) {
    return time;


    
//    double sample = this.travelTimeDistribution.sample();
//    if(sample<0.8){
//      sample=0.8;
//    }
//    long newTime = (long) (time*sample);
//    
//
//    return newTime;
  }

  @Override
  public void indicateIsFailing() {
    // TODO Auto-generated method stub
    actualNumberOfFailures++;
  }

  @Override
  public int getAmountOfFailures() {
    // TODO Auto-generated method stub
    return this.actualNumberOfFailures;
  }
	
	

}
