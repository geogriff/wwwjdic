package org.nick.wwwjdic.updates;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.nick.wwwjdic.R;
import org.nick.wwwjdic.WwwjdicPreferences;
import org.nick.wwwjdic.client.HttpClientFactory;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class UpdateCheckService extends IntentService {

    private static final String TAG = UpdateCheckService.class.getSimpleName();

    private static final String VERSIONS_URL_KEY = "versions-url";
    private static final String MARKET_NAME_KEY = "market-name";

    private String versionsUrl;
    private String marketName;

    private HttpClient httpClient;

    public static Intent createStartIntent(Context ctx, String versionsUrl,
            String marketName) {
        Intent intent = new Intent(ctx, UpdateCheckService.class);
        intent.putExtra(VERSIONS_URL_KEY, versionsUrl);
        intent.putExtra(MARKET_NAME_KEY, marketName);

        return intent;
    }

    public UpdateCheckService() {
        super("UpdateCheckService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        versionsUrl = extras.getString(VERSIONS_URL_KEY);
        marketName = extras.getString(MARKET_NAME_KEY);

        httpClient = HttpClientFactory
                .createWwwjdicHttpClient(WwwjdicPreferences
                        .getWwwjdicTimeoutSeconds(this) * 1000);

        HttpGet get = new HttpGet(versionsUrl);

        try {
            Log.d(TAG, "getting latests versions info from " + versionsUrl);
            HttpResponse response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() != 200) {
                Log.e(TAG,
                        "error getting current versions: "
                                + response.getStatusLine());
                if (response.getEntity() != null) {
                    response.getEntity().consumeContent();
                }
                return;
            }

            String jsonStr = EntityUtils.toString(response.getEntity());
            List<Version> allVersions = Version.listFromJson(jsonStr);
            Log.d(TAG, String.format("got %d versions", allVersions.size()));
            Version thisAppsVersion = Version.getAppVersion(this, marketName);
            if (thisAppsVersion == null) {
                return;
            }
            List<Version> matchingVersions = Version.findMatching(
                    thisAppsVersion, allVersions);
            if (matchingVersions.isEmpty()) {
                Log.w(TAG, "no versions matching current market : "
                        + marketName);
                return;
            }

            Version latest = matchingVersions.get(0);
            boolean needsUpdate = latest.getVersionCode() > thisAppsVersion
                    .getVersionCode();
            if (needsUpdate) {
                showNotification(latest);
            } else {
                Log.i(TAG, "already using latest version: " + thisAppsVersion);
            }

            // only set this on success
            WwwjdicPreferences.setLastUpdateCheck(this,
                    System.currentTimeMillis());
        } catch (Exception e) {
            Log.e(TAG,
                    "error checking for current versions: " + e.getMessage(), e);
        }
    }

    private void showNotification(Version latest) {
        CharSequence message = getResources().getString(
                R.string.update_available, latest.getVersionName());
        Notification notification = new Notification(R.drawable.ic_stat_update,
                message, System.currentTimeMillis());
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(latest
                .getDownloadUrl()));
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        notification.setLatestEventInfo(this,
                getResources().getString(R.string.app_name), message,
                contentIntent);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(latest.getPackageName().hashCode(), notification);
    }
}
