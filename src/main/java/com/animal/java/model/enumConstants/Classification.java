package com.animal.java.model.enumConstants;

import com.animal.java.utils.EnumId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Classification implements EnumId {

    CA("Carnivore"),
    HE("Herbivore"),
    OM("Omnivore"),
    FR("Frugivore"),
    GR("Granivore"),
    FO("Folivore"),
    IN("Insectivore"),
    PI("Piscivore"),
    MO("Molluscivore"),
    DE("Detritivore"),
    SA("Saprophage"),
    NE("Nectarivore"),
    PL("Planktivore"),
    XY("Xylophage"),
    CO("Coprophage"),
    HEA("Hematophage"),
    MY("Mycophage"),
    LI("Lignivore"),

    // Activity Patterns
    DI("Diurnal"),
    NO("Nocturnal"),
    CR("Crepuscular"),
    MA("Matutinal"),
    VE("Vespertine"),
    CAE("Cathemeral"),

    // Habitat & Lifestyle
    TE("Terrestrial"),
    AQ("Aquatic"),
    AR("Arboreal"),
    FOE("Fossorial"),
    MAE("Marine"),
    PE("Pelagic"),
    BE("Benthic"),
    RI("Riparian"),
    EP("Epiphytic"),
    SU("Subterranean");

    private final String description;
}
