package pc.dd.liveapp.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pc.dd.liveapp.R;
import pc.dd.liveapp.data.parse.ParseUserUtils;
import pc.dd.liveapp.utils.ButterKnifeUtils;

/**
 * Created by leaditteam on 27.11.2017.
 */

public class FacebookUsersAdapter extends BaseAdapter {
    List <ParseUserUtils> parseUsersUtils;
    LayoutInflater inflater;
    public FacebookUsersAdapter(Context context,List <ParseUserUtils> parseUsersUtils) {
        this.parseUsersUtils = parseUsersUtils;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    public void setParseUsersUtils(List<ParseUserUtils> parseUsersUtils) {
        this.parseUsersUtils = parseUsersUtils;
    }
    
    @Override
    public int getCount() {
        return parseUsersUtils.size();
    }
    
    @Override
    public Object getItem(int position) {
        return parseUsersUtils.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return 0;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View root = inflater.inflate(R.layout.view_facebook_user, null);
        new FacebookUsersHolder(root,parseUsersUtils.get(position));
        return root;
    }

    public class FacebookUsersHolder {
        @BindView(R.id.facebookUserImage)
        ImageView userImage;
        @BindView(R.id.facebookUserName)
        TextView userName;
    
        ParseUserUtils parseUsersUtil;
        
        public FacebookUsersHolder(View root, ParseUserUtils parseUsersUtil) {
            ButterKnife.bind(this,root);
            this.parseUsersUtil = parseUsersUtil;
            ButterKnife.apply(userImage,ButterKnifeUtils.setImageToImageView,parseUsersUtil.getPhotoUrl());
            ButterKnife.apply(userName, ButterKnifeUtils.setTextToEditText,"inviter "+parseUsersUtil.getFullName());
        }
        
    }
}
