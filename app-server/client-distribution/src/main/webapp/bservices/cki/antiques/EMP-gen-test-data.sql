SELECT a.empno, a.ename, a.job, a.mgr, a.hiredate, a.sal, a.comm,
       a.deptno
  FROM scott.emp a
/
declare
  cs_table_owner constant varchar2(30) := 'SCOTT';
  cs_table_name constant varchar2(30) := 'EMP';
begin
  dbms_output.put_line('Ext.define('''||lower(cs_table_name)||''', {');
  dbms_output.put_line('extend: ''Ext.data.Model'',');
  dbms_output.put_line('     fields: [');
  for c in (
      select a.*, 
            lead(a.column_id, 1, null) over (order by a.column_id) next_column_id
        from dba_tab_columns a 
       where a.owner = cs_table_owner 
         and a.table_name = cs_table_name)
  loop
    dbms_output.put_line('         {name: '''||lower(c.column_name)||''', type: '''||
      (case c.data_type 
        when 'NUMBER' then (case when c.data_scale > 0 then 'float' else 'int' end)
        when 'VARCHAR2' then 'string'
        when 'DATE' then 'date'
        else 'auto'
      end)||''''||
      (case c.data_type 
        when 'DATE' then ', dateFormat: ''Ymd'''
        else null
      end)||
      '}'||(case when c.next_column_id is null then null else ',' end));
  end loop;         
  dbms_output.put_line('     ]');
  dbms_output.put_line('});');
end;
/

declare
  cs_table_owner constant varchar2(30) := 'SCOTT';
  cs_table_name constant varchar2(30) := 'EMP';
  function as_string(pp_val in varchar2) return varchar2 is
  begin
    return '''' || pp_val || '''';
  end;
  function as_date(pp_val in date) return varchar2 is
  begin
    return '''' || to_char(pp_val, 'YYYYMMDD') || '''';
  end;
  function as_number(pp_val in number) return varchar2 is
  begin
    return replace(trim(to_char(pp_val)), ',', '.');
  end;
begin
  dbms_output.put_line('Idbm.test.Emp = [ ');
  for c in (
    select a.empno, a.ename, a.job, a.mgr, a.hiredate, a.sal, a.comm, a.deptno,
        lead(a.empno, 1, null) over (order by a.empno) next_empno
      from scott.emp a
  ) loop
      dbms_output.put_line('  ['||
        c.empno||', '||as_string(c.ename)||', '||as_string(c.job)||', '||c.mgr||', '||as_date(c.hiredate)||', '||as_number(c.sal)||', '||as_number(c.comm)||', '||c.deptno||
      ']'||(case when c.next_empno is null then null else ',' end));
  end loop;         
  dbms_output.put_line('];');
end;
/

