package ru.smax.social.network.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

    private final List<String> readOnlyReplicas;
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceType = TransactionSynchronizationManager.isCurrentTransactionReadOnly()
                ? pickReadOnlyReplica()
                : "write";
//        log.info("current datasource is '{}'", dataSourceType);
        return dataSourceType;
    }

    private String pickReadOnlyReplica() {
        // round-robin
        var index = counter.getAndUpdate(i -> (i + 1) % readOnlyReplicas.size());
        return readOnlyReplicas.get(index);
    }
}
