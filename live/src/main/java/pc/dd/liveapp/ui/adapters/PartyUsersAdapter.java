package pc.dd.liveapp.ui.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pc.dd.liveapp.R;
import pc.dd.liveapp.ui.activities.ChangeUserStatusActivity;
import pc.dd.liveapp.ui.fragments.ProfileFragment;
import pc.dd.liveapp.utils.ButterKnifeUtils;
import pc.dd.liveapp.utils.ColorUtils;
import pc.dd.liveapp.utils.Constants;
import pc.dd.liveapp.data.parse.ParseUserUtils;
import pc.dd.liveapp.utils.UiHelper;

/**
 * Created by leaditteam on 10.10.17.
 */

public class PartyUsersAdapter extends BaseAdapter {
    
    private  LayoutInflater inflater;
    private FragmentManager fragmentManager;
    
    private Boolean comeFromMainFragment;
    private String parseEventObjectId;
    private List<ParseUserUtils> parseUserUtilsList = new ArrayList<>();
    
    public PartyUsersAdapter(List<ParseUserUtils> parseUserUtilsList, Context context, FragmentManager fragmentManager, Boolean comeFromMainFragment, String parseEventObjectId) {
        this.parseUserUtilsList = parseUserUtilsList;
        this.fragmentManager = fragmentManager;
        this.comeFromMainFragment = comeFromMainFragment;
        this.parseEventObjectId = parseEventObjectId;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
    public int getCount() {
        return parseUserUtilsList.size();
    }
    
    @Override
    public Object getItem(int i) {
        return parseUserUtilsList.get(i);
    }
    
    @Override
    public long getItemId(int i) {
        return 0;
    }
    
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View root = view;
        if (root == null)
            root = inflater.inflate(R.layout.view_one_user, null);
        
        new PartyUsersHolder(root,parseUserUtilsList.get(i));
        
        return root;
    }
    
    public class PartyUsersHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.oneUserImage)
        ImageView image;
        @BindView(R.id.statusUserImage)
        ImageView statusUserImage;
        @BindView(R.id.nameUser)
        TextView name;
        @BindView(R.id.ageUser)
        TextView age;
        @BindView(R.id.addressUser)
        TextView address;
    
        ParseUserUtils parseUserUtils;
    
        public PartyUsersHolder(View itemView, ParseUserUtils parseUserUtils) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.parseUserUtils = parseUserUtils;
            setDataToView(itemView);
        }
    
        private void setDataToView(View root) {
        
            DisplayMetrics displayMetrics = root.getResources().getDisplayMetrics();
            int myHeight = displayMetrics.heightPixels / 3 - (displayMetrics.heightPixels / 20);
            int myWidth = displayMetrics.widthPixels / 2;
        
            image.getLayoutParams().height = myHeight;
            image.getLayoutParams().width = myWidth;
            
            ButterKnife.apply(image,ButterKnifeUtils.setImageToImageView,parseUserUtils.getPhotoUrl());
            ButterKnife.apply(name, ButterKnifeUtils.setTextToEditText,parseUserUtils.getFullName());
            ButterKnife.apply(age, ButterKnifeUtils.setTextToEditText,String.valueOf(UiHelper.getUserAge(parseUserUtils)));
            ButterKnife.apply(address, ButterKnifeUtils.setTextToEditText,parseUserUtils.getAddress());
            
            if (!comeFromMainFragment) {
                statusUserImage.setVisibility(View.VISIBLE);
                ColorUtils.drawImageByStatus(parseUserUtils.getStatusEvent(), statusUserImage, root.getContext());
            }
        }
        
        @OnClick(R.id.bottomLineUser)
        void setBottomLineUserOnClick(View view){
                if (parseUserUtils.getStatusEvent().equals(Constants.STATUS_PENDING_STRING)&&!comeFromMainFragment)
    
                    UiHelper
                            .openActivityDialog(
                                    parseUserUtils.getObjectId(),
                                    parseEventObjectId,
                                    null,
                                    null,
                                    0,
                                    0,
                                    view.getContext(),
                                    ChangeUserStatusActivity.class);
                else
                    UiHelper.showToast(view.getContext(), view.getContext().getString(R.string.toast_status_of_this_user_is_not_pending));
        }
        
        @OnClick(R.id.oneUserImage)
        void oneUserImageOnClick(){
            ProfileFragment profileFragment = ProfileFragment.newInstance(parseUserUtils);
            fragmentManager.beginTransaction()
                    .add(R.id.usersEventFragmentRoot,
                            profileFragment,
                            "fragmentSomeUserProfile")
                    .addToBackStack(profileFragment.getTag()).commit();
        }
    }
    
    
}
