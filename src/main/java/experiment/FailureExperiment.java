package experiment;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import rinde.logistics.pdptw.mas.TruckConfiguration;
import rinde.logistics.pdptw.mas.comm.AuctionCommModel;
import rinde.logistics.pdptw.mas.comm.Communicator;
import rinde.logistics.pdptw.mas.comm.InsertionCostBidder;
import rinde.logistics.pdptw.mas.comm.RandomBidder;
import rinde.logistics.pdptw.mas.route.GotoClosestRoutePlanner;
import rinde.logistics.pdptw.mas.route.RandomRoutePlanner;
import rinde.logistics.pdptw.mas.route.RoutePlanner;
import rinde.sim.core.model.Model;
import rinde.sim.pdptw.common.DynamicPDPTWScenario;
import rinde.sim.pdptw.common.DynamicPDPTWScenario.ProblemClass;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.pdptw.experiment.Experiment;
import rinde.sim.pdptw.experiment.Experiment.ExperimentResults;
import rinde.sim.pdptw.experiment.Experiment.SimulationResult;
import rinde.sim.pdptw.experiment.MASConfiguration;
import rinde.sim.pdptw.gendreau06.Gendreau06ObjectiveFunction;
import rinde.sim.pdptw.gendreau06.Gendreau06Scenarios;
import rinde.sim.pdptw.gendreau06.GendreauProblemClass;
import rinde.sim.util.SupplierRng;

import com.google.common.base.Charsets;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.io.Files;

import failures.DefaultFailureModel;

public class FailureExperiment {

	public FailureExperiment() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		gendreau();
//		failureExperiment(false);
		auctionExperiment(false);
		
	}
	public static void gendreau(){
		 System.out.println("offline");
		    final Gendreau06Scenarios offlineScenarios = new Gendreau06Scenarios(
		        "scenarios/", false, GendreauProblemClass.values());
//		    List<DynamicPDPTWScenario> failureScenarios = new LinkedList<DynamicPDPTWScenario>();
//
//			for(DynamicPDPTWScenario scenario: offlineScenarios.provide()){
//				long ts=scenario.getTickSize();
//				GendreauProblemClass problemClass=(GendreauProblemClass) scenario.getProblemClass();
//				int instanceNumber=Integer.valueOf(scenario.getProblemInstanceId());
//
//				FailureScenario failureScenario = new FailureScenario(scenario.asList(), scenario.getPossibleEventTypes(), ts, problemClass, instanceNumber);
//				failureScenarios.add(failureScenario);
//			}
		    SupplierRng<? extends RoutePlanner> routePlannerSupplier=RandomRoutePlanner.supplier();
			SupplierRng<? extends Communicator> communicatorSupplier=RandomBidder.supplier();
			ImmutableList<? extends SupplierRng<? extends Model<?>>> modelSuppliers = ImmutableList.of(AuctionCommModel.supplier());
		    final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();

			final ExperimentResults offlineResults = Experiment
					 .build(new Gendreau06ObjectiveFunction())
				        .addScenarios(offlineScenarios.provide())
				        .withRandomSeed(321)
				        .repeat(2)
				        .withThreads(1)
				        .showGui()
				        .addConfiguration(
				        		new TruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers))

				        .perform();
