package failures;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Optional;

import rinde.sim.core.TimeLapse;
import rinde.sim.core.model.ModelProvider;
import rinde.sim.core.model.pdp.PDPModel;
import rinde.sim.core.model.road.AbstractRoadModel;
import rinde.sim.core.model.road.MoveProgress;
import rinde.sim.core.model.road.MovingRoadUser;
import rinde.sim.core.model.road.RoadUser;
import rinde.sim.pdptw.common.PDPRoadModel;


public class PDPFailureRoadModel extends PDPRoadModel implements FallibleRoadModel {

	public PDPFailureRoadModel(AbstractRoadModel<?> rm,
			boolean allowVehicleDiversion) {
		super(rm, allowVehicleDiversion);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void registerModelProvider(ModelProvider mp) {
	    failureModel =mp.getModel(FailureModel.class);
	    pdpModel = Optional.fromNullable(mp.getModel(PDPModel.class));
//	    failureModel = Optional.fromNullable(mp.getModel(FailureModel.class));

	    
	}
	@Override
	public MoveProgress moveTo(MovingRoadUser object,
			RoadUser destinationRoadUser, TimeLapse time) {
		if(object instanceof FallibleEntity){
//			checkArgument(!this.failureModel.isFailing(time, (FallibleEntity) object));
//			if(!this.failureModel.isFailing(time, (FallibleEntity) object))
//				return super.moveTo(object, destinationRoadUser, time);
//			else
//				return null;
		}
		return super.moveTo(object, destinationRoadUser, time);

	}
//	private HashSet<FallibleEntity> failures = new HashSet<FallibleEntity>();
	public FailureModel failureModel;
//	public void notifyOfFailure(FallibleEntity failingEntity) {
//		// TODO Auto-generated method stub
//		failures.add(failingEntity);
//	}
//
//	public void unregisterFailures() {
//		// TODO Auto-generated method stub
//		failures.clear();
//	}
	

}
