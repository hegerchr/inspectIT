package info.novatec.inspectit.rcp.model.ci;

import info.novatec.inspectit.cmr.configuration.business.IApplicationDefinition;
import info.novatec.inspectit.rcp.model.Leaf;
import info.novatec.inspectit.rcp.provider.IApplicationProvider;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;

import java.util.List;

import org.eclipse.core.runtime.Assert;

/**
 * Application leaf for displaying in the tree.
 *
 * @author Alexander Wert
 *
 */
public class ApplicationLeaf extends Leaf implements IApplicationProvider {
	/**
	 * {@link IApplicationDefinition}.
	 */
	private IApplicationDefinition application;
	/**
	 * {@link CmrRepositoryDefinition}.
	 */
	private final CmrRepositoryDefinition cmrRepositoryDefinition;

	/**
	 * The parent list containing the {@link IApplicationDefinition} instance represented by this
	 * {@link ApplicationLeaf}.
	 */
	private final List<? extends IApplicationProvider> parentList;

	/**
	 * Default constructor.
	 *
	 * @param application
	 *            {@link IApplicationDefinition}
	 * @param parentList
	 *            The parent list containing the {@link IApplicationDefinition} instance represented
	 *            by this {@link ApplicationLeaf}.
	 * @param cmrRepositoryDefinition
	 *            {@link CmrRepositoryDefinition}.
	 */
	public ApplicationLeaf(IApplicationDefinition application, List<? extends IApplicationProvider> parentList, CmrRepositoryDefinition cmrRepositoryDefinition) {
		super();
		Assert.isNotNull(application);
		Assert.isNotNull(cmrRepositoryDefinition);
		this.setApplication(application);
		this.cmrRepositoryDefinition = cmrRepositoryDefinition;
		this.setName(application.getApplicationName());
		this.setTooltip(application.getApplicationName());
		this.parentList = parentList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getParentListSize() {
		return getParentList().size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getIndexInParentList() {
		return getParentList().indexOf(this);
	}

	@Override
	public CmrRepositoryDefinition getCmrRepositoryDefinition() {
		return cmrRepositoryDefinition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IApplicationDefinition getApplication() {
		return application;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((application == null) ? 0 : application.hashCode());
		result = prime * result + ((cmrRepositoryDefinition == null) ? 0 : cmrRepositoryDefinition.hashCode());
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
		if (null == obj) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ApplicationLeaf other = (ApplicationLeaf) obj;
		if (application == null) {
			if (other.application != null) {
				return false;
			}
		} else if (!application.equals(other.application)) {
			return false;
		}
		if (cmrRepositoryDefinition == null) {
			if (other.cmrRepositoryDefinition != null) {
				return false;
			}
		} else if (!cmrRepositoryDefinition.equals(other.cmrRepositoryDefinition)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<? extends IApplicationProvider> getParentList() {
		return parentList;
	}

	/**
	 * Sets {@link #application}.
	 *
	 * @param application
	 *            New value for {@link #application}
	 */
	public void setApplication(IApplicationDefinition application) {
		this.application = application;
	}

}