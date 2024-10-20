package swp.auctionkoi.mapper;

import org.mapstruct.Mapper;
import swp.auctionkoi.dto.request.AuctionRequestResponseData;
import swp.auctionkoi.models.AuctionRequest;

@Mapper(componentModel = "spring")
public interface AuctionRequestMapper {
    AuctionRequestResponseData toAuctionRequestResponseData(AuctionRequest auctionRequest);
}
