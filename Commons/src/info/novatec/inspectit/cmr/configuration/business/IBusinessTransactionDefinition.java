package info.novatec.inspectit.cmr.configuration.business;

/**
 * Configuration element defining a business transaction context.
 *
 * @author Alexander Wert
 *
 */
public interface IBusinessTransactionDefinition {
	/**
	 * The default identifier.
	 */
	short DEFAULT_ID = 0;

	/**
	 * Returns the identifier of this business transaction.
	 *
	 * @return Returns the identifier of this business transaction
	 */
	short getId();

	/**
	 * Returns the name of the business transaction.
	 *
	 * @return the name of the business transaction
	 */
	String getBusinessTransactionName();

	/**
	 * Sets the name of the business transaction.
	 *
	 * @param businessTransactionName
	 *            New value for the name of the business transaction
	 */
	void setBusinessTransactionName(String businessTransactionName);

	/**
	 * Returns the description text.
	 *
	 * @return Returns the description text.
	 */
	String getDescription();

	/**
	 * Sets the description text.
	 *
	 * @param description
	 *            New value for the description text.
	 */
	void setDescription(String description);

	/**
	 * Returns the unique identifier of this business transaction definition.
	 *
	 * @return Returns the unique identifier of this business transaction definition.
	 */

	/**
	 * Returns the expression defining the matching rule.
	 *
	 * @return Returns the {@link IExpression} for this business transaction.
	 */
	IExpression getMatchingRuleExpression();

	/**
	 * Sets the {@link IExpression} for this business transaction.
	 *
	 * @param expression
	 *            New value for {@link IExpression}.
	 */
	void setMatchingRuleExpression(IExpression expression);
}
