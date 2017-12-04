package pc.dd.liveapp.data.parse;

import android.support.annotation.NonNull;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.Serializable;
import java.util.List;

import pc.dd.liveapp.ui.other.ProgressBarLayout;
import pc.dd.liveapp.utils.Constants;

/**
 * Created by leaditteam on 02.11.17.
 */

public class ParseEventRequestUtils implements Serializable {
    private transient ParseUserUtils parseUserUtils;
    private transient ParseEventUtils parseEventUtils;
    
    private String status;
    private String objectId;
    
    private transient ParseObject currentObjectEventRequest;
    
    public ParseEventRequestUtils() {
             this
                .setParseEventUtils(parseEventUtils)
                .setParseUserUtils(parseUserUtils);
    }
    
    public interface ParseEventRequestCallback {
        void done(ParseEventRequestUtils parseEventRequest);
    }
    
    public void findByParseUserAndParseEvent(boolean showDalayedProgressBar , final ParseEventRequestCallback parseEventRequestInterface) {
        if (showDalayedProgressBar)
        ProgressBarLayout.showProgressBarDalayed(50, false);
        
        final ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.EVENT_REQUEST_STRING);
        query.whereEqualTo(Constants.USER, parseUserUtils.getUser());
        query.whereEqualTo(Constants.EVENT_STRING_TO_EVENT_REQUEST, parseEventUtils.getEventObject());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                ProgressBarLayout.dismissDelayedProgressBar();
                if (e == null) {
                    if (!ifObjectsMoreThanZero(objects, parseEventRequestInterface))
                        parseEventRequestInterface.done(ParseEventRequestUtils.this);
                } else {
                    parseEventRequestInterface.done(ParseEventRequestUtils.this);
                }
            }
        });
    }
    
    private Boolean ifObjectsMoreThanZero(List<ParseObject> objects, ParseEventRequestCallback parseEventRequestInterface) {
        if (objects.size() > 0) {
            status = objects.get(0).get(Constants.STATUS_STRING).toString();
            objectId = objects.get(0).getObjectId();
            currentObjectEventRequest = objects.get(0);
            parseEventRequestInterface.done(new ParseEventRequestUtils()
                    .setParseEventUtils(parseEventUtils)
                    .setParseUserUtils(parseUserUtils)
                    .setStatus(status)
                    .setCurrentObjectEventRequest(currentObjectEventRequest)
                    .setObjectId(objectId));
            
            return true;
        } else return false;
    }
    
    public void update(final ParseEventRequestCallback parseEventRequestInterface) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.EVENT_REQUEST_STRING);
        query.whereEqualTo(Constants.USER, parseUserUtils.getUser());
        query.whereEqualTo(Constants.EVENT_STRING_TO_EVENT_REQUEST, parseEventUtils.getEventObject());
        ParseObject.unpinAllInBackground(Constants.EVENT_REQUEST_STRING, new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(final List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            if (objects.size() > 0) {
                                
                                status = objects.get(0).get(Constants.STATUS_STRING).toString();
                                objectId = objects.get(0).getObjectId();
                                parseEventUtils =
                                        ParseEventUtils.getOneParseEventByObject((ParseObject) objects.get(0).get(Constants.EVENT_STRING_TO_EVENT_REQUEST));
                                parseUserUtils =
                                        ParseUserUtils.getParseUserUtilFromParseUser((ParseUser) objects.get(0).get(Constants.USER));
                                currentObjectEventRequest = objects.get(0);
                                
                                parseEventRequestInterface.done(new ParseEventRequestUtils()
                                        .setParseEventUtils(parseEventUtils)
                                        .setParseUserUtils(parseUserUtils)
                                        .setStatus(status)
                                        .setCurrentObjectEventRequest(currentObjectEventRequest)
                                        .setObjectId(objectId));
                            } else {
                                parseEventRequestInterface.done(new ParseEventRequestUtils()
                                        .setParseEventUtils(parseEventUtils)
                                        .setParseUserUtils(parseUserUtils)
                                        .setStatus(status)
                                        .setCurrentObjectEventRequest(currentObjectEventRequest)
                                        .setObjectId(objectId));
                            }
                        }
                    }
                });
                
            }
        });
    }
    
    public void findEventDependencies(final ParseCallbacks.ParseFindCallback parseEventRequestCallback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.EVENT_REQUEST_STRING);
        query.whereEqualTo(Constants.EVENT_STRING_TO_EVENT_REQUEST, this.parseEventUtils.getEventObject());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null)
                    parseEventRequestCallback.done(objects);
            }
        });
    }
    
    public static void createEventRequest(ParseUser user, ParseObject event, String status, final SaveCallback callback) {
        ParseObject eventRequest = new ParseObject(Constants.EVENT_REQUEST_STRING);
        eventRequest.put(Constants.USER, user);
        eventRequest.put(Constants.EVENT_STRING_TO_EVENT_REQUEST, event);
        eventRequest.put(Constants.STATUS_STRING, status);
        eventRequest.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null)
                callback.done(e);
            }
        });
    }
    
    public ParseObject getCurrentObjectEventRequest() {
        return currentObjectEventRequest;
    }
    
    public ParseEventRequestUtils setCurrentObjectEventRequest(ParseObject currentObjectEventRequest) {
        this.currentObjectEventRequest = currentObjectEventRequest;
        return this;
    }
    
    public ParseUserUtils getParseUserUtils() {
        return parseUserUtils;
    }
    
    public ParseEventRequestUtils setParseUserUtils(ParseUserUtils parseUserUtils) {
        this.parseUserUtils = parseUserUtils;
        return this;
    }
    
    public ParseEventUtils getParseEventUtils() {
        return parseEventUtils;
    }
    
    public ParseEventRequestUtils setParseEventUtils(ParseEventUtils parseEventUtils) {
        this.parseEventUtils = parseEventUtils;
        return this;
    }
    
    public String getStatus() {
        return status;
    }
    
    public ParseEventRequestUtils setStatus(String status) {
        this.status = status;
        return this;
    }
    
    public String getObjectId() {
        return objectId;
    }
    
    public ParseEventRequestUtils setObjectId(String objectId) {
        this.objectId = objectId;
        return this;
    }
}
