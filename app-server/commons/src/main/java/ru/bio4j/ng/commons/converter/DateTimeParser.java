package ru.bio4j.ng.commons.converter;

import ru.bio4j.ng.commons.utils.Regexs;
import ru.bio4j.ng.commons.utils.Strings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ayrat
 * 
 * Класс для преобразований из строки в дату.
 * 
 */
public class DateTimeParser {

	/**
	 * Экземпляр класса.
	 */
	private static DateTimeParser instance;

	public static DateTimeParser getInstance() {
		if (instance == null)
			synchronized (DateTimeParser.class) {
				if (instance == null)
					createDateTimeParser();
			}
		return instance;
	}

	private List<DateTimeParserTemplate> templates;

	private static void createDateTimeParser() {
		instance = new DateTimeParser();
		instance.templates = new ArrayList<DateTimeParserTemplate>();
		instance.templates.add(new DateTimeParserTemplate("yyyyMMddHHmmss", "^[012]\\d{3}[01]\\d{1}[0123]\\d{1}[012]\\d{1}[012345]\\d{1}[012345]\\d{1}$"));
		instance.templates.add(new DateTimeParserTemplate("dd.MM.yyyy HH:mm:ss", "^[0123]\\d{1}[.][01]\\d{1}[.][012]\\d{3}\\s[012]\\d{1}[:][012345]\\d{1}[:][012345]\\d{1}$"));
		instance.templates.add(new DateTimeParserTemplate("yyyy.MM.dd HH:mm:ss", "^[012]\\d{3}[.][01]\\d{1}[.][0123]\\d{1}\\s[012]\\d{1}[:][012345]\\d{1}[:][012345]\\d{1}$"));
		instance.templates.add(new DateTimeParserTemplate("yyyy.MM.dd", "^[012]\\d{3}[.][01]\\d{1}[.][0123]\\d{1}$"));
		instance.templates.add(new DateTimeParserTemplate("dd.MM.yyyy", "^[0123]\\d{1}[.][01]\\d{1}[.][012]\\d{3}$"));
		instance.templates.add(new DateTimeParserTemplate("yyyyMMdd", "^[012]\\d{3}[01]\\d{1}[0123]\\d{1}$"));
		instance.templates.add(new DateTimeParserTemplate("yyyyMM", "^[012]\\d{3}[01]\\d{1}$"));
		instance.templates.add(new DateTimeParserTemplate("ddMMyyyy", "^[0123]\\d{1}[01]\\d{1}[012]\\d{3}$"));
		instance.templates.add(new DateTimeParserTemplate("yyyy-MM-dd'T'HH:mm:ss", "^[012]\\d{3}[-][01]\\d{1}[-][0123]\\d{1}[T][012]\\d{1}[:][012345]\\d{1}[:][012345]\\d{1}$"));
		instance.templates.add(new DateTimeParserTemplate("yyyy-MM-dd HH:mm:ss", "^[012]\\d{3}[-][01]\\d{1}[-][0123]\\d{1}\\s[012]\\d{1}[:][012345]\\d{1}[:][012345]\\d{1}$"));
		instance.templates.add(new DateTimeParserTemplate("dd.MM.yyyy H:mm:ss", "^[0123]\\d{1}[.][01]\\d{1}[.][012]\\d{3}\\s[012]?\\d{1}[:][012345]\\d{1}[:][012345]\\d{1}$"));
		instance.templates.add(new DateTimeParserTemplate("yyyy.MM.dd HH:mm", "^[012]\\d{3}[.][01]\\d{1}[.][0123]\\d{1}\\s[012]\\d{1}[:][012345]\\d{1}$"));
		instance.templates.add(new DateTimeParserTemplate("yyyyMMdd HH:mm:ss", "^[012]\\d{3}[01]\\d{1}[0123]\\d{1}\\s[012]\\d{1}[:][012345]\\d{1}[:][012345]\\d{1}$"));
		instance.templates.add(new DateTimeParserTemplate("yyyyMMdd HH:mm", "^[012]\\d{3}[01]\\d{1}[0123]\\d{1}\\s[012]\\d{1}[:][012345]\\d{1}$"));
		instance.templates.add(new DateTimeParserTemplate("dd.MM.yyyy H:mm", "^[0123]\\d{1}[.][01]\\d{1}[.][012]\\d{3}\\s[012]?\\d{1}[:][012345]\\d{1}$"));
	}

	private DateTimeParser() {
	}

	public String detectFormat(String datetimeValue) {

		for (DateTimeParserTemplate f : this.templates) {
			if (Regexs.match(datetimeValue, f.getRegex(), true).matches())
				return f.getFormat();
		}
		return null;
	}

	public Date pars(String value, String format) throws DateParseException {
		if (!Strings.isNullOrEmpty(value)) {
			if (value.toUpperCase().equals("NOW"))
				return new Date();
			if (value.toUpperCase().equals("MAX"))
				return Types.maxValue();
			if (value.toUpperCase().equals("MIN"))
				return Types.minValue();
			try {
				return Types.parse(value, format);
			} catch (Exception ex) {
				throw new DateParseException("Ошибка разбора даты. Параметры: (" + value + ", " + format + "). Сообщение: " + ex.toString());
			}
		}
		return Types.minValue();
	}

	public Date pars(String value) throws DateParseException {
		String datetimeFormat = detectFormat(value);
		if (Strings.isNullOrEmpty(datetimeFormat))
			throw new DateParseException("Не верная дата: [" + value + "]. Невозможно определить формат даты.");
		return pars(value, datetimeFormat);
	}

}
