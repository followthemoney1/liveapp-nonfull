package pc.dd.liveapp.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pc.dd.liveapp.R;
import pc.dd.liveapp.ui.adapters.PartyUsersAdapter;
import pc.dd.liveapp.ui.other.ProgressBarLayout;
import pc.dd.liveapp.utils.Constants;
import pc.dd.liveapp.data.parse.ParseCallbacks;
import pc.dd.liveapp.data.parse.ParseEventUtils;
import pc.dd.liveapp.data.parse.ParseUserUtils;

/**
 * Created by leaditteam on 10.10.17.
 */

public class PartyUsersEventFragment extends Fragment {
    @BindView(R.id.gridViewPartyUsers)
    GridView gridViewPartyUsers;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    
    ParseEventUtils parseEventUtils;
    List<ParseUserUtils> parseUserUtilsList = new ArrayList<>();
    PartyUsersAdapter partyUsersAdapter;
    
    Boolean comeFromMainFragment;
    
    public static PartyUsersEventFragment newInstance(ParseEventUtils parseEventUtils, Boolean comeFromMainFragment) {
        
        Bundle args = new Bundle();
        
        args.putSerializable(Constants.PARSE_EXTRA_STRING, parseEventUtils);
        args.putBoolean(Constants.COME_FROM_MAIN_FRAGMENT_EXTRA, comeFromMainFragment);
        
        PartyUsersEventFragment fragment = new PartyUsersEventFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_party_users_event, container, false);
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProgressBarLayout.showProgressBar();
        ButterKnife.bind(this, view);
        getExtras();
        setDataToViews();
    }
    
    private void getExtras() {
        parseEventUtils = (ParseEventUtils) getArguments().getSerializable(Constants.PARSE_EXTRA_STRING);
        comeFromMainFragment = getArguments().getBoolean(Constants.COME_FROM_MAIN_FRAGMENT_EXTRA);
    }
    
    private void setDataToViews() {
        partyUsersAdapter = new PartyUsersAdapter(parseUserUtilsList, getActivity(), getChildFragmentManager(), comeFromMainFragment, parseEventUtils.getObjectId());
        
        gridViewPartyUsers.setAdapter(partyUsersAdapter);
        gridViewPartyUsers.setNumColumns(2);
        gridViewPartyUsers.setOnScrollListener(onScrollChangeListener);
        
        swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);
    }
    
    private void getListOfParseUsers() {
        if (parseEventUtils != null)
            ParseCallbacks.findEventRequestListByOneEvent(parseEventUtils.getEventObject(), new ParseCallbacks.ParseFindCallback() {
                @Override
                public void done(List<ParseObject> objects) {
                    parseUserUtilsList.clear();
                    
                    for (int i = 0; i < objects.size(); i++) {
                        
                        ParseUser parseUser = (ParseUser) objects.get(i).get(Constants.USER);
                        ParseUserUtils parseUserUtils = ParseUserUtils.getParseUserUtilFromParseUser(parseUser);
                        
                        if (parseUserUtils != null && !parseUser.getObjectId().equals(parseEventUtils.getUser().getObjectId())) {
                            if (comeFromMainFragment) {
                                if (objects.get(i).get(Constants.STATUS_STRING).toString().equals(Constants.STATUS_ACCEPT_STRING)) {
                                    parseUserUtilsList.add(parseUserUtils);
                                    partyUsersAdapter.notifyDataSetChanged();
                                }
                            } else {
                                parseUserUtils.setStatusEvent(objects.get(i).get(Constants.STATUS_STRING).toString());
                                parseUserUtilsList.add(parseUserUtils);
                                partyUsersAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
    }
    
    @OnClick(R.id.backPressBtn)
    void backPressBtnOnClick() {
        getFragmentManager().popBackStack();
    }
    
    SwipeRefreshLayout.OnRefreshListener swipeRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getListOfParseUsers();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 1200);
        }
    };
    
    AbsListView.OnScrollListener onScrollChangeListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        
        }
        
        @Override
        public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int topRowVerticalPosition = (listView == null || listView.getChildCount() == 0) ?
                    0 : gridViewPartyUsers.getChildAt(0).getTop();
            swipeRefreshLayout.setEnabled((topRowVerticalPosition >= 0));
        }
    };
    
    @Override
    public void onResume() {
        getListOfParseUsers();
        super.onResume();
    }
}
