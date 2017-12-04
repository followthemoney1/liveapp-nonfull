package pc.dd.liveapp.utils;

import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.ButterKnife;

/**
 * Created by leaditteam on 09.11.17.
 */

public class ButterKnifeUtils {
    public final static ButterKnife.Setter<TextView, String> setTextToEditText = new ButterKnife.Setter<TextView, String>() {
        @Override
        public void set(@NonNull TextView view, String value, int index) {
            view.setText(value);
        }
    };
    
    public final static ButterKnife.Setter<ImageView, String> setImageToImageView = new ButterKnife.Setter<ImageView, String>() {
        @Override
        public void set(@NonNull ImageView imageView, String value, int index) {
            Glide.with(imageView.getContext())
                    .load(value)
                    .into(imageView);
        }
    };
}
