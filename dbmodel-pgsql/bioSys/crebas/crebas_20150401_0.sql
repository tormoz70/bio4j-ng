/*==============================================================*/
/* DBMS name:      PostgreSQL 9.x                               */
/* Created on:     01.04.2015 16:38:06                          */
/*==============================================================*/


DROP INDEX BIOSYS.WS$ORG_FK;

DROP INDEX BIOSYS.ORG$ORG_FK;

DROP TABLE BIOSYS.ORG;

DROP TABLE BIOSYS.RADDRSS;

DROP TABLE BIOSYS.SMTP$LOG;

DROP TABLE BIOSYS.UGRANT;

DROP TABLE BIOSYS.UROLE;

DROP INDEX BIOSYS.WS$USR_FK;

DROP INDEX BIOSYS.ORG$USR_FK;

DROP INDEX BIOSYS.IX_USRS_GBRG;

DROP TABLE BIOSYS.USR;

DROP INDEX BIOSYS.GRANT$USR_FK;

DROP INDEX BIOSYS.USR$GRANT_FK;

DROP TABLE BIOSYS.USRGRNT;

DROP INDEX BIOSYS.ADDR$LOG_FK;

DROP TABLE BIOSYS.USRIN$LOG;

DROP INDEX BIOSYS.USR$LCK_FK;

DROP TABLE BIOSYS.USRLOCK;

DROP INDEX BIOSYS.USR$LOG_FK;

DROP TABLE BIOSYS.USRLOG;

DROP INDEX BIOSYS.USR$ROLE_FK;

DROP INDEX BIOSYS.ROLE$USR_FK;

DROP TABLE BIOSYS.USRRLE;

DROP INDEX BIOSYS.USR$WS_FK;

DROP TABLE BIOSYS.UWORKSPACE;

DROP DOMAIN BIOSYS.BIG_TEXT_T;

DROP DOMAIN BIOSYS.BOOL_T;

DROP DOMAIN BIOSYS.CITDEP_NAME_T;

DROP DOMAIN BIOSYS.CITY_NAME_T;

DROP DOMAIN BIOSYS.COMMENTS_T;

DROP DOMAIN BIOSYS.CONSTR_T;

DROP DOMAIN BIOSYS.DATETIME_T;

DROP DOMAIN BIOSYS.DATE_T;

DROP DOMAIN BIOSYS.DESC_T;

DROP DOMAIN BIOSYS.EMAIL_T;

DROP DOMAIN BIOSYS.ERROR_T;

DROP DOMAIN BIOSYS.FIO_FAM_T;

DROP DOMAIN BIOSYS.FIO_NAME_T;

DROP DOMAIN BIOSYS.FIO_SNAME_T;

DROP DOMAIN BIOSYS.GRANT_T;

DROP DOMAIN BIOSYS.ID_T;

DROP DOMAIN BIOSYS.NAME_T;

DROP DOMAIN BIOSYS.ORG_NAME_T;

DROP DOMAIN BIOSYS.PHONE_T;

DROP DOMAIN BIOSYS.PORT_T;

DROP DOMAIN BIOSYS.RADDR_DESC_T;

DROP DOMAIN BIOSYS.RADDR_T;

DROP DOMAIN BIOSYS.RCLIENT_T;

DROP DOMAIN BIOSYS.RHOST_T;

DROP DOMAIN BIOSYS.ROLE_T;

DROP DOMAIN BIOSYS.SES_STATUS_T;

DROP DOMAIN BIOSYS.SWITCH$C_T;

DROP DOMAIN BIOSYS.TABLE_NAME_T;

DROP DOMAIN BIOSYS.UID_T;

DROP DOMAIN BIOSYS.USR_LOGIN_T;

DROP DOMAIN BIOSYS.USR_PWD_T;

/*==============================================================*/
/* Domain: BIG_TEXT_T                                           */
/*==============================================================*/
CREATE DOMAIN BIOSYS.BIG_TEXT_T AS TEXT;

COMMENT ON DOMAIN BIOSYS.BIG_TEXT_T IS
'BIG_TEXT_T';

/*==============================================================*/
/* Domain: BOOL_T                                               */
/*==============================================================*/
CREATE DOMAIN BIOSYS.BOOL_T AS BOOL;

COMMENT ON DOMAIN BIOSYS.BOOL_T IS
'BOOL_T';

