package com.animal.java.model.enumConstants;

import com.animal.java.utils.EnumId;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AnimalCollections implements EnumId {
    RED_ANIAMLS ("Red Animals"),
    GLIDING_ANIMALS ("Gliding Animals"),
    BLUE_ANIMALS ("Blue Animals"),
    FLUFFY_ANIMALS ("Fluffy Animals"),
    GREEN_ANIMALS ("Green Animals"),
    COLORFUL_ANIMALS ("Colorful Animals"),
    SNOW_ANIMALS ("Snow White Animals"),
    COAL_ANIMALS ("Coal Black Animals"),
    BEST_HORNS  ("Best Horns"),
    FAST_RUNNERS ("Fast Runners"),
    SLOW_ANIMALS ("Slow Animals"),
    GIANT_ANIMALS ("Giant Animals"),
    BLACK_AND_WHITE_ANIMALS ("Black and White Animals"),
    DWARF_ANIMALS ("Dwarf Animals"),
    DANGEROUS_ANIMALS ("Dangerous Animals"),
    STATE_ANIMALS ("State Animals"),
    BIGEYED_ANIMALS ("Big Eyed Animals"),
    CHATTERBOX_ANIMALS ("Chatterbox Animals"),
    VICIOUS_ANIMALS ("Vicious Animals"),
    ARISTOCRATIC_ANIMALS ("Aristocrats"),
    SMART_ANIMALS ("Smart Animals"),
    CAMAFLAUGE_ANIMALS ("Camouflage Animals"),
    LONG_TAILED_ANIMALS ("Long Tailed Animals"),
    TRAVELERS ("Travelers"),
    LONG_LIVED_ANIMALS ("Long Lived Animals"),
    CREEPY_ANIMALS ("Creepy Animals"),
    SPOTTED_ANIMALS ("Spotted Animals"),
    LONG_NECKED_ANIMALS ("Long Necked Animals"),
    BRIGHT_ANIMALS ("Bright Animals");

    private final String description;

    @JsonCreator
    public static AnimalCollections parseId(String id){
        return EnumId.parseId(AnimalCollections.class, id);
    }

    public static AnimalCollections parseDescription(String description){
        for(AnimalCollections a: values()){
            if(a.getDescription().equalsIgnoreCase(description))
                return a;
        }
        return null;
    }
}
