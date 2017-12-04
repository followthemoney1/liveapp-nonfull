package pc.dd.liveapp;

import android.app.Application;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.PushServiceApi26;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import java.util.HashMap;

import butterknife.ButterKnife;
import pc.dd.liveapp.data.parse.notification.MyFirebaseInstanceIDService;
import pc.dd.liveapp.utils.Constants;

/**
 * Created by leaditteam on 07.09.17.
 */

public class App extends Application {
  
    @Override
    public void onCreate() {
        super.onCreate();
        ButterKnife.setDebug(BuildConfig.DEBUG);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getResources().getString(R.string.parse_app_id))
                .clientKey("3HmZbYvXx3Mr")
                .server(getResources().getString(R.string.parse_server_url))
                .enableLocalDataStore()
                .build());
        
        ParseFacebookUtils.initialize(this);
    }
    
    
}
