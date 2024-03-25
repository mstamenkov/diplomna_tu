package com.example.backend.repository;

import com.example.backend.model.Command;
import com.example.backend.serdes.CommandSerdes;
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

import static com.example.backend.util.Constants.COMMANDS_TOPIC;
import static com.example.backend.util.Utils.buildStreamProps;
import static com.example.backend.util.Utils.waitStreamToBecomeReady;
import static org.apache.kafka.streams.state.QueryableStoreTypes.keyValueStore;

public class CommandsStore implements ILifecycleController, IStore<Command> {
    private KafkaStreams commandStream;
    private final String COMMAND_STORE = "commands-store";

    @Override
    public void init() {
        final Logger log = LoggerFactory.getLogger(CommandRepositoryImpl.class);
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Command> commands = builder.table(COMMANDS_TOPIC, Materialized.<String, Command, KeyValueStore<Bytes, byte[]>>as(COMMAND_STORE)).toStream();
        commandStream = new KafkaStreams(builder.build(), buildStreamProps("kafka-commands", CommandSerdes.class));
        commandStream.start();
        waitStreamToBecomeReady(commandStream);
    }

    @Override
    public void destroy() {
        commandStream.close();
    }

    @Override
    public Optional<Command> get(String key) {
        Command command = waitForStoreToBecomeReady(commandStream).get(key);
        if (command != null) {
            return Optional.of(command);
        }
        return Optional.empty();
    }

    @Override
    public KeyValueIterator<String, Command> getAll() {
        return waitForStoreToBecomeReady(commandStream).all();
    }

    public Stream<KeyValue<String, Command>> getAllAsStream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(waitForStoreToBecomeReady(commandStream).all(), 0), false);
    }

    public long size() {
        return waitForStoreToBecomeReady(commandStream).approximateNumEntries();
    }

    private ReadOnlyKeyValueStore<String, Command> waitForStoreToBecomeReady(KafkaStreams streams) {
        ReadOnlyKeyValueStore<String, Command> store = null;
        while (true) {
            try {
                store = streams.store(StoreQueryParameters.fromNameAndType(COMMAND_STORE, keyValueStore()));
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
