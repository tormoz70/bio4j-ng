package ru.bio4j.ng.commons.types;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;

import ru.bio4j.ng.commons.converter.ConvertValueException;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.converter.Types;
import ru.bio4j.ng.commons.utils.*;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;

/**
 * Это helper для совершения разных манипуляций с List<Param>...
 */
public class Paramus implements Closeable {

    private static final Paramus instance = new Paramus();

	private Paramus() {
    }

    /**
     * Устанавливает  в активный контекст коллекцию params для текущего потока.
     * При этом предыдущая коллекция сохраняется в стек и привызове close() или pop() будет восставновлена в контекст
     * @param params
     * @return
     */
    public static Paramus set(List<Param> params){
        return instance.setContext(params);
    }

    public static Paramus instance(){
        return instance;
    }


    private final ThreadLocal<Stack<List<Param>>> context = new ThreadLocal<>();
    Paramus setContext(List<Param> params){
        if(context.get() == null)
            context.set(new Stack<List<Param>>());
        if(context.get().search(params) == -1)
            context.get().push(params);
        return this;
    }
    private void checkContext() {
        if(context.get() == null)
            throw new IllegalArgumentException("Call set() method to set context first!");
    }

    /**
     * Возвращает params установленный предыдущей командой set(params) в текущем потоке
     * @return
     */
    public List<Param> get(){
        checkContext();
        return context.get().peek();
    }

    /**
     * Удаляет из контекста текущую коллекцию params, возвращает ее в качастве результата и
     * воосстанавливает на место params, который был активен до вызова set(params)
     * @return
     */
    public List<Param> pop(){
        checkContext();
        return context.get().pop();
    }

    /**
     * Вполняет pop() без возвращения результата
     * @throws IOException
     */
    @Override
    public void close() {
        this.pop();
    }

	private static final String csDefaultDelimiter = "/";

