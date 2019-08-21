package ru.bio4j.ng.fcloud.h2registry;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.FileSpec;

import java.sql.Connection;
import java.util.Date;

public class H2ApiTest {

    FCloudDBApi fCloudDBApi = FCloudDBApi.getInstance();

    @BeforeTest
    public void initTests() throws Exception {
        H2Api.getInstance().startServer("9089", "d:/tmp/ekb-uploader/fcloud-registry", "sa", "sa");
    }

    @AfterTest
    public void deinitTests() throws Exception {
        H2Api.getInstance().shutdownServer();
    }


    @Test(enabled = true)
    public void conn5() throws Exception {
        Connection conn = H2Api.getInstance().getLocalConnection();

        fCloudDBApi.dropDB(conn);
        fCloudDBApi.initDB(conn);

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
        //Thread.sleep(30_000);
    }


}
