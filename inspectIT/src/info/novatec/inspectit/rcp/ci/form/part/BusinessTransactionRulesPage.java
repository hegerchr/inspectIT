/**
 *
 */
package info.novatec.inspectit.rcp.ci.form.part;

import info.novatec.inspectit.ci.business.BusinessTransactionDefinition;
import info.novatec.inspectit.cmr.configuration.business.IApplicationDefinition;
import info.novatec.inspectit.cmr.configuration.business.IBusinessTransactionDefinition;
import info.novatec.inspectit.cmr.configuration.business.IExpression;
import info.novatec.inspectit.exception.BusinessException;
import info.novatec.inspectit.rcp.ci.form.input.ApplicationDefinitionEditorInput;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Alexander Wert
 *
 */
public class BusinessTransactionRulesPage implements IDetailsPage, IPropertyListener {

	private final FormPage formPage;
	private final String title;
	private final FormToolkit toolkit;
	private final int style;

	private IManagedForm managedForm;
	private MatchingRulesPart rulesPart;
	/**
	 * Id of the currently selected business transaction.
	 */
	private IBusinessTransactionDefinition selectedBusinessTransaction;

	public BusinessTransactionRulesPage(FormPage formPage, String title, FormToolkit toolkit, int style) {
		this.formPage = formPage;
		this.title = title;
		this.toolkit = toolkit;
		this.style = style;
		this.formPage.getEditor().addPropertyListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createContents(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		rulesPart = new MatchingRulesPart(title, parent, toolkit, style);
		managedForm.addPart(rulesPart);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize(IManagedForm form) {
		this.managedForm = form;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		if (null != rulesPart) {
			rulesPart.dispose();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDirty() {
		return rulesPart.isDirty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void commit(boolean onSave) {
		if (onSave && null != selectedBusinessTransaction) {
			IExpression businessTxMatchingrule = rulesPart.constructMatchingRule();
			selectedBusinessTransaction.setMatchingRuleExpression(businessTxMatchingrule);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setFormInput(Object input) {
		return rulesPart.setFormInput(input);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		rulesPart.setFocus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStale() {
		return rulesPart.isStale();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		rulesPart.refresh();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		if (!selection.isEmpty()) {
			if (null != selectedBusinessTransaction) {
				IExpression rule = rulesPart.constructMatchingRule();
				selectedBusinessTransaction.setMatchingRuleExpression(rule);
			}
			selectedBusinessTransaction = (IBusinessTransactionDefinition) ((StructuredSelection) selection).getFirstElement();
			if (null == selectedBusinessTransaction) {
				rulesPart.setRulesVisible(false);
				rulesPart.setDescriptionText("No Business Transaction selected.");
			} else if (selectedBusinessTransaction.getId() == BusinessTransactionDefinition.DEFAULT_ID) {
				rulesPart.setRulesVisible(false);
				rulesPart.setDescriptionText("The default 'Unknown Transaction' is selected. The matching rules cannot be modified for this transaction definition!");
			} else {
				if (null != selectedBusinessTransaction) {
					rulesPart.changeInput(selectedBusinessTransaction.getMatchingRuleExpression());
					rulesPart.setRulesVisible(true);
					rulesPart.setDescriptionText("Select rules that should be used to match the selected business transaction:");
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChanged(Object source, int propId) {
		if (propId == IEditorPart.PROP_INPUT && null != selectedBusinessTransaction) {
			ApplicationDefinitionEditorInput input = (ApplicationDefinitionEditorInput) formPage.getEditor().getEditorInput();
			IApplicationDefinition application = input.getApplication();

			try {
				selectedBusinessTransaction = application.getBusinessTransactionDefinition(selectedBusinessTransaction.getId());
			} catch (BusinessException e) {
				selectedBusinessTransaction = null;
			}

			if (null != selectedBusinessTransaction) {
				rulesPart.changeInput(selectedBusinessTransaction.getMatchingRuleExpression());
			}
		}

	}

}
