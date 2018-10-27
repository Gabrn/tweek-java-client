
import java.util.Collection;

public class GetRequestOptions
{
	private Boolean flatten;
	private Boolean ignoreKeyTypes;
	private Collection<String> include; 
	public GetRequestOptions(Collection<String> include, Boolean flatten, Boolean ignoreKeyTypes) {
		this.flatten = flatten;
		this.include = include;
		this.ignoreKeyTypes = ignoreKeyTypes;
	}
	
	public Boolean isFlatten() {
		return flatten;
	}
	
	public Boolean shouldIgnoreKeyTypes() {
		return ignoreKeyTypes;
	}
	
	public Collection<String> getIncludes() {
		return include;
	}
}