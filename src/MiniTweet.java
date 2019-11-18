
public class MiniTweet {
	public long Id;
	public int Label;
	public String Query;
	
	public MiniTweet(long id, int label, String query) {
		Id = id;
		Label = label;
		Query = query;
	}
	
	@Override
	public String toString() {
		return Id + "\t" + Label + "\t" + Query;
	}
	
}
