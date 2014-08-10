package se.olapetersson.csgomatches;

import java.io.IOException;
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

abstract public class RootWidgetProvider extends AppWidgetProvider{
	//int TO_SHOW;
	private static int current_state = 0;
	static int nr_of_matches = 0;
	RemoteViews remoteViews;
	boolean firstTime = true;
	Document doc;
	static Toast mToast;
	Date date;
	int layout;
	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public void init(Context context) {
		setLayout();
	}
	
	abstract public void setLayout();
	
	abstract public int getNrToShow();
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		 init(context);
		 remoteViews = new RemoteViews(context.getPackageName(), layout);
		 date = new Date();
		 for (int i=0; i<appWidgetIds.length;i++) {
			Intent intent = new Intent(context, this.getClass());
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			intent.setAction("update");	
			
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetIds[i], intent, 0);
			
			remoteViews.setOnClickPendingIntent(R.id.img_refresh, pendingIntent);
			
			Intent rightIntent = new Intent(context, this.getClass());
			rightIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			rightIntent.setAction("right");
			
			PendingIntent pendingIntentRight = PendingIntent.getBroadcast(context, appWidgetIds[i], rightIntent, 0);
			remoteViews.setOnClickPendingIntent(R.id.img_right, pendingIntentRight);
			
			Intent leftIntent = new Intent(context, this.getClass());
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
					if(current_state+getNrToShow() < nr_of_matches+1) {
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
				date = new Date();
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
	abstract public void updateTextViews(List<MyListItem> result, Context context, RemoteViews remoteViews);
	class CollectDataTask extends AsyncTask<Context, Void, List<MyListItem>>{
		private Context context;
		
		@Override
		protected List<MyListItem> doInBackground(Context... params) {
			
			ArrayList<MyListItem> listItems = new ArrayList<MyListItem>();
			int roof = 0; 
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
					roof = titles.size();
					if(roof>=current_state+getNrToShow()) {
						roof = current_state+getNrToShow();
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
				if(roof < getNrToShow()-1) {
					if(nr_of_matches == 0) {
						MyListItem noMatchesItem = new MyListItem("No upcoming matches", "Last updated", dateFormat.format(date));
						
						listItems.add(noMatchesItem);
					}
					
					listItems.addAll(fillPlaceHolders());
				}
			} catch (IOException e) {
				e.printStackTrace();
				date = null;
			}
			System.out.println(nr_of_matches);
			System.out.println("TOSHOW: " +getNrToShow());
			return listItems;
		}
		
		public List<MyListItem> fillPlaceHolders() {
			List<MyListItem> returnList = new ArrayList<MyListItem>();
			
			for(int i = 1; i<getNrToShow()-1;i++) {
				returnList.add(new MyListItem(""));
			}
			return returnList;
			
		}
		@Override
		protected void onPostExecute(List<MyListItem> result) {
			updateTextViews(result, context, remoteViews);
		}
		
		
	}
	
}
