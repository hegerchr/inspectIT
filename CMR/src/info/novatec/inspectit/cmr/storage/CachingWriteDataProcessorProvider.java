package info.novatec.inspectit.cmr.storage;

import info.novatec.inspectit.communication.data.ExceptionSensorData;
import info.novatec.inspectit.communication.data.SqlStatementData;
import info.novatec.inspectit.communication.data.TimerData;
import info.novatec.inspectit.indexing.IIndexQuery;
import info.novatec.inspectit.indexing.aggregation.Aggregators;
import info.novatec.inspectit.indexing.aggregation.IAggregator;
import info.novatec.inspectit.indexing.query.factory.impl.ExceptionSensorDataQueryFactory;
import info.novatec.inspectit.indexing.query.factory.impl.SqlStatementDataQueryFactory;
import info.novatec.inspectit.indexing.query.factory.impl.TimerDataQueryFactory;
import info.novatec.inspectit.indexing.storage.impl.StorageIndexQuery;
import info.novatec.inspectit.storage.processor.write.AbstractWriteDataProcessor;
import info.novatec.inspectit.storage.processor.write.impl.QueryCachingDataProcessor;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

/**
 * Configuration class for specifying the caching processors for the storage writer.
 * <p>
 * These will be autowired to each storage writer.
 *
 * @author Ivan Senic, Christoph Heger
 *
 */
@Configuration
public class CachingWriteDataProcessorProvider {

	/**
	 * {@link ObjectFactory} is needed here because we are caching storage queries.
	 */
	@Autowired
	private ObjectFactory<StorageIndexQuery> storageIndexQueryFactory;

	/**
	 * {@link TimerDataQueryFactory}.
	 */
	@Autowired
	private TimerDataQueryFactory timerDataQueryFactory;

	/**
	 * {@link SqlStatementDataQueryFactory}.
	 */
	@Autowired
	private SqlStatementDataQueryFactory sqlStatementDataQueryFactory;

	/**
	 * {@link ExceptionSensorDataQueryFactory}.
	 */
	@Autowired
	private ExceptionSensorDataQueryFactory exceptionSensorDataQueryFactory;

	/**
	 * @return Returns {@link AbstractWriteDataProcessor} for caching the {@link TimerData} view.
	 */
	@Bean
	@Lazy
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public AbstractWriteDataProcessor getTimerDataCachingDataProcessor() {
		IIndexQuery query = storageIndexQueryFactory.getObject();
		query = timerDataQueryFactory.getAggregatedTimerDataQuery(query, new TimerData(), null, null);
		IAggregator<TimerData> aggregator = Aggregators.TIMER_DATA_AGGREGATOR;
		return new QueryCachingDataProcessor<>(query, aggregator);
	}

	/**
	 * @return Returns {@link AbstractWriteDataProcessor} for caching the {@link SqlStatementData}
	 *         view.
	 */
	@Bean
	@Lazy
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public AbstractWriteDataProcessor getSqlDataCachingDataProcessor() {
		IIndexQuery query = storageIndexQueryFactory.getObject();
		query = sqlStatementDataQueryFactory.getAggregatedSqlStatementsQuery(query, new SqlStatementData(), null, null);
		IAggregator<SqlStatementData> aggregator = Aggregators.SQL_STATEMENT_DATA_AGGREGATOR;
		return new QueryCachingDataProcessor<>(query, aggregator);
	}

	/**
	 * @return Returns {@link AbstractWriteDataProcessor} for caching the
	 *         {@link ExceptionSensorData} grouped view.
	 */
	@Bean
	@Lazy
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public AbstractWriteDataProcessor getGroupedExceptionsDataCachingDataProcessor() {
		IIndexQuery query = storageIndexQueryFactory.getObject();
		query = exceptionSensorDataQueryFactory.getDataForGroupedExceptionOverviewQuery(query, new ExceptionSensorData(), null, null);
		IAggregator<ExceptionSensorData> aggregator = Aggregators.GROUP_EXCEPTION_OVERVIEW_AGGREGATOR;
		return new QueryCachingDataProcessor<>(query, aggregator);
	}

}
