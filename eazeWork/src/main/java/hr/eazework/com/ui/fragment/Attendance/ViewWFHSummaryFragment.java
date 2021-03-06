package hr.eazework.com.ui.fragment.Attendance;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import hr.eazework.com.MainActivity;
import hr.eazework.com.R;
import hr.eazework.com.model.GetEmpWFHResponseItem;
import hr.eazework.com.model.GetWFHRequestDetail;
import hr.eazework.com.model.MenuItemModel;
import hr.eazework.com.model.ModelManager;
import hr.eazework.com.model.RemarkListItem;
import hr.eazework.com.model.SupportDocsItemModel;
import hr.eazework.com.model.TourSummaryRequestDetail;
import hr.eazework.com.model.WFHRequestDetailItem;
import hr.eazework.com.model.WFHSummaryResponse;
import hr.eazework.com.model.WithdrawWFHResponse;
import hr.eazework.com.ui.adapter.DocumentUploadAdapter;
import hr.eazework.com.ui.adapter.RemarksAdapter;
import hr.eazework.com.ui.customview.CustomDialog;
import hr.eazework.com.ui.fragment.BaseFragment;
import hr.eazework.com.ui.interfaces.IAction;
import hr.eazework.com.ui.util.AppsConstant;
import hr.eazework.com.ui.util.Preferences;
import hr.eazework.com.ui.util.Utility;
import hr.eazework.com.ui.util.custom.AlertCustomDialog;
import hr.eazework.mframe.communication.ResponseData;
import hr.eazework.selfcare.communication.AppRequestJSONString;
import hr.eazework.selfcare.communication.CommunicationConstant;
import hr.eazework.selfcare.communication.CommunicationManager;

/**
 * Created by Dell3 on 11-12-2017.
 */

public class ViewWFHSummaryFragment extends BaseFragment {
    private Context context;
    public static final String TAG = "ViewWFHSummaryFragment";
    private String screenName = "ViewWFHSummaryFragment";
    private Preferences preferences;
    private TextView requestIdTV,empNameTV,statusTV,startDateTV,endDateTV,daysTV;
    private TextView submittedByTV,pendingWithTV;
    private LinearLayout remarksLinearLayout,wfhSummaryLl,tourSummaryLl,odSummaryLl,docLl;
    private RecyclerView remarksRV;
    private Button withdrawBTN;
    private LinearLayout errorLinearLayout;
    private DocumentUploadAdapter documentViewAdapter;
    private RecyclerView documentRV;
    private ImageView plus_create_newIV;
    private View progressbar;
    private GetEmpWFHResponseItem getEmpWFHResponseItem;

    public GetEmpWFHResponseItem getGetEmpWFHResponseItem() {
        return getEmpWFHResponseItem;
    }

    public void setGetEmpWFHResponseItem(GetEmpWFHResponseItem getEmpWFHResponseItem) {
        this.getEmpWFHResponseItem = getEmpWFHResponseItem;
    }

