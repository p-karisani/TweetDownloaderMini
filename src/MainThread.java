import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MainThread {

	public static void main(String[] args)  throws Exception {
//		downloadTweetList();
		neverEndingSearch();
	}

	private static void downloadTweetList() throws Exception {
		String datasetPath = "file.txt"; // file format: tweet-id(TAB)query(TAB)label

		List<String> list = Files.readAllLines(Paths.get(datasetPath));
		ArrayList<MiniTweet> tws = new ArrayList<>();
		for (int ind = 0; ind < list.size(); ++ind) {
			String[] ts = list.get(ind).split("\t");
			long id = Long.parseLong(ts[0]);
			int lbl = Integer.parseInt(ts[2]);
			String query = ts[1];
			tws.add(new MiniTweet(id, lbl, query));
		}
		ArrayList<Tweet> dws = TwitterAPI.DownloadTweets(tws);
		Tweet.save(dws, datasetPath + "-content.txt");
		System.out.println("done!");
	}

	private static void neverEndingSearch() throws Exception {
		int tweetPerQuery = 100;
		int sleepTime = 1 * 60 * 1000; // milliseconds
		long counter = 0;
		
		String[] queries = new String[] {
				"money", "president", "singer", "movie"
		};

		//		a 30 miles box in which its center is Atlanta:
		//		(latitude, longitude)
		//		southwest: 		33.539291, -84.648562
		//		northeast: 		33.972526, -84.124072
		//		city center:		33.753746, -84.386330
		CityLoc location = new CityLoc(33.753746, -84.386330, 33.539291, -84.648562, 33.972526, -84.124072);

		long[] sinceIds = Lib.getInitializedArray(queries.length);
		while (true) {
			long[] cp = search(queries, sinceIds, tweetPerQuery, null, "filename"); // you can pass the location argument here					
			sinceIds = cp;
			++counter;
			System.out.println(">>>" + counter + "\t\t" + Lib.getTime());
			Thread.sleep(sleepTime);
		}
	}

	private static long[] search(String[] queries, long[] sinceTweetIds, 
			int countPerQuery, CityLoc loc, String filePostfix) throws Exception {
		long[] maxIds = Lib.getInitializedArray(queries.length);
		for (int ind = 0; ind < queries.length; ++ind) {
			ArrayList<Tweet> list = TwitterAPI.Search(queries[ind], sinceTweetIds[ind], countPerQuery, loc);
			maxIds[ind] = Math.max(sinceTweetIds[ind], Lib.getMaxId(list));
			StringBuilder sb = new StringBuilder();
			for (int lInd = 0; lInd < list.size(); ++lInd) {
				sb.append(list.get(lInd).toString() + "\n");
			}
			FileOutputStream fos = new FileOutputStream("search-" + filePostfix + ".txt", true);
			PrintStream ps = new PrintStream(fos, true, "UTF-8");
			ps.print(sb.toString());
			ps.close();
		}
		return maxIds;
	}

}
