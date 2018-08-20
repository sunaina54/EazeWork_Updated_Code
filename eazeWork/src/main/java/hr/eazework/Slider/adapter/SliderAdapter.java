package hr.eazework.Slider.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.util.ArrayList;
import java.util.List;

import hr.eazework.Slider.Slider;
import hr.eazework.Slider.customUI.RoundedCornersTransformations;
import hr.eazework.com.R;
import hr.eazework.com.model.AnnouncementItemsModel;




public class SliderAdapter extends PagerAdapter {

    private LayoutInflater layoutInflater;
    private AdapterView.OnItemClickListener itemClickListener;
   // private List<Slide> items = new ArrayList<>();
    private ArrayList<AnnouncementItemsModel> items = new ArrayList<>();

    public SliderAdapter(@NonNull Context context, ArrayList<AnnouncementItemsModel> items, AdapterView.OnItemClickListener itemClickListener) {
        this.items = items;
        this.itemClickListener = itemClickListener;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        Log.d("Size:",items.size()+"");
        return items.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
Log.d("Instance:","instantiate item called");
        View view = layoutInflater.inflate(R.layout.row_slider, container, false);
        ImageView sliderImage = (ImageView) view.findViewById(R.id.sliderImage);
        TextView announcementTV = (TextView) view.findViewById(R.id.announcementTV);
        announcementTV.setText(items.get(position).getDesc());
        String UrlFile="http://www.eazework.net/test";
        String imageUrl= UrlFile+items.get(position).getFilePath();
        //loadImage(sliderImage,imageUrl, items.get(position).getImageCorner());
        loadImage(sliderImage, imageUrl, items.get(position).getImageCorner());

        View parent = view.findViewById(R.id.ripple);
       /* parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null)
                    itemClickListener.onItemClick(null, null, position, 0);
            }
        });*/
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // The object returned by instantiateItem() is a key/identifier. This method checks whether
        // the View passed to it (representing the page) is associated with that key or not.
        // It is required by a PagerAdapter to function properly.
        return view == object;
    }

 /*   @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = layoutInflater.inflate(R.layout.row_slider, container, false);
        ImageView sliderImage = (ImageView) view.findViewById(R.id.sliderImage);
        TextView announcementTV = (TextView) view.findViewById(R.id.announcementTV);
        announcementTV.setText(items.get(position).getText());
        loadImage(sliderImage, items.get(position).getImageUrl(), items.get(position).getImageCorner());
        View parent = view.findViewById(R.id.ripple);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null)
                    itemClickListener.onItemClick(null, null, position, 0);
            }
        });
        container.addView(view);
        return view;
    }*/

   /* @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = layoutInflater.inflate(R.layout.row_slider, container, false);
        ImageView sliderImage = (ImageView) view.findViewById(R.id.sliderImage);
        TextView announcementTV = (TextView) view.findViewById(R.id.announcementTV);
        announcementTV.setText(items.get(position).getDesc());
        String UrlFile="http://www.eazework.net/test";
        String imageUrl= UrlFile+items.get(position).getFilePath();
        //loadImage(sliderImage,imageUrl, items.get(position).getImageCorner());
        loadImage(sliderImage, imageUrl, items.get(position).getImageCorner());

        View parent = view.findViewById(R.id.ripple);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null)
                    itemClickListener.onItemClick(null, null, position, 0);
            }
        });
        container.addView(view);
        return view;
    }
*/
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Removes the page from the container for the given position. We simply removed object using removeView()
        // but could’ve also used removeViewAt() by passing it the position.
        try {
            // Remove the view from the container
            container.removeView((View) object);
            // Invalidate the object
            object = null;
        } catch (Exception e) {
            Log.w(Slider.TAG, "destroyItem: failed to destroy item and clear it's used resources", e);
        }
    }

    /**
     * Display the gallery image into the image view provided.
     */
    private void loadImage(ImageView imageView, String url,int corner) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(imageView.getContext()) // Bind it with the context of the actual view used
                    .load(url) // Load the image
                    .bitmapTransform(new CenterCrop(imageView.getContext()), new RoundedCornersTransformations(imageView.getContext(), corner, 0, RoundedCornersTransformations.CornerType.ALL))
                    .animate(R.anim.fade_in) // need to manually set the animation as bitmap cannot use cross fade
                    .into(imageView);



        }
    }
}
