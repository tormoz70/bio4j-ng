create or replace function biosys.get_token(
	p_list varchar,
	p_delimeter varchar default ',') 
returns varchar as
$body$
declare
	rslt varchar(32000);
begin
	select (biosys.pop_token(p_list, p_delimeter)).p_rslt into rslt;
	return rslt;
end;
$body$ language plpgsql;


select biosys.get_token('qwe,asd,asd')