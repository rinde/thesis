package experiment;


import rinde.logistics.pdptw.mas.Truck;
import rinde.logistics.pdptw.mas.TruckConfiguration;
import rinde.logistics.pdptw.mas.comm.Communicator;
import rinde.logistics.pdptw.mas.route.RoutePlanner;
import rinde.sim.core.model.Model;
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


	  /**
	   * Factory method that can be overridden by subclasses that want to use their
	   * own {@link Truck} implementation.
	   * @param dto The {@link VehicleDTO} containing the vehicle information.
	   * @param rp The {@link RoutePlanner} to use in the truck.
	   * @param c The {@link Communicator} to use in the truck.
	   * @return The newly created truck.
	   */
	  protected Truck createTruck(VehicleDTO dto, RoutePlanner rp, Communicator c) {
	    FallibleTruck truck= new FallibleTruck(dto, rp, c);

	    return truck;
	  }
	

}
