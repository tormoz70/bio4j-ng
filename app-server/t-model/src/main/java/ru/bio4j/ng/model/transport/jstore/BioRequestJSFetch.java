package ru.bio4j.ng.model.transport.jstore;

import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.jstore.filter.Expression;

/**
 * Открывет на сервере курсор и пробегает по нему,
 * запуская на выполнение хранимую процедуру execBioCode.
 * При этом все колонки курсора передаются как параметры в хранимую процедуру execBioCode.
 */
public class BioRequestJSFetch extends BioRequest {

    /**
     * Накладывается как дополнительный фильтр на курсор
     */
    private String selection;

    /**
     * Используется при открытии курсора
     */
    private Sort sort;

    /**
     * Используется при открытии курсора
     */
    private Expression filter;

    /**
     * URL метаописания хранимой процедуры
     */
    private String execBioCode;

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public Expression getFilter() {
        return filter;
    }

    public void setFilter(Expression filter) {
        this.filter = filter;
    }

    public String getExecBioCode() {
        return execBioCode;
    }

    public void setExecBioCode(String execBioCode) {
        this.execBioCode = execBioCode;
    }
}
