package ru.bio4j.ng.model.transport;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by ayrat.haliullin on 02.11.2016.
 */
public class CDATAAdapter extends XmlAdapter<String, String> {

    @Override
    public String marshal(String v) throws Exception {
        return "<![CDATA[" + v + "]]>";
    }

    @Override
    public String unmarshal(String v) throws Exception {
        return v;
    }
}
