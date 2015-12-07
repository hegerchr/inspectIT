package info.novatec.inspectit.rcp.ci.view.matchingrules;

import info.novatec.inspectit.ci.business.BusinessTransactionDefinition;
import info.novatec.inspectit.ci.business.Expression;
import info.novatec.inspectit.ci.business.StringMatchingExpression;
import info.novatec.inspectit.cmr.configuration.business.IApplicationDefinition;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * THis abstract class forms the basis for all editing elements allowing to view and modify specific
 * instances of {@link Expression} as part of a {@link IMatchingRule} for an
 * {@link IApplicationDefinition} or a {@link BusinessTransactionDefinition}.
 *
 * @author Alexander Wert
 *
 */
public abstract class AbstractRuleEditingElement {

	/**
	 * Listeners that are notified when this element is modified or disposed.
	 */
	protected List<RuleEditingElementModifiedListener> modifyListeners = new ArrayList<RuleEditingElementModifiedListener>();

	/**
	 * Switch for the search in depth property of the {@link Expression}.
	 */
	private boolean searchInDepth = false;

	/**
	 * Search depth property when {@link #searchInDepth} is enabled.
	 */
	private int searchDepth = -1;

	/**
	 * Check box to select {@link #searchInDepth} property.
	 */
	private Button searchInTraceCheckBox;

	/**
	 * Spinner for configuring the {@link #searchDepth} property.
	 */
	private Spinner depthSpinner;

	/**
	 * Label for the {@link #searchInDepth} property.
	 */
	private Label depthLabel;

	/**
	 * Name of the editing element.
	 */
	private final String name;

	/**
	 * Indicates whether the searchInDepth sub-element shell be used in this editing element.
	 */
	private final boolean useSearchInDepthComposite;

	/**
	 * Label containing an Icon for heading.
	 */
	private Label icon;

	/**
	 * Heading text.
	 */
	private FormText headingText;

	/**
	 * FormText containing delete button.
	 */
	private FormText deleteText;

	/**
	 * Dummy label to fill the grid layout.
	 */
	private Label searchInTraceFillLabel;

	/**
	 * Indicates whether listeners shell be notified in the current state.
	 */
	private boolean notificationActive = true;

	/**
	 * Default constructor.
	 *
	 * @param name
	 *            Name of the editing element.
	 * @param useSearchInDepthComposite
	 *            Indicates whether the searchInDepth sub-element shell be used in this editing
	 *            element.
	 */
	public AbstractRuleEditingElement(String name, boolean useSearchInDepthComposite) {
		this.name = name;
		this.useSearchInDepthComposite = useSearchInDepthComposite;
	}

