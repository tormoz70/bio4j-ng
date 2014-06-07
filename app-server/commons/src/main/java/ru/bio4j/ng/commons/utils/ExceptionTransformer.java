package ru.bio4j.ng.commons.utils;

import flexjson.JSONContext;
import flexjson.JSONSerializer;
import flexjson.transformer.AbstractTransformer;

public class ExceptionTransformer extends AbstractTransformer {

	public void transform(Object value) {
        JSONContext context = getContext();
		String valueStr = new JSONSerializer()
                .exclude("cause", "localizedMessage", "stackTraceDepth","stackTrace")
                .serialize(value);
		context.write(valueStr);
	}

}
