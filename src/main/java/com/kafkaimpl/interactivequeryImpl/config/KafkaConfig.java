package com.kafkaimpl.interactivequeryImpl.config;

import com.kafkaimpl.interactivequeryImpl.listeners.StateStoreListener;
import com.kafkaimpl.interactivequeryImpl.model.Stocks;
import com.kafkaimpl.interactivequeryImpl.serdes.StocksSerdes;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.rmi.ServerError;
import java.util.Properties;
import java.util.stream.Stream;

@Configuration
public class KafkaConfig {

  private static final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);

  @Value("${kafka-bootstrap-servers}")
  String bootstrapServers;

  @Value("${state-store-directory}")
  String stateStoreDirectoryPath;

  @Value("${topic-partition-count}")
  String partitionCount;

  @Value("${storeName}")
  String storeName;

  @Value("${topicName}")
  String mytopic;

  private static String applicationId = "interactive-query-app1";

  @Autowired StateStoreListener stateStoreListener;

  @Bean(name = "kafkaStreamApp")
  public KafkaStreams streamApp() {
    Properties properties = new Properties();

    properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    properties.put(
        StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, StocksSerdes.class);
    properties.put(StreamsConfig.STATE_DIR_CONFIG, stateStoreDirectoryPath);
    properties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);

    /**
     * Consumer configs, Based on the message size tune these to fetch max messages per each poll
     * Utilize your network.
     */
    properties.put(
        StreamsConfig.consumerPrefix(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG), 5 * 60 * 1000);
    properties.put(
        StreamsConfig.consumerPrefix(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG), 60 * 1000);

    properties.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 5000);
    properties.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, partitionCount);
    properties.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 16 * 1024);

    /**
     * Producer configs: Change these settings based on the message payload size which are published
     * to the change log topic. If you hold more messages in the application which consumes the
     * native memory please provide some attention, other wise you will end up in OOM issues.
     */
    properties.put(
        StreamsConfig.producerPrefix(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG),
        StringSerializer.class);
    properties.put(
        StreamsConfig.producerPrefix(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG),
        JsonSerializer.class);

    /**
     * RocksDB config: Use this class to configure/Optimize the storage utilized by the Kafka
     * Streams RocksDB config what ever we have defined is not FINAL, it has to be tuned based on
     * your network, disk, memory. There is no foolproof config for this, you have to set the config
     * based on your infrastructure.
     */
    properties.put(StreamsConfig.ROCKSDB_CONFIG_SETTER_CLASS_CONFIG, RocksDBConfig.class);

    /**
     * Define the Application Server Config: This is utilised by the Kafka to manage the instances
     * of the same group If you deployed to different instances across the regions, or sites, for
     * each instance this should be unique. Kafka maintains the list of instances to track the
     * consumer threads. This parameter should be unique for the each instance.
     */
    properties.put(StreamsConfig.APPLICATION_SERVER_CONFIG, "localhost");

    final StreamsBuilder builder = new StreamsBuilder();
    KeyValueBytesStoreSupplier persistantKeyValueStore = Stores.persistentKeyValueStore(storeName);
    Materialized<String, Stocks, KeyValueStore<Bytes, byte[]>> materialized =
        Materialized.as(persistantKeyValueStore);

    builder.table(mytopic, Consumed.with(Serdes.String(), new StocksSerdes()), materialized);
    Topology topology = builder.build();
    final KafkaStreams streams = new KafkaStreams(topology, properties);
    streams.setGlobalStateRestoreListener(stateStoreListener);
    streams.start();
    return streams;
  }
}
