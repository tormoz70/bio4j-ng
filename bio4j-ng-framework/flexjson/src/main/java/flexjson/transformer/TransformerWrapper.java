package flexjson.transformer;

import flexjson.JSONContext;

/**
 * This class quietly wraps all transformers so that FlexJSON can perform
 * certain functionality consistently across all transformers.
 */
public class TransformerWrapper extends AbstractTransformer {

	protected Transformer transformer;
	protected Boolean isInterceptorTransformer = Boolean.FALSE;

	public TransformerWrapper(Transformer transformer, JSONContext context) {
		this.setContext(context);
		this.transformer = transformer;
		if (this.transformer != null)
			((AbstractTransformer) this.transformer).setContext(context);
	}

	public void transform(Object object) {

		// push object onto stack so object has reference before starting
		getContext().getObjectStack().addFirst(object);

		this.transformer.transform(object);

		// Call FlexJSON interceptors afterTranform last
		getContext().getObjectStack().removeFirst();

	}

	@Override
	public Boolean isInline() {
		return transformer instanceof Inline && ((Inline) transformer).isInline();
	}

}
