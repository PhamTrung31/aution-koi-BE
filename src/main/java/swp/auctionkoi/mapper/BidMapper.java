package swp.auctionkoi.mapper;

import org.mapstruct.Mapper;
import swp.auctionkoi.dto.request.bid.BidRequest;
import swp.auctionkoi.models.Bid;

@Mapper(componentModel = "spring")
public interface BidMapper {
    Bid toBid(BidRequest bidRequest);
}
