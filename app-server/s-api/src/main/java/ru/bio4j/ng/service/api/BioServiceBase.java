package ru.bio4j.ng.service.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ayrat on 04.05.14.
 */
public class BioServiceBase implements BioService {
    protected boolean redy;

//    private Map<BioEventType, List<BioEventHandler>> registredEvents = new HashMap<>();

    @Override
    public boolean isRedy() {
        return redy;
    }

//    protected void fireEvent(BioEventType event, BioEventAttrs attrs) {
//        List<BioEventHandler> handlers = registredEvents.get(event);
//        if(handlers != null && handlers.size() > 0){
//            for(BioEventHandler h : handlers) {
//                try {
//                    h.handle(this, attrs);
//                } catch (Exception e) {
//
//                }
//            }
//        }
//    }
//
//    @Override
//    public synchronized void addListener(BioEventType event, BioEventHandler handler) {
//        List<BioEventHandler> handlers = registredEvents.get(event);
//        if(handlers == null) {
//            handlers = new ArrayList<>();
//            registredEvents.put(event, handlers);
//        }
//        handlers.add(handler);
//    }
}