/*==============================================================*/
/* Domain: CITDEP_NAME_T                                        */
/*==============================================================*/
CREATE DOMAIN BIOSYS.CITDEP_NAME_T AS VARCHAR(200);

COMMENT ON DOMAIN BIOSYS.CITDEP_NAME_T IS
'CITDEP_NAME_T';

/*==============================================================*/
/* Domain: CITY_NAME_T                                          */
/*==============================================================*/
CREATE DOMAIN BIOSYS.CITY_NAME_T AS VARCHAR(100);

COMMENT ON DOMAIN BIOSYS.CITY_NAME_T IS
'CITY_NAME_T';

/*==============================================================*/
/* Domain: COMMENTS_T                                           */
/*==============================================================*/
CREATE DOMAIN BIOSYS.COMMENTS_T AS VARCHAR(4000);

COMMENT ON DOMAIN BIOSYS.COMMENTS_T IS
'COMMENTS_T';

/*==============================================================*/
/* Domain: CONSTR_T                                             */
/*==============================================================*/
CREATE DOMAIN BIOSYS.CONSTR_T AS VARCHAR(500);

COMMENT ON DOMAIN BIOSYS.CONSTR_T IS
'CONSTR_T';

/*==============================================================*/
/* Domain: DATETIME_T                                           */
/*==============================================================*/
CREATE DOMAIN BIOSYS.DATETIME_T AS TIMESTAMP;

COMMENT ON DOMAIN BIOSYS.DATETIME_T IS
'DATETIME_T';

/*==============================================================*/
/* Domain: DATE_T                                               */
/*==============================================================*/
CREATE DOMAIN BIOSYS.DATE_T AS DATE;

COMMENT ON DOMAIN BIOSYS.DATE_T IS
'DATE_T';

/*==============================================================*/
/* Domain: DESC_T                                               */
/*==============================================================*/
CREATE DOMAIN BIOSYS.DESC_T AS VARCHAR(1000);

COMMENT ON DOMAIN BIOSYS.DESC_T IS
'DESC_T';

/*==============================================================*/
/* Domain: EMAIL_T                                              */
/*==============================================================*/
CREATE DOMAIN BIOSYS.EMAIL_T AS VARCHAR(100);

COMMENT ON DOMAIN BIOSYS.EMAIL_T IS
'EMAIL_T';

/*==============================================================*/
/* Domain: ERROR_T                                              */
/*==============================================================*/
CREATE DOMAIN BIOSYS.ERROR_T AS TEXT;

COMMENT ON DOMAIN BIOSYS.ERROR_T IS
'ERROR_T';

/*==============================================================*/
/* Domain: FIO_FAM_T                                            */
/*==============================================================*/
CREATE DOMAIN BIOSYS.FIO_FAM_T AS VARCHAR(100);

COMMENT ON DOMAIN BIOSYS.FIO_FAM_T IS
'FIO_FAM_T';

/*==============================================================*/
/* Domain: FIO_NAME_T                                           */
/*==============================================================*/
CREATE DOMAIN BIOSYS.FIO_NAME_T AS VARCHAR(100);

COMMENT ON DOMAIN BIOSYS.FIO_NAME_T IS
'FIO_NAME_T';

/*==============================================================*/
/* Domain: FIO_SNAME_T                                          */
/*==============================================================*/
CREATE DOMAIN BIOSYS.FIO_SNAME_T AS VARCHAR(100);

COMMENT ON DOMAIN BIOSYS.FIO_SNAME_T IS
'FIO_SNAME_T';

/*==============================================================*/
/* Domain: GRANT_T                                              */
/*==============================================================*/
CREATE DOMAIN BIOSYS.GRANT_T AS VARCHAR(64);

COMMENT ON DOMAIN BIOSYS.GRANT_T IS
'GRANT_T';

/*==============================================================*/
/* Domain: ID_T                                                 */
/*==============================================================*/
CREATE DOMAIN BIOSYS.ID_T AS INT8;

COMMENT ON DOMAIN BIOSYS.ID_T IS
'ID_T';

/*==============================================================*/
/* Domain: NAME_T                                               */
/*==============================================================*/
CREATE DOMAIN BIOSYS.NAME_T AS VARCHAR(200);

COMMENT ON DOMAIN BIOSYS.NAME_T IS
'NAME_T';

/*==============================================================*/
/* Domain: ORG_NAME_T                                           */
/*==============================================================*/
CREATE DOMAIN BIOSYS.ORG_NAME_T AS VARCHAR(500);

