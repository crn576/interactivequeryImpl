package com.kafkaimpl.interactivequeryImpl.listeners;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.streams.processor.StateRestoreListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *  Provide the state Store listener, optional, this is required to see progress of the instance in case of restoration, how it is restoring the data from
 *  the changelog topic to back to store, remember to always to use the change-log topic, to restore the data,
 *  this is to ensure:
 *  1. In case of cloud instance, where each instances are deployed across the regions, in any case if one instance went down for
 *  some reason, kafka will assign those partitions back to other instance to continue work, in that case, kafka looks to changelog
 *  and restore the data.
 *
 *  2. In case of single instance where all the partition threads are consuming the data, and for some reason the
 *  instance has crashed, so when it restarts, stream threads joins back to cluster, and restores the data from the change-log,
 *  to track this progress, we a listener which will show us the progress.
 *
 *  3. You can use the stand-by stores for each instance, but that comes with cost of storage/Disk, and when the died instance comes back,
 *  you have to assign back them some partitions, this is done by kafka. So in any case you will see some restoration happens, and you want
 *  see the progress.
 *
 *   If you don't provide the state store listener, while at restart of stream app/instance, it wont consume any data, and you will assume that
 *   stream is not doing any work, but in background it is restoring the data from change-log.
 *
 */
@Component
public class StateStoreListener implements StateRestoreListener {
    private static final Logger logger = LoggerFactory.getLogger(StateStoreListener.class);


    @Override
    public void onRestoreStart(TopicPartition topicPartition, String storeName, long startingOffset, long endingOffset) {
        logger.info(" Restoration started for " + topicPartition.partition());
    }

    @Override
    public void onBatchRestored(TopicPartition topicPartition, String storeName, long batchEndOffset, long numRestored) {
        logger.info(" Batch Restoration started for " + topicPartition.partition());
    }

    @Override
    public void onRestoreEnd(TopicPartition topicPartition, String storeName, long totalRestored) {
        logger.info(" Restoration completed for " + topicPartition.partition());
    }
}
