package hr.eazework.com.ui.fragment.Ticket;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


import hr.eazework.com.MainActivity;
import hr.eazework.com.R;

import hr.eazework.com.model.AttendanceReqDetail;
import hr.eazework.com.model.ExpenseApprovalList;
import hr.eazework.com.model.GetWFHRequestDetail;
import hr.eazework.com.model.LeaveRejectResponseModel;
import hr.eazework.com.model.ODRequestDetail;
import hr.eazework.com.model.ODResponseModel;
import hr.eazework.com.model.ODSummaryResponse;
import hr.eazework.com.model.TicketItem;
import hr.eazework.com.model.TicketResponseModel;
import hr.eazework.com.model.TicketSummaryRequestModel;
import hr.eazework.com.model.TimeModificationSummaryResponseModel;
import hr.eazework.com.model.TourResponseModel;
import hr.eazework.com.model.TourSummaryRequestDetail;
import hr.eazework.com.model.TourSummaryResponse;
import hr.eazework.com.model.WFHRequestDetailItem;
import hr.eazework.com.model.WFHResponseModel;
import hr.eazework.com.model.WFHSummaryResponse;
import hr.eazework.com.ui.customview.CustomBuilder;
import hr.eazework.com.ui.fragment.Attendance.AttendanceApprovalFragment;
import hr.eazework.com.ui.fragment.Attendance.OutdoorDutyRequestFragment;
import hr.eazework.com.ui.fragment.Attendance.TourRequestFragment;
import hr.eazework.com.ui.fragment.Attendance.WorkFromHomeRequestFragment;
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
 * Created by SUNAINA on 17-10-2018.
 */