COMMENT ON DOMAIN BIOSYS.ORG_NAME_T IS
'ORG_NAME_T';

/*==============================================================*/
/* Domain: PHONE_T                                              */
/*==============================================================*/
CREATE DOMAIN BIOSYS.PHONE_T AS VARCHAR(12);

COMMENT ON DOMAIN BIOSYS.PHONE_T IS
'PHONE_T';

/*==============================================================*/
/* Domain: PORT_T                                               */
/*==============================================================*/
CREATE DOMAIN BIOSYS.PORT_T AS NUMERIC(6);

COMMENT ON DOMAIN BIOSYS.PORT_T IS
'PORT_T';

/*==============================================================*/
/* Domain: RADDR_DESC_T                                         */
/*==============================================================*/
CREATE DOMAIN BIOSYS.RADDR_DESC_T AS VARCHAR(1000);

COMMENT ON DOMAIN BIOSYS.RADDR_DESC_T IS
'RADDR_DESC_T';

/*==============================================================*/
/* Domain: RADDR_T                                              */
/*==============================================================*/
CREATE DOMAIN BIOSYS.RADDR_T AS VARCHAR(32);

COMMENT ON DOMAIN BIOSYS.RADDR_T IS
'RADDR_T';

/*==============================================================*/
/* Domain: RCLIENT_T                                            */
/*==============================================================*/
CREATE DOMAIN BIOSYS.RCLIENT_T AS VARCHAR(1000);

COMMENT ON DOMAIN BIOSYS.RCLIENT_T IS
'RCLIENT_T';

/*==============================================================*/
/* Domain: RHOST_T                                              */
/*==============================================================*/
CREATE DOMAIN BIOSYS.RHOST_T AS VARCHAR(100);

COMMENT ON DOMAIN BIOSYS.RHOST_T IS
'RHOST_T';

/*==============================================================*/
/* Domain: ROLE_T                                               */
/*==============================================================*/
CREATE DOMAIN BIOSYS.ROLE_T AS VARCHAR(64);

COMMENT ON DOMAIN BIOSYS.ROLE_T IS
'ROLE_T';

/*==============================================================*/
/* Domain: SES_STATUS_T                                         */
/*==============================================================*/
CREATE DOMAIN BIOSYS.SES_STATUS_T AS VARCHAR(200);

COMMENT ON DOMAIN BIOSYS.SES_STATUS_T IS
'SES_STATUS_T';

/*==============================================================*/
/* Domain: SWITCH$C_T                                           */
/*==============================================================*/
CREATE DOMAIN BIOSYS.SWITCH$C_T AS CHAR(1);

COMMENT ON DOMAIN BIOSYS.SWITCH$C_T IS
'SWITCH$C_T';

/*==============================================================*/
/* Domain: TABLE_NAME_T                                         */
/*==============================================================*/
CREATE DOMAIN BIOSYS.TABLE_NAME_T AS VARCHAR(30);

COMMENT ON DOMAIN BIOSYS.TABLE_NAME_T IS
'TABLE_NAME_T';

/*==============================================================*/
/* Domain: UID_T                                                */
/*==============================================================*/
CREATE DOMAIN BIOSYS.UID_T AS VARCHAR(32);

COMMENT ON DOMAIN BIOSYS.UID_T IS
'UID_T';

/*==============================================================*/
/* Domain: USR_LOGIN_T                                          */
/*==============================================================*/
CREATE DOMAIN BIOSYS.USR_LOGIN_T AS VARCHAR(64);

COMMENT ON DOMAIN BIOSYS.USR_LOGIN_T IS
'USR_LOGIN_T';

/*==============================================================*/
/* Domain: USR_PWD_T                                            */
/*==============================================================*/
CREATE DOMAIN BIOSYS.USR_PWD_T AS VARCHAR(32);

COMMENT ON DOMAIN BIOSYS.USR_PWD_T IS
'USR_PWD_T';

/*==============================================================*/
/* Table: ORG                                                   */
/*==============================================================*/
CREATE TABLE BIOSYS.ORG (
   ORG_ID               BIOSYS.ID_T                 NOT NULL,
   PRNT_ORG_ID          BIOSYS.ID_T                 NULL,
   WORKSPACE_ID         BIOSYS.ID_T                 NOT NULL,
   ANAME                BIOSYS.ORG_NAME_T           NOT NULL,
   ADESC                BIOSYS.DESC_T               NULL,
   CONSTRAINT PK_ORG PRIMARY KEY (ORG_ID)
);

