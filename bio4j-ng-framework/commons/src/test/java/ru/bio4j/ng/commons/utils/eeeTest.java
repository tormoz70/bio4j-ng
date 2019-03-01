package ru.bio4j.ng.commons.utils;

import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.model.transport.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ayrat
 * Date: 13.12.13
 * Time: 1:17
 * To change this template use File | Settings | File Templates.
 */
public class eeeTest {
    @Test(enabled = false)
    public void testDdd() throws Exception {
        String sss = "Тест!!!";
        Assert.fail(sss);
    }


    @Test(enabled = true)
    public void deleteNonSQLSubstringsInSQLTest() throws Exception {
        String sql = "\n" +
                "        SELECT COUNT(1) ttlCnt$wrpr\n" +
                "          FROM ( with \n" +
                "orgs as (\n" +
                "  select /*+ MATERIALIZE */ o.id_org, o.holding_id, o.time_zone, o.id_vnd, DECODE (o.test, 1, 'тестовый', 'реальный') AS test,\n" +
                "        v.vndname, v.contacts AS vndcontacts\n" +
                "    from org$r o \n" +
                "      left join givc_org.softvendor v ON v.id_vnd = o.id_vnd\n" +
                "),\n" +
                "ekbps as (\n" +
                "SELECT /*+ MATERIALIZE */\n" +
                "   a.packet_id as id,\n" +
                "  a.org_id,\n" +
                "  a.sess_prnt_org_id,\n" +
                "  a.sess_org_id,\n" +
                "  a.ip_addr as ip,\n" +
                "  a.registred as date_incoming,\n" +
                "  decode(a.packet_name, '...', a.zip_name, a.packet_name) as packet_name,\n" +
                "  a.zip_name,\n" +
                "  a.processed as date_processing,\n" +
                "  a.is_loaded,\n" +
                "  a.cur_pstate as cur_pstate0,\n" +
                "  decode(a.is_loaded, '1', 'загружен', s.description) as cur_pstate,\n" +
                "  a.cur_pstate_msg as last_pstate_msg,\n" +
                "  a.is_log_downloaded,\n" +
                "  a.log_downloaded,\n" +
                "  nvl(o1.time_zone, o2.time_zone) as time_zone,\n" +
                "  nvl(o1.id_vnd, o2.id_vnd) as id_vnd, \n" +
                "  nvl(o1.vndname, o2.vndname) as vndname, \n" +
                "  nvl(o1.vndcontacts, o2.vndcontacts) as vndcontacts,\n" +
                "  nvl(o1.test, o2.test) as test,\n" +
                "  decode(a.load_method, \n" +
                "          0, 'Авт. система', \n" +
                "          1, 'Из кабинета', \n" +
                "          2, 'CreateXMLStatic', \n" +
                "          3, 'CreateXMLMobile', \n" +
                "          4, 'EkbUploadRobot', \n" +
                "          5, 'Grader', 'не определен') as load_method,\n" +
                "  decode(a.show_date, null, '00000000', to_char(a.show_date, 'YYYYMMDD')) part_key\n" +
                "  FROM FPACKET a\n" +
                "        left join orgs o1 on o1.id_org = a.sess_org_id\n" +
                "        left join orgs o2 on o2.id_org = a.org_id\n" +
                "        left join nsi$ekbpstate s on s.id = a.cur_pstate\n" +
                "  WHERE a.packet_name = nvl(:packet_name_full, a.packet_name) and\n" +
                "        ((a.registred >= biosys.ai_utl.db_datetime(decode(:reg_from, null, to_date('1900', 'YYYY'), :reg_from), :time_zone)) AND\n" +
                "         (a.registred < biosys.ai_utl.db_datetime(decode(:reg_to, null, to_date('2900', 'YYYY'), :reg_to), :time_zone)+1))\n" +
                "    AND ((:force_org_id IS NULL) OR \n" +
                "          ((a.org_id = :force_org_id) OR ((a.sess_org_id = :force_org_id) and (a.org_id = o1.holding_id))\n" +
                "          or (a.sess_prnt_org_id =:force_org_id))\n" +
                "         )\n" +
                "    \n" +
                ")\n" +
                "SELECT \n" +
                "  a.id, \n" +
                "  a.org_id,\n" +
                "  a.sess_prnt_org_id,\n" +
                "  a.sess_org_id,\n" +
                "  a.ip, \n" +
                "  a.date_incoming,\n" +
                "  a.packet_name, \n" +
                "  a.zip_name,\n" +
                "  a.date_processing,\n" +
                "  a.time_zone,\n" +
                "  a.is_loaded,\n" +
                "  a.cur_pstate0,\n" +
                "  a.cur_pstate,\n" +
                "  a.last_pstate_msg,\n" +
                "  a.is_log_downloaded,\n" +
                "  a.log_downloaded, \n" +
                "  a.id_vnd, a.vndname, a.vndcontacts,\n" +
                "  a.load_method,\n" +
                "  a.test,\n" +
                "  a.part_key\n" +
                "  FROM ekbps a\n" +
                "  WHERE ((:SYS_CURUSERROLES in ('6')) or \n" +
                "         ((:SYS_CURUSERROLES in ('4')) and (\n" +
                "           (a.org_id = to_number(:SYS_CURODEPUID)) or\n" +
                "           (a.org_id in (select o.id_org from givc_org.org o where o.holding_id = to_number(:SYS_CURODEPUID)))\n" +
                "          )\n" +
                "         ) or\n" +
                "         ((:SYS_CURUSERROLES in ('3')) and (\n" +
                "            /*(a.sess_org_id = to_number(:SYS_CURODEPUID)) and*/\n" +
                "            ((a.org_id = to_number(:SYS_CURODEPUID)) or\n" +
                "             (a.org_id in (select nvl(o.holding_id, o.id_org) from givc_org.org o where o.id_org = to_number(:SYS_CURODEPUID)))\n" +
                "            )\n" +
                "          )\n" +
                "         )\n" +
                "    ) AND \n" +
                "    (\n" +
                "      (a.org_id = decode(:org_id, null, a.org_id, nvl(to_number(regexp_substr(:org_id, '^\\d+$')), 0))) and\n" +
                "      (nvl(a.sess_prnt_org_id, 0) = decode(:sess_prnt_org_id, null, nvl(a.sess_prnt_org_id, 0), nvl(to_number(regexp_substr(:sess_prnt_org_id, '^\\d+$')), 0))) and\n" +
                "      (a.sess_org_id = decode(:sess_org_id, null, a.sess_org_id, nvl(to_number(regexp_substr(:sess_org_id, '^\\d+$')), 0))) and\n" +
                "      (a.packet_name like decode(:packet_name, null, '%', '%'||lower(:packet_name)||'%')) and\n" +
                "      (a.ip like decode(:ip, null, '%', '%'||lower(:ip)||'%')) and\n" +
                "      (a.cur_pstate like decode(:cur_pstate, null, '%', '%'||lower(:cur_pstate)||'%')) and\n" +
                "      (a.last_pstate_msg like decode(:message, null, '%', '%'||lower(:message)||'%')) and\n" +
                "      (lower(a.load_method) like decode(:load_method, null, '%', '%'||lower(:load_method)||'%')) and\n" +
                "      (a.test like decode(:test, null, '%', '%'||lower(:test)||'%')) \n" +
                "    )\n" +
                "  ORDER BY date_incoming desc\n" +
                " )\n" +
                "    ";
        String sql1 = Sqls.deleteNonSQLSubstringsInSQL(sql);
        Assert.assertTrue(true);
    }


    @Test(enabled = true)
    public void EvalsTest() throws Exception {
        Evals evals = Evals.getInstance();
        List<Param> prms = new ArrayList<>();
        Paramus.setParamValue(prms, "SYS_CURUSERROLES", "6");
        Assert.assertTrue(evals.runCondition("SYS_CURUSERROLES != '3'", prms));
        Assert.assertTrue(evals.runCondition("SYS_CURUSERROLES == '6'", prms));
        Paramus.setParamValue(prms, "SYS_CURUSERROLES", 6);
        Assert.assertTrue(evals.runCondition("SYS_CURUSERROLES != '3'", prms));
        Assert.assertTrue(evals.runCondition("SYS_CURUSERROLES == '6'", prms));
        Assert.assertTrue(evals.runCondition("SYS_CURUSERROLES != 3", prms));
        Assert.assertTrue(evals.runCondition("SYS_CURUSERROLES == 6", prms));
    }
}
