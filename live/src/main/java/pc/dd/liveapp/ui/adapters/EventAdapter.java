package pc.dd.liveapp.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pc.dd.liveapp.R;
import pc.dd.liveapp.data.parse.ParseCallbacks;
import pc.dd.liveapp.ui.fragments.EventDescriptionFragment;
import pc.dd.liveapp.data.interfaces.EventUpdateCallback;
import pc.dd.liveapp.data.parse.ParseEventRequestUtils;
import pc.dd.liveapp.data.parse.ParseEventUtils;
import pc.dd.liveapp.data.interfaces.PageAdapterCallback;
import pc.dd.liveapp.data.parse.ParseUserUtils;
import pc.dd.liveapp.utils.ButterKnifeUtils;
import pc.dd.liveapp.utils.Constants;
import pc.dd.liveapp.utils.UiHelper;

import static pc.dd.liveapp.ui.activities.LiveMainActivity.currentPositionOfPageAdapter;
import static pc.dd.liveapp.utils.ButterKnifeUtils.setTextToEditText;

/**
 * Created by leaditteam on 27.09.17.
 */

public class EventAdapter extends BaseAdapter {
    List<ParseEventUtils> events = new ArrayList<>();
    
    LayoutInflater inflater;
    EventUpdateCallback eventUpdateInterface;
    PageAdapterCallback pageAdapterInterface;
    
    public EventAdapter(List<ParseEventUtils> allEvents, Activity activity, EventUpdateCallback eventUpdate) {
        this.events = allEvents;
        this.eventUpdateInterface = eventUpdate;
        pageAdapterInterface = (PageAdapterCallback) activity;
        
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
    public int getCount() {
        return events.size();
    }
    
    @Override
    public Object getItem(int i) {
        return events.get(i);
    }
    
    @Override
    public long getItemId(int i) {
        return i;
    }
    
    @Override
    public View getView(final int i, View root, ViewGroup viewGroup) {
        root = inflater.inflate(R.layout.view_one_event, viewGroup, false);
        
        new MainViewHolder(root, events.get(i));
        
        return root;
    }
    
    public void sortByPrice(final String searchText) {
        sortActionFindMatch(searchText);
        
        Collections.sort(events, new Comparator<ParseEventUtils>() {
            @Override
            public int compare(ParseEventUtils t1, ParseEventUtils t2) {
                return t1.getPrice() - t2.getPrice();
            }
        });
        
        notifyDataSetChanged();
    }
    
    public void sortByDistance(final String searchText, final ParseGeoPoint parseGeoPoint) {
        sortActionFindMatch(searchText);
        
        Collections.sort(events, new Comparator<ParseEventUtils>() {
            @Override
            public int compare(ParseEventUtils t1, ParseEventUtils t2) {
                if (t1.getLocation() == null) {
                    return (t2.getLocation() == null) ? -2 : 0;
                }
                if (t2.getLocation() == null) {
                    return -1;
                }
                double k1 = t1.getLocation().distanceInKilometersTo(parseGeoPoint);
                double k2 = t2.getLocation().distanceInKilometersTo(parseGeoPoint);
                return (int) (k1 - k2);
            }
        });
        
        notifyDataSetChanged();
    }
    
    public void sortByAttendersCount(String searchText) {
        sortActionFindMatch(searchText);
        
        final int[] callbackEndIteration = {0};
        
        for (int i = 0; i < events.size(); i++) {
            
            final ParseEventUtils eventUtils = events.get(i);
            
            new ParseEventRequestUtils()
                    .setParseEventUtils(eventUtils)
                    .setParseUserUtils(ParseUserUtils.getParseUserUtilFromParseUser(eventUtils.getUser()))
                    .findEventDependencies(new ParseCallbacks.ParseFindCallback() {
                        @Override
                        public void done(List<ParseObject> objects) {
                            
                            callbackEndIteration[0]++;
                            eventUtils.setDependencyCount(objects.size());
                            //when iteration is last we update views
                            if (callbackEndIteration[0] == events.size()) {
                                Collections.sort(events, new Comparator<ParseEventUtils>() {
                                    @Override
                                    public int compare(ParseEventUtils t1, ParseEventUtils t2) {
                                        return t2.getDependencyCount() - t1.getDependencyCount();
                                    }
                                });
                                notifyDataSetChanged();
                            }
                        }
                    });
        }
    }
    
    private void sortActionFindMatch(String searchText) {
        if (searchText != null && !searchText.trim().equals("")) {
            for (int i = 0; i < events.size(); i++) {
                if ((!events.get(i).getTitle().toLowerCase().contains(searchText.toLowerCase().trim()))
                        && !events.get(i).getAbout().toLowerCase().contains(searchText.toLowerCase().trim())) {
                    events.remove(i);
                    i = i - 1;
                }
            }
        }
        ;
    }
    
    public void updateEvents(List<ParseEventUtils> allEvents) {
        this.events = allEvents;
    }
    
    class MainViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.oneEventImage)
        ImageView image;
        @BindView(R.id.eventCancel)
        RelativeLayout cancel;
        @BindView(R.id.eventName)
        TextView name;
        @BindView(R.id.eventPrice)
        TextView price;
        @BindView(R.id.eventRegion)
        TextView address;
        @BindView(R.id.rootLinearLayout)
        RelativeLayout root;
        
        ParseEventUtils parseEventUtils;
        
        public MainViewHolder(View itemView, ParseEventUtils parseEventUtils) {
            super(itemView);
            this.parseEventUtils = parseEventUtils;
            ButterKnife.bind(this, itemView);
            setData();
        }
        
        private void setData() {
            ButterKnife.apply(image, ButterKnifeUtils.setImageToImageView,parseEventUtils.getImageString());
            ButterKnife.apply(name, setTextToEditText, parseEventUtils.getTitle());
            ButterKnife.apply(price, setTextToEditText, parseEventUtils.getPrice() + "€");
            
            if (parseEventUtils.getParseEventRequestUtils().getStatus() != null &&
                    (parseEventUtils.getParseEventRequestUtils().getStatus().equals(Constants.STATUS_ACCEPT_STRING) ||
                            parseEventUtils.getParseEventRequestUtils().getStatus().equals(Constants.STATUS_PENDING_STRING)))
                
                ButterKnife.apply(address, setTextToEditText, parseEventUtils.getAddress()
                        + ", " + parseEventUtils.getFullAddress());
            
            else ButterKnife.apply(address, setTextToEditText, parseEventUtils.getAddress());
            
        }
        
        @OnClick(R.id.oneEventImage)
        void imageOnClick() {
            ButterKnife.apply(image, onEventClick, parseEventUtils);
        }
        
        @OnClick(R.id.eventCancel)
        void cancelEvent() {
            ButterKnife.apply(cancel, onEventDismiss, parseEventUtils);
        }
        
        final ButterKnife.Setter<ImageView, ParseEventUtils> onEventClick = new ButterKnife.Setter<ImageView, ParseEventUtils>() {
            
            @Override
            public void set(@NonNull final ImageView view, ParseEventUtils value, int index) {
                view.setClickable(false);
                
                pageAdapterInterface.addFragmentToUp(currentPositionOfPageAdapter,
                        EventDescriptionFragment.newInstance(parseEventUtils.getParseEventRequestUtils()));
                
                view.setClickable(true);
            }
        };
    }
}
