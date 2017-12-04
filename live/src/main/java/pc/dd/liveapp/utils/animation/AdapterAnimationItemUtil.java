package pc.dd.liveapp.utils.animation;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by leaditteam on 29.09.17.
 */

public class AdapterAnimationItemUtil extends Animation {
    
    int mFromHeight;
    int mToHeight;
    View mView;
    
    public AdapterAnimationItemUtil(View view, int fromHeight, int toHeight) {
        this.mView = view;
        this.mFromHeight = fromHeight;
        this.mToHeight = toHeight;
    }
    
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        int newHeight;
        
        if (mView.getHeight() != mToHeight) {
            newHeight = (int) (mFromHeight + ((mToHeight - mFromHeight) * interpolatedTime));
            mView.getLayoutParams().height = newHeight;
            mView.requestLayout();
        }
    }
    
    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }
    
    @Override
    public boolean willChangeBounds() {
        return true;
    }
}