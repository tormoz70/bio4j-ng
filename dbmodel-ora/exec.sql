PROMPT Введите параметры для создания схемы BIOSYS
ACCEPT pdb      PROMPT 'Введите имя БД => '
ACCEPT tnsname  PROMPT 'Введите имя TNS (по умолчанию: &pdb) => ' default '&pdb'
ACCEPT ppswd    PROMPT 'Введите пароль для пользователя SYS => ' HIDE
ACCEPT datapath PROMPT 'Путь к БД на сервере (по умолчанию: +DATA) => ' default '+DATA'

SPOOL install.log

CONNECT sys/&ppswd@&tnsname as sysdba

@@cre_objects.sql

prompt Компиляция схемы...
EXEC DBMS_UTILITY.COMPILE_SCHEMA('BIOSYS',TRUE);
prompt Завершено

@@bioSys/grants.sql

spool off
disconnect
exit 0
