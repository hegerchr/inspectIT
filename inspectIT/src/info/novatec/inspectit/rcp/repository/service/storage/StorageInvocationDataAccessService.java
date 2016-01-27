package info.novatec.inspectit.rcp.repository.service.storage;

import info.novatec.inspectit.cmr.service.IInvocationDataAccessService;
import info.novatec.inspectit.communication.comparator.DefaultDataComparatorEnum;
import info.novatec.inspectit.communication.comparator.ResultComparator;
import info.novatec.inspectit.communication.data.InvocationSequenceData;
import info.novatec.inspectit.indexing.query.factory.impl.InvocationSequenceDataQueryFactory;
import info.novatec.inspectit.indexing.storage.IStorageTreeComponent;
import info.novatec.inspectit.indexing.storage.impl.StorageIndexQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * {@link IInvocationDataAccessService} for storage purposes.
 *
 * @author Ivan Senic
 *
 */
public class StorageInvocationDataAccessService extends AbstractStorageService<InvocationSequenceData> implements IInvocationDataAccessService {

	/**
	 * Indexing tree.
	 */
	private IStorageTreeComponent<InvocationSequenceData> indexingTree;

	/**
	 * Index query provider.
	 */
	private InvocationSequenceDataQueryFactory invocationDataQueryFactory;

	/**
	 * {@link ObjectFactory} to create {@link StorageIndexQuery} instances.
	 */
	@Autowired
	private ObjectFactory<StorageIndexQuery> storageIndexQueryFactory;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<InvocationSequenceData> getInvocationSequenceOverview(long platformId, long methodId, int limit, ResultComparator<InvocationSequenceData> resultComparator) {
		return this.getInvocationSequenceOverview(platformId, methodId, limit, null, null, resultComparator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<InvocationSequenceData> getInvocationSequenceOverview(long platformId, int limit, ResultComparator<InvocationSequenceData> resultComparator) {
		return this.getInvocationSequenceOverview(platformId, 0, limit, resultComparator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<InvocationSequenceData> getInvocationSequenceOverview(long platformId, long methodId, int limit, Date fromDate, Date toDate,
			ResultComparator<InvocationSequenceData> resultComparator) {
		StorageIndexQuery query = storageIndexQueryFactory.getObject();
		query = invocationDataQueryFactory.getInvocationSequenceOverview(query, platformId, methodId, limit, fromDate, toDate);
		query.setOnlyInvocationsWithoutChildren(true);
		if (null != resultComparator) {
			resultComparator.setCachedDataService(getStorageRepositoryDefinition().getCachedDataService());
			return super.executeQuery(query, resultComparator, limit);
		} else {
			return super.executeQuery(query, DefaultDataComparatorEnum.TIMESTAMP, limit);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<InvocationSequenceData> getInvocationSequenceOverview(long platformId, int limit, Date fromDate, Date toDate, ResultComparator<InvocationSequenceData> resultComparator) {
		return this.getInvocationSequenceOverview(platformId, 0, limit, fromDate, toDate, resultComparator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<InvocationSequenceData> getInvocationSequenceOverview(long platformId, Collection<Long> invocationIdCollection, int limit, ResultComparator<InvocationSequenceData> resultComparator) {
		StorageIndexQuery query = storageIndexQueryFactory.getObject();
		query = invocationDataQueryFactory.getInvocationSequenceOverview(query, platformId, invocationIdCollection, limit);
		query.setOnlyInvocationsWithoutChildren(true);
		if (null != resultComparator) {
			resultComparator.setCachedDataService(getStorageRepositoryDefinition().getCachedDataService());
			return super.executeQuery(query, resultComparator, limit);
		} else {
			return super.executeQuery(query, DefaultDataComparatorEnum.TIMESTAMP, limit);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InvocationSequenceData getInvocationSequenceDetail(InvocationSequenceData template) {
		// here we need to create new query since this one does not exist in factory
		StorageIndexQuery query = storageIndexQueryFactory.getObject();
		ArrayList<Class<?>> searchedClasses = new ArrayList<Class<?>>();
		searchedClasses.add(InvocationSequenceData.class);
		query.setObjectClasses(searchedClasses);
		query.setPlatformIdent(template.getPlatformIdent());
		query.setMethodIdent(template.getMethodIdent());
		query.setSensorTypeIdent(template.getSensorTypeIdent());
		query.setOnlyInvocationsWithoutChildren(false);
		ArrayList<Long> includeIds = new ArrayList<Long>();
		includeIds.add(template.getId());
		query.setIncludeIds(includeIds);
		List<InvocationSequenceData> results = super.executeQuery(query);
		if (results.size() == 1) {
			return results.get(0);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStorageTreeComponent<InvocationSequenceData> getIndexingTree() {
		return indexingTree;
	}

	/**
	 * @param indexingTree
	 *            the indexingTree to set
	 */
	public void setIndexingTree(IStorageTreeComponent<InvocationSequenceData> indexingTree) {
		this.indexingTree = indexingTree;
	}

	/**
	 * @param invocationDataQueryFactory
	 *            the invocationDataQueryFactory to set
	 */
	public void setInvocationDataQueryFactory(InvocationSequenceDataQueryFactory invocationDataQueryFactory) {
		this.invocationDataQueryFactory = invocationDataQueryFactory;
	}

}
