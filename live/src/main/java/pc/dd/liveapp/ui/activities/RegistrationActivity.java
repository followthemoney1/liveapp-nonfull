package pc.dd.liveapp.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.ParseUser;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pc.dd.liveapp.R;
import pc.dd.liveapp.ui.widget.MyDatePicker;
import pc.dd.liveapp.utils.Constants;
import pc.dd.liveapp.utils.UiHelper;
import pc.dd.liveapp.utils.image.ImagePathHelper;
import pc.dd.liveapp.data.parse.ParseUserUtils;

import static pc.dd.liveapp.utils.Constants.PICK_IMAGE;


/**
 * Created by leaditteam on 06.09.17.
 */

public class RegistrationActivity extends Activity {
    @BindView(R.id.imageUser)
    ImageView image;
    @BindView(R.id.nameUserRegistration)
    EditText firstName;
    @BindView(R.id.lastNameUserRegistration)
    EditText lastName;
    @BindView(R.id.aboutEditTextRegistration)
    EditText about;
    @BindView(R.id.addressEditTextFull)
    EditText addressCountry;
    @BindView(R.id.addressEditTextNotFull)
    EditText addressCity;
    @BindView(R.id.myDatePicker)
    MyDatePicker datePicker;
    
    String imagePath;
    Date birthdayDate;
    ParseUserUtils parseUserUtils;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        getExtras();
        setUpDataToViews();
        setUpDate();
    }
    
    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        parseUserUtils = (ParseUserUtils) extras.getSerializable(Constants.PARSE_USER_UTILS_EXTRA);
        imagePath = parseUserUtils.getPhotoUrl();
        birthdayDate = (Date) parseUserUtils.getBirthDate();
    }
    
    private void setUpDataToViews() {
        if (imagePath != null)
            image.setImageURI(Uri.fromFile(new File(imagePath).getAbsoluteFile()));
        
        firstName.setText(parseUserUtils.getFullName().split(" ")[0]);
        lastName.setText(parseUserUtils.getFullName().split(" ")[1]);
        
        firstName.setSelection(firstName.getText().length());
        lastName.setSelection(lastName.getText().length());
        
        datePicker.setDateChangedListener(datePickerChangeListener);
    }
    
    private void setUpDate() {
        if (birthdayDate != null) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(birthdayDate);
            datePicker.setCal(calendar);
            birthdayDate = calendar.getTime();
        }
    }
    
    MyDatePicker.DateWatcher datePickerChangeListener = new MyDatePicker.DateWatcher() {
        @Override
        public void onDateChanged(Calendar c) {
            birthdayDate = c.getTime();
        }
    };
    
    @OnClick(R.id.imageUser)
    void imageRegistrationProfileOnClickListener() {
        Uri temp;
        if ((temp = UiHelper.openImagePickIntent(RegistrationActivity.this, PICK_IMAGE))!=null)
        imagePath = temp.toString();
    }
    
    @OnClick(R.id.cardViewSaveDataRegistration)
    void cardViewSaveDataRegistrationOnClick() {
        if (!checkFieldNotEmpty() || birthdayDate == null) return;
        
        new ParseUserUtils(RegistrationActivity.this, LiveMainActivity.class)
                .setEmail(parseUserUtils.getEmail())
                .setUser(ParseUser.getCurrentUser())
                .setGender(parseUserUtils.getGender())
                .setFacebookId(parseUserUtils.getFacebookId())
                .setLastName(lastName.getText().toString())
                .setBirthDate(birthdayDate)
                .setAbout(about.getText().toString())
                .setAddress(addressCountry.getText().toString() + " " + addressCity.getText().toString())
                .setFullName(firstName.getText().toString() + " " + lastName.getText().toString())
                .setFirstName(firstName.getText().toString())
                .setPhotoUrl(imagePath)
                .createUser();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    if (data != null && data.getData() != null) {
                        imagePath = ImagePathHelper.getPath(this, data.getData());
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                            image.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            Log.e(this.getLocalClassName(), e.getMessage());
                        }
                    } else {
                        try {
                            Bitmap bitmapFile = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(imagePath));
                            image.setImageBitmap(bitmapFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (resultCode == RESULT_CANCELED) {
                
                }
                return;
        }
    }
    
    ;
    
    private Boolean checkFieldNotEmpty() {
        String fName = firstName.getText().toString().trim();
        String lName = lastName.getText().toString().trim();
        String fAddress = addressCountry.getText().toString().trim();
        String sAddress = addressCity.getText().toString().trim();
        String sAbout = about.getText().toString().trim();
        
        if (fName.isEmpty()) {
            UiHelper.showToast(this, getString(R.string.toast_you_must_fielt_first_name));
            return false;
        } else if (lName.isEmpty()) {
            UiHelper.showToast(this, getString(R.string.toast_you_must_fill_last_name));
            return false;
        } else if (fAddress.isEmpty() && sAddress.isEmpty()) {
            UiHelper.showToast(this, getString(R.string.toast_you_must_fill_adress));
            return false;
        } else if (sAbout.isEmpty()) {
            UiHelper.showToast(this, getString(R.string.toast_you_must_fill_about));
            return false;
        } else if ((imagePath == null || imagePath.isEmpty())) {
            UiHelper.showToast(this, getString(R.string.toast_you_must_upload_image));
            return false;
        } else {
            return true;
        }
    }
}
