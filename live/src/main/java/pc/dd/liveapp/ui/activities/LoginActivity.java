package pc.dd.liveapp.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseFacebookUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pc.dd.liveapp.R;
import pc.dd.liveapp.utils.Constants;
import pc.dd.liveapp.utils.image.DownloadImageAsync;
import pc.dd.liveapp.data.interfaces.FacebookLoginUserCallback;
import pc.dd.liveapp.data.parse.ParseUserUtils;

public class LoginActivity extends Activity implements FacebookLoginUserCallback {
    
    ParseUserUtils parseUserUtils;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        checkLoginUser();
    }
    
    private void checkLoginUser() {
        if (ParseUserUtils.getCurrentUser() != null) {
            startActivity(new Intent(this, LiveMainActivity.class));
            finish();
        }
        
    }
    
    @OnClick(R.id.facebookLoginBtnLayout)
    void facebookLoginBtnLayoutOnClick() {
        FacebookDataHelper.createAccountFromParse(this);
    }
    
    @Override
    public void facebookLoginCallback(Boolean login, ParseUserUtils parseUserUtils) {
        if (login)
            startActivity(new Intent(this, LiveMainActivity.class));
         else {
            this.parseUserUtils = parseUserUtils;
            new DownloadImageAsync(parseUserUtils.getPhotoUrl(), LoginActivity.this, downloadImageAsyncCallback).execute();
        }
        
    }
    
    DownloadImageAsync.DownloadImageAsyncCallback downloadImageAsyncCallback = new DownloadImageAsync.DownloadImageAsyncCallback() {
        @Override
        public void done(String url) {
            if (parseUserUtils != null) {
                parseUserUtils.setPhotoUrl(url);
                Intent registrationIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                registrationIntent.putExtra(Constants.PARSE_USER_UTILS_EXTRA, parseUserUtils);
                startActivity(registrationIntent);
            }
        }
    };
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
