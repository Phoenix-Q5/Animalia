package com.animal.java.utils.datetime;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.temporal.Temporal;
import java.util.Optional;

@NoArgsConstructor
@Setter
@Accessors(fluent = true)
public class DateTimeRange<T extends Temporal> {

    private T start;

    private T end;

    @NonNull
    public Optional<T> start(){
        return Optional.ofNullable(start);
    }

    @NonNull
    public Optional<T> end(){
        return Optional.ofNullable(end);
    }

    @Override
    public String toString(){
        ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        start().ifPresent(v -> sb.append("start", v));
        end().ifPresent(v -> sb.append("end", v));
        return sb.toString();
    }
}
