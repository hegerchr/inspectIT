package info.novatec.inspectit.rcp.diagnoseit.overview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.diagnoseit.spike.result.ProblemInstance;

public class DITResultNodeType extends DITResultElement {

	private DITResultBusinessTransaction parent;
	private List<DITResultEntryPoint> children;
	
	private boolean sorted = false;
	protected List<DITResultEntryPoint> getEntryPoints() {
		return children;
	}

	protected void addEntryPoint(DITResultEntryPoint entryPoint) {
		if (children == null) {
			children = new ArrayList<DITResultEntryPoint>(2);
		}
		children.add(entryPoint);
		entryPoint.setParent(this);
		sorted = false;
	}
	
	@Override
	public DITResultElement getParent() {
		return parent;
	}
	
	protected void setParent(DITResultBusinessTransaction parent) {
		this.parent = parent;
	}

	@Override
	public List<? extends DITResultElement> getChildren() {
		if (!sorted) {
			Collections.sort(children, new SeverityComperator());
		}
		return children;
	}

	@Override
	public String getColumnContent(DITOverviewColumn column) {
		if (children == null || children.isEmpty()) {
			throw new IllegalStateException("Node Type result element must always have at least one child!");
		}
		switch (column) {
		case LAST_OCCURENCE:
			Date max = calculateLastOccurrence();
			LastOccurrenceDiff diff = DITResultProblemInstance.getLastOccurrenceDiff(max, new Date());
			return diff.toString();
		case NUM_AFFECTED_NODES:
			Set<String> affectedNodes = calculateAffectedNodesSet();
			return String.valueOf(affectedNodes.size());
		case NUM_INSTANCES:
			return String.valueOf(calculateNumProblemInstances());
		case NUM_OCCURRENCES:
			int sumOccurrences = calculateNumOccurrences();
			return String.valueOf(sumOccurrences);
		case PROBLEM_OVERVIEW:
			return getStringRepresentation();
		case SEVERITY:
			double maxSeverity = getSeverity();
			if (maxSeverity > 0.8) {
				return "high";
			} else if (maxSeverity > 0.5) {
				return "medium";
			} else {
				return "low";
			}
		default:
			throw new IllegalArgumentException("Unsupported Column!");
		}
	}

	public String getIdentifier() {
		return getRepresentativeProblemInstance().getNodeType();
	}
	
	
	public double getSeverity() {
		double maxSeverity = -1.0;
		for (DITResultEntryPoint child : children) {
			double sevValue = child.getSeverity();
			maxSeverity = sevValue > maxSeverity ? sevValue : maxSeverity;
		}
		return maxSeverity;
	}

	protected ProblemInstance getRepresentativeProblemInstance() {
		return children.get(0).getRepresentativeProblemInstance();
	}
	
	protected int calculateNumProblemInstances() {
		int sum = 0;
		for (DITResultEntryPoint child : children) {
			sum += child.calculateNumProblemInstances();
		}
		return sum;
	}

	protected int calculateNumOccurrences() {
		int sumOccurrences = 0;
		for (DITResultEntryPoint child : children) {
			sumOccurrences += child.calculateNumOccurrences();
		}
		return sumOccurrences;
	}

	protected Set<String> calculateAffectedNodesSet() {
		Set<String> affectedNodes = new HashSet<String>();
		for (DITResultEntryPoint child : children) {
			affectedNodes.addAll(child.calculateAffectedNodesSet());
		}
		return affectedNodes;
	}

	protected Date calculateLastOccurrence() {
		Date max = new Date(0L);
		for (DITResultEntryPoint child : children) {
			Date value = child.calculateLastOccurrence();
			max = value.getTime() > max.getTime() ? value : max;
		}
		return max;
	}
	

	@Override
	public ResultElementType getResultElementType() {
		return ResultElementType.NODE_TYPE;
	}

	@Override
	public String getStringRepresentation() {
		String identifier = getIdentifier();
		return identifier.substring(0, Math.min(80, identifier.length()));
	}


}