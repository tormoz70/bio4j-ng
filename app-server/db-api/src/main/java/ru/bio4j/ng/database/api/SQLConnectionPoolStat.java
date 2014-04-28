package ru.bio4j.ng.database.api;

public class SQLConnectionPoolStat {

    private String connectionPoolName;
    private int minPoolSize;
    private int maxPoolSize;
    private int connectionWaitTimeout;
    private int initialPoolSize;

    private int totalConnectionsCount;
    private int availableConnectionsCount;
    private int borrowedConnectionsCount;
    private int averageBorrowedConnectionsCount;
    private int peakConnectionsCount;
    private int remainingPoolCapacityCount;

    private int labeledConnectionsCount;
    private int connectionsCreatedCount;
    private int connectionsClosedCount;
    private long averageConnectionWaitTime;
    private long peakConnectionWaitTime;
    private int abandonedConnectionsCount;
    private int pendingRequestsCount;
    private long cumulativeConnectionWaitTime;
    private long cumulativeConnectionBorrowedCount;
    private long cumulativeConnectionUseTime;
    private long cumulativeConnectionReturnedCount;
    private long cumulativeSuccessfulConnectionWaitTime;
    private long cumulativeSuccessfulConnectionWaitCount;
    private long cumulativeFailedConnectionWaitTime;
    private long cumulativeFailedConnectionWaitCount;

    public SQLConnectionPoolStat(
            String connectionPoolName, int minPoolSize, int maxPoolSize, int connectionWaitTimeout, int initialPoolSize,
            int totalConnectionsCount, int availableConnectionsCount, int borrowedConnectionsCount,
            int averageBorrowedConnectionsCount, int peakConnectionsCount, int remainingPoolCapacityCount,
            int labeledConnectionsCount, int connectionsCreatedCount, int connectionsClosedCount,
            long averageConnectionWaitTime, long peakConnectionWaitTime, int abandonedConnectionsCount,
            int pendingRequestsCount, long cumulativeConnectionWaitTime, long cumulativeConnectionBorrowedCount,
            long cumulativeConnectionUseTime, long cumulativeConnectionReturnedCount, long cumulativeSuccessfulConnectionWaitTime,
            long cumulativeSuccessfulConnectionWaitCount, long cumulativeFailedConnectionWaitTime,
            long cumulativeFailedConnectionWaitCount) {
        this.connectionPoolName = connectionPoolName;
        this.minPoolSize = minPoolSize;
        this.maxPoolSize = maxPoolSize;
        this.connectionWaitTimeout = connectionWaitTimeout;
        this.initialPoolSize = initialPoolSize;
        this.totalConnectionsCount = totalConnectionsCount;
        this.availableConnectionsCount = availableConnectionsCount;
        this.borrowedConnectionsCount = borrowedConnectionsCount;
        this.averageBorrowedConnectionsCount = averageBorrowedConnectionsCount;
        this.peakConnectionsCount = peakConnectionsCount;
        this.remainingPoolCapacityCount = remainingPoolCapacityCount;
        this.labeledConnectionsCount = labeledConnectionsCount;
        this.connectionsCreatedCount = connectionsCreatedCount;
        this.connectionsClosedCount = connectionsClosedCount;
        this.averageConnectionWaitTime = averageConnectionWaitTime;
        this.peakConnectionWaitTime = peakConnectionWaitTime;
        this.abandonedConnectionsCount = abandonedConnectionsCount;
        this.pendingRequestsCount = pendingRequestsCount;
        this.cumulativeConnectionWaitTime = cumulativeConnectionWaitTime;
        this.cumulativeConnectionBorrowedCount = cumulativeConnectionBorrowedCount;
        this.cumulativeConnectionUseTime = cumulativeConnectionUseTime;
        this.cumulativeConnectionReturnedCount = cumulativeConnectionReturnedCount;
        this.cumulativeSuccessfulConnectionWaitTime = cumulativeSuccessfulConnectionWaitTime;
        this.cumulativeSuccessfulConnectionWaitCount = cumulativeSuccessfulConnectionWaitCount;
        this.cumulativeFailedConnectionWaitTime = cumulativeFailedConnectionWaitTime;
        this.cumulativeFailedConnectionWaitCount = cumulativeFailedConnectionWaitCount;
    }

    public String getConnectionPoolName() {
        return connectionPoolName;
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getConnectionWaitTimeout() {
        return connectionWaitTimeout;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public int getTotalConnectionsCount() {
        return totalConnectionsCount;
    }

    public int getAvailableConnectionsCount() {
        return availableConnectionsCount;
    }

    public int getBorrowedConnectionsCount() {
        return borrowedConnectionsCount;
    }

    public int getAverageBorrowedConnectionsCount() {
        return averageBorrowedConnectionsCount;
    }

    public int getPeakConnectionsCount() {
        return peakConnectionsCount;
    }

    public int getRemainingPoolCapacityCount() {
        return remainingPoolCapacityCount;
    }

    public int getLabeledConnectionsCount() {
        return labeledConnectionsCount;
    }

    public int getConnectionsCreatedCount() {
        return connectionsCreatedCount;
    }

    public int getConnectionsClosedCount() {
        return connectionsClosedCount;
    }

    public long getAverageConnectionWaitTime() {
        return averageConnectionWaitTime;
    }

    public long getPeakConnectionWaitTime() {
        return peakConnectionWaitTime;
    }

    public int getAbandonedConnectionsCount() {
        return abandonedConnectionsCount;
    }

    public int getPendingRequestsCount() {
        return pendingRequestsCount;
    }

    public long getCumulativeConnectionWaitTime() {
        return cumulativeConnectionWaitTime;
    }

    public long getCumulativeConnectionBorrowedCount() {
        return cumulativeConnectionBorrowedCount;
    }

    public long getCumulativeConnectionUseTime() {
        return cumulativeConnectionUseTime;
    }

    public long getCumulativeConnectionReturnedCount() {
        return cumulativeConnectionReturnedCount;
    }

    public long getCumulativeSuccessfulConnectionWaitTime() {
        return cumulativeSuccessfulConnectionWaitTime;
    }

    public long getCumulativeSuccessfulConnectionWaitCount() {
        return cumulativeSuccessfulConnectionWaitCount;
    }

    public long getCumulativeFailedConnectionWaitTime() {
        return cumulativeFailedConnectionWaitTime;
    }

    public long getCumulativeFailedConnectionWaitCount() {
        return cumulativeFailedConnectionWaitCount;
    }

}
