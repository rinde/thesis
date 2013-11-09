package ui;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import rinde.sim.core.TimeLapse;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.ModelProvider;
import rinde.sim.core.model.pdp.PDPModel;
import rinde.sim.core.model.road.RoadModel;
import rinde.sim.core.model.road.RoadUser;
import rinde.sim.ui.renderers.ModelRenderer;
import rinde.sim.ui.renderers.ViewPort;
import rinde.sim.ui.renderers.ViewRect;
import failures.FailureModel;
import failures.FallibleEntity;

public class FailureRenderer implements ModelRenderer {
	private Color activeTruckColor;
	private Color failingTruckColor;
	private boolean isInitialized = false;
	public FailureRenderer() {
	}
	public void initialize(GC gc){
		this.isInitialized=true;
		this.activeTruckColor=gc.getDevice().getSystemColor(SWT.COLOR_GREEN);
		this.failingTruckColor=gc.getDevice().getSystemColor(SWT.COLOR_RED);
	}
	private FailureModel failureModel;
	private RoadModel roadModel;
	public void registerModelProvider(ModelProvider mp) {
		this.failureModel=mp.getModel(FailureModel.class);
		this.roadModel=mp.getModel(RoadModel.class);
	}

	public void renderStatic(GC gc, ViewPort vp) {
		// TODO Auto-generated method stub
		
	}

	public void renderDynamic(GC gc, ViewPort vp, long time) {
		if(!isInitialized)
			initialize(gc);
		final int radius = 4;
		final int outerRadius = 10;
//		uiSchema.initialize(gc.getDevice());
//		gc.setBackground(uiSchema.getDefaultColor());

		final Map<RoadUser, Point> objects = roadModel.getObjectsAndPositions();
		synchronized (objects) {
			TimeLapse qdsf;
			for (final Entry<RoadUser, Point> entry : objects.entrySet()) {
				final Point p = entry.getValue();
				final Class<?> type = entry.getKey().getClass();
				final int x = vp.toCoordX(p.x) - radius;
				final int y = vp.toCoordY(p.y) - radius;
				FallibleEntity entity =null;
				if(entry.getKey() instanceof FallibleEntity)
					entity = (FallibleEntity) entry.getKey();
				if(failureModel.isFailing(time, entity)){
					gc.setBackground(failingTruckColor);
				}
				else
					gc.setBackground(activeTruckColor);

				gc.fillOval((int) (vp.origin.x + (p.x - vp.rect.min.x) * vp.scale)
						- radius, (int) (vp.origin.y + (p.y - vp.rect.min.y) * vp.scale)
						- radius, 2 * radius, 2 * radius);


			}
		}
	}

	public ViewRect getViewRect() {
		// TODO Auto-generated method stub
		return null;
	}

}
