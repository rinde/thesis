package thesis;

import java.util.LinkedList;
import java.util.List;

import javax.measure.Measure;
import javax.measure.unit.SI;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import rinde.logistics.pdptw.mas.comm.Communicator;
import rinde.logistics.pdptw.mas.comm.InsertionCostBidder;
import rinde.logistics.pdptw.mas.route.RoutePlanner;
import rinde.logistics.pdptw.mas.route.SolverRoutePlanner;
import rinde.logistics.pdptw.solver.MultiVehicleHeuristicSolver;
import rinde.sim.core.Simulator;
import rinde.sim.core.TickListener;
import rinde.sim.core.TimeLapse;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.Model;
import rinde.sim.core.model.road.MovingRoadUser;
import rinde.sim.core.model.road.PlaneRoadModel;
import rinde.sim.core.model.road.RoadModel;
import rinde.sim.pdptw.common.DynamicPDPTWScenario;
import rinde.sim.pdptw.experiment.Experiment;
import rinde.sim.pdptw.experiment.Experiment.ExperimentResults;
import rinde.sim.pdptw.gendreau06.Gendreau06ObjectiveFunction;
import rinde.sim.pdptw.gendreau06.Gendreau06Scenarios;
import rinde.sim.pdptw.gendreau06.GendreauProblemClass;
import rinde.sim.ui.MenuItems;
import rinde.sim.ui.View;
import rinde.sim.ui.View.Builder;
import rinde.sim.ui.renderers.PlaneRoadModelRenderer;
import rinde.sim.ui.renderers.RoadUserRenderer;
import rinde.sim.util.SupplierRng;

import com.google.common.collect.ImmutableList;

import experiment.FailureScenario;
import experiment.FailureTruckConfiguration;
import failures.DefaultFailureModel;
import failures.FailureModel;
import failures.FallibleEntity;
import failures.PDPFailureRoadModel;

public class Test {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		testDummyWithFailures();
		experiment();
	}

	private static void testDummyWithFailures() {
		// initialize a random generator which we use throughout this
        // 'experiment'
	    final RandomGenerator rnd = new MersenneTwister(123);
	   
	    // initialize a new Simulator instance
	    final Simulator sim = new Simulator(rnd, Measure.valueOf(1000L,
	        SI.MILLI(SI.SECOND)));

        // register a PlaneRoadModel, a model which facilitates the moving of
        // RoadUsers on a plane. The plane is bounded by two corner points:
        // (0,0) and (10,10)
        PlaneRoadModel roadModel = new PlaneRoadModel(new Point(0, 0), new Point(10, 10), 50);
        PDPFailureRoadModel failureRoadModel = new PDPFailureRoadModel(roadModel,true);
		sim.register(failureRoadModel);
		
		DefaultFailureModel failureModel = new DefaultFailureModel(100l, 0.5,3,40000,2000);


        failureRoadModel.failureModel=failureModel;

        // configure the simulator, once configured we can no longer change the
        // configuration (i.e. add new models) but we can start adding objects
		sim.register(failureModel);
        sim.configure();

        // add a number of drivers on the road
        final int numDrivers = 20;
        for (int i = 0; i < numDrivers; i++) {
                // when an object is registered in the simulator it gets
                // automatically 'hooked up' with models that it's interested in. An
                // object declares to be interested in an model by implementing an
                // interface.
                Driver obj = new Driver(rnd);
                obj.initRoadUser(failureRoadModel, failureModel);

				sim.register(obj);
        }
//        failureModel.initializeFailures(100l);
        // initialize the gui. We use separate renderers for the road model and
        // for the drivers. By default the road model is rendererd as a square
        // (indicating its boundaries), and the drivers are rendererd as red
        // dots.
        Builder builder=  View.create(sim);
        builder.setAccelerators(MenuItems.AZERTY_ACCELERATORS);
        builder.with(new PlaneRoadModelRenderer(),new RoadUserRenderer());
        
        builder.show(); 
        // in case a GUI is not desired, the simulation can simply be run by
        // calling: sim.start();
	}
	
