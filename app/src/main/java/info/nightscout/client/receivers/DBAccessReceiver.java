package info.nightscout.client.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import info.nightscout.client.MainApp;
import info.nightscout.client.NSClient;
import info.nightscout.client.acks.NSAddAck;
import info.nightscout.client.acks.NSUpdateAck;
import info.nightscout.client.data.DbRequest;
import info.nightscout.client.data.UploadQueue;

public class DBAccessReceiver extends BroadcastReceiver {
    private static Logger log = LoggerFactory.getLogger(DBAccessReceiver.class);


    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "sendQueue");
        wakeLock.acquire();
        try {
            Bundle bundles = intent.getExtras();
            if (bundles == null) return;
            if (!bundles.containsKey("action")) return;

            NSClient nsClient = MainApp.getNSClient();
            if (nsClient == null) return;

            String collection = null;
            String _id = null;
            JSONObject data = null;
            String action = bundles.getString("action");
            try { collection = bundles.getString("collection"); } catch (Exception e) {}
            try { _id = bundles.getString("_id"); } catch (Exception e) {}
            try { data = new JSONObject(bundles.getString("data")); } catch (Exception e) {}

            if (data == null) {
                log.debug("DBACCESS no data inside record");
                return;
            }

            // mark by id
            try {
                data.put("NSCLIENT_ID", (new Date()).getTime());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (!isAllowedCollection(collection)) {
                log.debug("DBACCESS wrong collection specified");
                return;
            }

            DbRequest dbr = new DbRequest(action, collection, data);
            UploadQueue.add(dbr);

        } finally {
            wakeLock.release();
        }

    }

    private boolean isAllowedCollection(String collection) {
        // "treatments" || "entries" || "devicestatus" || "profile" || "food"
        if (collection.equals("treatments")) return true;
        if (collection.equals("entries")) return true;
        if (collection.equals("devicestatus")) return true;
        if (collection.equals("profile")) return true;
        if (collection.equals("food")) return true;
        return false;
    }
}
