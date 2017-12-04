package pc.dd.liveapp.data.parse;

import android.content.Context;
import android.support.annotation.NonNull;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import pc.dd.liveapp.R;
import pc.dd.liveapp.ui.other.ProgressBarLayout;
import pc.dd.liveapp.utils.Constants;
import pc.dd.liveapp.utils.UiHelper;

/**
 * Created by leaditteam on 09.11.17.
 */

public class ParseCallbacks {
    
    public interface ParseFindCallback {
        void done(List<ParseObject> objects);
    }
    public interface ParseCallback {
        void done();
    }
    
    public interface EmailCallback{
        void exist(Boolean exist);
    }
    
    public interface LoadCallback{
        void onLoad(Object object);
    }
    
    public static void findEventListFromParseObjectsId(String parseEventObjectId, final ParseFindCallback findCallback){
        if (parseEventObjectId!=null) {
            ProgressBarLayout.showProgressBar();
            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(Constants.EVENT_STRING);
            parseQuery.whereContains(Constants.OBJECT_ID_STRING, parseEventObjectId);
            parseQuery.findInBackground(new com.parse.FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        findCallback.done(objects);
                    }
                    ProgressBarLayout.dismissProgressBar();
                }
            });
        }
    }
    
    public static void findEventRequestListByParseUser(ParseUser parseUser, final ParseFindCallback parseFindCallback){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.EVENT_REQUEST_STRING);
        query.whereEqualTo(Constants.USER, parseUser);
        query.fromNetwork().findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    parseFindCallback.done(objects);
                }
            }
        });
    }
    
    public static void findEventRequestListByOneEvent(ParseObject parseObject, final ParseFindCallback parseFindCallback){
        ProgressBarLayout.showProgressBar();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.EVENT_REQUEST_STRING);
        query.whereEqualTo(Constants.EVENT_STRING_TO_EVENT_REQUEST, parseObject);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    parseFindCallback.done(objects);
                }
                ProgressBarLayout.dismissProgressBar();
            }
        });
    }
    
    public static void findUserByFacebookId(String facebookId, final ParseFindCallback parseFindCallback){
        ParseQuery query = ParseUser.getQuery();
        query.whereEqualTo(Constants.FACEBOOK_ID_STRING,facebookId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size()>0)
                        parseFindCallback.done(objects);
                }
            }
        });
    }
    
    public static void deleteEventRequestObjectIfStatusPending(ParseEventRequestUtils parseEventRequest, Context context, final ParseCallback callback) {
        ProgressBarLayout.showProgressBar();
        if (parseEventRequest.getStatus() != null && parseEventRequest.getCurrentObjectEventRequest() != null
                && !parseEventRequest.getParseEventUtils().getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            if (parseEventRequest.getStatus().equals(Constants.STATUS_PENDING_STRING)) {
                parseEventRequest.getCurrentObjectEventRequest().deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                           callback.done();
                        }
                        ProgressBarLayout.dismissProgressBar();
                    }
                });
            }
        } else {
            UiHelper.showToast(context, context.getString(R.string.toast_you_cant_leave_you_party));
            ProgressBarLayout.dismissProgressBar();
        }
    }
}