COMMENT ON TABLE BIOSYS.ORG IS
'Подразделение';

COMMENT ON COLUMN ORG.ORG_ID IS
'ID подразделения';

COMMENT ON COLUMN ORG.PRNT_ORG_ID IS
'ID подразделения-родителя';

COMMENT ON COLUMN ORG.WORKSPACE_ID IS
'ID пространства';

COMMENT ON COLUMN ORG.ANAME IS
'Название подразделения';

COMMENT ON COLUMN ORG.ADESC IS
'Описание подразделения';

-- set table ownership
--alter table BIOSYS.ORG owner to BIOSYS;
/*==============================================================*/
/* Index: ORG$ORG_FK                                            */
/*==============================================================*/
CREATE  INDEX ORG$ORG_FK ON BIOSYS.ORG (
PRNT_ORG_ID
);

/*==============================================================*/
/* Index: WS$ORG_FK                                             */
/*==============================================================*/
CREATE  INDEX WS$ORG_FK ON BIOSYS.ORG (
WORKSPACE_ID
);

/*==============================================================*/
/* Table: RADDRSS                                               */
/*==============================================================*/
CREATE TABLE BIOSYS.RADDRSS (
   REM_ADDR             BIOSYS.RADDR_T              NOT NULL,
   REM_HOST             BIOSYS.RHOST_T              NOT NULL,
   ADDR_DESC            BIOSYS.RADDR_DESC_T         NULL,
   CONSTRAINT PK_RADDRSS PRIMARY KEY (REM_ADDR)
);

COMMENT ON TABLE BIOSYS.RADDRSS IS
'Справочник адресов';

COMMENT ON COLUMN RADDRSS.REM_ADDR IS
'Адрес пользователя';

COMMENT ON COLUMN RADDRSS.REM_HOST IS
'Имя хоста пользователя';

COMMENT ON COLUMN RADDRSS.ADDR_DESC IS
'Описание';

-- set table ownership
--alter table BIOSYS.RADDRSS owner to BIOSYS;
/*==============================================================*/
/* Table: SMTP$LOG                                              */
/*==============================================================*/
CREATE TABLE BIOSYS.SMTP$LOG (
   ERR_UID              BIOSYS.UID_T                NOT NULL,
   CRE_DATE             BIOSYS.DATETIME_T           NOT NULL,
   SRV_ADDR             BIOSYS.RHOST_T              NOT NULL,
   PORT_NUM             BIOSYS.PORT_T               NOT NULL,
   USR_LOGIN            BIOSYS.USR_LOGIN_T          NULL,
   USR_PWD              BIOSYS.USR_PWD_T            NULL,
   ADDR_FROM            BIOSYS.EMAIL_T              NOT NULL,
   ADDR_TO              BIOSYS.EMAIL_T              NOT NULL,
   MSG_SUBJ             BIOSYS.DESC_T               NULL,
   ERROR_TEXT           BIOSYS.ERROR_T              NOT NULL,
   CONSTRAINT PK_SMTP$LOG PRIMARY KEY (ERR_UID)
);

COMMENT ON TABLE BIOSYS.SMTP$LOG IS
'Лог ошибок отправки сообщений по SMTP';

COMMENT ON COLUMN SMTP$LOG.ERR_UID IS
'UID ошибки';

COMMENT ON COLUMN SMTP$LOG.CRE_DATE IS
'Дата/время';

COMMENT ON COLUMN SMTP$LOG.SRV_ADDR IS
'Сервер';

COMMENT ON COLUMN SMTP$LOG.PORT_NUM IS
'Порт';

COMMENT ON COLUMN SMTP$LOG.USR_LOGIN IS
'Пользователь';

COMMENT ON COLUMN SMTP$LOG.USR_PWD IS
'Пароль';

COMMENT ON COLUMN SMTP$LOG.ADDR_FROM IS
'Адрес отправителя';

COMMENT ON COLUMN SMTP$LOG.ADDR_TO IS
'Адрес получателя';

COMMENT ON COLUMN SMTP$LOG.MSG_SUBJ IS
'Тема сообщения';

COMMENT ON COLUMN SMTP$LOG.ERROR_TEXT IS
'Текст ошибки';

