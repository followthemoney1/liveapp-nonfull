package pc.dd.liveapp.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.bumptech.glide.Glide;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pc.dd.liveapp.R;
import pc.dd.liveapp.ui.activities.LiveMainActivity;
import pc.dd.liveapp.ui.activities.PaymentDialogActivity;
import pc.dd.liveapp.ui.widget.MyDatePicker;
import pc.dd.liveapp.utils.Constants;
import pc.dd.liveapp.utils.UiHelper;
import pc.dd.liveapp.utils.image.ImagePathHelper;
import pc.dd.liveapp.data.parse.ParseCallbacks;
import pc.dd.liveapp.data.parse.ParseEventUtils;
import pc.dd.liveapp.data.interfaces.ViewInterface;

import static android.app.Activity.RESULT_OK;
import static pc.dd.liveapp.utils.Constants.PAYMENT_CREATE_EVENT_RESULT;
import static pc.dd.liveapp.utils.Constants.PICK_IMAGE;
import static pc.dd.liveapp.utils.Constants.PRIVATE;
import static pc.dd.liveapp.utils.Constants.SEMI_PRIVATE;

/**
 * Created by leaditteam on 08.09.17.
 */

public class CreateEventFragment extends Fragment {
    @BindView(R.id.nameEventCreation)
    EditText partyName;
    @BindView(R.id.aboutEditTextRegistration)
    EditText about;
    @BindView(R.id.addressEditTextFull)
    EditText addressFull;
    @BindView(R.id.addressEditTextNotFull)
    EditText addressNotFull;
    @BindView(R.id.placesEditText)
    EditText places;
    @BindView(R.id.priceEditText)
    EditText price;
    @BindView(R.id.imageCreateEvent)
    ImageView image;
    @BindView(R.id.hasCocktail)
    ImageView hasCocktail;
    @BindView(R.id.hasSwim)
    ImageView hasSwim;
    @BindView(R.id.hasFood)
    ImageView hasFood;
    @BindView(R.id.hasSleep)
    ImageView hasSleep;
    @BindView(R.id.hasDj)
    ImageView hasDj;
    @BindView(R.id.hasMusic)
    ImageView hasMusic;
    @BindView(R.id.imageArrowBack)
    ImageView arrowBackBtn;
    @BindView(R.id.cardViewSaveEvent)
    CardView saveEventBtn;
    @BindView(R.id.myDatePicker)
    MyDatePicker datePicker;
    @BindView(R.id.rootContentLayout)
    LinearLayout rootContentLayout;
    
    View root;
    
    Boolean hasCocktailBoolean = false;
    Boolean hasSwimBoolean = false;
    Boolean hasFoodBoolean = false;
    Boolean hasSleepBoolean = false;
    Boolean hasDjBoolean = false;
    Boolean hasMusicBoolean = false;
    
    Boolean canWeCreateEvent = false;
    
    String partyType = SEMI_PRIVATE;
    String parseEventObjectId = null;
    String imagePath;
    
    Date date;
    
    ParseEventUtils parseEventUtils;
    
    ParseGeoPoint parseGeoPoint;
    
    public static CreateEventFragment newInstance(String parseEventObjectId) {
        Bundle args = new Bundle();
        args.putString(Constants.EVENT_OBJECT_ID_STRING, parseEventObjectId);
        CreateEventFragment fragment = new CreateEventFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root = view;
        ButterKnife.bind(this, view);
        getExtra();
        init();
    }
    
    private void getExtra() {
        parseEventObjectId = getArguments().getString(Constants.EVENT_OBJECT_ID_STRING);
    }
    
    private void init() {
        datePicker.setDateChangedListener(myDatePickerChangeListener);
        
        if (parseEventObjectId == null) {
            saveEventBtn.setOnClickListener(cardViewCreateEventOnClick);
        } else {
            arrowBackBtn.setVisibility(View.VISIBLE);
            saveEventBtn.setOnClickListener(cardViewSaveEventOnClick);
            getParseObjectEvent();
        }
    }
    
