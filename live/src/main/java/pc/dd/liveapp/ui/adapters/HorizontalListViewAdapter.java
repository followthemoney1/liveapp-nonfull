package pc.dd.liveapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pc.dd.liveapp.R;
import pc.dd.liveapp.ui.fragments.EventDescriptionFragment;
import pc.dd.liveapp.ui.fragments.PartyUsersEventFragment;
import pc.dd.liveapp.utils.ButterKnifeUtils;
import pc.dd.liveapp.utils.ColorUtils;
import pc.dd.liveapp.utils.Constants;
import pc.dd.liveapp.data.parse.ParseCallbacks;
import pc.dd.liveapp.data.parse.ParseEventUtils;
import pc.dd.liveapp.data.interfaces.PageAdapterCallback;

import static pc.dd.liveapp.ui.activities.LiveMainActivity.currentPositionOfPageAdapter;
import static pc.dd.liveapp.utils.ButterKnifeUtils.setTextToEditText;

/**
 * Created by leaditteam on 12.10.17.
 */

public class HorizontalListViewAdapter extends RecyclerView.Adapter<HorizontalListViewAdapter.HorizontalRecyclerViewHolder> {
    
    List<ParseEventUtils> listOfMyEventRequests = new ArrayList<>();
    List<ParseEventUtils> listOfMyEvents = new ArrayList<>();
    List<String> listOfPartyStatus = new ArrayList<>();
    
    Boolean youEvents;
    
    public HorizontalListViewAdapter(Boolean youEvents) {
        this.youEvents = youEvents;
        getParseEventsUtilsMyEvent();
    }
    
    @Override
    public HorizontalListViewAdapter.HorizontalRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_event_listview, parent, false);
        
        return new HorizontalRecyclerViewHolder(v);
    }
    
    @Override
    public void onBindViewHolder(HorizontalListViewAdapter.HorizontalRecyclerViewHolder holder, int position) {
        if (youEvents)
            holder.init(listOfMyEvents.get(position), youEvents, listOfPartyStatus.get(position));
        else
            holder.init(listOfMyEventRequests.get(position), youEvents, null);
    }
    
    @Override
    public long getItemId(int i) {
        return 0;
    }
    
    @Override
    public int getItemCount() {
        if (youEvents)
            return listOfMyEvents.size();
        else
            return listOfMyEventRequests.size();
    }
    
    public void updateData() {
        listOfPartyStatus.clear();
        listOfMyEvents.clear();
        listOfMyEventRequests.clear();
        getParseEventsUtilsMyEvent();
    }
    
    private void getParseEventsUtilsMyEvent() {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        ParseCallbacks.findEventRequestListByParseUser(currentUser, new ParseCallbacks.ParseFindCallback() {
            @Override
            public void done(List<ParseObject> objects) {
                for (int i = 0; i < objects.size(); i++) {
                    ParseEventUtils parseEventUtils =
                            ParseEventUtils.getOneParseEventByObject((ParseObject) objects.get(i).get(Constants.EVENT_STRING_TO_EVENT_REQUEST));
                    
                    if (!parseEventUtils.getUser().getObjectId().equals(currentUser.getObjectId())) {
                        String status = (String) objects.get(i).get(Constants.STATUS_STRING);
                        listOfPartyStatus.add(status);
                        listOfMyEvents.add(parseEventUtils);
                    } else {
                        listOfMyEventRequests.add(parseEventUtils);
                    }
                }
                notifyDataSetChanged();
            }
        });
    }
    
    
    static class HorizontalRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.eventImage)
        ImageView eventImage;
        @BindView(R.id.circleStatus)
        ImageView circleStatus;
        @BindView(R.id.statusOfParty)
        TextView statusOfParty;
        @BindView(R.id.howMuchPeopleHaveOnParty)
        TextView howMuchPeopleHaveOnParty;
        @BindView(R.id.howMuchPeopleMustBe)
        TextView howMuchPeopleMustBe;
        @BindView(R.id.rootLinearLayout)
        LinearLayout rootLinearLayout;
        @BindView(R.id.nameParty)
        TextView nameParty;
        @BindView(R.id.addressEditText)
        TextView addressEditText;
        @BindView(R.id.priceParty)
        TextView priceParty;
    
        Context context;
        ParseEventUtils currentParseEvent;
        Boolean currentEvent;
        PageAdapterCallback pageAdapterInterface;
        
        public HorizontalRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
            pageAdapterInterface = (PageAdapterCallback) context;
            setLayoutSize(rootLinearLayout);
        }
        
        private void setLayoutSize(LinearLayout layout) {
            DisplayMetrics displayMetrics = layout.getResources().getDisplayMetrics();
            int height = displayMetrics.heightPixels / 3;
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(layout.getLayoutParams());
            params.height = height;
            params.width = height;
            
            layout.setLayoutParams(params);
            layout.requestLayout();
        }
        
        public void init(ParseEventUtils parseEventUtils, Boolean youEvents, String status) {
            currentParseEvent = parseEventUtils;
            currentEvent = youEvents;
            
            if (youEvents) {
                ColorUtils.drawImageByStatus(status, circleStatus, context);
                ButterKnife.apply(statusOfParty, setTextToEditText, status);
            } else {
                statusOfParty.setVisibility(View.GONE);
                circleStatus.setVisibility(View.INVISIBLE);
            }
            
            setData(currentParseEvent);
        }
        
        private void setData(ParseEventUtils parseEventUtils) {
            ButterKnife.apply(eventImage, ButterKnifeUtils.setImageToImageView,parseEventUtils.getImageString());
            ButterKnife.apply(howMuchPeopleHaveOnParty, setTextToEditText, String.valueOf(parseEventUtils.getAttendersCount()));
            ButterKnife.apply(howMuchPeopleMustBe, setTextToEditText, "/" + parseEventUtils.getPlaces());
            ButterKnife.apply(nameParty, setTextToEditText, parseEventUtils.getTitle());
            ButterKnife.apply(addressEditText, setTextToEditText, parseEventUtils.getAddress());
            ButterKnife.apply(priceParty, setTextToEditText, parseEventUtils.getPrice() + "€");
        }
        
        @OnClick(R.id.eventImage)
        void eventImageOnClick() {
            if (!currentEvent) {
                pageAdapterInterface.
                        addFragmentToUp(currentPositionOfPageAdapter,
                                PartyUsersEventFragment.newInstance(currentParseEvent, false));
            } else {
                pageAdapterInterface.addFragmentToUp(currentPositionOfPageAdapter,
                        EventDescriptionFragment.newInstance(currentParseEvent.getParseEventRequestUtils()));
                
            }
        }
    }
    
}
