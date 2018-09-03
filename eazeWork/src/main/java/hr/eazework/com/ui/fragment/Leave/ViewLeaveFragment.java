package hr.eazework.com.ui.fragment.Leave;

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
import hr.eazework.com.model.EmployeeLeaveModel;
import hr.eazework.com.model.GetEmpWFHResponseItem;
import hr.eazework.com.model.GetWFHRequestDetail;
import hr.eazework.com.model.LeaveDetailResponseModel;
import hr.eazework.com.model.LeaveModel;
import hr.eazework.com.model.LeaveReqsItem;
import hr.eazework.com.model.LeaveRequestDetailsModel;
import hr.eazework.com.model.MenuItemModel;
import hr.eazework.com.model.ModelManager;
import hr.eazework.com.model.RemarkListItem;
import hr.eazework.com.model.SupportDocsItemModel;
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
 * Created by Dell3 on 12-02-2018.
 */

public class ViewLeaveFragment extends BaseFragment {

    private Context context;
    public static final String TAG = "ViewLeaveFragment";
    private String screenName = "ViewLeaveFragment";
    private Preferences preferences;
    private TextView requestIdTV, empNameTV, statusTV, startDateTV, endDateTV, daysTV, dateWorkedTV;
    private TextView submittedByTV, pendingWithTV;
    private LinearLayout remarksLinearLayout, wfhSummaryLl, tourSummaryLl, odSummaryLl, docLl;
    private RecyclerView remarksRV;
    private Button withdrawBTN, clickBTN;
    private LinearLayout errorLinearLayout;
    private DocumentUploadAdapter documentViewAdapter;
    private RecyclerView documentRV;
    private ImageView plus_create_newIV;
    private EmployeeLeaveModel employeeLeaveModel;
    private LeaveReqsItem leaveReqsItem;
    private LinearLayout dateWorkedLl;
    private LeaveRequestDetailsModel leaveRequestDetailsModel;
    private View progressbar;
    private GetWFHRequestDetail requestDetail;

    public LeaveReqsItem getLeaveReqsItem() {
        return leaveReqsItem;
    }

    public void setLeaveReqsItem(LeaveReqsItem leaveReqsItem) {
        this.leaveReqsItem = leaveReqsItem;
    }

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