    private GetWFHRequestDetail requestDetail;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.setShowPlusMenu(true);
        this.setShowEditTeamButtons(false);
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_view_tour_summary, container, false);
        context = getContext();
        preferences = new Preferences(getContext());
        setupScreen();
        return rootView;
    }

    private void setupScreen(){
        context = getActivity();

        preferences = new Preferences(getContext());
        int textColor = Utility.getTextColorCode(preferences);
        ((TextView) getActivity().findViewById(R.id.tv_header_text)).setTextColor(textColor);
        ((TextView) ((MainActivity) getActivity()).findViewById(R.id.tv_header_text)).setText(R.string.view_tour_summary);
        ((MainActivity) getActivity()).findViewById(R.id.ibRight).setVisibility(View.GONE);
        ((MainActivity) getActivity()).findViewById(R.id.ibWrong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuItemModel menuItemModel = ModelManager.getInstance().getMenuItemModel();
                if (menuItemModel != null) {
                    MenuItemModel itemModel = menuItemModel.getItemModel(MenuItemModel.WORK_FROM_HOME);
                    if (itemModel != null && itemModel.isAccess()) {
                        mUserActionListener.performUserAction(IAction.WORK_FROM_HOME_SUMMARY, null, null);
                    } else {
                        mUserActionListener.performUserAction(IAction.WORK_FROM_HOME_SUMMARY, null, null);
                    }
                }
            }
        });
        progressbar = (LinearLayout) rootView.findViewById(R.id.ll_progress_container);
        progressbar.bringToFront();
        documentRV = (RecyclerView) rootView.findViewById(R.id.expenseRecyclerView);
        errorLinearLayout = (LinearLayout) rootView.findViewById(R.id.errorDocTV);
        errorLinearLayout.setVisibility(View.VISIBLE);

        docLl= (LinearLayout) rootView.findViewById(R.id.docLl);
        plus_create_newIV= (ImageView) rootView.findViewById(R.id.plus_create_newIV);
        plus_create_newIV.setVisibility(View.GONE);
        wfhSummaryLl= (LinearLayout) rootView.findViewById(R.id.wfhSummaryLl);
        wfhSummaryLl.setVisibility(View.VISIBLE);
        tourSummaryLl= (LinearLayout) rootView.findViewById(R.id.tourSummaryLl);
        tourSummaryLl.setVisibility(View.GONE);
        odSummaryLl= (LinearLayout) rootView.findViewById(R.id.odSummaryLl);
        odSummaryLl.setVisibility(View.GONE);

        remarksLinearLayout= (LinearLayout) rootView.findViewById(R.id.remarksLinearLayout);
        remarksRV= (RecyclerView) rootView.findViewById(R.id.remarksRV);

        //First
        requestIdTV= (TextView) rootView.findViewById(R.id.requestIdTV);
        empNameTV= (TextView) rootView.findViewById(R.id.empNameTV);
        statusTV= (TextView) rootView.findViewById(R.id.statusTV);
        submittedByTV= (TextView) rootView.findViewById(R.id.submittedByTV);
        pendingWithTV= (TextView) rootView.findViewById(R.id.pendingWithTV);
        startDateTV= (TextView) rootView.findViewById(R.id.startDateTV);
        endDateTV= (TextView) rootView.findViewById(R.id.endDateTV);
        daysTV= (TextView) rootView.findViewById(R.id.daysTV);

        withdrawBTN= (Button) rootView.findViewById(R.id.withdrawBTN);
        withdrawBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendWithdrawRequestData();
            }
        });

        sendViewRequestSummaryData();



    }

    private void sendViewRequestSummaryData() {
        requestDetail = new GetWFHRequestDetail();
        requestDetail.setReqID(getEmpWFHResponseItem.getReqID());
        requestDetail.setAction(AppsConstant.VIEW_ACTION);
        Utility.showHidePregress(progressbar, true);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.WFHSummaryDetails(requestDetail),
                CommunicationConstant.API_GET_WFH_REQUEST_DETAIL, true);
    }

    private void sendWithdrawRequestData() {
        requestDetail = new GetWFHRequestDetail();
        requestDetail.setReqStatus(getEmpWFHResponseItem.getStatus());
        requestDetail.setReqID(getEmpWFHResponseItem.getReqID());
        Utility.showHidePregress(progressbar, true);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.WFHSummaryDetails(requestDetail),
                CommunicationConstant.API_WITHDRAW_WFH_REQUEST, true);
    }

    @Override
    public void validateResponse(ResponseData response) {
        Utility.showHidePregress(progressbar, false);
        switch (response.getRequestData().getReqApiId()) {
            case CommunicationConstant.API_GET_WFH_REQUEST_DETAIL:
                String str = response.getResponseData();
                Log.d("TAG", "WFH Response : " + str);
                WFHSummaryResponse wfhSummaryResponse = WFHSummaryResponse.create(str);
                if (wfhSummaryResponse != null && wfhSummaryResponse.getGetWFHRequestDetailResult() != null
                        && wfhSummaryResponse.getGetWFHRequestDetailResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)
                        && wfhSummaryResponse.getGetWFHRequestDetailResult().getWFHRequestDetail()!=null) {
                    updateUI(wfhSummaryResponse.getGetWFHRequestDetailResult().getWFHRequestDetail());
                    refreshRemarksList(wfhSummaryResponse.getGetWFHRequestDetailResult().getWFHRequestDetail().getRemarkList());
                    refreshDocumentList(wfhSummaryResponse.getGetWFHRequestDetailResult().getWFHRequestDetail().getAttachments());
                }

                break;
            case CommunicationConstant.API_WITHDRAW_WFH_REQUEST:
                String strResponse = response.getResponseData();
                Log.d("TAG", "WFH Response : " + strResponse);
                WithdrawWFHResponse withdrawWFHResponse = WithdrawWFHResponse.create(strResponse);
                if (withdrawWFHResponse != null && withdrawWFHResponse.getWithdrawWFHRequestResult() != null
                        && withdrawWFHResponse.getWithdrawWFHRequestResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                    CustomDialog.alertOkWithFinishFragment(context, withdrawWFHResponse.getWithdrawWFHRequestResult().getErrorMessage(), mUserActionListener, IAction.HOME_VIEW, true);
                }else {
                    new AlertCustomDialog(getActivity(), withdrawWFHResponse.getWithdrawWFHRequestResult().getErrorMessage());
                }
                break;
            default:
                break;
        }
        super.validateResponse(response);
    }

    private void updateUI(WFHRequestDetailItem item){
        submittedByTV.setText(item.getSubmittedBy());
        pendingWithTV.setText(item.getPendWithName());
        requestIdTV.setText(item.getReqCode());
        empNameTV.setText(item.getForEmpName());
        statusTV.setText(item.getStatusDesc());
        startDateTV.setText(item.getStartDate());
        endDateTV.setText(item.getEndDate());
        daysTV.setText(item.getTotalDays()+" " + "day(s)");
        setupButtons(item);
    }

    private void refreshRemarksList(ArrayList<RemarkListItem> remarksItems) {
        if (remarksItems != null && remarksItems.size() > 0) {
            remarksLinearLayout.setVisibility(View.GONE);
            remarksRV.setLayoutManager(new LinearLayoutManager(getActivity()));
            remarksRV.setVisibility(View.VISIBLE);
            RemarksAdapter adapter = new RemarksAdapter(remarksItems,context,screenName,remarksLinearLayout);
            remarksRV.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            remarksLinearLayout.setVisibility(View.VISIBLE);
            remarksRV.setVisibility(View.GONE);
        }
    }

    private void refreshDocumentList(ArrayList<SupportDocsItemModel> docListModels) {
        if (docListModels != null && docListModels.size() > 0) {
            errorLinearLayout.setVisibility(View.GONE);
            documentRV.setLayoutManager(new LinearLayoutManager(getActivity()));
            documentRV.setVisibility(View.VISIBLE);
            documentViewAdapter = new DocumentUploadAdapter(docListModels,context,AppsConstant.VIEW,errorLinearLayout,getActivity());
            documentRV.setAdapter(documentViewAdapter);
            documentViewAdapter.notifyDataSetChanged();
        } else {
            errorLinearLayout.setVisibility(View.VISIBLE);
            documentRV.setVisibility(View.GONE);
        }
    }

    private void setupButtons(WFHRequestDetailItem item){
        if(item.getButtons()!=null){
            for(String button : item.getButtons() ){
                if(button.equalsIgnoreCase(AppsConstant.WITHDRAW)){
                    withdrawBTN.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
