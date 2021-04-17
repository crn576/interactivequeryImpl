package com.kafkaimpl.interactivequeryImpl.config;

import org.apache.kafka.streams.state.RocksDBConfigSetter;
import org.rocksdb.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RocksDBConfig implements RocksDBConfigSetter {

  private static final Cache cache = new LRUCache(250 * 1024L * 1024L);
  private static final WriteBufferManager writeBufferManager =
      new WriteBufferManager(250 * 1024L * 1024L, cache);

  @Override
  public void setConfig(String s, Options options, Map<String, Object> map) {
    BlockBasedTableConfig tableConfig = (BlockBasedTableConfig) options.tableFormatConfig();

    tableConfig.setBlockCache(cache);
    tableConfig.setCacheIndexAndFilterBlocks(true);
    tableConfig.setCacheIndexAndFilterBlocks(true);
    tableConfig.setPinTopLevelIndexAndFilter(true);
    tableConfig.setBlockSize(16 * 1024L);

    options.setWriteBufferManager(writeBufferManager);
    options.setMaxWriteBufferNumber(10);
    options.setWriteBufferSize(32 * 1024 * 1024);
    options.setTableFormatConfig(tableConfig);
    options.setMaxBackgroundCompactions(10);
    options.setMaxBackgroundFlushes(10);
    options.setMaxBackgroundJobs(10);
    options.setIncreaseParallelism(10);

    /**
     * Compaction Style: This affects the write performance, so decide based on your data, whether
     * your is a write heavy or read heavy Write Heavy : UNIVERSAL Read Heavy : LEVEL
     */
    options.setCompactionStyle(CompactionStyle.UNIVERSAL);
  }
}
