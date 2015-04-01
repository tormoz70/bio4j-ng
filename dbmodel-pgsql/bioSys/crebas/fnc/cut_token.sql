create or replace function biosys.cut_token(
	p_list varchar,
	p_delimeter varchar default ',') 
returns varchar as
$body$
declare
	rslt varchar(32000);
begin
	select (biosys.pop_token(p_list, p_delimeter)).p_list into p_list;
	return p_list;
end;
$body$ language plpgsql;


select biosys.cut_token('qwe,asd,asd')
