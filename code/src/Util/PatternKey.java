package Util;

import java.io.Serializable;
import java.util.List;

public class PatternKey implements Serializable{
	
	private static final long serialVersionUID = 5468335797443850679L;
	
	private List<String> pattern;
	
	private int len;
	
	
	public PatternKey(List<String> p, int len)
	{
		this.pattern = p;
		this.len = len;
//		for(Set<String> s:p)
//			len += s.size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PatternKey other = (PatternKey) obj;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		return true;
	}

	public List<String> getPattern() {
		return pattern;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}
	
}