//	static void offlineExperiment() {
//	    System.out.println("offline");
//	    final Gendreau06Scenarios offlineScenarios = new Gendreau06Scenarios(
//	        "scenarios/", true, GendreauProblemClass.values());
//	    Gendreau06ObjectiveFunction objectiveFunction = new Gendreau06ObjectiveFunction();
//		final ExperimentResults offlineResults = Experiment
//	        .build(objectiveFunction)
//	        .addScenarioProvider(offlineScenarios)
//	        .addConfigurator(new TruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers))
//	        .withRandomSeed(321)
//	        .repeat(1)
//	        .withThreads(1)
//	        .showGui()
//	        .perform();
//	    
//	  }
	static void experiment(){
	    final Gendreau06Scenarios offlineScenarios = new Gendreau06Scenarios(
        "scenarios/", true, GendreauProblemClass.values());
	    List<DynamicPDPTWScenario> failureScenarios = new LinkedList<DynamicPDPTWScenario>();

		for(DynamicPDPTWScenario scenario: offlineScenarios.provide()){
		    long ts=scenario.getTickSize();
			GendreauProblemClass problemClass=(GendreauProblemClass) scenario.getProblemClass();
			int instanceNumber=Integer.valueOf(scenario.getProblemInstanceId());
			
	    	FailureScenario failureScenario = new FailureScenario(scenario.asList(), scenario.getPossibleEventTypes(), ts, problemClass, instanceNumber);
	    	failureScenarios.add(failureScenario);
	    }

//		SupplierRng<? extends RoutePlanner> routePlannerSupplier= GotoClosestRoutePlanner.supplier();
		
		Gendreau06ObjectiveFunction objectiveFunction = new Gendreau06ObjectiveFunction();

		SupplierRng<? extends RoutePlanner> routePlannerSupplier=SolverRoutePlanner
        .supplier(MultiVehicleHeuristicSolver.supplier(200, 50000));
		SupplierRng<? extends Communicator> communicatorSupplier= InsertionCostBidder.supplier(objectiveFunction) ;
		ImmutableList<? extends SupplierRng<? extends Model<?>>> modelSuppliers  = ImmutableList.of(DefaultFailureModel.supplier());
		

		//		final FailureScenario failureScenario = new FailureScenario(pEvents, pSupportedTypes, ts, problemClass, instanceNumber);
		final ExperimentResults offlineResults = Experiment
				.build(objectiveFunction)
				.addScenarios(failureScenarios)
				.addConfiguration(new FailureTruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers))
				.withRandomSeed(321)
				.repeat(1)
				.withThreads(1)
				.showGui()
				.perform();
	
	}
	
	
	 static class Driver implements MovingRoadUser, TickListener, FallibleEntity {
         // the MovingRoadUser interface indicates that this class can move on a
         // RoadModel. The TickListener interface indicates that this class wants
         // to keep track of time.
         protected RoadModel roadModel;
         protected FailureModel failureModel; 
         protected final RandomGenerator rnd;

         public Driver(RandomGenerator r) {
                 // we store the reference to the random generator
                 rnd = r;
         }

         public void initRoadUser(RoadModel model, FailureModel failureModel) {
                 // this is where we receive an instance to the model. we store the
                 // reference and add ourselves to the model on a random position.
                 roadModel = model;
                 roadModel.addObjectAt(this, roadModel.getRandomPosition(rnd));
                 this.failureModel=failureModel;
                 failureModel.register(this);
         }
         private Point currentDestination;
         public void tick(TimeLapse timeLapse) {
                 // every time step (tick) this gets called. Each time we chose a
                 // different destination and move in that direction using the time
                 // that was made available to us.

        	 if(currentDestination == null || roadModel.getPosition(this).equals(currentDestination)){
        		 currentDestination = roadModel.getRandomPosition(rnd);
        	 }
        	 if(!failureModel.isFailing(timeLapse, this))
                 roadModel.moveTo(this, this.currentDestination, timeLapse);
         }
         

         public void afterTick(TimeLapse timeLapse) {
                 // we don't need this in this example. This method is called after
                 // all TickListener#tick() calls, hence the name.
         }

         public double getSpeed() {
                 // the drivers speed
                 return 50d;
         }

		public void notifyOfFailure() {
			// TODO Auto-generated method stub
		}

		public void initRoadUser(RoadModel model) {
			// TODO Auto-generated method stub
//            roadModel = model;
//            roadModel.addObjectAt(this, roadModel.getRandomPosition(rnd));
		}

		public void setFailureModel(FailureModel model) {
			// TODO Auto-generated method stub
			
		}

 }

}
