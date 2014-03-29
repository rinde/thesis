package models;

import rinde.logistics.pdptw.mas.comm.AuctionCommModel;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.util.SupplierRng;
import rinde.sim.util.SupplierRng.DefaultSupplierRng;

public class ReAuctionCommModel extends AuctionCommModel {

  public ReAuctionCommModel(long seed) {
    super(seed);
    // TODO Auto-generated constructor stub
  }
  public void reauction(DefaultParcel p, long time) {
    super.receiveParcel(p, time);
  }
  public static SupplierRng<AuctionCommModel> supplier() {
    return new DefaultSupplierRng<AuctionCommModel>() {
      @Override
      public AuctionCommModel get(long seed) {
        return new ReAuctionCommModel(seed);
      }
    };
  }


}