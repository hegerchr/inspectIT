package info.novatec.inspectit.ci.business;

import info.novatec.inspectit.cmr.service.ICachedDataService;
import info.novatec.inspectit.communication.data.InvocationSequenceData;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * Abstract class for sources of string values within an invocation sequence.
 *
 * @author Alexander Wert
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ HttpUriValueSource.class, HttpParameterValueSource.class, MethodSignatureValueSource.class, HostValueSource.class })
public abstract class StringValueSource {
	/**
	 * Retrieves the string values for the given {@link InvocationSequenceData} instance according
	 * to the specific string values source type.
	 *
	 * @param invocSequence
	 *            {@link InvocationSequenceData} to retrieve the string values from
	 * @param cachedDataService
	 *            {@link ICachedDataService} used to retrieve additional information
	 * @return Returns the string values for the given {@link InvocationSequenceData} instance.
	 *         Returns null if no strings can be retrieved.
	 */
	public abstract String[] getStringValues(InvocationSequenceData invocSequence, ICachedDataService cachedDataService);
}