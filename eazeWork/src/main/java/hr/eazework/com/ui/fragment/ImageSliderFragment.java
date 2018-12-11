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
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import hr.eazework.Slider.customUI.RoundedCornersTransformations;
import hr.eazework.com.R;
import hr.eazework.com.model.AnnouncementItemsModel;
import hr.eazework.com.ui.util.AppsConstant;
import hr.eazework.selfcare.communication.CommunicationConstant;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageSliderFragment extends Fragment {

    private ImageView sliderImageIV;
    private TextView sliderTextTV;
    private AnnouncementItemsModel announcementItemsModel = null;
    private CardView cardViewAnnouncement;
    private WebView announcementWV;
    private String screen = "";

    public ImageSliderFragment() {
        // Required empty public constructor
    }

    public AnnouncementItemsModel getAnnouncementItemsModel() {
        return announcementItemsModel;
    }

    public void setAnnouncementItemsModel(AnnouncementItemsModel announcementItemsModel) {
        this.announcementItemsModel = announcementItemsModel;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
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
        announcementWV = (WebView) view.findViewById(R.id.announcementWV);
        // sliderTextTV = (TextView) view.findViewById(R.id.sliderTextTV);
        sliderImageIV.setVisibility(View.GONE);
        announcementWV.setVisibility(View.GONE);

        //  sliderTextTV.setVisibility(View.GONE);
        // cardViewAnnouncement.setVisibility(View.GONE);
    /*    if (!getScreen().equalsIgnoreCase("")
                && getScreen().equalsIgnoreCase("HomeScreen")) {*/
            if (announcementItemsModel.getType() != null && !announcementItemsModel.getType().equalsIgnoreCase("") &&
                    announcementItemsModel.getType().equalsIgnoreCase(AppsConstant.TEXT_ANNOUNCEMENT)) {
                //   cardViewAnnouncement.setVisibility(View.VISIBLE);
                announcementWV.setVisibility(View.VISIBLE);
                //  sliderTextTV.setVisibility(View.VISIBLE);
                //  sliderTextTV.setText(Html.fromHtml(announcementItemsModel.getDesc()));
                String testDesc = "<HTML>" + announcementItemsModel.getDesc() + "</HTML>";
                Log.d("Text-Announcement", testDesc);
                announcementWV.loadData(testDesc
                        , "text/html; charset=utf-8", "UTF-8");
            } else if (announcementItemsModel.getFilePath() != null &&
                    !announcementItemsModel.getFilePath().equalsIgnoreCase("")) {
                sliderImageIV.setVisibility(View.VISIBLE);
                //String UrlFile = CommunicationConstant.UrlFile;
                String UrlFile = CommunicationConstant.getMobileCareURl();
                String imageUrl = UrlFile + announcementItemsModel.getFilePath().replace("..", "");
                Log.d("image-ann", imageUrl);
                // announcementWV.loadUrl(imageUrl);
                loadImage(sliderImageIV, imageUrl);
            }
       // }

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
