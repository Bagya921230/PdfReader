package com.sample.pdfreader.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sample.pdfreader.Pdf;
import com.sample.pdfreader.R;

import java.util.ArrayList;

/**
 * Created by Suranga on 3/27/2017.
 */
public class PdfAdapter extends BaseAdapter {

    private ArrayList<Pdf> pdfs;
    private LayoutInflater pdfInf;

    public PdfAdapter(Context c, ArrayList<Pdf> thePdfs){

        pdfs = thePdfs;
        pdfInf = LayoutInflater.from(c);

    }

    @Override
    public int getCount() {
        return pdfs.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout pdfLay = (LinearLayout)pdfInf.inflate
                (R.layout.pdf, parent, false);

        TextView pdfView = (TextView)pdfLay.findViewById(R.id.pdf_name);

        Pdf currPdf = pdfs.get(position);
        //get title and artist strings
//        pdfView.setText("dsfsdfds");
        pdfView.setText(currPdf.getName());
        //set position as tag
        pdfLay.setTag(currPdf.getName());
        return pdfLay;
    }
}
