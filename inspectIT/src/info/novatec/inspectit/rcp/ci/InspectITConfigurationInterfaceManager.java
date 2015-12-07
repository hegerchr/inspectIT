package info.novatec.inspectit.rcp.ci;

import info.novatec.inspectit.ci.AgentMappings;
import info.novatec.inspectit.ci.Environment;
import info.novatec.inspectit.ci.Profile;
import info.novatec.inspectit.cmr.configuration.business.IApplicationDefinition;
import info.novatec.inspectit.rcp.ci.listener.IAgentMappingsChangeListener;
import info.novatec.inspectit.rcp.ci.listener.IApplicationDefinitionChangeListener;
import info.novatec.inspectit.rcp.ci.listener.IEnvironmentChangeListener;
import info.novatec.inspectit.rcp.ci.listener.IProfileChangeListener;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.util.ListenerList;

/**
 * Manager for the CI related UI actions. Listens and delegates the CI events.
 * 
 * @author Ivan Senic
 * 
 */
public class InspectITConfigurationInterfaceManager implements IProfileChangeListener, IEnvironmentChangeListener, IAgentMappingsChangeListener, IApplicationDefinitionChangeListener {

	/**
	 * List of {@link IProfileChangeListener}s.
	 */
	private ListenerList<IProfileChangeListener> profileChangeListeners = new ListenerList<>();

	/**
	 * List of {@link IEnvironmentChangeListener}s.
	 */
	private ListenerList<IEnvironmentChangeListener> environmentChangeListeners = new ListenerList<>();

	/**
	 * List of {@link IAgentMappingsChangeListener}s.
	 */
	private ListenerList<IAgentMappingsChangeListener> agentMappingChangeListeners = new ListenerList<>();

	/**
	 * List of {@link IApplicationDefinitionChangeListener}s.
	 */
	private ListenerList<IApplicationDefinitionChangeListener> applicationChangeListeners = new ListenerList<>();

