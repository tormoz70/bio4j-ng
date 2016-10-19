package ru.bio4j.ng.commons.utils;

import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.MetaType;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

@XmlType
@XmlRootElement
public class XLRCfg {

    private String bioCode;

    public String getBioCode() {
        return bioCode;
    }

    public void setBioCode(String bioCode) {
        this.bioCode = bioCode;
    }
}
