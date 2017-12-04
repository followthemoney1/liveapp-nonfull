package pc.dd.liveapp.data.interfaces;

import pc.dd.liveapp.data.stripe.StripeCustomer;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by leaditteam on 18.10.17.
 */

public interface RetrofitGetStripeId {
    @POST("create_customer")
    Call<StripeCustomer> createStripeCustomer(@Header("email") String email,@Header("source") String source );
}
