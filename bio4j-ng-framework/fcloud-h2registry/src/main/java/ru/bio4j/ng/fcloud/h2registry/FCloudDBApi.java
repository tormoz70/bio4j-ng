package ru.bio4j.ng.fcloud.h2registry;

import ru.bio4j.ng.model.transport.Param;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

public class FCloudDBApi {
    public static void initDB(final Connection conn) throws Exception {
        H2Api.getInstance().execSql(conn,"CREATE TABLE IF NOT EXISTS FCLOUD(\n" +
                "   FILESUID             VARCHAR(32)          NOT NULL,\n" +
                "   PERSON_UID           VARCHAR(32)          NOT NULL,\n" +
                "   FCLOUD_ID            BIGINT               AUTO_INCREMENT,\n" +
                "   FILENAMEORIG         VARCHAR(1000)        NOT NULL,\n" +
                "   MD5                  VARCHAR(100)         NOT NULL,\n" +
                "   BSIZE                BIGINT               NOT NULL,\n" +
                "   RMTIPADDR            VARCHAR(100)         NOT NULL,\n" +
                "   FLDATE               TIMESTAMP            NOT NULL,\n" +
                "   LDDATE               TIMESTAMP            NOT NULL,\n" +
                "   MEDIATYPE            VARCHAR(500)         NOT NULL,\n" +
                "   UPLOADUID            VARCHAR(500)         NOT NULL,\n" +
                "   CONSTRAINT PK_FCLOUD PRIMARY KEY (FILESUID)\n" +
                ");", null);
    }
}
