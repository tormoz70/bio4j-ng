package ru.bio4j.ng.service.api;

/**
 * Created by ayrat.haliullin on 19.10.2016.
 */
public class ExpDatasetApi {
    /**
     * Код инф. объекта, который выполняет команду "Run" для процесса экспорта набора данных
     */
    private String runCommand;
    /**
     * Код инф. объекта, который выполняет команду "State" для процесса экспорта набора данных
     */
    private String stateCommand;

    /**
     * Код инф. объекта, который выполняет команду "Break" для процесса экспорта набора данных
     */
    private String breakCommand;

    public String getRunCommand() {
        return runCommand;
    }

    public void setRunCommand(String runCommand) {
        this.runCommand = runCommand;
    }

    public String getStateCommand() {
        return stateCommand;
    }

    public void setStateCommand(String stateCommand) {
        this.stateCommand = stateCommand;
    }

    public String getBreakCommand() {
        return breakCommand;
    }

    public void setBreakCommand(String breakCommand) {
        this.breakCommand = breakCommand;
    }
}
