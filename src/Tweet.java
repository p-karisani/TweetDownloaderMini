import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.management.Query;

public class Tweet {
	private static final int useridAlign = 25;
	private static final String tokenInReplyTo = "InReplyTo";
	private static final String tokenReply = "Reply";
	public String Tweetid;
	public int Label;
	public String Userid;
	public String Time;
	public int ReplyCount;
	public int LikeCount;
	public int RetweetCount;
	public String Query;
	public String HTML;
	public String Text;
	public ArrayList<Tweet> InReplyToSample = new ArrayList<>();
	public ArrayList<Tweet> ReplySample = new ArrayList<>();

	public Tweet() {
		
	}
	
	public Tweet(String tweetid, String userid, String time,  int replyCount, 
			int likeCount, int retweetCount, String query, String html) {
		Tweetid = tweetid;
		Userid = userid;
		Time = time;
		ReplyCount = replyCount;
		LikeCount = likeCount;
		RetweetCount = retweetCount;
		Query = query;
		HTML = html;
		Text = getTextFromHTML(html);
	}
	
	public Tweet(String line) {
		String[] tokens = line.split("\t");
		int ind = setFields(this, tokens, 0);
		while (ind < tokens.length) {
			Tweet tw = new Tweet();
			if (tokens[ind].equals(tokenInReplyTo)) {
				InReplyToSample.add(tw);
			}
			else if (tokens[ind].equals(tokenReply)) {
				ReplySample.add(tw);
			}
			ind = setFields(tw, tokens, ind + 1);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(getString(this));
		for (int ind = 0; ind < InReplyToSample.size(); ++ind) {
			result.append("\t");
			result.append(tokenInReplyTo);
			result.append("\t");
			result.append(getString(InReplyToSample.get(ind)));
		}
		for (int ind = 0; ind < ReplySample.size(); ++ind) {
			result.append("\t");
			result.append(tokenReply);
			result.append("\t");
			result.append(getString(ReplySample.get(ind)));
		}
		return result.toString();
	}
	
	public void clearExtraInfo() {
		Tweetid = null;
		HTML = null;
		for (int ind = 0; ind < InReplyToSample.size(); ++ind) {
			InReplyToSample.get(ind).Tweetid = null;
			InReplyToSample.get(ind).HTML = null;
		}
		for (int ind = 0; ind < ReplySample.size(); ++ind) {
			ReplySample.get(ind).Tweetid = null;
			ReplySample.get(ind).HTML = null;
		}
	}
	
	private static String getTextFromHTML(String html) {
		return html;
	}
	
	private static  int setFields(Tweet tw, String[] tokens, int startIndex) {
		int ind = startIndex;
		tw.Tweetid = tokens[ind];
		ind++;
		tw.Label = Integer.parseInt(tokens[ind]);
		ind++;
		tw.Userid = tokens[ind].substring(0, useridAlign).trim();
		tw.Time = tokens[ind].substring(useridAlign, tokens[ind].length());
		ind++;
		tw.ReplyCount = Integer.parseInt(tokens[ind]);
		ind++;
		tw.LikeCount = Integer.parseInt(tokens[ind]);
		ind++;
		tw.RetweetCount = Integer.parseInt(tokens[ind]);
		ind++;
		tw.Query = tokens[ind];
		ind++;		
		tw.HTML = tokens[ind];
		tw.Text = getTextFromHTML(tokens[ind]);
		ind++;
		return ind;
	}
	
	private static String getString(Tweet tw) {
		StringBuilder sb = new StringBuilder();
		sb.append(tw.Tweetid);
		sb.append("\t");
		sb.append(tw.Label);
		sb.append("\t");
		sb.append(String.format("%-" + useridAlign + "s", tw.Userid));
		sb.append(tw.Time);
		sb.append("\t");
		sb.append(tw.ReplyCount);
		sb.append("\t");
		sb.append(tw.LikeCount);
		sb.append("\t");
		sb.append(tw.RetweetCount);
		sb.append("\t");
		sb.append(tw.Query);
		sb.append("\t");
		sb.append(tw.HTML);
		return sb.toString();
	}
	
	public static ArrayList<Tweet> load(String filePath) throws Exception {
		ArrayList<Tweet> result = new ArrayList<>();
		List<String> lines = Files.readAllLines(Paths.get(filePath), 
				Charset.forName("UTF-8"));
		for (int ind = 0; ind < lines.size(); ++ind) {
			Tweet tw = new Tweet(lines.get(ind));
			result.add(tw);
		}
		return result;
	}
	
	public static void save(ArrayList<Tweet> list, String filePath) 
			throws Exception {
		PrintStream ps = new PrintStream(filePath, "UTF-8");
		for (int ind = 0; ind < list.size(); ++ind) {
			ps.println(list.get(ind).toString());
		}
		ps.close();
	}
	
	public static String printHumanReadable(Tweet tw) {
		StringBuilder result = new StringBuilder();
		result.append(tw.Query + "\t" +  tw.Tweetid + "\t" + tw.Time + 
				"\treply:" + tw.ReplyCount + "\tlike:" + tw.LikeCount + 
				"\tretweet:" + tw.RetweetCount + "\t\t" + tw.Text + "\n");
		result.append("\t> " + tokenInReplyTo + ":\n");
		for (int ind = 0; ind < tw.InReplyToSample.size(); ++ind) {
			Tweet tw2 = tw.InReplyToSample.get(ind);
			result.append("\t" + (ind + 1) + ". " + tw2.Query + "\t" +  
					tw2.Tweetid + "\t" + tw2.Time + "\treply:" + tw2.ReplyCount + 
					"\tlike:" + tw2.LikeCount + "\tretweet:" + tw2.RetweetCount + "\t\t" + tw2.Text + "\n");
		}
		result.append("\t> " + tokenReply+ ":\n");
		for (int ind = 0; ind < tw.ReplySample.size(); ++ind) {
			Tweet tw2 = tw.ReplySample.get(ind);
			result.append("\t" + (ind + 1) + ". " + tw2.Query + "\t" +  
					tw2.Tweetid + "\t" + tw2.Time + "\treply:" + tw2.ReplyCount + 
					"\tlike:" + tw2.LikeCount + "\tretweet:" + tw2.RetweetCount + "\t\t" + tw2.Text + "\n");
		}
		return result.toString();
	}
	
	public static String getTexts(ArrayList<Tweet> list) {
		StringBuilder result = new StringBuilder();
		for (int ind = 0; ind < list.size(); ++ind) {
			result.append(list.get(ind).Text + "\n");
		}
		return result.toString();
	}
	
}
