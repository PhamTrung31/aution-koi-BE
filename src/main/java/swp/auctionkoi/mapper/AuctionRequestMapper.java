package swp.auctionkoi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestResponseData;
import swp.auctionkoi.models.AuctionRequest;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AuctionRequestMapper {
    AuctionRequestResponseData toAuctionRequestResponseData(AuctionRequest auctionRequest);
    AuctionRequest toAuctionRequest(AuctionRequestDTO auctionRequestDTO);
}
