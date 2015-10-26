package edu.tuberlin.senser.images.flink;

import edu.tuberlin.senser.images.flink.io.FlinkJMSStreamSink;
import edu.tuberlin.senser.images.flink.io.FlinkJMSStreamSource;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.helper.Time;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Flin kStream example.
 */
public class StreamExample implements Serializable {

    public static void startFlinkStream() throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env
                .addSource(new FlinkJMSStreamSource())
                .flatMap(new Splitter())
                .window(Time.of(5, TimeUnit.SECONDS)).every(Time.of(2, TimeUnit.SECONDS))
                .groupBy(0)
                .sum(1)
                .flatten()
                // Now forward the result to a JMS Queue
                .addSink(new FlinkJMSStreamSink("output"));

        env.execute("JMS Stream");

    }

    public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {
            for (String word : sentence.split(" ")) {
                out.collect(new Tuple2<>(word, 1));
            }
        }
    }


}