	/**
	 * {@inheritDoc}
	 */
	public void profileCreated(Profile profile, CmrRepositoryDefinition repositoryDefinition) {
		for (IProfileChangeListener listener : profileChangeListeners) {
			listener.profileCreated(profile, repositoryDefinition);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void profileUpdated(Profile profile, CmrRepositoryDefinition repositoryDefinition, boolean onlyProperties) {
		for (IProfileChangeListener listener : profileChangeListeners) {
			listener.profileUpdated(profile, repositoryDefinition, onlyProperties);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void profileDeleted(Profile profile, CmrRepositoryDefinition repositoryDefinition) {
		for (IProfileChangeListener listener : profileChangeListeners) {
			listener.profileDeleted(profile, repositoryDefinition);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void environmentCreated(Environment environment, CmrRepositoryDefinition repositoryDefinition) {
		for (IEnvironmentChangeListener listener : environmentChangeListeners) {
			listener.environmentCreated(environment, repositoryDefinition);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void environmentUpdated(Environment environment, CmrRepositoryDefinition repositoryDefinition) {
		for (IEnvironmentChangeListener listener : environmentChangeListeners) {
			listener.environmentUpdated(environment, repositoryDefinition);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void environmentDeleted(Environment environment, CmrRepositoryDefinition repositoryDefinition) {
		for (IEnvironmentChangeListener listener : environmentChangeListeners) {
			listener.environmentDeleted(environment, repositoryDefinition);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void agentMappingsUpdated(AgentMappings agentMappings, CmrRepositoryDefinition repositoryDefinition) {
		for (IAgentMappingsChangeListener listener : agentMappingChangeListeners) {
			listener.agentMappingsUpdated(agentMappings, repositoryDefinition);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void applicationCreated(IApplicationDefinition application, int positionIndex, CmrRepositoryDefinition repositoryDefinition) {
		for (IApplicationDefinitionChangeListener listener : applicationChangeListeners) {
			listener.applicationCreated(application, positionIndex, repositoryDefinition);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public void applicationMoved(IApplicationDefinition application, int oldPositionIndex, int newPositionIndex, CmrRepositoryDefinition repositoryDefinition) {
		for (IApplicationDefinitionChangeListener listener : applicationChangeListeners) {
			listener.applicationMoved(application, oldPositionIndex, newPositionIndex, repositoryDefinition);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void applicationUpdated(IApplicationDefinition application, CmrRepositoryDefinition repositoryDefinition) {
		for (IApplicationDefinitionChangeListener listener : applicationChangeListeners) {
			listener.applicationUpdated(application, repositoryDefinition);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public void applicationDeleted(IApplicationDefinition application, CmrRepositoryDefinition repositoryDefinition) {
		for (IApplicationDefinitionChangeListener listener : applicationChangeListeners) {
			listener.applicationDeleted(application, repositoryDefinition);
		}
	}

	/**
	 * Registers a {@link IProfileChangeListener} if the same listener does not already exist.
	 * 
	 * @param profileChangeListener
	 *            {@link IProfileChangeListener} to add.
	 */
	public void addProfileChangeListener(IProfileChangeListener profileChangeListener) {
		profileChangeListeners.add(profileChangeListener);
	}

	/**
	 * Removes a {@link IProfileChangeListener}.
	 * 
	 * @param profileChangeListener
	 *            {@link IProfileChangeListener} to remove.
	 */
	public void removeProfileChangeListener(IProfileChangeListener profileChangeListener) {
		profileChangeListeners.remove(profileChangeListener);
	}

	/**
	 * Registers a {@link IEnvironmentChangeListener} if the same listener does not already exist.
	 * 
	 * @param environmentChangeListener
	 *            {@link IEnvironmentChangeListener} to add.
	 */
	public void addEnvironmentChangeListener(IEnvironmentChangeListener environmentChangeListener) {
		environmentChangeListeners.add(environmentChangeListener);
	}

	/**
	 * Removes a {@link IEnvironmentChangeListener}.
	 * 
	 * @param environmentChangeListener
	 *            {@link IEnvironmentChangeListener} to remove.
	 */
	public void removeEnvironmentChangeListener(IEnvironmentChangeListener environmentChangeListener) {
		environmentChangeListeners.remove(environmentChangeListener);
	}

	/**
	 * Registers a {@link IAgentMappingsChangeListener} if the same listener does not already exist.
	 * 
	 * @param agentMappingsChangeListener
	 *            {@link IAgentMappingsChangeListener} to add.
	 */
	public void addAgentMappingsChangeListener(IAgentMappingsChangeListener agentMappingsChangeListener) {
		agentMappingChangeListeners.add(agentMappingsChangeListener);
	}

	/**
	 * Removes a {@link IAgentMappingsChangeListener}.
	 * 
	 * @param agentMappingsChangeListener
	 *            {@link IAgentMappingsChangeListener} to remove.
	 */
	public void removeAgentMappingsChangeListener(IAgentMappingsChangeListener agentMappingsChangeListener) {
		agentMappingChangeListeners.remove(agentMappingsChangeListener);
	}

	/**
	 * Registers a {@link IApplicationDefinitionChangeListener} if the same listener does not
	 * already exist.
	 * 
	 * @param applicationDefinitionChangeListener
	 *            {@link IApplicationDefinitionChangeListener} to add.
	 */
	public void addApplicationDefinitionChangeListener(IApplicationDefinitionChangeListener applicationDefinitionChangeListener) {
		applicationChangeListeners.add(applicationDefinitionChangeListener);
	}

	/**
	 * Removes a {@link IApplicationDefinitionChangeListener}.
	 * 
	 * @param applicationDefinitionChangeListener
	 *            {@link IApplicationDefinitionChangeListener} to remove.
	 */
	public void removeApplicationDefinitionChangeListener(IApplicationDefinitionChangeListener applicationDefinitionChangeListener) {
		applicationChangeListeners.remove(applicationDefinitionChangeListener);
	}

}
