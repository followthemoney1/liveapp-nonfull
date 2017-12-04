package pc.dd.liveapp.ui.activities;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.vansuita.gaussianblur.GaussianBlur;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pc.dd.liveapp.R;
import pc.dd.liveapp.ui.fragments.PaymentDialogFragment;
import pc.dd.liveapp.ui.other.ProgressBarLayout;
import pc.dd.liveapp.utils.Constants;
import pc.dd.liveapp.data.interfaces.PaymentFragmentBackCallback;
import pc.dd.liveapp.utils.UiHelper;

/**
 * Created by leaditteam on 11.10.17.
 */

public class PaymentDialogActivity extends Activity implements PaymentFragmentBackCallback, Serializable {
    @BindView(R.id.relativeContent)
    RelativeLayout content;
    @BindView(R.id.backgroundImage)
    ImageView imageBackground;
    
    String parseObjectId;
    Bitmap bitmap;
    String imagePath;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        getExtras();
        initProgressBar();
        addFragmentToActivity();
        startAnimation();
    }
    
    public void getExtras() {
        imagePath = getIntent().getStringExtra(Constants.BITMAP_EXTRA);
        parseObjectId = getIntent().getStringExtra(Constants.PARSE_EVENT_STRING);
        bitmap = UiHelper.setImageBackgroundAndReturnBitmap(imagePath, imageBackground);
    }
    
    public void initProgressBar() {
        new ProgressBarLayout((ProgressBar) findViewById(R.id.loading));
        ProgressBarLayout.dismissProgressBar();
        ProgressBarLayout.setCanDismissed(false);
    }
    
    public void addFragmentToActivity() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.paymentDialogContainer, PaymentDialogFragment.newInstance(parseObjectId))
                .commit();
    }
    
    public void startAnimation() {
        content.setTranslationY(-2000f);
        content.animate().translationY(0f).setDuration(Constants.DURATON_300).start();
        
        if (bitmap != null) {
            final ValueAnimator animation = ValueAnimator.ofInt(1, 20);
            animation.setDuration(Constants.DURATON_500);
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int value = (int) animation.getAnimatedValue();
                    GaussianBlur.with(PaymentDialogActivity.this).radius(value).put(bitmap, imageBackground);
                }
            });
            
            animation.start();
        }
    }
    
    public void endAnimation() {
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
                        GaussianBlur.with(PaymentDialogActivity.this).radius(value).put(bitmap, imageBackground);
                    else {
                        imageBackground.setImageBitmap(bitmap);
                        PaymentDialogActivity.this.finish();
                    }
                }
            });
            
            animation.start();
        }
        
    }
    
    @OnClick(R.id.relativeContent)
    void relativeContentOnClick() {
       onBackPressed();
    }
    
    @OnClick(R.id.paymentDialogContainer)
    void paymentDialogContainerOnClick() {
        //nothing do cause we need null action on click
    }
    
    @Override
    public void onBackPressCallback() {
        onBackPressed();
    }
    
    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        endAnimation();
    }
    
    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }
   
}
