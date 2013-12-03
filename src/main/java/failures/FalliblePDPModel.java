package failures;

import rinde.sim.core.model.pdp.ForwardingPDPModel;
import rinde.sim.core.model.pdp.PDPModel;
import rinde.sim.core.model.pdp.PDPModelEvent;
import rinde.sim.core.model.pdp.Parcel;
import rinde.sim.core.model.pdp.Vehicle;

public class FalliblePDPModel extends ForwardingPDPModel {

	public FalliblePDPModel(PDPModel deleg) {
		super(deleg);
	}
	protected void doDrop(Vehicle vehicle, Parcel parcel, long time) {
		synchronized (this) {
			super.containerContents.remove(vehicle, parcel);
			containerContentsSize.put(vehicle, containerContentsSize.get(vehicle)
					- parcel.getMagnitude());
			roadModel.get().addObjectAtSamePosition(parcel,vehicle);
			parcelState.put(ParcelState.AVAILABLE, parcel);
//			LOGGER.info("{} end delivery of {} by {}", time, parcel, vehicle);
			eventDispatcher.dispatchEvent(new PDPModelEvent(
					PDPModelEventType.PARCEL_AVAILABLE, self, time, parcel, null));
		}
	}

}