-- set table ownership
--alter table BIOSYS.SMTP$LOG owner to BIOSYS;
/*==============================================================*/
/* Table: UGRANT                                                */
/*==============================================================*/
CREATE TABLE BIOSYS.UGRANT (
   GRANT_UID            BIOSYS.GRANT_T              NOT NULL,
   ANAME                BIOSYS.NAME_T               NOT NULL,
   IS_SYS               BIOSYS.BOOL_T               NOT NULL,
   ADESC                BIOSYS.DESC_T               NULL,
   CONSTRAINT PK_UGRANT PRIMARY KEY (GRANT_UID)
);

COMMENT ON TABLE BIOSYS.UGRANT IS
'Разрешение';

COMMENT ON COLUMN UGRANT.GRANT_UID IS
'UID разрешения';

COMMENT ON COLUMN UGRANT.ANAME IS
'Имя разрешения';

COMMENT ON COLUMN UGRANT.IS_SYS IS
'Системное разрешения';

COMMENT ON COLUMN UGRANT.ADESC IS
'Описание роли';

-- set table ownership
--alter table BIOSYS.UGRANT owner to BIOSYS;
/*==============================================================*/
/* Table: UROLE                                                 */
/*==============================================================*/
CREATE TABLE BIOSYS.UROLE (
   ROLE_UID             BIOSYS.ROLE_T               NOT NULL,
   ANAME                BIOSYS.NAME_T               NOT NULL,
   IS_SYS               BIOSYS.BOOL_T               NOT NULL,
   ADESC                BIOSYS.DESC_T               NULL,
   CONSTRAINT PK_UROLE PRIMARY KEY (ROLE_UID)
);

COMMENT ON TABLE BIOSYS.UROLE IS
'Роль';

COMMENT ON COLUMN UROLE.ROLE_UID IS
'UID роли';

COMMENT ON COLUMN UROLE.ANAME IS
'Имя роли';

COMMENT ON COLUMN UROLE.IS_SYS IS
'Системная роль';

COMMENT ON COLUMN UROLE.ADESC IS
'Описание роли';

-- set table ownership
--alter table BIOSYS.UROLE owner to BIOSYS;
/*==============================================================*/
/* Table: USR                                                   */
/*==============================================================*/
CREATE TABLE BIOSYS.USR (
   USR_UID              BIOSYS.UID_T                NOT NULL,
   ORG_ID               BIOSYS.ID_T                 NULL,
   WORKSPACE_ID         BIOSYS.ID_T                 NOT NULL,
   USR_LOGIN            BIOSYS.USR_LOGIN_T          NOT NULL
      CONSTRAINT CKC_USR_LOGIN_USR CHECK (USR_LOGIN = LOWER(USR_LOGIN)),
   USR_PWD              BIOSYS.USR_PWD_T            NOT NULL,
   FIO_FAM              BIOSYS.FIO_FAM_T            NOT NULL,
   FIO_FNAME            BIOSYS.FIO_NAME_T           NOT NULL,
   FIO_SNAME            BIOSYS.FIO_SNAME_T          NOT NULL,
   REG_DATE             BIOSYS.DATETIME_T           NOT NULL,
   EMAIL_ADDR           BIOSYS.EMAIL_T              NOT NULL,
   USR_PHONE            BIOSYS.PHONE_T              NULL,
   CONFIRMED            BIOSYS.BOOL_T               NULL,
   GARBAGED             BIOSYS.BOOL_T               NOT NULL,
   EXTINFO              BIOSYS.DESC_T               NULL,
   CONSTRAINT PK_USR PRIMARY KEY (USR_UID)
);

COMMENT ON TABLE BIOSYS.USR IS
'Пользователи';

COMMENT ON COLUMN USR.USR_UID IS
'UID пользователя';

COMMENT ON COLUMN USR.ORG_ID IS
'ID подразделения';

COMMENT ON COLUMN USR.WORKSPACE_ID IS
'ID пространства';

COMMENT ON COLUMN USR.USR_LOGIN IS
'Логин';

COMMENT ON COLUMN USR.USR_PWD IS
'Пароль';

COMMENT ON COLUMN USR.FIO_FAM IS
'Фамилия';

COMMENT ON COLUMN USR.FIO_FNAME IS
'Имя';

COMMENT ON COLUMN USR.FIO_SNAME IS
'Отчество';

COMMENT ON COLUMN USR.REG_DATE IS
'Дата регистрации';

COMMENT ON COLUMN USR.EMAIL_ADDR IS
'e-mail';

COMMENT ON COLUMN USR.USR_PHONE IS
'Телефон';

