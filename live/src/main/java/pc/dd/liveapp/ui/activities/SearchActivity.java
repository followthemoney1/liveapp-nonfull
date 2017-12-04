package pc.dd.liveapp.ui.activities;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vansuita.gaussianblur.GaussianBlur;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import pc.dd.liveapp.R;
import pc.dd.liveapp.utils.Constants;
import pc.dd.liveapp.utils.UiHelper;

import static butterknife.OnTextChanged.Callback.AFTER_TEXT_CHANGED;

/**
 * Created by leaditteam on 03.10.17.
 */

public class SearchActivity extends Activity {
    @BindView(R.id.searchEditText)
    EditText search;
    @BindView(R.id.searchHint)
    TextView searchHint;
    @BindView(R.id.circularRevalAnimationRelative)
    RelativeLayout circularAnimationLayout;
    @BindView(R.id.layoutPrix)
    LinearLayout price;
    @BindView(R.id.layoutDistance)
    LinearLayout distance;
    @BindView(R.id.layoutDemandes)
    LinearLayout demandes;
    @BindView(R.id.image)
    ImageView image;
    
    Bitmap bitmap;
    
    int numberOfCategory;
    String searchText;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        getExtras();
        setViewsData();
        startAnimation();
    }
    
    private void getExtras() {
        numberOfCategory = getIntent().getIntExtra(Constants.SEARCH_RESULT_INT_EXTRA, 0);
        searchText = getIntent().getStringExtra(Constants.SEARCH_RESULT_STRING_EXTRA);
        String imagePath = getIntent().getStringExtra(Constants.BITMAP_EXTRA);
        bitmap = UiHelper.setImageBackgroundAndReturnBitmap(imagePath, image);
    }
    
    private void setViewsData() {
        if (numberOfCategory != 0) {
            switch (numberOfCategory) {
                case Constants.RESULT_PRIX:
                    price.setAlpha(1);
                    break;
                case Constants.RESULT_DISTANCE:
                    distance.setAlpha(1);
                    break;
                case Constants.RESULT_DEMANDES:
                    demandes.setAlpha(1);
                    break;
            }
            search.setText(searchText);
            search.setSelection(search.getText().length());
            setResultIntent(numberOfCategory);
        } else {
            distance.setAlpha(1);
        }
    }
    
    private void startAnimation() {
        if (bitmap != null) {
            final ValueAnimator animation = ValueAnimator.ofInt(1, 20);
            animation.setDuration(500);
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int value = (int) animation.getAnimatedValue();
                    GaussianBlur.with(SearchActivity.this).radius(value).put(bitmap, image);
                }
            });
            
            animation.start();
        }
        
        circularAnimationLayout.setVisibility(View.VISIBLE);
        circularAnimationLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                int w = circularAnimationLayout.getWidth();
                int h = circularAnimationLayout.getHeight();
                
                int endRadius = (int) Math.hypot(w, h);
                
                int cx = (int) (search.getX());
                int cy = (int) (search.getY());
                
                Animator revealAnimator = ViewAnimationUtils.createCircularReveal(circularAnimationLayout, cx, cy, 0, endRadius);
                
                revealAnimator.setDuration(300);
                revealAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {}
                    
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);
                        search.callOnClick();
                        search.requestFocus();
                    }
                    
                    @Override
                    public void onAnimationCancel(Animator animator) {}
                    
                    @Override
                    public void onAnimationRepeat(Animator animator) {}
                });
                revealAnimator.start();
                circularAnimationLayout.removeOnLayoutChangeListener(this);
            }
        });
    }
    
    private void endAnimation() {
        
        int w = circularAnimationLayout.getWidth();
        int h = circularAnimationLayout.getHeight();
        
        int endRadius = (int) Math.hypot(w, h);
        
        int cx = (int) (search.getX());
        int cy = (int) (search.getY());
        
        Animator revealAnimator = ViewAnimationUtils.createCircularReveal(circularAnimationLayout, cx, cy, endRadius, 0);
        
        revealAnimator.setDuration(300);
        revealAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}
            
            @Override
            public void onAnimationEnd(Animator animator) {
                circularAnimationLayout.setVisibility(View.GONE);
                SearchActivity.this.finish();
            }
            
            @Override
            public void onAnimationCancel(Animator animator) {}
            
            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        revealAnimator.start();
    }
    
    @Override
    protected void onPause() {
        this.overridePendingTransition(0, 0);
        super.onPause();
    }
    
    @Override
    public void onBackPressed() {
        endAnimation();
    }
    
    private void setResultIntent(int resultInt) {
        Intent result = new Intent();
        result.putExtra(Constants.SEARCH_RESULT_STRING_EXTRA, search.getText().toString());
        this.setResult(resultInt, result);
    }
    
    @OnTextChanged(value = R.id.searchEditText, callback = AFTER_TEXT_CHANGED)
    void afterTextChange(Editable editable) {
        if (editable.length() == 0) {
            searchHint.setVisibility(View.VISIBLE);
        } else {
            searchHint.setVisibility(View.GONE);
        }
    }
    
    @OnClick(R.id.closeSearch)
    void closeSearchOnClick() {
        onBackPressed();
    }
    
    @OnClick(R.id.image)
    void imageOnClick() {
        onBackPressed();
    }
    
    @OnClick(R.id.layoutPrix)
    void layoutPriceOnClick(View view) {
        categoryClick(view, Constants.RESULT_PRIX);
    }
    
    @OnClick(R.id.layoutDistance)
    void layoutDistanceOnClick(View view) {
        categoryClick(view, Constants.RESULT_DISTANCE);
    }
    
    @OnClick(R.id.layoutDemandes)
    void layoutDemandesOnClick(View view) {
        categoryClick(view, Constants.RESULT_DEMANDES);
    }
    
    @OnClick(R.id.bkg)
    void backgroundOnClick(){
        //nothing to do cause ve need null action
    }
    
    public void categoryClick(View view, int resultInt) {
        distance.setAlpha((float) 0.5);
        price.setAlpha((float) 0.5);
        demandes.setAlpha((float) 0.5);
        
        view.setAlpha(1);
        
        setResultIntent(resultInt);
        
        this.finish();
    }
}
