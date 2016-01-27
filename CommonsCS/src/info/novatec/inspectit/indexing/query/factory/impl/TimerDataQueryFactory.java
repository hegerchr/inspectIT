package info.novatec.inspectit.indexing.query.factory.impl;

import info.novatec.inspectit.communication.data.AggregatedTimerData;
import info.novatec.inspectit.communication.data.TimerData;
import info.novatec.inspectit.indexing.IIndexQuery;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * Factory for all queries for the {@link TimerData}.
 *
 * @author Ivan Senic, Christoph Heger
 *
 */
@Component
public class TimerDataQueryFactory {

	/**
	 * Returns the query for aggregating the {@link TimerData}.
	 *
	 * @param <E>
	 *            Query of type {@link IIndexQuery}.
	 * @param query
	 *            The query object to enrich.
	 * @param timerData
	 *            The template containing the platform id.
	 * @param fromDate
	 *            Date to include data from.
	 * @param toDate
	 *            Date to include data to.
	 * @return Enriched query object.
	 */
	public <E extends IIndexQuery> E getAggregatedTimerDataQuery(E query, TimerData timerData, Date fromDate, Date toDate) {
		query.setPlatformIdent(timerData.getPlatformIdent());
		query.setMethodIdent(timerData.getMethodIdent());
		ArrayList<Class<?>> searchedClasses = new ArrayList<Class<?>>();
		// we need to add the subclasses that are timers manually as the search will not include
		// subclasses by default
		// HttpTimerData will not be shown in the timer data view (we also do not show SQL data)
		searchedClasses.add(TimerData.class);
		searchedClasses.add(AggregatedTimerData.class);
		query.setObjectClasses(searchedClasses);
		if (null != fromDate) {
			query.setFromDate(new Timestamp(fromDate.getTime()));
		}
		if (null != toDate) {
			query.setToDate(new Timestamp(toDate.getTime()));
		}
		return query;
	}
}
