package pc.dd.liveapp.ui.fragments;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import pc.dd.liveapp.R;
import pc.dd.liveapp.ui.adapters.HorizontalListViewAdapter;
import pc.dd.liveapp.ui.widget.MyRecycleView;
import pc.dd.liveapp.ui.widget.OnlyVerticalSwipeRefreshLayout;

/**
 * Created by leaditteam on 08.09.17.
 */

public class ProposalFragment extends Fragment {
    @BindView(R.id.listViewInPartyesRequests)
    MyRecycleView recycleViewPartyRequests;
    @BindView(R.id.listViewOutPartyesRequests)
    MyRecycleView recycleViewMyParty;
    @BindView(R.id.swipeRefreshLayout)
    OnlyVerticalSwipeRefreshLayout swipeRefreshLayout;
    
    HorizontalListViewAdapter adapterPending;
    HorizontalListViewAdapter adapterMyEvents;
    
    public static ProposalFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ProposalFragment fragment = new ProposalFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_proposal, container, false);
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setDataToViews();
    }
    
    private void setDataToViews() {
        adapterPending = new HorizontalListViewAdapter(false);
        recycleViewPartyRequests.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recycleViewPartyRequests.setAdapter(adapterPending);
        setRecycleViewHeight(recycleViewPartyRequests);
        
        adapterMyEvents = new HorizontalListViewAdapter(true);
        recycleViewMyParty.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recycleViewMyParty.setAdapter(adapterMyEvents);
        setRecycleViewHeight(recycleViewMyParty);
        
        swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);
    }
    
    private void setRecycleViewHeight(RecyclerView recyclerView) {
        DisplayMetrics displayMetrics = recyclerView.getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels / 3;
        
        recyclerView.setMinimumHeight(height);
    }
    
    SwipeRefreshLayout.OnRefreshListener swipeRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            adapterPending.updateData();
            adapterMyEvents.updateData();
            
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 1200);
        }
    };
}
