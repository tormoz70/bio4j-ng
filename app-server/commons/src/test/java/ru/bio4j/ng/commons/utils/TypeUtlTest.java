package ru.bio4j.ng.commons.utils;

import java.util.Calendar;
import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.converter.Types;

public class TypeUtlTest {

	@Test
	public void parse() {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(2012, (12-1), 20, 15, 11, 50);
		System.out.println("calendar.getTime(): "+calendar.getTime());
		Date testDate = Types.parse("2012.12.20-15:11:50", "yyyy.MM.dd-HH:mm:ss");
		Assert.assertEquals(testDate, calendar.getTime());
	}
}
