package info.novatec.inspectit.ci.business;

import info.novatec.inspectit.communication.data.InvocationSequenceData;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Definition of a string matching expression.
 *
 * @author Alexander Wert
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "string-matching")
public class StringMatchingExpression extends Expression {
	/**
	 * Matching type to use for comparing the string value against a snippet.
	 */
	@XmlAttribute(name = "matching-type", required = true)
	private PatternMatchingType matchingType;

	/**
	 * String value to check against.
	 */
	@XmlAttribute(name = "snippet", required = true)
	private String snippet;

	/**
	 * Source of the string value to be compared against the snippet.
	 */
	@XmlElementRef(required = true)
	private StringValueSource stringValueSource;

	/**
	 * Indicates whether this value source apply to any node in the {@link InvocationSequenceData}
	 * or only at the root element. If this variable is false, the string value extraction will be
	 * only applied on the root element of the invocation sequence, otherwise the invocation
	 * sequence is searched for a corresponding node.
	 */
	@XmlAttribute(name = "search-in-trace", required = false)
	private Boolean searchNodeInTrace = false;

	/**
	 * If search-in-trace attribute is set to true, this attribute defines the maximum depth in the
	 * trace to search for the string matching. -1 means no limit.
	 */
	@XmlAttribute(name = "max-search-depth", required = false)
	private Integer maxSearchDepth = -1;

	/**
	 * Default Constructor.
	 */
	public StringMatchingExpression() {
	}

	/**
	 * Constructor.
	 *
	 * @param matchingType
	 *            matching type that defines how the string value is compared to the URL of the
	 *            evaluation context
	 * @param snippet
	 *            string value to compare against
	 */
	public StringMatchingExpression(PatternMatchingType matchingType, String snippet) {
		this.matchingType = matchingType;
		this.snippet = snippet;
	}

	/**
	 * Gets {@link #matchingType}.
	 *
	 * @return {@link #matchingType}
	 */
	public PatternMatchingType getMatchingType() {
		return matchingType;
	}

	/**
	 * Sets {@link #matchingType}.
	 *
	 * @param matchingType
	 *            New value for {@link #matchingType}
	 */
	public void setMatchingType(PatternMatchingType matchingType) {
		this.matchingType = matchingType;
	}

	/**
	 * Gets {@link #snippet}.
	 *
	 * @return {@link #snippet}
	 */
	public String getSnippet() {
		return snippet;
	}

	/**
	 * Sets {@link #snippet}.
	 *
	 * @param snippet
	 *            New value for {@link #snippet}
	 */
	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	/**
	 * Gets {@link #stringValueSource}.
	 *
	 * @return {@link #stringValueSource}
	 */
	public StringValueSource getStringValueSource() {
		return stringValueSource;
	}

	/**
	 * Sets {@link #stringValueSource}.
	 *
	 * @param stringValueSource
	 *            New value for {@link #stringValueSource}
	 */
	public void setStringValueSource(StringValueSource stringValueSource) {
		this.stringValueSource = stringValueSource;
	}

	/**
	 * Gets {@link #searchNodeInTrace}.
	 *
	 * @return {@link #searchNodeInTrace}
	 */
	public boolean isSearchNodeInTrace() {
		return null != searchNodeInTrace ? searchNodeInTrace : false;
	}

	/**
	 * Sets {@link #searchNodeInTrace}.
	 *
	 * @param searchNodeInTrace
	 *            New value for {@link #searchNodeInTrace}
	 */
	public void setSearchNodeInTrace(boolean searchNodeInTrace) {
		this.searchNodeInTrace = searchNodeInTrace;
	}

	/**
	 * Gets {@link #maxSearchDepth}.
	 *
	 * @return {@link #maxSearchDepth}
	 */
	public int getMaxSearchDepth() {
		return null != maxSearchDepth ? maxSearchDepth : -1;
	}

	/**
	 * Sets {@link #maxSearchDepth}.
	 *
	 * @param maxSearchDepth
	 *            New value for {@link #maxSearchDepth}
	 */
	public void setMaxSearchDepth(int maxSearchDepth) {
		this.maxSearchDepth = maxSearchDepth;
	}

}