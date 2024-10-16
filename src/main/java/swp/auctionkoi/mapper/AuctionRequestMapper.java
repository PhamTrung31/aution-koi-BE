package swp.auctionkoi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import swp.auctionkoi.dto.request.AuctionRequest;
import swp.auctionkoi.dto.request.AuctionRequestUpdate;
import swp.auctionkoi.dto.respone.AuctionRequestResponse;

@Mapper(componentModel = "spring")
public interface AuctionRequestMapper
{
    swp.auctionkoi.models.AuctionRequest toAuctionRequest(AuctionRequest auctionRequest);
    AuctionRequestResponse toAuctionRequestResponse(swp.auctionkoi.models.AuctionRequest auctionRequest);
    void updateAuctionRequest(@MappingTarget swp.auctionkoi.models.AuctionRequest auctionRequest, AuctionRequestUpdate auctionRequestDto);
}
