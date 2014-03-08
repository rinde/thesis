package experiment;

import static com.google.common.base.Preconditions.checkArgument;
import heuristics.FailureObjectiveFunction;
import heuristics.FreeTimeObjectiveFunction;
import heuristics.WorkloadObjectiveFunction;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import rinde.logistics.pdptw.mas.TruckConfiguration;
import rinde.logistics.pdptw.mas.comm.AuctionCommModel;
import rinde.logistics.pdptw.mas.comm.Communicator;
import rinde.logistics.pdptw.mas.comm.NegotiatingBidder;
import rinde.logistics.pdptw.mas.comm.NegotiatingBidder.SelectNegotiatorsHeuristic;
import rinde.logistics.pdptw.mas.comm.RandomBidder;
import rinde.logistics.pdptw.mas.route.RandomRoutePlanner;
import rinde.logistics.pdptw.mas.route.RoutePlanner;
import rinde.logistics.pdptw.mas.route.SolverRoutePlanner;
import rinde.logistics.pdptw.solver.CheapestInsertionHeuristic;
import rinde.logistics.pdptw.solver.MultiVehicleHeuristicSolver;
import rinde.sim.core.model.Model;
import rinde.sim.pdptw.common.DynamicPDPTWScenario;
import rinde.sim.pdptw.common.DynamicPDPTWScenario.ProblemClass;
import rinde.sim.pdptw.experiment.Experiment;
import rinde.sim.pdptw.experiment.Experiment.ExperimentResults;
import rinde.sim.pdptw.experiment.Experiment.SimulationResult;
import rinde.sim.pdptw.experiment.MASConfiguration;
import rinde.sim.pdptw.gendreau06.Gendreau06ObjectiveFunction;
import rinde.sim.pdptw.gendreau06.Gendreau06Parser;
import rinde.sim.pdptw.gendreau06.Gendreau06Scenario;
import rinde.sim.pdptw.gendreau06.GendreauProblemClass;
import rinde.sim.util.SupplierRng;
import analyse.Analyser;
import central.Central;

import com.google.common.base.Charsets;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.io.Files;

import failures.DefaultFailureModel;
import failures.FailureNegotiatingBidder;
import failures.FailureSolverBidder;

public class FailureExperiment {

