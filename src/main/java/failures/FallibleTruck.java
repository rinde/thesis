package failures;

import java.util.Iterator;
import java.util.LinkedList;

import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import com.google.common.collect.ImmutableSet;

import rinde.logistics.pdptw.mas.Truck;
import rinde.logistics.pdptw.mas.comm.Communicator;
import rinde.logistics.pdptw.mas.comm.Communicator.CommunicatorEventType;
import rinde.logistics.pdptw.mas.route.RoutePlanner;
import rinde.sim.core.TimeLapse;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.pdp.Parcel;
import rinde.sim.event.Event;
import rinde.sim.event.EventDispatcher;
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
	public boolean isFailing(){
	  return isFailing;
	}
//	@Override
//  protected long computeTravelTimeTo(Point p, Unit<Duration> timeUnit) {
//    long travelTime=super.computeTravelTimeTo(p, timeUnit);
//    
//    return this.failureModel.computeTravelTime(travelTime);
//    
//    
//  }

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
	    return StateMachine.create(waitState)
	        .addTransition(gotoState, FailureEvent.FAILURE, failState)
//	        .addTransition(failState, DefaultEvent.GOTO, gotoState)
	        .addTransition(failState, FailureEvent.RECOVERY, gotoState)
	        .addTransitionsFrom(fsm)
	        .build();

	}
	enum FailureEvent implements StateEvent {
		FAILURE,RECOVERY;
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
				failState.failing=false;
				isFailing=false;

			}
			super.preTick(timeLapse);
		}
		else{
			if(!this.isFailing&&super.stateMachine.stateIs(gotoState)){
				this.isFailing=true;
				failState.failing=true;
				super.stateMachine.handle(FailureEvent.FAILURE,this);
				
			}
		}
	}

	public void handleEvent(Event e) {
//	  if(!isFailing){
	    super.handleEvent(e);
//	  }
//		if (e.getEventType() != CommunicatorEventType.CHANGE) {
//			@SuppressWarnings("unchecked")
//			final StateTransitionEvent<StateEvent, RouteFollowingVehicle> event = (StateTransitionEvent<StateEvent, RouteFollowingVehicle>) e;
//			if (event.event == DefaultEvent.GOTO&&isFailing) {
//				isFailing=false;
//				return;
//			}
//		}
//		super.handleEvent(e);
		
		
	
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
		Parcel currentlyBeingDropped;
		public Failing(){

		}
		@Override
	  public void onEntry(StateEvent event, RouteFollowingVehicle context) {
//			setRoute(new LinkedList<DefaultParcel>());
//		  ImmutableSet<DefaultParcel> contents =pdpModel.get().getContents(context);
//      setRoute(contents);
//			System.out.print("failure/");
			failureModel.indicateIsFailing();
//      if(pdpModel.get().getContents(context).size()>1)
//        System.out.println("Dropping more than one parcel");
//      dropNextParcel(context);


		}
		private void dropNextParcel(RouteFollowingVehicle context) {
		  Iterator<Parcel> it=pdpModel.get().getContents(context).iterator();
		  if(!it.hasNext())
		    return;
		  Parcel nextParcelToDrop=it.next();
		  pdpModel.get().drop(context, nextParcelToDrop, getCurrentTime());
		  currentlyBeingDropped=nextParcelToDrop;

    }
		@Override
		public void onExit(StateEvent event, RouteFollowingVehicle context){
		}
		@Override
		public StateEvent handle(StateEvent event, RouteFollowingVehicle context) {
//		  if(!pdpModel.get().containerContains(context, currentlyBeingDropped)){
//	      dropNextParcel(context);
//		  }


		  getCurrentTime().consumeAll();

		  if(event==null&&!failing){
		    return FailureEvent.RECOVERY;
		  }
		  return null;
		}

	}

}
