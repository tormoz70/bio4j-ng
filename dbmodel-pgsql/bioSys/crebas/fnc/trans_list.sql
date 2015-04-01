create or replace function biosys.trans_list(p_list varchar, p_delimeter varchar default ',')
returns setof varchar as
$body$
declare
	v_item varchar(32000);
	v_overload_count integer := 0;
begin
	while p_list is not null loop
		select biosys.get_token(p_list, p_delimeter) into v_item;
		select biosys.cut_token(p_list, p_delimeter) into p_list;
		RAISE INFO '%   -   %', p_list, v_item; 
		return next v_item;
		v_overload_count := v_overload_count + 1;
		if v_overload_count  > 10 then
			exit;
		end if;
	end loop;
end;
$body$ language plpgsql;

select ((biosys.pop_token('zxc')).p_list is null)
select biosys.trans_list('qwe,asd,zxc')