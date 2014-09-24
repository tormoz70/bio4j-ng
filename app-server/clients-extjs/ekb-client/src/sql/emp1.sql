/*#test*/
SELECT 
  a.empno/*@empno, title=Уникальный ID, javaType=string*/, 
  a.ename, 
  a.job, 
  a.mgr,
  to_char(a.hiredate, 'DD/MM/YYYY') hiredate/*@hiredate, title=Hire Date, javaType=string*/,
  a.sal,
  a.comm, 
  a.deptno
FROM scott.emp a
WHERE a.empno = /*$empno, type=int {*/7499/*}*/
