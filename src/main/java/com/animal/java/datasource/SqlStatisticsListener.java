package com.animal.java.datasource;

import com.google.inject.internal.util.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.ExecuteContext;
import org.jooq.impl.DefaultExecuteListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SqlStatisticsListener extends DefaultExecuteListener {

    private final ThreadLocal<State> statePerThread = new ThreadLocal<>();

    @Autowired
    private JooqConfigSettingsProperties properties;

    public void startRecording(){
        if(properties.isLogStatistics()){
            State previous = statePerThread.get();
            statePerThread.set(new State(previous));
        }
    }

    public void stopRecordingAndLog(){
        try {
            if(properties.isLogStatistics()) {
                log.debug("Statistics:\n{}", state());
                statePerThread.set(state().previous);
            }
        } catch (RuntimeException | Error e){
            log.error("Exception during logging: ", e);
        }
    }

    @Override
    public void executeStart(ExecuteContext ctx){
        State state = statePerThread.get();
        if(state == null){
            return;
        }
        state.startTimeNs = System.nanoTime();
    }

    @Override
    public void executeEnd(ExecuteContext ctx){
        State state = statePerThread.get();
        if(state == null){
            return;
        }
        long time = System.nanoTime() - state.startTimeNs;
        String sql = ctx.sql() != null ? ctx.sql() :
                ctx.batchSQL().length != 0 && ctx.batchSQL()[0] != null ? ctx.batchSQL()[0] :
                        "<unknown>";

        Statement statement = state.statements.computeIfAbsent(sql, Statement::new);
        statement.count++;
        statement.totalBatchSize += ctx.batchSQL().length;
        statement.totalTimeNs += time;
    }

    @RequiredArgsConstructor
    private static class State {
        private final State previous;
        private long startTimeNs;
        private final Map<String, Statement> statements = new HashMap<>();

        @Override
        public String toString(){
            StringBuilder builder = new StringBuilder();
            builder.append(String.format("%-11s %-5s %-9s %-12s %s\n", "TotalTimeMs","Count","AvgTimeMs","AvgBatchSize","Sql"));
            statements.values().stream().sorted(Comparator.comparingLong(s -> s.totalTimeNs))
                    .forEach(st -> builder.append(String.format("%11.3f %5d %9.3f %12.0f %s\n", st.totalTimeNs(), st.count, st.avgTimeMs(), st.avgBatchSize()
                    , st.sql.replace('\r',' ').replace('\n', ' '))));
            return builder.toString();
        }
    }

    private State state(){
        State state = statePerThread.get();
        Preconditions.checkState(state != null);
        return state;
    }

    @RequiredArgsConstructor
    private static class Statement {
        private final String sql;
        private long count;
        private long totalTimeNs;
        private long totalBatchSize;

        private double totalTimeNs(){
            return nsToMs(totalTimeNs);
        }
        private double avgTimeMs(){
            return nsToMs(totalTimeNs * 1.0 ) / count;
        }
        private Double avgBatchSize(){return totalBatchSize * 1.0 / count; }
        private double nsToMs(double ns){return ns / 1_000_000.0;}
    }
}
