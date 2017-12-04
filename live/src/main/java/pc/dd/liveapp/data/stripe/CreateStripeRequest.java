package pc.dd.liveapp.data.stripe;

import android.content.Context;

import com.stripe.android.SourceCallback;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Source;
import com.stripe.android.model.SourceParams;
import com.stripe.android.model.Token;

import pc.dd.liveapp.data.interfaces.RetrofitGetStripeId;
import pc.dd.liveapp.data.parse.ParseUserUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by leaditteam on 10.11.17.
 */

public class CreateStripeRequest {
    
    private final String STRIPE_PUBLISH_KEY = "pk_test_IlqouQ9IeKSw7ai8ss5ZpTtA";
    
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://theliveapp.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    
    RetrofitGetStripeId retrofitGetStripeIdInterface = retrofit.create(RetrofitGetStripeId.class);
    
    StripeCustomer stripeCustomer;
    
    StripeCallback stripeCallback;
    
    Context context;
    
    public CreateStripeRequest(Context context, Card card , StripeCallback stripeCallback) {
        this.context = context;
        this.stripeCallback = stripeCallback;
        sentStripeRequest(card);
    }
    
    public interface StripeCallback{
        void success(String customerId, String sourceId);
        void error(Exception e);
    }
    
    private void sentStripeRequest(final Card card) {
        final Stripe stripe = new Stripe(context, STRIPE_PUBLISH_KEY);
        stripe.createToken(
                card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        createSourceId(card, stripe);
                    }
                    
                    public void onError(Exception error) {
                        stripeCallback.error(error);
                    }
                }
        );
    }
    
    private void createSourceId(Card card, Stripe stripe) {
        SourceParams cardSourceParams = SourceParams.createCardParams(card);
        stripe.createSource(
                cardSourceParams,
                new SourceCallback() {
                    @Override
                    public void onError(Exception error) {
                        stripeCallback.error(error);
                    }
                    
                    @Override
                    public void onSuccess(Source source) {
                        getCustomerIdFromServer(source.getId());
                    }
                });
        
    }
    
    private void getCustomerIdFromServer(final String sourceId) {
        ParseUserUtils parseUserUtils = ParseUserUtils.getCurrentUser();
        if (parseUserUtils != null)
            retrofitGetStripeIdInterface.createStripeCustomer(parseUserUtils.getEmail(), sourceId).enqueue(new Callback<StripeCustomer>() {
                @Override
                public void onResponse(Call<StripeCustomer> call, Response<StripeCustomer> response) {
                    stripeCustomer = response.body();
                    stripeCallback.success(stripeCustomer.getId(), sourceId);
                }
                
                @Override
                public void onFailure(Call<StripeCustomer> call, Throwable t) {
                    Exception exception = new Exception(t);
                    stripeCallback.error(exception);
                }
            });
    }
    
}
