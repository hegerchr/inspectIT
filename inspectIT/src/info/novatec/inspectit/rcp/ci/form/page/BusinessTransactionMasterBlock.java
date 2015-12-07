/**
 *
 */
package info.novatec.inspectit.rcp.ci.form.page;

import info.novatec.inspectit.ci.business.BusinessTransactionDefinition;
import info.novatec.inspectit.cmr.configuration.business.IApplicationDefinition;
import info.novatec.inspectit.cmr.configuration.business.IBusinessTransactionDefinition;
import info.novatec.inspectit.exception.BusinessException;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.ci.form.input.ApplicationDefinitionEditorInput;
import info.novatec.inspectit.rcp.ci.form.part.BusinessTransactionRulesPage;
import info.novatec.inspectit.rcp.ci.testers.BusinessContextTester;
import info.novatec.inspectit.rcp.ci.wizard.CreateBusinessTransactionWizard;
import info.novatec.inspectit.rcp.dialog.EditNameDescriptionDialog;
import info.novatec.inspectit.rcp.editor.viewers.StyledCellIndexLabelProvider;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IPartSelectionListener;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class BusinessTransactionMasterBlock extends MasterDetailsBlock implements IFormPart, IPartSelectionListener, ISelectionChangedListener, IPropertyListener {

	/**
	 * Menu id.
	 */
	public static final String MENU_ID = "info.novatec.inspectit.rcp.ci.view.businessTransactionsView";

	/**
	 * Property tester for selected {@link IBusinessTransactionDefinition} instances.
	 */
	private final BusinessContextTester propertyTester = new BusinessContextTester();

	/**
	 * Table viewer in the form.
	 */
	protected TableViewer tableViewer;

	/**
	 * Managed form to report to.
	 */
	private IManagedForm managedForm;

	/**
	 * {@link FormPage} section belongs to.
	 */
	private final FormPage formPage;

	private IApplicationDefinition applicationDefinition;
	/**
	 * Dirty state.
	 */
	private boolean dirty;

	public BusinessTransactionMasterBlock(FormPage formPage) {
		this.formPage = formPage;
		this.formPage.getEditor().addPropertyListener(this);
		ApplicationDefinitionEditorInput input = (ApplicationDefinitionEditorInput) formPage.getEditor().getEditorInput();
		applicationDefinition = input.getApplication();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createContent(IManagedForm managedForm, Composite parent) {
		super.createContent(managedForm, parent);

		// set sashes for more space a bit in bottom
		sashForm.setOrientation(SWT.VERTICAL);
		sashForm.setWeights(new int[] { 3, 4 });
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		this.detailsPart = detailsPart;
		detailsPart.registerPage(BusinessTransactionDefinition.class,
				new BusinessTransactionRulesPage(formPage, "Business Transaction Mapping", managedForm.getToolkit(), Section.TITLE_BAR | Section.EXPANDED | Section.TWISTIE));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createToolBarActions(IManagedForm managedForm) {

		final CreateBusinessTransactionAction createAction = new CreateBusinessTransactionAction();
		createAction.setEnabled(true);
		final MoveUpAction moveUpAction = new MoveUpAction();
		moveUpAction.setEnabled(false);
		final MoveDownAction moveDownAction = new MoveDownAction();
		moveDownAction.setEnabled(false);
		final DeleteBusinessTransactionAction deleteAction = new DeleteBusinessTransactionAction();
		deleteAction.setEnabled(false);
		final EditAction editAction = new EditAction();
		editAction.setEnabled(false);

		// toolbar
		IToolBarManager toolBarManager = managedForm.getForm().getToolBarManager();
		// ToolBar toolbar = toolBarManager.createControl(getSection());
		toolBarManager.add(createAction);
		toolBarManager.add(moveUpAction);
		toolBarManager.add(moveDownAction);
		toolBarManager.add(editAction);
		toolBarManager.add(deleteAction);
		toolBarManager.update(true);

		// menu
		MenuManager menuManager = new MenuManager();
		menuManager.add(createAction);
		menuManager.add(moveUpAction);
		menuManager.add(moveDownAction);
		menuManager.add(editAction);
		menuManager.add(deleteAction);
		formPage.getSite().registerContextMenu(menuManager, tableViewer);
		Control control = tableViewer.getControl();
		Menu menu = menuManager.createContextMenu(control);
		control.setMenu(menu);
		tableViewer.addSelectionChangedListener(this);
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				// manage enabled status
				boolean noneSelected = true;
				if (selection instanceof StructuredSelection) {
					StructuredSelection sSelection = (StructuredSelection) selection;
					if (sSelection.size() == 1) {
						IApplicationDefinition application = getApplication();
						boolean isDefaultBTx = propertyTester.test(sSelection.getFirstElement(), BusinessContextTester.IS_DEFAULT_BTX_PROPERTY, new Object[] { application }, null);
						boolean canMoveUp = propertyTester.test(sSelection.getFirstElement(), BusinessContextTester.CAN_MOVE_UP_PROPERTY, new Object[] { application }, null);
						boolean canMoveDown = propertyTester.test(sSelection.getFirstElement(), BusinessContextTester.CAN_MOVE_DOWN_PROPERTY, new Object[] { application }, null);

						moveUpAction.setEnabled(canMoveUp && !isDefaultBTx);
						moveDownAction.setEnabled(canMoveDown && !isDefaultBTx);
						deleteAction.setEnabled(!isDefaultBTx);
						editAction.setEnabled(!isDefaultBTx);
						noneSelected = false;
					} else if (sSelection.size() > 1) {
						boolean containsDefaultBTx = false;
						IApplicationDefinition application = getApplication();
						for (Object element : sSelection.toList()) {
							if (propertyTester.test(element, BusinessContextTester.IS_DEFAULT_BTX_PROPERTY, new Object[] { application }, null)) {
								containsDefaultBTx = true;
								break;
							}
						}
						moveUpAction.setEnabled(false);
						moveDownAction.setEnabled(false);
						editAction.setEnabled(false);
						deleteAction.setEnabled(!containsDefaultBTx);
						noneSelected = false;
					}
				}
				if (noneSelected) {
					moveUpAction.setEnabled(false);
					moveDownAction.setEnabled(false);
					deleteAction.setEnabled(false);
					editAction.setEnabled(false);
				}
			}
		});

		updateContent(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		this.managedForm = managedForm;
		managedForm.addPart(this);
		FormToolkit toolkit = managedForm.getToolkit();

		int borderStyle = toolkit.getBorderStyle();
		toolkit.setBorderStyle(SWT.NO);
		Table table = toolkit.createTable(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);

		tableViewer = new TableViewer(table);
		toolkit.setBorderStyle(borderStyle);

		TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		viewerColumn.getColumn().setMoveable(true);
		viewerColumn.getColumn().setResizable(true);
		viewerColumn.getColumn().setText("Business Transaction");
		viewerColumn.getColumn().setWidth(250);
		viewerColumn.getColumn().setToolTipText("Business Transaction Name.");

		viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		viewerColumn.getColumn().setMoveable(true);
		viewerColumn.getColumn().setResizable(true);
		viewerColumn.getColumn().setText("Description");
		viewerColumn.getColumn().setWidth(300);
		viewerColumn.getColumn().setToolTipText("Description.");

		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new BusinessTransactionLabelProvider());

		tableViewer.setComparator(getViewerComparator());
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setVisible(true);
		tableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.NO_RECREATE);

		this.managedForm.getMessageManager().addMessage(this, "Pay attention to the order of business transaction definitions!", null, IMessageProvider.INFORMATION);
	}

	/**
	 * Retrieves current {@link IApplicationDefinition} instance under modification.
	 *
	 * @return Returns current {@link IApplicationDefinition} instance under modification.
	 */
	private IApplicationDefinition getApplication() {
		return applicationDefinition;
	}

	/**
	 * Updates the content of the table view.
	 *
	 * @param selection
	 *            selection to be applied to the table view. If null, no selection will be applied.
	 */
	protected void updateContent(final ISelection selection) {
		if (null != tableViewer) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					tableViewer.setInput(getApplication().getBusinessTransactionDefinitions());
					if (null != selection) {
						tableViewer.setSelection(selection, true);
						tableViewer.getTable().setFocus();
					} else if (tableViewer.getTable().getItemCount() > 0) {
						ISelection firstSelection = new StructuredSelection(tableViewer.getElementAt(0));
						tableViewer.setSelection(firstSelection, true);
						tableViewer.getTable().setFocus();
					}
					tableViewer.getTable().layout();
				}
			});
		}
	}

	/**
	 * Returns comparator to be used in the tree viewer. Default implementation returns a comparator
	 * based on the {@link Comparable} interface, which means no-comparator.
	 *
	 * Sub-class can override.
	 *
	 * @return Returns comparator to be used in the tree viewer.
	 */
	protected ViewerComparator getViewerComparator() {
		// just compare based on the comparable interface
		return new ViewerComparator() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof Comparable && e2 instanceof Comparable) {
					return ((Comparable) e1).compareTo(e2);
				}
				return 0;
			}
		};
	}

	/**
	 * Checks if we are in dirty state.
	 */
	private void markDirty() {
		if (!dirty) {
			dirty = true;
			managedForm.dirtyStateChanged();
		}
	}

	/**
	 * Moves a {@link IBusinessTransactionDefinition} in the parent list contained in the
	 * corresponding {@link IApplicationDefinition}.
	 *
	 * @param businessTransaction
	 *            {@link IBusinessTransactionDefinition} instance to move.
	 * @param up
	 *            indicates whether to move up or down. If true moves up, otherwise down.
	 */
	private synchronized void moveBusinessTransaction(IBusinessTransactionDefinition businessTransaction, boolean up) {
		IApplicationDefinition application = getApplication();
		int currentIndex = application.getBusinessTransactionDefinitions().indexOf(businessTransaction);
		int newIndex = currentIndex;
		if (up) {
			newIndex--;
		} else {
			newIndex++;
		}
		if (newIndex < 0 || newIndex >= application.getBusinessTransactionDefinitions().size() - 1) {
			return;
		}
		IBusinessTransactionDefinition businessTxToMove = businessTransaction;
		application.deleteBusinessTransactionDefinition(businessTxToMove);
		try {
			application.addBusinessTransactionDefinition(businessTxToMove, newIndex);
			markDirty();
		} catch (BusinessException e) {
			InspectIT.getDefault().createErrorDialog("Moving of the business transaction definition '" + businessTxToMove.getBusinessTransactionName() + "' failed due to the following exception.", e,
					-1);
		}

	}

	/**
	 * Label provider for the business transaction table view.
	 *
	 * @author Alexander Wert
	 *
	 */
	public static class BusinessTransactionLabelProvider extends StyledCellIndexLabelProvider {
		/**
		 * Empty.
		 */
		private final StyledString empty = new StyledString();

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected StyledString getStyledText(Object element, int index) {
			if (element instanceof IBusinessTransactionDefinition) {
				IBusinessTransactionDefinition bTxDef = (IBusinessTransactionDefinition) element;
				String content = "";
				switch (index) {
				case 0:
					content = bTxDef.getBusinessTransactionName();
					break;
				case 1:
					content = (bTxDef.getDescription() != null) ? bTxDef.getDescription() : "";
					break;
				default:
					return empty;
				}
				if (bTxDef.getId() == BusinessTransactionDefinition.DEFAULT_ID) {
					return new StyledString(content, StyledString.QUALIFIER_STYLER);
				} else {
					return new StyledString(content);
				}

			}

			return empty;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Image getColumnImage(Object element, int index) {
			if (element instanceof IBusinessTransactionDefinition) {
				IBusinessTransactionDefinition bTxDef = (IBusinessTransactionDefinition) element;
				switch (index) {
				case 0:
					if (bTxDef.getId() == BusinessTransactionDefinition.DEFAULT_ID) {
						return InspectIT.getDefault().getImage(InspectITImages.IMG_BUSINESS_TRANSACTION_GREY);
					} else {
						return InspectIT.getDefault().getImage(InspectITImages.IMG_BUSINESS_TRANSACTION);
					}

				default:
					return super.getColumnImage(element, index);
				}
			}
			return super.getColumnImage(element, index);
		}

	}

	/**
	 * This action creates a new {@link IBusinessTransactionDefinition}.
	 *
	 * @author Alexander Wert
	 *
	 */
	private class CreateBusinessTransactionAction extends Action {
		@Override
		public ImageDescriptor getImageDescriptor() {
			return InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_ADD);
		}

		@Override
		public String getText() {
			return "Create";
		}

		@Override
		public void run() {
			CreateBusinessTransactionWizard wizard = new CreateBusinessTransactionWizard(getApplication());
			WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
			dialog.setTitle(wizard.getWindowTitle());
			int returnCode = dialog.open();
			if (returnCode == Dialog.OK) {
				IBusinessTransactionDefinition businessTX = wizard.getNewBusinessTransaction();
				StructuredSelection newSelection = new StructuredSelection(businessTX);
				updateContent(newSelection);
				markDirty();
			}

		}
	}

	/**
	 * This action deletes a {@link IBusinessTransactionDefinition}.
	 *
	 * @author Alexander Wert
	 *
	 */
	private class DeleteBusinessTransactionAction extends Action {
		@Override
		public ImageDescriptor getImageDescriptor() {
			return InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_DELETE);
		}

		@Override
		public String getText() {
			return "Delete";
		}

		@Override
		public void run() {
			ISelection selection = tableViewer.getSelection();
			if (selection instanceof StructuredSelection) {
				for (Object selectedElement : ((StructuredSelection) selection).toList()) {
					getApplication().deleteBusinessTransactionDefinition((IBusinessTransactionDefinition) selectedElement);
				}
				updateContent(null);
				managedForm.fireSelectionChanged(BusinessTransactionMasterBlock.this, StructuredSelection.EMPTY);
				markDirty();
			}
		}

	}

	/**
	 * This action moves up a {@link IBusinessTransactionDefinition}.
	 *
	 * @author Alexander Wert
	 *
	 */
	private class MoveUpAction extends Action {
		@Override
		public ImageDescriptor getImageDescriptor() {
			return InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_PREVIOUS);
		}

		@Override
		public String getText() {
			return "Move Up";
		}

		@Override
		public void run() {
			ISelection selection = tableViewer.getSelection();
			if (selection instanceof StructuredSelection) {
				IBusinessTransactionDefinition businessTX = (IBusinessTransactionDefinition) ((StructuredSelection) selection).getFirstElement();
				moveBusinessTransaction(businessTX, true);
				StructuredSelection newSelection = new StructuredSelection(businessTX);
				updateContent(newSelection);
			}
		}

	}

	/**
	 * This action moves down a {@link IBusinessTransactionDefinition}.
	 *
	 * @author Alexander Wert
	 *
	 */
	private class MoveDownAction extends Action {
		@Override
		public ImageDescriptor getImageDescriptor() {
			return InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_NEXT);
		}

		@Override
		public String getText() {
			return "Move Down";
		}

		@Override
		public void run() {
			ISelection selection = tableViewer.getSelection();
			if (selection instanceof StructuredSelection) {
				IBusinessTransactionDefinition businessTX = (IBusinessTransactionDefinition) ((StructuredSelection) selection).getFirstElement();
				moveBusinessTransaction(businessTX, false);
				StructuredSelection newSelection = new StructuredSelection(businessTX);
				updateContent(newSelection);
			}
		}
	}

	/**
	 * This action opens up a wizard to change the name and description of a
	 * {@link IBusinessTransactionDefinition}.
	 *
	 * @author Alexander Wert
	 *
	 */
	private class EditAction extends Action {
		@Override
		public ImageDescriptor getImageDescriptor() {
			return InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_EDIT);
		}

		@Override
		public String getText() {
			return "Edit";
		}

		@Override
		public void run() {
			ISelection selection = tableViewer.getSelection();
			if (selection instanceof StructuredSelection) {
				IBusinessTransactionDefinition businessTransactionDef = (IBusinessTransactionDefinition) ((StructuredSelection) selection).getFirstElement();
				IApplicationDefinition appDefinition = getApplication();
				String[] existingBusinessTxNames = new String[appDefinition.getBusinessTransactionDefinitions().size()];
				int i = 0;
				for (IBusinessTransactionDefinition businessTx : appDefinition.getBusinessTransactionDefinitions()) {
					existingBusinessTxNames[i] = businessTx.getBusinessTransactionName();
					i++;
				}
				EditNameDescriptionDialog dialog = new EditNameDescriptionDialog(Display.getCurrent().getActiveShell(), businessTransactionDef.getBusinessTransactionName(),
						businessTransactionDef.getDescription(), "Edit Business Transaction", "Enter new business transaction name and/or description", existingBusinessTxNames);
				if (Dialog.OK == dialog.open()) {
					businessTransactionDef.setBusinessTransactionName(dialog.getName());
					if (StringUtils.isNotBlank(dialog.getDescription())) {
						businessTransactionDef.setDescription(dialog.getDescription());
					}
					StructuredSelection newSelection = new StructuredSelection(businessTransactionDef);
					updateContent(newSelection);
					markDirty();
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChanged(Object source, int propId) {
		if (propId == IEditorPart.PROP_INPUT) {
			ApplicationDefinitionEditorInput input = (ApplicationDefinitionEditorInput) formPage.getEditor().getEditorInput();
			applicationDefinition = input.getApplication();
			updateContent(null);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (!event.getSelection().isEmpty()) {
			processSelectionChanged((StructuredSelection) event.getSelection());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		if (!selection.isEmpty()) {
			processSelectionChanged((StructuredSelection) selection);
		}
	}

	private void processSelectionChanged(StructuredSelection selection) {
		managedForm.fireSelectionChanged(this, selection);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize(IManagedForm form) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		this.formPage.getEditor().removePropertyListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void commit(boolean onSave) {
		if (onSave) {
			dirty = false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setFormInput(Object input) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		tableViewer.getTable().setFocus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStale() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
	}

}
