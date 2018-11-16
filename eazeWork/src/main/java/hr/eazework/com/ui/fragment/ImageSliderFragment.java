package hr.eazework.com.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import hr.eazework.Slider.customUI.RoundedCornersTransformations;
import hr.eazework.com.R;
import hr.eazework.com.model.AnnouncementItemsModel;
import hr.eazework.com.ui.util.AppsConstant;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageSliderFragment extends Fragment {

    private ImageView sliderImageIV;
    private TextView sliderTextTV;
    private AnnouncementItemsModel announcementItemsModel;
    private CardView cardViewAnnouncement;

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
        cardViewAnnouncement = (CardView) view.findViewById(R.id.cardViewAnnouncement);
        sliderImageIV = (ImageView) view.findViewById(R.id.sliderImageIV);
        sliderTextTV = (TextView) view.findViewById(R.id.sliderTextTV);
        sliderImageIV.setVisibility(View.GONE);
        sliderTextTV.setVisibility(View.GONE);
       // cardViewAnnouncement.setVisibility(View.GONE);
        if(announcementItemsModel.getType()!=null && !announcementItemsModel.getType().equalsIgnoreCase("") &&
                announcementItemsModel.getType().equalsIgnoreCase(AppsConstant.TEXT_ANNOUNCEMENT)){
         //   cardViewAnnouncement.setVisibility(View.VISIBLE);
            sliderTextTV.setVisibility(View.VISIBLE);
            sliderTextTV.setText(Html.fromHtml(announcementItemsModel.getDesc()));
        }else {
            sliderImageIV.setVisibility(View.VISIBLE);
            String UrlFile = "http://www.eazework.net/test";
            String imageUrl = UrlFile + announcementItemsModel.getFilePath().replace("..", "");
            // String imageUrl= announcementItemsModel.getFilePath();
            loadImage(sliderImageIV, imageUrl);
        }
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
