package hr.eazework.com.ui.fragment.Attendance;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hr.eazework.com.BackdatedAttendanceActivity;
import hr.eazework.com.R;
import hr.eazework.com.TimeModificationActivity;
import hr.eazework.com.model.AttendanceApprovalResponse;
import hr.eazework.com.model.AttendanceItem;
import hr.eazework.com.model.AttendanceRejectItem;
import hr.eazework.com.model.AttendanceRejectRequestModel;
import hr.eazework.com.model.AttendanceRejectResponseModel;
import hr.eazework.com.model.AttendanceReqDetail;
import hr.eazework.com.model.ExpenseApprovalList;
import hr.eazework.com.model.GetODRequestDetail;
import hr.eazework.com.model.GetTimeModificationRequestDetail;
import hr.eazework.com.model.GetWFHRequestDetail;
import hr.eazework.com.model.LeaveRejectResponseModel;
import hr.eazework.com.model.ODRequestDetail;
import hr.eazework.com.model.ODRequestModel;
import hr.eazework.com.model.ODResponseModel;
import hr.eazework.com.model.ODSummaryResponse;
import hr.eazework.com.model.OdReqDetailModel;
import hr.eazework.com.model.PartialDayDataModel;
import hr.eazework.com.model.PartialDayModel;
import hr.eazework.com.model.TimeModificationItem;
import hr.eazework.com.model.TimeModificationRequestModel;
import hr.eazework.com.model.TimeModificationSummaryResponseModel;
import hr.eazework.com.model.TourReasonListModel;
import hr.eazework.com.model.TourReqDetail;
import hr.eazework.com.model.TourRequestModel;
import hr.eazework.com.model.TourResponseModel;
import hr.eazework.com.model.TourSummaryRequestDetail;
import hr.eazework.com.model.TourSummaryResponse;
import hr.eazework.com.model.WFHRejectRequestModel;
import hr.eazework.com.model.WFHRequestDetailItem;
import hr.eazework.com.model.WFHRequestDetailModel;
import hr.eazework.com.model.WFHRequestModel;
import hr.eazework.com.model.WFHResponseModel;
import hr.eazework.com.model.WFHSummaryResponse;
import hr.eazework.com.ui.customview.CustomBuilder;
import hr.eazework.com.ui.fragment.BaseFragment;
import hr.eazework.com.ui.interfaces.IAction;
import hr.eazework.com.ui.util.AppsConstant;
import hr.eazework.com.ui.util.Utility;
import hr.eazework.com.ui.util.custom.AlertCustomDialog;
import hr.eazework.mframe.communication.ResponseData;
import hr.eazework.selfcare.communication.AppRequestJSONString;
import hr.eazework.selfcare.communication.CommunicationConstant;
import hr.eazework.selfcare.communication.CommunicationManager;

/**
 * Created by Dell3 on 12-02-2018.
 */

public class AttendanceApprovalFragment extends BaseFragment {

    private Context context;
    public static final String TAG = "AttendanceApprovalFragment";
    private RecyclerView expenseApprovalRecyclerView;
    public static final String screenName = "PendingActivityFragment";
    private AttendanceApprovalAdapter AttendanceApprovalAdapter;
    private String comments;
    private ArrayList<ExpenseApprovalList> filterExpenseApprovalList;

