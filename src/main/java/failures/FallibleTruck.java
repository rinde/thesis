package failures;

import java.util.LinkedList;

import rinde.logistics.pdptw.mas.Truck;
import rinde.logistics.pdptw.mas.comm.Communicator;
import rinde.logistics.pdptw.mas.comm.Communicator.CommunicatorEventType;
import rinde.logistics.pdptw.mas.route.RoutePlanner;
import rinde.sim.core.TimeLapse;
import rinde.sim.event.Event;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.RouteFollowingVehicle;
import rinde.sim.pdptw.common.VehicleDTO;
import rinde.sim.util.fsm.StateMachine;
import rinde.sim.util.fsm.StateMachine.StateTransitionEvent;

public class FallibleTruck extends Truck implements FallibleEntity {
	protected FailureModel failureModel; 
	protected Failing failState;
	private boolean isFailing=false;
	public void setFailureModel(FailureModel model){
		this.failureModel=model;
	}
	public FallibleTruck(VehicleDTO pDto, RoutePlanner rp, Communicator c) {
		super(pDto, rp, c);

	}

	@Override
	protected StateMachine<StateEvent, RouteFollowingVehicle> createStateMachine() {
	    // when overriding, please see doc of super method!

	    // reuse super implementation
	    final StateMachine<StateEvent, RouteFollowingVehicle> fsm = super
	        .createStateMachine();

	    // get ref to existing state
	    final Goto gotoState = fsm.getStateOfType(Goto.class);
	    
	    final Wait waitState = fsm.getStateOfType(Wait.class);

	    // add new state
	    failState = new Failing();

	    // add two new transitions
	    return StateMachine.create(gotoState)
	        .addTransition(gotoState, FailureEvent.FAILURE, failState)
	        .addTransition(failState, DefaultEvent.GOTO, gotoState)
	        .addTransition(failState, DefaultEvent.NOGO, waitState)
	        .addTransitionsFrom(fsm)
	        .build();

	}
	enum FailureEvent implements StateEvent {
		FAILURE;
	}
	/**
	 * Sending a new StateEvent
	 */
//	public void printState(){
//		System.out.println(super.stateMachine.getCurrentState());
//
//	}
	@Override
	public void preTick(TimeLapse timeLapse) {
		if(!failureModel.isFailing(timeLapse, this)){
			if(isFailing){
//				isFailing=false;
				failState.failing=false;
//				super.stateMachine.handle(null,this);
//				super.stateMachine.handle(StateEvent.NOGO,this);

			}
			super.preTick(timeLapse);
		}
		else{
			if(!this.isFailing&&super.stateMachine.stateIs(gotoState)){
				this.isFailing=true;
				failState.failing=true;
//				this.failState.previous=super.stateMachine.getCurrentState();
				super.stateMachine.handle(FailureEvent.FAILURE,this);
			}

			//   	 		timeLapse.consumeAll();
			//   	 		super.setRoute(new LinkedList<DefaultParcel>());
			//   	 		if(super.stateMachine.stateIs(gotoState))
			//   	 			super.stateMachine.handle(StateEvent.NOGO, this);
		}
	}

	public void handleEvent(Event e) {
		if (e.getEventType() != CommunicatorEventType.CHANGE) {
			@SuppressWarnings("unchecked")
			final StateTransitionEvent<StateEvent, RouteFollowingVehicle> event = (StateTransitionEvent<StateEvent, RouteFollowingVehicle>) e;
			if (event.event == DefaultEvent.GOTO&&isFailing) {
				isFailing=false;
				return;
			}
		}
		super.handleEvent(e);
		
		
	
	}


	//	@Override
	//	public void handleEvent(Event e) {
	//		if(!failureModel.isFailing(getCurrentTime(), this)){
	//			super.handleEvent(e);
	//		}
	//   	 	else{
	//   	 		super.setRoute(new LinkedList<DefaultParcel>());
	//   	 		if(super.stateMachine.stateIs(gotoState))
	//   	 			super.stateMachine.handle(StateEvent.NOGO, this);
	//   	 	}
	//}
	//	public void afterTick(TimeLapse time) {
	//		if(!failureModel.isFailing(getCurrentTime(), this)){
	//			super.afterTick(time);
	//		}
	//		else{
	//			super.setRoute(new LinkedList<DefaultParcel>());
	//			if(super.stateMachine.stateIs(gotoState))
	//				super.stateMachine.handle(StateEvent.NOGO, this);
	//		}
	//	}



	protected class Failing extends AbstractTruckState{

		public boolean failing=false;
		public Failing(){

		}
		@Override
	    public void onEntry(StateEvent event, RouteFollowingVehicle context) {
			setRoute(new LinkedList<DefaultParcel>());
		}
		@Override
		public StateEvent handle(StateEvent event, RouteFollowingVehicle context) {
			getCurrentTime().consumeAll();
			if(event==null&&!failing){
				return DefaultEvent.NOGO;
			}
			return null;
		}

	}

}
