package se.olapetersson.csgomatches;

import java.util.List;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetProvider4x4 extends RootWidgetProvider{

	@Override
	public void setLayout() {
		// TODO Auto-generated method stub
		layout = R.layout.four_times_four_layout;
	}

	@Override
	public void updateTextViews(List<MyListItem> result, Context context, RemoteViews remoteViews) {
		// TODO Auto-generated method stub
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		remoteViews = new RemoteViews(context.getPackageName(), R.layout.four_times_four_layout);
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
	
			MyListItem item4 = result.get(3);
			remoteViews.setTextViewText(R.id.tv_team_labels4, item4.getLabelTeams());
			remoteViews.setTextViewText(R.id.tv_starts_at4, item4.getStartTime());
			remoteViews.setTextViewText(R.id.tv_tournament4, item4.getTournament());
			Intent webIntent4 = new Intent(context, this.getClass());
			webIntent4.putExtra("URL", item4.getURL());
			webIntent4.setAction("url"+item4.getLabelTeams());
			PendingIntent pendingIntentWeb4 = PendingIntent.getBroadcast(context, 0, webIntent4, 0);
			remoteViews.setOnClickPendingIntent(R.id.rl_button4, pendingIntentWeb4);
		
			MyListItem item5 = result.get(4);
			remoteViews.setTextViewText(R.id.tv_team_labels5, item5.getLabelTeams());
			remoteViews.setTextViewText(R.id.tv_starts_at5, item5.getStartTime());
			remoteViews.setTextViewText(R.id.tv_tournament5, item5.getTournament());
			Intent webIntent5 = new Intent(context, this.getClass());
			webIntent5.putExtra("URL", item5.getURL());
			webIntent5.setAction("url"+item5.getLabelTeams());
			PendingIntent pendingIntentWeb5 = PendingIntent.getBroadcast(context, 0, webIntent5, 0);
			remoteViews.setOnClickPendingIntent(R.id.rl_button5, pendingIntentWeb5);
	
			MyListItem item6 = result.get(5);
			remoteViews.setTextViewText(R.id.tv_team_labels6, item6.getLabelTeams());
			remoteViews.setTextViewText(R.id.tv_starts_at6, item6.getStartTime());
			remoteViews.setTextViewText(R.id.tv_tournament6, item6.getTournament());
			Intent webIntent6 = new Intent(context, this.getClass());
			webIntent6.putExtra("URL", item6.getURL());
			webIntent6.setAction("url"+item6.getLabelTeams());
			PendingIntent pendingIntentWeb6 = PendingIntent.getBroadcast(context, 0, webIntent6, 0);
			remoteViews.setOnClickPendingIntent(R.id.rl_button6, pendingIntentWeb6);

		} catch(IndexOutOfBoundsException e) {
			e.printStackTrace();
			
		}
		if(date!=null)
			remoteViews.setTextViewText(R.id.tv_last_updated, "Last updated: " +dateFormat.format(date));
		System.out.println("AESDSADSA"+ getClass().getName());
		appWidgetManager.updateAppWidget(new ComponentName(context.getPackageName(), this.getClass().getName()), remoteViews);
	}

	@Override
	public int getNrToShow() {
		return 7;
	}
}