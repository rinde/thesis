package failures;

import java.io.Serializable;

import rinde.sim.util.TimeWindow;

public class FailureDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * The time window in which this vehicle is available.
	 */
	public final TimeWindow failureTimeWindow;

	public FailureDTO(TimeWindow failureTimeWindow){
		this.failureTimeWindow = failureTimeWindow;
	}
}
