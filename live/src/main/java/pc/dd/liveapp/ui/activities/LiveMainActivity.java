package pc.dd.liveapp.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseUser;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;
import pc.dd.liveapp.R;
import pc.dd.liveapp.data.parse.ParseCallbacks;
import pc.dd.liveapp.data.parse.ParseEventRequestUtils;
import pc.dd.liveapp.data.parse.ParseEventUtils;
import pc.dd.liveapp.data.parse.ParseUserUtils;
import pc.dd.liveapp.data.parse.notification.NotificationHelper;
import pc.dd.liveapp.ui.adapters.MyPagerAdapter;
import pc.dd.liveapp.ui.fragments.EventDescriptionFragment;
import pc.dd.liveapp.utils.ColorUtils;
import pc.dd.liveapp.ui.other.ProgressBarLayout;
import pc.dd.liveapp.data.interfaces.PageAdapterCallback;
import pc.dd.liveapp.utils.Constants;

import static butterknife.OnPageChange.Callback.PAGE_SCROLLED;
import static pc.dd.liveapp.utils.Constants.PAYMENT_CREATE_EVENT_RESULT;
import static pc.dd.liveapp.utils.Constants.PICK_IMAGE;
import static pc.dd.liveapp.utils.Constants.SEARCH_ACTIVITY_RESULT;

/**
 * Created by leaditteam on 07.09.17.
 */

public class LiveMainActivity extends FragmentActivity implements PageAdapterCallback {
    @BindView(R.id.vpPager)
    ViewPager pager;
    @BindView(R.id.bottonMenu)
    LinearLayout navigation;
    
    @BindColor(R.color.gradient_start)
    @ColorInt
    int firstColor;
    
    @BindColor(R.color.gradient_end)
    @ColorInt
    int endColor;
    
    @BindColor(R.color.menu_default)
    @ColorInt
    int defaultColor;
    
    MyPagerAdapter pagerAdapter;
    public static int currentPositionOfPageAdapter = 0;
    
    int[] imageMenuArray = new int[]{R.id.btnMain,R.id.btnViewProposition,R.id.btnCreateProposition,R.id.btnProfile};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_main);
        ButterKnife.bind(this);
        init();
        NotificationHelper.init(this);
        getPendingIntent();
    }
    
    private void init() {
        new ProgressBarLayout(((ProgressBar) findViewById(R.id.loading)));
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setOffscreenPageLimit(3);
    }
    
    private void getPendingIntent(){
      if (getIntent().getExtras().getInt(Constants.NAME_EXTRA) != 0)
          onHasNotificationResult(getIntent().getExtras().getString(Constants.EVENT_ID_STRING));
    }
    
    @Override
    public void addFragmentToUp(int position, Fragment fragment) {
        pagerAdapter.addFragment(position, fragment);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = pagerAdapter.getItem(currentPositionOfPageAdapter);;
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    notifyFragments(fragment,requestCode,resultCode,data);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, getString(R.string.toast_cant_load_image), Toast.LENGTH_SHORT).show();
                }
                break;
            case PAYMENT_CREATE_EVENT_RESULT:
                notifyFragments(fragment,requestCode,resultCode,data);
                break;
            case SEARCH_ACTIVITY_RESULT:
                notifyFragments(fragment,requestCode,resultCode,data);
                break;
            default:
                break;
            
        }
    }
    
    private void notifyFragments(Fragment fragment, int requestCode, int resultCode, Intent data){
        if (fragment!=null)
        fragment.onActivityResult(requestCode, resultCode, data);
    
        //notify all child fragment if need
        if (fragment!=null)
        for (Fragment childFragment : fragment.getChildFragmentManager().getFragments()) {
            childFragment.onActivityResult(requestCode, resultCode, data);
        
            for (Fragment childFragmentSecond : childFragment.getChildFragmentManager().getFragments()) {
                childFragmentSecond.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
    
    private void onHasNotificationResult(final String eventId){
        if (eventId!=null&&ParseUserUtils.getCurrentUser()!=null)
                    ParseEventUtils.getEventById(eventId, new ParseCallbacks.LoadCallback() {
                        @Override
                        public void onLoad(Object event) {
                            new ParseEventRequestUtils()
                                    .setParseUserUtils(ParseUserUtils.getCurrentUser())
                                    .setParseEventUtils((ParseEventUtils) event)
                                    .findByParseUserAndParseEvent(false, new ParseEventRequestUtils.ParseEventRequestCallback() {
                                        @Override
                                        public void done(ParseEventRequestUtils parseEventRequest) {
                                            addFragmentToUp(currentPositionOfPageAdapter,
                                                    EventDescriptionFragment.newInstance(parseEventRequest));
                                        }
                                    });
                        }
        
            });
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            navigation.setVisibility(View.GONE);
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            navigation.setVisibility(View.VISIBLE);
        }
    }
    
    @OnClick(R.id.btnMain)
    void btnMainOnClick() {
        pager.setCurrentItem(0);
    }
    
    @OnClick(R.id.btnViewProposition)
    void btnViewPropositionOnClick() {
        pager.setCurrentItem(1);
    }
    
    @OnClick(R.id.btnCreateProposition)
    void btnCreatePropositionOnClick() {
        pager.setCurrentItem(2);
    }
    
    @OnClick(R.id.btnProfile)
    void btnProfileOnClick() {
        pager.setCurrentItem(3);
    }
    
    @OnPageChange(value = R.id.vpPager,callback = PAGE_SCROLLED)
    void onPageScrolled(int position){
        currentPositionOfPageAdapter = position;
        ColorUtils.setIconsColorDefault(defaultColor,this, imageMenuArray);
        ColorUtils.onMenuItemSelected((ImageView) findViewById(imageMenuArray[position]), firstColor, endColor);
    }
    
    public void setCurrentPositionOfPageAdapter(int positionOfPageAdapter) {
        if (pager != null)
            pager.setCurrentItem(positionOfPageAdapter);
    }
}
