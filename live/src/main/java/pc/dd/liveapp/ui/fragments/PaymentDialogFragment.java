package pc.dd.liveapp.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.stripe.android.model.Card;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pc.dd.liveapp.R;
import pc.dd.liveapp.data.parse.ParseEventRequestUtils;
import pc.dd.liveapp.ui.activities.PaymentDialogActivity;
import pc.dd.liveapp.ui.other.ProgressBarLayout;
import pc.dd.liveapp.utils.Constants;
import pc.dd.liveapp.utils.UiHelper;
import pc.dd.liveapp.data.parse.ParseCallbacks;
import pc.dd.liveapp.data.parse.ParseEventUtils;
import pc.dd.liveapp.data.parse.ParseUserUtils;

import pc.dd.liveapp.ui.other.TextWatcherForCardNumber;
import pc.dd.liveapp.data.interfaces.PaymentFragmentBackCallback;
import pc.dd.liveapp.data.stripe.CreateStripeRequest;

/**
 * Created by leaditteam on 11.10.17.
 */

public class PaymentDialogFragment extends Fragment {
    
    @BindView(R.id.cardNumber)
    EditText cardNumber;
    @BindView(R.id.monthCard)
    EditText cardMonth;
    @BindView(R.id.yearCardNumber)
    EditText cardYear;
    @BindView(R.id.cardCvvNumber)
    EditText cardCVV;
    @BindView(R.id.titlePaymentDialog)
    TextView titlePaymentDialog;
    
    String parseEventId;
    
    Boolean paymentBtnClickable = true;
    
    PaymentFragmentBackCallback paymentFragmentBackCallback;
    
    ParseUser parseUser = ParseUser.getCurrentUser();
    ParseEventUtils parseEventUtils;
    
