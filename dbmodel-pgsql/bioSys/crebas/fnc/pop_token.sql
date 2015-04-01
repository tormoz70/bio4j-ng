create or replace function biosys.pop_token(
	p_list inout varchar,
	p_rslt out varchar,
	p_delimeter varchar default ',') 
returns record as
$body$
declare
	vCommaPos integer;
	vDelimeterLen integer := length(p_delimeter);
begin
	p_rslt := null;
	if length(p_list) > 0  then
		vCommaPos := position(p_delimeter in p_list);
		if vCommaPos > 0 then
			p_rslt := substring(p_list from 1 for vCommaPos-1);
			p_list := substring(p_list from vCommaPos+vDelimeterLen);
		else
			p_rslt := p_list;
			p_list := null;
		end if;
	end if;
	return;
end;
$body$ language plpgsql;


select biosys.pop_token('qwe,asd,asd')
	 