COMMENT ON COLUMN USR.CONFIRMED IS
'Подтверждение';

COMMENT ON COLUMN USR.GARBAGED IS
'В мусор';

COMMENT ON COLUMN USR.EXTINFO IS
'Дополнительная информация';

-- set table ownership
--alter table BIOSYS.USR owner to BIOSYS;
/*==============================================================*/
/* Index: IX_USRS_GBRG                                          */
/*==============================================================*/
CREATE  INDEX IX_USRS_GBRG ON BIOSYS.USR (
GARBAGED
);

/*==============================================================*/
/* Index: ORG$USR_FK                                            */
/*==============================================================*/
CREATE  INDEX ORG$USR_FK ON BIOSYS.USR (
ORG_ID
);

/*==============================================================*/
/* Index: WS$USR_FK                                             */
/*==============================================================*/
CREATE  INDEX WS$USR_FK ON BIOSYS.USR (
WORKSPACE_ID
);

/*==============================================================*/
/* Table: USRGRNT                                               */
/*==============================================================*/
CREATE TABLE BIOSYS.USRGRNT (
   GRANT_UID            BIOSYS.ROLE_T               NOT NULL,
   USR_UID              BIOSYS.UID_T                NOT NULL,
   CONSTRAINT PK_USRGRNT PRIMARY KEY (GRANT_UID, USR_UID)
);

COMMENT ON TABLE BIOSYS.USRGRNT IS
'Разрешение пользователя';

COMMENT ON COLUMN USRGRNT.GRANT_UID IS
'UID разрешения';

COMMENT ON COLUMN USRGRNT.USR_UID IS
'UID пользователя';

-- set table ownership
--alter table BIOSYS.USRGRNT owner to BIOSYS;
/*==============================================================*/
/* Index: USR$GRANT_FK                                          */
/*==============================================================*/
CREATE  INDEX USR$GRANT_FK ON BIOSYS.USRGRNT (
USR_UID
);

/*==============================================================*/
/* Index: GRANT$USR_FK                                          */
/*==============================================================*/
CREATE  INDEX GRANT$USR_FK ON BIOSYS.USRGRNT (
GRANT_UID
);

/*==============================================================*/
/* Table: USRIN$LOG                                             */
/*==============================================================*/
CREATE TABLE BIOSYS.USRIN$LOG (
   REC_ID               BIOSYS.ID_T                 NOT NULL,
   REM_ADDR             BIOSYS.RADDR_T              NOT NULL,
   USR_LOGIN            BIOSYS.USR_LOGIN_T          NOT NULL,
   SESSION_ID           BIOSYS.UID_T                NOT NULL,
   REM_CLIENT           BIOSYS.RCLIENT_T            NOT NULL,
   ASTATUS              BIOSYS.SES_STATUS_T         NOT NULL,
   USRIN_DATE           BIOSYS.DATETIME_T           NOT NULL,
   CONSTRAINT PK_USRIN$LOG PRIMARY KEY (REC_ID)
);

COMMENT ON TABLE BIOSYS.USRIN$LOG IS
'Лог входов в систему';

COMMENT ON COLUMN USRIN$LOG.REC_ID IS
'ID записи';

COMMENT ON COLUMN USRIN$LOG.REM_ADDR IS
'Адрес пользователя';

COMMENT ON COLUMN USRIN$LOG.USR_LOGIN IS
'Логин';

COMMENT ON COLUMN USRIN$LOG.SESSION_ID IS
'ID Сессии';

COMMENT ON COLUMN USRIN$LOG.REM_CLIENT IS
'Клиент пользователя';

COMMENT ON COLUMN USRIN$LOG.ASTATUS IS
'Статус';

COMMENT ON COLUMN USRIN$LOG.USRIN_DATE IS
'Дата входа';

-- set table ownership
--alter table BIOSYS.USRIN$LOG owner to BIOSYS;
/*==============================================================*/
/* Index: ADDR$LOG_FK                                           */
/*==============================================================*/
CREATE  INDEX ADDR$LOG_FK ON BIOSYS.USRIN$LOG (
REM_ADDR
);

