package experiment;


import rinde.logistics.pdptw.mas.Truck;
import rinde.logistics.pdptw.mas.TruckConfiguration;
import rinde.logistics.pdptw.mas.comm.Communicator;
import rinde.logistics.pdptw.mas.route.RoutePlanner;
import rinde.sim.core.Simulator;
import rinde.sim.core.model.Model;
import rinde.sim.pdptw.common.AddVehicleEvent;
import rinde.sim.pdptw.common.DynamicPDPTWProblem.Creator;
import rinde.sim.pdptw.common.VehicleDTO;
import rinde.sim.util.SupplierRng;

import com.google.common.collect.ImmutableList;

import failures.FallibleTruck;

public class FailureTruckConfiguration extends TruckConfiguration {

	public FailureTruckConfiguration(
			SupplierRng<? extends RoutePlanner> routePlannerSupplier,
			SupplierRng<? extends Communicator> communicatorSupplier,
			ImmutableList<? extends SupplierRng<? extends Model<?>>> modelSuppliers) {
		super(routePlannerSupplier, communicatorSupplier, modelSuppliers);
	}
	
	  @Override
	  public Creator<AddVehicleEvent> getVehicleCreator() {
	    return new Creator<AddVehicleEvent>() {
	      public boolean create(Simulator sim, AddVehicleEvent event) {
	        final RoutePlanner rp = rpSupplier.get(sim.getRandomGenerator()
	            .nextLong());
	        final Communicator c = cSupplier.get(sim.getRandomGenerator()
	            .nextLong());
	        return sim.register(createTruck(event.vehicleDTO, rp, c));
	      }
	    };
	  }

	  /**
	   * Factory method that can be overridden by subclasses that want to use their
	   * own {@link Truck} implementation.
	   * @param dto The {@link VehicleDTO} containing the vehicle information.
	   * @param rp The {@link RoutePlanner} to use in the truck.
	   * @param c The {@link Communicator} to use in the truck.
	   * @return The newly created truck.
	   */
	  //TODO: random seed to get function
	  protected Truck createTruck(VehicleDTO dto, RoutePlanner rp, Communicator c) {
	    FallibleTruck truck= new FallibleTruck(dto, rp, c);
//	    Model<?> model2 = mSuppliers.get(0).get(1l);
//	    @Nullable
//	    FailureModel model=null;
//	    if(model2 instanceof FailureModel)
//	    	model = (FailureModel) model2;
//		truck.setFailureModel(model);
	    return truck;
	  }
	

}
