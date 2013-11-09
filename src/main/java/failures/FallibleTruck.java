package failures;

import java.util.LinkedList;

import rinde.logistics.pdptw.mas.Truck;
import rinde.logistics.pdptw.mas.comm.Communicator;
import rinde.logistics.pdptw.mas.route.RoutePlanner;
import rinde.sim.core.TimeLapse;
import rinde.sim.event.Event;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.VehicleDTO;

public class FallibleTruck extends Truck implements FallibleEntity {
    protected FailureModel failureModel; 
    public void setFailureModel(FailureModel model){
    	this.failureModel=model;
    }
	public FallibleTruck(VehicleDTO pDto, RoutePlanner rp, Communicator c) {
		super(pDto, rp, c);
		
	}
	@Override
    public void preTick(TimeLapse timeLapse) {
   	 	if(!failureModel.isFailing(timeLapse, this)){
   	 		super.preTick(timeLapse);
   	 	}
   	 	else{
   	 		super.setRoute(new LinkedList<DefaultParcel>());
   	 		if(super.stateMachine.stateIs(gotoState))
   	 			super.stateMachine.handle(StateEvent.NOGO, this);
   	 	}
	}

	@Override
	public void handleEvent(Event e) {
		if(!failureModel.isFailing(getCurrentTime(), this)){
			super.handleEvent(e);
		}
   	 	else{
   	 		super.setRoute(new LinkedList<DefaultParcel>());
   	 		if(super.stateMachine.stateIs(gotoState))
   	 			super.stateMachine.handle(StateEvent.NOGO, this);
   	 	}
	}

	
//	public void handle(Event e){
//		if (e.getEventType() == CommunicatorEventType.CHANGE) {
//			super.changed = true;
//		} else {
//			// we know this is safe since it can only be one type of event
//			@SuppressWarnings("unchecked")
//			final StateTransitionEvent<StateEvent, RouteFollowingVehicle> event = (StateTransitionEvent<StateEvent, RouteFollowingVehicle>) e;
//			if (event.event == StateEvent.GOTO && !failureModel.isFailing(getCurrentTime(), this)) {
//				final DefaultParcel cur = getRoute().iterator().next();
//				if (pdpModel.get().getParcelState(cur) != ParcelState.IN_CARGO) {
//					communicator.claim(cur);
//				}
//			} else if (event.event == StateEvent.DONE) {
//				routePlanner.next(getCurrentTime().getTime());
//			}
//		}
//	}
	public class FallibleGoto extends Goto{
		
	}

}
