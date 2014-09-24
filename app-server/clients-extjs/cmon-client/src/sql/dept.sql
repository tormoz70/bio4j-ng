/*#test*/
SELECT a.deptno, 
        a.deptno || ' - ' || a.dname||' - '||a.loc dname
  FROM scott.dept a