import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterAPI {
	private static Twitter twitter;
	private static int sleepTime = 5 * 1000;

	static {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("")
		  .setOAuthConsumerSecret("")
		  .setOAuthAccessToken("")
		  .setOAuthAccessTokenSecret("")
		  .setTweetModeExtended(true);

		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	}
	
	public static void Sleep() throws Exception {
		Thread.sleep(sleepTime);
	}
	
	public static void Sleep(int seconds) throws Exception {
		Thread.sleep(seconds * 1000);
	}	
	
	private static Tweet convertToTweet(Status sts, String _query) {
		String tweetid = String.valueOf(sts.getId());
		String time = sts.getCreatedAt().toString();
		int replyCount = 0; //not supported by api!
		int likeCount = (int)sts.getFavoriteCount();
		int retweetCount = (int)sts.getRetweetCount();
		String query = _query;
		String HTML = Lib.removeSpaces(sts.getText());
		String userid = sts.getUser().getScreenName();
		Tweet result = new Tweet(tweetid, userid, time, replyCount,
				likeCount, retweetCount, query, HTML);
		return result;
	}

	public static ArrayList<Tweet> DownloadTweets(ArrayList<MiniTweet> list) throws Exception {
		ArrayList<Tweet> result = new ArrayList<>();
		int st = 0;
		Hashtable<Long, MiniTweet> tbl = new Hashtable<>();
		for (int ind = 0; ind < list.size(); ++ind) {
			tbl.put(list.get(ind).Id, list.get(ind));
		}
		while (st < list.size()) {
			int bound = Math.min(st + 100, list.size());
			long[] ids = new long[bound - st];
			int stInd = 0;
			for (int ind = st; ind < bound; ++ind) {
				ids[stInd] = list.get(ind).Id;
				++stInd;
			}
			int tried = 0;
			ResponseList<Status> statusList = null;
			while (true) {
				try {
					statusList = twitter.lookup(ids);
					break;
				}
				catch (Exception e) {
					System.out.println(tried + " tried->\t\t" + e.getMessage());
					++tried;
					if (tried == 5) {
						System.out.println("retried enough!");
						return null;
					}
					Sleep(5);
				}
			}
			for (int ind = 0; ind < statusList.size(); ++ind) {
				Tweet tw = convertToTweet(statusList.get(ind), "u");
				tw.Label = tbl.get(statusList.get(ind).getId()).Label;
				tw.Query = tbl.get(statusList.get(ind).getId()).Query;
				result.add(tw);
			}
			st = bound;
			if (st % 1000 == 0) {
				System.out.println(st + "/" + list.size() + " (" + result.size() + ")");
			}			
			Sleep();
		}
		return result;
	}
	
	public static ArrayList<Tweet> Search(String query, long sinceTweetId, 
			int tweetCount, CityLoc loc) throws Exception {
		ArrayList<Tweet> result = new ArrayList<>();
		try {
			Query qy = new Query();
			qy.setQuery(query + " +exclude:retweets +exclude:replies");
			qy.setCount(100);
			qy.setLang("en");
			qy.setSinceId(sinceTweetId);
			if (loc != null) {
				qy.setGeoCode(new GeoLocation(loc.CenterLat, loc.CenterLong), 
						15.0, Query.Unit.mi);
			}
			while (result.size() < tweetCount && qy != null) {
				QueryResult qr = twitter.search(qy);
				List<Status> sts = qr.getTweets();
				for (int ind = 0; ind < sts.size(); ++ind) {
					Tweet tw = convertToTweet(sts.get(ind), query);
					result.add(tw);
				}
				qy = qr.nextQuery();
				System.out.println(query + "\t\t" + result.size() + "/" + tweetCount);
				Sleep();
			}
			System.out.println(query + "\t\t" + result.size() + "/" + tweetCount + "   <");
		}
		catch (Exception err) {
			System.out.println(err.toString());
		}
		return result;
	}

	
}
