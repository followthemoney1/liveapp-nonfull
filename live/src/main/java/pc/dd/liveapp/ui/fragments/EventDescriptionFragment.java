package pc.dd.liveapp.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pc.dd.liveapp.R;
import pc.dd.liveapp.data.parse.ParseUserUtils;
import pc.dd.liveapp.ui.activities.PaymentDialogActivity;
import pc.dd.liveapp.utils.UiHelper;
import pc.dd.liveapp.utils.pdf.PdfCreator;
import pc.dd.liveapp.utils.DateUtil;
import pc.dd.liveapp.ui.other.ProgressBarLayout;
import pc.dd.liveapp.utils.Constants;
import pc.dd.liveapp.data.parse.ParseCallbacks;
import pc.dd.liveapp.data.parse.ParseEventRequestUtils;
import pc.dd.liveapp.data.parse.ParseEventUtils;

import static pc.dd.liveapp.utils.ButterKnifeUtils.setTextToEditText;

/**
 * Created by leaditteam on 28.09.17.
 */

public class EventDescriptionFragment extends Fragment {
    @BindView(R.id.peopleIcons)
    LinearLayout peopleIcons;
    @BindView(R.id.imageEvent)
    ImageView image;
    @BindView(R.id.changeEventInformation)
    ImageView changeEvent;
    @BindView(R.id.eventName)
    TextView name;
    @BindView(R.id.eventRegion)
    TextView address;
    @BindView(R.id.eventPrice)
    TextView price;
    @BindView(R.id.eventDate)
    TextView date;
    @BindView(R.id.eventAbout)
    TextView about;
    @BindView(R.id.cardEventText)
    TextView cardEventBtn;
    @BindView(R.id.howPeopleWeHave)
    TextView howMuchPeopleWeHave;
    @BindView(R.id.howMuchPeopleMustBe)
    TextView howMuchPeopleMustBe;
    @BindView(R.id.hasCocktail)
    RelativeLayout hasCocktail;
    @BindView(R.id.hasSwim)
    RelativeLayout hasSwim;
    @BindView(R.id.hasFood)
    RelativeLayout hasFood;
    @BindView(R.id.hasSleep)
    RelativeLayout hasSleep;
    @BindView(R.id.hasDj)
    RelativeLayout hasDj;
    @BindView(R.id.hasMusic)
    RelativeLayout hasMusic;
    @BindView(R.id.pdfIcon)
    ImageView pdfIcon;
    
    ParseEventUtils parseEventUtils;
    ParseEventRequestUtils parseEventRequest;
    
    Boolean ifAlreadyPending = false;
    Boolean needRefresh = false;
    
    View root;
    