/*==============================================================*/
/* Table: USRLOCK                                               */
/*==============================================================*/
CREATE TABLE BIOSYS.USRLOCK (
   LOCK_ID              BIOSYS.ID_T                 NOT NULL,
   USR_UID              BIOSYS.UID_T                NOT NULL,
   LOCK_TYPE            BIOSYS.SWITCH$C_T           NOT NULL DEFAULT '0'
      CONSTRAINT CKC_LOCK_TYPE_USRLOCK CHECK (LOCK_TYPE IN ('0','1')),
   CREATED              BIOSYS.DATETIME_T           NOT NULL,
   FROM_POINT           BIOSYS.DATETIME_T           NOT NULL,
   TO_POINT             BIOSYS.DATETIME_T           NULL,
   COMMENTS             BIOSYS.COMMENTS_T           NULL,
   DELETED              BIOSYS.BOOL_T               NOT NULL,
   CONSTRAINT PK_USRLOCK PRIMARY KEY (LOCK_ID)
);

COMMENT ON TABLE BIOSYS.USRLOCK IS
'Блокировка пользователя. Данные пользователи блокируются на указанный период';

COMMENT ON COLUMN USRLOCK.LOCK_ID IS
'ID блокировки';

COMMENT ON COLUMN USRLOCK.USR_UID IS
'UID пользователя';

COMMENT ON COLUMN USRLOCK.LOCK_TYPE IS
'Тип блокировки (0-ручная; 1-автоматическая)';

COMMENT ON COLUMN USRLOCK.CREATED IS
'Дата/Время создания';

COMMENT ON COLUMN USRLOCK.FROM_POINT IS
'Дата/Время начала блокировки';

COMMENT ON COLUMN USRLOCK.TO_POINT IS
'Дата/Время окончания блокировки';

COMMENT ON COLUMN USRLOCK.COMMENTS IS
'Комментарии';

COMMENT ON COLUMN USRLOCK.DELETED IS
'Блокировка удалена';

-- set table ownership
--alter table BIOSYS.USRLOCK owner to BIOSYS;
/*==============================================================*/
/* Index: USR$LCK_FK                                            */
/*==============================================================*/
CREATE  INDEX USR$LCK_FK ON BIOSYS.USRLOCK (
USR_UID
);

/*==============================================================*/
/* Table: USRLOG                                                */
/*==============================================================*/
CREATE TABLE BIOSYS.USRLOG (
   USRLOG_ID            BIOSYS.ID_T                 NOT NULL,
   USR_UID              BIOSYS.UID_T                NOT NULL,
   IOBJ_CD              BIOSYS.TABLE_NAME_T         NOT NULL,
   IOBJ_ID              BIOSYS.UID_T                NOT NULL,
   ACTION_TEXT          BIOSYS.BIG_TEXT_T           NOT NULL,
   ACT_DATE             BIOSYS.DATETIME_T           NOT NULL,
   IOBJ_MASTER_CD       BIOSYS.TABLE_NAME_T         NULL,
   IOBJ_MASTER_ID       BIOSYS.UID_T                NULL,
   CONSTRAINT PK_USRLOG PRIMARY KEY (USRLOG_ID)
);

COMMENT ON TABLE BIOSYS.USRLOG IS
'Протокол действий пользователя';

COMMENT ON COLUMN USRLOG.USRLOG_ID IS
'ID записи';

COMMENT ON COLUMN USRLOG.USR_UID IS
'UID пользователя';

COMMENT ON COLUMN USRLOG.IOBJ_CD IS
'Код измененного объекта (Имя таблицы)';

COMMENT ON COLUMN USRLOG.IOBJ_ID IS
'ID объекта';

COMMENT ON COLUMN USRLOG.ACTION_TEXT IS
'Описание действия';

COMMENT ON COLUMN USRLOG.ACT_DATE IS
'Дата и время действия';

COMMENT ON COLUMN USRLOG.IOBJ_MASTER_CD IS
'Код измененного master-объекта (Имя таблицы)';

COMMENT ON COLUMN USRLOG.IOBJ_MASTER_ID IS
'ID master-объекта';

-- set table ownership
--alter table BIOSYS.USRLOG owner to BIOSYS;
/*==============================================================*/
/* Index: USR$LOG_FK                                            */
/*==============================================================*/
CREATE  INDEX USR$LOG_FK ON BIOSYS.USRLOG (
USR_UID
);

/*==============================================================*/
/* Table: USRRLE                                                */
/*==============================================================*/
CREATE TABLE BIOSYS.USRRLE (
   ROLE_UID             BIOSYS.ROLE_T               NOT NULL,
   USR_UID              BIOSYS.UID_T                NOT NULL,
   CONSTRAINT PK_USRRLE PRIMARY KEY (ROLE_UID, USR_UID)
);

