package ru.bio4j.ng.fcloud.h2registry;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.FileSpec;

import java.sql.Connection;
import java.util.Date;

public class H2ApiTest {

    private User usr;

    @BeforeTest
    public void initTests() {
        usr = new User();
        usr.setRemoteClient("tester-client");
        usr.setInnerUid("tester-user-uid");
        usr.setOrgId("tester-user-org-uid");
        usr.setLogin("tester-user-login");
    }

    @Test(priority = 0)
    public void getConnectionTest() throws Exception {
        Connection conn = H2Api.getInstance().getConnection("jdbc:h2:d:/test", "sa", "sa");
        FCloudDBApi.dropDB(conn, "fcloud");
        FCloudDBApi.initDB(conn, "fcloud");
    }

    @Test(priority = 1, enabled = true)
    public void storeFileDescTest() throws Exception {
        Connection conn = H2Api.getInstance().getConnection("jdbc:h2:d:/test", "sa", "sa");

        FileSpec fileSpec = new FileSpec();
        fileSpec.setFileUUID("test-file-uid");
        fileSpec.setFileNameOrig("test-file-name");
        fileSpec.setMd5("test-file-md5");
        fileSpec.setFileSize(100);
        fileSpec.setRemoteIpAddress("localhost");
        fileSpec.setFileDatetime(new Date());
        fileSpec.setCreDatetime(new Date());
        fileSpec.setContentType("text");
        fileSpec.setUploadUID("test-file-upload-uid");
        fileSpec.setAdesc("test");

        FileSpec rslt = FCloudDBApi.storeFileSpec(conn, fileSpec, usr);
        Assert.assertEquals(rslt.getOwnerUserUid(), usr.getInnerUid());
    }

    @Test(priority = 2, enabled = true)
    public void readFileDescTest() throws Exception {
        Connection conn = H2Api.getInstance().getConnection("jdbc:h2:d:/test", "sa", "sa");
        FileSpec fileSpec = FCloudDBApi.readFileSpec(conn, "test-file-uid", usr);
        Assert.assertEquals(fileSpec.getFileNameOrig(), "test-file-name");
    }

    @Test(priority = 3, enabled = true)
    public void removeFileDescTest() throws Exception {
        Connection conn = H2Api.getInstance().getConnection("jdbc:h2:d:/test", "sa", "sa");
        FCloudDBApi.removeFileSpec(conn, "test-file-uid", usr);
    }

}
