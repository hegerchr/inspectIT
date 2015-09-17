package info.novatec.inspectit.rcp.repository.service.storage;

import java.util.List;

import org.diagnoseit.spike.result.ProblemInstance;

import info.novatec.inspectit.cmr.service.IDITResultsAccessService;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.repository.StorageRepositoryDefinition;
import info.novatec.inspectit.rcp.repository.service.cmr.CmrServiceProvider;

public class StorageDITResultsAccessService implements IDITResultsAccessService {

	private IDITResultsAccessService cmrDiagnoseITService;
	private String storageDataId;

	public void setCmrRepositoryDefinition(StorageRepositoryDefinition storageRepositoryDefinition) {
		cmrDiagnoseITService = InspectIT.getService(CmrServiceProvider.class).getDiagnoseITResultsAccessService(storageRepositoryDefinition.getCmrRepositoryDefinition());
	}

	public void setStorageDataId(String id) {
		storageDataId = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProblemInstance> getProblemInstances() {
		return this.analyzeInteractively(storageDataId, -1, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProblemInstance> analyzeInteractively(long platformId, List<Long> traceIds) {
		return this.analyzeInteractively(storageDataId, platformId, traceIds);
	}

	@Override
	public List<ProblemInstance> analyzeInteractively(String storageDataId, long platformId, List<Long> traceIds) {
		return cmrDiagnoseITService.analyzeInteractively(storageDataId, platformId, traceIds);
	}

}
