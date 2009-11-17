package net.sourceforge.synergy;

public class RelatedFile implements Comparable<RelatedFile> {

	private static final long serialVersionUID = 1L;

	private String name;
	private int relevance;

	public RelatedFile(String name) {
		super();
		this.name = name;
		this.relevance = 0;

	}

	public int compareTo(RelatedFile rf) {
		if (this.relevance == rf.relevance) {
			return 0;
		} else if (this.relevance < rf.relevance){
			return -1;
		}else{
			return 1;
		}
		
	}

	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setRelevance(int rel) {
		this.relevance = rel;
	}

	public int getRelevance() {
		return this.relevance;
	}



	public String toString() {
		return name;
	}
}