public class TicketApprovalFragment extends BaseFragment {
    private Context context;
    public static final String TAG = "TicketApprovalFragment";
    private RecyclerView expenseApprovalRecyclerView;
    public static final String screenName = "TicketApprovalFragment";
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
    private ArrayList<TicketItem> searchExpenseList;
    private EditText searchET;
    private ImageView searchIV, filterIV, searchCancelIV,clearTextIV;
    private String searchTag = null;
    private RelativeLayout norecordLayout;
    private View progressbar;
    private TicketSummaryRequestModel ticketSummaryRequestModel;
    private TicketResponseModel ticketResponseModel;


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
                }/*else if (searchTag.toString().equalsIgnoreCase(getResources().getString(R.string.request_type_search_attendance))) {
                    searchET.setText("");
                    searchET.setHint("Enter Request Type");
                }*/else if (searchTag.toString().equalsIgnoreCase(getResources().getString(R.string.request_for_search))) {
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
                        ArrayList<TicketItem> list = new ArrayList<>();
                        if (searchTag.equalsIgnoreCase(getResources().getString(R.string.request_id_search_attendance))) {
                            for (TicketItem item : ticketResponseModel.getGetPendingTicketsResult().getTickets()) {
                                if (item.getTicketCode().toUpperCase().contains(s.toString().toUpperCase())) {
                                    list.add(item);
                                }
                            }
                            refresh(list);
                        }

                       /* if (searchTag.equalsIgnoreCase(getResources().getString(R.string.request_type_search_attendance))) {
                            for (TicketItem item : ticketResponseModel.getGetPendingTicketsResult().getTickets()) {
                                if (item.getReqTypeDesc().toUpperCase().contains(s.toString().toUpperCase())) {
                                    list.add(item);
                                }
                            }
                            refresh(list);
                        }*/

                        if (searchTag.equalsIgnoreCase(getResources().getString(R.string.request_for_search))) {
                            for (TicketItem item :
                                    ticketResponseModel.getGetPendingTicketsResult().getTickets()) {
                                if (item.getCustomerEmpName().toUpperCase().contains(s.toString().toUpperCase())) {
                                    list.add(item);
                                }
                            }
                            refresh(list);
                        }

                    } else {
                        refresh(ticketResponseModel.getGetPendingTicketsResult().getTickets());
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
              //  searchList.add(getResources().getString(R.string.request_type_search_attendance));
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
                        }  /*else if (selectedObject.toString().equalsIgnoreCase(getResources().getString(R.string.request_type_search_attendance))) {
                            search_layout.setVisibility(View.VISIBLE);
                            searchET.setHint("Enter Request Type");
                            searchTag = getResources().getString(R.string.request_type_search_attendance);
                        }*/ else if (selectedObject.toString().equalsIgnoreCase(getResources().getString(R.string.request_for_search))) {
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
    @Override
    public void validateResponse(ResponseData response) {
        Utility.showHidePregress(progressbar, false);
        switch (response.getRequestData().getReqApiId()) {
            case CommunicationConstant.API_GET_PENDING_TICKETS:
                String str1 = response.getResponseData();
                Log.d("TAG", "Ticket Pending List : " + str1);
                searchParentLayout.setVisibility(View.GONE);
                expenseApprovalRecyclerView.setVisibility(View.GONE);
                errorLinearLayout.setVisibility(View.VISIBLE);
                ticketResponseModel = TicketResponseModel.create(str1);
                if (ticketResponseModel != null && ticketResponseModel.getGetPendingTicketsResult() != null
                        && ticketResponseModel.getGetPendingTicketsResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)
                        && ticketResponseModel.getGetPendingTicketsResult().getTickets()!=null && ticketResponseModel.getGetPendingTicketsResult().getTickets().size() > 0) {
                    if (ticketResponseModel.getGetPendingTicketsResult().getTickets().get(0) != null) {
                        expenseApprovalRecyclerView.setVisibility(View.VISIBLE);
                        errorLinearLayout.setVisibility(View.GONE);
                        searchParentLayout.setVisibility(View.VISIBLE);
                        refresh(ticketResponseModel.getGetPendingTicketsResult().getTickets());
                    }
                }

                break;
           /* case CommunicationConstant.API_REJECT_WFH_REQUEST:
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
                break;*/



            default:
                break;
        }
        super.validateResponse(response);
    }


    public void sendRequestSummaryData() {
        ticketSummaryRequestModel=new TicketSummaryRequestModel();
        Utility.showHidePregress(progressbar, true);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.ticketSummary(ticketSummaryRequestModel),
                CommunicationConstant.API_GET_PENDING_TICKETS ,true);
    }

    private void refresh(ArrayList<TicketItem> items) {
        norecordLayout.setVisibility(View.GONE);
        if(items != null && items.size()<=0){
            norecordLayout.setVisibility(View.VISIBLE);
        }
        AttendanceApprovalAdapter = new AttendanceApprovalAdapter(items);
        expenseApprovalRecyclerView.setAdapter(AttendanceApprovalAdapter);
        expenseApprovalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        AttendanceApprovalAdapter.notifyDataSetChanged();

    }


    private class AttendanceApprovalAdapter extends
            RecyclerView.Adapter<AttendanceApprovalAdapter.MyViewHolder> {
        private ArrayList<TicketItem> dataSet;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView ticketIdTV, createdOnTV, empNameTV, subjectTV, remarksTV;
            Button editBTN;
            ImageView menuIV;


            public MyViewHolder(View v) {
                super(v);

                ticketIdTV = (TextView) v.findViewById(R.id.ticketIdTV);
                createdOnTV = (TextView) v.findViewById(R.id.createdOnTV);
                empNameTV = (TextView) v.findViewById(R.id.empNameTV);
                subjectTV = (TextView) v.findViewById(R.id.subjectTV);
                remarksTV = (TextView) v.findViewById(R.id.remarksTV);
                menuIV = (ImageView) v.findViewById(R.id.menuIV);
                menuIV.setVisibility(View.GONE);
                editBTN = (Button) v.findViewById(R.id.editBTN);


            }
        }

        public void addAll(List<TicketItem> list) {

            dataSet.addAll(list);
            notifyDataSetChanged();
        }

        public AttendanceApprovalAdapter(List<TicketItem> data) {
            this.dataSet = (ArrayList<TicketItem>) data;

        }

        @Override
        public AttendanceApprovalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ticket_pending_item, parent, false);
      AttendanceApprovalAdapter.MyViewHolder myViewHolder = new AttendanceApprovalAdapter.MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final AttendanceApprovalAdapter.MyViewHolder holder, final int listPosition) {

            final TicketItem item = dataSet.get(listPosition);

            if(item.getTicketCode()!=null) {
                holder.ticketIdTV.setText(item.getTicketCode());
            }
            if(item.getDate()!=null) {
                holder.createdOnTV.setText(item.getDate());
            }
            if(item.getCustomerEmpName()!=null) {
                holder.empNameTV.setText(item.getCustomerEmpName());
            }
            if(item.getSubject()!=null) {
                holder.subjectTV.setText(item.getSubject());
            }
            if(item.getDescription()!=null) {
                holder.remarksTV.setText(item.getDescription());
            }
            holder.editBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* if (item.getReqCode() != null && item.getReqCode().startsWith("HR") || item.getReqCode().startsWith("HW")) {
                        status=item.getStatus();
                        sendViewRequestSummaryData(item);
                    }*/
                    CreateTicketAdvanceFragment requestFragment = new CreateTicketAdvanceFragment();
                    requestFragment.setTicketItem(item);
                    requestFragment.setScreenName(screenName);
                    Fragment fragment=requestFragment;
                    mUserActionListener.performUserActionFragment(IAction.RAISE_TICKET_ADV,fragment,null);

                }
            });
/*

            final ArrayList<String> menuList = new ArrayList<>();
            menuList.add("Edit");
          */
/*  if (item.getButtons() != null) {
                for (String str : item.getButtons()) {
                    if (str.equalsIgnoreCase("Edit")) {
                        menuList.add(str);
                    }
                   *//*
*/
/* if (str.equalsIgnoreCase("View")) {
                        menuList.add(str);
                    }
                    if (str.equalsIgnoreCase("Reject")) {
                        menuList.add(str);
                    }*//*
*/
/*

                }
            }*//*


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
                               if (selectedObject.toString().equalsIgnoreCase("Edit")) {
                                   CreateTicketAdvanceFragment requestFragment = new CreateTicketAdvanceFragment();
                                   requestFragment.setTicketItem(item);
                                   requestFragment.setScreenName(screenName);
                                   Fragment fragment=requestFragment;
                                   mUserActionListener.performUserActionFragment(IAction.WORK_FROM_HOME,fragment,null);
                                }
                                builder.dismiss();
                            }
                        });
                        builder.show();
                    }
                });
*/

          //  }
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
        if (Utility.isNetworkAvailable(getContext())) {
           // MainActivity.isAnimationLoaded = false;
            sendRequestSummaryData();
           // showHideProgressView(true);
        } else {
            new AlertCustomDialog(getActivity(), getString(R.string.msg_internet_connection));
        }
    }
}
