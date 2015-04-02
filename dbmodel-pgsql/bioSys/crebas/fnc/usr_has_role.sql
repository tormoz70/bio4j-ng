create or replace function biosys.usr_has_role(p_usr_uid varchar, p_roles varchar) 
returns integer as
$body$
declare
	v_exists integer;
begin
	select count(1) into v_exists from usrrle u
	  where u.usr_uid = upper(p_usr_uid)
	    and u.role_uid in (select upper(trim(item)) from ai$utl.trans_list(p_roles, ';')));
	return (case when v_exists = 0 then 0 else 1 end);
end;
$body$ language plpgsql;