package pc.dd.liveapp.data.parse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pc.dd.liveapp.utils.FileUtils;
import pc.dd.liveapp.utils.Constants;
import pc.dd.liveapp.data.interfaces.FacebookLoginUserCallback;

/**
 * Created by leaditteam on 07.09.17.
 */

public class ParseUserUtils implements Serializable {
    
    transient ParseUser user = new ParseUser();
    transient ParseFile photoFile;
    transient Context from;
    Class destination;
    
    String email;
    String password;
    String username;
    String lastName;
    String gender = "female";
    Date birthDate = Calendar.getInstance().getTime();
    String about;
    String address;
    String fullName;
    String firstName;
    String photoUrl;
    String objectId;
    String stripeSourceId;
    String stripeCustomerId;
    String facebookId;
    
    String statusEvent;
    
    public ParseUserUtils(Context from, Class destination) {
        this.from = from;
        this.destination = destination;
    }
    
    public void createUser() {
        byte[] data = FileUtils.getFilesToByte(this.photoUrl);
        final ParseFile image = new ParseFile(String.valueOf(SystemClock.currentThreadTimeMillis()) + ".jpeg", data);
        
        try {
            user.setEmail(this.email);
        }catch (Exception e){}
        
        user.setUsername(this.user.getUsername());
        user.put(Constants.LAST_NAME_STRING, this.lastName);
        user.put(Constants.GENDER_STRING, this.gender);
        user.put(Constants.BIRD_DATE_STRING, this.birthDate);
        user.put(Constants.ABOUT_STRING, this.about);
        user.put(Constants.ADDRESS_STRING, this.address);
        user.put(Constants.FULL_NAME_STRING, this.fullName);
        user.put(Constants.FACEBOOK_ID_STRING,this.facebookId);
        user.put(Constants.FIRST_NAME_STRING, this.firstName);
        ParseFacebookUtils.logInInBackground(AccessToken.getCurrentAccessToken(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    user.put(Constants.IMAGE_STRING, image);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            if (e == null){
                                Toast.makeText(from, "Success", Toast.LENGTH_LONG).show();
                                from.startActivity(new Intent(from, destination));
                            } else Log.e("ERROR SAVE FB DATA ---", e.getCode() + e.getMessage());
                        }});
                } else {
                    Log.e("ERROR SAVE FB DATA2 ---", e.getCode() + e.getMessage());
                    Toast.makeText(from, "Error"+ e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    
    public static void getUserById(String userId, final ParseCallbacks.LoadCallback callback){
        ParseQuery parseQuery = ParseUser.getQuery();
        parseQuery.whereEqualTo(Constants.OBJECT_ID_STRING,userId);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects!=null&&objects.size()>0)
                    callback.onLoad(getParseUserUtilFromParseUser((ParseUser) objects.get(0)));
            }});
    }
    
    public static ParseUserUtils getCurrentUser() {
        if (ParseUser.getCurrentUser() != null) {
            try {
                ParseUser.getCurrentUser().fetch();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
            return returnParseUserUtils(ParseUser.getCurrentUser());
        } else {
            return null;
        }
    }
    
    public static ParseUserUtils getParseUserUtilFromParseUser(ParseUser tempUser) {
        try {
            tempUser.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return returnParseUserUtils(tempUser);
    }
    
    private static ParseUserUtils returnParseUserUtils(ParseUser tempUser) {
        ParseFile applicantResume = (ParseFile) tempUser.get(Constants.IMAGE_STRING);
        ParseUserUtils tempUtils = new ParseUserUtils(null, null)
                .setEmail(tempUser.getEmail())
                .setUsername(tempUser.getUsername())
                .setLastName((String) tempUser.get(Constants.LAST_NAME_STRING))
                .setGender((String) tempUser.get(Constants.GENDER_STRING))
                .setBirthDate((Date) tempUser.get(Constants.BIRD_DATE_STRING))
                .setAbout((String) tempUser.get(Constants.ABOUT_STRING))
                .setAddress((String) tempUser.get(Constants.ADDRESS_STRING))
                .setFullName((String) tempUser.get(Constants.FULL_NAME_STRING))
                .setFirstName((String) tempUser.get(Constants.FIRST_NAME_STRING))
                .setStripeCustomerId((String) tempUser.get(Constants.STRIPE_CUSTOMER_ID_STRING))
                .setStripeSourceId((String) tempUser.get(Constants.STRIPE_SOURCE_ID_STRING))
                .setFacebookId((String) tempUser.get(Constants.FACEBOOK_ID_STRING))
                .setUser(tempUser)
                .setObjectId(tempUser.getObjectId())
                .setPhotoUrl(applicantResume.getUrl());
        
        return tempUtils;
    }
    
    public void updateUserInfo(final Activity context, final FragmentManager fragmentManager) {
        
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    parseUser.put(Constants.LAST_NAME_STRING, getLastName());
                    parseUser.put(Constants.GENDER_STRING, getGender());
                    parseUser.put(Constants.BIRD_DATE_STRING, getBirthDate());
                    parseUser.put(Constants.ABOUT_STRING, getAbout());
                    parseUser.put(Constants.ADDRESS_STRING, getAddress());
                    parseUser.put(Constants.FULL_NAME_STRING, getFullName());
                    parseUser.put(Constants.FIRST_NAME_STRING, getFirstName());
                    
                    byte[] data = FileUtils.getFilesToByte(getPhotoUrl());
                    if (data.length > 0) {
                        ParseFile image = new ParseFile(String.valueOf(SystemClock.currentThreadTimeMillis()) + ".jpeg", data);
                        parseUser.put(Constants.IMAGE_STRING, image);
                    }
                    
                    parseUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(context, "Information updated", Toast.LENGTH_SHORT).show();
                                fragmentManager.popBackStack();
                            }
                        }
                    });
                }
            }
        });
    }
    
    
    public void callLoginInterface(Boolean login,ParseUserUtils parseUserUtils){
        FacebookLoginUserCallback loginUserInterface = (FacebookLoginUserCallback) from;
        loginUserInterface.facebookLoginCallback(login, parseUserUtils);
    }
    
    public void checkLogin(final ParseUserUtils parseUserUtils) {
        ParseUser.logInInBackground(this.username, this.password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                FacebookLoginUserCallback loginUserInterface = (FacebookLoginUserCallback) from;
                boolean login = (user != null);
                loginUserInterface.facebookLoginCallback(login, parseUserUtils);
            }
        });
    }
    
    public String getFacebookId() {
        return facebookId;
    }
    
    public ParseUserUtils setFacebookId(String facebookId) {
        this.facebookId = facebookId;
        return this;
    }
    
    public String getStatusEvent() {
        return statusEvent;
    }
    
    public ParseUserUtils setStatusEvent(String statusEvent) {
        this.statusEvent = statusEvent;
        return this;
    }
    
    public String getStripeSourceId() {
        return stripeSourceId;
    }
    
    public ParseUserUtils setStripeSourceId(String stripeSourceId) {
        this.stripeSourceId = stripeSourceId;
        return this;
    }
    
    public String getStripeCustomerId() {
        return stripeCustomerId;
    }
    
    public ParseUserUtils setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
        return this;
    }
    
    public String getObjectId() {
        return objectId;
    }
    
    public ParseUserUtils setObjectId(String objectId) {
        this.objectId = objectId;
        return this;
    }
    
    public ParseFile getPhotoFile() {
        return photoFile;
    }
    
    public ParseUserUtils setPhotoFile(ParseFile photoFile) {
        this.photoFile = photoFile;
        return this;
    }
    
    public ParseUserUtils setUser(ParseUser user) {
        this.user = user;
        return this;
    }
    
    public ParseUserUtils setFrom(Context from) {
        this.from = from;
        return this;
    }
    
    public ParseUserUtils setDestination(Class destination) {
        this.destination = destination;
        return this;
    }
    
    public ParseUserUtils setEmail(String email) {
        this.email = email;
        return this;
    }
    
    public ParseUserUtils setPassword(String password) {
        this.password = password;
        return this;
    }
    
    public ParseUserUtils setUsername(String username) {
        this.username = username;
        return this;
    }
    
    public ParseUserUtils setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    
    public ParseUserUtils setGender(String gender) {
        this.gender = gender;
        return this;
    }
    
    public ParseUserUtils setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
        return this;
    }
    
    public ParseUserUtils setAbout(String about) {
        this.about = about;
        return this;
    }
    
    public ParseUserUtils setAddress(String address) {
        this.address = address;
        return this;
    }
    
    public ParseUserUtils setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }
    
    public ParseUserUtils setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    
    public ParseUserUtils setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        return this;
    }
    
    public ParseUser getUser() {
        return user;
    }
    
    public Context getFrom() {
        return from;
    }
    
    public Class getDestination() {
        return destination;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getGender() {
        return gender;
    }
    
    public Date getBirthDate() {
        return birthDate;
    }
    
    public String getAbout() {
        return about;
    }
    
    public String getAddress() {
        return address;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getPhotoUrl() {
        return photoUrl;
    }
}
