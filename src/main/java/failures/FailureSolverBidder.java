package failures;

import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.Queue;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import models.ReAuctionCommModel;
import rinde.logistics.pdptw.mas.Truck;
import rinde.logistics.pdptw.mas.comm.SolverBidder;
import rinde.sim.core.model.pdp.PDPModel;
import rinde.sim.core.model.pdp.PDPModel.PDPModelEventType;
import rinde.sim.core.model.pdp.PDPModelEvent;
import rinde.sim.core.model.pdp.Parcel;
import rinde.sim.event.Event;
import rinde.sim.pdptw.central.GlobalStateObject;
import rinde.sim.pdptw.central.Solver;
import rinde.sim.pdptw.central.SolverValidator;
import rinde.sim.pdptw.central.Solvers;
import rinde.sim.pdptw.central.Solvers.SolveArgs;
import rinde.sim.pdptw.central.Solvers.StateContext;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.pdptw.common.ParcelDTO;
import rinde.sim.util.SupplierRng;
import rinde.sim.util.SupplierRng.DefaultSupplierRng;

public class FailureSolverBidder extends SolverBidder implements FallibleBidder {

  public FailureSolverBidder(ObjectiveFunction objFunc, Solver s) {
    // TODO Auto-generated constructor stub
    super(objFunc,s);
    LOGGER.trace("trace");
  }
  @Override
  public double getBidFor(DefaultParcel p, long time) {
    FallibleTruck fTruck;
    final ImmutableList<DefaultParcel> currentRoute = ImmutableList
        .copyOf(((Truck) vehicle.get()).getRoute());
    if(currentRoute.contains(null)){
      System.err.println("nulll in route");
    }
    if(vehicle.get() instanceof FallibleTruck){
      fTruck = ((FallibleTruck) vehicle.get());
      if(fTruck.isFailing()){
//        return Double.MAX_VALUE;
        return super.getBidFor(p, time)+1000;
      }


    }
    return super.getBidFor(p, time);
    
    
  }

  public void release(){
    this.assignedParcels.clear();
    eventDispatcher
    .dispatchEvent(new Event(CommunicatorEventType.CHANGE, this));
  }

  public static SupplierRng<SolverBidder> supplier(
      final ObjectiveFunction objFunc,
      final SupplierRng<? extends Solver> solverSupplier) {
    return new DefaultSupplierRng<SolverBidder>() {
      public SolverBidder get(long seed) {
        return new FailureSolverBidder(objFunc, solverSupplier.get(seed));
      }

      @Override
      public String toString() {
        return super.toString() + "-" + solverSupplier.toString();
      }
    };
  }
  private ReAuctionCommModel reauctions;
  @Override
  public void setAuctionModel(ReAuctionCommModel reAuctionCommModel) {
    // TODO Auto-generated method stub
    reauctions=reAuctionCommModel;
  }
  public void reauction(Set<DefaultParcel> parcels,long time){
    this.reauctions.reauction(parcels, time);
  }
}
