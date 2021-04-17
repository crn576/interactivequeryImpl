package com.kafkaimpl.interactivequeryImpl.serdes;

import com.google.gson.Gson;
import com.kafkaimpl.interactivequeryImpl.model.Stocks;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;

public class StocksSerdes extends Serdes.WrapperSerde<Stocks> {


    public StocksSerdes() {
         super(new Serializer<Stocks>() {
             private Gson gson = new Gson();

             @Override
             public byte[] serialize(String s, Stocks stocks) {
                 return this.gson.toJson(stocks).getBytes(StandardCharsets.UTF_8);
             }

         }, new Deserializer<Stocks>() {
             private Gson gson = new Gson();
             Stocks stocks = null;

             @Override
             public Stocks deserialize(String s, byte[] bytes) {
                 try{
                     stocks = (Stocks) this.gson.fromJson(new String(bytes), Stocks.class);
                 }
                 catch (Exception e)
                 {
                     stocks = null;
                 }
                 return stocks;
             }
         });
    }
}
