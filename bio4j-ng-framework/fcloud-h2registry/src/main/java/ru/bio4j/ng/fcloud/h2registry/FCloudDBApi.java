package ru.bio4j.ng.fcloud.h2registry;

import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.FileSpec;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FCloudDBApi {

    public static void dropDB(final Connection conn, final String tableName) throws Exception {
        H2Api.getInstance().execSql(conn,String.format("DROP TABLE IF EXISTS %s", tableName.toUpperCase()), null);
    }

    public static void initDB(final Connection conn, final String tableName) throws Exception {
        H2Api.getInstance().execSql(conn, String.format("CREATE TABLE IF NOT EXISTS %s(\n" +
                "   FILEUID              VARCHAR(32)          NOT NULL,\n" +
                "   USR_UID              VARCHAR(32)          NOT NULL,\n" +
                "   USR_NAME             VARCHAR(1000)        NOT NULL,\n" +
                "   USR_ORG_UID          VARCHAR(1000)        NOT NULL,\n" +
                "   FCLOUD_ID            BIGINT               AUTO_INCREMENT,\n" +
                "   FILENAMEORIG         VARCHAR(1000)        NOT NULL,\n" +
                "   MD5                  VARCHAR(100)         NOT NULL,\n" +
                "   BSIZE                BIGINT               NOT NULL,\n" +
                "   RMTIPADDR            VARCHAR(100)         NOT NULL,\n" +
                "   FLDATE               TIMESTAMP            NOT NULL,\n" +
                "   LDDATE               TIMESTAMP            NOT NULL,\n" +
                "   MEDIATYPE            VARCHAR(500)         NOT NULL,\n" +
                "   UPLOADUID            VARCHAR(500)         NOT NULL,\n" +
                "   ADESC                VARCHAR(32000)       NOT NULL,\n" +
                "   CONSTRAINT PK_FCLOUD PRIMARY KEY (FILEUID)\n" +
                ")", tableName.toUpperCase()), null);
    }

    public static FileSpec storeFileSpec(final Connection conn, final FileSpec fileSpec, final User usr) throws Exception {
        String sql = "INSERT INTO FCLOUD(FILEUID, USR_UID, USR_NAME, USR_ORG_UID, FILENAMEORIG, MD5, BSIZE, RMTIPADDR, FLDATE, LDDATE, MEDIATYPE, UPLOADUID, ADESC)\n"+
        "VALUES(lower(:fileuid), lower(:usr_uid), lower(:usr_name), lower(:usr_org_uid), lower(:filenameorig), lower(:md5), :bsize, :rmtipaddr, :fldate, :lddate, lower(:mediatype), lower(:uploaduid), :adesc)";
        List<Param> prms = new ArrayList<>();
        fileSpec.setOwnerUserUid(usr.getInnerUid());
        Paramus.setParamValue(prms, "fileuid", fileSpec.getFileUUID());
        Paramus.setParamValue(prms, "usr_uid", fileSpec.getOwnerUserUid());
        Paramus.setParamValue(prms, "usr_name", usr.getLogin());
        Paramus.setParamValue(prms, "usr_org_uid", usr.getOrgId());
        Paramus.setParamValue(prms, "filenameorig", fileSpec.getFileNameOrig());
        Paramus.setParamValue(prms, "md5", fileSpec.getMd5());
        Paramus.setParamValue(prms, "bsize", fileSpec.getFileSize());
        Paramus.setParamValue(prms, "rmtipaddr", fileSpec.getRemoteIpAddress());
        Paramus.setParamValue(prms, "fldate", fileSpec.getFileDatetime());
        Paramus.setParamValue(prms, "lddate", fileSpec.getCreDatetime());
        Paramus.setParamValue(prms, "mediatype", fileSpec.getContentType());
        Paramus.setParamValue(prms, "uploaduid", fileSpec.getUploadUID());
        Paramus.setParamValue(prms, "adesc", fileSpec.getAdesc());
        H2Api.getInstance().execSql(conn, sql, prms);
        return fileSpec;
    }

    private static <T> T getColumnValue(final ResultSet resultSet, final ResultSetMetaData metaData, final String columnName, Class<T> type) throws Exception {
        int colCount = metaData.getColumnCount();
        String colName;
        for (int i=1; i<=colCount; i++) {
            colName = metaData.getColumnName(i);
            if (colName.equalsIgnoreCase(columnName))
                return Converter.toType(resultSet.getObject(i), type);
        }
        return null;
    }


    private static FileSpec readFileSpec(final ResultSet resultSet, final String fileUid) throws Exception {
        final FileSpec rslt = new FileSpec();
        final ResultSetMetaData metaData = resultSet.getMetaData();
        rslt.setFileUUID(fileUid);
        rslt.setOwnerUserUid(getColumnValue(resultSet, metaData, "USR_UID", String.class));
        rslt.setFileNameOrig(getColumnValue(resultSet, metaData, "FILENAMEORIG", String.class));
        rslt.setMd5(getColumnValue(resultSet, metaData, "MD5", String.class));
        rslt.setFileSize(getColumnValue(resultSet, metaData, "BSIZE", int.class));
        rslt.setRemoteIpAddress(getColumnValue(resultSet, metaData, "RMTIPADDR", String.class));
        rslt.setFileDatetime(getColumnValue(resultSet, metaData, "FLDATE", Date.class));
        rslt.setCreDatetime(getColumnValue(resultSet, metaData, "LDDATE", Date.class));
        rslt.setContentType(getColumnValue(resultSet, metaData, "MEDIATYPE", String.class));
        rslt.setUploadUID(getColumnValue(resultSet, metaData, "UPLOADUID", String.class));
        rslt.setAdesc(getColumnValue(resultSet, metaData, "ADESC", String.class));
        return rslt;
    }

    public static FileSpec readFileSpec(final Connection conn, final String fileUid, final User usr) throws Exception {
        String sql = "SELECT FILEUID, USR_UID, USR_NAME, USR_ORG_UID, FILENAMEORIG, MD5, BSIZE, RMTIPADDR, FLDATE, LDDATE, MEDIATYPE, UPLOADUID, ADESC FROM FCLOUD \n" +
                "WHERE FILEUID = lower(:fileuid)";
        List<Param> prms = new ArrayList<>();
        Paramus.setParamValue(prms, "fileuid", fileUid);
        try (ResultSet resultSet = H2Api.getInstance().openSql(conn, sql, prms)) {
            if (resultSet.next()) {
                final FileSpec rslt = readFileSpec(resultSet, fileUid);
                return rslt;
            }
        }
        return null;
    }

    public static boolean removeFileSpec(final Connection conn, final String fileUid, final User usr) throws Exception {
        String sql = "DELETE FROM FCLOUD WHERE FILEUID = lower(:fileuid)";
        List<Param> prms = new ArrayList<>();
        Paramus.setParamValue(prms, "fileuid", fileUid);
        return H2Api.getInstance().execSql(conn, sql, prms);
    }
}
