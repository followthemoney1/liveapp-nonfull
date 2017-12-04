package pc.dd.liveapp.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pc.dd.liveapp.R;
import pc.dd.liveapp.utils.Constants;
import pc.dd.liveapp.data.parse.ParseEventUtils;
import pc.dd.liveapp.data.parse.ParseUserUtils;
import pc.dd.liveapp.data.interfaces.PaymentFragmentBackCallback;
import pc.dd.liveapp.utils.UiHelper;

/**
 * Created by leaditteam on 19.10.17.
 */

public class ChangeUserStatusFragment extends Fragment {
    @BindView(R.id.userImageProfile)
    ImageView userImage;
    @BindView(R.id.nameUser)
    TextView userName;
    @BindView(R.id.userAge)
    TextView userAge;
    @BindView(R.id.userLocation)
    TextView userLocation;
    
    String parseUserId;
    String parseEventId;
    
    PaymentFragmentBackCallback paymentBackCallback;
    
    ParseUserUtils parseUserUtils;
    ParseEventUtils parseEventUtils;
    
    public static ChangeUserStatusFragment newInstance(String userId, String eventId) {
        Bundle args = new Bundle();
        args.putString(Constants.USER_OBJECT_ID_STRING, userId);
        args.putString(Constants.EVENT_OBJECT_ID_STRING, eventId);
        ChangeUserStatusFragment fragment = new ChangeUserStatusFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_set_user_status, container, false);
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        getExtras();
        getObjectsById();
    }
    
    private void getExtras() {
        parseUserId = getArguments().getString(Constants.USER_OBJECT_ID_STRING);
        parseEventId = getArguments().getString(Constants.EVENT_OBJECT_ID_STRING);
    }
    
    private void updateViews() {
        if (parseUserUtils != null) {
            Glide.with(getActivity())
                    .load(parseUserUtils.getPhotoUrl())
                    .into(userImage);
            
            userName.setText(parseUserUtils.getFullName());
            userAge.setText(String.valueOf(UiHelper.getUserAge(parseUserUtils)));
            userLocation.setText(parseUserUtils.getAddress());
        }
    }
    
    private void getObjectsById() {
        ParseQuery<ParseObject> parseEventQuery = ParseQuery.getQuery(Constants.EVENT_STRING);
        parseEventQuery.whereEqualTo(Constants.OBJECT_ID_STRING, parseEventId);
        parseEventQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        parseEventUtils = ParseEventUtils.getOneParseEventByObject(objects.get(0));
                    }
                }
            }
        });
        
        ParseQuery<ParseUser> parseUserQuery = ParseUser.getQuery();
        parseUserQuery.whereEqualTo(Constants.OBJECT_ID_STRING, parseUserId);
        parseUserQuery.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        parseUserUtils = ParseUserUtils.getParseUserUtilFromParseUser((ParseUser) objects.get(0));
                        updateViews();
                    }
                }
            }
        });
        
    }
    
    private void setStatusEvent(final String statusEvent) {
        if (parseEventUtils != null && parseUserUtils != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.EVENT_REQUEST_STRING);
            query.whereEqualTo(Constants.EVENT_STRING_TO_EVENT_REQUEST, parseEventUtils.getEventObject());
            query.whereEqualTo(Constants.USER, parseUserUtils.getUser());
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            objects.get(0).put(Constants.STATUS_STRING, statusEvent);
                            objects.get(0).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (statusEvent.equals(Constants.STATUS_ACCEPT_STRING)) {
                                        parseEventUtils.setAttendersCount(parseEventUtils.getAttendersCount() + 1);
                                        parseEventUtils.update(parseEventUtils.getObjectId(), null);
                                    }
                                    paymentBackCallback.onBackPressCallback();
                                }
                            });
                        }
                    }
                }
            });
        }
    }
    
    @OnClick(R.id.declineUser)
    void declineUserOnClick() {
        setStatusEvent(Constants.STATUS_REJECTED_STRING);
    }
    
    @OnClick(R.id.acceptUser)
    void acceptUserOnLick() {
        setStatusEvent(Constants.STATUS_ACCEPT_STRING);
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getInterface(context);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getInterface(activity);
    }
    
    private void getInterface(Context context) {
        if (context instanceof Activity)
            paymentBackCallback = (PaymentFragmentBackCallback) context;
    }
}
