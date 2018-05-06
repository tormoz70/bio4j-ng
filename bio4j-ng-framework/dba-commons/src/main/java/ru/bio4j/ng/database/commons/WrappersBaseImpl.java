package ru.bio4j.ng.database.commons;

import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.database.commons.wrappers.*;
import ru.bio4j.ng.database.api.FilteringWrapper;
import ru.bio4j.ng.database.api.GetrowWrapper;
import ru.bio4j.ng.database.api.LocateWrapper;
import ru.bio4j.ng.database.api.PaginationWrapper;

import java.io.InputStream;
import java.util.Map;

public class WrappersBaseImpl implements Wrappers {

    private Map<WrapQueryType, String> templates = null;
    private FilteringWrapper filteringWrapper;
    private SortingWrapper sortingWrapper;
    private PaginationWrapper paginationWrapper;
    private TotalsWrapper totalsWrapper;
    private LocateWrapper locateWrapper;
    private GetrowWrapper getrowWrapper;


    public WrappersBaseImpl(String dbmsName, WrapperInterpreter wrapperInterpreter) throws Exception {
        final String templFileName = "/cursor/wrapper/templates/" + dbmsName + ".xml";
        final InputStream is = this.getClass().getResourceAsStream(templFileName);
        if(is == null)
            throw new IllegalArgumentException(String.format("Resource %s not found!", templFileName));
        templates = WrapperLoader.loadQueries(is, dbmsName);

        filteringWrapper = new FilteringWrapperBaseImpl(templates.get(WrapQueryType.FILTERING), wrapperInterpreter);
        sortingWrapper = new SortingWrapperBaseImpl(templates.get(WrapQueryType.SORTING), wrapperInterpreter);
        paginationWrapper = new PaginationWrapperBaseImpl(templates.get(WrapQueryType.PAGINATION), wrapperInterpreter);
        totalsWrapper = new TotalsWrapperBaseImpl(templates.get(WrapQueryType.TOTALS), wrapperInterpreter);
        locateWrapper = new LocateWrapperBaseImpl(templates.get(WrapQueryType.LOCATE), wrapperInterpreter);
        getrowWrapper = new GetrowWrapperBaseImpl(templates.get(WrapQueryType.GETROW), wrapperInterpreter);

    }

    @Override
    public FilteringWrapper getFilteringWrapper() {
        return filteringWrapper;
    }

    @Override
    public SortingWrapper getSortingWrapper() {
        return sortingWrapper;
    }

    @Override
    public PaginationWrapper getPaginationWrapper() {
        return paginationWrapper;
    }

    @Override
    public TotalsWrapper getTotalsWrapper() {
        return totalsWrapper;
    }

    @Override
    public LocateWrapper getLocateWrapper() {
        return locateWrapper;
    }

    @Override
    public GetrowWrapper getGetrowWrapper() {
        return getrowWrapper;
    }
}
