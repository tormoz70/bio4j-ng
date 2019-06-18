package ru.bio4j.ng.model.transport;

public class SpaceStat {
    private String path;
    private long total;
    private long usable;
    private double freePCT;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getUsable() {
        return usable;
    }

    public void setUsable(long usable) {
        this.usable = usable;
    }

    public double getFreePCT() {
        return freePCT;
    }

    public void setFreePCT(double freePCT) {
        this.freePCT = freePCT;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
