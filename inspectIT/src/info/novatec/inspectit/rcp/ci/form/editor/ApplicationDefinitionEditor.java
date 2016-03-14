package info.novatec.inspectit.rcp.ci.form.editor;

import info.novatec.inspectit.ci.business.ApplicationDefinition;
import info.novatec.inspectit.cmr.configuration.business.IApplicationDefinition;
import info.novatec.inspectit.exception.BusinessException;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.ci.form.input.ApplicationDefinitionEditorInput;
import info.novatec.inspectit.rcp.ci.form.page.ApplicationDefinitionPage;
import info.novatec.inspectit.rcp.ci.form.page.BusinessTransactionPage;
import info.novatec.inspectit.rcp.ci.listener.IApplicationDefinitionChangeListener;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition.OnlineStatus;

import java.util.Objects;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

/**
 * Editor for the {@link ApplicationDefinition}.
 *
 * @author Alexander Wert
 *
 */
public class ApplicationDefinitionEditor extends AbstractConfigurationInterfaceFormEditor implements IApplicationDefinitionChangeListener {

	/**
	 * Editor ID.
	 */
	public static final String ID = "info.novatec.inspectit.rcp.ci.editor.applicationDefinitionEditor";

	/**
	 * Application definition page.
	 */
	private ApplicationDefinitionPage applicationDefinitionPage;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (!(input instanceof ApplicationDefinitionEditorInput)) {
			throw new PartInitException("Editor input must be of a type: " + ApplicationDefinitionEditorInput.class.getName());
		}

		setSite(site);
		setInput(input);

		ApplicationDefinitionEditorInput applicationDefinitionEditorInput = (ApplicationDefinitionEditorInput) input;
		setPartName(applicationDefinitionEditorInput.getName());
		setTitleImage(InspectIT.getDefault().getImage(InspectITImages.IMG_BUSINESS_CONTEXT));

		InspectIT.getDefault().getInspectITConfigurationInterfaceManager().addApplicationDefinitionChangeListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addPages() {
		try {
			applicationDefinitionPage = new ApplicationDefinitionPage(this);
			addPage(applicationDefinitionPage);
			addPage(new BusinessTransactionPage(this));
		} catch (PartInitException e) {
			InspectIT.getDefault().log(IStatus.ERROR, "Error occurred trying to open the Application Definition editor.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		monitor.beginTask("Saving appplication definition..", IProgressMonitor.UNKNOWN);

		if (!checkValid()) {
			monitor.setCanceled(true);
			monitor.done();
			return;
		}

		ApplicationDefinitionEditorInput applicationDefinitionEditorInput = (ApplicationDefinitionEditorInput) getEditorInput();
		CmrRepositoryDefinition cmrRepositoryDefinition = applicationDefinitionEditorInput.getCmrRepositoryDefinition();
		IApplicationDefinition applicationDefinition = applicationDefinitionEditorInput.getApplication();

		if (cmrRepositoryDefinition.getOnlineStatus() != OnlineStatus.OFFLINE) {
			try {
				commitPages(true);
				applicationDefinitionPage.doSave();
				applicationDefinition = applicationDefinitionEditorInput.getApplication();

				// store changes to CMR
				cmrRepositoryDefinition.getBusinessContextMangementService().updateApplicationDefinition(applicationDefinition);

				// update listeners
				InspectIT.getDefault().getInspectITConfigurationInterfaceManager().applicationUpdated(applicationDefinition, cmrRepositoryDefinition);

				// set no exception and fire dirty state changed
				setExceptionOnSave(false);
				editorDirtyStateChanged();
			} catch (BusinessException e) {
				monitor.setCanceled(true);
				setExceptionOnSave(true);
				editorDirtyStateChanged();
				InspectIT.getDefault().createErrorDialog("Saving of the application definition '" + applicationDefinition.getApplicationName() + "' failed due to the exception on the CMR.", e, -1);
			} catch (Throwable t) { // NOPMD
				monitor.setCanceled(true);
				setExceptionOnSave(true);
				editorDirtyStateChanged();
				InspectIT.getDefault().createErrorDialog("Unexpected exception occurred during an attempt to save the application '" + applicationDefinition.getApplicationName() + "'.", t, -1);
			}
		} else {
			monitor.setCanceled(true);
			InspectIT.getDefault().createErrorDialog("Saving of the application '" + applicationDefinition.getApplicationName() + "' failed because CMR is currently not online.", -1);
		}

		monitor.done();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void applicationCreated(IApplicationDefinition application, int positionIndex, CmrRepositoryDefinition repositoryDefinition) {
		// nothing to do here

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void applicationMoved(IApplicationDefinition application, int oldPositionIndex, int newPositionIndex, CmrRepositoryDefinition repositoryDefinition) {
		applicationUpdated(application, repositoryDefinition);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void applicationUpdated(IApplicationDefinition application, CmrRepositoryDefinition repositoryDefinition) {
		ApplicationDefinitionEditorInput input = (ApplicationDefinitionEditorInput) getEditorInput();

		if (!Objects.equals(repositoryDefinition, input.getCmrRepositoryDefinition())) {
			return;
		}

		if (Objects.equals(input.getApplication().getId(), application.getId())) {
			final ApplicationDefinitionEditorInput newInput = new ApplicationDefinitionEditorInput(application, repositoryDefinition);
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					setPartName(newInput.getName());
					setInputWithNotify(newInput);
				}
			});
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void applicationDeleted(IApplicationDefinition application, CmrRepositoryDefinition repositoryDefinition) {
		ApplicationDefinitionEditorInput input = (ApplicationDefinitionEditorInput) getEditorInput();

		if (!Objects.equals(repositoryDefinition, input.getCmrRepositoryDefinition())) {
			return;
		}

		if (Objects.equals(input.getApplication().getId(), application.getId())) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					close(false);
				}
			});
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		InspectIT.getDefault().getInspectITConfigurationInterfaceManager().removeApplicationDefinitionChangeListener(this);
		super.dispose();
	}

}