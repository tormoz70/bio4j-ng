PROMPT ������� ��������� ��� �������� ����� BIOSYS
ACCEPT pdb      PROMPT '������� ��� �� => '
ACCEPT tnsname  PROMPT '������� ��� TNS (�� ���������: &pdb) => ' default '&pdb'
ACCEPT ppswd    PROMPT '������� ������ ��� ������������ SYS => ' HIDE
ACCEPT datapath PROMPT '���� � �� �� ������� (�� ���������: +DATA) => ' default '+DATA'

SPOOL install.log

CONNECT sys/&ppswd@&tnsname as sysdba

@@cre_objects.sql

prompt ���������� �����...
EXEC DBMS_UTILITY.COMPILE_SCHEMA('BIOSYS',TRUE);
prompt ���������

@@bioSys/grants.sql

spool off
disconnect
exit 0
