alter session set current_schema=BIOSYS;
drop context bio_context;
create context bio_context using biosys.LOGIN$UTL;
