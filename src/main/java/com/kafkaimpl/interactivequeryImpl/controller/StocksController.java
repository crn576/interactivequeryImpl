package com.kafkaimpl.interactivequeryImpl.controller;

import com.kafkaimpl.interactivequeryImpl.model.Stocks;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StocksController {

  private static final Logger logger = LoggerFactory.getLogger(StocksController.class);

  @Value("${storeName}")
  String storeName;

  @Autowired KafkaStreams kafkaStreams;

  String currentHostName = "localhost";

  @RequestMapping(
      value = "/query/{stockSymbol}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Stocks> getStockDetails(@PathVariable("stockSymbol") String stockSymbol) {
    ResponseEntity<Stocks> responseEntity = null;
    Stocks stocks = null;
    try {
      /** Check the stream status, Its should be runing state. */
      if (kafkaStreams.state().name().equalsIgnoreCase(KafkaStreams.State.RUNNING.toString())) {
        /**
         * Get the key metadata which gives us where the actual data is located, this matters when
         * we deployed the same app on different instances.
         */
        KeyQueryMetadata keyQueryMetadata =
            kafkaStreams.queryMetadataForKey(storeName, stockSymbol, Serdes.String().serializer());

        /**
         * Check whether the current host is holding the data or not. If the data is on other host
         * then redirect to that instance.
         */
        if (keyQueryMetadata.getActiveHost().host().equalsIgnoreCase(currentHostName)) {
          StoreQueryParameters<ReadOnlyKeyValueStore<String, Stocks>> storeStoreQueryParameters =
              StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.keyValueStore());
          stocks = kafkaStreams.store(storeStoreQueryParameters).get(stockSymbol);
          if (stocks != null) {
            responseEntity = new ResponseEntity<>(stocks, HttpStatus.OK);
          } else {
            responseEntity = new ResponseEntity<>(stocks, HttpStatus.NO_CONTENT);
          }
        } else {
          responseEntity = getFromRemoteHost(stockSymbol, keyQueryMetadata);
        }

      } else {
        responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }

    } catch (Exception e) {
      logger.error(" Exception ", e);
    }
    return responseEntity;
  }

  private ResponseEntity<Stocks> getFromRemoteHost(
      String stockSymbol, KeyQueryMetadata keyQueryMetadata) {
    Stocks stocks = null;

    /**
     * Call to the host which is having the data, Use the same enpoint as what we use for this
     * request.
     */
    return new ResponseEntity<>(stocks, HttpStatus.OK);
  }
}
