SELECT * FROM (
        SELECT pgng$wrpr0.*, ROWNUM rnum$pgng
          FROM ( select *
      from table(ADSS_API09_DEV.getCampaignDays(
        p_rlocale=>null, p_currid=>null, p_campaignid=>null, p_periodstart=>'2019.07.01', p_periodend=>'2019.08.01',
        p_userorgid=>'1009686', p_userrole=>'21', p_usergrants=>'151,152')) ) pgng$wrpr0
    ) pgng$wrpr WHERE pgng$wrpr.rnum$pgng BETWEEN
        0 + 1
        AND
        0+50