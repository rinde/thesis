package failures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.measure.Measure;
import javax.measure.quantity.Velocity;

import com.google.common.base.Optional;

import rinde.logistics.pdptw.mas.comm.Communicator;
import rinde.logistics.pdptw.mas.comm.Communicator.CommunicatorEventType;
import rinde.logistics.pdptw.mas.route.RoutePlanner;
import rinde.sim.core.TimeLapse;
import rinde.sim.core.model.pdp.PDPModel.ParcelState;
import rinde.sim.event.Event;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.VehicleDTO;
import rinde.sim.util.fsm.State;
import rinde.sim.util.fsm.StateMachine.StateTransitionEvent;
import truck.RouteFollowingVehicle;
import truck.Truck;

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
	protected Set<StateTransition> getAdditionalTransitions(){
		HashSet<StateTransition> result = new HashSet<StateTransition>();
		this.failState = new Failing();
//		result.add(new StateTransition(waitState, StateEvent.FAIL, failState));
		result.add(new StateTransition(gotoState, StateEvent.FAIL, failState));
//		result.add(new StateTransition(waitForServiceState, StateEvent.FAIL, failState));
//		result.add(new StateTransition(serviceState, StateEvent.FAIL, failState));
		result.add(new StateTransition(failState, StateEvent.GOTO, gotoState));
//		result.add(new StateTransition(failState, StateEvent.GOTO, gotoState));
//		result.add(new StateTransition(failState, StateEvent.ARRIVED, waitForServiceState));
//		result.add(new StateTransition(failState, StateEvent.READY_TO_SERVICE, serviceState));



		return result;
	}
//	protected Measure<Double, Velocity> oldSpeed;
//	@Override
//	public void preTick(TimeLapse timeLapse){
//		if(!failureModel.isFailing(timeLapse, this)){
//			if(isFailing){
//				isFailing=false;
//				super.speed=Optional.of(oldSpeed);
//			}
//			super.preTick(timeLapse);
//		}
//		else{
//			if(!this.isFailing){
//				isFailing=true;
//				oldSpeed = super.speed.get();
//				super.speed=Optional.of(Measure.valueOf(0.00001, roadModel.get()
//						.getSpeedUnit()));
//			}
//		}
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
				super.stateMachine.handle(StateEvent.FAIL,this);
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
			if (event.event == StateEvent.GOTO&&isFailing) {
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
	protected class Failing extends AbstractTruckState{
//		@Override
//		public void onEntry(StateEvent event, RouteFollowingVehicle context) {
//
//		}
		public boolean failing=false;
		public Failing(){
			events.put(gotoState, StateEvent.GOTO);
			events.put(waitState, StateEvent.NOGO);
			events.put(serviceState, StateEvent.NOGO);
			events.put(waitForServiceState, StateEvent.NOGO);
		}
		HashMap<AbstractTruckState,StateEvent> events=new HashMap<AbstractTruckState, StateEvent>();
		public State<StateEvent, RouteFollowingVehicle> previous;
		@Override
		public StateEvent handle(StateEvent event, RouteFollowingVehicle context) {
			getCurrentTime().consumeAll();
			if(event==null&&!failing){
				return StateEvent.GOTO;
			}
			return null;
		}

	}

}
