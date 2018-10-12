package hr.eazework.com.ui.fragment.Attendance;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.Calendar;
import java.util.List;

import hr.eazework.com.MainActivity;
import hr.eazework.com.R;
import hr.eazework.com.model.GetEmpWFHRequestsModel;
import hr.eazework.com.model.GetEmpWFHResponseItem;
import hr.eazework.com.model.GetEmpWFHResponseModel;
import hr.eazework.com.ui.customview.CustomBuilder;
import hr.eazework.com.ui.fragment.BaseFragment;
import hr.eazework.com.ui.interfaces.IAction;
import hr.eazework.com.ui.util.AppsConstant;
import hr.eazework.com.ui.util.Utility;
import hr.eazework.mframe.communication.ResponseData;
import hr.eazework.selfcare.communication.AppRequestJSONString;
import hr.eazework.selfcare.communication.CommunicationConstant;
import hr.eazework.selfcare.communication.CommunicationManager;

/**
 * Created by Dell3 on 15-01-2018.
 */

public class TimeAndAttendanceSummaryFragment extends BaseFragment {

    public static final String TAG = "TimeAndAttendanceSummaryFragment";
    public static final String screenName = "TimeAndAttendanceSummaryFragment";
    private RecyclerView summaryRecyclerView;
    private Context context;
    private LinearLayout errorLinearLayout;

