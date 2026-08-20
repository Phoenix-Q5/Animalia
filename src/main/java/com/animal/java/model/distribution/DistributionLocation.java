package com.animal.java.model.distribution;

import com.animal.java.model.enumConstants.LocationType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DistributionLocation {

    private UUID id;
    private UUID distributionId;
    private LocationType locationType;
    private String value;
}
