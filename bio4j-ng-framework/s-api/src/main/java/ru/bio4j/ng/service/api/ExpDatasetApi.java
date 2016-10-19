package ru.bio4j.ng.service.api;

import ru.bio4j.ng.commons.types.Prop;

/**
 * Created by ayrat.haliullin on 19.10.2016.
 */
public class ExpDatasetApi {
    /**
     * Код инф. объекта, который выполняет команду "Run" для процесса экспорта набора данных
     */
    private String exportDatasetApiRun;
    /**
     * Код инф. объекта, который выполняет команду "GetState" для процесса экспорта набора данных
     */
    private String exportDatasetApiGetState;

    /**
     * Код инф. объекта, который выполняет команду "Break" для процесса экспорта набора данных
     */
    private String exportDatasetApiBreak;

    public String getExportDatasetApiRun() {
        return exportDatasetApiRun;
    }

    public void setExportDatasetApiRun(String exportDatasetApiRun) {
        this.exportDatasetApiRun = exportDatasetApiRun;
    }

    public String getExportDatasetApiGetState() {
        return exportDatasetApiGetState;
    }

    public void setExportDatasetApiGetState(String exportDatasetApiGetState) {
        this.exportDatasetApiGetState = exportDatasetApiGetState;
    }

    public String getExportDatasetApiBreak() {
        return exportDatasetApiBreak;
    }

    public void setExportDatasetApiBreak(String exportDatasetApiBreak) {
        this.exportDatasetApiBreak = exportDatasetApiBreak;
    }
}
