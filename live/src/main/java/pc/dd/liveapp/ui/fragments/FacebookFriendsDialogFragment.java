package pc.dd.liveapp.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import pc.dd.liveapp.R;
import pc.dd.liveapp.data.interfaces.FacebookFriendsDialogCallback;
import pc.dd.liveapp.data.interfaces.PaymentFragmentBackCallback;
import pc.dd.liveapp.data.parse.ParseUserUtils;
import pc.dd.liveapp.ui.adapters.FacebookUsersAdapter;
import pc.dd.liveapp.utils.UiHelper;

/**
 * Created by leaditteam on 27.11.2017.
 */

public class FacebookFriendsDialogFragment extends Fragment {
    @BindView(R.id.dialogListView)
    ListView dialogListView;
    
    FacebookUsersAdapter facebookUsersAdapter;
    FacebookFriendsDialogCallback facebookFriendsDialogCallback;
    PaymentFragmentBackCallback paymentFragmentBackCallback;
    
    HashMap<Integer,ParseUserUtils> parseUserUtilsBooleanHashMap = new HashMap<>();
    
    public static FacebookFriendsDialogFragment newInstance() {
        
        Bundle args = new Bundle();
        
        FacebookFriendsDialogFragment fragment = new FacebookFriendsDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_facebook_list, container, false);
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        init();
        callFacebookFriendsCallback();
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createCallback(context);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        createCallback(activity);
    }
    
    private void createCallback(Context context){
        if (context!=null) {
            facebookFriendsDialogCallback = (FacebookFriendsDialogCallback) context;
            paymentFragmentBackCallback = (PaymentFragmentBackCallback) context;
        }
    }
    
    private void init() {
        facebookUsersAdapter = new FacebookUsersAdapter(getActivity(), new ArrayList<ParseUserUtils>());
        dialogListView.setAdapter(facebookUsersAdapter);
    }
    
    private void callFacebookFriendsCallback() {
        FacebookDataHelper.createFacebookFriendRequest(new FacebookDataHelper.FacebookFriendsCallback() {
            @Override
            public void done(List<ParseUserUtils> list) {
                facebookUsersAdapter.setParseUsersUtils(list);
                facebookUsersAdapter.notifyDataSetChanged();
            }
            
            @Override
            public void cancel() {
                UiHelper.showToast(getActivity(),getString(R.string.toast_cant_load_you_friend_list));
            }
        });
    }
    
    @OnItemClick(R.id.dialogListView)
    void listViewOnItemClick(int position) {
        View root = dialogListView.getChildAt(position);
        if (((ColorDrawable)root.getBackground()).getColor()!=root.getResources().getColor(R.color.white)){
            root.setBackgroundColor(root.getResources().getColor(R.color.white));
            ((TextView)root.findViewById(R.id.facebookUserName)).setTextColor(root.getResources().getColor(R.color.black));
            parseUserUtilsBooleanHashMap.put(position, (ParseUserUtils) facebookUsersAdapter.getItem(position));
        } else {
            root.setBackgroundColor(root.getResources().getColor(R.color.background_alpha));
            ((TextView)root.findViewById(R.id.facebookUserName)).setTextColor(root.getResources().getColor(R.color.white));
            parseUserUtilsBooleanHashMap.remove(position);
        }
    
        facebookFriendsDialogCallback.listItemClicked(parseUserUtilsBooleanHashMap);
    }
    
    @OnClick(R.id.imageCloseDialog)
    void imageCloseDialogOnClick(){
        if (paymentFragmentBackCallback!=null)
            paymentFragmentBackCallback.onBackPressCallback();
    }
    
}
