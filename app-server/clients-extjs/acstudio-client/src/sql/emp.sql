/*#test*/
SELECT 
  a.empno/*@empno, title=Уникальный ID, javaType=string*/, 
  a.ename, 
  a.job, 
  a.mgr,
  a.hiredate/*@hiredate, title=Hire Date, javaType=date*/,
  a.sal,
  a.comm, 
  a.deptno
FROM scott.emp a
