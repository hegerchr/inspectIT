package info.novatec.inspectit.indexing.query.factory.impl;

import info.novatec.inspectit.communication.ExceptionEvent;
import info.novatec.inspectit.communication.data.AggregatedExceptionSensorData;
import info.novatec.inspectit.communication.data.ExceptionSensorData;
import info.novatec.inspectit.indexing.IIndexQuery;
import info.novatec.inspectit.indexing.restriction.impl.IndexQueryRestrictionFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * Factory for all queries for the {@link ExceptionSensorData}.
 *
 * @author Ivan Senic, Christoph Heger
 *
 */
@Component
public class ExceptionSensorDataQueryFactory {

	/**
	 * Returns a query for a list of {@link ExceptionSensorData} objects which are between the from
	 * and to {@link Date} objects. This list can be used to get an overview over recorded
	 * Exceptions in a target application.
	 *
	 * @param <E>
	 *            Query of type {@link IIndexQuery}.
	 * @param query
	 *            The query to enrich.
	 * @param template
	 *            The template data object.
	 * @param limit
	 *            The limit/size of the list.
	 * @param fromDate
	 *            The start date.
	 * @param toDate
	 *            The end date.
	 * @return Query for list of {@link ExceptionSensorData} objects to get an overview of recorded
	 *         Exceptions.
	 */
	public <E extends IIndexQuery> E getUngroupedExceptionOverviewQuery(E query, ExceptionSensorData template, int limit, Date fromDate, Date toDate) {
		query.setPlatformIdent(template.getPlatformIdent());
		query.setSensorTypeIdent(template.getSensorTypeIdent());
		query.setMethodIdent(template.getMethodIdent());
		if (null != template.getThrowableType()) {
			query.addIndexingRestriction(IndexQueryRestrictionFactory.equal("throwableType", template.getThrowableType()));
		}
		ArrayList<Class<?>> searchedClasses = new ArrayList<Class<?>>();
		searchedClasses.add(ExceptionSensorData.class);
		searchedClasses.add(AggregatedExceptionSensorData.class);
		query.setObjectClasses(searchedClasses);
		query.addIndexingRestriction(IndexQueryRestrictionFactory.equal("exceptionEvent", ExceptionEvent.CREATED));
		if (null != fromDate) {
			query.setFromDate(new Timestamp(fromDate.getTime()));
		}
		if (null != toDate) {
			query.setToDate(new Timestamp(toDate.getTime()));
		}
		return query;
	}

	/**
	 * Get query for exception tree.
	 *
	 * @param <E>
	 *            Query of type {@link IIndexQuery}.
	 * @param query
	 *            The query to enrich.
	 * @param template
	 *            Template to use.
	 * @return Query.
	 */
	public <E extends IIndexQuery> E getExceptionTreeQuery(E query, ExceptionSensorData template) {
		ArrayList<Class<?>> searchedClasses = new ArrayList<Class<?>>();
		searchedClasses.add(ExceptionSensorData.class);
		searchedClasses.add(AggregatedExceptionSensorData.class);
		query.setObjectClasses(searchedClasses);
		query.setPlatformIdent(template.getPlatformIdent());
		query.setMethodIdent(template.getMethodIdent());
		query.addIndexingRestriction(IndexQueryRestrictionFactory.equal("throwableIdentityHashCode", template.getThrowableIdentityHashCode()));
		return query;
	}

	/**
	 * Returns a query for list of objects that is used to show an overview over Exceptions with
	 * specific information about the number of caused event types.
	 *
	 * @param <E>
	 *            Query of type {@link IIndexQuery}.
	 * @param query
	 *            The query to enrich.
	 * @param template
	 *            The template object to be used for the query.
	 * @param fromDate
	 *            The start date.
	 * @param toDate
	 *            The end date.
	 * @return A query for the list of objects with additional information about how often a
	 *         specific eventType was caused.
	 */
	public <E extends IIndexQuery> E getDataForGroupedExceptionOverviewQuery(E query, ExceptionSensorData template, Date fromDate, Date toDate) {
		ArrayList<Class<?>> searchedClasses = new ArrayList<Class<?>>();
		searchedClasses.add(ExceptionSensorData.class);
		searchedClasses.add(AggregatedExceptionSensorData.class);
		query.setObjectClasses(searchedClasses);
		query.setPlatformIdent(template.getPlatformIdent());
		if (null != template.getThrowableType()) {
			query.addIndexingRestriction(IndexQueryRestrictionFactory.equal("throwableType", template.getThrowableType()));
		}
		if (null != fromDate) {
			query.setFromDate(new Timestamp(fromDate.getTime()));
		}
		if (null != toDate) {
			query.setToDate(new Timestamp(toDate.getTime()));
		}
		return query;
	}

	/**
	 * Returns a query for a stack trace message of a throwable type.
	 *
	 * @param <E>
	 *            Query of type {@link IIndexQuery}.
	 * @param query
	 *            The query to enrich.
	 * @param template
	 *            Template to use.
	 * @return Query.
	 */
	public <E extends IIndexQuery> E getStackTraceMessagesForThrowableTypeQuery(E query, ExceptionSensorData template) {
		ArrayList<Class<?>> searchedClasses = new ArrayList<Class<?>>();
		searchedClasses.add(ExceptionSensorData.class);
		searchedClasses.add(AggregatedExceptionSensorData.class);
		query.setObjectClasses(searchedClasses);
		query.setPlatformIdent(template.getPlatformIdent());
		query.addIndexingRestriction(IndexQueryRestrictionFactory.equal("throwableType", template.getThrowableType()));
		query.addIndexingRestriction(IndexQueryRestrictionFactory.isNotNull("stackTrace"));
		return query;
	}
}
