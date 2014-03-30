package models;

import java.util.Set;

import rinde.logistics.pdptw.mas.comm.AuctionCommModel;
import rinde.logistics.pdptw.mas.comm.Communicator;
import rinde.sim.core.model.pdp.Parcel;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.util.SupplierRng;
import rinde.sim.util.SupplierRng.DefaultSupplierRng;
import failures.FallibleBidder;

public class ReAuctionCommModel extends AuctionCommModel {
  private boolean init=false;
  public ReAuctionCommModel(long seed) {
    super(seed);
    // TODO Auto-generated constructor stub
  }

  public static SupplierRng<AuctionCommModel> supplier() {
    return new DefaultSupplierRng<AuctionCommModel>() {
      @Override
      public AuctionCommModel get(long seed) {
        return new ReAuctionCommModel(seed);
      }
    };
  }
  public void initialise(){
    for(Communicator comm:communicators){
      if(comm instanceof FallibleBidder){
        FallibleBidder bidder = (FallibleBidder) comm;
        bidder.setAuctionModel(this);
      }
    }
  }
  public void receiveParcel(DefaultParcel p, long time){
    super.receiveParcel(p, time);
    if(!init){
      init=true;
      initialise();
    }
  }
  public void reauction( Set<DefaultParcel> parcels, long time){
    for(DefaultParcel p:parcels){
      if(p!=null){
        this.receiveParcel(p, time);
      }
    }
  }


}