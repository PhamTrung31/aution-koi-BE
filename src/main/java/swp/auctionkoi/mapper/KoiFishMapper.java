package swp.auctionkoi.mapper;

import org.mapstruct.Mapper;
import swp.auctionkoi.dto.respone.koifish.KoiFishResponse;
import swp.auctionkoi.models.KoiFishs;

@Mapper(componentModel = "spring")
public interface KoiFishMapper {
    KoiFishResponse toKoiFishResponse(KoiFishs fish);
}
