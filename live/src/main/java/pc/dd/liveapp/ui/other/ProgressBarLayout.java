package pc.dd.liveapp.ui.other;

import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;


/**
 * Created by leaditteam on 24.10.17.
 */

public class ProgressBarLayout {
    private static ProgressBar progressBar;
    private static Boolean needShowDelayedProgressBar = true;
    
    public ProgressBarLayout(final ProgressBar progressBar) {
        this.progressBar = progressBar;
        progressBar.setClickable(true);
        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    
    public static void showProgressBar() {
        if (progressBar != null && progressBar.getVisibility() == View.GONE ) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    
    public static void dismissProgressBar() {
        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
    }
    
    public static void setCanDismissed(Boolean dismissed) {
        if (progressBar != null) {
            progressBar.setClickable(dismissed);
        }
    }
    
    public static void showProgressBarDalayed(int delay, final Boolean canDismiss ){
        needShowDelayedProgressBar = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (needShowDelayedProgressBar&&progressBar!=null&&progressBar.getVisibility() == View.GONE) {
                    showProgressBar();
                    setCanDismissed(canDismiss);
                }
            }
        },delay);
    }
    
    public static void dismissDelayedProgressBar(){
        needShowDelayedProgressBar = false;
    }
    
}