    public static EventDescriptionFragment newInstance(ParseEventRequestUtils parseEventRequest) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.PARSE_EXTRA_STRING, parseEventRequest);
        EventDescriptionFragment fragment = new EventDescriptionFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_full_event, container, false);
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root = view;
        ProgressBarLayout.showProgressBar();
        
        ButterKnife.bind(this, root);
        getChildFragmentManager().addOnBackStackChangedListener(fragmentManagerBackStackChangeListener);
        getExtras();
        getParseObjectAndSetViewsData();
        
        ProgressBarLayout.dismissProgressBar();
    }
    
    private void getExtras() {
        parseEventRequest = (ParseEventRequestUtils) getArguments().getSerializable(Constants.PARSE_EXTRA_STRING);
    }
    
    private void getParseObjectAndSetViewsData() {
        if (parseEventRequest != null) {
            parseEventUtils = parseEventRequest.getParseEventUtils();
            
            setTopViewLayoutParams();
            setDataToViews();
            setContentDataFromStatus();
        }
        
    }
    
    private void setTopViewLayoutParams() {
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) peopleIcons.getLayoutParams();
        
        if (parseEventUtils.getUser() != null && parseEventUtils.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            changeEvent.setVisibility(View.VISIBLE);
            cardEventBtn.setText(R.string.annuler);
        } else {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        
        peopleIcons.setLayoutParams(layoutParams);
        if (parseEventRequest.getParseEventUtils() != null && parseEventRequest.getParseEventUtils().getUser() != null)
            if (parseEventRequest.getParseEventUtils().getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId()))
                pdfIcon.setVisibility(View.VISIBLE);
    }
    
    private void setContentDataFromStatus() {
        if (parseEventRequest.getStatus() != null) {
            if (parseEventRequest.getStatus()
                    .equals(Constants.STATUS_PENDING_STRING) ||
                    parseEventRequest.getStatus()
                            .equals(Constants.STATUS_ACCEPT_STRING)) {
                
                ifAlreadyPending = true;
                cardEventBtn.setText(R.string.annuler);
                address.setText(address.getText() + ", " + parseEventUtils.getFullAddress());
                
            } else if (parseEventRequest.getStatus()
                    .equals(Constants.STATUS_REJECTED_STRING)) {
                cardEventBtn.setVisibility(View.GONE);
                
            }
        } else {
            ifAlreadyPending = false;
        }
    }
    
    private void setDataToViews() {
        Glide.with(this).load(parseEventUtils.getImageString()).into(image);
        
        setVisabilityForCathegory();
        
        ButterKnife.apply(name, setTextToEditText, parseEventUtils.getTitle());
        ButterKnife.apply(address, setTextToEditText, parseEventUtils.getAddress());
        ButterKnife.apply(price, setTextToEditText, parseEventUtils.getPrice() + " €");
        ButterKnife.apply(about, setTextToEditText, parseEventUtils.getAbout());
        ButterKnife.apply(howMuchPeopleWeHave, setTextToEditText, String.valueOf(parseEventUtils.getAttendersCount()));
        ButterKnife.apply(howMuchPeopleMustBe, setTextToEditText, "/" + String.valueOf(parseEventUtils.getPlaces()));
    }
    
    @Override
    public void onResume() {
        if (needRefresh)
            updateData();
        else needRefresh = true;
        super.onResume();
    }
    
    private void updateData() {
        ProgressBarLayout.showProgressBar();
        if (parseEventRequest != null)
            parseEventRequest.update(new ParseEventRequestUtils.ParseEventRequestCallback() {
                @Override
                public void done(ParseEventRequestUtils request) {
                    parseEventRequest = request;
                    getParseObjectAndSetViewsData();
                    ProgressBarLayout.dismissProgressBar();
                }
            });
    }
    
    @OnClick(R.id.cardViewEventJoin)
    void cardViewEventJoinOnClick() {
        if (!ifAlreadyPending) {
            openPaymentDialogOrSendEventRequest();
        } else {
            deleteStatusPending();
        }
    }
    
    private void openPaymentDialogOrSendEventRequest() {
        ParseUserUtils parseUserUtils = ParseUserUtils.getParseUserUtilFromParseUser(ParseUser.getCurrentUser());
        if (parseUserUtils != null &&
                (parseUserUtils.getStripeCustomerId() == null || parseUserUtils.getStripeSourceId() == null || parseUserUtils.getStripeCustomerId().trim().equals("") || parseUserUtils.getStripeSourceId().trim().equals("")))
            UiHelper.openActivityDialog(null,
                    null,
                    null,
                    parseEventRequest,
                    0,
                    Constants.PAYMENT_CREATE_EVENT_RESULT,
                    getContext(),
                    PaymentDialogActivity.class);
        else {
            ParseEventRequestUtils.createEventRequest(ParseUser.getCurrentUser(), parseEventUtils.getEventObject(), Constants.STATUS_PENDING_STRING, new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    openFacebookFriendListDialog();
                }
            });
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PAYMENT_CREATE_EVENT_RESULT)
            if (resultCode == Activity.RESULT_OK)
                openFacebookFriendListDialog();
    }
    
    private void openFacebookFriendListDialog() {
        FacebookDataHelper.checkIfFacebookFriendsExists(new FacebookDataHelper.FacebookFriendsCallback() {
            @Override
            public void done(List<ParseUserUtils> list) {
                UiHelper.openActivityDialog(null,
                        parseEventRequest.getParseEventUtils().getObjectId(),
                        null,
                        null,
                        0,
                        0,
                        getContext(),
                        FacebookFriendsDialogActivity.class);
            }
            
            @Override
            public void cancel() {
            
            }
        });
    }
    
    private void deleteStatusPending() {
        ParseCallbacks.deleteEventRequestObjectIfStatusPending(parseEventRequest, getContext(), new ParseCallbacks.ParseCallback() {
            @Override
            public void done() {
                ifAlreadyPending = false;
                cardEventBtn.setText(R.string.rejoindre);
                address.setText(parseEventUtils.getAddress());
            }
        });
    }
    
    android.support.v4.app.FragmentManager.OnBackStackChangedListener fragmentManagerBackStackChangeListener = new android.support.v4.app.FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            updateData();
        }
    };
    
    @OnClick(R.id.peopleIcons)
    void peopleIconsOnClick() {
        PartyUsersEventFragment partyUsersEventFragment = PartyUsersEventFragment.newInstance(parseEventUtils, true);
        
        getChildFragmentManager()
                .beginTransaction()
                .replace(root.getId(),
                        partyUsersEventFragment,
                        Constants.SHOW_PEOPLE_EVENT_FRAGMENT_TAG)
                .addToBackStack(Constants.SHOW_PEOPLE_EVENT_FRAGMENT_TAG).commit();
        
        getChildFragmentManager().executePendingTransactions();
    }
    
    @OnClick(R.id.changeEventInformation)
    void changeEventInformationOnClick() {
        CreateEventFragment createEventFragment = CreateEventFragment.newInstance(parseEventUtils.getObjectId());
        
        getChildFragmentManager()
                .beginTransaction()
                .replace(root.getId(),
                        createEventFragment,
                        Constants.CHANGE_EVENT_INFORMATION_FRAGMENT_TAG)
                .addToBackStack(Constants.CHANGE_EVENT_INFORMATION_FRAGMENT_TAG).commit();
        
        getChildFragmentManager().executePendingTransactions();
    }
    
    @OnClick(R.id.backIcon)
    void backIconOnClick() {
        getFragmentManager().popBackStack();
    }
    
    @OnClick(R.id.pdfIcon)
    void pdfIconOnClick() {
        new PdfCreator(getContext());
    }
    
    private void setVisabilityForCathegory() {
        if (parseEventUtils.getHasCocktail())
            hasCocktail.setVisibility(View.VISIBLE);
        else hasCocktail.setVisibility(View.GONE);
        if (parseEventUtils.getHasDj())
            hasDj.setVisibility(View.VISIBLE);
        else hasDj.setVisibility(View.GONE);
        if (parseEventUtils.getHasFood())
            hasFood.setVisibility(View.VISIBLE);
        else hasFood.setVisibility(View.GONE);
        if (parseEventUtils.getHasMusic())
            hasMusic.setVisibility(View.VISIBLE);
        else hasMusic.setVisibility(View.GONE);
        if (parseEventUtils.getHasSleep())
            hasSleep.setVisibility(View.VISIBLE);
        else hasSleep.setVisibility(View.GONE);
        if (parseEventUtils.getHasSwim())
            hasSwim.setVisibility(View.VISIBLE);
        else hasSwim.setVisibility(View.GONE);
    }
}
