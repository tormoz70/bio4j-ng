SELECT * FROM (
        SELECT pgng$wrpr0.*, ROWNUM rnum$pgng
          FROM ( select *
      from table(ADSS_API09_DEV.getCampaignDays(
        p_rlocale=>:rlocale, p_currid=>:currId, p_campaignid=>:id, p_periodstart=>:dateStart, p_periodend=>:dateEnd,
        p_userorgid=>:p_sys_curusr_org_uid, p_userrole=>:p_sys_curusr_roles, p_usergrants=>:p_sys_curusr_grants)) ) pgng$wrpr0
    ) pgng$wrpr WHERE pgng$wrpr.rnum$pgng BETWEEN
        :pagination$offset + 1
        AND
        :pagination$offset+:pagination$limit