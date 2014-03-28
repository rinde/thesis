package vehicles;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import rinde.sim.core.TimeLapse;
import rinde.sim.core.model.pdp.PDPModel.ParcelState;
import rinde.sim.core.model.pdp.Parcel;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.RouteFollowingVehicle;
import rinde.sim.pdptw.common.VehicleDTO;
import rinde.sim.util.fsm.StateMachine;

import com.google.common.collect.ImmutableSet;

import failures.FailureModel;
import failures.FallibleEntity;

public class FallibleRouteFollowingVehicle extends RouteFollowingVehicle implements FallibleEntity{



  public FallibleRouteFollowingVehicle(VehicleDTO pDto,
      boolean allowDelayedRouteChanging) {
    super(pDto, allowDelayedRouteChanging);
    // TODO Auto-generated constructor stub
  }
  private FailureModel failureModel;
  @Override
  public void setFailureModel(FailureModel model) {
    // TODO Auto-generated method stub
    this.failureModel=model;
  }

  protected Failing failState;
  private boolean isFailing=false;

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
      failState = new Failing();

      // add two new transitions
      return StateMachine.create(waitState)
          .addTransition(gotoState, FailureEvent.FAILURE, failState)
//          .addTransition(failState, DefaultEvent.GOTO, gotoState)
          .addTransition(failState, FailureEvent.RECOVERY, waitState)
          .addTransitionsFrom(fsm).explicitRecursiveTransitions()
          .build();

  }
  enum FailureEvent implements StateEvent {
    FAILURE,RECOVERY;
  }
  /**
   * Sending a new StateEvent
   */
//  public void printState(){
//    System.out.println(super.stateMachine.getCurrentState());
//
//  }
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
  public boolean routeContainsOnlyLoadedParcels(){
    for(DefaultParcel dp:this.getRoute()){
      ParcelState parcelState = this.pdpModel.get().getParcelState(dp);
      if(parcelState!=ParcelState.IN_CARGO){
        return false;
      }
    }
    return true;
  }
  public Set<Parcel> getLoadedParcels(){
    ImmutableSet<Parcel> contents = this.pdpModel.get().getContents(this);
    Collection<DefaultParcel> oldRoute = this.getRoute();
    Set<Parcel> result = new HashSet<Parcel>();

    for(DefaultParcel p:oldRoute){
      if(contents.contains(p)){
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
//      setRoute(new LinkedList<DefaultParcel>());
//      ImmutableSet<DefaultParcel> contents =pdpModel.get().getContents(context);
//      setRoute(contents);
//      System.out.print("failure/");
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
//      if(!pdpModel.get().containerContains(context, currentlyBeingDropped)){
//        dropNextParcel(context);
//      }


      getCurrentTime().consumeAll();

      if(event==null&&!failing){
        return FailureEvent.RECOVERY;
      }
      return null;
    }

  }

}
