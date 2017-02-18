package statistics;

public class AttributeStatistics {
	public String attributeName;
	public Integer minimum, maximum;	
	
	public AttributeStatistics(String attributeName, 
			Integer minimum, Integer maximum) {
		this.attributeName = attributeName;
		this.minimum = minimum;
		this.maximum = maximum;
	}
	
	public AttributeStatistics(String attributeName) {
		this.attributeName = attributeName;
		this.minimum = Integer.MAX_VALUE;
		this.maximum = Integer.MIN_VALUE;
	}
	
	@Override
	public String toString(){
		return "Minimum: "+ minimum + " Maximum: "+maximum;
	}
}