    private void setupScreen() {
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
        documentRV = (RecyclerView) rootView.findViewById(R.id.expenseRecyclerView);
        errorLinearLayout = (LinearLayout) rootView.findViewById(R.id.errorDocTV);
        errorLinearLayout.setVisibility(View.VISIBLE);
        progressbar = (LinearLayout) rootView.findViewById(R.id.ll_progress_container);
        progressbar.bringToFront();
        docLl = (LinearLayout) rootView.findViewById(R.id.docLl);
        plus_create_newIV = (ImageView) rootView.findViewById(R.id.plus_create_newIV);
        plus_create_newIV.setVisibility(View.GONE);
        wfhSummaryLl = (LinearLayout) rootView.findViewById(R.id.wfhSummaryLl);
        wfhSummaryLl.setVisibility(View.VISIBLE);
        tourSummaryLl = (LinearLayout) rootView.findViewById(R.id.tourSummaryLl);
        tourSummaryLl.setVisibility(View.GONE);
        odSummaryLl = (LinearLayout) rootView.findViewById(R.id.odSummaryLl);
        odSummaryLl.setVisibility(View.GONE);

        remarksLinearLayout = (LinearLayout) rootView.findViewById(R.id.remarksLinearLayout);
        remarksRV = (RecyclerView) rootView.findViewById(R.id.remarksRV);

        //First
        requestIdTV = (TextView) rootView.findViewById(R.id.requestIdTV);
        empNameTV = (TextView) rootView.findViewById(R.id.empNameTV);
        statusTV = (TextView) rootView.findViewById(R.id.statusTV);
        dateWorkedLl = (LinearLayout) rootView.findViewById(R.id.dateWorkedLl);
        dateWorkedTV = (TextView) rootView.findViewById(R.id.dateWorkedTV);
        submittedByTV = (TextView) rootView.findViewById(R.id.submittedByTV);
        pendingWithTV = (TextView) rootView.findViewById(R.id.pendingWithTV);
        startDateTV = (TextView) rootView.findViewById(R.id.startDateTV);
        endDateTV = (TextView) rootView.findViewById(R.id.endDateTV);
        daysTV = (TextView) rootView.findViewById(R.id.daysTV);

       /* clickBTN = (Button) rootView.findViewById(R.id.clickBTN);
        clickBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leaveReqsItem != null && leaveReqsItem.getReqID() != null) {
                    sendViewLeaveRequestSummaryData(leaveReqsItem);
                }
            }
        });*/
        withdrawBTN = (Button) rootView.findViewById(R.id.withdrawBTN);
        withdrawBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendWithdrawRequestData();
            }
        });

        if (leaveReqsItem != null && leaveReqsItem.getReqID() != null) {
            sendViewLeaveRequestSummaryData(leaveReqsItem);
        }


    }

    private void sendViewLeaveRequestSummaryData(LeaveReqsItem item) {
        requestDetail = new GetWFHRequestDetail();
        requestDetail.setReqID(item.getReqID());
        requestDetail.setAction(AppsConstant.VIEW_ACTION);
        Utility.showHidePregress(progressbar, true);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.WFHSummaryDetails(requestDetail),
                CommunicationConstant.API_GET_LEAVE_REQUEST_DETAIL, true);
    }


    private void sendWithdrawRequestData() {
        GetWFHRequestDetail requestDetail = new GetWFHRequestDetail();
        requestDetail.setReqStatus(leaveRequestDetailsModel.getReqStatus() + "");
        requestDetail.setReqID(String.valueOf(leaveRequestDetailsModel.getReqID()));
        Utility.showHidePregress(progressbar, true);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.WFHSummaryDetails(requestDetail),
                CommunicationConstant.API_WITHDRAW_LEAVE_REQUEST, true);
    }

    @Override
    public void validateResponse(ResponseData response) {
        Utility.showHidePregress(progressbar, false);
        switch (response.getRequestData().getReqApiId()) {
            case CommunicationConstant.API_GET_LEAVE_REQUEST_DETAIL:
                String leaveResp = response.getResponseData();
                Log.d("TAG", "WFH Response : " + leaveResp);
                LeaveDetailResponseModel leaveDetailResponseModel = LeaveDetailResponseModel.create(leaveResp);
                if (leaveDetailResponseModel != null && leaveDetailResponseModel.getGetLeaveRequestDetailsResult() != null
                        && leaveDetailResponseModel.getGetLeaveRequestDetailsResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)
                        && leaveDetailResponseModel.getGetLeaveRequestDetailsResult().getLeaveRequestDetails() != null) {
                    leaveRequestDetailsModel = leaveDetailResponseModel.getGetLeaveRequestDetailsResult().getLeaveRequestDetails();
                    updateUI(leaveRequestDetailsModel);
                    refreshRemarksList(leaveRequestDetailsModel.getRemarkList());
                    refreshDocumentList(leaveRequestDetailsModel.getAttachments());
                }

                break;
            case CommunicationConstant.API_WITHDRAW_LEAVE_REQUEST:
                String strResponse = response.getResponseData();
                Log.d("TAG", "Leave Withdraw Response : " + strResponse);
                WithdrawWFHResponse withdrawWFHResponse = WithdrawWFHResponse.create(strResponse);
                if (withdrawWFHResponse != null && withdrawWFHResponse.getWithdrawLeaveRequestResult() != null
                        && withdrawWFHResponse.getWithdrawLeaveRequestResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                    CustomDialog.alertOkWithFinishFragment(context, withdrawWFHResponse.getWithdrawLeaveRequestResult().getErrorMessage(), mUserActionListener, IAction.HOME_VIEW, true);
                } else {
                    new AlertCustomDialog(getActivity(), withdrawWFHResponse.getWithdrawLeaveRequestResult().getErrorMessage());
                }
                break;
            default:
                break;
        }
        super.validateResponse(response);
    }

    private void updateUI(LeaveRequestDetailsModel item) {
        dateWorkedLl.setVisibility(View.GONE);
        if (item.getCompOffLeaveWithStep1YN().equalsIgnoreCase("Y")) {
            dateWorkedLl.setVisibility(View.VISIBLE);
            dateWorkedTV.setText(item.getStartDate());
            startDateTV.setText(item.getEndDate());
            endDateTV.setText(item.getEndDate());
        } else {
            dateWorkedLl.setVisibility(View.GONE);
            startDateTV.setText(item.getStartDate());
            endDateTV.setText(item.getEndDate());
        }
        submittedByTV.setText(item.getSubmittedBy());
        pendingWithTV.setText(item.getPendWithName());
        requestIdTV.setText(item.getReqCode());
        empNameTV.setText(item.getForEmpName());
        statusTV.setText(item.getStatusDesc());


        String duration = item.getTotalDays();
        if (item.getHalfDayFS() != null && item.getHalfDayFS().equalsIgnoreCase("F")) {
            duration = duration + " (First Half)";
        } else if (item.getHalfDayFS() != null && item.getHalfDayFS().equalsIgnoreCase("S")) {
            duration = duration + " (Second Half)";
        }
        daysTV.setText(duration);

        setupButtons(item);
    }

    private void refreshRemarksList(ArrayList<RemarkListItem> remarksItems) {
        if (remarksItems != null && remarksItems.size() > 0) {
            remarksLinearLayout.setVisibility(View.GONE);
            remarksRV.setLayoutManager(new LinearLayoutManager(getActivity()));
            remarksRV.setVisibility(View.VISIBLE);
            RemarksAdapter adapter = new RemarksAdapter(remarksItems, context, screenName, remarksLinearLayout);
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
            documentViewAdapter = new DocumentUploadAdapter(docListModels, context, AppsConstant.VIEW, errorLinearLayout, getActivity());
            documentRV.setAdapter(documentViewAdapter);
            documentViewAdapter.notifyDataSetChanged();
        } else {
            errorLinearLayout.setVisibility(View.VISIBLE);
            documentRV.setVisibility(View.GONE);
        }
    }

    private void setupButtons(LeaveRequestDetailsModel item) {
        if (item.getButtons() != null) {
            for (String button : item.getButtons()) {
                if (button.equalsIgnoreCase(AppsConstant.WITHDRAW)) {
                    withdrawBTN.setVisibility(View.VISIBLE);
                }
            }
        }
    }


}
