package com.emojimaker.creator.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.emojimaker.creator.R;
import com.emojimaker.creator.item.ItemPhoto;
import java.util.ArrayList;

public class PhotoShowPager extends PagerAdapter {
    private ArrayList<ItemPhoto> arrPhoto;
    private Context context;

    @Override 
    public int getItemPosition(Object obj) {
        return -2;
    }

    @Override 
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public PhotoShowPager(Context context, ArrayList<ItemPhoto> arrayList) {
        this.arrPhoto = arrayList;
        this.context = context;
    }

    @Override 
    public int getCount() {
        return this.arrPhoto.size();
    }

    @Override 
    public Object instantiateItem(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(this.context).inflate(R.layout.item_photo_pager, viewGroup, false);
        viewGroup.addView(inflate);
        ((ImageView) inflate.findViewById(R.id.im_item_photo)).setImageURI(Uri.parse(this.arrPhoto.get(i).getLink()));
        return inflate;
    }

    @Override 
    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        viewGroup.removeView((View) obj);
    }
}
