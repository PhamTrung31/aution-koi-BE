package swp.auctionkoi.mapper;

import org.mapstruct.Mapper;
import swp.auctionkoi.dto.respone.AuctionResponse;
import swp.auctionkoi.models.Auction;
import swp.auctionkoi.models.AuctionRequest;

@Mapper
public interface AuctionMapper {
    Auction toAuction(swp.auctionkoi.dto.request.Auction auction);
    AuctionResponse toAuctionResponse(Auction auction);
//    void updateAuction(@MappingTarget AuctionMapper auction, )
}
