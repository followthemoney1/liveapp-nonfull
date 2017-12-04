package pc.dd.liveapp.utils.pdf;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.hendrix.pdfmyxml.PdfDocument;
import com.hendrix.pdfmyxml.viewRenderer.AbstractViewRenderer;

import java.io.File;

import pc.dd.liveapp.R;

/**
 * Created by leaditteam on 07.11.17.
 */

public class PdfCreator {
    
    public PdfCreator(Context context) {
        createPdf( addPdfRender(context),context);
    }
    
    private AbstractViewRenderer addPdfRender(Context context){
        
        AbstractViewRenderer page = new AbstractViewRenderer(context, R.layout.view_pdf) {
            @Override
            protected void initView(View view) {
            }
        };
        
        page.setReuseBitmap(true);
//        TextView tv = page.getView().findViewById(R.id.pdfTextView);
//        tv.setText("kyrlik");
        page.render(1500,2400);
       
        return page;
    }
    
    private void createPdf(AbstractViewRenderer page, Context context){
        new PdfDocument.Builder(context)
                .addPage(page)
                .filename("test")
                .orientation(PdfDocument.A4_MODE.PORTRAIT)
                .progressMessage(R.string.accepter)
                .progressTitle(R.string.address_postcode_invalid)
                .renderWidth(1500)
                .renderHeight(2400)
                .listener(new PdfDocument.Callback() {
                    @Override
                    public void onComplete(File file) {
                        Log.i(PdfDocument.TAG_PDF_MY_XML, "Complete"+file.getAbsolutePath());
                    }
                
                    @Override
                    public void onError(Exception e) {
                        Log.i(PdfDocument.TAG_PDF_MY_XML, "Error");
                    }
                }).create().createPdf(context);
    }
    
}