//		    writeGendreauResults(offlineResults);
	}

	public static void failureExperiment(boolean failuresEnabled){
		List<DynamicPDPTWScenario> failureScenarios = createFailureScenarios();
		Gendreau06ObjectiveFunction objectiveFunction = new Gendreau06ObjectiveFunction();

//		SupplierRng<? extends RoutePlanner> routePlannerSupplier=SolverRoutePlanner
//				.supplier(MultiVehicleHeuristicSolver.supplier(200, 50000));
		SupplierRng<? extends RoutePlanner> routePlannerSupplier=RandomRoutePlanner.supplier();
//		SupplierRng<? extends Communicator> communicatorSupplier= InsertionCostBidder.supplier(objectiveFunction) ;
		SupplierRng<? extends Communicator> communicatorSupplier= RandomBidder.supplier() ;

		ImmutableList<? extends SupplierRng<? extends Model<?>>> modelSuppliers  = ImmutableList.of(DefaultFailureModel.supplier(),AuctionCommModel.supplier());
		MASConfiguration config;
		if(failuresEnabled){
			config = new FailureTruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
		}
		else
			config = new TruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
		performExperiment(failureScenarios, objectiveFunction, config);
	}

	private static List<DynamicPDPTWScenario> createFailureScenarios() {
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
		return failureScenarios;
	}
	public static void auctionExperiment(boolean failuresEnabled){
		List<DynamicPDPTWScenario> failureScenarios = createFailureScenarios();
		Gendreau06ObjectiveFunction objectiveFunction = new Gendreau06ObjectiveFunction();

//		SupplierRng<? extends RoutePlanner> routePlannerSupplier=SolverRoutePlanner
//				.supplier(MultiVehicleHeuristicSolver.supplier(200, 50000));
		SupplierRng<? extends RoutePlanner> routePlannerSupplier=GotoClosestRoutePlanner.supplier();
		SupplierRng<? extends Communicator> communicatorSupplier= InsertionCostBidder.supplier(objectiveFunction) ;
//		SupplierRng<? extends Communicator> communicatorSupplier= RandomBidder.supplier() ;

		ImmutableList<? extends SupplierRng<? extends Model<?>>> modelSuppliers  = ImmutableList.of(DefaultFailureModel.supplier(),AuctionCommModel.supplier());
		MASConfiguration config;
		if(failuresEnabled){
			config = new FailureTruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
		}
		else
			config = new TruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
		performExperiment(failureScenarios, objectiveFunction, config);
	}
	public static void centralExperiment(boolean failuresEnabled){
		
	}

	private static void performExperiment(
			List<DynamicPDPTWScenario> failureScenarios,
			Gendreau06ObjectiveFunction objectiveFunction,
			MASConfiguration config) {
		final ExperimentResults offlineResults = Experiment
				.build(objectiveFunction)
				.addScenarios(failureScenarios)
				.addConfiguration(config)				
				.withRandomSeed(321)
				.repeat(2)
				.withThreads(1)
				.showGui()
				.perform();
		writeGendreauResults(offlineResults);
	}
	static void writeGendreauResults(ExperimentResults results) {

	    final Table<MASConfiguration, ProblemClass, StringBuilder> table = HashBasedTable
	        .create();

	    checkArgument(results.objectiveFunction instanceof Gendreau06ObjectiveFunction);
	    final Gendreau06ObjectiveFunction obj = (Gendreau06ObjectiveFunction) results.objectiveFunction;

	    for (final SimulationResult r : results.results) {
	      final MASConfiguration config = r.masConfiguration;
	      final ProblemClass pc = r.scenario.getProblemClass();

	      if (!table.contains(config, pc)) {
	        table
	            .put(
	                config,
	                pc,
	                new StringBuilder(
	                    "seed,instance,duration,frequency,cost,tardiness,travelTime,overTime,computationTime\n"));
	      }
	      final StringBuilder sb = table.get(config, pc);

	      final GendreauProblemClass gpc = (GendreauProblemClass) pc;
	      /* seed */
	      sb.append(r.seed).append(",")
	      /* instance */
	      .append(r.scenario.getProblemInstanceId()).append(",")
	      /* duration */
	      .append(gpc.duration).append(",")
	      /* frequency */
	      .append(gpc.frequency).append(",")
	      /* cost */
	      .append(obj.computeCost(r.stats)).append(',')
	      /* tardiness */
	      .append(obj.tardiness(r.stats)).append(',')
	      /* travelTime */
	      .append(obj.travelTime(r.stats)).append(',')
	      /* overTime */
	      .append(obj.overTime(r.stats)).append(',')
	      /* computation time */
	      .append(r.stats.computationTime).append("\n");
	    }
	    final Set<Cell<MASConfiguration, ProblemClass, StringBuilder>> set = table
	    		.cellSet();
	    for (final Cell<MASConfiguration, ProblemClass, StringBuilder> cell : set) {
	    	try {
	    		final File dir = new File("files/results/gendreau"
	    				+ cell.getColumnKey().getId());
	    		if (!dir.exists() || !dir.isDirectory()) {
	    			Files.createParentDirs(dir);
	    			dir.mkdir();
	    		}
	    		final File file = new File(dir.getPath() + "/"
	    				+ cell.getRowKey().toString() + "_" + results.masterSeed
	    				+ cell.getColumnKey().getId() + ".txt");
	    		if (file.exists()) {
	    			file.delete();
	    		}

	    		Files.write(cell.getValue().toString(), file, Charsets.UTF_8);
	    	} catch (final IOException e) {
	    		throw new RuntimeException(e);
	    	}
	    }
	}

}
