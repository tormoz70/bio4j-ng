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

import ru.bio4j.ng.commons.collections.KeyValue;
import ru.bio4j.ng.commons.converter.ConvertValueException;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.converter.Types;
import ru.bio4j.ng.commons.utils.*;
import ru.bio4j.ng.model.transport.BioQueryParams;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.RestParamNames;

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
            throw Utl.wrapErrorAsRuntimeException(ex);
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

	public List<Param> process(DelegateCheck<Param> check) {
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
		if(rslt != null)
            get().remove(rslt);
		return rslt;
	}

	public Paramus add(Param item, Boolean replaceIfExists) {
		if (item != null) {
            Param exists = this.getParam(item.getName());
			if (exists == null)
                get().add(item);
            else {
                if (replaceIfExists) {
                    int indx = this.getIndexOf(exists.getName());
                    get().remove(exists);
                    get().add(indx, item);
                }
            }
		}
		return this;
	}

    public Paramus add(Param item) {
        return this.add(item, false);
    }


	public Paramus add(String name, Object value, MetaType metaType, Boolean replaceIfExists) {
		if (!Strings.isNullOrEmpty(name)) {
			this.add(Param.builder().name(name).type(metaType).value(value).build(), replaceIfExists);
		}
		return this;
	}

    public Paramus add(String name, Object value, Boolean replaceIfExists) {
        if (!Strings.isNullOrEmpty(name)) {
            this.add(Param.builder().name(name).value(value).build(), replaceIfExists);
        }
        return this;
    }

	public Paramus add(String name, MetaType metaType, Object value) {
		return this.add(name, value, metaType, false);
	}

    public Paramus add(String name, Object value) {
        return this.add(name, value, false);
    }

    public Paramus add(String name, Object value, Object innerObject) {
		return this.add(Param.builder().name(name).value(value).innerObject(innerObject).build(), false);
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
     */
    public Paramus apply(Param item) {
        apply(Arrays.asList(item));
        return this;
    }

    public Paramus apply(List<Param> params, boolean applyOnlyExists, boolean replaceIfExists) {
        if ((params != null) && (params != this.get())) {
            List<Param> toKill = new ArrayList<>();
            for (Param pp : params) {
                Param local = this.getParam(pp.getName());
                if(local != null) {
                    if(replaceIfExists){
                        this.add(pp, true);
                    }else {
                        local.setValue(pp.getValue());
                        local.setInnerObject(pp.getInnerObject());
                        MetaType ppType = pp.getType() != null ? pp.getType() : MetaType.UNDEFINED;
                        if (ppType != MetaType.UNDEFINED)
                            local.setType(ppType);
                        Param.Direction ppDirection = pp.getDirection() != null ? pp.getDirection() : Param.Direction.UNDEFINED;
                        if (ppDirection != Param.Direction.UNDEFINED)
                            local.setDirection(ppDirection);
                    }
                } else {
                    if(!applyOnlyExists)
                        this.add(pp.export(), false);
                }
            }
        }
        return this;
    }

    public Paramus apply(HashMap<String, Object> params, boolean applyOnlyExists, boolean replaceIfExists) {
        if ((params != null) && (params != this.get())) {
            List<Param> toKill = new ArrayList<>();
            for (String key : params.keySet()) {
                Param pp = Param.builder().name(key).value(params.get(key)).build();
                Param local = this.getParam(key);
                if(local != null) {
                    if(replaceIfExists){
                        this.add(pp, true);
                    }else {
                        local.setValue(pp.getValue());
                        local.setInnerObject(pp.getInnerObject());
                        MetaType ppType = pp.getType() != null ? pp.getType() : MetaType.UNDEFINED;
                        if (ppType != MetaType.UNDEFINED)
                            local.setType(ppType);
                        Param.Direction ppDirection = pp.getDirection() != null ? pp.getDirection() : Param.Direction.UNDEFINED;
                        if (ppDirection != Param.Direction.UNDEFINED)
                            local.setDirection(ppDirection);
                    }
                } else {
                    if(!applyOnlyExists)
                        this.add(pp.export(), false);
                }
            }
        }
        return this;
    }

    public Paramus apply(List<Param> params, boolean applyOnlyExists) {
        return apply(params, applyOnlyExists, false);
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

	public Map<String, String> toMap() {
		Map<String, String> rslt = new HashMap<String, String>();
		for (Param prm : get()) {
			String val = null;
			if ((prm.getValue() != null) && (prm.getValue().getClass() == String.class))
				val = paramValueAsString(prm);
			else {
				val = Jecksons.getInstance().encode(prm.getValue());
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

	public String encode() {
		return Jecksons.getInstance().encode(this);
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

	public static String extractDateFormat(String fmt) {
		String rslt = Regexs.find(fmt, "(?<=to_date\\(').*(?='\\);)", Pattern.CASE_INSENSITIVE);
		return Strings.isNullOrEmpty(rslt) ? "yyyy.MM.dd HH:mm:ss" : rslt;
	}

    private static final String cs_default_number_format = "##0.##";
	public static String extractNumberFormat(String fmt){
        if(Strings.isNullOrEmpty(fmt)) return cs_default_number_format;
		String rslt = Regexs.find(fmt, "(?<=;to_number\\(').*(?='\\))", Pattern.CASE_INSENSITIVE);
		return Strings.isNullOrEmpty(rslt) ? cs_default_number_format : rslt;
	}

    public Paramus setValue(String name, Object value, MetaType forceType, Param.Direction direction, boolean addIfNotExists) {
        Param param = this.getParam(name);
        if(param != null) {
			Class inClass = value != null ? value.getClass() : String.class;
			if(forceType != MetaType.UNDEFINED)
                param.setType(forceType);
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
                if (forceType != MetaType.UNDEFINED)
                    valueType = forceType;
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
    public Paramus setValue(String name, Object value, Param.Direction direction, boolean addIfNotExists) {
	    return setValue(name, value, MetaType.UNDEFINED, direction, addIfNotExists);
    }
    public Paramus setValue(String name, Object value, MetaType forceType, boolean addIfNotExists) {
        return setValue(name, value, forceType, Param.Direction.UNDEFINED, addIfNotExists);
    }
    public Paramus setValue(String name, Object value, MetaType forceType) {
        return setValue(name, value, forceType, Param.Direction.UNDEFINED, true);
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

    public static List<Param> clone(List<Param> params) {
	    if(params == null)
	        return null;
        List<Param> rslt = new ArrayList<>();
        for(Param p : params)
            rslt.add((Param) Utl.cloneBean(p));
	    return rslt;
    }

    public static List<Param> createParams() {
	    return new ArrayList<>();
    }

    public static List<Param> createParams(HashMap<String, Object> params) {
        List<Param> rslt = createParams();
        Paramus.applyParams(rslt, params, false, true);
        return rslt;
    }

    private static List<Param> _createParams(List<String> keys, List<Object> vals) {
        List<Param> rslt = createParams();
        Object val;
        for(int i=0; i<keys.size(); i++) {
            val = null;
            if (i >= 0 && i < vals.size())
                val = vals.get(i);
            Paramus.setParamValue(rslt, keys.get(i), val);
        }
        return rslt;
    }

    public static List<Param> createParams(Object ... params) {
        List<String> keys = new ArrayList<>();
        List<Object> vals = new ArrayList<>();
        for(int i=0; i<params.length; i++) {
            if(i % 2 == 0)
                keys.add((String)params[i]);
            else
                vals.add(params[i]);
        }
        return _createParams(keys, vals);
    }

    public <T> T getParamValue(String paramName, Class<T> type, boolean silent) {
        Param param = this.getParam(paramName, true);
        if (param != null)
            return paramValue(param, type);
        if(!silent)
            throw new IllegalArgumentException(String.format("Param \"%s\" not found in collection \"params\"!", paramName));
        else
            return null;
    }

    public <T> T getParamValue(String paramName, Class<T> type) {
	    return getParamValue(paramName, type, false);
    }

    public Object getParamValue(String paramName, boolean silent) {
        Param param = this.getParam(paramName, true);
        if (param != null)
            return param.getValue();
        if(!silent)
            throw new IllegalArgumentException(String.format("Param \"%s\" not found in collection \"params\"!", paramName));
        else
            return null;
    }

    public Object getParamValue(String paramName) {
	    return getParamValue(paramName, false);
    }

    public boolean paramIsEmpty(String paramName) {
        Param param = this.getParam(paramName, true);
        return (param == null) || param.isEmpty();
    }

    public static <T> T paramValue(Param param, Class<T> type) {
        return Converter.toType(param.getValue(), type);
    }

	public static String paramValueAsString(Param param) {
        try{
		    return (param.getValue() == null) ? null : Converter.toType(param.getValue(), String.class, "yyyy-MM-dd-HH-mm-ss");
        } catch (ConvertValueException ex) {
            return ex.getMessage();
        }
	}

    public static <T> T paramValue(List<Param> params, String paramName, Class<T> type, T defaultValue) {
        T rslt = null;
        try (Paramus paramus = Paramus.set(params)) {
            rslt = Utl.nvl(paramus.getParamValue(paramName, type, true), defaultValue);
        }
        return rslt;
    }

    public static Param removeParam(List<Param> params, String paramName) {
	    Param rslt = null;
        try (Paramus paramus = Paramus.set(params)) {
            rslt = paramus.getParam(paramName, true);
            paramus.remove(paramName);
        }
        return rslt;
    }

    public static Object paramValue(List<Param> params, String paramName) {
        try (Paramus paramus = Paramus.set(params)) {
            return paramus.getParamValue(paramName);
        }
    }

    public static boolean paramIsEmpty(List<Param> params, String paramName) {
        try (Paramus paramus = Paramus.set(params)) {
            return paramus.paramIsEmpty(paramName);
        }
    }

    public static String paramValueAsString(List<Param> params, String paramName, String defaultValue) {
        String rslt = null;
        try (Paramus paramus = Paramus.set(params)) {
            rslt = Utl.nvl(paramus.getParamValue(paramName, String.class, true), defaultValue);
        }
        return rslt;
    }

    public static String paramValueAsString(List<Param> params, String paramName) {
        return paramValueAsString(params, paramName, null);
    }

    public static void setParamValue(List<Param> params, String paramName, Object value, MetaType forceType, Param.Direction direction) {
        try (Paramus paramus = Paramus.set(params)) {
            paramus.setValue(paramName, value, forceType, direction, true);
        }
    }

    public static void setParamValue(List<Param> params, String paramName, Object value, MetaType forceType) {
        try (Paramus paramus = Paramus.set(params)) {
            paramus.setValue(paramName, value, forceType);
        }
    }

    public static void setParamValue(List<Param> params, String paramName, Object value) {
        try (Paramus paramus = Paramus.set(params)) {
            paramus.setValue(paramName, value);
        }
    }

    public static Param getParam(List<Param> params, String paramName) {
        try (Paramus paramus = Paramus.set(params)) {
            return paramus.getParam(paramName);
        }
    }

    public static int indexOf(List<Param> params, String paramName) {
        try (Paramus paramus = Paramus.set(params)) {
            return paramus.getIndexOf(paramName);
        }
    }

    public static void setParam(List<Param> params, Param param, boolean applyOnlyExists, boolean replaceIfExists) {
        try (Paramus paramus = Paramus.set(params)) {
            paramus.apply(Arrays.asList(param), applyOnlyExists, replaceIfExists);
        }
    }

    public static void setParam(List<Param> params, Param param, boolean replaceIfExists) {
        try (Paramus paramus = Paramus.set(params)) {
            paramus.apply(Arrays.asList(param), false, replaceIfExists);
        }
    }

    public static void setParams(List<Param> params, List<Param> paramsFrom) {
        try (Paramus paramus = Paramus.set(paramsFrom)) {
            for (Param p : paramus.get())
                Paramus.setParamValue(params, p.getName(), p.getValue());
        }
    }

    public static void applyParams(List<Param> params, List<Param> paramsFrom, boolean applyOnlyExists, boolean replaceIfExists) {
        try (Paramus paramus = Paramus.set(params)) {
            paramus.apply(paramsFrom, applyOnlyExists, replaceIfExists);
        }
    }

    public static void applyParams(List<Param> params, HashMap<String, Object> paramsFrom, boolean applyOnlyExists, boolean replaceIfExists) {
        try (Paramus paramus = Paramus.set(params)) {
            paramus.apply(paramsFrom, applyOnlyExists, replaceIfExists);
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

    public static String paramToString(Param param) {
        String innrObjStr = (param.getInnerObject() == null) ? null : "o:" + param.getInnerObject().toString();
        StringBuilder objsStr = new StringBuilder();
        if (param.getInnerObject() == null){
            objsStr.append(objsStr.length() == 0 ? innrObjStr : ";"+innrObjStr);
        }
        String objStr = objsStr.toString();
        Object val = param.getValue();
        String valStr = String.format((val instanceof String) ? "[\"%s\"]" : "[%s]", val);
        valStr = valStr + (!Strings.isNullOrEmpty(objStr) ? "(" + objsStr.toString() + ")" : null);
        return String.format("(%s=%s; tp:%s; sz:%d; dr:%s; fx:%s; fm:%s)", param.getName(), valStr, param.getType(), param.getSize(), param.getDirection(), param.getOverride(), param.getFormat());
    }


    public static String paramsAsString(List<Param> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (Param p : params)
            sb.append("\t"+paramToString(p)+",\n");
        sb.append("]");
        return sb.toString();
    }

    public static void setQueryParamsToBioParams(BioQueryParams qprms) {
        if(qprms.bioParams == null)
            qprms.bioParams = new ArrayList<>();
        Paramus.setParamValue(qprms.bioParams, RestParamNames.PAGINATION_PARAM_PAGE, qprms.page, MetaType.INTEGER);
        Paramus.setParamValue(qprms.bioParams, RestParamNames.PAGINATION_PARAM_PAGESIZE, qprms.pageSize, MetaType.INTEGER);
        Paramus.setParamValue(qprms.bioParams, RestParamNames.PAGINATION_PARAM_OFFSET, qprms.offset, MetaType.INTEGER);
        Paramus.setParamValue(qprms.bioParams, RestParamNames.PAGINATION_PARAM_TOTALCOUNT, qprms.totalCount);
//        Paramus.setParamValue(qprms.bioParams, RestParamNames.GETROW_PARAM_PKVAL, qprms.id);
//        Paramus.setParamValue(qprms.bioParams, RestParamNames.RAPI_PARAM_FILEHASHCODE, qprms.fileHashCode);
        Paramus.setParamValue(qprms.bioParams, RestParamNames.QUERY_PARAM_VALUE, qprms.query);
        Object location = qprms.location;
        if (location != null && location instanceof String) {
            if (((String) location).startsWith("1||"))
                location = null;
            if (((String) location).startsWith("0||")) {
                location = Regexs.find((String) location, "(?<=0\\|\\|)(\\w|\\d|-|\\+)+", Pattern.CASE_INSENSITIVE);
            }
            Paramus.setParamValue(qprms.bioParams, RestParamNames.LOCATE_PARAM_PKVAL, location);
        }
        Paramus.setParamValue(qprms.bioParams, RestParamNames.LOCATE_PARAM_STARTFROM, qprms.offset);
    }

}
