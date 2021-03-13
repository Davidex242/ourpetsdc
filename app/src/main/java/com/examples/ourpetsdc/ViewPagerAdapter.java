package com.examples.ourpetsdc;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.NotNull;

public class ViewPagerAdapter extends PagerAdapter {
    private final Context context;
    private LayoutInflater layoutInflater;
    private final int[] images = {R.drawable.wishkas,R.drawable.royal,R.drawable.goldpet};
    private Dialog popUpAdds;
    private WebView webView;
    public ViewPagerAdapter(Context context) {
        this.context = context;
    }
    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = layoutInflater.inflate(R.layout.custom_layout_images, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setImageResource(images[position]);
        iniPopUp();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position==0){
                    webView.loadUrl("https://www.whiskas.pt/");
                    webView.showContextMenu();
                    popUpAdds.show();
                }else if (position==1){
                    webView.loadUrl("https://www.royalcanin.com/pt");
                    webView.showContextMenu();
                    popUpAdds.show();
                }else if (position==2){
                    webView.loadUrl("https://goldpet.pt/");
                    webView.showContextMenu();
                    popUpAdds.show();
                }
            }
        });
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(@NotNull ViewGroup container, int position, @NotNull Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }

    private void iniPopUp() {

        popUpAdds = new Dialog(context);
        popUpAdds.setContentView(R.layout.popupadd);
        popUpAdds.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popUpAdds.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popUpAdds.getWindow().getAttributes().gravity = Gravity.TOP;
        webView = popUpAdds.findViewById(R.id.webAdds);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setDomStorageEnabled(true);

        //webView.loadUrl("http://www.teluguoneradio.com/rssHostDescr.php?hostId=147");

    }
}
