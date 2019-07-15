package ru.bio4j.ng.commons.utils;

import com.rabbitmq.client.Connection;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 * User: ayrat
 * Date: 13.12.13
 * Time: 1:17
 * To change this template use File | Settings | File Templates.
 */
public class amqsTest {

    @Test(enabled = false)
    public void testSend() throws Exception {
        try(Connection conn = Amqs.createConnection(
                "mustang.rmq.cloudamqp.com", "iwacygav", "4K8knDCFH2YHrLHfw1zvF3T2ps0O8x_j", "iwacygav", 5672
        )) {
            Amqs.postMessage(conn, "ekb-exchange", "packet-to-process", "test-msg");
        }
    }

    @Test(enabled = false)
    public void testGetNext() throws Exception {
        try(Connection conn = Amqs.createConnection(
                "mustang.rmq.cloudamqp.com", "iwacygav", "4K8knDCFH2YHrLHfw1zvF3T2ps0O8x_j", "iwacygav", 5672
        )) {
            String msg = Amqs.getMessage(conn, "ekb-exchange", "ekb-packets-to-process", "packet-to-process", false);
            System.out.println(msg);
        }
    }

}
