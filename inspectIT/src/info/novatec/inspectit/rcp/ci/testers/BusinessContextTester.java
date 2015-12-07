package info.novatec.inspectit.rcp.ci.testers;

import info.novatec.inspectit.cmr.configuration.business.IApplicationDefinition;
import info.novatec.inspectit.cmr.configuration.business.IBusinessTransactionDefinition;
import info.novatec.inspectit.rcp.provider.IApplicationProvider;

import org.eclipse.core.expressions.PropertyTester;

/**
 * Property tester for the business context.
 *
 * @author Alexander Wert
 *
 */
public class BusinessContextTester extends PropertyTester {
	/**
	 * Tester property for the default application.
	 */
	public static final String IS_DEFAULT_APP_PROPERTY = "isDefaultApplication";

	/**
	 * Tester property for the default business transaction.
	 */
	public static final String IS_DEFAULT_BTX_PROPERTY = "isDefaultBusinessTransaction";

	/**
	 * Tester property for the moving up capability.
	 */
	public static final String CAN_MOVE_UP_PROPERTY = "canMoveUp";

	/**
	 * Tester property for the moving down capability.
	 */
	public static final String CAN_MOVE_DOWN_PROPERTY = "canMoveDown";

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof IApplicationProvider) {
			IApplicationProvider applicationProvider = (IApplicationProvider) receiver;
			if (IS_DEFAULT_APP_PROPERTY.equals(property)) {
				return applicationProvider.getApplication().getId() == IApplicationDefinition.DEFAULT_ID;
			} else if (CAN_MOVE_UP_PROPERTY.equals(property)) {
				return applicationProvider.getIndexInParentList() > 0;
			} else if (CAN_MOVE_DOWN_PROPERTY.equals(property)) {
				int listSize = applicationProvider.getParentListSize();
				return applicationProvider.getIndexInParentList() < listSize - 2;
			}
		}
		if (receiver instanceof IBusinessTransactionDefinition) {
			IBusinessTransactionDefinition businessTransactionDef = (IBusinessTransactionDefinition) receiver;
			if (IS_DEFAULT_BTX_PROPERTY.equals(property)) {
				return businessTransactionDef.getId() == IBusinessTransactionDefinition.DEFAULT_ID;
			} else if (CAN_MOVE_UP_PROPERTY.equals(property) && null != args && args.length > 0 && args[0] instanceof IApplicationDefinition) {
				return ((IApplicationDefinition) args[0]).getBusinessTransactionDefinitions().indexOf(businessTransactionDef) > 0;
			} else if (CAN_MOVE_DOWN_PROPERTY.equals(property) && null != args && args.length > 0 && args[0] instanceof IApplicationDefinition) {
				int index = ((IApplicationDefinition) args[0]).getBusinessTransactionDefinitions().indexOf(businessTransactionDef);
				return index >= 0 && index < ((IApplicationDefinition) args[0]).getBusinessTransactionDefinitions().size() - 2;
			}
		}

		return false;
	}

}
