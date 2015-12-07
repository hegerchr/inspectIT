package info.novatec.inspectit.cmr.service;

import info.novatec.inspectit.cmr.ci.ConfigurationInterfaceManager;
import info.novatec.inspectit.cmr.configuration.business.IApplicationDefinition;
import info.novatec.inspectit.cmr.configuration.business.IBusinessContextDefinition;
import info.novatec.inspectit.exception.BusinessException;
import info.novatec.inspectit.spring.logger.Log;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Cached access and management service to the business context definition.
 *
 * @author Alexander Wert
 *
 */
@Service
public class BusinessContextManagementService implements IBusinessContextManagementService, InitializingBean {

	/** The logger of this class. */
	@Log
	Logger log;

	/**
	 * {@link BusinessContextManager}.
	 */
	@Autowired
	private ConfigurationInterfaceManager ciManager;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IBusinessContextDefinition getBusinessContextDefinition() {
		return ciManager.getBusinessconContextDefinition();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IApplicationDefinition> getApplicationDefinitions() {
		return ciManager.getBusinessconContextDefinition().getApplicationDefinitions();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IApplicationDefinition getApplicationDefinition(short id) throws BusinessException {
		return ciManager.getBusinessconContextDefinition().getApplicationDefinition(id);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws BusinessException
	 */
	@Override
	public synchronized void addApplicationDefinition(IApplicationDefinition appDefinition) throws BusinessException {
		IBusinessContextDefinition businessContextDefinition = ciManager.getBusinessconContextDefinition();
		businessContextDefinition.addApplicationDefinition(appDefinition);
		ciManager.updateBusinessContextDefinition(businessContextDefinition);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void addApplicationDefinition(IApplicationDefinition appDefinition, int insertBeforeIndex) throws BusinessException {
		IBusinessContextDefinition businessContextDefinition = ciManager.getBusinessconContextDefinition();
		businessContextDefinition.addApplicationDefinition(appDefinition, insertBeforeIndex);
		ciManager.updateBusinessContextDefinition(businessContextDefinition);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void deleteApplicationDefinition(IApplicationDefinition appDefinition) throws BusinessException {
		IBusinessContextDefinition businessContextDefinition = ciManager.getBusinessconContextDefinition();
		businessContextDefinition.deleteApplicationDefinition(appDefinition);
		ciManager.updateBusinessContextDefinition(businessContextDefinition);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void moveApplicationDefinition(IApplicationDefinition appDefinition, int index) throws BusinessException {
		IBusinessContextDefinition businessContextDefinition = ciManager.getBusinessconContextDefinition();
		businessContextDefinition.moveApplicationDefinition(appDefinition, index);
		ciManager.updateBusinessContextDefinition(businessContextDefinition);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void updateApplicationDefinition(IApplicationDefinition appDefinition) throws BusinessException {
		IBusinessContextDefinition businessContextDefinition = ciManager.getBusinessconContextDefinition();
		businessContextDefinition.updateApplicationDefinition(appDefinition);
		ciManager.updateBusinessContextDefinition(businessContextDefinition);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if (log.isInfoEnabled()) {
			log.info("|-Business Context Management Service active...");
		}
	}

}
