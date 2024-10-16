package swp.auctionkoi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import swp.auctionkoi.dto.request.koifish.KoiFishCreateRequest;
import swp.auctionkoi.dto.request.koifish.KoiFishUpdateRequest;
import swp.auctionkoi.dto.respone.koifish.KoiFishResponse;
import swp.auctionkoi.models.KoiFish;

@Mapper(componentModel = "spring")
public interface KoiFishMapper {
    KoiFishResponse toKoiFishResponse(KoiFish fish);

    KoiFish toKoiFish(KoiFishCreateRequest request);

    void updateKoiFish(@MappingTarget KoiFish koiFish, KoiFishUpdateRequest request);
}
