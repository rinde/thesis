package failures;

import java.util.Arrays;

import rinde.logistics.pdptw.mas.comm.NegotiatingBidder;
import rinde.sim.pdptw.central.Solver;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.util.SupplierRng;
import rinde.sim.util.SupplierRng.DefaultSupplierRng;

import com.google.common.base.Joiner;

public class FailureNegotiatingBidder extends NegotiatingBidder {

  public FailureNegotiatingBidder(ObjectiveFunction objFunc, Solver s1,
      Solver s2, int numOfNegotiators, SelectNegotiatorsHeuristic h) {
    super(objFunc, s1, s2, numOfNegotiators, h);
    // TODO Auto-generated constructor stub
  }
  @Override
  public double getBidFor(DefaultParcel p, long time) {
    FallibleTruck fTruck;
    if(vehicle.get() instanceof FallibleTruck){
      fTruck = ((FallibleTruck) vehicle.get());
      if(fTruck.isFailing()){
        return Double.MAX_VALUE;
      }
    }
    return super.getBidFor(p, time);
    
    
  }
  public static SupplierRng<NegotiatingBidder> supplier(
      final ObjectiveFunction objFunc,
      final SupplierRng<? extends Solver> bidderSolverSupplier,
      final SupplierRng<? extends Solver> negoSolverSupplier,
      final int numOfNegotiators, final SelectNegotiatorsHeuristic heuristic) {
    return new DefaultSupplierRng<NegotiatingBidder>() {
      public NegotiatingBidder get(long seed) {
        return new FailureNegotiatingBidder(objFunc, bidderSolverSupplier.get(seed),
            negoSolverSupplier.get(seed), numOfNegotiators, heuristic);
      }

      @Override
      public String toString() {
        return Joiner.on('-').join(
            Arrays.<Object> asList(super.toString(),
                bidderSolverSupplier.toString(), negoSolverSupplier.toString(),
                numOfNegotiators, heuristic.toString().replaceAll("_", "-")));
      }
    };
  }


}
