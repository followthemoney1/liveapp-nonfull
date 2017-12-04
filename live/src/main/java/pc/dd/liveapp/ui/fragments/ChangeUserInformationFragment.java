package pc.dd.liveapp.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pc.dd.liveapp.R;
import pc.dd.liveapp.ui.widget.MyDatePicker;
import pc.dd.liveapp.utils.UiHelper;
import pc.dd.liveapp.utils.image.ImagePathHelper;
import pc.dd.liveapp.data.parse.ParseUserUtils;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static pc.dd.liveapp.utils.Constants.PICK_IMAGE;

/**
 * Created by dd.pc on 22.09.2017.
 */

public class ChangeUserInformationFragment extends Fragment {
    @BindView(R.id.imageUser)
    ImageView userImage;
    @BindView(R.id.nameUserRegistration)
    EditText userName;
    @BindView(R.id.lastNameUserRegistration)
    EditText userLastName;
    @BindView(R.id.aboutEditTextRegistration)
    EditText userAbout;
    @BindView(R.id.addressEditTextFull)
    EditText addressFull;
    @BindView(R.id.addressEditTextNotFull)
    EditText addressNotFull;
    @BindView(R.id.myDatePicker)
    MyDatePicker datePicker;
    
    View root;
    
    String imagePath;
    ParseUserUtils parseUserUtils;
    Date date;
    
    public static ChangeUserInformationFragment newInstance() {
        Bundle args = new Bundle();
        ChangeUserInformationFragment fragment = new ChangeUserInformationFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_registration, container, false);
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root = view;
        ButterKnife.bind(this, view);
        init();
    }
    
    private void init() {
        parseUserUtils = (ParseUserUtils) ParseUserUtils.getCurrentUser();
        if (parseUserUtils != null) {
            
            imagePath = parseUserUtils.getPhotoUrl();
            
            Glide.with(this).load(imagePath).into(userImage);
            
            String[] address = parseUserUtils.getAddress().split("&");
            addressFull.setText(address[0]);
            addressNotFull.setText(address[1]);
            userName.setText(parseUserUtils.getFirstName());
            userLastName.setText(parseUserUtils.getLastName());
            userAbout.setText(parseUserUtils.getAbout());
            
            addressFull.setSelection(addressFull.getText().length());
            addressNotFull.setSelection(addressNotFull.getText().length());
            userName.setSelection(userName.getText().length());
            userLastName.setSelection(userLastName.getText().length());
            userAbout.setSelection(userAbout.getText().length());
            
            datePicker.setDateChangedListener(myDatePickerChangeListener);
            
            setUpDate();
        }
    }
    
    private void setUpDate() {
        if (parseUserUtils.getBirthDate() != null) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime((Date) parseUserUtils.getBirthDate());
            datePicker.setCal(calendar);
            date = calendar.getTime();
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    if (data != null && data.getData() != null) {
                        imagePath = ImagePathHelper.getPath(getContext(), data.getData());
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                            userImage.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            Log.e(getActivity().getLocalClassName(), e.getMessage());
                        }
                    } else {
                        try {
                            Bitmap bitmapFile = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(imagePath));
                            Bitmap newOrientationBitmap = UiHelper.resultPhotoBitmapRotate(Uri.parse(imagePath).getPath(), bitmapFile);
                            userImage.setImageBitmap(newOrientationBitmap);
                            imagePath = Uri.parse(imagePath).getPath();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    UiHelper.showToast(getContext(), getString(R.string.toast_cant_load_image));
                }
                return;
        }
    }
    
    MyDatePicker.DateWatcher myDatePickerChangeListener = new MyDatePicker.DateWatcher() {
        @Override
        public void onDateChanged(Calendar c) {
            date = c.getTime();
        }
    };
    
    @OnClick(R.id.imageUser)
    void imageRegistrationProfileOnClickListener() {
        imagePath = UiHelper.openImagePickIntent(getActivity(), PICK_IMAGE).toString();
    }
    
    @OnClick(R.id.cardViewSaveDataRegistration)
    void cardViewSaveDataRegistrationOnClick() {
        if (!isFieldNotEmpty() || date == null) return;
        
        parseUserUtils
                .setLastName(userLastName.getText().toString())
                .setBirthDate(date)
                .setAbout(userAbout.getText().toString())
                .setAddress(addressFull.getText().toString() + " & " + addressNotFull.getText().toString())
                .setFullName(userName.getText().toString() + " " + userLastName.getText().toString())
                .setFirstName(userName.getText().toString())
                .setPhotoUrl(imagePath)
                .updateUserInfo(getActivity(), getFragmentManager());
        
    }
    
    @OnClick(R.id.backButton)
    void backButtonOnClick() {
        getFragmentManager().popBackStack();
    }
    
    private Boolean isFieldNotEmpty() {
        String fName = userName.getText().toString().trim();
        String lName = userLastName.getText().toString().trim();
        String fAddress = addressFull.getText().toString().trim();
        String sAddress = addressNotFull.getText().toString().trim();
        String sAbout = userAbout.getText().toString().trim();
        
        if (fName.isEmpty()) {
            UiHelper.showToast(getContext(), getString(R.string.toast_you_must_fielt_first_name));
            return false;
        } else if (lName.isEmpty()) {
            UiHelper.showToast(getContext(), getString(R.string.toast_you_must_fill_last_name));
            return false;
        } else if (fAddress.isEmpty() && sAddress.isEmpty()) {
            UiHelper.showToast(getContext(), getString(R.string.toast_you_must_fill_adress));
            return false;
        } else if (sAbout.isEmpty()) {
            UiHelper.showToast(getContext(), getString(R.string.toast_you_must_fill_about));
            return false;
        } else if ((imagePath == null || imagePath.isEmpty())) {
            UiHelper.showToast(getContext(), getString(R.string.toast_you_must_upload_image));
            return false;
        } else {
            return true;
        }
    }
}
