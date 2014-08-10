package se.olapetersson.csgomatches;

import java.util.List;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetProvider4x2 extends RootWidgetProvider{

	@Override
	public void updateTextViews(List<MyListItem> result, Context context, RemoteViews remoteViews) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		remoteViews = new RemoteViews(context.getPackageName(), R.layout.two_times_four_layout);
		try {		
					MyListItem item = result.get(0);
					remoteViews.setTextViewText(R.id.tv_team_labels, item.getLabelTeams());
					remoteViews.setTextViewText(R.id.tv_starts_at, item.getStartTime());
					remoteViews.setTextViewText(R.id.tv_tournament, item.getTournament());
					Intent webIntent = new Intent(context, this.getClass());
					webIntent.putExtra("URL", item.getURL());
					webIntent.setAction("url"+item.getLabelTeams());
					PendingIntent pendingIntentWeb = PendingIntent.getBroadcast(context, 0, webIntent, 0);
					remoteViews.setOnClickPendingIntent(R.id.rl_button, pendingIntentWeb);

					MyListItem item2 = result.get(1);
					remoteViews.setTextViewText(R.id.tv_team_labels2, item2.getLabelTeams());
					remoteViews.setTextViewText(R.id.tv_starts_at2, item2.getStartTime());
					remoteViews.setTextViewText(R.id.tv_tournament2, item2.getTournament());
					Intent webIntent2 = new Intent(context, this.getClass());
					webIntent2.putExtra("URL", item2.getURL());
					webIntent2.setAction("url"+item2.getLabelTeams());
					PendingIntent pendingIntentWeb2 = PendingIntent.getBroadcast(context, 0, webIntent2, 0);
					remoteViews.setOnClickPendingIntent(R.id.rl_button2, pendingIntentWeb2);
					
					MyListItem item3 = result.get(2);
					remoteViews.setTextViewText(R.id.tv_team_labels3, item3.getLabelTeams());
					remoteViews.setTextViewText(R.id.tv_starts_at3, item3.getStartTime());
					remoteViews.setTextViewText(R.id.tv_tournament3, item3.getTournament());
					Intent webIntent3 = new Intent(context, this.getClass());
					webIntent3.putExtra("URL", item3.getURL());
					webIntent3.setAction("url"+item3.getLabelTeams());
					PendingIntent pendingIntentWeb3 = PendingIntent.getBroadcast(context, 0, webIntent3, 0);
					remoteViews.setOnClickPendingIntent(R.id.rl_button3, pendingIntentWeb3);
				
					System.out.println("HEj2" +item.getLabelTeams() +"+"+item2.getLabelTeams());
		} catch(IndexOutOfBoundsException e) {
			e.printStackTrace();
			System.out.println("error");
			
		}
		System.out.println("layout: " +layout);
		String lastUpdatedText ="";
		if(date!=null) {
			remoteViews.setTextViewText(R.id.tv_last_updated, "Last updated: " +dateFormat.format(date));
		}
		System.out.println("AESDSADSA"+ getClass().getName());
		
		appWidgetManager.updateAppWidget(new ComponentName(context.getPackageName(), this.getClass().getName()), remoteViews);
	}

	@Override
	public void setLayout() {
		layout = R.layout.two_times_four_layout;
	}

	@Override
	public int getNrToShow() {
		return 4;
	}

}