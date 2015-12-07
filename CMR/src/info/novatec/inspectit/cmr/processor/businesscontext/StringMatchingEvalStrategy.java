package info.novatec.inspectit.cmr.processor.businesscontext;

import info.novatec.inspectit.ci.business.StringMatchingExpression;
import info.novatec.inspectit.communication.data.InvocationSequenceData;

/**
 * Evaluation strategy for the {@link StringMatchingExpression}.
 *
 * @author Alexander Wert
 *
 */
public class StringMatchingEvalStrategy extends AbstractExpressionEvaluationStrategy<StringMatchingExpression> {

	/**
	 * Constructor.
	 *
	 * @param expressionEvaluation
	 *            expression evaluation component used for recursive evaluation
	 */
	public StringMatchingEvalStrategy(ExpressionEvaluation expressionEvaluation) {
		super(expressionEvaluation);
	}

	/**
	 *
	 * {@inheritDoc}
	 */
	@Override
	public boolean evaluate(StringMatchingExpression expression, InvocationSequenceData invocSequence) {
		return evaluate(expression, invocSequence, 0);
	}

	/**
	 * Recursive evaluation in the invocation sequence structure if search in trace is activated.
	 *
	 * @param expression
	 *            {@link StringMatchingExpression} to evaluate
	 * @param invocSequence
	 *            {@link InvocationSequenceData} forming the evaluation context
	 * @param depth
	 *            current search depth in the invocation sequence tree structure
	 * @return Returns evaluation result.
	 */
	private boolean evaluate(StringMatchingExpression expression, InvocationSequenceData invocSequence, int depth) {
		boolean matches = false;

		String[] strArray = expression.getStringValueSource().getStringValues(invocSequence, expressionEvaluation.getCachedDataService());
		if (null == strArray) {
			return false;
		}

		for (String element : strArray) {
			if (null != element && evaluateString(expression, element)) {
				return true;
			}
		}

		if (!matches && expression.isSearchNodeInTrace() && (expression.getMaxSearchDepth() < 0 || depth < expression.getMaxSearchDepth())) {
			for (InvocationSequenceData childNode : invocSequence.getNestedSequences()) {
				if (evaluate(expression, childNode, depth + 1)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Evaluates the string array against the snippet in the {@link StringMatchingExpression}
	 * instance.
	 *
	 * @param expression
	 *            {@link StringMatchingExpression} instance
	 * @param stringValue
	 *            string to check
	 * @return boolean evaluation result
	 */
	private boolean evaluateString(StringMatchingExpression expression, String stringValue) {
		switch (expression.getMatchingType()) {
		case CONTAINS:
			return stringValue.contains(expression.getSnippet());
		case ENDS_WITH:
			return stringValue.endsWith(expression.getSnippet());
		case STARTS_WITH:
			return stringValue.startsWith(expression.getSnippet());
		case EQUALS:
			return stringValue.equals(expression.getSnippet());
		default:
			return false;
		}
	}

}
