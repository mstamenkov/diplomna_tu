package com.example.backend.repository;

import com.example.backend.model.Execution;
import com.example.backend.serdes.ExecutionSerdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.example.backend.util.Constants.EXECUTIONS_TOPIC;
import static com.example.backend.util.Utils.buildStreamProps;
import static com.example.backend.util.Utils.waitStreamToBecomeReady;
import static org.apache.kafka.streams.state.QueryableStoreTypes.keyValueStore;

public class ExecutionsStore implements ILifecycleController, IStore<Execution> {
    private KafkaStreams executionStream;
    private final String EXECUTIONS_STORE = "executions-store";

    @Override
    public void init() {
        final Logger log = LoggerFactory.getLogger(CommandRepositoryImpl.class);
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Execution> executions = builder.table(EXECUTIONS_TOPIC, Materialized.<String, Execution, KeyValueStore<Bytes, byte[]>>as(EXECUTIONS_STORE)).toStream();
        executionStream = new KafkaStreams(builder.build(), buildStreamProps("kafka-executions", ExecutionSerdes.class));
        executionStream.start();
        waitStreamToBecomeReady(executionStream);
    }

    @Override
    public void destroy() {
        executionStream.close();
    }

    @Override
    public Optional<Execution> get(String key) {
        Execution execution = waitForStoreToBecomeReady(executionStream).get(key);
        if(execution != null){
            return Optional.of(execution);
        }
        return Optional.empty();
    }

    @Override
    public KeyValueIterator<String, Execution> getAll() {
        return waitForStoreToBecomeReady(executionStream).all();
    }

    public Stream<KeyValue<String, Execution>> getAllAsStream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(waitForStoreToBecomeReady(executionStream).all(), 0), false);
    }

    private ReadOnlyKeyValueStore<String, Execution> waitForStoreToBecomeReady(KafkaStreams streams) {
        ReadOnlyKeyValueStore<String, Execution> store = null;
        while (true) {
            try {
                store = streams.store(StoreQueryParameters.fromNameAndType(EXECUTIONS_STORE, keyValueStore()));
            } catch (InvalidStateStoreException ignored) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            break;
        }
        return store;
    }
}

