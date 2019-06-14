package ru.bio4j.ng.commons.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 * User: ayrat
 * Date: 13.12.13
 * Time: 1:17
 * To change this template use File | Settings | File Templates.
 */
public class emailsTest {

    @Test(enabled = true)
    public void testSend() throws Exception {
        Emails.sendPlain("ah@fond-kino.ru", "Тестовое сообщение bio4j", "Тестовое сообщение bio4j - ТЕЛО");
        Assert.assertTrue(true);
    }

}
