package failures;

import java.util.Collection;
import java.util.HashSet;

import rinde.logistics.pdptw.mas.comm.SolverBidder;
import rinde.sim.event.Event;
import rinde.sim.pdptw.central.Solver;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.util.SupplierRng;
import rinde.sim.util.SupplierRng.DefaultSupplierRng;

public class FailureSolverBidder extends SolverBidder {

  public FailureSolverBidder(ObjectiveFunction objFunc, Solver s) {
    // TODO Auto-generated constructor stub
    super(objFunc,s);
    LOGGER.trace("trace");
  }
  @Override
  public double getBidFor(DefaultParcel p, long time) {
    FallibleTruck fTruck;
    if(vehicle.get() instanceof FallibleTruck){
      fTruck = ((FallibleTruck) vehicle.get());
      if(fTruck.isFailing()){
//        return Double.MAX_VALUE;
        return super.getBidFor(p, time)+1000;
      }
    }
    return super.getBidFor(p, time);
    
    
  }
  public void update(Collection<DefaultParcel> newRoute){
    this.assignedParcels.clear();
    this.claimedParcels.clear();
//    for(DefaultParcel p:newRoute){
//      this.assignedParcels.add(p);
//    }
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

}
