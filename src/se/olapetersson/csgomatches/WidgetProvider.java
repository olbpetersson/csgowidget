package se.olapetersson.csgomatches;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WidgetProvider extends AppWidgetProvider{
	static int TO_SHOW = 7;
	private static int current_state = 0;
	static int nr_of_matches = 0;
	RemoteViews remoteViews;
	boolean firstTime = true;
	Document doc;
	static Toast mToast;
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		 remoteViews = new RemoteViews(context.getPackageName(), R.layout.button_layout);
		
		for (int i=0; i<appWidgetIds.length;i++) {
			Intent intent = new Intent(context, WidgetProvider.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			intent.setAction("update");	
			
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetIds[i], intent, 0);
			
			remoteViews.setOnClickPendingIntent(R.id.img_refresh, pendingIntent);
			
			Intent rightIntent = new Intent(context, WidgetProvider.class);
			rightIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			rightIntent.setAction("right");
			
			PendingIntent pendingIntentRight = PendingIntent.getBroadcast(context, appWidgetIds[i], rightIntent, 0);
			remoteViews.setOnClickPendingIntent(R.id.img_right, pendingIntentRight);
			
			Intent leftIntent = new Intent(context, WidgetProvider.class);
			rightIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			leftIntent.setAction("left");
			
			PendingIntent pendingIntentLeft = PendingIntent.getBroadcast(context, appWidgetIds[i], leftIntent, 0);
			remoteViews.setOnClickPendingIntent(R.id.img_left, pendingIntentLeft);
			
			new CollectDataTask().execute(context);
			appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
			super.onUpdate(context, appWidgetManager, appWidgetIds);
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(mToast==null)
			mToast = Toast.makeText(context, "No more matches", Toast.LENGTH_SHORT);
		super.onReceive(context, intent);
		try {
			if (intent.getAction().equals("right") || intent.getAction().equals("left")) {
				if(intent.getAction().equals("right")){
					if(current_state+TO_SHOW < nr_of_matches+1) {
						current_state++;
					} else {
						mToast.setText("No more matches");
						mToast.show();
					}
				}
				else if (current_state > 0){
						current_state--;
				} 
				else {
					mToast.setText("No more matches");
					mToast.show();
				}
				new CollectDataTask().execute(context);
			} 
			else if (intent.getAction().equals("update")){
				doc = null;
				current_state = 0;
				mToast.setText("Updated");
				mToast.show();
				new CollectDataTask().execute(context);
			} else if (intent.getAction().contains("url") || intent.getAction().equals("match2")) {
				String url = intent.getStringExtra("URL");
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(browserIntent);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	class CollectDataTask extends AsyncTask<Context, Void, List<MyListItem>>{
		private Context context;
		
		@Override
		protected List<MyListItem> doInBackground(Context... params) {
			
			ArrayList<MyListItem> listItems = new ArrayList<MyListItem>();
			 
			try {
				context = params[0];
				
				if(doc == null) {
					doc = Jsoup.connect("http://www.hltv.org/hltv.rss.php?pri=15").get();
				}
				Elements descriptions = doc.getElementsByTag("description");
				Elements titles = doc.getElementsByTag("title");
				Elements times = doc.getElementsByTag("pubDate");
				nr_of_matches = titles.size()-1;
				if (titles != null) {
					int roof = titles.size();
					if(roof>=current_state+TO_SHOW) {
						roof = current_state+TO_SHOW;
					}
					for (int i = current_state+1; i<roof; i++) {
						MyListItem item = new MyListItem(titles.get(i).text(), times.get(i-1).text(), descriptions.get(i).text());						
						List<Node> nodes = descriptions.get(i).siblingNodes();

						for(Node n : nodes) {
							if(n.toString().contains("http")) {
								item.setURL(n.toString());
							}
						}
						
						
						listItems.add(item);
					}
				} 
			} catch (IOException e) {
				e.printStackTrace();
				MyListItem item = new MyListItem("Unable to connect", "Error", "Please check your internet connection");
				listItems.add(item);
				listItems.addAll(fillPlaceHolders());
				
			}
			
			if(nr_of_matches < 6) {
				if(nr_of_matches == 0) {
					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date date = new Date();
					MyListItem noMatchesItem = new MyListItem("No upcoming matches", "Last updated", dateFormat.format(date));
					
					listItems.add(noMatchesItem);
				}
				
				listItems.addAll(fillPlaceHolders());
			}
			return listItems;
		}
		
		public List<MyListItem> fillPlaceHolders() {
			List<MyListItem> returnList = new ArrayList<MyListItem>();
			
			for(int i = 1; i<6;i++) {
				returnList.add(new MyListItem(""));
			}
			return returnList;
			
		}
		@Override
		protected void onPostExecute(List<MyListItem> result) {
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			
			remoteViews = new RemoteViews(context.getPackageName(), R.layout.button_layout);
			try {		
						MyListItem item = result.get(0);
						remoteViews.setTextViewText(R.id.tv_team_labels, item.getLabelTeams());
						remoteViews.setTextViewText(R.id.tv_starts_at, item.getStartTime());
						remoteViews.setTextViewText(R.id.tv_tournament, item.getTournament());
						Intent webIntent = new Intent(context, WidgetProvider.class);
						webIntent.putExtra("URL", item.getURL());
						webIntent.setAction("url"+item.getLabelTeams());
						PendingIntent pendingIntentWeb = PendingIntent.getBroadcast(context, 0, webIntent, 0);
						remoteViews.setOnClickPendingIntent(R.id.rl_button, pendingIntentWeb);

						MyListItem item2 = result.get(1);
						remoteViews.setTextViewText(R.id.tv_team_labels2, item2.getLabelTeams());
						remoteViews.setTextViewText(R.id.tv_starts_at2, item2.getStartTime());
						remoteViews.setTextViewText(R.id.tv_tournament2, item2.getTournament());
						Intent webIntent2 = new Intent(context, WidgetProvider.class);
						webIntent2.putExtra("URL", item2.getURL());
						webIntent2.setAction("url"+item2.getLabelTeams());
						PendingIntent pendingIntentWeb2 = PendingIntent.getBroadcast(context, 0, webIntent2, 0);
						remoteViews.setOnClickPendingIntent(R.id.rl_button2, pendingIntentWeb2);
					
					
						MyListItem item3 = result.get(2);
						remoteViews.setTextViewText(R.id.tv_team_labels3, item3.getLabelTeams());
						remoteViews.setTextViewText(R.id.tv_starts_at3, item3.getStartTime());
						remoteViews.setTextViewText(R.id.tv_tournament3, item3.getTournament());
						Intent webIntent3 = new Intent(context, WidgetProvider.class);
						webIntent3.putExtra("URL", item3.getURL());
						webIntent3.setAction("url"+item3.getLabelTeams());
						PendingIntent pendingIntentWeb3 = PendingIntent.getBroadcast(context, 0, webIntent3, 0);
						remoteViews.setOnClickPendingIntent(R.id.rl_button3, pendingIntentWeb3);
				
						MyListItem item4 = result.get(3);
						remoteViews.setTextViewText(R.id.tv_team_labels4, item4.getLabelTeams());
						remoteViews.setTextViewText(R.id.tv_starts_at4, item4.getStartTime());
						remoteViews.setTextViewText(R.id.tv_tournament4, item4.getTournament());
						Intent webIntent4 = new Intent(context, WidgetProvider.class);
						webIntent4.putExtra("URL", item4.getURL());
						webIntent4.setAction("url"+item4.getLabelTeams());
						PendingIntent pendingIntentWeb4 = PendingIntent.getBroadcast(context, 0, webIntent4, 0);
						remoteViews.setOnClickPendingIntent(R.id.rl_button4, pendingIntentWeb4);
					
						MyListItem item5 = result.get(4);
						remoteViews.setTextViewText(R.id.tv_team_labels5, item5.getLabelTeams());
						remoteViews.setTextViewText(R.id.tv_starts_at5, item5.getStartTime());
						remoteViews.setTextViewText(R.id.tv_tournament5, item5.getTournament());
						Intent webIntent5 = new Intent(context, WidgetProvider.class);
						webIntent5.putExtra("URL", item5.getURL());
						webIntent5.setAction("url"+item5.getLabelTeams());
						PendingIntent pendingIntentWeb5 = PendingIntent.getBroadcast(context, 0, webIntent5, 0);
						remoteViews.setOnClickPendingIntent(R.id.rl_button5, pendingIntentWeb5);
				
						MyListItem item6 = result.get(5);
						remoteViews.setTextViewText(R.id.tv_team_labels6, item6.getLabelTeams());
						remoteViews.setTextViewText(R.id.tv_starts_at6, item6.getStartTime());
						remoteViews.setTextViewText(R.id.tv_tournament6, item6.getTournament());
						Intent webIntent6 = new Intent(context, WidgetProvider.class);
						webIntent6.putExtra("URL", item6.getURL());
						webIntent6.setAction("url"+item6.getLabelTeams());
						PendingIntent pendingIntentWeb6 = PendingIntent.getBroadcast(context, 0, webIntent6, 0);
						remoteViews.setOnClickPendingIntent(R.id.rl_button6, pendingIntentWeb6);
		
			} catch(IndexOutOfBoundsException e) {
				e.printStackTrace();
				
			}
		
			appWidgetManager.updateAppWidget(new ComponentName(context.getPackageName(), WidgetProvider.class.getName()), remoteViews);
			
		}
	}
	
}
