package failures;

import java.util.Set;

import models.ReAuctionCommModel;
import rinde.sim.pdptw.common.DefaultParcel;

public interface FallibleBidder {
  public void release();

  public void setAuctionModel(ReAuctionCommModel reAuctionCommModel);

  public void reauction(Set<DefaultParcel> nonLoadedParcels,long time);

}
