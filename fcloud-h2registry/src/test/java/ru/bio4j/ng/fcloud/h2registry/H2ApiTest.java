package ru.bio4j.ng.fcloud.h2registry;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.FileSpec;

import java.sql.Connection;
import java.util.Date;

public class H2ApiTest {

    FCloudDBApi fCloudDBApi = FCloudDBApi.getInstance();

    @BeforeTest
    public void initTests() {
    }

    @Test(priority = 0)
    public void getConnectionTest() throws Exception {
        Connection conn = H2Api.getInstance().getConnection("jdbc:h2:d:/test", "sa", "sa");
        fCloudDBApi.dropDB(conn);
        fCloudDBApi.initDB(conn);
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

        fCloudDBApi.storeFileSpec(conn, fileSpec);
        Assert.assertTrue(true);
    }

    @Test(priority = 2, enabled = true)
    public void readFileDescTest() throws Exception {
        Connection conn = H2Api.getInstance().getConnection("jdbc:h2:d:/test", "sa", "sa");
        FileSpec fileSpec = fCloudDBApi.readFileSpec(conn, "test-file-uid");
        Assert.assertEquals(fileSpec.getFileNameOrig(), "test-file-name");
    }

    @Test(priority = 3, enabled = true)
    public void removeFileDescTest() throws Exception {
        Connection conn = H2Api.getInstance().getConnection("jdbc:h2:d:/test", "sa", "sa");
        fCloudDBApi.removeFileSpec(conn, "test-file-uid");
    }

}
