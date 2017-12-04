package pc.dd.liveapp.data.stripe;

import com.google.gson.annotations.SerializedName;

/**
 * Created by leaditteam on 18.10.17.
 */

public class StripeCustomer {
    @SerializedName("id")
    String id;
    @SerializedName("email")
    String email;
    
    public String getId() {
        return id;
    }
    
    public String getEmail() {
        return email;
    }
}
