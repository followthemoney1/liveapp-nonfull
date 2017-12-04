package pc.dd.liveapp.ui.activities;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.vansuita.gaussianblur.GaussianBlur;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pc.dd.liveapp.R;
import pc.dd.liveapp.ui.fragments.ChangeUserStatusFragment;
import pc.dd.liveapp.utils.Constants;
import pc.dd.liveapp.data.interfaces.PaymentFragmentBackCallback;
import pc.dd.liveapp.utils.UiHelper;

/**
 * Created by leaditteam on 19.10.17.
 */

public class ChangeUserStatusActivity extends Activity implements PaymentFragmentBackCallback{
    @BindView(R.id.relativeContent)
    RelativeLayout content;
    @BindView(R.id.backgroundImage)
    ImageView imageBackground;
    
    String userObjectId;
    String parseEventId;
   
    Bitmap bitmap;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_status_event);
        ButterKnife.bind(this);
        getExtras();
        addFragmentToActivity();
        startAnimation();
    }
    
    private void getExtras() {
        userObjectId = getIntent().getStringExtra(Constants.USER_OBJECT_ID_STRING);
        parseEventId = getIntent().getStringExtra(Constants.EVENT_OBJECT_ID_STRING);
        String imagePath = getIntent().getStringExtra(Constants.BITMAP_EXTRA);
        bitmap = UiHelper.setImageBackgroundAndReturnBitmap(imagePath, imageBackground);
    }
    
    private void addFragmentToActivity() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.paymentDialogContainer, ChangeUserStatusFragment.newInstance(userObjectId, parseEventId))
                .commit();
    }
    
    private void startAnimation() {
        content.setTranslationY(-2000f);
        content.animate().translationY(0f).setDuration(Constants.DURATON_300).start();
        
        if (bitmap != null) {
            final ValueAnimator animation = ValueAnimator.ofInt(1, 20);
            animation.setDuration(Constants.DURATON_500);
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int value = (int) animation.getAnimatedValue();
                    GaussianBlur.with(ChangeUserStatusActivity.this).radius(value).put(bitmap, imageBackground);
                }
            });
            
            animation.start();
        }
    }
    
    private void backAnimation() {
        content.setTranslationY(-0f);
        content.animate().translationY(-2000f).setDuration(Constants.DURATON_300).start();
        
        if (bitmap != null) {
            final ValueAnimator animation = ValueAnimator.ofInt(10, 1);
            animation.setDuration(Constants.DURATON_300);
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int value = (int) animation.getAnimatedValue();
                    if (value != 1)
                        GaussianBlur.with(ChangeUserStatusActivity.this).radius(value).put(bitmap, imageBackground);
                    else {
                        imageBackground.setImageBitmap(bitmap);
                        ChangeUserStatusActivity.this.finish();
                    }
                }
            });
            
            animation.start();
        }
        
    }
    
    @OnClick(R.id.paymentDialogContainer)
    void paymentDialogContainerOnClick(){
        //nothing to do cause we need null onclick event
    }
    
    @Override
    public void onBackPressCallback(){
        onBackPressed();
    }
    
    @OnClick(R.id.backgroundImage)
    void backgroundImageOnClick()  {
        onBackPressed();
    }
    
    @Override
    public void onBackPressed() {
        backAnimation();
    }
    
    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }
}
