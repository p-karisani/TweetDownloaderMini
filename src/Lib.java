import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Lib {
	
	public static String removeSpaces(String text) {
		return text.replaceAll("\n", " ").replaceAll("\r", " ").replaceAll("\t", " ");
	}
	
	public static String getTime() {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date();
		String result = dateFormat.format(date);
		return result;
	}

	public static long[] getInitializedArray(int len) {
		long[] result = new long[len];
		for (int ind = 0; ind < result.length; ++ind) {
			result[ind] = -1;
		}
		return result;
	}
	
	public static long getMaxId(ArrayList<Tweet> list) {
		long result = -1;
		for (int ind = 0; ind < list.size(); ++ind) {
			long cur = Long.parseLong(list.get(ind).Tweetid);
			if (result < cur) {
				result = cur;
			}
		}
		return result;
	}
	
}
