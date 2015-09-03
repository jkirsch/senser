package edu.tuberlin.senser.images.flink;

import edu.tuberlin.senser.images.flink.io.FlinkJMSStreamSink;
import edu.tuberlin.senser.images.flink.io.FlinkJMSStreamSource;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.WindowMapFunction;
import org.apache.flink.streaming.api.windowing.StreamWindow;
import org.apache.flink.streaming.api.windowing.helper.Time;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 */
public class StreamExample implements Serializable {

    public static void startFlinkStream() throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<StreamWindow<Tuple2<String, Integer>>> dataStream = env
                .addSource(new FlinkJMSStreamSource())
                .window(Time.of(5, TimeUnit.SECONDS)).every(Time.of(2, TimeUnit.SECONDS))
                .mapWindow(new WindowMapFunction<String, Tuple2<String, Integer>>() {
                    @Override
                    public void mapWindow(Iterable<String> iterable, final Collector<Tuple2<String, Integer>> out) throws Exception {
                        iterable.forEach(new Consumer<String>() {
                            @Override
                            public void accept(String sentence) {
                                for (String word : sentence.split(" ")) {
                                    out.collect(new Tuple2<String, Integer>(word, 1));
                                }
                            }
                        });
                    }
                })
                .groupBy(0)
                .sum(1)
                .getDiscretizedStream()
                // Now forward the result to a JMS Queue
                .addSink(new FlinkJMSStreamSink("output"));

        env.execute("JMS Stream");

    }

    public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {
            for (String word : sentence.split(" ")) {
                out.collect(new Tuple2<String, Integer>(word, 1));
            }
        }
    }


}