    public static PaymentDialogFragment newInstance(String parseObjectId) {
        
        Bundle args = new Bundle();
        args.putString(Constants.PARSE_EVENT_STRING, parseObjectId);
        PaymentDialogFragment fragment = new PaymentDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_input_card, container, false);
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        getExtra();
        getParseEventUtils();
        setCardData();
        
    }
    
    private void getExtra() {
        parseEventId = getArguments().getString(Constants.PARSE_EVENT_STRING);
    }
    
    private void getParseEventUtils() {
        ParseCallbacks.findEventListFromParseObjectsId(parseEventId, new ParseCallbacks.ParseFindCallback() {
            @Override
            public void done(List<ParseObject> objects) {
                if (objects.size() > 0)
                    parseEventUtils = ParseEventUtils.getOneParseEventByObject(objects.get(0));
                
                ParseUserUtils parseUserUtils = ParseUserUtils.getParseUserUtilFromParseUser(parseEventUtils.getUser());
                
                titlePaymentDialog
                        .setText(getString(R.string.title_payment_option_start) + " " +
                                parseUserUtils.getFirstName().toUpperCase() + " " +
                                getString(R.string.title_payment_option_end) + " " +
                                String.valueOf(parseEventUtils.getPrice()).toUpperCase() + " EUROS.");
            }
        });
    }
    
    private void setCardData() {
        cardNumber.addTextChangedListener(new TextWatcherForCardNumber());
        cardCVV.setOnEditorActionListener(onEditorActionListener);
    }
    
    @OnClick(R.id.paymentButton)
    void paymentButtonOnClick() {
        
        if (!checkValidCard()) {
            return;
        }
        
        String cardNumberString = cardNumber.getText().toString().trim();
        int monthInt = Integer.parseInt(cardMonth.getText().toString());
        int yearInt = Integer.parseInt("20" + cardYear.getText().toString());
        String cvvString = cardCVV.getText().toString();
        
        final Card card = new Card(cardNumberString, monthInt, yearInt, cvvString);
    
        if (parseUser.getEmail()!=null){
                if (card.validateCard() && paymentBtnClickable) {
                    createStripeRequest(card);
                } else {
                    UiHelper.showToast(getActivity(), getString(R.string.toast_card_number_is_not_valid));
                }
        } else showEmailDialog();
    }
    
    private void showEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_write_email_title);
        final EditText email = UiHelper.createEditText(getActivity());
        builder.setView(email);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (UiHelper.isValidEmail(email.getText().toString())) {
                    ParseUser user = ParseUser.getCurrentUser();
                    user.setEmail(email.getText().toString());
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null)
                                UiHelper.showToast(getActivity(), getString(R.string.toast_email_save_success));
                            else UiHelper.showToast(getActivity(), getString(R.string.toast_email_is_wrong_facebook));
                        }
                    });
                } else {
                    email.setError(getString(R.string.toast_email_is_wrong_facebook));
                    email.requestFocus();
                    return;
                }
            }
        });
        builder.setNegativeButton("NO", null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getActivity().getResources().getColor(R.color.black));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getActivity().getResources().getColor(R.color.black));
            }
        });
        alertDialog.show();
    }
    
    private void createStripeRequest(Card card) {
        ProgressBarLayout.showProgressBar();
        paymentBtnClickable = false;
        
        new CreateStripeRequest(getActivity(), card, new CreateStripeRequest.StripeCallback() {
            @Override
            public void success(String customerId, String sourceId) {
                putIdsToServer(customerId, sourceId);
            }
            
            @Override
            public void error(Exception e) {
                UiHelper.showToast(getActivity(), e.getLocalizedMessage());
                ProgressBarLayout.dismissProgressBar();
                paymentBtnClickable = true;
            }
        });
        
    }
    
    //TODO: this method cant be in activity,refactor later!
    private void putIdsToServer(String customerId, String sourceId) {
        if (parseUser != null)
            if (customerId != null && sourceId != null && parseEventUtils != null) {
                parseUser.put(Constants.STRIPE_CUSTOMER_ID_STRING, customerId);
                parseUser.put(Constants.STRIPE_SOURCE_ID_STRING, sourceId);
                parseUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            ParseEventRequestUtils.createEventRequest(ParseUser.getCurrentUser(), parseEventUtils.getEventObject(), Constants.STATUS_PENDING_STRING, new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    imageCloseDialogOnClick();
                                }
                            });
                        } else {
                            paymentBtnClickable = true;
                        }
                        ProgressBarLayout.dismissProgressBar();
                    }
                });
            } else {
                parseUser.put(Constants.STRIPE_TEST_ACCOUNT_ID_STRING, sourceId);
                parseUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            imageCloseDialogOnClick();
                        } else {
                            paymentBtnClickable = true;
                        }
                        ProgressBarLayout.dismissProgressBar();
                    }
                });
            }
        else paymentBtnClickable = true;
    }
    
    @OnClick(R.id.imageCloseDialog)
    void imageCloseDialogOnClick() {
        if (paymentFragmentBackCallback != null)
            paymentFragmentBackCallback.onBackPressCallback();
    }
    
    TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == EditorInfo.IME_ACTION_DONE) {
                paymentButtonOnClick();
                return true;
            }
            return false;
        }
    };
    
    private Boolean checkValidCard() {
        if (cardYear.getText().length() < 2) {
            UiHelper.showToast(getActivity(), getString(R.string.toast_string_must_have_more_numbers));
            return false;
        } else if (Integer.parseInt(cardYear.getText().toString()) < 17) {
            UiHelper.showToast(getActivity(), getString(R.string.toast_string_year_must_be_later_than_this_year));
            return false;
        } else if (Integer.parseInt(cardMonth.getText().toString()) < 0 || Integer.parseInt(cardMonth.getText().toString()) > 13) {
            UiHelper.showToast(getActivity(), getString(R.string.toast_string_month_is_incorrect));
            return false;
        } else {
            return true;
        }
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
    
    private void getInterface(Object context) {
        if (context instanceof PaymentDialogActivity)
            paymentFragmentBackCallback = (PaymentFragmentBackCallback) context;
        
    }
}
