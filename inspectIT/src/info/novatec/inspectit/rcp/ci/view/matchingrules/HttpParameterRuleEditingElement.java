package info.novatec.inspectit.rcp.ci.view.matchingrules;

import info.novatec.inspectit.ci.business.Expression;
import info.novatec.inspectit.ci.business.HttpParameterValueSource;
import info.novatec.inspectit.ci.business.StringMatchingExpression;
import info.novatec.inspectit.ci.business.StringValueSource;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Editing element for a HTTP parameter matching expression.
 *
 * @author Alexander Wert
 *
 */
public class HttpParameterRuleEditingElement extends AbstractStringMatchingRuleEditingElement {

	/**
	 * Property for the name of the HTTP parameter.
	 */
	private String parameterName = "";

	/**
	 * Text editing field for the {@link #parameterName} property.
	 */
	private Text parameterNameText;

	/**
	 * Label for parameter name.
	 */
	private Label parameterLabel;

	/**
	 * Dummy label to fill the grid layout.
	 */
	private Label fillLabel;

	/**
	 * Default constructor.
	 */
	public HttpParameterRuleEditingElement() {
		super("HTTP Parameter Matching", "the value ", true);
	}

	@Override
	protected void createSpecificElements(final Composite parent, FormToolkit toolkit) {
		fillLabel = toolkit.createLabel(parent, "");
		fillLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

		parameterLabel = toolkit.createLabel(parent, "For the HTTP parameter");
		parameterLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

		parameterNameText = toolkit.createText(parent, "", SWT.BORDER);
		parameterNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		parameterNameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				parameterName = ((Text) e.getSource()).getText();
				notifyModifyListeners();
			}
		});

		super.createSpecificElements(parent, toolkit);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void disposeSpecificElements() {
		parameterLabel.dispose();
		parameterNameText.dispose();
		fillLabel.dispose();
		super.disposeSpecificElements();
	}

	@Override
	protected void executeSpecificInitialization(Expression expression) {
		if (isValidExpression(expression)) {
			super.executeSpecificInitialization(expression);
			StringMatchingExpression strMatchingExpression = ((StringMatchingExpression) expression);
			parameterName = ((HttpParameterValueSource) strMatchingExpression.getStringValueSource()).getParameterName();
			parameterNameText.setText(parameterName);
		}

	}

	@Override
	protected StringValueSource getStringValueSource() {
		return new HttpParameterValueSource(parameterName);
	}

	@Override
	protected boolean isValidExpression(Expression expression) {
		return expression instanceof StringMatchingExpression && ((StringMatchingExpression) expression).getStringValueSource() instanceof HttpParameterValueSource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getNumRows() {
		return super.getNumRows() + 1;
	}

}
