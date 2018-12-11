package hr.eazework.com;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


import hr.eazework.com.model.GetQuickHelpSearchRequestModel;
import hr.eazework.com.model.GetQuickHelpSearchResponseModel;
import hr.eazework.com.model.QuickHelpListModel;
import hr.eazework.com.model.QuickHelpParamModel;

import hr.eazework.com.ui.util.AppsConstant;
import hr.eazework.com.ui.util.Preferences;
import hr.eazework.com.ui.util.Utility;
import hr.eazework.mframe.communication.ResponseData;
import hr.eazework.selfcare.communication.AppRequestJSONString;
import hr.eazework.selfcare.communication.CommunicationConstant;
import hr.eazework.selfcare.communication.CommunicationManager;

public class GetQuickHelpActivity extends BaseActivity {
    private Context context;
    private String currentScreen = "";
    private RecyclerView helpListRecyclerView;
    private LinearLayout nodataLL;
    private EditText searchET;
    private ImageView searchIV;
    private RelativeLayout backLayout;
    private Preferences preferences;
    private View progressbar;
    private String searchText = "";
    private GetQuickHelpSearchResponseModel getQuickHelpSearchResponseModel;
    private TextView noDataTV,tv_header_text,articlesTV;
    private QuickHelpAdapter quickHelpAdapter;
private LinearLayout mainLayout;
private Button helpCenterBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_quick_help);
        context = this;
        setupScreen();
    }

    private void setupScreen() {
        currentScreen = getIntent().getStringExtra("CurrentScreen");
        progressbar = (LinearLayout) findViewById(R.id.ll_progress_container);
        progressbar.bringToFront();
        preferences = new Preferences(context);
        int textColor = Utility.getTextColorCode(preferences);
        int bgColor = Utility.getBgColorCode(context, preferences);
        tv_header_text= (TextView) findViewById(R.id.tv_header_text);
        tv_header_text.setTextColor(textColor);
        mainLayout=(LinearLayout) findViewById(R.id.mainLayout);
        mainLayout.setBackgroundColor(bgColor);
        backLayout=(RelativeLayout)findViewById(R.id.backLayout);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        noDataTV = (TextView) findViewById(R.id.noDataTV);
        noDataTV.setText("No article found");

        articlesTV = (TextView) findViewById(R.id.articlesTV);
        helpListRecyclerView = (RecyclerView) findViewById(R.id.helpListRecyclerView);
        nodataLL = (LinearLayout) findViewById(R.id.nodataLL);
        searchET = (EditText) findViewById(R.id.searchET);
        searchIV = (ImageView) findViewById(R.id.searchIV);
        helpCenterBTN = (Button) findViewById(R.id.helpCenterBTN);

        Log.d("CurrentScreenHelp:",currentScreen);
        if (currentScreen != null && !currentScreen.equalsIgnoreCase("")) {
            getSearchArticlesList(searchText);
        }
        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentScreen != null && !currentScreen.equalsIgnoreCase("")) {
                    searchText = searchET.getText().toString();
                    getSearchArticlesList(searchText);
                    noDataTV.setText("No article found matching to " + searchText);
                    articlesTV.setText("Search Results");

                }
            }
        });

        helpCenterBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(getQuickHelpSearchResponseModel!=null && getQuickHelpSearchResponseModel.getGetQuickHelpSearchResult()!=null
                        && getQuickHelpSearchResponseModel.getGetQuickHelpSearchResult().getHomeLink()!=null
                        && !getQuickHelpSearchResponseModel.getGetQuickHelpSearchResult().getHomeLink().equalsIgnoreCase("")) {

                    String urlFile = CommunicationConstant.getMobileCareURl();
                    String link = urlFile + getQuickHelpSearchResponseModel.getGetQuickHelpSearchResult().getHomeLink().replace("..","");
                    Log.d("Link is:", link);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(browserIntent);
                }
            }
        });



    }

    private void getSearchArticlesList(String searchText) {
        progressbar.setVisibility(View.VISIBLE);
        GetQuickHelpSearchRequestModel requestModel = new GetQuickHelpSearchRequestModel();
        QuickHelpParamModel paramModel = new QuickHelpParamModel();
        paramModel.setID(currentScreen);
        paramModel.setPageNum("10");
        paramModel.setSearchString(searchText);
        requestModel.setQuickHelpParam(paramModel);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.quickHelpData(requestModel),
                CommunicationConstant.API_GET_QUICK_HELP_SEARCH, true);
    }


    public void validateResponse(ResponseData response) {
        //Utility.showHidePregress(progressbar, false);
        progressbar.setVisibility(View.GONE);
        switch (response.getRequestData().getReqApiId()) {
            case CommunicationConstant.API_GET_QUICK_HELP_SEARCH:
                String str = response.getResponseData();
                Log.d("TAG", "Attendance Response : " + str);
                helpListRecyclerView.setVisibility(View.GONE);
                nodataLL.setVisibility(View.VISIBLE);
                getQuickHelpSearchResponseModel = GetQuickHelpSearchResponseModel.create(str);
                if (getQuickHelpSearchResponseModel != null && getQuickHelpSearchResponseModel.getGetQuickHelpSearchResult() != null &&
                        getQuickHelpSearchResponseModel.getGetQuickHelpSearchResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)
                        && getQuickHelpSearchResponseModel.getGetQuickHelpSearchResult().getQuickHelpList().size() > 0) {
                    if (getQuickHelpSearchResponseModel.getGetQuickHelpSearchResult().getQuickHelpList().get(0) != null) {
                        helpListRecyclerView.setVisibility(View.VISIBLE);
                        nodataLL.setVisibility(View.GONE);
                        refresh(getQuickHelpSearchResponseModel.getGetQuickHelpSearchResult().getQuickHelpList());
                    }

                }
                break;

            default:
                break;
        }
        super.validateResponse(response);
    }



    private void refresh(ArrayList<QuickHelpListModel> getQuickHelpList) {

        nodataLL.setVisibility(View.GONE);
        if (getQuickHelpList != null && getQuickHelpList.size() <= 0) {
            nodataLL.setVisibility(View.VISIBLE);
            noDataTV.setText("No article found matching to " + searchText);
        }
        ArrayList<QuickHelpListModel> quickHelpListModels= Utility.prepareFilterListHelp(getQuickHelpList);
        quickHelpAdapter = new QuickHelpAdapter(quickHelpListModels);
        helpListRecyclerView.setAdapter(quickHelpAdapter);
        helpListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        quickHelpAdapter.notifyDataSetChanged();
    }



    private class QuickHelpAdapter extends
            RecyclerView.Adapter<QuickHelpAdapter.MyViewHolder> {
        private ArrayList<QuickHelpListModel> dataSet;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView helpSearchTV,seqNoTV;
            private RelativeLayout searchHelpRL;

            public MyViewHolder(View v) {
                super(v);
                helpSearchTV = (TextView) v.findViewById(R.id.helpSearchTV);
                seqNoTV = (TextView) v.findViewById(R.id.seqNoTV);
                seqNoTV.setVisibility(View.GONE);
                searchHelpRL = (RelativeLayout) v.findViewById(R.id.searchHelpRL);

            }

        }

        public void addAll(List<QuickHelpListModel> list) {

            dataSet.addAll(list);
            notifyDataSetChanged();
        }

        public QuickHelpAdapter(List<QuickHelpListModel> data) {
            this.dataSet = (ArrayList<QuickHelpListModel>) data;

        }

        @Override
        public QuickHelpAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.help_list_item, parent, false);
            //view.setOnClickListener(MainActivity.myOnClickListener);
            QuickHelpAdapter.MyViewHolder myViewHolder = new QuickHelpAdapter.MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final QuickHelpAdapter.MyViewHolder holder, final int listPosition) {
            final QuickHelpListModel item = dataSet.get(listPosition);
            if(item.getText()!=null && !item.getText().equalsIgnoreCase("")) {
                holder.helpSearchTV.setText(item.getText());
            }

            if(item.getSeqNo()!=null && !item.getSeqNo().equalsIgnoreCase("")) {
                holder.seqNoTV.setText(item.getSeqNo()+".");
            }


            holder.searchHelpRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(item.getLink()!=null && !item.getLink().equalsIgnoreCase("")){
                        //String urlFile = CommunicationConstant.UrlFile;
                        String urlFile = CommunicationConstant.getMobileCareURl();
                        String link=urlFile+item.getLink().replace("..", "");;
                        Log.d("Link is:",link);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        startActivity(browserIntent);
                    }
                }
            });


        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        public void clearDataSource() {
            dataSet.clear();
            notifyDataSetChanged();
        }
    }
}
