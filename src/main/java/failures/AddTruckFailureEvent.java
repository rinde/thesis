package failures;

import rinde.sim.scenario.TimedEvent;

public class AddTruckFailureEvent extends TimedEvent {

	public final FailureDTO failureDTO;
	
	
	public AddTruckFailureEvent(FailureDTO dto) {
		super(FailureScenarioEvent.ADD_FAILURE, dto.failureTimeWindow.begin);
		this.failureDTO=dto;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4546260324551407016L;
	
	
}
