package pc.dd.liveapp.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pc.dd.liveapp.R;
import pc.dd.liveapp.data.parse.ParseEventRequestUtils;
import pc.dd.liveapp.data.parse.ParseUserUtils;
import pc.dd.liveapp.ui.activities.SearchActivity;
import pc.dd.liveapp.ui.adapters.EventAdapter;
import pc.dd.liveapp.ui.other.ProgressBarLayout;
import pc.dd.liveapp.utils.Constants;
import pc.dd.liveapp.utils.LocationUtils;
import pc.dd.liveapp.utils.PermissionUtils;
import pc.dd.liveapp.data.interfaces.EventUpdateCallback;
import pc.dd.liveapp.data.parse.ParseEventUtils;
import pc.dd.liveapp.utils.UiHelper;

import static pc.dd.liveapp.utils.Constants.SEARCH_ACTIVITY_RESULT;

/**
 * Created by leaditteam on 08.09.17.
 */

public class EventsFragment extends Fragment {
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.mainEventListView)
    ListView mainEventListView;
    
    EventAdapter eventAdapter;
    
    FragmentManager fragmentManager;
    
    String searchText = null;
    int searchResult = 0;
    
    Location myPosition;
    GoogleApiClient googleApiClient;
    
    Boolean fragmentBackStackComeOut = false;
    
    public static EventsFragment newInstance() {
        Bundle args = new Bundle();
        EventsFragment fragment = new EventsFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        init();
        LocationUtils.displayLocationSettingsRequest(getContext());
    }
    
    private void init() {
        fragmentManager = getFragmentManager();
        eventAdapter = new EventAdapter(ParseEventUtils.getEventList(eventUpdateInterface), getActivity(), eventUpdateInterface);
        mainEventListView.setAdapter(eventAdapter);
        mainEventListView.setOnScrollListener(onScrollChangeListener);
        swipeRefreshLayout.setOnRefreshListener(swipeRefreshLayoutOnRefresh);
        
        getChildFragmentManager().addOnBackStackChangedListener(fragmentManegerBackStackChangeListener);
    }
    
    EventUpdateCallback eventUpdateInterface = new EventUpdateCallback() {
        @Override
        public void getEventCallback() {
            sortByResult();
            ProgressBarLayout.dismissProgressBar();
        }
        
        @Override
        public void refreshEventCallback() {
            eventAdapter.updateEvents(ParseEventUtils.getEventList(eventUpdateInterface));
        }
    };
    
    SwipeRefreshLayout.OnRefreshListener swipeRefreshLayoutOnRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            eventUpdateInterface.refreshEventCallback();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 100);
        }
    };
    
    AbsListView.OnScrollListener onScrollChangeListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        
        }
        
        @Override
        public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int topRowVerticalPosition = (listView == null || listView.getChildCount() == 0) ?
                    0 : mainEventListView.getChildAt(0).getTop();
            swipeRefreshLayout.setEnabled((topRowVerticalPosition >= 0));
        }
    };
    
    @OnClick(R.id.searchImage)
    void searchImageOnClick() {
        UiHelper
                .openActivityDialog(
                        null,
                        null,
                        searchText,
                        null,
                        searchResult,
                        SEARCH_ACTIVITY_RESULT,
                        getActivity(),
                        SearchActivity.class);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEARCH_ACTIVITY_RESULT:
                if (data != null) {
                    searchText = data.getStringExtra(Constants.SEARCH_RESULT_STRING_EXTRA);
                    searchResult = resultCode;
                    sortByResult();
                }
            
        }
    }
    
    private void sortByResult() {
        if (searchResult == Constants.RESULT_PRIX) {
            eventAdapter.sortByPrice(searchText);
        } else if (searchResult == Constants.RESULT_DEMANDES) {
            eventAdapter.sortByAttendersCount(searchText);
        } else if (searchResult == Constants.RESULT_DISTANCE) {
            sortByDistance();
        } else if (searchResult == 0) {
            sortByDistance();
        }
    }
    
    
    android.support.v4.app.FragmentManager.OnBackStackChangedListener fragmentManegerBackStackChangeListener = new android.support.v4.app.FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            if (fragmentBackStackComeOut) {
                eventUpdateInterface.refreshEventCallback();
                fragmentBackStackComeOut = false;
            } else
                fragmentBackStackComeOut = true;
            
        }
    };
    
}
