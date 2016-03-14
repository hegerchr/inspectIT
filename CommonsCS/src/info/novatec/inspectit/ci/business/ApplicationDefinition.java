package info.novatec.inspectit.ci.business;

import info.novatec.inspectit.cmr.configuration.business.IApplicationDefinition;
import info.novatec.inspectit.cmr.configuration.business.IBusinessTransactionDefinition;
import info.novatec.inspectit.cmr.configuration.business.IExpression;
import info.novatec.inspectit.exception.BusinessException;
import info.novatec.inspectit.exception.TechnicalException;
import info.novatec.inspectit.exception.enumeration.BusinessContextErrorCodeEnum;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Configuration element defining an application context.
 *
 * @author Alexander Wert
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "applicaction")
public class ApplicationDefinition implements IApplicationDefinition {
	/**
	 * The name of the default business transaction.
	 */
	private static final String UNKNOWN_BUSINESS_TX = "Unknown Transaction";

	/**
	 * The default business transaction definition that does not match any data.
	 */
	private static final IBusinessTransactionDefinition DEFAULT_BUSINESS_TRANSACTION_DEFINITION = new BusinessTransactionDefinition(BusinessTransactionDefinition.DEFAULT_ID, UNKNOWN_BUSINESS_TX,
			new BooleanExpression(true));

	/**
	 * Identifier of the application. Needs to be unique!
	 */
	@XmlAttribute(name = "id", required = true)
	private short id = (short) UUID.randomUUID().getMostSignificantBits();

	/**
	 * Name of the application.
	 */
	@XmlAttribute(name = "name", required = true)
	private String applicationName;

	/**
	 * Description.
	 */
	@XmlAttribute(name = "description")
	private String description;

	/**
	 * Rule definition for matching measurement data to applications.
	 *
	 * Default Matching rule should not match any data, therefore {@link BooleanExpression} with
	 * false is used.
	 */
	@XmlElementRef(type = Expression.class)
	private IExpression matchingRuleExpression = new BooleanExpression(false);

	/**
	 * Business transaction definitions.
	 */
	@XmlElementWrapper(name = "business-transactions")
	@XmlElementRef(type = BusinessTransactionDefinition.class)
	private final List<IBusinessTransactionDefinition> businessTransactionDefinitions = new LinkedList<IBusinessTransactionDefinition>();

	/**
	 * Default business transaction definition.
	 */
	@XmlElementRef(type = BusinessTransactionDefinition.class)
	private final IBusinessTransactionDefinition defaultBusinessTxDefinition = DEFAULT_BUSINESS_TRANSACTION_DEFINITION;

	/**
	 * Default constructor.
	 */
	public ApplicationDefinition() {

	}

	/**
	 * Constructor.
	 *
	 * @param applicationName
	 *            name of the application
	 */
	public ApplicationDefinition(String applicationName) {
		this.applicationName = applicationName;
	}

	/**
	 * Constructor.
	 *
	 * @param id
	 *            unique identifier to use for this {@link ApplicationDefinition}
	 * @param applicationName
	 *            name of the application
	 * @param matchingRuleExpression
	 *            matching rule to use for recognition of this application
	 */
	public ApplicationDefinition(short id, String applicationName, IExpression matchingRuleExpression) {
		this(applicationName);
		this.matchingRuleExpression = matchingRuleExpression;
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IExpression getMatchingRuleExpression() {
		return matchingRuleExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMatchingRuleExpression(IExpression matchingRuleExpression) {
		this.matchingRuleExpression = matchingRuleExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IBusinessTransactionDefinition> getBusinessTransactionDefinitions() {
		List<IBusinessTransactionDefinition> allbusinessTxDefinitions = new LinkedList<IBusinessTransactionDefinition>(businessTransactionDefinitions);
		allbusinessTxDefinitions.add(getDefaultBusinessTransactionDefinition());
		return Collections.unmodifiableList(allbusinessTxDefinitions);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addBusinessTransactionDefinition(IBusinessTransactionDefinition businessTransactionDefinition) throws BusinessException {
		if (businessTransactionDefinitions.contains(businessTransactionDefinition)) {
			throw new BusinessException("Adding business transaction " + businessTransactionDefinition.getBusinessTransactionName() + " with id " + businessTransactionDefinition.getId() + ".",
					BusinessContextErrorCodeEnum.DUPLICATE_ITEM);
		} else {
			businessTransactionDefinitions.add(businessTransactionDefinition);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addBusinessTransactionDefinition(IBusinessTransactionDefinition businessTransactionDefinition, int insertBeforeIndex) throws BusinessException {
		if (businessTransactionDefinitions.contains(businessTransactionDefinition)) {
			throw new BusinessException("Adding business transaction " + businessTransactionDefinition.getBusinessTransactionName() + " with id " + businessTransactionDefinition.getId() + ".",
					BusinessContextErrorCodeEnum.DUPLICATE_ITEM);
		} else if (insertBeforeIndex < 0 || insertBeforeIndex > businessTransactionDefinitions.size()) {
			throw new BusinessException("Adding business transaction " + businessTransactionDefinition.getBusinessTransactionName() + " with id " + businessTransactionDefinition.getId() + " at index "
					+ insertBeforeIndex + ".", BusinessContextErrorCodeEnum.INVALID_MOVE_OPRATION);
		} else {
			businessTransactionDefinitions.add(insertBeforeIndex, businessTransactionDefinition);

		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteBusinessTransactionDefinition(IBusinessTransactionDefinition businessTransactionDefinition) {
		return businessTransactionDefinitions.remove(businessTransactionDefinition);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void moveBusinessTransactionDefinition(IBusinessTransactionDefinition businessTransactionDefinition, int index) throws BusinessException {
		if (index < 0 || index >= businessTransactionDefinitions.size()) {
			throw new BusinessException("Moving business transaction to index " + index + ".", BusinessContextErrorCodeEnum.INVALID_MOVE_OPRATION);
		}

		int currentIndex = businessTransactionDefinitions.indexOf(businessTransactionDefinition);
		if (currentIndex < 0) {
			throw new BusinessException("Moving business transaction to index " + index + ".", BusinessContextErrorCodeEnum.UNKNOWN_BUSINESS_TRANSACTION);
		}

		IBusinessTransactionDefinition definitionToMove = businessTransactionDefinitions.remove(currentIndex);
		businessTransactionDefinitions.add(index, definitionToMove);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IBusinessTransactionDefinition getBusinessTransactionDefinition(long id) throws BusinessException {
		for (IBusinessTransactionDefinition businessTxDef : businessTransactionDefinitions) {
			if (businessTxDef.getId() == id) {
				return businessTxDef;
			}
		}

		throw new TechnicalException("Retrieve business transaction with id '" + id + "'.", BusinessContextErrorCodeEnum.UNKNOWN_BUSINESS_TRANSACTION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IBusinessTransactionDefinition getDefaultBusinessTransactionDefinition() {
		return defaultBusinessTxDefinition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (id ^ (id >>> 32));
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ApplicationDefinition other = (ApplicationDefinition) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

}