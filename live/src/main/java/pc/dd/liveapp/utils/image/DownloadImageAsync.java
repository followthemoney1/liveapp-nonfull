package pc.dd.liveapp.utils.image;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import pc.dd.liveapp.ui.activities.RegistrationActivity;
import pc.dd.liveapp.utils.Constants;

/**
 * Created by leaditteam on 07.09.17.
 */

public class DownloadImageAsync extends AsyncTask<Void,Void,String> {
    
    private String urlToImage;
    private Context context;
    private String outputFile;
    private DownloadImageAsyncCallback downloadImageAsyncCallback;
    public interface DownloadImageAsyncCallback{
        void done(String url);
    }
    
    public DownloadImageAsync(String url, Context c,DownloadImageAsyncCallback downloadImageAsyncCallback ) {
        this.urlToImage = url;
        this.context = c;
        this.downloadImageAsyncCallback = downloadImageAsyncCallback;
    }
    
    @Override
    protected String doInBackground(Void... voids) {
        if (urlToImage!=null) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            outputFile = context.getExternalCacheDir().getAbsolutePath() + File.separator + "photo.jpg";
            try {
                URL fromString = new URL(urlToImage);
                connection = (HttpURLConnection) fromString.openConnection();
                connection.connect();
        
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
        
                input = connection.getInputStream();
                output = new FileOutputStream(outputFile);
        
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
        
                if (connection != null)
                    connection.disconnect();
            }
        }
        return null;
    }
    
    @Override
    protected void onPostExecute(String s) {
        downloadImageAsyncCallback.done(outputFile);
        super.onPostExecute(s);
    }
}
