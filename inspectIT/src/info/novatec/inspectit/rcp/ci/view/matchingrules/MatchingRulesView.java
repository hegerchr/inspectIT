package info.novatec.inspectit.rcp.ci.view.matchingrules;

import info.novatec.inspectit.ci.business.BooleanExpression;
import info.novatec.inspectit.ci.business.Expression;
import info.novatec.inspectit.ci.business.OrExpression;
import info.novatec.inspectit.cmr.configuration.business.IExpression;
import info.novatec.inspectit.rcp.ci.view.matchingrules.MatchingRulesEditingElementFactory.MatchingRuleType;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * Composite element for matching rules viewing, creation and modification.
 *
 * @author Alexander Wert
 *
 */
public class MatchingRulesEditingElement {
	/**
	 * List of {@link AbstractRuleEditingElement} comprised in this element.
	 */
	private final List<AbstractRuleEditingElement> ruleElements = new ArrayList<AbstractRuleEditingElement>();

	/**
	 * Main {@link Composite} of this element.
	 */
	private final Composite main;

	/**
	 * Main form of this element.
	 */
	private final ScrolledForm form;

	/**
	 * {@link FormToolkit} to use for creation of sub elements.
	 */
	private final FormToolkit toolkit;

	/**
	 * Indicates whether this element is in the initialization phase.
	 */
	private boolean initializationPhase = false;

	/**
	 * List of listeners to be notified on modification.
	 */
	private final List<RuleEditingElementModifiedListener> listeners = new ArrayList<RuleEditingElementModifiedListener>();

	/**
	 * Constructor.
	 *
	 * @param parent
	 *            parent {@link Composite}
	 * @param matchingRuleExpression
	 *            {@link MatchingRule} instance to be used for initialization of the contents. Can
	 *            be null, then no initialization will be performed.
	 */
	public MatchingRulesEditingElement(Composite parent, IExpression matchingRuleExpression) {
		Color color = parent.getBackground();
		final ManagedForm managedForm = new ManagedForm(parent);
		toolkit = managedForm.getToolkit();
		toolkit.setBackground(color);
		form = managedForm.getForm();
		form.setLayout(new GridLayout(1, false));
		form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		managedForm.getToolkit().decorateFormHeading(form.getForm());
		main = form.getBody();
		main.setBackground(color);
		main.setLayout(new GridLayout(7, false));
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		if (null != matchingRuleExpression) {
			initialize(matchingRuleExpression);
		}

	}

	/**
	 * Constructor.
	 *
	 * @param parent
	 *            parent {@link Composite}
	 */
	public MatchingRulesEditingElement(Composite parent) {
		this(parent, null);
	}

	/**
	 * Adds a {@link RuleEditingElementModifiedListener}.
	 *
	 * @param listener
	 *            {@link RuleEditingElementModifiedListener} instance to add.
	 */
	public void addModifyListener(RuleEditingElementModifiedListener listener) {
		listeners.add(listener);

	}

	/**
	 * Constructs a {@link IExpression} instance from the contents of this element controls.
	 *
	 * @return Returns a {@link IExpression} instance.
	 */
	public IExpression constructMatchingRuleExpression() {
		List<Expression> activeExpressions = new ArrayList<Expression>();
		for (AbstractRuleEditingElement ruleComposite : ruleElements) {
			Expression expression = ruleComposite.constructRuleExpression();
			if (null != expression) {
				activeExpressions.add(expression);
			}
		}
		IExpression matchingRuleExpression = null;
		if (!activeExpressions.isEmpty()) {
			Expression[] expressions = new Expression[activeExpressions.size()];
			activeExpressions.toArray(expressions);
			matchingRuleExpression = new OrExpression(expressions);
		}
		if (null == matchingRuleExpression) {
			matchingRuleExpression = new BooleanExpression(false);
		}
		return matchingRuleExpression;
	}

	/**
	 *
	 * @return The {@link Form} of this element.
	 */
	public ScrolledForm getForm() {
		return form;
	}

	/**
	 * Sets {@link GridData} for this element.
	 *
	 * @param gridData
	 *            {@link GridData}.
	 */
	public void setLayoutData(GridData gridData) {
		form.setLayoutData(gridData);
	}

	/**
	 * Resets all sub-elements.
	 */
	public void reset() {
		for (AbstractRuleEditingElement ruleElement : ruleElements) {
			ruleElement.dispose();
		}
		ruleElements.clear();
	}

	/**
	 * Initializes the contents of the sub-elements according to the {@link IExpression}.
	 *
	 * @param matchingRuleExpression
	 *            {@link IExpression} instance describing the content.
	 */
	public synchronized void initialize(IExpression matchingRuleExpression) {
		initializationPhase = true;
		if (matchingRuleExpression instanceof OrExpression) {
			for (Expression expression : ((OrExpression) matchingRuleExpression).getOperands()) {
				AbstractRuleEditingElement ruleComposite = MatchingRulesEditingElementFactory.createRuleComposite(expression);
				addNewRuleEditingElement(ruleComposite);
				ruleComposite.initialize(expression);
			}
		}
		main.layout(true, true);
		initializationPhase = false;
	}

	/**
	 * Adds a new rule editing element to this composite element.
	 *
	 * @param ruleComposite
	 *            {@link AbstractRuleEditingElement} instance to add.
	 */
	private void addNewRuleEditingElement(AbstractRuleEditingElement ruleComposite) {
		ruleComposite.createControls(main, toolkit);

		for (RuleEditingElementModifiedListener listener : listeners) {
			ruleComposite.addModifyListener(listener);
		}
		ruleComposite.addModifyListener(new RuleEditingElementModifiedListener() {

			@Override
			public void contentModified() {
				// TODO Auto-generated method stub

			}

			@Override
			public void elementDisposed(AbstractRuleEditingElement ruleComposite) {
				// execute update only if the rules element is not in an initialization phase
				if (!initializationPhase) {
					ruleElements.remove(ruleComposite);
					main.layout(true, true);
					for (RuleEditingElementModifiedListener listener : listeners) {
						listener.contentModified();
					}
				}
			}
		});
		ruleElements.add(ruleComposite);

	}

	/**
	 * Reinitializes the contents of the sub-elements according to the {@link IExpression}.
	 *
	 * @param matchingRuleExpression
	 *            {@link IExpression} instance describing the content.
	 */
	public synchronized void reinitialize(IExpression matchingRuleExpression) {
		initializationPhase = true;
		reset();
		initialize(matchingRuleExpression);
		initializationPhase = false;
	}

	/**
	 * Marks the receiver as visible if the argument is true, and marks it invisible otherwise.
	 *
	 * @param visible
	 *            the new visibility state
	 */
	public void setVisible(boolean visible) {
		main.getParent().setVisible(visible);
		main.setVisible(visible);
	}

	/**
	 * Adds a new rule editing element to this composite element.
	 *
	 * @param matchingRuleType
	 *            {@link MatchingRuleType} specifying the type of the sub-element.
	 */
	public void addNewRuleComposite(MatchingRulesEditingElementFactory.MatchingRuleType matchingRuleType) {
		AbstractRuleEditingElement ruleComposite = MatchingRulesEditingElementFactory.createRuleComposite(matchingRuleType);
		addNewRuleEditingElement(ruleComposite);
		main.layout(true, true);
		for (RuleEditingElementModifiedListener listener : listeners) {
			listener.contentModified();
		}

	}

}
