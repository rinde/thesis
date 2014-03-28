package failures;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import rinde.logistics.pdptw.mas.Truck;
import rinde.logistics.pdptw.mas.comm.Communicator;
import rinde.logistics.pdptw.mas.comm.Communicator.CommunicatorEventType;
import rinde.logistics.pdptw.mas.route.RoutePlanner;
import rinde.sim.core.TimeLapse;
import rinde.sim.core.model.pdp.PDPModel.ParcelState;
import rinde.sim.core.model.pdp.Parcel;
import rinde.sim.event.Event;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.RouteFollowingVehicle;
import rinde.sim.pdptw.common.VehicleDTO;
import rinde.sim.util.fsm.StateMachine;
import rinde.sim.util.fsm.StateMachine.StateTransitionEvent;

import com.google.common.collect.ImmutableSet;

public class FallibleTruck extends Truck implements FallibleEntity {
	protected FailureModel failureModel; 
	protected Failing failState;
	private boolean isFailing=false;
	public void setFailureModel(FailureModel model){
		this.failureModel=model;
	}
	public FallibleTruck(VehicleDTO pDto, RoutePlanner rp, Communicator c) {
		super(pDto, rp, c);
		this.failState=stateMachine.getStateOfType(Failing.class);


	}
	public boolean isFailing(){
	  return isFailing;
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
	    final Failing failingState = new Failing();

	    // add two new transitions
	    return StateMachine.create(waitState)
	        .addTransition(gotoState, FailureEvent.FAILURE, failingState)
//	        .addTransition(failingState, DefaultEvent.GOTO, gotoState)
	        .addTransition(failingState, FailureEvent.RECOVERY, gotoState)
	        .addTransitionsFrom(fsm).explicitRecursiveTransitions()
	        .build();

	}
	enum FailureEvent implements StateEvent {
		FAILURE,RECOVERY;
	}

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
  @Override
  public void handleEvent(Event e) {
    if (e.getEventType() == CommunicatorEventType.CHANGE) {
      super.handleEvent(e);
      return;
    }
    final StateTransitionEvent<StateEvent, RouteFollowingVehicle> event = (StateTransitionEvent<StateEvent, RouteFollowingVehicle>) e;

    if(event.event==FailureEvent.FAILURE){
      if (!pdpModel.get().getParcelState(gotoState.getPreviousDestination())
          .isPickedUp()) {
        getCommunicator().unclaim(gotoState.getPreviousDestination());
      }
    }
//    else if(event.event==FailureEvent.RECOVERY){
//      final DefaultParcel cur = getRoute().iterator().next();
//      LOGGER.trace("event: "+e + "currentDestination: " + cur);
//
//      if (!pdpModel.get().getParcelState(cur).isPickedUp()) {
//        getCommunicator().claim(cur);
//      }
//    }
    else{
      super.handleEvent(e);
    }


  }
  public List<Parcel> getLoadedParcels(){
    ImmutableSet<Parcel> contents = this.pdpModel.get().getContents(this);
    Collection<DefaultParcel> oldRoute = this.getRoute();
    List<Parcel> result = new LinkedList<Parcel>();

    for(DefaultParcel p:oldRoute){
      ParcelState state = pdpModel.get().getParcelState(p);
      if(contents.contains(p)&& !result.contains(p)&&state.isPickedUp()){
        result.add(p);
      }


    }
    return result;
  }




	protected class Failing extends AbstractTruckState{
		public boolean failing=false;
		Parcel currentlyBeingDropped;
		public Failing(){

		}
		@Override
	  public void onEntry(StateEvent event, RouteFollowingVehicle context) {		  
//      LinkedList<DefaultParcel> newRoute =new LinkedList<DefaultParcel>();
////
//		  List<Parcel> loadedParcels = getLoadedParcels();
////		  
//      for(Parcel p: loadedParcels){
//        
//        if(p instanceof DefaultParcel){
//          DefaultParcel parcel = (DefaultParcel) p;
//          newRoute.add(parcel);
//        }
//       
//      }
//      setRoute(newRoute);
//      updateRoute();
		  ((FailureSolverBidder) getCommunicator()).release();
			failureModel.indicateIsFailing();

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
		  getCurrentTime().consumeAll();

		  if(event==null&&!failing){
		    
		      return FailureEvent.RECOVERY;
		  }
		  return null;
		}

	}

}
