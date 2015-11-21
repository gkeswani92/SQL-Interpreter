package statistics;

public class AttributeSelectionStatistics {

	Long lowerBound, upperBound;
	Double reductionFactor;
	
	public AttributeSelectionStatistics() {
		lowerBound = null;
		upperBound = null;
		reductionFactor = null;
	}
	
	public AttributeSelectionStatistics(Long lowerBound, Long upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		reductionFactor = null;
	}
	
	public Double getReductionFactor() {
		return reductionFactor;
	}
	
	public Long getLowerBound() {
		return lowerBound;
	}
	
	public void setLowerBound(Long lowerBound) {
		this.lowerBound = lowerBound;
	}
	
	public Long getUpperBound() {
		return upperBound;
	}
	
	public void setUpperBound(Long upperBound) {
		this.upperBound = upperBound;
	}
}
