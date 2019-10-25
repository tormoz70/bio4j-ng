package ru.bio4j.ng.commons.utils;

import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;

import java.util.List;

/**
 * Created by ayrat on 13.03.2016.
 */
public class SrvcUtils {

    public static final String PARAM_STOKEN            = "p_sys_stoken";
    public static final String PARAM_CURUSR_UID        = "p_sys_curusr_uid";
    public static final String PARAM_CURUSR_ORG_UID    = "p_sys_curusr_org_uid";
    public static final String PARAM_CURUSR_ROLES      = "p_sys_curusr_roles";
    public static final String PARAM_CURUSR_GRANTS     = "p_sys_curusr_grants";
    public static final String PARAM_CURUSR_IP         = "p_sys_curusr_ip";
    public static final String PARAM_CURUSR_CLIENT     = "p_sys_curusr_client";

    public static void applyCurrentUserParams(final User usr, final List<Param> params) {
        if (usr != null) {
            try (Paramus p = Paramus.set(params)) {
                p.setValue(SrvcUtils.PARAM_STOKEN, usr.getStoken(), Param.Direction.IN, true);
                p.setValue(SrvcUtils.PARAM_CURUSR_UID, usr.getInnerUid(), Param.Direction.IN, true);
                p.setValue(SrvcUtils.PARAM_CURUSR_ORG_UID, usr.getOrgId(), Param.Direction.IN, true);
                p.setValue(SrvcUtils.PARAM_CURUSR_ROLES, usr.getRoles(), Param.Direction.IN, true);
                p.setValue(SrvcUtils.PARAM_CURUSR_GRANTS, usr.getGrants(), Param.Direction.IN, true);
                p.setValue(SrvcUtils.PARAM_CURUSR_IP, usr.getRemoteIP(), Param.Direction.IN, true);
                p.setValue(SrvcUtils.PARAM_CURUSR_CLIENT, usr.getRemoteClient(), Param.Direction.IN, true);
            }
        }
    }

}
