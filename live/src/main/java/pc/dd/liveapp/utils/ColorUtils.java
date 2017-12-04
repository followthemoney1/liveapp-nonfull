package pc.dd.liveapp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import pc.dd.liveapp.R;

/**
 * Created by leaditteam on 08.09.17.
 */

public class ColorUtils {
    public ColorUtils() {
    }
    
    public static void onMenuItemSelected(ImageView image, int firstColor, int secondColor) {
        Bitmap myBitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        
        Bitmap newBitmap = addGradient(myBitmap,firstColor,secondColor);
        image.setImageDrawable(new BitmapDrawable(image.getContext().getResources(), newBitmap));
    }
    
    public static void setIconsColorDefault(int color, Activity activity, int... objects ){
        for (int i = 0; i<objects.length; i++){
            onMenuItemSelected((ImageView) activity.findViewById(objects[i]),color,color);
        }
    }
    
    
    private static Bitmap addGradient(Bitmap originalBitmap, int firstColor, int secondColor) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        Bitmap updatedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(updatedBitmap);
        
        canvas.drawBitmap(originalBitmap, 0, 0, null);
        
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, 0, 0, height, firstColor, secondColor, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawRect(0, 0, width, height, paint);
        
        return updatedBitmap;
    }
    
    public static void setBitmapFromImageWithInnerColor(ImageView image, int color) {
        Bitmap myBitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        
        Bitmap newBitmap = addColor(myBitmap,color);
        image.setImageDrawable(new BitmapDrawable(image.getContext().getResources(), newBitmap));
    }
    
    
    private static Bitmap addColor(Bitmap originalBitmap,int  color) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        Bitmap updatedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(updatedBitmap);
        
        canvas.drawBitmap(originalBitmap, 0, 0, null);
        
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, 0, 0, height, color, color, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawRect(0, 0, width, height, paint);
        
        return updatedBitmap;
    }
    
    public static void drawImageByStatus(String status, ImageView image, Context context) {
        if (status.equals(Constants.STATUS_ACCEPT_STRING)) {
            image.setColorFilter(context.getResources().getColor(R.color.green_status));
        } else if (status.equals(Constants.STATUS_REJECTED_STRING)) {
            image.setColorFilter(context.getResources().getColor(R.color.red_status));
        } else if (status.equals(Constants.STATUS_PENDING_STRING)) {
            image.setColorFilter(context.getResources().getColor(R.color.menu_default));
        }
    }
}
