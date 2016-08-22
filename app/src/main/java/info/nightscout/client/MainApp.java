package info.nightscout.client;

import android.app.Application;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import io.fabric.sdk.android.Fabric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.nightscout.client.data.NSProfile;
import info.nightscout.client.data.UploadQueue;

/**
 * Created by mike on 17.02.2016.
 */
public class MainApp extends Application {
    private static Logger log = LoggerFactory.getLogger(MainApp.class);

    private static MainApp sInstance;
    private static Bus sBus;

    private static Integer downloadedRecords = 0;

    private static NSClient nsClient = null;

    public static NSProfile nsProfile;

    @Override
    public void onCreate() {
        super.onCreate();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("enable_crashlytics", true)) {
            Fabric.with(this, new Crashlytics());
        }
        sInstance = this;

        sBus = new Bus(ThreadEnforcer.ANY);
    }

    public static MainApp instance() {
        return sInstance;
    }

    public static Bus bus() {
        return sBus;
    }

    public static NSClient getNSClient() {
        return nsClient;
    }
    public static void setNSClient(NSClient client) {
        nsClient = client;
    }

    public static void setNsProfile(NSProfile profile) { nsProfile = profile; }
    public static NSProfile getNsProfile() { return nsProfile; }

}
