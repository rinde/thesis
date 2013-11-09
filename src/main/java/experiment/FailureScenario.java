package experiment;

import java.util.Collection;
import java.util.Set;

import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import rinde.sim.core.graph.Point;
import rinde.sim.core.model.pdp.DefaultPDPModel;
import rinde.sim.core.model.pdp.PDPModel;
import rinde.sim.core.model.pdp.twpolicy.TardyAllowedPolicy;
import rinde.sim.core.model.road.PlaneRoadModel;
import rinde.sim.core.model.road.RoadModel;
import rinde.sim.pdptw.common.DynamicPDPTWProblem.SimulationInfo;
import rinde.sim.pdptw.common.DynamicPDPTWProblem.StopCondition;
import rinde.sim.pdptw.common.DynamicPDPTWScenario;
import rinde.sim.pdptw.common.PDPRoadModel;
import rinde.sim.pdptw.gendreau06.GendreauProblemClass;
import rinde.sim.scenario.TimedEvent;
import rinde.sim.util.TimeWindow;
import rinde.sim.util.spec.Specification;
import rinde.sim.util.spec.Specification.ISpecification;
import failures.DefaultFailureModel;
import failures.FailureModel;
import failures.PDPFailureRoadModel;

public class FailureScenario extends DynamicPDPTWScenario{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6879658985765196364L;
	private static final Point MIN = new Point(0, 0);
	private static final Point MAX = new Point(5, 5);
	private static final Measure<Double, Velocity> MAX_SPEED = Measure.valueOf(
			30d, NonSI.KILOMETERS_PER_HOUR);

	private final long tickSize;
	private final GendreauProblemClass problemClass;
	private final int instanceNumber;

	public FailureScenario(Collection<? extends TimedEvent> pEvents,
			Set<Enum<?>> pSupportedTypes, long ts, GendreauProblemClass problemClass,
			int instanceNumber) {
		super(pEvents, pSupportedTypes);
		tickSize = ts;
		this.problemClass = problemClass;
		this.instanceNumber = instanceNumber;
	}
	@Override
	public RoadModel createRoadModel() {
		PlaneRoadModel roadModel = new PlaneRoadModel(MIN, MAX, 50);
		PDPFailureRoadModel failureRoadModel = new PDPFailureRoadModel(roadModel, true);
		return failureRoadModel;
	}

	@Override
	public PDPModel createPDPModel() {
		return new DefaultPDPModel(new TardyAllowedPolicy());
	}

	@Override
	public Unit<Duration> getTimeUnit() {
		return SI.MILLI(SI.SECOND);
	}

	@Override
	public Unit<Velocity> getSpeedUnit() {
		return NonSI.KILOMETERS_PER_HOUR;
	}

	@Override
	public Unit<Length> getDistanceUnit() {
		return SI.KILOMETER;
	}

	@Override
	public TimeWindow getTimeWindow() {
		return TimeWindow.ALWAYS;
	}

	@Override
	public long getTickSize() {
		return tickSize;
	}

	@Override
	public ISpecification<SimulationInfo> getStopCondition() {
		return Specification.of(StopCondition.VEHICLES_DONE_AND_BACK_AT_DEPOT)
				.and(StopCondition.TIME_OUT_EVENT).build();
	}


	@Override
	protected FailureScenario newInstance(
			Collection<? extends TimedEvent> events) {
		return new FailureScenario(events, getPossibleEventTypes(), tickSize,
				problemClass, instanceNumber);
	}

	@Override
	public ProblemClass getProblemClass() {
		return problemClass;
	}

	@Override
	public String getProblemInstanceId() {
		return "" + instanceNumber;
	}

}
