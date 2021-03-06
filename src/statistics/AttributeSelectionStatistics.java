package statistics;

public class AttributeSelectionStatistics {

	Long lowerBound, upperBound;
	Double reductionFactor;
	
	public AttributeSelectionStatistics() {
		lowerBound = null;
		upperBound = null;
		reductionFactor = 1.0;
	}
	
	public AttributeSelectionStatistics(Long lowerBound, Long upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		reductionFactor = 1.0;
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
	
	public void setReductionFactor(Integer minAttributeValue, Integer maxAttributeValue) {
		if (upperBound != null && lowerBound != null) {
			reductionFactor = (upperBound - lowerBound + 1) * 1.0 / (maxAttributeValue - minAttributeValue + 1);
		} else if (upperBound != null) {
			reductionFactor = (upperBound - minAttributeValue + 1) * 1.0 / (maxAttributeValue - minAttributeValue + 1);
		} else if (lowerBound != null){
			reductionFactor = (maxAttributeValue - lowerBound + 1) * 1.0 / (maxAttributeValue - minAttributeValue + 1);
		} else {
			reductionFactor = -1.0;
		}
	}
}
