package pc.dd.liveapp.ui.fragments;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pc.dd.liveapp.R;
import pc.dd.liveapp.ui.activities.LoginActivity;
import pc.dd.liveapp.utils.Constants;
import pc.dd.liveapp.data.parse.ParseUserUtils;
import pc.dd.liveapp.data.interfaces.PageAdapterCallback;
import pc.dd.liveapp.utils.UiHelper;

import static pc.dd.liveapp.ui.activities.LiveMainActivity.currentPositionOfPageAdapter;

/**
 * Created by leaditteam on 08.09.17.
 */

public class ProfileFragment extends Fragment {
    @BindView(R.id.nameUser)
    TextView name;
    @BindView(R.id.userAge)
    TextView age;
    @BindView(R.id.userAbout)
    TextView about;
    @BindView(R.id.logoutTemp)
    ImageView logoutBtn;
    @BindView(R.id.imageChangeProfileInformation)
    ImageView editBtn;
    @BindView(R.id.imageArrowBack)
    ImageView backBtn;
    @BindView(R.id.imageProfile)
    ImageView image;
    @BindView(R.id.userAddress)
    TextView address;
    
    ParseUserUtils parseUserUtils;
    
    Boolean currentUser = false;
    
    Boolean needRefreshViews = false;
    
    public static ProfileFragment newInstance(ParseUserUtils parseUserUtils) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.PARSE_USER_UTILS_EXTRA, parseUserUtils);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        getExtra();
        setViewsData(view);
    }
    
    private void getExtra() {
        parseUserUtils = (ParseUserUtils) getArguments().getSerializable(Constants.PARSE_USER_UTILS_EXTRA);
        if (parseUserUtils == null) {
            currentUser = true;
            parseUserUtils = (ParseUserUtils) ParseUserUtils.getCurrentUser();
        }
    }
    
    private void setViewsData(View root) {
        
        if (parseUserUtils != null) {
            Glide.with(root).load(parseUserUtils.getPhotoUrl()).into(image);
            name.setText(parseUserUtils.getFirstName());
            age.setText(String.valueOf(UiHelper.getUserAge(parseUserUtils)));
            about.setText(parseUserUtils.getAbout());
            address.setText(parseUserUtils.getAddress());
        }
        
        if (!currentUser) {
            backBtn.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.GONE);
            editBtn.setVisibility(View.GONE);
        }
        
        getChildFragmentManager().addOnBackStackChangedListener(fragmentManegerBackStackChangeListener);
    }
    
    @OnClick(R.id.logoutTemp)
    void logoutTempOnClick() {
        ParseUser.logOut();
        LoginManager.getInstance().logOut();
        getContext().startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }
    
    @OnClick(R.id.imageChangeProfileInformation)
    void imageChangeProfileInformationOnClick() {
        PageAdapterCallback pageAdapterInterface = (PageAdapterCallback) getActivity();
        pageAdapterInterface.addFragmentToUp(currentPositionOfPageAdapter, ChangeUserInformationFragment.newInstance());
    }
    
    @OnClick(R.id.imageArrowBack)
    void imageArrowBackOnClick() {
        getFragmentManager().popBackStack();
    }
    
    android.support.v4.app.FragmentManager.OnBackStackChangedListener fragmentManegerBackStackChangeListener = new android.support.v4.app.FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            if (needRefreshViews) {
                refreshThisView();
                needRefreshViews = false;
            } else needRefreshViews = true;
        }
    };
    
    @Override
    public void onResume() {
        refreshThisView();
        super.onResume();
    }
    
    private void refreshThisView() {
        parseUserUtils = ParseUserUtils.getParseUserUtilFromParseUser(parseUserUtils.getUser());
        if (getView() != null)
            setViewsData(getView());
    }
}