	public Param getParam(final String name, final Boolean ignoreCase) {
        try {
            List<Param> result = this.process(new DelegateCheck<Param>() {
                @Override
                public Boolean callback(Param param) {
                    return Strings.compare(param.getName(), name, ignoreCase);
                }
            });
            return result.isEmpty() ? null : result.get(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
	}

	public Param getParam(final String name) {
		return getParam(name, true);
	}

	public String getNamesList() {
		String rslt = null;
		for (Param param : get())
			rslt = Strings.append(rslt, "\"" + param.getName() + "\"", ",");
		return rslt;
	}

	public String getValsList() {
		String rslt = null;
		for (Param param : get())
			rslt = Strings.append(rslt, "\"" + paramValueAsString(param) + "\"", ",");
		return rslt;
	}

	public Integer getIndexOf(String name) {
		return get().indexOf(this.getParam(name, true));
	}

	public List<Param> process(DelegateCheck<Param> check) throws Exception {
        return Lists.select(get(), check);
	}

	public Param first() {
		if (!get().isEmpty())
			return get().get(0);
		else
			return null;
	}

//	public Param removeParam(Param param) {
//		if (param.getOwner() == this)
//			param.remove();
//		return param;
//	}

	public Param remove(String name) {
		Param rslt = this.getParam(name);
        get().remove(rslt);
		return rslt;
	}

//	private Boolean alredyExists(String name) {
//		Boolean result = false;
//		Param exists = this.getParam(name);
//		if (exists != null) {
////			if (replaceIfExists) {
////                get().remove(exists);
////				exists = null;
////			} else
//				result = true;
//		}
//		return result;
//	}

	public Paramus add(Param item, Boolean replaceIfExists) {
		if (item != null) {
            Param exists = this.getParam(item.getName());
			if (exists == null)
                get().add(item);
            else {
                if (replaceIfExists) {
                    get().remove(exists);
                    get().add(item);
                }
            }
		}
		return this;
	}

    public Paramus add(Param item) {
        return this.add(item, false);
    }


	public Paramus add(String name, Object value, Boolean replaceIfExists) {
		if (!Strings.isNullOrEmpty(name)) {
//            Class<?> type = (value != null) ? value.getClass() : null;
			this.add(Param.builder()./*owner(get()).*/name(name).value(value).build(), replaceIfExists);
		}
		return this;
	}

	public Paramus add(String name, Object value) {
		return this.add(name, value, false);
	}

	public Paramus add(String name, Object value, Object innerObject) {
//        Class<?> type = (value != null) ? value.getClass() : null;
		return this.add(Param.builder()./*owner(get()).*/name(name).value(value).innerObject(innerObject).build(), false);
	}

	public Paramus merge(List<Param> params, Boolean overwrite) {
		if ((params != null) && (params != this.get())) {
			for (Param pp : params)
                this.add(pp.export(), overwrite);
		}
		return this;
	}

    /**
     * Если есть параметр с таким именем, то обновляет его поля, иначе просто добавляет
     * @param item
     * @return
     * @throws Exception
     */
    public Paramus apply(Param item) throws Exception {
        apply(Arrays.asList(item));
        return this;
    }

    public Paramus apply(List<Param> params, boolean applyOnliExists) {
        if ((params != null) && (params != this.get())) {
            for (Param pp : params) {
                Param local = this.getParam(pp.getName());
                if(local != null) {
                    local.setValue(pp.getValue());
                    local.setInnerObject(pp.getInnerObject());
                    MetaType ppType = pp.getType() != null ? pp.getType() : MetaType.UNDEFINED;
                    if(ppType != MetaType.UNDEFINED)
                        local.setType(ppType);
                    Param.Direction ppDirection = pp.getDirection() != null ? pp.getDirection() : Param.Direction.UNDEFINED;
                    if(ppDirection != Param.Direction.UNDEFINED)
                        local.setDirection(ppDirection);
                } else {
                    if(!applyOnliExists)
                        this.add(pp.export(), false);
                }
            }
        }
        return this;
    }

    public Paramus apply(List<Param> params) {
        return apply(params, false);
    }

	public Object getInnerObjectByName(String name, Boolean ignoreCase) {
		Param param = this.getParam(name, ignoreCase);
		if (param != null)
			return param.getInnerObject();
		return null;
	}

	public String getValueAsStringByName(String name, Boolean ignoreCase) {
		Param param = this.getParam(name, ignoreCase);
		if (param != null)
			return paramValueAsString(param);
		return null;
	}

	public Object getValueByName(String name, Boolean ignoreCase) {
		Param param = this.getParam(name, ignoreCase);
		if (param != null)
			return param.getValue();
		return null;
	}

    public <T> T getValueByName(Class<T> type, String name, Boolean ignoreCase) throws ConvertValueException {
        Param param = this.getParam(name, ignoreCase);
        if (param != null)
            return paramValue(param, type);
        return null;
    }

	public Map<String, String> toMap() throws Exception {
		Map<String, String> rslt = new HashMap<String, String>();
		for (Param prm : get()) {
			String val = null;
			if ((prm.getValue() != null) && (prm.getValue().getClass() == String.class))
				val = paramValueAsString(prm);
			else {
				val = Jsons.encode(prm.getValue());
			}
			rslt.put(prm.getName(), val);
		}
		return rslt;
	}

	public String buildUrlParams() {
		String rslt = null;
		for (Param prm : get()) {
			String paramStr = null;
			try {
                String valueStr = paramValueAsString(prm);
				paramStr = prm.getName() + "=" + (Strings.isNullOrEmpty(valueStr) ? "null" : URLEncoder.encode(valueStr, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
			rslt = Strings.append(rslt, paramStr, "&");
		}
		return rslt;
	}

	public String buildUrlParams(String baseURL) {
		String rslt = this.buildUrlParams();
		if (!Strings.isNullOrEmpty(baseURL))
			return (baseURL.indexOf("?") >= 0) ? baseURL + "&" + rslt : baseURL + "?" + rslt;
		else
			return rslt;
	}

	public String encode() throws Exception {
		return Jsons.encode(this);
	}

//	public static List<Param> decode(String jsonString) throws Exception {
//		return Jsons.decode(jsonString, ArrayList<Param>.class);
//	}

	public Boolean paramExists(String name) {
		return this.getParam(name) != null;
	}

	public Boolean paramExists(String name, Boolean ignoreCase) {
		return this.getParam(name, ignoreCase) != null;
	}

	public Paramus addList(String names, Object[] values, String delimiter) {
		String[] paramNames = Strings.split(names, delimiter);
		for (int i = 0; i < paramNames.length; i++)
			this.add(paramNames[i], (i < values.length) ? values[i] : null);
		return this;
	}

	public Paramus addList(String names, Object[] values) {
		return this.addList(names, values, csDefaultDelimiter);
	}

	public Paramus addList(String names, String values, String delimiter) {
		return this.addList(names, Strings.split(values, delimiter), delimiter);
	}

	public Paramus addList(String names, String values) {
		return addList(names, values, csDefaultDelimiter);
	}

	public Paramus setList(String names, Object[] values, String delimiter) {
		String[] strs = Strings.split(names, delimiter);
		for (int i = 0; i < strs.length; i++)
			if (i < values.length)
				this.add(Param.builder().name(strs[i]).value(values[i]).build(), true);
		return this;
	}

	public Paramus setList(String names, String values, String delimiter) {
		return setList(names, Strings.split(values, delimiter), delimiter);
	}

	public Paramus setList(String names, String values) {
		return setList(names, values, csDefaultDelimiter);
	}

	public static String extractDateFormat(String fmt){
		String rslt = Regexs.find(fmt, "(?<=to_date\\(').*(?='\\);)", Pattern.CASE_INSENSITIVE);
		return Strings.isNullOrEmpty(rslt) ? "yyyy.MM.dd HH:mm:ss" : rslt;
	}

	public static String extractNumberFormat(String fmt){
		String rslt = Regexs.find(fmt, "(?<=;to_number\\(').*(?='\\))", Pattern.CASE_INSENSITIVE);
		return Strings.isNullOrEmpty(rslt) ? "##0.##" : rslt;
	}

    public Paramus setValue(String name, Object value, Param.Direction direction, boolean addIfNotExists) {
        Param param = this.getParam(name);
        if(param != null) {
			Class inClass = value != null ? value.getClass() : String.class;
            MetaType paramType = param.getType();
            MetaType valueType = (paramType == MetaType.UNDEFINED ? MetaTypeConverter.read(inClass) : paramType);
            param.setType(valueType);
			Class valueClass = MetaTypeConverter.write(valueType);
			if(valueType == MetaType.STRING && (Types.typeIsDate(inClass) || Types.typeIsNumber(inClass))){
				if(Types.typeIsDate(inClass)){
					String format = extractDateFormat(param.getFormat());
					DateFormat df = new SimpleDateFormat(format);
					value = df.format(value);
				}else if(Types.typeIsNumber(inClass)){
					String format = extractNumberFormat(param.getFormat());
					DecimalFormat myFormatter = new DecimalFormat(format, new DecimalFormatSymbols(Locale.ENGLISH));
					value = myFormatter.format(value);
				}
			}
            try {
                param.setValue(Converter.toType(value, valueClass));
            } catch (ConvertValueException e) {
                throw new IllegalArgumentException(String.format("Cannot set value \"%s\" to parameter \"%s[%s]\"!", ""+value, name, paramType.name()));
            }
            if(direction != Param.Direction.UNDEFINED)
                param.setDirection(direction);
        } else {
            if(addIfNotExists) {
                MetaType valueType = MetaTypeConverter.read(value != null ? value.getClass() : String.class);
                this.add(Param.builder()
                        .name(name)
                        .value(value)
                        .type(valueType)
                        .direction(direction)
                        .build());
            }
        }
        return this;
    }
    public Paramus setValue(String name, Object value, boolean addIfNotExists) {
        return setValue(name, value, Param.Direction.UNDEFINED, addIfNotExists);
    }

    public Paramus setValue(String name, Object value) {
        return setValue(name, value, true);
    }

	public Paramus removeList(String names, String delimiter) {
		String[] strs = Strings.split(names, delimiter);
		for (int i = 0; i < strs.length; i++)
			this.remove(strs[i]);
		return this;
	}

	public Paramus removeList(String names) {
		return removeList(names, csDefaultDelimiter);
	}

    public static List<Param> clone(List<Param> params) throws Exception {
        List<Param> rslt = new ArrayList<>();
        for(Param p : params)
            rslt.add((Param) Utl.cloneBean(p));
	    return rslt;
    }

    public <T> T getParamValue(String paramName, Class<T> type) throws ConvertValueException {
        Param param = this.getParam(paramName, true);
        if (param != null)
            return paramValue(param, type);
        throw new IllegalArgumentException(String.format("Param \"%s\" not found in collection \"params\"!", paramName));
    }

    public static <T> T paramValue(Param param, Class<T> type) throws ConvertValueException {
        return Converter.toType(param.getValue(), type);
    }

	public static String paramValueAsString(Param param) {
        try{
		    return (param.getValue() == null) ? null : Converter.toType(param.getValue(), String.class, "yyyy-MM-dd-HH-mm-ss");
        } catch (ConvertValueException ex) {
            return ex.getMessage();
        }
	}

//	@Override
//    public Param clone() throws CloneNotSupportedException {
//		Param.Builder builder = Param.builder().override(this, this.getOwner());
//		try {
//			builder.value(BeanUtils.cloneBean(this.getValue()));
//		} catch(Exception ex) {
//			builder.value(this.getValue());
//		}
//		try {
//			builder.innerObject(BeanUtils.cloneBean(this.getInnerObject()));
//		} catch(Exception ex) {
//			builder.innerObject(this.getInnerObject());
//		}
//	    return builder.build();
//    }


}
