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
    
    LOGGER.trace("trace");
    // TODO Auto-generated constructor stub
  }
  @Override
  public void registerModelProvider(ModelProvider mp) {
    super.registerModelProvider(mp);
    failureModel =mp.getModel(FailureModel.class);
    

  }
  @Override
  public MoveProgress moveTo(MovingRoadUser object,
      RoadUser destinationRoadUser, TimeLapse time) {
    if(object instanceof FallibleEntity){
      FallibleEntity test = (FallibleEntity) object;
      checkArgument(!test.isFailing());
    }
    return super.moveTo(object, destinationRoadUser, time);

  }
  public FailureModel failureModel;



}
