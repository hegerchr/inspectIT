package info.novatec.inspectit.indexing.query.factory.impl;

import info.novatec.inspectit.communication.data.AggregatedHttpTimerData;
import info.novatec.inspectit.communication.data.HttpTimerData;
import info.novatec.inspectit.indexing.IIndexQuery;
import info.novatec.inspectit.indexing.restriction.impl.IndexQueryRestrictionFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * Factory for all queries for the {@link HttpTimerData}.
 *
 * @author Ivan Senic, Christoph Heger
 *
 */
@Component
public class HttpTimerDataQueryFactory {

	/**
	 * Return query for all <code>HttpTimerData</code> objects.
	 * 
	 * @param <E>
	 *            Query of type {@link IIndexQuery}.
	 * @param query
	 *            The query to enrich.
	 * @param httpData
	 *            <code>HttpTimerData</code> object used to retrieve the platformId
	 * @param fromDate
	 *            the fromDate or <code>null</code> if not applicable
	 * @param toDate
	 *            the toDate or <code>null</code> if not applicable
	 * @return Query for all <code>HttpTimerData</code> objects in the buffer.
	 */
	public <E extends IIndexQuery> E getFindAllHttpTimersQuery(E query, HttpTimerData httpData, Date fromDate, Date toDate) {
		query.setPlatformIdent(httpData.getPlatformIdent());
		ArrayList<Class<?>> classesToSearch = new ArrayList<Class<?>>();
		classesToSearch.add(HttpTimerData.class);
		classesToSearch.add(AggregatedHttpTimerData.class);
		query.setObjectClasses(classesToSearch);
		if (null != fromDate) {
			query.setFromDate(new Timestamp(fromDate.getTime()));
		}
		if (null != toDate) {
			query.setToDate(new Timestamp(toDate.getTime()));
		}
		return query;
	}

	/**
	 * Return query for all <code>HttpTimerData</code> objects that have a inspectIT tag header
	 * value.
	 *
	 * @param <E>
	 *            Query of type {@link IIndexQuery}.
	 * @param query
	 *            The query to enrich.
	 * @param httpData
	 *            <code>HttpTimerData</code> object used to retrieve the platformId
	 * @param fromDate
	 *            the fromDate or <code>null</code> if not applicable
	 * @param toDate
	 *            the toDate or <code>null</code> if not applicable
	 * @return Query for all <code>HttpTimerData</code> objects in the buffer.
	 */
	public <E extends IIndexQuery> E getFindAllTaggedHttpTimersQuery(E query, HttpTimerData httpData, Date fromDate, Date toDate) {
		query.setPlatformIdent(httpData.getPlatformIdent());
		ArrayList<Class<?>> classesToSearch = new ArrayList<Class<?>>();
		classesToSearch.add(HttpTimerData.class);
		classesToSearch.add(AggregatedHttpTimerData.class);
		query.setObjectClasses(classesToSearch);
		query.addIndexingRestriction(IndexQueryRestrictionFactory.isNotNull("inspectItTaggingHeaderValue"));
		if (null != fromDate) {
			query.setFromDate(new Timestamp(fromDate.getTime()));
		}
		if (null != toDate) {
			query.setToDate(new Timestamp(toDate.getTime()));
		}
		return query;
	}
}
