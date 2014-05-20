package ru.bio4j.ng.commons.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

public class StringUtlTest {

	@Test
	public void appendStr() {
		String line = null;
		line = Strings.append(line, "qwe1", "|");
		Assert.assertEquals(line, "qwe1");
		line = Strings.append(line, "qwe2", "|");
		Assert.assertEquals(line, "qwe1|qwe2");
		line = Strings.append(line, "", "|");
		Assert.assertEquals(line, "qwe1|qwe2|");
		line = Strings.append(line, null, "|");
		Assert.assertEquals(line, "qwe1|qwe2||");
		line = Strings.append(line, "asd", "|");
		Assert.assertEquals(line, "qwe1|qwe2|||asd");
	}

	@Test
	public void compareStrings() {
		Assert.assertEquals(Strings.compare(null, null, false), true);
		Assert.assertEquals(Strings.compare("", null, false), false);
		Assert.assertEquals(Strings.compare(null, "", false), false);
		Assert.assertEquals(Strings.compare("asd", "asd", false), true);
		Assert.assertEquals(Strings.compare("asd", "ASD", false), false);
		Assert.assertEquals(Strings.compare("asd", "ASD", true), true);
	}

	@Test
	public void split() {
		//System.out.println(String.format("UPPER(%%%s%%)", "FIELD1"));
		//System.out.println(String.format("UPPER(%s) LIKE UPPER('\u0025'||:%s||'\u0025')", "FIELD1", "test"));
		//System.out.println(String.format("%s IS NULL", "fff1", "fff2"));
		String[] strs = Strings.split("qwe,asd,zxc", ",");
		Assert.assertEquals(strs[0], "qwe");
		Assert.assertEquals(strs[1], "asd");
		Assert.assertEquals(strs[2], "zxc");
	}

	@Test
	public void isNullOrEmpty() {
		Assert.assertEquals(Strings.isNullOrEmpty(null), true);
		Assert.assertEquals(Strings.isNullOrEmpty(""), true);
		Assert.assertEquals(Strings.isNullOrEmpty("qwe"), false);
	}

    @Test
    public void combineArrayTest() {
        int[] a = {1,2,3,4,5};
        String lst = Strings.combineArray(a, ";");
        Assert.assertEquals(lst, "1;2;3;4;5");
    }

    @Test
    public void trimTest() {
        String s = Strings.trim("     [\"'asd fgh'\"] ", " []\"'");
        Assert.assertEquals(s, "asd fgh");
    }

    @Test(enabled = true)
    public void getFirstItemTest() throws Exception {
        String list1 = "qwe1/qwe2";
        Assert.assertEquals(Strings.getFirstItem(list1, "/"), "qwe1");
        String list2 = "qwe1";
        Assert.assertEquals(Strings.getFirstItem(list2, "/"), "qwe1");
        String list3 = "";
        Assert.assertEquals(Strings.getFirstItem(list3, "/"), "");
    }
    @Test(enabled = true)
    public void cutFirstItemTest() throws Exception {
        String list1 = "qwe1/qwe2/qwe3";
        Assert.assertEquals(Strings.cutFirstItem(list1, "/"), "qwe2/qwe3");
        String list2 = "qwe1";
        Assert.assertEquals(Strings.cutFirstItem(list2, "/"), null);
        String list3 = "";
        Assert.assertEquals(Strings.cutFirstItem(list3, "/"), null);
    }

}
