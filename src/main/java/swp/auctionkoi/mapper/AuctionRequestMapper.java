package swp.auctionkoi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import swp.auctionkoi.dto.request.AuctionRequestDTO;
import swp.auctionkoi.dto.request.AuctionRequestResponseData;
import swp.auctionkoi.models.AuctionRequest;
import swp.auctionkoi.models.KoiFish;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AuctionRequestMapper {
    AuctionRequestResponseData toAuctionRequestResponseData(AuctionRequest auctionRequest);
    AuctionRequest toAuctionRequest(AuctionRequestDTO auctionRequestDTO);
}
