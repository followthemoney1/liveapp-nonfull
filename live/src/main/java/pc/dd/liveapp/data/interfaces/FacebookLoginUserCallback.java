package pc.dd.liveapp.data.interfaces;

import pc.dd.liveapp.data.parse.ParseUserUtils;

/**
 * Created by leaditteam on 07.09.17.
 */

public interface FacebookLoginUserCallback {
    void facebookLoginCallback(Boolean login, ParseUserUtils parseUserUtils);
}