  public FailureExperiment() {
    // TODO Auto-generated constructor stub
  }

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    //		gendreau();
    //    		failureExperiment_Random(false);
    //    		failureExperiment_Random(true);
//        		workLoadInsertionExperiment(true);
//        		freeTimeInsertionExperiment(true);
//            auctionExperiment(true);
//    freeTime_negotiatingExperiment(true);
//    workload_negotiatingExperiment(true);
//        negotiatingExperiment(false);
//        		centralExperiment(false);
//        combinedHeuristicsInsertionExperiment(true);

  }


  public static void failureExperiment_Random(boolean failuresEnabled){
    List<DynamicPDPTWScenario> failureScenarios = createFailureScenarios();
    Gendreau06ObjectiveFunction objectiveFunction = new Gendreau06ObjectiveFunction();
    SupplierRng<? extends RoutePlanner> routePlannerSupplier=RandomRoutePlanner.supplier();
    SupplierRng<? extends Communicator> communicatorSupplier= RandomBidder.supplier() ;

    ImmutableList<? extends SupplierRng<? extends Model<?>>> modelSuppliers  = ImmutableList.of(DefaultFailureModel.supplier(),AuctionCommModel.supplier());
    MASConfiguration config;
    if(failuresEnabled){
      config = new FailureTruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
    }
    else{

      config = new TruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
    }
    performExperiment(failureScenarios, objectiveFunction, config, 150, "random.txt");
  }

  private static List<DynamicPDPTWScenario> createFailureScenarios() {
    final List<Gendreau06Scenario> onlineScenarios = Gendreau06Parser.parser()
        .addDirectory("scenarios/").allowDiversion()
        .filter(GendreauProblemClass.SHORT_LOW_FREQ).parse();
    List<DynamicPDPTWScenario> failureScenarios = new LinkedList<DynamicPDPTWScenario>();

    for(DynamicPDPTWScenario scenario: onlineScenarios){
      long ts=scenario.getTickSize();
      GendreauProblemClass problemClass=(GendreauProblemClass) scenario.getProblemClass();
      int instanceNumber=Integer.valueOf(scenario.getProblemInstanceId());

      FailureScenario failureScenario = new FailureScenario(scenario.asList(), scenario.getPossibleEventTypes(), ts, problemClass, instanceNumber);
      failureScenarios.add(failureScenario);
    }
    return failureScenarios;
  }
  public static void negotiatingExperiment(boolean failuresEnabled){
    final List<Gendreau06Scenario> offlineScenarios = createScenarios();
    List<DynamicPDPTWScenario> failureScenarios = createFailureScenarios();
    Gendreau06ObjectiveFunction objectiveFunction = new Gendreau06ObjectiveFunction();

    SupplierRng<? extends RoutePlanner> routePlannerSupplier=SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(200, 50000));

    SupplierRng<? extends Communicator> communicatorSupplier= NegotiatingBidder.supplier(objectiveFunction, MultiVehicleHeuristicSolver.supplier(20,10000), MultiVehicleHeuristicSolver.supplier(200, 50000), 2, SelectNegotiatorsHeuristic.FIRST_DESTINATION_POSITION) ;


    ImmutableList<? extends SupplierRng<? extends Model<?>>> modelSuppliers  = ImmutableList.of(DefaultFailureModel.supplier(),AuctionCommModel.supplier());
    MASConfiguration config;
    if(failuresEnabled){
      config = new FailureTruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
      performExperiment(failureScenarios, objectiveFunction, config, 150, "negotiating.txt");
    }
    else{
      config = new TruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
      performExperiment(offlineScenarios, objectiveFunction, config, 30, "negotiating.txt");
    }
  }
  public static void freeTime_negotiatingExperiment(boolean failuresEnabled){
    final List<Gendreau06Scenario> offlineScenarios = createScenarios();
    List<DynamicPDPTWScenario> failureScenarios = createFailureScenarios();
    Gendreau06ObjectiveFunction objectiveFunction = new Gendreau06ObjectiveFunction();

    SupplierRng<? extends RoutePlanner> routePlannerSupplier=SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(200, 50000));
    Gendreau06ObjectiveFunction heuristicObjectiveFunction = new FreeTimeObjectiveFunction();

    SupplierRng<? extends Communicator> communicatorSupplier= FailureNegotiatingBidder.supplier(heuristicObjectiveFunction, MultiVehicleHeuristicSolver.supplier(20,10000), MultiVehicleHeuristicSolver.supplier(200, 50000), 2, SelectNegotiatorsHeuristic.FIRST_DESTINATION_POSITION) ;

    ImmutableList<? extends SupplierRng<? extends Model<?>>> modelSuppliers  = ImmutableList.of(DefaultFailureModel.supplier(),AuctionCommModel.supplier());
    MASConfiguration config;
    if(failuresEnabled){
      config = new FailureTruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
      performExperiment(failureScenarios, objectiveFunction, config, 150, "freetime_negotiating.txt");
    }
    else{
      config = new TruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
      performExperiment(offlineScenarios, objectiveFunction, config, 30, "x.txt");
    }
  }
  public static void workload_negotiatingExperiment(boolean failuresEnabled){
    final List<Gendreau06Scenario> offlineScenarios = createScenarios();
    List<DynamicPDPTWScenario> failureScenarios = createFailureScenarios();
    Gendreau06ObjectiveFunction objectiveFunction = new Gendreau06ObjectiveFunction();

    SupplierRng<? extends RoutePlanner> routePlannerSupplier=SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(200, 50000));
    Gendreau06ObjectiveFunction heuristicObjectiveFunction = new WorkloadObjectiveFunction();

    SupplierRng<? extends Communicator> communicatorSupplier= FailureNegotiatingBidder.supplier(heuristicObjectiveFunction, MultiVehicleHeuristicSolver.supplier(20,10000), MultiVehicleHeuristicSolver.supplier(200, 50000), 2, SelectNegotiatorsHeuristic.FIRST_DESTINATION_POSITION) ;

    ImmutableList<? extends SupplierRng<? extends Model<?>>> modelSuppliers  = ImmutableList.of(DefaultFailureModel.supplier(),AuctionCommModel.supplier());
    MASConfiguration config;
    if(failuresEnabled){
      config = new FailureTruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
      performExperiment(failureScenarios, objectiveFunction, config, 100, "workload_negotiating.txt");
    }
    else{
      config = new TruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
      performExperiment(offlineScenarios, objectiveFunction, config, 30, "x.txt");
    }
  }
  public static void auctionExperiment(boolean failuresEnabled){
    final List<Gendreau06Scenario> offlineScenarios = createScenarios();
    List<DynamicPDPTWScenario> failureScenarios = createFailureScenarios();
    Gendreau06ObjectiveFunction objectiveFunction = new Gendreau06ObjectiveFunction();

    SupplierRng<? extends RoutePlanner> routePlannerSupplier=SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(60, 200));
    SupplierRng<? extends Communicator> communicatorSupplier= FailureSolverBidder.supplier(objectiveFunction,
        CheapestInsertionHeuristic
        .supplier(objectiveFunction)); 
    SupplierRng<DefaultFailureModel> failureModel = DefaultFailureModel.supplier();
    ImmutableList<? extends SupplierRng<? extends Model<?>>> modelSuppliers  = ImmutableList.of(failureModel,AuctionCommModel.supplier());
    MASConfiguration config;
    if(failuresEnabled){
      config = new FailureTruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
      performExperiment(failureScenarios, objectiveFunction, config, 150, "insertion.txt");

    }
    else{
      config = new TruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
      performExperiment(offlineScenarios, objectiveFunction, config, 30, "x.txt");

    }
  }
  
  public static void workLoadInsertionExperiment(boolean failuresEnabled){
    final List<Gendreau06Scenario> offlineScenarios = createScenarios();
    List<DynamicPDPTWScenario> failureScenarios = createFailureScenarios();
    Gendreau06ObjectiveFunction objectiveFunction = new Gendreau06ObjectiveFunction();
    Gendreau06ObjectiveFunction heuristicObjectiveFunction = new WorkloadObjectiveFunction();

    SupplierRng<? extends RoutePlanner> routePlannerSupplier=SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(60, 200));
    SupplierRng<? extends Communicator> communicatorSupplier= FailureSolverBidder.supplier(heuristicObjectiveFunction,
        CheapestInsertionHeuristic
        .supplier(heuristicObjectiveFunction)); 
    SupplierRng<DefaultFailureModel> failureModel = DefaultFailureModel.supplier();
    ImmutableList<? extends SupplierRng<? extends Model<?>>> modelSuppliers  = ImmutableList.of(failureModel,AuctionCommModel.supplier());
    MASConfiguration config;
    if(failuresEnabled){
      config = new FailureTruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
      performExperiment(failureScenarios, objectiveFunction, config, 150, "workload_insertion.txt");

    }
    else{
      config = new TruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
      performExperiment(offlineScenarios, objectiveFunction, config, 150, "x.txt");

    }
  }
  public static void combinedHeuristicsInsertionExperiment(boolean failuresEnabled){
    final List<Gendreau06Scenario> offlineScenarios = createScenarios();
    List<DynamicPDPTWScenario> failureScenarios = createFailureScenarios();
    Gendreau06ObjectiveFunction objectiveFunction = new Gendreau06ObjectiveFunction();
    Gendreau06ObjectiveFunction heuristicObjectiveFunction = new FailureObjectiveFunction();

    SupplierRng<? extends RoutePlanner> routePlannerSupplier=SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(60, 200));
    SupplierRng<? extends Communicator> communicatorSupplier= FailureSolverBidder.supplier(heuristicObjectiveFunction,
        CheapestInsertionHeuristic
        .supplier(heuristicObjectiveFunction));
    SupplierRng<DefaultFailureModel> failureModel = DefaultFailureModel.supplier();
    ImmutableList<? extends SupplierRng<? extends Model<?>>> modelSuppliers  = ImmutableList.of(failureModel,AuctionCommModel.supplier());
    MASConfiguration config;
    if(failuresEnabled){
      config = new FailureTruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
      performExperiment(failureScenarios, objectiveFunction, config, 150, "combined_insertion.txt");

    }
    else{
      config = new TruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
      performExperiment(offlineScenarios, objectiveFunction, config, 30, "x.txt");

    }
  }

  private static List<Gendreau06Scenario> createScenarios() {
    return Gendreau06Parser.parser()
        .addDirectory("scenarios/").allowDiversion()
        .filter(GendreauProblemClass.SHORT_LOW_FREQ).parse();
  }
  public static void freeTimeInsertionExperiment(boolean failuresEnabled){
    final List<Gendreau06Scenario> onlineScenarios = Gendreau06Parser.parser()
        .addDirectory("scenarios/").allowDiversion()
        .filter(GendreauProblemClass.SHORT_LOW_FREQ).parse();
    List<DynamicPDPTWScenario> failureScenarios = createFailureScenarios();
    Gendreau06ObjectiveFunction objectiveFunction = new Gendreau06ObjectiveFunction();
    Gendreau06ObjectiveFunction heuristicObjectiveFunction = new FreeTimeObjectiveFunction();

    SupplierRng<? extends RoutePlanner> routePlannerSupplier=SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(60, 200));

    SupplierRng<? extends Communicator> communicatorSupplier= FailureSolverBidder.supplier(heuristicObjectiveFunction,
        CheapestInsertionHeuristic
        .supplier(heuristicObjectiveFunction)); 
    SupplierRng<DefaultFailureModel> failureModel = DefaultFailureModel.supplier();
    ImmutableList<? extends SupplierRng<? extends Model<?>>> modelSuppliers  = ImmutableList.of(failureModel,AuctionCommModel.supplier());
    MASConfiguration config;
    if(failuresEnabled){
      config = new FailureTruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
      performExperiment(failureScenarios, objectiveFunction, config, 150, "freetimeinsertion.txt");

    }
    else{
      config = new TruckConfiguration(routePlannerSupplier, communicatorSupplier, modelSuppliers);
      performExperiment(onlineScenarios, objectiveFunction, config, 150, "x.txt");

    }
  }
  public static void centralExperiment(boolean failuresEnabled){
    List<DynamicPDPTWScenario> failureScenarios = createFailureScenarios();
    final List<Gendreau06Scenario> offlineScenarios = createScenarios();
    Gendreau06ObjectiveFunction objectiveFunction = new Gendreau06ObjectiveFunction();

//    ImmutableList<? extends SupplierRng<? extends Model<?>>> modelSuppliers  = ImmutableList.of(DefaultFailureModel.supplier());
    MASConfiguration config=Central.solverConfiguration(MultiVehicleHeuristicSolver.supplier(60, 200), "-Online");
    if(failuresEnabled){
      performExperiment(failureScenarios, objectiveFunction, config, 150, "central.txt");

    }
    else
      performExperiment(offlineScenarios, objectiveFunction, config, 30, "x.txt");

  }

  private static void performExperiment(
      List<? extends DynamicPDPTWScenario> onlineScenarios,
      Gendreau06ObjectiveFunction objectiveFunction,
      MASConfiguration config, int runs, String fileName) {
    final ExperimentResults offlineResults = Experiment
        .build(objectiveFunction)
        .addScenarios(onlineScenarios)
        .addConfiguration(config)				
        .withRandomSeed(320)
        .repeat(runs).usePostProcessor(new FailurePostProcessor())
        .withThreads(1)
        //        .showGui()
        .perform();
    writeGendreauResults(offlineResults);
    new Analyser(fileName);
  }
  static void writeGendreauResults(ExperimentResults results) {

    final Table<MASConfiguration, ProblemClass, StringBuilder> table = HashBasedTable
        .create();

    checkArgument(results.objectiveFunction instanceof Gendreau06ObjectiveFunction);
    final Gendreau06ObjectiveFunction obj = (Gendreau06ObjectiveFunction) results.objectiveFunction;
    double costSum=0;
    double[] costValues = new double[results.results.size()];
    ProblemClass probclass = null;
    MASConfiguration masconfig = null;
    int i =0;
    for (final SimulationResult r : results.results) {
      Integer amountOfFailures = (Integer) r.simulationData;

      final MASConfiguration config = r.masConfiguration;
      final ProblemClass pc = r.scenario.getProblemClass();
      probclass= pc;
      masconfig = config;

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
      double computeCost = obj.computeCost(r.stats);
      costSum+=computeCost;
      costValues[i]=computeCost;
      sb.append(r.seed).append(",")
      /* instance */
      .append(r.scenario.getProblemInstanceId()).append(",")
      /* duration */
      .append(gpc.duration).append(",")
      /* frequency */
      .append(gpc.frequency).append(",")
      /* cost */
      .append(computeCost).append(',')
      /* tardiness */
      .append(obj.tardiness(r.stats)).append(',')
      /* travelTime */
      .append(obj.travelTime(r.stats)).append(',')
      /* overTime */
      .append(obj.overTime(r.stats)).append(',')
      /* computation time */
      .append(r.stats.computationTime).append(',').append(amountOfFailures).append("\n");
      i++;
    }
    double mean = costSum / results.results.size();
    StandardDeviation standardDeviation= new StandardDeviation();
    double std = standardDeviation.evaluate(costValues);
    //    System.out.println(results.results.size());
    final StringBuilder sb = table.get(masconfig, probclass);
    sb.append(mean);
    sb.append("std=");
    sb.append(std);



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
        final File result = new File("result.txt");
        if(result.exists()){
          result.delete();
        }
        Files.write(cell.getValue().toString(), file, Charsets.UTF_8);
        Files.write(cell.getValue().toString(), result,Charsets.UTF_8);
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

}