COMMENT ON TABLE BIOSYS.USRRLE IS
'Роль пользователя';

COMMENT ON COLUMN USRRLE.ROLE_UID IS
'UID роли';

COMMENT ON COLUMN USRRLE.USR_UID IS
'UID пользователя';

-- set table ownership
--alter table BIOSYS.USRRLE owner to BIOSYS;
/*==============================================================*/
/* Index: ROLE$USR_FK                                           */
/*==============================================================*/
CREATE  INDEX ROLE$USR_FK ON BIOSYS.USRRLE (
ROLE_UID
);

/*==============================================================*/
/* Index: USR$ROLE_FK                                           */
/*==============================================================*/
CREATE  INDEX USR$ROLE_FK ON BIOSYS.USRRLE (
USR_UID
);

/*==============================================================*/
/* Table: UWORKSPACE                                            */
/*==============================================================*/
CREATE TABLE BIOSYS.UWORKSPACE (
   WORKSPACE_ID         BIOSYS.ID_T                 NOT NULL,
   USR_UID              BIOSYS.UID_T                NULL,
   ANAME                BIOSYS.NAME_T               NOT NULL,
   ADESC                BIOSYS.DESC_T               NULL,
   CONSTRAINT PK_UWORKSPACE PRIMARY KEY (WORKSPACE_ID)
);

COMMENT ON TABLE BIOSYS.UWORKSPACE IS
'Пространство';

COMMENT ON COLUMN UWORKSPACE.WORKSPACE_ID IS
'ID пространства';

COMMENT ON COLUMN UWORKSPACE.USR_UID IS
'UID пользователя-владельца';

COMMENT ON COLUMN UWORKSPACE.ANAME IS
'Имя пространства';

COMMENT ON COLUMN UWORKSPACE.ADESC IS
'Описание пространства';

-- set table ownership
--alter table BIOSYS.UWORKSPACE owner to BIOSYS;
/*==============================================================*/
/* Index: USR$WS_FK                                             */
/*==============================================================*/
CREATE  INDEX USR$WS_FK ON BIOSYS.UWORKSPACE (
USR_UID
);

ALTER TABLE ORG
   ADD CONSTRAINT FK_ORG_ORG FOREIGN KEY (PRNT_ORG_ID)
      REFERENCES ORG (ORG_ID)
      ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ORG
   ADD CONSTRAINT FK_ORG_UWORKSPACE FOREIGN KEY (WORKSPACE_ID)
      REFERENCES UWORKSPACE (WORKSPACE_ID)
      ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE USR
   ADD CONSTRAINT FK_USR_ORG FOREIGN KEY (ORG_ID)
      REFERENCES ORG (ORG_ID)
      ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE USR
   ADD CONSTRAINT FK_USR_UWORKSPACE FOREIGN KEY (WORKSPACE_ID)
      REFERENCES UWORKSPACE (WORKSPACE_ID)
      ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE USRGRNT
   ADD CONSTRAINT FK_USRGRNT_UGRANT FOREIGN KEY (GRANT_UID)
      REFERENCES UGRANT (GRANT_UID)
      ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE USRGRNT
   ADD CONSTRAINT FK_USRGRNT_USR FOREIGN KEY (USR_UID)
      REFERENCES USR (USR_UID)
      ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE USRIN$LOG
   ADD CONSTRAINT FK_USRIN$LOG_RADDRSS FOREIGN KEY (REM_ADDR)
      REFERENCES RADDRSS (REM_ADDR)
      ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE USRLOCK
   ADD CONSTRAINT FK_USRLOCK_USR FOREIGN KEY (USR_UID)
      REFERENCES USR (USR_UID)
      ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE USRLOG
   ADD CONSTRAINT FK_USRLOG_USR FOREIGN KEY (USR_UID)
      REFERENCES USR (USR_UID)
      ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE USRRLE
   ADD CONSTRAINT FK_USRRLE_UROLE FOREIGN KEY (ROLE_UID)
      REFERENCES UROLE (ROLE_UID)
      ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE USRRLE
   ADD CONSTRAINT FK_USRRLE_USR FOREIGN KEY (USR_UID)
      REFERENCES USR (USR_UID)
      ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE UWORKSPACE
   ADD CONSTRAINT FK_UWORKSPACE_USR FOREIGN KEY (USR_UID)
      REFERENCES USR (USR_UID)
      ON DELETE RESTRICT ON UPDATE RESTRICT;