    public void setFragmentDataEmpty() {
        hasCocktailBoolean = false;
        hasSwimBoolean = false;
        hasFoodBoolean = false;
        hasSleepBoolean = false;
        hasDjBoolean = false;
        hasMusicBoolean = false;
        partyName.setText("");
        about.setText("");
        addressFull.setText("");
        addressNotFull.setText("");
        places.setText("");
        price.setText("");
        imagePath = "";
        //TODO:just invalidate && postinvalidate views not work, why?
        
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this)
                .setReorderingAllowed(false)
                .setAllowOptimization(false)
                .commit();
        
    }
    
    private void getParseObjectEvent() {
        ParseCallbacks.findEventListFromParseObjectsId(parseEventObjectId, new ParseCallbacks.ParseFindCallback() {
            @Override
            public void done(List<ParseObject> objects) {
                if (objects.size() > 0) {
                    parseEventUtils = ParseEventUtils.getOneParseEventByObject(objects.get(0));
                    setDataToViewsFromParseObject();
                }
            }
        });
        
    }
    
    private void setDataToViewsFromParseObject() {
        if (parseEventUtils != null) {
            hasCocktailBoolean = clickButton(!parseEventUtils.getHasCocktail(), hasCocktail);
            hasSwimBoolean = clickButton(!parseEventUtils.getHasSwim(), hasSwim);
            hasFoodBoolean = clickButton(!parseEventUtils.getHasFood(), hasFood);
            hasSleepBoolean = clickButton(!parseEventUtils.getHasSleep(), hasSleep);
            hasDjBoolean = clickButton(!parseEventUtils.getHasDj(), hasDj);
            hasMusicBoolean = clickButton(!parseEventUtils.getHasMusic(), hasMusic);
            
            partyName.setText(parseEventUtils.getTitle());
            about.setText(parseEventUtils.getAbout());
            addressFull.setText(parseEventUtils.getAddress());
            addressNotFull.setText(parseEventUtils.getFullAddress());
            places.setText(String.valueOf(parseEventUtils.getPlaces()));
            price.setText(String.valueOf(parseEventUtils.getPrice()));
            imagePath = parseEventUtils.getImageString();
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parseEventUtils.getDate());
            datePicker.setCal(calendar);
            
            Glide.with(getContext())
                    .load(imagePath)
                    .into(image);
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PAYMENT_CREATE_EVENT_RESULT:
                
                if (resultCode == RESULT_OK)
                    canWeCreateEvent = true;
                
                saveEventBtn.callOnClick();
                break;
            
            case PICK_IMAGE:
                
                if (data != null && data.getData() != null) {
                    imagePath = ImagePathHelper.getPath(getActivity(), data.getData());
                    
                    try {
                        Bitmap bitmapFile = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        image.setImageBitmap(bitmapFile);
                    } catch (Exception e) {
                        Log.e(this.getTag(), e.getMessage());
                    }
                    
                } else {
                    
                    try {
                        Bitmap bitmapFile = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(imagePath));
                        Bitmap newOrientationBitmap = UiHelper.resultPhotoBitmapRotate(Uri.parse(imagePath).getPath(), bitmapFile);
                        image.setImageBitmap(newOrientationBitmap);
                        imagePath = Uri.parse(imagePath).getPath();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    
                }
                break;
            
            default:
                break;
        }
        
    }
    
    ViewInterface eventCreateCallback = new ViewInterface() {
        @Override
        public void doneCallback() {
            if (parseEventObjectId == null) {
                setFragmentDataEmpty();
                UiHelper.showToast(getContext(), getString(R.string.toast_event_create_success));
                
                if (getContext() instanceof LiveMainActivity)
                    ((LiveMainActivity) getContext()).setCurrentPositionOfPageAdapter(0);
                
            } else {
                getFragmentManager().popBackStack();
                getFragmentManager().executePendingTransactions();
            }
        }
    };
    
    MyDatePicker.DateWatcher myDatePickerChangeListener = new MyDatePicker.DateWatcher() {
        @Override
        public void onDateChanged(Calendar c) {
            date = c.getTime();
        }
    };
    
    View.OnClickListener cardViewCreateEventOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!checkFieldIsNotEmpty() || ParseUser.getCurrentUser() == null) return;
            
            if ((String) ParseUser.getCurrentUser().get(Constants.STRIPE_TEST_ACCOUNT_ID_STRING) == null) {
                UiHelper
                        .openActivityDialog(
                                null,
                                null,
                                null,
                                null,
                                0,
                                PAYMENT_CREATE_EVENT_RESULT,
                                getContext(),
                                PaymentDialogActivity.class);
                
            } else {
                canWeCreateEvent = true;
            }
            
            if (!canWeCreateEvent) return;
            
            parseGeoPoint =
                    UiHelper.getParseGeopointFromAddress(addressNotFull.getText().toString() + " " +
                                    addressFull.getText().toString(),
                            getContext());
            
            new ParseEventUtils()
                    .setHasCocktail(hasCocktailBoolean)
                    .setHasDj(hasDjBoolean)
                    .setHasFood(hasFoodBoolean)
                    .setHasMusic(hasMusicBoolean)
                    .setHasSleep(hasSleepBoolean)
                    .setHasSwim(hasSwimBoolean)
                    .setAbout(about.getText().toString())
                    .setAddress(addressNotFull.getText().toString())
                    .setFullAddress(addressFull.getText().toString())
                    .setAttendersCount(0)
                    .setDate(date)
                    .setPrice(Integer.parseInt(price.getText().toString()))
                    .setTitle(partyName.getText().toString())
                    .setUser(ParseUser.getCurrentUser())
                    .setImageString(imagePath)
                    .setPlaces(Integer.parseInt(places.getText().toString()))
                    .setType(partyType)
                    .setLocation(parseGeoPoint)
                    .createEvent(eventCreateCallback);
        }
    };
    
    View.OnClickListener cardViewSaveEventOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!checkFieldIsNotEmpty() || ParseUser.getCurrentUser() == null) return;
            
            new ParseEventUtils()
                    .setHasCocktail(hasCocktailBoolean)
                    .setHasDj(hasDjBoolean)
                    .setHasFood(hasFoodBoolean)
                    .setHasMusic(hasMusicBoolean)
                    .setHasSleep(hasSleepBoolean)
                    .setHasSwim(hasSwimBoolean)
                    .setAbout(about.getText().toString())
                    .setAddress(addressFull.getText().toString())
                    .setDate(date)
                    .setFullAddress(addressNotFull.getText().toString())
                    .setPrice(Integer.parseInt(price.getText().toString()))
                    .setTitle(partyName.getText().toString())
                    .setUser(ParseUser.getCurrentUser())
                    .setImageString(imagePath)
                    .setPlaces(Integer.parseInt(places.getText().toString()))
                    .setType(partyType)
                    .update(parseEventObjectId, eventCreateCallback);
            
        }
    };
    
    @OnClick(R.id.imageArrowBack)
    void imageArrowBackOnClick() {
        getFragmentManager().popBackStack();
    }
    
    @OnClick({R.id.privateTypeRB, R.id.publicTypeRB})
    void radioGroupTypeOnChecked(RadioButton radioButton) {
        boolean checked = radioButton.isChecked();
        
        switch (radioButton.getId()) {
            case R.id.privateTypeRB:
                if (checked) {
                    partyType = PRIVATE;
                }
                break;
            case R.id.publicTypeRB:
                if (checked) {
                    partyType = SEMI_PRIVATE;
                }
                break;
        }
    }
    
    @OnClick(R.id.imageCreateEvent)
    void imageCreatePartyOnClick() {
        Uri temp = UiHelper.openImagePickIntent(getActivity(), PICK_IMAGE);
        if (temp != null) imagePath = temp.toString();
    }
    
    @OnClick(R.id.hasCocktail)
    void hasCocktailOnClick(View view) {
        hasCocktailBoolean = clickButton(hasCocktailBoolean, view);
    }
    
    @OnClick(R.id.hasSwim)
    void hasSwimOnClick(View view) {
        hasSwimBoolean = clickButton(hasSwimBoolean, view);
    }
    
    @OnClick(R.id.hasFood)
    void hasFoodOnClick(View view) {
        hasFoodBoolean = clickButton(hasFoodBoolean, view);
    }
    
    @OnClick(R.id.hasSleep)
    void hasSleepOnClick(View view) {
        hasSleepBoolean = clickButton(hasSleepBoolean, view);
    }
    
    @OnClick(R.id.hasDj)
    void hasDjOnClick(View view) {
        hasDjBoolean = clickButton(hasDjBoolean, view);
    }
    
    @OnClick(R.id.hasMusic)
    void hasMusicOnClick(View view) {
        hasMusicBoolean = clickButton(hasMusicBoolean, view);
    }
    
    private Boolean checkFieldIsNotEmpty() {
        String fName = partyName.getText().toString().trim();
        String fAdress = addressFull.getText().toString().trim();
        String sAdress = addressNotFull.getText().toString().trim();
        String _about = about.getText().toString().trim();
        String _places = places.getText().toString().trim();
        String _price = price.getText().toString().trim();
        
        if (fName.isEmpty()) {
            UiHelper.showToast(getContext(), getString(R.string.toast_you_must_fill_party_name));
            return false;
        } else if (fAdress.isEmpty() || sAdress.isEmpty()) {
            UiHelper.showToast(getContext(), getString(R.string.toast_you_must_fill_adress));
            return false;
        } else if (_about.isEmpty()) {
            UiHelper.showToast(getContext(), getString(R.string.toast_you_must_fill_about));
            return false;
        } else if ((imagePath == null || imagePath.isEmpty())) {
            UiHelper.showToast(getContext(), getString(R.string.toast_must_upload_image));
            return false;
        } else if (_places.isEmpty() || Integer.parseInt(_places) == 0) {
            UiHelper.showToast(getContext(), getString(R.string.toast_must_fill_places));
            return false;
        } else if (_price.isEmpty() || Integer.parseInt(_price) == 0) {
            UiHelper.showToast(getContext(), getString(R.string.toast_must_fill_price));
            return false;
        } else if (date == null) {
            UiHelper.showToast(getContext(), getString(R.string.toast_date_is_wrong));
            return false;
        } else if (UiHelper.getParseGeopointFromAddress(addressFull.getText().toString().trim(), getContext()) == null) {
            UiHelper.showToast(getContext(), getString(R.string.toast_address_is_wrong));
            return false;
        } else if (UiHelper.getParseGeopointFromAddress(addressNotFull.getText().toString().trim(), getContext()) == null) {
            UiHelper.showToast(getContext(), getString(R.string.toast_city_is_wrong));
            return false;
        } else {
            return true;
        }
    }
    
    private Boolean clickButton(Boolean clicked, View view) {
        if (!clicked) {
            view.setAlpha(1);
            clicked = true;
        } else {
            view.setAlpha((float) 0.3);
            clicked = false;
        }
        return clicked;
    }
}