    private View view;
    private String beginYearDate,calendarDate;
    private GetEmpWFHRequestsModel getEmpWFHRequestsModel;
    public static final long daysForFilter = 15552000000L;
    private GetEmpWFHResponseModel getEmpWFHResponseModel;
    private WFHSummaryAdapter summaryAdapter;
    private Button attendanceHistoryBTN;
    private ArrayList<GetEmpWFHResponseItem> filterExpenseList;
    private ArrayList<GetEmpWFHResponseItem> searchExpenseList;
    private RelativeLayout search_layout;
    private EditText searchET;
    private LinearLayout searchParentLayout;
    private ImageView searchIV, filterIV, searchCancelIV,clearTextIV;
    private String searchTag = null;
    private RelativeLayout norecordLayout;
    private View progressbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_tour_summary, container, false);
        setupScreen(view);
        return view;

    }

    private void setupScreen(View view) {
        context = getActivity();
        filterExpenseList = new ArrayList<>();
        searchExpenseList = new ArrayList<>();
        progressbar = (LinearLayout) view.findViewById(R.id.ll_progress_container);
        progressbar.bringToFront();
        search_layout = (RelativeLayout) view.findViewById(R.id.search_layout);
        search_layout.setVisibility(View.INVISIBLE);
        searchET = (EditText) view.findViewById(R.id.searchET);
        searchParentLayout = (LinearLayout) view.findViewById(R.id.searchParentLayout);
        searchParentLayout.setVisibility(View.GONE);
        searchIV = (ImageView) view.findViewById(R.id.searchIV);
        searchIV.setOnClickListener(this);
        filterIV = (ImageView) view.findViewById(R.id.filterIV);
        filterIV.setOnClickListener(this);
        searchCancelIV = (ImageView) view.findViewById(R.id.searchCancelIV);
        searchCancelIV.setOnClickListener(this);
         clearTextIV = (ImageView) view.findViewById(R.id.clearTextIV);
        norecordLayout=(RelativeLayout) view.findViewById(R.id.noRecordLayout);
        summaryRecyclerView = (RecyclerView) view.findViewById(R.id.summaryRecyclerView);
        errorLinearLayout = (LinearLayout) view.findViewById(R.id.errorLinearLayout);
        errorLinearLayout.setVisibility(View.GONE);
        attendanceHistoryBTN= (Button) view.findViewById(R.id.attendanceHistoryBTN);
        attendanceHistoryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AttendanceHistory viewWFHSummaryFragment = new AttendanceHistory();
                mUserActionListener.performUserAction(IAction.ATTANDANCE_HISTORY,null,null);

              /*  FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.view_advance_expense, viewWFHSummaryFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
            }
        });
        Calendar calendar = Calendar.getInstance();
        Calendar beginYear = Calendar.getInstance();
        beginYear.setTimeInMillis(beginYear.getTimeInMillis() - daysForFilter);
        calendar.setTimeInMillis(calendar.getTimeInMillis() + daysForFilter);
        beginYearDate = String.format("%1$td/%1$tm/%1$tY", beginYear);
        calendarDate = String.format("%1$td/%1$tm/%1$tY", calendar);
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
                } else if (searchTag.toString().equalsIgnoreCase(getResources().getString(R.string.date_search))) {
                    searchET.setText("");
                    searchET.setHint("Enter Date(dd/mm/yyyy)");
                } else if (searchTag.toString().equalsIgnoreCase(getResources().getString(R.string.pending_with_search))) {
                    searchET.setText("");
                    searchET.setHint("Enter Pending with person name");
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
                        ArrayList<GetEmpWFHResponseItem> list = new ArrayList<>();
                        if (searchTag.equalsIgnoreCase(getResources().getString(R.string.request_id_search_attendance))) {
                            for (GetEmpWFHResponseItem item : getEmpWFHResponseModel.getGetEmpAttendanceRequestsResult().getRequests()) {
                                if (item.getReqCode().toUpperCase().contains(s.toString().toUpperCase())) {
                                    list.add(item);
                                }
                            }
                            refresh(list);
                        }
                        if (searchTag.equalsIgnoreCase(getResources().getString(R.string.request_type_search_attendance))) {
                            for (GetEmpWFHResponseItem item : getEmpWFHResponseModel.getGetEmpAttendanceRequestsResult().getRequests()) {
                                if (item.getRequestTypeDesc().toUpperCase().contains(s.toString().toUpperCase())) {
                                    list.add(item);
                                }
                            }
                            refresh(list);
                        }
                        if (searchTag.equalsIgnoreCase(getResources().getString(R.string.date_search))) {
                            for (GetEmpWFHResponseItem item : getEmpWFHResponseModel.getGetEmpAttendanceRequestsResult().getRequests()) {
                                if (item.getStartDate().toUpperCase().contains(s.toString().toUpperCase())) {
                                    list.add(item);
                                }
                            }
                            refresh(list);
                        }

                        if (searchTag.equalsIgnoreCase(getResources().getString(R.string.pending_with_search))) {
                            for (GetEmpWFHResponseItem item : getEmpWFHResponseModel.getGetEmpAttendanceRequestsResult().getRequests()) {
                                if (item.getPendWithName().toUpperCase().contains(s.toString().toUpperCase())) {
                                    list.add(item);
                                }
                            }
                            refresh(list);
                        }

                    } else {
                        refresh(getEmpWFHResponseModel.getGetEmpAttendanceRequestsResult().getRequests());
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
                searchList.add(getResources().getString(R.string.date_search));
                searchList.add(getResources().getString(R.string.pending_with_search));
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
                        }else if (selectedObject.toString().equalsIgnoreCase(getResources().getString(R.string.request_type_search_attendance))) {
                            search_layout.setVisibility(View.VISIBLE);
                            searchET.setHint("Enter Request Type");
                            searchTag = getResources().getString(R.string.request_type_search_attendance);
                        }  else if (selectedObject.toString().equalsIgnoreCase(getResources().getString(R.string.date_search))) {
                            search_layout.setVisibility(View.VISIBLE);
                            searchET.setHint("Enter Date(dd/mm/yyyy)");
                            searchTag = getResources().getString(R.string.date_search);
                        } else if (selectedObject.toString().equalsIgnoreCase(getResources().getString(R.string.pending_with_search))) {
                            search_layout.setVisibility(View.VISIBLE);
                            searchET.setHint("Enter Pending with person name");
                            searchTag = getResources().getString(R.string.pending_with_search);
                        }


                        builder.dismiss();
                    }


                });
                searchCustomBuilder.show();

                break;
            case R.id.filterIV:
                searchET.setText("");
                ArrayList<String> list = new ArrayList<>();
                list.add("All");
                ArrayList<GetEmpWFHResponseItem> listModels= Utility.prepareFilterListAttendance(getEmpWFHResponseModel.getGetEmpAttendanceRequestsResult().getRequests());
                if(listModels!=null && listModels.size()>0) {
                    for (GetEmpWFHResponseItem model : listModels) {
                        list.add(model.getStatusDesc());
                    }
                    GetEmpWFHResponseItem itemListModel = new GetEmpWFHResponseItem();
                    itemListModel.setStatus("-1");
                    itemListModel.setStatusDesc("All");
                    listModels.add(0, itemListModel);
                }

                if (getEmpWFHResponseModel != null && getEmpWFHResponseModel.getGetEmpAttendanceRequestsResult() != null &&
                        getEmpWFHResponseModel.getGetEmpAttendanceRequestsResult().getRequests() != null &&
                        getEmpWFHResponseModel.getGetEmpAttendanceRequestsResult().getRequests().size() > 0) {
                    CustomBuilder customBuilder = new CustomBuilder(getContext(), "Filter By", true);
                    customBuilder.setSingleChoiceItems(listModels, null, new CustomBuilder.OnClickListener() {
                        @Override
                        public void onClick(CustomBuilder builder, Object selectedObject) {
                            GetEmpWFHResponseItem model=(GetEmpWFHResponseItem)selectedObject;

                            if (model.getStatus().equalsIgnoreCase("-1")) {
                                refresh(getEmpWFHResponseModel.getGetEmpAttendanceRequestsResult().getRequests());

                            }else {
                                filterExpenseList = new ArrayList<>();
                                for (int i = 0; i < getEmpWFHResponseModel.getGetEmpAttendanceRequestsResult().getRequests().size(); i++) {
                                    if (getEmpWFHResponseModel.getGetEmpAttendanceRequestsResult().getRequests().get(i)
                                            .getStatusDesc().equalsIgnoreCase(model.getStatusDesc())) {
                                        filterExpenseList.add(getEmpWFHResponseModel.getGetEmpAttendanceRequestsResult().getRequests().get(i));
                                    }
                                }
                                refresh(filterExpenseList);
                            }
                            builder.dismiss();
                        }


                    });
                    customBuilder.show();
                }
                break;


            default:
                break;
        }
        super.onClick(v);
    }
    public void sendRequestSummaryData() {
        getEmpWFHRequestsModel=new GetEmpWFHRequestsModel();
        getEmpWFHRequestsModel.setDateFrom(calendarDate);
        getEmpWFHRequestsModel.setDateTo(beginYearDate);
        Utility.showHidePregress(progressbar, true);

        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.timeAttendanceSummary(getEmpWFHRequestsModel),
                CommunicationConstant.API_GET_EMP_ATTENDANCE_REQUEST ,true);


    }

    @Override
    public void validateResponse(ResponseData response) {
         Utility.showHidePregress(progressbar, false);
        switch (response.getRequestData().getReqApiId()) {
            case CommunicationConstant.API_GET_EMP_ATTENDANCE_REQUEST:
                String str = response.getResponseData();
                Log.d("TAG", "Attendance Response : " + str);
                summaryRecyclerView.setVisibility(View.GONE);
                errorLinearLayout.setVisibility(View.VISIBLE);
                searchParentLayout.setVisibility(View.GONE);
                getEmpWFHResponseModel = GetEmpWFHResponseModel.create(str);
                if (getEmpWFHResponseModel != null && getEmpWFHResponseModel.getGetEmpAttendanceRequestsResult() != null &&
                        getEmpWFHResponseModel.getGetEmpAttendanceRequestsResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)
                        && getEmpWFHResponseModel.getGetEmpAttendanceRequestsResult().getRequests().size() > 0) {
                    if (getEmpWFHResponseModel.getGetEmpAttendanceRequestsResult().getRequests().get(0) != null) {
                        summaryRecyclerView.setVisibility(View.VISIBLE);
                        errorLinearLayout.setVisibility(View.GONE);
                       searchParentLayout.setVisibility(View.VISIBLE);
                        refresh(getEmpWFHResponseModel.getGetEmpAttendanceRequestsResult().getRequests());
                    }

                }
                break;

            default:
                break;
        }
        super.validateResponse(response);
    }

    private void refresh(ArrayList<GetEmpWFHResponseItem> getEmpWFHResponseList) {
        norecordLayout.setVisibility(View.GONE);
        if (getEmpWFHResponseList != null && getEmpWFHResponseList.size() <= 0) {
            norecordLayout.setVisibility(View.VISIBLE);
        }
            summaryAdapter = new WFHSummaryAdapter(getEmpWFHResponseList);
            summaryRecyclerView.setAdapter(summaryAdapter);
            summaryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            summaryAdapter.notifyDataSetChanged();
    }


    private class WFHSummaryAdapter extends
            RecyclerView.Adapter<WFHSummaryAdapter.MyViewHolder> {
        private ArrayList<GetEmpWFHResponseItem> dataSet;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView requestIdTV, descriptionTV, startDateTV, endDateTV, daysTV, pendingWithTV, statusTV,startDateLabelTV;
            private Button viewBTN;
            private LinearLayout endDateLl,daysLl;


            public MyViewHolder(View v) {
                super(v);

                requestIdTV = (TextView) v.findViewById(R.id.requestIdTV);
                descriptionTV = (TextView) v.findViewById(R.id.descriptionTV);
                startDateTV = (TextView) v.findViewById(R.id.startDateTV);
                endDateTV = (TextView) v.findViewById(R.id.endDateTV);
                pendingWithTV = (TextView) v.findViewById(R.id.pendingWithTV);
                daysTV = (TextView) v.findViewById(R.id.daysTV);
                endDateLl= (LinearLayout) v.findViewById(R.id.endDateLl);
                startDateLabelTV= (TextView) v.findViewById(R.id.startDateLabelTV);
                statusTV = (TextView) v.findViewById(R.id.statusTV);
                viewBTN = (Button) v.findViewById(R.id.viewBTN);
                daysLl= (LinearLayout) v.findViewById(R.id.daysLl);


            }

        }

        public void addAll(List<GetEmpWFHResponseItem> list) {

            dataSet.addAll(list);
            notifyDataSetChanged();
        }

        public WFHSummaryAdapter(List<GetEmpWFHResponseItem> data) {
            this.dataSet = (ArrayList<GetEmpWFHResponseItem>) data;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tour_summary_item, parent, false);
            //view.setOnClickListener(MainActivity.myOnClickListener);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
            final GetEmpWFHResponseItem item = dataSet.get(listPosition);
            holder.requestIdTV.setText(item.getReqCode());
            holder.descriptionTV.setText(item.getRequestTypeDesc());
            holder.daysLl.setVisibility(View.VISIBLE);

            if(item.getRequestTypeDesc().equalsIgnoreCase("OD")){
                holder.startDateLabelTV.setText(getResources().getString(R.string.date));
                holder.startDateTV.setText(item.getStartDate());
                holder.endDateLl.setVisibility(View.GONE);
            }else{
                holder.startDateLabelTV.setText(getResources().getString(R.string.start_date));
                holder.endDateLl.setVisibility(View.VISIBLE);
                holder.startDateTV.setText(item.getStartDate());
                holder.endDateTV.setText(item.getEndDate());
            }

            if(item.getRequestTypeDesc().equalsIgnoreCase("Time Modification") || item.getRequestTypeDesc().equalsIgnoreCase("Attendance")){
                holder.daysLl.setVisibility(View.GONE);
            }

            holder.daysTV.setText(item.getTotalDays());
            holder.pendingWithTV.setText(item.getPendWithName());
            holder.statusTV.setText(item.getStatusDesc());


            if(item.getButtons()!=null && item.getButtons().length>0) {
                holder.viewBTN.setText(item.getButtons()[0]);
            }

            holder.viewBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if(item.getRequestTypeDesc()!=null && item.getRequestTypeDesc().equalsIgnoreCase(AppsConstant.WFH) &&
                        item.getStatusDesc()!=null &&
                        !item.getStatusDesc().equalsIgnoreCase(AppsConstant.DRAFT)  ) {
                    ViewWFHSummaryFragment viewWFHSummaryFragment = new ViewWFHSummaryFragment();
                    viewWFHSummaryFragment.setGetEmpWFHResponseItem(dataSet.get(listPosition));
                    Fragment fragment=viewWFHSummaryFragment;
                    mUserActionListener.performUserActionFragment(IAction.VIEW_WFH,fragment,null);
                  /*  FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.view_advance_expense, viewWFHSummaryFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();*/
                }

                    if(item.getRequestTypeDesc()!=null && item.getRequestTypeDesc().equalsIgnoreCase(AppsConstant.WFH)
                            && item.getStatusDesc()!=null &&
                            item.getStatusDesc().equalsIgnoreCase(AppsConstant.DRAFT)) {
                        WorkFromHomeRequestFragment requestFragment = new WorkFromHomeRequestFragment();
                        requestFragment.setGetEmpWFHResponseItem(dataSet.get(listPosition));
                        Fragment fragment=requestFragment;
                        requestFragment.setScreenName(screenName);
                        mUserActionListener.performUserActionFragment(IAction.WORK_FROM_HOME,fragment,null);

                      /*  FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.view_advance_expense, requestFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();*/
                    }

                    if(item.getRequestTypeDesc()!=null && item.getRequestTypeDesc().equalsIgnoreCase(AppsConstant.OD)
                            && item.getStatusDesc()!=null &&
                            !item.getStatusDesc().equalsIgnoreCase(AppsConstant.DRAFT)){
                        ViewOdSummaryFragment viewOdSummaryFragment = new ViewOdSummaryFragment();
                        viewOdSummaryFragment.setGetEmpWFHResponseItem(dataSet.get(listPosition));
                        Fragment fragment=viewOdSummaryFragment;
                        mUserActionListener.performUserActionFragment(IAction.VIEW_OD,fragment,null);
                       /* FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.view_advance_expense, viewOdSummaryFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();*/
                    }

                    if(item.getRequestTypeDesc()!=null &&
                            item.getRequestTypeDesc().equalsIgnoreCase(AppsConstant.OD) && item.getStatusDesc()!=null &&
                            item.getStatusDesc().equalsIgnoreCase(AppsConstant.DRAFT)){
                        OutdoorDutyRequestFragment outdoorDutyRequestFragment = new OutdoorDutyRequestFragment();
                        outdoorDutyRequestFragment.setGetEmpWFHResponseItem(dataSet.get(listPosition));
                        outdoorDutyRequestFragment.setScreenName(screenName);

                        Fragment fragment=outdoorDutyRequestFragment;
                        mUserActionListener.performUserActionFragment(IAction.OUTDOOR_DUTY,fragment,null);
                        /*FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.view_advance_expense, outdoorDutyRequestFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();*/
                    }


                    if(item.getRequestTypeDesc()!=null &&
                            item.getRequestTypeDesc().equalsIgnoreCase(AppsConstant.TOUR)&&
                            item.getStatusDesc()!=null &&
                            !item.getStatusDesc().equalsIgnoreCase(AppsConstant.DRAFT)){
                        ViewTourSummaryFragment viewTourSummaryFragment = new ViewTourSummaryFragment();
                        viewTourSummaryFragment.setGetEmpWFHResponseItem(dataSet.get(listPosition));
                        Fragment fragment=viewTourSummaryFragment;
                        mUserActionListener.performUserActionFragment(IAction.VIEW_TOUR,fragment,null);
                       /* FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.view_advance_expense, viewTourSummaryFragment);
                        fragmentTransaction.commit();*/
                    }

                    if(item.getRequestTypeDesc()!=null &&
                            item.getRequestTypeDesc().equalsIgnoreCase(AppsConstant.TOUR)&&
                            item.getStatusDesc()!=null &&
                            item.getStatusDesc().equalsIgnoreCase(AppsConstant.DRAFT)){
                        TourRequestFragment tourRequestFragment = new TourRequestFragment();
                        tourRequestFragment.setGetEmpWFHResponseItem(dataSet.get(listPosition));
                        tourRequestFragment.setScreenName(screenName);
                        Fragment fragment=tourRequestFragment;
                        mUserActionListener.performUserActionFragment(IAction.TOUR,fragment,null);
                        /*FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.view_advance_expense, tourRequestFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();*/
                    }

                    if(item.getRequestTypeDesc()!=null &&
                            item.getRequestTypeDesc().equalsIgnoreCase(AppsConstant.TIME_MODIFICATION)
                            ||  item.getRequestTypeDesc().equalsIgnoreCase(AppsConstant.BACK_DATED_ATTENDANCE)){
                        ViewTimeModificationSummary viewTimeModificationSummary = new ViewTimeModificationSummary();
                        viewTimeModificationSummary.setGetEmpWFHResponseItem(dataSet.get(listPosition));
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.view_advance_expense, viewTimeModificationSummary);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
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
