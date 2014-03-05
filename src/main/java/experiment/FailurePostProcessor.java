package experiment;

import rinde.sim.core.Simulator;
import rinde.sim.pdptw.experiment.PostProcessor;
import failures.FailureModel;

public class FailurePostProcessor implements PostProcessor<Integer> {

  public FailurePostProcessor() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public Integer collectResults(Simulator sim) {
    // TODO Auto-generated method stub
    final FailureModel model = sim.getModelProvider().getModel(FailureModel.class);
    
    return model.getAmountOfFailures();
  }

}
