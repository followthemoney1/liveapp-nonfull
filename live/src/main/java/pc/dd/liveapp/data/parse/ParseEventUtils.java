package pc.dd.liveapp.data.parse;

import android.os.SystemClock;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pc.dd.liveapp.ui.other.ProgressBarLayout;
import pc.dd.liveapp.utils.FileUtils;
import pc.dd.liveapp.utils.Constants;
import pc.dd.liveapp.utils.animation.AdapterAnimationItemUtil;
import pc.dd.liveapp.utils.DateUtil;
import pc.dd.liveapp.data.interfaces.EventUpdateCallback;
import pc.dd.liveapp.data.interfaces.ViewInterface;

/**
 * Created by leaditteam on 26.09.17.
 */

public class ParseEventUtils implements Serializable {
    transient ParseObject eventObject = new ParseObject(Constants.EVENT_STRING);
    transient ParseUser user;
    transient ParseGeoPoint location;
    
    Boolean hasDj = false;
    Boolean hasSwim = false;
    int attendersCount = 0;
    int price = 0;
    Boolean hasMusic = false;
    int places = 0;
    String about;
    Date date;
    String address;
    Boolean hasFood = false;
    String title;
    
    Boolean hasSleep = false;
    Boolean hasCocktail;
    String fullAddress;
    String imageString;
    String type;
    String objectId;
    
    List<String> ignoredEvents = new ArrayList<>();
    
    ParseEventRequestUtils parseEventRequestUtils;
    
    int dependencyCount;
    
