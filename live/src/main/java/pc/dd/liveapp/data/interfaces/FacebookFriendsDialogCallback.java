package pc.dd.liveapp.data.interfaces;

import java.util.HashMap;

import pc.dd.liveapp.data.parse.ParseUserUtils;

/**
 * Created by leaditteam on 27.11.2017.
 */

public interface FacebookFriendsDialogCallback {
    void listItemClicked(HashMap<Integer,ParseUserUtils> listClicked);
}
