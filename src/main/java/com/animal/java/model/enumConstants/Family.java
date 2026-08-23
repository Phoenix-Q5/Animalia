package com.animal.java.model.enumConstants;

import com.animal.java.utils.EnumId;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Family implements EnumId {
    FELIDAE ("Cats"),
    CANIDAE ("Dogs, Wolves, Coyotes, African Wild Dogs"),
    URSIDAE ("Bears"),
    MUSTELIDAE ("Weasels, Badgers, Otters"),
    PROCYONIDAE ("Raccoons, Coatis, Olingos"),
    MEPHITIDAE ("Skunks, Stink Badgers"),
    HERPESTIDAE ("Mongooses"),
    HYAENIDAE ("Hyenas"),
    VIVERRIDAE ("Civets, Genets, and Linsangs"),
    OTARIIDAE ("Sea Lions, Fur Seals"),
    PHOCIDAE ("True Seals"),
    ODOBENIDAE ("Walrus"),
    LEPORIDAE ("Rabbits and Hares"),
    OCHOTONIDAE ("Pikas"),
    OTHER("Other / Not Yet Classified");

    private final String example;

    @JsonCreator
    public static Family parseId(String id){
        return EnumId.parseId(Family.class, id);
    }

    public static Family parseExamples(String example){
        for(Family f : values()){
            if(f.getExample().equalsIgnoreCase(example))
                return f;
        }
        return null;
    }
}