    public void createEvent(final ViewInterface recreatePartyFragment) {
        ProgressBarLayout.showProgressBar();
        ProgressBarLayout.setCanDismissed(false);
        
        byte[] data = FileUtils.getFilesToByte(imageString);
        final ParseFile image = new ParseFile(String.valueOf(SystemClock.currentThreadTimeMillis()) + ".jpeg", data);
        
        eventObject = new ParseObject(Constants.EVENT_STRING);
        
        eventObject.put(Constants.HAS_DJ_STRING, this.hasDj);
        eventObject.put(Constants.HAS_SWIM_STRING, this.hasSwim);
        eventObject.put(Constants.ATTENDER_COUNT_STRING, this.attendersCount);
        eventObject.put(Constants.PRICE_STRING, this.price);
        eventObject.put(Constants.HAS_MUSIC_STRING, this.hasMusic);
        eventObject.put(Constants.USER, this.user);
        eventObject.put(Constants.PLACES_STRING, this.places);
        eventObject.put(Constants.ABOUT_STRING, this.about);
        eventObject.put(Constants.DATE_STRING, this.date);
        eventObject.put(Constants.ADDRESS_STRING, this.address);
        eventObject.put(Constants.HAS_FOOD_STRING, this.hasFood);
        eventObject.put(Constants.TITLE_STRING, this.title);
        eventObject.put(Constants.IMAGE_STRING, image);
        eventObject.put(Constants.HAS_SLEEP_STRING, this.hasSleep);
        eventObject.put(Constants.HAS_COCKTAIL_STRING, this.hasCocktail);
        eventObject.put(Constants.FULL_ADDRESS_STRING, this.fullAddress);
        eventObject.put(Constants.LOCATION_STRING, this.location);
        eventObject.put(Constants.TYPE_STRING, this.type);
        
        image.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                    eventObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseEventRequestUtils.createEventRequest(user, eventObject, Constants.STATUS_PENDING_STRING, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        recreatePartyFragment.doneCallback();
                                    }
                                });
                            }
                        }
                    });
                ProgressBarLayout.dismissProgressBar();
                ProgressBarLayout.setCanDismissed(true);
            }
        });
    }
    
    public static List<ParseEventUtils> getEventList(final EventUpdateCallback eventUpdateInterface) {
        final List<ParseEventUtils> allEvents = new ArrayList<>();
        
        ParseQuery<ParseObject> receiver = ParseQuery.getQuery(Constants.EVENT_STRING);
        receiver.include(Constants.HAS_DJ_STRING);
        receiver.include(Constants.HAS_SWIM_STRING);
        receiver.include(Constants.ATTENDER_COUNT_STRING);
        receiver.include(Constants.PRICE_STRING);
        receiver.include(Constants.HAS_MUSIC_STRING);
        receiver.include(Constants.USER);
        receiver.include(Constants.PLACES_STRING);
        receiver.include(Constants.ABOUT_STRING);
        receiver.include(Constants.DATE_STRING);
        receiver.include(Constants.ADDRESS_STRING);
        receiver.include(Constants.HAS_FOOD_STRING);
        receiver.include(Constants.TITLE_STRING);
        receiver.include(Constants.IMAGE_STRING);
        receiver.include(Constants.HAS_SLEEP_STRING);
        receiver.include(Constants.HAS_COCKTAIL_STRING);
        receiver.include(Constants.FULL_ADDRESS_STRING);
        receiver.include(Constants.TYPE_STRING);
        receiver.include(Constants.LOCATION_STRING);
        receiver.include(Constants.IGNORED_EVENTS_STRING);
        
        receiver.whereGreaterThan(Constants.DATE_STRING, DateUtil.getCurrentDate());
        
        receiver.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> feed, ParseException e) {
                
                if (e == null) {
                    final int[] finalI = {0};
                    for (int i = 0; i < feed.size(); i++) {
                        
                        ParseObject obj = feed.get(i);
                        obj.saveEventually();
                        
                        List<String> ignoredEvent = ParseUser.getCurrentUser().getList(Constants.IGNORED_EVENTS_STRING);
                        
                        if (ignoredEvent == null) ignoredEvent = new ArrayList<>();
                        if (!ignoredEvent.contains(obj.getObjectId())) {
                            final ParseEventUtils parseEventUtils = getOneParseEventByObject(obj);
                            
                            new ParseEventRequestUtils()
                                    .setParseEventUtils(parseEventUtils)
                                    .setParseUserUtils(ParseUserUtils.getParseUserUtilFromParseUser(ParseUser.getCurrentUser()))
                                    .findByParseUserAndParseEvent(false, new ParseEventRequestUtils.ParseEventRequestCallback() {
                                        @Override
                                        public void done(ParseEventRequestUtils parseEventRequest) {
                                            finalI[0]++;
                                            
                                            parseEventUtils.setParseEventRequestUtils(parseEventRequest);
                                            allEvents.add(parseEventUtils);
                                            
                                            //update when end iteration
                                            if (finalI[0] == feed.size())
                                                eventUpdateInterface.getEventCallback();
                                        }
                                    });
                            
                        }
                    }
                    
                }
                
            }
        });
        
        return allEvents;
    }
    
    public void update(String objectId, final ViewInterface viewInterface) {
        ProgressBarLayout.showProgressBar();
        
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.EVENT_STRING);
        
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            public void done(final ParseObject event, ParseException e) {
                if (e == null) {
                    
                    byte[] data = FileUtils.getFilesToByte(imageString);
                    ParseFile image = null;
                    if (data.length > 0)
                        image = new ParseFile(String.valueOf(SystemClock.currentThreadTimeMillis()) + ".jpeg", data);
                    
                    event.put(Constants.HAS_DJ_STRING, hasDj);
                    event.put(Constants.HAS_SWIM_STRING, hasSwim);
                    event.put(Constants.ATTENDER_COUNT_STRING, attendersCount);
                    event.put(Constants.PRICE_STRING, price);
                    event.put(Constants.HAS_MUSIC_STRING, hasMusic);
                    event.put(Constants.USER, user);
                    event.put(Constants.PLACES_STRING, places);
                    event.put(Constants.ABOUT_STRING, about);
                    event.put(Constants.DATE_STRING, date);
                    event.put(Constants.ADDRESS_STRING, address);
                    event.put(Constants.HAS_FOOD_STRING, hasFood);
                    event.put(Constants.TITLE_STRING, title);
                    event.put(Constants.HAS_SLEEP_STRING, hasSleep);
                    event.put(Constants.HAS_COCKTAIL_STRING, hasCocktail);
                    event.put(Constants.FULL_ADDRESS_STRING, fullAddress);
                    event.put(Constants.TYPE_STRING, type);
                    
                    if (viewInterface != null)
                        if (image != null) {
                            event.put(Constants.IMAGE_STRING, image);
                            image.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null)
                                        event.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    viewInterface.doneCallback();
                                                }
                                            }
                                        });
                                }
                            });
                        } else {
                            event.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        viewInterface.doneCallback();
                                    }
                                }
                            });
                        }
                    ProgressBarLayout.dismissProgressBar();
                    
                }
            }
        });
    }
    
    public static void getEventById(String eventId, final ParseCallbacks.LoadCallback eventLoadCallback){
        ParseQuery parseQuery = ParseQuery.getQuery(Constants.EVENT_STRING);
        parseQuery.whereEqualTo(Constants.OBJECT_ID_STRING,eventId);
        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (objects!=null&&objects.size()>0)
                    eventLoadCallback.onLoad(getOneParseEventByObject(objects.get(0)));
            }});
    }
    
    public static ParseEventUtils getOneParseEventByObject(ParseObject obj) {
        try {
            obj.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ParseFile applicantResume = (ParseFile) obj.get(Constants.IMAGE_STRING);
        final ParseEventUtils parseEventUtils = new ParseEventUtils()
                .setHasDj((Boolean) obj.get(Constants.HAS_DJ_STRING))
                .setHasSwim((Boolean) obj.get(Constants.HAS_SWIM_STRING))
                .setAttendersCount((Integer) obj.get(Constants.ATTENDER_COUNT_STRING))
                .setPrice((Integer) obj.get(Constants.PRICE_STRING))
                .setHasMusic((Boolean) obj.get(Constants.HAS_MUSIC_STRING))
                .setUser((ParseUser) obj.get(Constants.USER))
                .setPlaces((Integer) obj.get(Constants.PLACES_STRING))
                .setAbout((String) obj.get(Constants.ABOUT_STRING))
                .setDate((Date) obj.get(Constants.DATE_STRING))
                .setAddress((String) obj.get(Constants.ADDRESS_STRING))
                .setHasFood((Boolean) obj.get(Constants.HAS_FOOD_STRING))
                .setTitle((String) obj.get(Constants.TITLE_STRING))
                .setImageString(applicantResume.getUrl())
                .setLocation((ParseGeoPoint) obj.get(Constants.LOCATION_STRING))
                .setHasSleep((Boolean) obj.get(Constants.HAS_SLEEP_STRING))
                .setHasCocktail((Boolean) obj.get(Constants.HAS_COCKTAIL_STRING))
                .setFullAddress((String) obj.get(Constants.FULL_ADDRESS_STRING))
                .setObjectId(obj.getObjectId())
                .setEventObject(obj)
                .setType((String) obj.get(Constants.TYPE_STRING));
        
        new ParseEventRequestUtils()
                .setParseEventUtils(parseEventUtils)
                .setParseUserUtils(ParseUserUtils.getParseUserUtilFromParseUser(ParseUser.getCurrentUser()))
                .findByParseUserAndParseEvent(false, new ParseEventRequestUtils.ParseEventRequestCallback() {
                    @Override
                    public void done(ParseEventRequestUtils parseEventRequest) {
                        parseEventUtils.setParseEventRequestUtils(parseEventRequest);
                    }
                });
        
        return parseEventUtils;
    }
    
    public void setIgnoredItem(ParseUser currentUtil, ParseEventUtils eventUtils, final EventUpdateCallback eventUpdate, final RelativeLayout relativeLayout) {
        List<String> ignoredEvent = eventUtils.getIgnoredEvents();
        ignoredEvent.add(eventUtils.getObjectId());
        currentUtil.addAllUnique(Constants.IGNORED_EVENTS_STRING, ignoredEvent);
        currentUtil.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    int height = relativeLayout.getMeasuredHeight();
                    AdapterAnimationItemUtil adapterAnimationItemUtil = new AdapterAnimationItemUtil(relativeLayout, height, 0);
                    adapterAnimationItemUtil.setDuration(800);
                    adapterAnimationItemUtil.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        
                        }
                        
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            eventUpdate.refreshEventCallback();
                        }
                        
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        
                        }
                    });
                    relativeLayout.setAnimation(adapterAnimationItemUtil);
                    relativeLayout.startAnimation(adapterAnimationItemUtil);
                }
            }
        });
    }
    
    public ParseEventRequestUtils getParseEventRequestUtils() {
        return parseEventRequestUtils;
    }
    
    public void setParseEventRequestUtils(ParseEventRequestUtils parseEventRequestUtils) {
        this.parseEventRequestUtils = parseEventRequestUtils;
    }
    
    public int getDependencyCount() {
        return dependencyCount;
    }
    
    public void setDependencyCount(int dependencyCount) {
        this.dependencyCount = dependencyCount;
    }
    
    public ParseGeoPoint getLocation() {
        return location;
    }
    
    public ParseEventUtils setLocation(ParseGeoPoint location) {
        this.location = location;
        return this;
    }
    
    public ParseObject getEventObject() {
        return eventObject;
    }
    
    public ParseEventUtils setEventObject(ParseObject eventObject) {
        this.eventObject = eventObject;
        return this;
    }
    
    public List<String> getIgnoredEvents() {
        return ignoredEvents;
    }
    
    public ParseEventUtils setIgnoredEvents(List<String> ignoredEvents) {
        this.ignoredEvents.addAll(ignoredEvents);
        return this;
    }
    
    public String getObjectId() {
        return objectId;
    }
    
    public ParseEventUtils setObjectId(String objectId) {
        this.objectId = objectId;
        return this;
    }
    
    public String getType() {
        return type;
    }
    
    public ParseEventUtils setType(String type) {
        this.type = type;
        return this;
    }
    
    public Boolean getHasDj() {
        return hasDj;
    }
    
    public ParseEventUtils setHasDj(Boolean hasDj) {
        this.hasDj = hasDj;
        return this;
    }
    
    public Boolean getHasSwim() {
        return hasSwim;
    }
    
    public ParseEventUtils setHasSwim(Boolean hasSwim) {
        this.hasSwim = hasSwim;
        return this;
    }
    
    public int getAttendersCount() {
        return attendersCount;
    }
    
    public ParseEventUtils setAttendersCount(int attendersCount) {
        this.attendersCount = attendersCount;
        return this;
    }
    
    public int getPrice() {
        return price;
    }
    
    public ParseEventUtils setPrice(int price) {
        this.price = price;
        return this;
    }
    
    public Boolean getHasMusic() {
        return hasMusic;
    }
    
    public ParseEventUtils setHasMusic(Boolean hasMusic) {
        this.hasMusic = hasMusic;
        return this;
    }
    
    public ParseUser getUser() {
        return user;
    }
    
    public ParseEventUtils setUser(ParseUser user) {
        this.user = user;
        return this;
    }
    
    public int getPlaces() {
        return places;
    }
    
    public ParseEventUtils setPlaces(int places) {
        this.places = places;
        return this;
    }
    
    public String getAbout() {
        return about;
    }
    
    public ParseEventUtils setAbout(String about) {
        this.about = about;
        return this;
    }
    
    public Date getDate() {
        return date;
    }
    
    public ParseEventUtils setDate(Date date) {
        this.date = date;
        return this;
    }
    
    public String getAddress() {
        return address;
    }
    
    public ParseEventUtils setAddress(String address) {
        this.address = address;
        return this;
    }
    
    public Boolean getHasFood() {
        return hasFood;
    }
    
    public ParseEventUtils setHasFood(Boolean hasFood) {
        this.hasFood = hasFood;
        return this;
    }
    
    public String getTitle() {
        return title;
    }
    
    public ParseEventUtils setTitle(String title) {
        this.title = title;
        return this;
    }
    
    public String getImageString() {
        return imageString;
    }
    
    public ParseEventUtils setImageString(String imageString) {
        this.imageString = imageString;
        return this;
    }
    
    public Boolean getHasSleep() {
        return hasSleep;
    }
    
    public ParseEventUtils setHasSleep(Boolean hasSleep) {
        this.hasSleep = hasSleep;
        return this;
    }
    
    public Boolean getHasCocktail() {
        return hasCocktail;
    }
    
    public ParseEventUtils setHasCocktail(Boolean hasCocktail) {
        this.hasCocktail = hasCocktail;
        return this;
    }
    
    public String getFullAddress() {
        return fullAddress;
    }
    
    public ParseEventUtils setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
        return this;
    }
}
