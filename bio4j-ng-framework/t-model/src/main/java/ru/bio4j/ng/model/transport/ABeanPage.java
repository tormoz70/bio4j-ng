package ru.bio4j.ng.model.transport;

import ru.bio4j.ng.model.transport.jstore.Field;

import java.util.List;

public class ABeanPage {
    private List<ABean> rows;
    private int paginationPage;
    private int paginationOffset;
    private int paginationCount;
    private int totalCount;
    private List<Field> metadata;

    public List<ABean> getRows() {
        return rows;
    }

    public void setRows(List<ABean> rows) {
        this.rows = rows;
    }

    public int getPaginationOffset() {
        return paginationOffset;
    }

    public void setPaginationOffset(int paginationOffset) {
        this.paginationOffset = paginationOffset;
    }

    public int getPaginationCount() {
        return paginationCount;
    }

    public void setPaginationCount(int paginationCount) {
        this.paginationCount = paginationCount;
    }

    public List<Field> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<Field> metadata) {
        this.metadata = metadata;
    }

    public int getPaginationPage() {
        return paginationPage;
    }

    public void setPaginationPage(int paginationPage) {
        this.paginationPage = paginationPage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
