package hr.eazework.com.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import hr.eazework.Slider.customUI.RoundedCornersTransformations;
import hr.eazework.com.R;
import hr.eazework.com.model.AnnouncementItemsModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageSliderFragment extends Fragment {

    private ImageView sliderImageIV;
    private AnnouncementItemsModel announcementItemsModel;

    public ImageSliderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_slider, container, false);
        setupScreen(view);
        return view;
    }

    private void setupScreen(View view) {
        sliderImageIV = (ImageView) view.findViewById(R.id.sliderImageIV);
        String UrlFile="http://www.eazework.net/test";
        String imageUrl= UrlFile+announcementItemsModel.getFilePath().replace("..","");
        loadImage(sliderImageIV,imageUrl);
    }

    public AnnouncementItemsModel getAnnouncementItemsModel() {
        return announcementItemsModel;
    }

    public void setAnnouncementItemsModel(AnnouncementItemsModel announcementItemsModel) {
        this.announcementItemsModel = announcementItemsModel;
    }

    private void loadImage(ImageView imageView, String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(imageView.getContext()) // Bind it with the context of the actual view used
                    .load(url) // Load the image
                    .bitmapTransform(new CenterCrop(imageView.getContext()), new RoundedCornersTransformations(imageView.getContext(), 0, 0, RoundedCornersTransformations.CornerType.ALL))
                    .animate(R.anim.fade_in) // need to manually set the animation as bitmap cannot use cross fade
                    .into(imageView);
        }
    }

}
