package flexjson;

import flexjson.transformer.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class TransformerUtil {

	public static TypeTransformerMap getDefaultTypeTransformers(JSONContext context) {
		TypeTransformerMap defaultTransformers = new TypeTransformerMap();

		// define all standard type transformers
		Transformer transformer = new NullTransformer();
		defaultTransformers.put(null, new TransformerWrapper(transformer, context));

		transformer = new ObjectTransformer();
		defaultTransformers.put(Object.class, new TransformerWrapper(transformer, context));

		transformer = new ClassTransformer();
		defaultTransformers.put(Class.class, new TransformerWrapper(transformer, context));

		transformer = new BooleanTransformer();
		defaultTransformers.put(boolean.class, new TransformerWrapper(transformer, context));
		defaultTransformers.put(Boolean.class, new TransformerWrapper(transformer, context));

		transformer = new NumberTransformer();
		defaultTransformers.put(Number.class, new TransformerWrapper(transformer, context));

		defaultTransformers.put(Integer.class, new TransformerWrapper(transformer, context));
		defaultTransformers.put(int.class, new TransformerWrapper(transformer, context));

		defaultTransformers.put(Long.class, new TransformerWrapper(transformer, context));
		defaultTransformers.put(long.class, new TransformerWrapper(transformer, context));

		defaultTransformers.put(Double.class, new TransformerWrapper(transformer, context));
		defaultTransformers.put(double.class, new TransformerWrapper(transformer, context));

		defaultTransformers.put(Float.class, new TransformerWrapper(transformer, context));
		defaultTransformers.put(float.class, new TransformerWrapper(transformer, context));

		defaultTransformers.put(BigDecimal.class, new TransformerWrapper(transformer, context));
		defaultTransformers.put(BigInteger.class, new TransformerWrapper(transformer, context));

		transformer = new StringTransformer();
		defaultTransformers.put(String.class, new TransformerWrapper(transformer, context));

		transformer = new CharacterTransformer();
		defaultTransformers.put(Character.class, new TransformerWrapper(transformer, context));
		defaultTransformers.put(char.class, new TransformerWrapper(transformer, context));

		transformer = new BasicDateTransformer();
		defaultTransformers.put(Date.class, new TransformerWrapper(transformer, context));

		transformer = new EnumTransformer();
		defaultTransformers.put(Enum.class, new TransformerWrapper(transformer, context));

		transformer = new IterableTransformer();
		defaultTransformers.put(Iterable.class, new TransformerWrapper(transformer, context));

		transformer = new MapTransformer();
		defaultTransformers.put(Map.class, new TransformerWrapper(transformer, context));

		transformer = new NullTransformer();
		defaultTransformers.put(void.class, new TransformerWrapper(transformer, context));

		transformer = new ArrayTransformer();
		defaultTransformers.put(Arrays.class, new TransformerWrapper(transformer, context));

		try {
			Class hibernateProxy = Class.forName("org.hibernate.proxy.HibernateProxy");
			defaultTransformers.put(hibernateProxy, new TransformerWrapper(new HibernateTransformer(), context));
		} catch (ClassNotFoundException ex) {
			// no hibernate so ignore.
		}

		Collections.unmodifiableMap(defaultTransformers);
		return defaultTransformers;
	}

}
