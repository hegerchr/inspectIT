package info.novatec.inspectit.rcp.repository.service.storage;

import info.novatec.inspectit.cmr.service.IExceptionDataAccessService;
import info.novatec.inspectit.communication.comparator.DefaultDataComparatorEnum;
import info.novatec.inspectit.communication.comparator.ResultComparator;
import info.novatec.inspectit.communication.data.AggregatedExceptionSensorData;
import info.novatec.inspectit.communication.data.ExceptionSensorData;
import info.novatec.inspectit.indexing.aggregation.Aggregators;
import info.novatec.inspectit.indexing.query.factory.impl.ExceptionSensorDataQueryFactory;
import info.novatec.inspectit.indexing.storage.IStorageTreeComponent;
import info.novatec.inspectit.indexing.storage.impl.StorageIndexQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * {@link IExceptionDataAccessService} for storage purposes.
 *
 * @author Ivan Senic, Christoph Heger
 *
 */
public class StorageExceptionDataAccessService extends AbstractStorageService<ExceptionSensorData> implements IExceptionDataAccessService {

	/**
	 * Storage index query factory.
	 */
	@Autowired
	private ObjectFactory<StorageIndexQuery> storageIndexQueryFactory;

	/**
	 * Indexing tree.
	 */
	private IStorageTreeComponent<ExceptionSensorData> indexingTree;

	/**
	 * Index query provider.
	 */
	private ExceptionSensorDataQueryFactory exceptionSensorDataQueryFactory;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ExceptionSensorData> getUngroupedExceptionOverview(ExceptionSensorData template, int limit, ResultComparator<ExceptionSensorData> resultComparator) {
		return this.getUngroupedExceptionOverview(template, limit, null, null, resultComparator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ExceptionSensorData> getUngroupedExceptionOverview(ExceptionSensorData template, int limit, Date fromDate, Date toDate, ResultComparator<ExceptionSensorData> resultComparator) {
		StorageIndexQuery query = storageIndexQueryFactory.getObject();
		query = exceptionSensorDataQueryFactory.getUngroupedExceptionOverviewQuery(query, template, limit, fromDate, toDate);
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
	public List<ExceptionSensorData> getUngroupedExceptionOverview(ExceptionSensorData template, ResultComparator<ExceptionSensorData> resultComparator) {
		return this.getUngroupedExceptionOverview(template, -1, null, null, resultComparator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ExceptionSensorData> getUngroupedExceptionOverview(ExceptionSensorData template, Date fromDate, Date toDate, ResultComparator<ExceptionSensorData> resultComparator) {
		return this.getUngroupedExceptionOverview(template, -1, fromDate, toDate, resultComparator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ExceptionSensorData> getExceptionTree(ExceptionSensorData template) {
		// here we have a problem because we have to de-serialize every exception to find the right
		// one, we need to check if we can change this method
		StorageIndexQuery query = storageIndexQueryFactory.getObject();
		query = exceptionSensorDataQueryFactory.getExceptionTreeQuery(query, template);
		List<ExceptionSensorData> results = super.executeQuery(query);
		Collections.reverse(results);
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AggregatedExceptionSensorData> getDataForGroupedExceptionOverview(ExceptionSensorData template) {
		return this.getDataForGroupedExceptionOverview(template, null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AggregatedExceptionSensorData> getDataForGroupedExceptionOverview(ExceptionSensorData template, Date fromDate, Date toDate) {
		StorageIndexQuery query = storageIndexQueryFactory.getObject();
		query = exceptionSensorDataQueryFactory.getDataForGroupedExceptionOverviewQuery(query, template, fromDate, toDate);
		List<ExceptionSensorData> resultList = super.executeQuery(query, Aggregators.GROUP_EXCEPTION_OVERVIEW_AGGREGATOR);
		List<AggregatedExceptionSensorData> filterList = new ArrayList<AggregatedExceptionSensorData>(resultList.size());
		for (ExceptionSensorData data : resultList) {
			if (data instanceof AggregatedExceptionSensorData) {
				filterList.add((AggregatedExceptionSensorData) data);
			}
		}
		return filterList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ExceptionSensorData> getStackTraceMessagesForThrowableType(ExceptionSensorData template) {
		// same problem again, we need to de-serialize all exceptions
		StorageIndexQuery query = storageIndexQueryFactory.getObject();
		query = exceptionSensorDataQueryFactory.getStackTraceMessagesForThrowableTypeQuery(query, template);
		return super.executeQuery(query, Aggregators.DISTINCT_STACK_TRACES_AGGREGATOR);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStorageTreeComponent<ExceptionSensorData> getIndexingTree() {
		return indexingTree;
	}

	/**
	 * @param indexingTree
	 *            the indexingTree to set
	 */
	public void setIndexingTree(IStorageTreeComponent<ExceptionSensorData> indexingTree) {
		this.indexingTree = indexingTree;
	}

	/**
	 * @param exceptionSensorDataQueryFactory
	 *            the exceptionSensorDataQueryFactory to set
	 */
	public void setExceptionSensorDataQueryFactory(ExceptionSensorDataQueryFactory exceptionSensorDataQueryFactory) {
		this.exceptionSensorDataQueryFactory = exceptionSensorDataQueryFactory;
	}

}
