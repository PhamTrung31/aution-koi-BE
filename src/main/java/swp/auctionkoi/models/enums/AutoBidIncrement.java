package swp.auctionkoi.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AutoBidIncrement {
    TEN_PERCENT(0.10F),
    TWENTY_PERCENT(0.20F),
    FORTY_PERCENT(0.40F);

    private final float increment;
}
