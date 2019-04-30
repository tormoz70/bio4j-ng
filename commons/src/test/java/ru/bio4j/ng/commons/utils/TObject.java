package ru.bio4j.ng.commons.utils;

import ru.bio4j.ng.model.transport.Prop;
import ru.bio4j.ng.model.transport.Param;

import java.util.Date;

public class TObject {
    @Prop(direction = Param.Direction.INOUT)
    public Long tobject_id;
    public Long factory_org_id;
    public Long tobjtype_id;
    public String autor_person_uid;
    public String filesuid;
    public String aname;
    public String adesc;
    public String prodplace;
    public String cretimes;
    public Integer cretime_strt;
    public Integer cretime_end;
    public Date cretime_date;
    public String sdims;
    public String weight;
}