    private TimeModificationSummaryResponseModel summaryResponseModel;
    private WFHRequestDetailItem wfhRequest;
    private ODRequestDetail odRequestDetailResult;
    private TourSummaryRequestDetail tourSummaryRequestDetail;
    private AttendanceReqDetail reqDetail;
    private GetWFHRequestDetail requestDetail;
    private String status;
    private RelativeLayout search_layout;
    private LinearLayout searchParentLayout, errorLinearLayout;
    private ArrayList<AttendanceItem> searchExpenseList;
    private EditText searchET;
    private ImageView searchIV, filterIV, searchCancelIV,clearTextIV;
    private String searchTag = null;
    private AttendanceApprovalResponse attendanceApprovalResponse;
    private RelativeLayout norecordLayout;
    private View progressbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.attendance_approval_fragment, container, false);
        context = getContext();
        setupScreen(rootView);
        return rootView;
    }

    private void setupScreen(View view) {
        progressbar = (LinearLayout) view.findViewById(R.id.ll_progress_container);
        progressbar.bringToFront();
        searchExpenseList = new ArrayList<>();
        search_layout = (RelativeLayout) view.findViewById(R.id.search_layout);
        search_layout.setVisibility(View.INVISIBLE);
        searchET = (EditText) view.findViewById(R.id.searchET);
        searchParentLayout = (LinearLayout) view.findViewById(R.id.searchParentLayout);
        searchParentLayout.setVisibility(View.GONE);
        searchIV = (ImageView) view.findViewById(R.id.searchIV);
        searchIV.setOnClickListener(this);
        filterIV = (ImageView) view.findViewById(R.id.filterIV);
        filterIV.setVisibility(View.GONE);
        searchCancelIV = (ImageView) view.findViewById(R.id.searchCancelIV);
        searchCancelIV.setOnClickListener(this);
        clearTextIV = (ImageView) view.findViewById(R.id.clearTextIV);
        norecordLayout=(RelativeLayout) rootView.findViewById(R.id.noRecordLayout);
        expenseApprovalRecyclerView = (RecyclerView) view.findViewById(R.id.expenseApprovalRecyclerView);
        errorLinearLayout = (LinearLayout) view.findViewById(R.id.errorLinearLayout);
        errorLinearLayout.setVisibility(View.GONE);

        sendRequestSummaryData();

        clearTextIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchTag.toString().equalsIgnoreCase(getResources().getString(R.string.request_id_search_attendance))) {
                    searchET.setText("");
                    searchET.setHint("Enter Request ID");
                }else if (searchTag.toString().equalsIgnoreCase(getResources().getString(R.string.request_type_search_attendance))) {
                    searchET.setText("");
                    searchET.setHint("Enter Request Type");
                }else if (searchTag.toString().equalsIgnoreCase(getResources().getString(R.string.request_for_search))) {
                    searchET.setText("");
                    searchET.setHint("Enter Request For Person Name");
                }
            }
        });
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchTag != null) {
                    if (!s.toString().equalsIgnoreCase("")) {
                        ArrayList<AttendanceItem> list = new ArrayList<>();
                        if (searchTag.equalsIgnoreCase(getResources().getString(R.string.request_id_search_attendance))) {
                            for (AttendanceItem item : attendanceApprovalResponse.getGetEmpPendingApprovalAttendReqsResult().getReqTypeDetails()) {
                                if (item.getReqCode().toUpperCase().contains(s.toString().toUpperCase())) {
                                    list.add(item);
                                }
                            }
                            refresh(list);
                        }
                       
                        if (searchTag.equalsIgnoreCase(getResources().getString(R.string.request_type_search_attendance))) {
                            for (AttendanceItem item : attendanceApprovalResponse.getGetEmpPendingApprovalAttendReqsResult().getReqTypeDetails()) {
                                if (item.getReqTypeDesc().toUpperCase().contains(s.toString().toUpperCase())) {
                                    list.add(item);
                                }
                            }
                            refresh(list);
                        }

                        if (searchTag.equalsIgnoreCase(getResources().getString(R.string.request_for_search))) {
                            for (AttendanceItem item : attendanceApprovalResponse.getGetEmpPendingApprovalAttendReqsResult().getReqTypeDetails()) {
                                if (item.getForEmpName().toUpperCase().contains(s.toString().toUpperCase())) {
                                    list.add(item);
                                }
                            }
                            refresh(list);
                        }

                    } else {
                        refresh(attendanceApprovalResponse.getGetEmpPendingApprovalAttendReqsResult().getReqTypeDetails());
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchCancelIV:
                searchTag = null;
                search_layout.setVisibility(View.INVISIBLE);
                searchIV.setVisibility(View.VISIBLE);
                searchCancelIV.setVisibility(View.GONE);
                break;
            case R.id.searchIV:
                searchET.setText("");
                ArrayList<String> searchList = new ArrayList<>();
                searchList.add(getResources().getString(R.string.request_id_search_attendance));
                searchList.add(getResources().getString(R.string.request_type_search_attendance));
                searchList.add(getResources().getString(R.string.request_for_search));
                CustomBuilder searchCustomBuilder = new CustomBuilder(getContext(), "Search By", false);
                searchCustomBuilder.setSingleChoiceItems(searchList, null, new CustomBuilder.OnClickListener() {
                    @Override
                    public void onClick(CustomBuilder builder, Object selectedObject) {
                        search_layout.setVisibility(View.INVISIBLE);
                        searchCancelIV.setVisibility(View.VISIBLE);
                        searchIV.setVisibility(View.GONE);
                        searchTag = selectedObject.toString();
                        if (selectedObject.toString().equalsIgnoreCase(getResources().getString(R.string.request_id_search_attendance))) {
                            search_layout.setVisibility(View.VISIBLE);
                            searchTag = getResources().getString(R.string.request_id_search_attendance);
                            searchET.setHint("Enter Request ID");
                        }  else if (selectedObject.toString().equalsIgnoreCase(getResources().getString(R.string.request_type_search_attendance))) {
                            search_layout.setVisibility(View.VISIBLE);
                            searchET.setHint("Enter Request Type");
                            searchTag = getResources().getString(R.string.request_type_search_attendance);
                        } else if (selectedObject.toString().equalsIgnoreCase(getResources().getString(R.string.request_for_search))) {
                            search_layout.setVisibility(View.VISIBLE);
                            searchET.setHint("Enter Request For Person Name");
                            searchTag = getResources().getString(R.string.request_for_search);
                        }


                        builder.dismiss();
                    }


                });
                searchCustomBuilder.show();

                break;

            default:
                break;
        }
        super.onClick(v);
    }

    public void sendRequestSummaryData() {
        Utility.showHidePregress(progressbar, true);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.getExpenseClaimSummaryData(),
                CommunicationConstant.API_PENDING_APPROVAL_ATTENDANCE_REQUEST, true);
    }

    @Override
    public void validateResponse(ResponseData response) {
        Utility.showHidePregress(progressbar, false);
        switch (response.getRequestData().getReqApiId()) {
            case CommunicationConstant.API_PENDING_APPROVAL_ATTENDANCE_REQUEST:
                String str1 = response.getResponseData();
                Log.d("TAG", "Advance Response : " + str1);
                searchParentLayout.setVisibility(View.GONE);
                expenseApprovalRecyclerView.setVisibility(View.GONE);
                errorLinearLayout.setVisibility(View.VISIBLE);
                attendanceApprovalResponse = AttendanceApprovalResponse.create(str1);
                if (attendanceApprovalResponse != null && attendanceApprovalResponse.getGetEmpPendingApprovalAttendReqsResult() != null
                        && attendanceApprovalResponse.getGetEmpPendingApprovalAttendReqsResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)
                        && attendanceApprovalResponse.getGetEmpPendingApprovalAttendReqsResult().getReqTypeDetails().size() > 0) {
                    if (attendanceApprovalResponse.getGetEmpPendingApprovalAttendReqsResult().getReqTypeDetails().get(0) != null) {
                        expenseApprovalRecyclerView.setVisibility(View.VISIBLE);
                        errorLinearLayout.setVisibility(View.GONE);
                        searchParentLayout.setVisibility(View.VISIBLE);
                        refresh(attendanceApprovalResponse.getGetEmpPendingApprovalAttendReqsResult().getReqTypeDetails());
                    }
                }

                break;
            case CommunicationConstant.API_REJECT_WFH_REQUEST:
                String responseData = response.getResponseData();
                Log.d("TAG", "reject response : " + responseData);
                LeaveRejectResponseModel leaveRejectResponse = LeaveRejectResponseModel.create(responseData);
                if (leaveRejectResponse != null && leaveRejectResponse.getRejectWFHRequestResult() != null
                        && leaveRejectResponse.getRejectWFHRequestResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                    alertOkWithFinishFragment(context,leaveRejectResponse.getRejectWFHRequestResult().getErrorMessage(),true);

                    //CustomDialog.alertOkWithFinishFragment(context, leaveRejectResponse.getRejectWFHRequestResult().getErrorMessage(), mUserActionListener, IAction.HOME_VIEW, true);
                } else {
                    new AlertCustomDialog(getActivity(), leaveRejectResponse.getRejectWFHRequestResult().getErrorMessage());
                }
                break;
            case CommunicationConstant.API_REJECT_OD_REQUEST:
                String data = response.getResponseData();
                Log.d("TAG", "reject response : " + data);
                LeaveRejectResponseModel rejectODResponse = LeaveRejectResponseModel.create(data);
                if (rejectODResponse != null && rejectODResponse.getRejectODRequestResult() != null
                        && rejectODResponse.getRejectODRequestResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                    alertOkWithFinishFragment(context,rejectODResponse.getRejectODRequestResult().getErrorMessage(),true);
                   // CustomDialog.alertOkWithFinishFragment(context, rejectODResponse.getRejectODRequestResult().getErrorMessage(), mUserActionListener, IAction.HOME_VIEW, true);
                } else {
                    new AlertCustomDialog(getActivity(), rejectODResponse.getRejectODRequestResult().getErrorMessage());
                }
                break;
            case CommunicationConstant.API_REJECT_TOUR_REQUEST:
                String respData = response.getResponseData();
                Log.d("TAG", "reject response : " + respData);
                LeaveRejectResponseModel rejectTourResponse = LeaveRejectResponseModel.create(respData);
                if (rejectTourResponse != null && rejectTourResponse.getRejectTourRequestResult() != null
                        && rejectTourResponse.getRejectTourRequestResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                    alertOkWithFinishFragment(context,rejectTourResponse.getRejectTourRequestResult().getErrorMessage(),true);
                    /// /  CustomDialog.alertOkWithFinishFragment(context, rejectTourResponse.getRejectTourRequestResult().getErrorMessage(), mUserActionListener, IAction.HOME_VIEW, true);
                } else {
                    new AlertCustomDialog(getActivity(), rejectTourResponse.getRejectTourRequestResult().getErrorMessage());
                }
                break;

            case CommunicationConstant.API_GET_WFH_REQUEST_DETAIL:
                String str = response.getResponseData();
                Log.d("TAG", "WFH Response : " + str);
                WFHSummaryResponse wfhSummaryResponse = WFHSummaryResponse.create(str);
                if (wfhSummaryResponse != null && wfhSummaryResponse.getGetWFHRequestDetailResult() != null
                        && wfhSummaryResponse.getGetWFHRequestDetailResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)
                        && wfhSummaryResponse.getGetWFHRequestDetailResult().getWFHRequestDetail() != null) {
                    wfhRequest = wfhSummaryResponse.getGetWFHRequestDetailResult().getWFHRequestDetail();
                    sendApprovalWFHData();
                }

                break;

            case CommunicationConstant.API_APPROVE_WFH_REQUEST:
                String resp = response.getResponseData();
                Log.d("TAG", "wfh response : " + resp);
                WFHResponseModel wfhResponseModel = WFHResponseModel.create(resp);
                if (wfhResponseModel != null && wfhResponseModel.getApproveWFHRequestResult() != null
                        && wfhResponseModel.getApproveWFHRequestResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                   // CustomDialog.alertOkWithFinishFragment(context, wfhResponseModel.getApproveWFHRequestResult().getErrorMessage(), mUserActionListener, IAction.HOME_VIEW, true);
                    alertOkWithFinishFragment(context,wfhResponseModel.getApproveWFHRequestResult().getErrorMessage(),true);
                } else {
                    new AlertCustomDialog(getActivity(), wfhResponseModel.getApproveWFHRequestResult().getErrorMessage());
                }
                break;
            case CommunicationConstant.API_GET_OD_REQUEST_DETAIL:
                String strResp = response.getResponseData();
                Log.d("TAG", "OD Summary Response : " + strResp);
                ODSummaryResponse odSummaryResponse = ODSummaryResponse.create(strResp);
                if (odSummaryResponse != null && odSummaryResponse.getGetODRequestDetailResult() != null
                        && odSummaryResponse.getGetODRequestDetailResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)
                        && odSummaryResponse.getGetODRequestDetailResult().getODRequestDetail() != null) {
                    odRequestDetailResult = odSummaryResponse.getGetODRequestDetailResult().getODRequestDetail();
                    sendApprovalODData();
                }

                break;
            case CommunicationConstant.API_APPROVE_OD_REQUEST:
                String odresponseData = response.getResponseData();
                Log.d("TAG", "od response : " + odresponseData);
                ODResponseModel odResponseModel = ODResponseModel.create(odresponseData);
                if (odResponseModel != null && odResponseModel.getApproveODRequestResult() != null
                        && odResponseModel.getApproveODRequestResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                    //CustomDialog.alertOkWithFinishFragment(context, odResponseModel.getApproveODRequestResult().getErrorMessage(), mUserActionListener, IAction.HOME_VIEW, true);
                    alertOkWithFinishFragment(context,odResponseModel.getApproveODRequestResult().getErrorMessage(),true);
                } else {
                    new AlertCustomDialog(getActivity(), odResponseModel.getApproveODRequestResult().getErrorMessage());
                }
                break;
            case CommunicationConstant.API_GET_TOUR_REQUEST_DETAIL:
                String tourResponse = response.getResponseData();
                Log.d("TAG", "Tour Response : " + tourResponse);
                TourSummaryResponse tourSummaryResponse = TourSummaryResponse.create(tourResponse);
                if (tourSummaryResponse != null && tourSummaryResponse.getGetTourRequestDetailResult() != null
                        && tourSummaryResponse.getGetTourRequestDetailResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)
                        && tourSummaryResponse.getGetTourRequestDetailResult().getTourRequestDetail() != null) {
                    tourSummaryRequestDetail = tourSummaryResponse.getGetTourRequestDetailResult().getTourRequestDetail();
                    sendApprovalTourData();
                }
                break;
            case CommunicationConstant.API_APPROVE_TOUR_REQUEST:
                String tourResp = response.getResponseData();
                Log.d("TAG", "Tour approval response : " + tourResp);
                TourResponseModel tourResponseModel = TourResponseModel.create(tourResp);
                if (tourResponseModel != null && tourResponseModel.getApproveTourRequestResult() != null
                        && tourResponseModel.getApproveTourRequestResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                   // CustomDialog.alertOkWithFinishFragment(context, tourResponseModel.getApproveTourRequestResult().getErrorMessage(), mUserActionListener, IAction.HOME_VIEW, true);
                    alertOkWithFinishFragment(context,tourResponseModel.getApproveTourRequestResult().getErrorMessage(),true);
                } else {
                    new AlertCustomDialog(getActivity(), tourResponseModel.getApproveTourRequestResult().getErrorMessage());
                }
                break;

            case CommunicationConstant.API_REJECT_ATTENDANCE_REQUEST:
                String respDataAttendance = response.getResponseData();
                Log.d("TAG", "reject attendance response : " + respDataAttendance);
                AttendanceRejectResponseModel attendanceRejectResponseModel = AttendanceRejectResponseModel.create(respDataAttendance);
                if (attendanceRejectResponseModel != null && attendanceRejectResponseModel.getRejectRequestResult() != null
                        && attendanceRejectResponseModel.getRejectRequestResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                   // CustomDialog.alertOkWithFinishFragment(context, attendanceRejectResponseModel.getRejectRequestResult().getErrorMessage(), mUserActionListener, IAction.HOME_VIEW, true);
                    alertOkWithFinishFragment(context,attendanceRejectResponseModel.getRejectRequestResult().getErrorMessage(),true);
                } else {
                    new AlertCustomDialog(getActivity(), attendanceRejectResponseModel.getRejectRequestResult().getErrorMessage());
                }
                break;
            case CommunicationConstant.API_APPROVE_ATTENDANCE_REQUEST:
                String respDataAttend = response.getResponseData();
                Log.d("TAG", "reject attendance response : " + respDataAttend);
                AttendanceRejectResponseModel attendanceApproveResponseModel = AttendanceRejectResponseModel.create(respDataAttend);
                if (attendanceApproveResponseModel != null && attendanceApproveResponseModel.getApproveRequestResult() != null
                        && attendanceApproveResponseModel.getApproveRequestResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {

                    alertOkWithFinishFragment(context,attendanceApproveResponseModel.getApproveRequestResult().getErrorMessage(),true);
                   // CustomDialog.alertOkWithFinishFragment(context, attendanceApproveResponseModel.getApproveRequestResult().getErrorMessage(), mUserActionListener, IAction.HOME_VIEW, true);
                } else {
                    new AlertCustomDialog(getActivity(), attendanceApproveResponseModel.getApproveRequestResult().getErrorMessage());
                }
                break;
            case CommunicationConstant.API_GET_ATTENDANCE_DETAIL:
                String strDetails = response.getResponseData();
                Log.d("TAG", "Time Modification Summary Response : " + strDetails);
                summaryResponseModel = TimeModificationSummaryResponseModel.create(strDetails);
                if (summaryResponseModel != null && summaryResponseModel.getGetAttendanceReqDetailResult() != null
                        && summaryResponseModel.getGetAttendanceReqDetailResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)
                        && summaryResponseModel.getGetAttendanceReqDetailResult().getAttendanceReqDetail() != null) {
                    reqDetail = summaryResponseModel.getGetAttendanceReqDetailResult().getAttendanceReqDetail();
                    sendAttendanceApprovalData();
                }

                break;

            default:
                break;
        }
        super.validateResponse(response);
    }

    private void refresh(ArrayList<AttendanceItem> items) {
        norecordLayout.setVisibility(View.GONE);
        if(items != null && items.size()<=0){
            norecordLayout.setVisibility(View.VISIBLE);
        }

            AttendanceApprovalAdapter = new AttendanceApprovalAdapter(items);
            expenseApprovalRecyclerView.setAdapter(AttendanceApprovalAdapter);
            expenseApprovalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            AttendanceApprovalAdapter.notifyDataSetChanged();

    }

    void alertOkWithFinishFragment(final Context mContext, String msg, boolean isMaterial) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(mContext,
                isMaterial ? R.style.MyDialogTheme
                        : R.style.MyDialogThemeTransparent);
        final Dialog openDialog = new Dialog(ctw);
        openDialog.setCancelable(false);
        openDialog.setContentView(R.layout.material_dialog_layout);
        TextView tv_message = (TextView)openDialog.findViewById(R.id.tv_message);
        TextView tv_title = (TextView) openDialog.findViewById(R.id.tv_title);
        tv_title.setText("Confirmation");
        tv_message.setText(msg);
        TextView tv_cancel = (TextView) openDialog.findViewById(R.id.tv_cancel);
        tv_cancel.setVisibility(View.GONE);
        TextView tv_ok = (TextView) openDialog.findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mUserActionListener.performUserActionFragment(action, null, null);
                sendRequestSummaryData();

                openDialog.dismiss();
            }
        });
        openDialog.show();
    }
    private class AttendanceApprovalAdapter extends
            RecyclerView.Adapter<AttendanceApprovalAdapter.MyViewHolder> {
        private ArrayList<AttendanceItem> dataSet;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView requestIdTV, requestNoTV, empNameTV, detailsTV, leaveRemarksTV;
            Button approveBTN;
            ImageView menuIV;


            public MyViewHolder(View v) {
                super(v);

                requestIdTV = (TextView) v.findViewById(R.id.requestIdTV);
                requestNoTV = (TextView) v.findViewById(R.id.requestNoTV);
                empNameTV = (TextView) v.findViewById(R.id.empNameTV);
                detailsTV = (TextView) v.findViewById(R.id.detailsTV);
                leaveRemarksTV = (TextView) v.findViewById(R.id.leaveRemarksTV);
                menuIV = (ImageView) v.findViewById(R.id.menuIV);
                menuIV.setVisibility(View.VISIBLE);
                approveBTN = (Button) v.findViewById(R.id.approveBTN);


            }
        }

        public void addAll(List<AttendanceItem> list) {

            dataSet.addAll(list);
            notifyDataSetChanged();
        }

        public AttendanceApprovalAdapter(List<AttendanceItem> data) {
            this.dataSet = (ArrayList<AttendanceItem>) data;

        }

        @Override
        public AttendanceApprovalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.leave_approval_detail_layout, parent, false);
            AttendanceApprovalAdapter.MyViewHolder myViewHolder = new AttendanceApprovalAdapter.MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final AttendanceApprovalAdapter.MyViewHolder holder, final int listPosition) {

            final AttendanceItem item = dataSet.get(listPosition);

            holder.requestIdTV.setText(item.getReqTypeDesc());
            holder.requestNoTV.setText(item.getReqCode());
            holder.empNameTV.setText(item.getForEmpName());
            holder.detailsTV.setText(item.getDetails());
            holder.leaveRemarksTV.setText(item.getRemark());
            holder.approveBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getReqCode() != null && item.getReqCode().startsWith("HR") || item.getReqCode().startsWith("HW")) {
                        status=item.getStatus();
                        sendViewRequestSummaryData(item);
                    }
                    if (item.getReqCode() != null && item.getReqCode().startsWith("OD") || item.getReqCode().startsWith("OW")) {
                        status=item.getStatus();
                        sendViewODRequestSummaryData(item);
                    }
                    if (item.getReqCode() != null && item.getReqCode().startsWith("TR") || item.getReqCode().startsWith("TW")) {
                        status=item.getStatus();
                        sendViewTourRequestSummaryData(item);

                    }
                    if (item.getReqCode() != null && item.getReqCode().startsWith("BA")) {
                        status=item.getStatus();
                        sendViewAttendanceSummaryData(item);
                    }
                    if (item.getReqCode() != null && item.getReqCode().startsWith("TM")) {
                        status=item.getStatus();
                        sendViewAttendanceSummaryData(item);
                    }
                }
            });

            final ArrayList<String> menuList = new ArrayList<>();
            if (item.getButtons() != null) {
                for (String str : item.getButtons()) {
                    if (str.equalsIgnoreCase("Edit")) {
                        menuList.add(str);
                    }
                 /*   if (str.equalsIgnoreCase("View")) {
                        menuList.add(str);
                    }*/
                    if (str.equalsIgnoreCase("Reject")) {
                        menuList.add(str);
                    }

                }
            }

            if (menuList.size() > 0) {
                // menu list visible
                holder.menuIV.setVisibility(View.VISIBLE);
                holder.menuIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomBuilder builder = new CustomBuilder(getContext(), "Options", true);
                        builder.setSingleChoiceItems(menuList, null, new CustomBuilder.OnClickListener() {
                            @Override
                            public void onClick(CustomBuilder builder, Object selectedObject) {
                                if (selectedObject.toString().equalsIgnoreCase("Reject")) {
                                    status=item.getStatus();
                                    rejectPopup(item);
                                } else if (selectedObject.toString().equalsIgnoreCase("Edit")) {


                                    if (item.getReqCode() != null && item.getReqCode().startsWith("HR")) {
                                        WorkFromHomeRequestFragment requestFragment = new WorkFromHomeRequestFragment();
                                        requestFragment.setEmployeeLeaveModel(item);
                                        requestFragment.setScreenName(screenName);
                                        Fragment fragment=requestFragment;
                                        mUserActionListener.performUserActionFragment(IAction.WORK_FROM_HOME,fragment,null);
                                    }


                                    if (item.getReqCode() != null && item.getReqCode().startsWith("OD")) {
                                        OutdoorDutyRequestFragment outdoorDutyRequestFragment = new OutdoorDutyRequestFragment();
                                        outdoorDutyRequestFragment.setEmployeeLeaveModel(item);
                                        outdoorDutyRequestFragment.setScreenName(screenName);
                                        Fragment fragment=outdoorDutyRequestFragment;
                                        mUserActionListener.performUserActionFragment(IAction.OUTDOOR_DUTY,fragment,null);
                                    }

                                    if (item.getReqCode() != null && item.getReqCode().startsWith("TR")) {
                                        TourRequestFragment tourRequestFragment = new TourRequestFragment();
                                        tourRequestFragment.setEmployeeLeaveModel(item);
                                        tourRequestFragment.setScreenName(screenName);
                                        Fragment fragment=tourRequestFragment;
                                        mUserActionListener.performUserActionFragment(IAction.TOUR,fragment,null);
                                       /* FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.view_advance_expense, fragment);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();*/
                                    }

                                    if (item.getReqCode() != null && item.getReqCode().startsWith("BA")) {
                                        Intent intent = new Intent(getActivity(), BackdatedAttendanceActivity.class);
                                        BackdatedAttendanceActivity.employeeLeaveModel = item;
                                        startActivityForResult(intent, BackdatedAttendanceActivity.TIMEMODIFICATIONREQUESTCODE);

                                    }

                                    if (item.getReqCode() != null && item.getReqCode().startsWith("TM")) {
                                        Intent intent = new Intent(getActivity(), TimeModificationActivity.class);
                                        TimeModificationActivity.employeeLeaveModel = item;
                                        startActivityForResult(intent, TimeModificationActivity.TIMEMODIFICATIONREQUESTCODE);
                                    }

                                    if (item.getReqCode() != null && item.getReqCode().startsWith("HW")) {
                                      /*  WorkFromHomeRequestFragment requestFragment = new WorkFromHomeRequestFragment();
                                        requestFragment.setEmployeeLeaveModel(item);
                                        requestFragment.setScreenName(screenName);*/
                                        WorkFromHomeRequestFragment requestFragment = new WorkFromHomeRequestFragment();
                                        requestFragment.setEmployeeLeaveModel(item);
                                        requestFragment.setScreenName(screenName);
                                        Fragment fragment=requestFragment;
                                        mUserActionListener.performUserActionFragment(IAction.WORK_FROM_HOME,fragment,null);
                                    }
                                    if (item.getReqCode() != null && item.getReqCode().startsWith("OW")) {
                                       /* OutdoorDutyRequestFragment outdoorDutyRequestFragment = new OutdoorDutyRequestFragment();
                                        outdoorDutyRequestFragment.setEmployeeLeaveModel(item);
                                        outdoorDutyRequestFragment.setScreenName(screenName);
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.view_advance_expense, outdoorDutyRequestFragment);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();*/

                                        OutdoorDutyRequestFragment outdoorDutyRequestFragment = new OutdoorDutyRequestFragment();
                                        outdoorDutyRequestFragment.setEmployeeLeaveModel(item);
                                        outdoorDutyRequestFragment.setScreenName(screenName);
                                        Fragment fragment=outdoorDutyRequestFragment;
                                        mUserActionListener.performUserActionFragment(IAction.OUTDOOR_DUTY,fragment,null);
                                    }
                                    if (item.getReqCode() != null && item.getReqCode().startsWith("TW")) {
                                        /*TourRequestFragment tourRequestFragment = new TourRequestFragment();
                                        tourRequestFragment.setEmployeeLeaveModel(item);
                                        tourRequestFragment.setScreenName(screenName);
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.view_advance_expense, tourRequestFragment);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();*/

                                        TourRequestFragment tourRequestFragment = new TourRequestFragment();
                                        tourRequestFragment.setEmployeeLeaveModel(item);
                                        tourRequestFragment.setScreenName(screenName);
                                        Fragment fragment=tourRequestFragment;
                                        mUserActionListener.performUserActionFragment(IAction.TOUR,fragment,null);
                                    }

                                }/* else if (selectedObject.toString().equalsIgnoreCase("View")) {

                                    if (item.getReqCode() != null && item.getReqCode().startsWith("HW")) {
                                        WorkFromHomeRequestFragment requestFragment = new WorkFromHomeRequestFragment();
                                        requestFragment.setEmployeeLeaveModel(item);
                                        requestFragment.setScreenName(screenName);
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.view_advance_expense, requestFragment);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    }
                                    if (item.getReqCode() != null && item.getReqCode().startsWith("OW")) {
                                        OutdoorDutyRequestFragment outdoorDutyRequestFragment = new OutdoorDutyRequestFragment();
                                        outdoorDutyRequestFragment.setEmployeeLeaveModel(item);
                                        outdoorDutyRequestFragment.setScreenName(screenName);
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.view_advance_expense, outdoorDutyRequestFragment);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    }
                                    if (item.getReqCode() != null && item.getReqCode().startsWith("TW")) {
                                        TourRequestFragment tourRequestFragment = new TourRequestFragment();
                                        tourRequestFragment.setEmployeeLeaveModel(item);
                                        tourRequestFragment.setScreenName(screenName);
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.view_advance_expense, tourRequestFragment);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    }


                                }*/
                                builder.dismiss();
                            }
                        });
                        builder.show();
                    }
                });

            }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showLog(AttandanceFragment.class,"ResultCode "+ resultCode+"" + data);
        /*if (requestCode == TimeModificationActivity.TIMEMODIFICATIONREQUESTCODE && resultCode==1) {
          //  mUserActionListener.performUserAction(IAction.HOME_VIEW, null, null);
            sendRequestSummaryData();
            showLog(AttandanceFragment.class,"ResultCode1 "+ resultCode+"");
        }*/
        sendRequestSummaryData();

    }

    private void sendViewRequestSummaryData(AttendanceItem item) {
        requestDetail = new GetWFHRequestDetail();
        requestDetail.setReqID(item.getReqID());
        requestDetail.setAction(AppsConstant.VIEW_ACTION);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.WFHSummaryDetails(requestDetail),
                CommunicationConstant.API_GET_WFH_REQUEST_DETAIL, true);
    }

    private void sendViewODRequestSummaryData(AttendanceItem item) {
        GetODRequestDetail getODRequestDetail = new GetODRequestDetail();
        getODRequestDetail.setReqID(Integer.parseInt(item.getReqID()));
        getODRequestDetail.setAction(AppsConstant.VIEW_ACTION);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.ODSummaryDetails(getODRequestDetail),
                CommunicationConstant.API_GET_OD_REQUEST_DETAIL, true);
    }

    private void sendViewTourRequestSummaryData(AttendanceItem item) {
        requestDetail = new GetWFHRequestDetail();
        requestDetail.setReqID(item.getReqID());
        requestDetail.setAction(AppsConstant.VIEW_ACTION);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.WFHSummaryDetails(requestDetail),
                CommunicationConstant.API_GET_TOUR_REQUEST_DETAIL, true);
    }

    private void sendViewAttendanceSummaryData(AttendanceItem item) {
        GetTimeModificationRequestDetail getTimeModificationRequestDetail = new GetTimeModificationRequestDetail();
        getTimeModificationRequestDetail.setReqID(item.getReqID());
        getTimeModificationRequestDetail.setAction(AppsConstant.EDIT_ACTION);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.timeModificationSummaryDetails(getTimeModificationRequestDetail),
                CommunicationConstant.API_GET_ATTENDANCE_DETAIL, true);
    }

    private void rejectPopup(final AttendanceItem item) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.reject_layout);

        final EditText messageTV = (EditText) dialog.findViewById(R.id.messageTV);

        final TextView cancelTV, okTV;
        okTV = (TextView) dialog.findViewById(R.id.okTV);
        cancelTV = (TextView) dialog.findViewById(R.id.cancelTV);
        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        okTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comments = messageTV.getText().toString();
                if (comments.equalsIgnoreCase("")) {
                    new AlertCustomDialog(getContext(), getResources().getString(R.string.enter_remarks));
                    return;
                }

                if (item.getReqCode() != null && item.getReqCode().startsWith("HR") || item.getReqCode().startsWith("HW")) {
                    rejectWFHRequest(item);
                }

                if (item.getReqCode() != null && item.getReqCode().startsWith("OD") || item.getReqCode().startsWith("OW")) {
                    rejectODRequest(item);
                }

                if (item.getReqCode() != null && item.getReqCode().startsWith("TR") || item.getReqCode().startsWith("TW")) {
                    rejectTourRequest(item);
                }
                if (item.getReqCode() != null && item.getReqCode().startsWith("BA")) {
                    rejectAttendanceRequest(item);
                }

                if (item.getReqCode() != null && item.getReqCode().startsWith("TM")) {
                    rejectAttendanceRequest(item);
                }
                dialog.dismiss();
            }

        });
        dialog.show();
    }

    private void rejectWFHRequest(AttendanceItem item) {
        WFHRejectRequestModel rejectRequestModel = new WFHRejectRequestModel();
        rejectRequestModel.setReqID(item.getReqID());
        rejectRequestModel.setReqStatus(status);
        rejectRequestModel.setComments(comments);
        Utility.showHidePregress(progressbar, true);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.rejectRequest(rejectRequestModel),
                CommunicationConstant.API_REJECT_WFH_REQUEST, true);
    }

    private void rejectAttendanceRequest(AttendanceItem item) {
        AttendanceRejectRequestModel rejectRequestModel = new AttendanceRejectRequestModel();
        AttendanceRejectItem attendanceRejectItem = new AttendanceRejectItem();
        attendanceRejectItem.setReqID(item.getReqID());
        attendanceRejectItem.setRemark(comments);
        attendanceRejectItem.setApprovalLevel(item.getApprovalLevel());
        attendanceRejectItem.setStatus(item.getStatus());
        rejectRequestModel.setRequest(attendanceRejectItem);
        Utility.showHidePregress(progressbar, true);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.rejectAttendanceRequest(rejectRequestModel),
                CommunicationConstant.API_REJECT_ATTENDANCE_REQUEST, true);
    }

    private void rejectODRequest(AttendanceItem item) {
        WFHRejectRequestModel rejectRequestModel = new WFHRejectRequestModel();
        rejectRequestModel.setReqID(item.getReqID());
        rejectRequestModel.setReqStatus(status);
        rejectRequestModel.setComments(comments);
        Utility.showHidePregress(progressbar, true);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.rejectRequest(rejectRequestModel),
                CommunicationConstant.API_REJECT_OD_REQUEST, true);

    }

    private void rejectTourRequest(AttendanceItem item) {
        WFHRejectRequestModel rejectRequestModel = new WFHRejectRequestModel();
        rejectRequestModel.setReqID(item.getReqID());
        rejectRequestModel.setReqStatus(status);
        rejectRequestModel.setComments(comments);
        Utility.showHidePregress(progressbar, true);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.rejectRequest(rejectRequestModel),
                CommunicationConstant.API_REJECT_TOUR_REQUEST, true);

    }

    private void sendAttendanceApprovalData() {
        TimeModificationItem timeModificationItem = new TimeModificationItem();
        timeModificationItem.setForEmpID(reqDetail.getEmpID() + "");
        timeModificationItem.setAttendID(reqDetail.getAttendID());
        timeModificationItem.setReqTime(reqDetail.getReqTime());
        timeModificationItem.setReqOutTime(reqDetail.getReqOutTime());
        timeModificationItem.setApprovalLevel(reqDetail.getApprovalLevel());
        timeModificationItem.setStatus(status);
        timeModificationItem.setRemark(reqDetail.getRemark());
        timeModificationItem.setReqId(reqDetail.getReqID());

        TimeModificationRequestModel timeModificationRequestModel = new TimeModificationRequestModel();
        timeModificationRequestModel.setRequest(timeModificationItem);
        Utility.showHidePregress(progressbar, true);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.timeModificationRequest(timeModificationRequestModel),
                CommunicationConstant.API_APPROVE_ATTENDANCE_REQUEST, true);
    }

    private void sendApprovalWFHData() {
        WFHRequestDetailModel wfhRequestDetailModel = new WFHRequestDetailModel();
        PartialDayDataModel partialDayDataModel = new PartialDayDataModel();
        wfhRequestDetailModel.setStartDate(wfhRequest.getStartDate());
        wfhRequestDetailModel.setApprovalLevel(Integer.parseInt(wfhRequest.getApprovalLevel()));
        wfhRequestDetailModel.setEndDate(wfhRequest.getEndDate());
        wfhRequestDetailModel.setForEmpID(wfhRequest.getForEmpID());
        wfhRequestDetailModel.setReqID(String.valueOf(wfhRequest.getReqID()));
        wfhRequestDetailModel.setRemarks(wfhRequest.getRemark());
        wfhRequestDetailModel.setStatus(status);
        partialDayDataModel.setDayP50(wfhRequest.getPartialDay().getPartialDayData().getDayP50());
        partialDayDataModel.setDayFull(wfhRequest.getPartialDay().getPartialDayData().getDayFull());
        PartialDayModel partialDayModel = new PartialDayModel();
        partialDayModel.setPartialDayData(partialDayDataModel);
        wfhRequestDetailModel.setPartialDay(partialDayModel);
        wfhRequestDetailModel.setAttachments(wfhRequest.getAttachments());
        WFHRequestModel wfhRequestModel = new WFHRequestModel();
        wfhRequestModel.setWfhRequestDetail(wfhRequestDetailModel);
        Utility.showHidePregress(progressbar, true);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.WFHRequest(wfhRequestModel),
                CommunicationConstant.API_APPROVE_WFH_REQUEST, true);
    }

    private void sendApprovalODData() {
        OdReqDetailModel odReqDetailModel = new OdReqDetailModel();
        odReqDetailModel.setForEmpID(odRequestDetailResult.getForEmpID() + "");
        odReqDetailModel.setReqID(odRequestDetailResult.getReqID() + "");
        odReqDetailModel.setDate(odRequestDetailResult.getDate());
        odReqDetailModel.setStartTime(odRequestDetailResult.getStartTime());
        odReqDetailModel.setEndTime(odRequestDetailResult.getEndTime());
        odReqDetailModel.setPlace(odRequestDetailResult.getPlace());
        odReqDetailModel.setStatus(status);
        odReqDetailModel.setRemarks(odRequestDetailResult.getRemark());
        odReqDetailModel.setApprovalLevel(Integer.parseInt(odRequestDetailResult.getApprovalLevel()));
        odReqDetailModel.setAttachments(odRequestDetailResult.getAttachments());
        ODRequestModel odRequestModel = new ODRequestModel();
        odRequestModel.setOdReqDetail(odReqDetailModel);
        Utility.showHidePregress(progressbar, true);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.ODRequest(odRequestModel),
                CommunicationConstant.API_APPROVE_OD_REQUEST, true);
    }

    private void sendApprovalTourData() {
        TourReqDetail tourReqDetail = new TourReqDetail();
        tourReqDetail.setForEmpID(tourSummaryRequestDetail.getForEmpID());
        tourReqDetail.setReqID(tourSummaryRequestDetail.getReqID());
        tourReqDetail.setStartDate(tourSummaryRequestDetail.getStartDate());
        tourReqDetail.setEndDate(tourSummaryRequestDetail.getEndDate());
        tourReqDetail.setTravelFrom(tourSummaryRequestDetail.getTravelFrom());
        tourReqDetail.setTravelTo(tourSummaryRequestDetail.getTravelTo());
        tourReqDetail.setRemarks(tourSummaryRequestDetail.getRemark());
        tourReqDetail.setReqStatus(status);
        tourReqDetail.setAccomodationYN(tourSummaryRequestDetail.getAccomodationYN());
        tourReqDetail.setAccomodationDet(tourSummaryRequestDetail.getAccomodationDet());
        tourReqDetail.setTicketYN(tourSummaryRequestDetail.getTicketYN());
        tourReqDetail.setTicketDet(tourSummaryRequestDetail.getTicketDet());
        TourReasonListModel reasonListModel = new TourReasonListModel();
        reasonListModel.setReasonCode(tourSummaryRequestDetail.getTourReason().getReasonCode());
        tourReqDetail.setTourReason(reasonListModel);
        tourReqDetail.setCustomFields(tourSummaryRequestDetail.getCustomFields());
        tourReqDetail.setAttachments(tourSummaryRequestDetail.getAttachments());
        tourReqDetail.setApprovalLevel(Integer.parseInt(tourSummaryRequestDetail.getApprovalLevel()));
        TourRequestModel tourRequestModel = new TourRequestModel();
        tourRequestModel.setTourReqDetail(tourReqDetail);
        Utility.showHidePregress(progressbar, true);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.tourRequest(tourRequestModel),
                CommunicationConstant.API_APPROVE_TOUR_REQUEST, true);
    }

}
