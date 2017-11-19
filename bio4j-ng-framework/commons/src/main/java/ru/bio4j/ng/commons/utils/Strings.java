package ru.bio4j.ng.commons.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;


/**
 * @author ayrat
 *	Утилиты для работы с String
 *
 */
public class Strings {
	/**
	 * Проверяет строку на null или пустую (length == 0)
	 * @param str 
	 * @return Если null или пусто (length == 0), то возвращает true
	 */
	public static boolean isNullOrEmpty(String str) {
		return (str == null) || (str.length() == 0);
	}

	/**
	 * Добавляет к строке подстроку через разделитель
	 * @param line - строка к которой надо добавить
	 * @param str - то что нужно добавить к line 
	 * @param delimiter - разделитель, через который надо добавить str к line  
	 * @return результирующий текст
	 */
	public static String append(String line, String str, String delimiter) {
		if (isNullOrEmpty(line))
			line = ((str == null) ? "" : str);
		else
			line += delimiter + ((str == null) ? "" : str);
		return line;
	}

    public static void append(StringBuilder stringBuilder, String str, String delimiter) {
        if (stringBuilder.length() == 0)
            stringBuilder.append((str == null) ? "" : str);
        else
            stringBuilder.append(delimiter + ((str == null) ? "" : str));
    }

	/**
	 * Разбивает строку на подстроки с заданными разделителями
	 * @param str - строка, которую необходимо разбить
	 * @param delimiters - список возможных разделителей
	 * @return - массив подстрок
	 */
	public static String[] split(String str, String ... delimiters) {
		if (!isNullOrEmpty(str)) {
			if ((delimiters != null) && (delimiters.length > 0)) {
				String line = str;
				String dlmtr = null;
				if (delimiters.length > 1) {
					final String csDlmtrPG = "#inner_pg_delimeter_str#";
					for (String delimeter : delimiters)
						line = line.replace(delimeter, csDlmtrPG);
                    dlmtr = csDlmtrPG;
                } else
					dlmtr = delimiters[0];
				List<String> lst = new ArrayList<String>();
				int item_bgn = 0;
				while (item_bgn <= line.length()) {
					String line2Add = "";
					int dlmtr_pos = line.indexOf(dlmtr, item_bgn);
					if (dlmtr_pos == -1)
						dlmtr_pos = line.length();
					line2Add = line.substring(item_bgn, dlmtr_pos);
					lst.add(line2Add);
					item_bgn += line2Add.length() + dlmtr.length();
				}
				return lst.toArray(new String[lst.size()]);
			} else
				return new String[] { str };
		} else
			return new String[] {};
	}

    public static String[] split(String str, char ... delimiters) {
        String[] d = new String[delimiters.length];
        for(int i=0; i<delimiters.length; i++){
            d[i] = ""+delimiters[i];
        }
        return split(str, d);
    }

    public static String getFirstItem(String list, String delimiter) {
        if(isNullOrEmpty(list))
            return list;
        int posOfFirstDelimeter = list.indexOf(delimiter);
        if(posOfFirstDelimeter == -1)
            return list;
        return list.substring(0, posOfFirstDelimeter);
    }

    public static String cutFirstItem(String list, String delimiter) {
        if(isNullOrEmpty(list))
            return null;
        int posOfFirstDelimeter = list.indexOf(delimiter);
        if(posOfFirstDelimeter == -1)
            return null;
        return list.substring(posOfFirstDelimeter+1);
    }

	/**
	 * Сравнивает две строки
	 * @param str1 - строка 1
	 * @param str2 - строка 2
	 * @param ignoreCase - игнорировать регистр 
	 * @return если равны, тогда true
	 */
	public static boolean compare(String str1, String str2, Boolean ignoreCase) {
		if ((str1 == null) && (str2 == null))
			return true;
		else if ((str1 == null) || (str2 == null))
			return false;
		else {
			if (ignoreCase)
				return str1.equalsIgnoreCase(str2);
			else
				return str1.equals(str2);
		}
	}

    public static <T> String combineArray(T[] array, String delimiter) {
        StringBuilder sb  = new StringBuilder();
        for (T item : array)
            sb.append(sb.length() == 0 ? item.toString() : delimiter+item.toString());
        return sb.toString();
    }

    public static String combineArray(int[] array, String delimiter) {
        StringBuilder sb  = new StringBuilder();
        for (int item : array)
            sb.append(sb.length() == 0 ? item : delimiter+item);
        return sb.toString();
    }
    public static String combineArray(byte[] array, String delimiter) {
        StringBuilder sb  = new StringBuilder();
        for (byte item : array)
            sb.append(sb.length() == 0 ? item : delimiter+item);
        return sb.toString();
    }

    public static boolean isString(Object value) {
        return value == null || value instanceof String;
    }

    public static String trim(String str, String substr) {
        if(isNullOrEmpty(str))
            return str;
        if(isNullOrEmpty(substr))
            return str;
        StringBuilder sb = new StringBuilder(str);
        for(char c : substr.toCharArray()) {
            while(sb.charAt(0) == c)
                sb.deleteCharAt(0);
            while(sb.charAt(sb.length()-1) == c)
                sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }

    public static String replace(String str, int startIndex, int endIndex, String replacement) {
        StringBuffer text = new StringBuffer(str);
        text.replace(startIndex, endIndex+1, replacement);
        return text.toString();
    }
    public static String replace(String str, String what, String replacement) {
        if(!isNullOrEmpty(str) && !isNullOrEmpty(what)) {
            while (str.indexOf(what) >= 0) {
                int pos = str.indexOf(what);
                int posto = pos + what.length() - 1;
                str = replace(str, pos, posto, replacement);
            }
        }
        return str;
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    public static Boolean containsIgnoreCase(Collection<String> list, String soughtFor) {
        for (String current : list) {
            if (current.equalsIgnoreCase(soughtFor)) {
                return true;
            }
        }
        return false;
    }

    public static <T> T findIgnoreCase(Map<String, T> map, String soughtFor) {
        for (String current : map.keySet()) {
            if (current.equalsIgnoreCase(soughtFor)) {
                return map.get(current);
            }
        }
        return null;
    }
}