	/**
	 * Creates controls for this editing element.
	 *
	 * @param parent
	 *            parent {@link Composite}.
	 * @param toolkit
	 *            {@link FormToolkit} to use for the creation of controls.
	 */
	public void createControls(Composite parent, FormToolkit toolkit) {
		icon = toolkit.createLabel(parent, "");
		icon.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		icon.setImage(InspectIT.getDefault().getImage(InspectITImages.IMG_PROPERTIES));

		headingText = toolkit.createFormText(parent, false);
		headingText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		headingText.setText("<form><p><b>" + name + "</b></p></form>", true, false);

		deleteText = toolkit.createFormText(parent, false);
		deleteText.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 2 + getNumRows()));
		deleteText.setText("<form><p><a href=\"delete\"><img href=\"deleteImg\" /></a></p></form>", true, false);
		deleteText.setImage("deleteImg", InspectIT.getDefault().getImage(InspectITImages.IMG_DELETE));
		deleteText.addHyperlinkListener(new IHyperlinkListener() {

			@Override
			public void linkExited(HyperlinkEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void linkActivated(HyperlinkEvent e) {
				dispose();
			}
		});

		createSpecificElements(parent, toolkit);
		if (useSearchInDepthComposite) {
			searchInTraceFillLabel = toolkit.createLabel(parent, "");
			searchInTraceFillLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
			searchInTraceCheckBox = toolkit.createButton(parent, "search in trace", SWT.CHECK);
			searchInTraceCheckBox.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

			depthLabel = toolkit.createLabel(parent, "maximum search depth: ");
			depthLabel.setEnabled(false);
			depthLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

			depthSpinner = new Spinner(parent, SWT.BORDER);
			depthSpinner.setMinimum(-1);
			depthSpinner.setMaximum(Integer.MAX_VALUE);
			depthSpinner.setSelection(-1);
			depthSpinner.setIncrement(1);
			depthSpinner.setPageIncrement(100);
			depthSpinner.setEnabled(false);
			depthSpinner.setToolTipText("A value of -1 means that no limit for the search depth is used!");
			depthSpinner.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			depthSpinner.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					searchDepth = depthSpinner.getSelection();
					notifyModifyListeners();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub

				}

			});
			searchInTraceCheckBox.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (searchInTraceCheckBox.getSelection()) {
						depthLabel.setEnabled(true);
						depthSpinner.setEnabled(true);
						searchInDepth = true;
						searchDepth = depthSpinner.getSelection();
					} else {
						depthLabel.setEnabled(false);
						depthSpinner.setEnabled(false);
						searchInDepth = false;
					}
					notifyModifyListeners();

				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub

				}
			});
		}
	}

	/**
	 * Creates specific controls of this editing element.
	 *
	 * @param parent
	 *            parent {@link Composite}.
	 * @param toolkit
	 *            {@link FormToolkit} to use for the creation of controls.
	 */
	protected abstract void createSpecificElements(Composite parent, FormToolkit toolkit);

	/**
	 * Disposes specific controls of this editing element.
	 */
	protected abstract void disposeSpecificElements();

	/**
	 * Returns the number of rows the body of the corresponding editing element spans.
	 *
	 * @return Returns the number of rows the body of the corresponding editing element spans.
	 */
	protected abstract int getNumRows();

	/**
	 * Constructs an {@link Expression} from the contents of the editing element controls.
	 *
	 * @return an {@link Expression}.
	 */
	public abstract Expression constructRuleExpression();

	/**
	 * Gets {@link #searchInDepth}.
	 *
	 * @return {@link #searchInDepth}
	 */
	public boolean isSearchInDepth() {
		return searchInDepth;
	}

	/**
	 * Gets {@link #searchDepth}.
	 *
	 * @return {@link #searchDepth}
	 */
	public int getSearchDepth() {
		return searchDepth;
	}

	/**
	 * Adds a {@link RuleEditingElementModifiedListener} to this editing element.
	 *
	 * @param listener
	 *            {@link RuleEditingElementModifiedListener} to add.
	 */
	public void addModifyListener(RuleEditingElementModifiedListener listener) {
		modifyListeners.add(listener);
	}

	/**
	 * Notifies all modify listeners about a modification of the controls content.
	 */
	protected void notifyModifyListeners() {
		if (notificationActive) {
			for (RuleEditingElementModifiedListener listener : modifyListeners) {
				listener.contentModified();
			}
		}
	}

	/**
	 * Notifies all modify listeners about the disposition of this editing element.
	 */
	protected void notifyDisposed() {
		if (notificationActive) {
			for (RuleEditingElementModifiedListener listener : modifyListeners) {
				listener.elementDisposed(this);
			}
		}
	}

	/**
	 * Initializes the content of the controls comprised in this editing element.
	 *
	 * @param expression
	 *            An {@link Expression} determining the content.
	 */
	public void initialize(Expression expression) {
		notificationActive = false;
		executeSpecificInitialization(expression);
		notificationActive = true;
	}

	/**
	 * Executes sub-class specific initialization of the controls.
	 *
	 * @param expression
	 *            An {@link Expression} determining the content.
	 */
	protected void executeSpecificInitialization(Expression expression) {
		if (expression instanceof StringMatchingExpression && ((StringMatchingExpression) expression).isSearchNodeInTrace() && useSearchInDepthComposite) {
			searchDepth = ((StringMatchingExpression) expression).getMaxSearchDepth();
			searchInDepth = true;
			searchInTraceCheckBox.setSelection(true);

			depthSpinner.setSelection(searchDepth);
			depthSpinner.setEnabled(true);
			depthLabel.setEnabled(true);
		}
	}

	/**
	 *
	 */
	public void dispose() {
		disposeSpecificElements();
		deleteText.dispose();
		headingText.dispose();
		icon.dispose();
		if (useSearchInDepthComposite) {
			searchInTraceCheckBox.dispose();
			depthLabel.dispose();
			depthSpinner.dispose();
			searchInTraceFillLabel.dispose();
		}
		notifyDisposed();
	}
}
