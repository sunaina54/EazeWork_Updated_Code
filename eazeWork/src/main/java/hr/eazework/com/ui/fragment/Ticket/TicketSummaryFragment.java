package hr.eazework.com.ui.fragment.Ticket;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
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
import java.util.List;

import hr.eazework.com.R;
import hr.eazework.com.model.TicketItem;

import hr.eazework.com.model.TicketItem;
import hr.eazework.com.model.TicketResponseModel;
import hr.eazework.com.model.TicketSummaryRequestModel;
import hr.eazework.com.ui.customview.CustomBuilder;
import hr.eazework.com.ui.fragment.Attendance.ViewOdSummaryFragment;
import hr.eazework.com.ui.fragment.Attendance.WorkFromHomeRequestFragment;
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

public class TicketSummaryFragment extends BaseFragment {

    public static final String TAG = "TicketSummaryFragment";
    public static final String screenName = "TicketSummaryFragment";
    private RecyclerView summaryRecyclerView;
    private Context context;
    private LinearLayout errorLinearLayout;

    private View view;
    private String beginYearDate,calendarDate;
    private TicketSummaryRequestModel ticketSummaryRequestModel;
    public static final long daysForFilter = 15552000000L;

    private WFHSummaryAdapter summaryAdapter;
    private Button attendanceHistoryBTN;
    private ArrayList<TicketItem> filterExpenseList;
    private ArrayList<TicketItem> searchExpenseList;
    private RelativeLayout search_layout;
    private EditText searchET;
    private LinearLayout searchParentLayout;
    private ImageView searchIV, filterIV, searchCancelIV,clearTextIV;
    private String searchTag = null;
    private RelativeLayout norecordLayout;
    private View progressbar;
    private LinearLayout searchTempLayout;
    private TicketResponseModel ticketResponseModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_ticket_summary, container, false);
        setupScreen(view);
        return view;

    }

    private void setupScreen(View view) {
        context = getActivity();
        filterExpenseList = new ArrayList<>();
        searchExpenseList = new ArrayList<>();
        progressbar = (LinearLayout) view.findViewById(R.id.ll_progress_container);
        progressbar.bringToFront();
        /*searchTempLayout = (LinearLayout) view.findViewById(R.id.searchTempLayout);
        searchTempLayout.setVisibility(View.GONE);*/
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
                        ArrayList<TicketItem> list = new ArrayList<>();
                        if (searchTag.equalsIgnoreCase(getResources().getString(R.string.request_id_search_attendance))) {
                            for (TicketItem item : ticketResponseModel.getGetTicketsResult().getTickets()) {
                                if (item.getTicketCode().toUpperCase().contains(s.toString().toUpperCase())) {
                                    list.add(item);
                                }
                            }
                            refresh(list);
                        }
                        /*if (searchTag.equalsIgnoreCase(getResources().getString(R.string.request_type_search_attendance))) {
                            for (TicketItem item : ticketResponseModel.getGetTicketsResult().getTickets()) {
                                if (item.getRequestTypeDesc().toUpperCase().contains(s.toString().toUpperCase())) {
                                    list.add(item);
                                }
                            }
                            refresh(list);
                        }*/
                        if (searchTag.equalsIgnoreCase(getResources().getString(R.string.date_search))) {
                            for (TicketItem item : ticketResponseModel.getGetTicketsResult().getTickets()) {
                                if (item.getDate().toUpperCase().contains(s.toString().toUpperCase())) {
                                    list.add(item);
                                }
                            }
                            refresh(list);
                        }

                        if (searchTag.equalsIgnoreCase(getResources().getString(R.string.pending_with_search))) {
                            for (TicketItem item : ticketResponseModel.getGetTicketsResult().getTickets()) {
                                if (item.getPendingWith().toUpperCase().contains(s.toString().toUpperCase())) {
                                    list.add(item);
                                }
                            }
                            refresh(list);
                        }

                    } else {
                        refresh(ticketResponseModel.getGetTicketsResult().getTickets());
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
             //   searchList.add(getResources().getString(R.string.request_type_search_attendance));
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
                        }/*else if (selectedObject.toString().equalsIgnoreCase(getResources().getString(R.string.request_type_search_attendance))) {
                            search_layout.setVisibility(View.VISIBLE);
                            searchET.setHint("Enter Request Type");
                            searchTag = getResources().getString(R.string.request_type_search_attendance);
                        }*/  else if (selectedObject.toString().equalsIgnoreCase(getResources().getString(R.string.date_search))) {
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
                ArrayList<TicketItem> listModels= Utility.prepareFilterListTicket(ticketResponseModel.getGetTicketsResult().getTickets());
                if(listModels!=null && listModels.size()>0) {
                    for (TicketItem model : listModels) {
                        list.add(model.getStatusDesc());
                    }
                    TicketItem itemListModel = new TicketItem();
                    itemListModel.setStatus("-1");
                    itemListModel.setStatusDesc("All");
                    listModels.add(0, itemListModel);
                }

                if (ticketResponseModel != null && ticketResponseModel.getGetTicketsResult() != null &&
                        ticketResponseModel.getGetTicketsResult().getTickets() != null &&
                        ticketResponseModel.getGetTicketsResult().getTickets().size() > 0) {
                    CustomBuilder customBuilder = new CustomBuilder(getContext(), "Filter By", true);
                    customBuilder.setSingleChoiceItems(listModels, null, new CustomBuilder.OnClickListener() {
                        @Override
                        public void onClick(CustomBuilder builder, Object selectedObject) {
                            TicketItem model=(TicketItem)selectedObject;

                            if (model.getStatus().equalsIgnoreCase("-1")) {
                                refresh(ticketResponseModel.getGetTicketsResult().getTickets());

                            }else {
                                filterExpenseList = new ArrayList<>();
                                for (int i = 0; i < ticketResponseModel.getGetTicketsResult().getTickets().size(); i++) {
                                    if (ticketResponseModel.getGetTicketsResult().getTickets().get(i)
                                            .getStatusDesc().equalsIgnoreCase(model.getStatusDesc())) {
                                        filterExpenseList.add(ticketResponseModel.getGetTicketsResult().getTickets().get(i));
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
        ticketSummaryRequestModel=new TicketSummaryRequestModel();
        Utility.showHidePregress(progressbar, true);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.ticketSummary(ticketSummaryRequestModel),
                CommunicationConstant.API_GET_TICKETS ,true);
    }

    @Override
    public void validateResponse(ResponseData response) {
         Utility.showHidePregress(progressbar, false);
        switch (response.getRequestData().getReqApiId()) {
            case CommunicationConstant.API_GET_TICKETS:
                String str = response.getResponseData();
                Log.d("TAG", "Attendance Response : " + str);
                summaryRecyclerView.setVisibility(View.GONE);
                errorLinearLayout.setVisibility(View.VISIBLE);
                searchParentLayout.setVisibility(View.GONE);
                ticketResponseModel = TicketResponseModel.create(str);
                //ticketResponseModel = ticketResponseModel.create(str);
                if (ticketResponseModel != null && ticketResponseModel.getGetTicketsResult() != null &&
                        ticketResponseModel.getGetTicketsResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)
                        && ticketResponseModel.getGetTicketsResult().getTickets().size() > 0) {
                    if (ticketResponseModel.getGetTicketsResult().getTickets().get(0) != null) {
                        summaryRecyclerView.setVisibility(View.VISIBLE);
                        errorLinearLayout.setVisibility(View.GONE);
                        searchParentLayout.setVisibility(View.VISIBLE);
                        refresh(ticketResponseModel.getGetTicketsResult().getTickets());
                    }

                }
                break;

            default:
                break;
        }
        super.validateResponse(response);
    }

    private void refresh(ArrayList<TicketItem> ticketItems) {
        norecordLayout.setVisibility(View.GONE);
        if (ticketItems != null && ticketItems.size() <= 0) {
            norecordLayout.setVisibility(View.VISIBLE);
        }
            summaryAdapter = new WFHSummaryAdapter(ticketItems);
            summaryRecyclerView.setAdapter(summaryAdapter);
            summaryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            summaryAdapter.notifyDataSetChanged();
    }


    private class WFHSummaryAdapter extends
            RecyclerView.Adapter<WFHSummaryAdapter.MyViewHolder> {
        private ArrayList<TicketItem> dataSet;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView ticketIdTV, descriptionTV, dateTV, priorityTV, pendingWithTV, statusTV;
            private Button viewBTN;
            private LinearLayout endDateLl,daysLl;


            public MyViewHolder(View v) {
                super(v);

                ticketIdTV = (TextView) v.findViewById(R.id.ticketIdTV);
                descriptionTV = (TextView) v.findViewById(R.id.descriptionTV);
                dateTV = (TextView) v.findViewById(R.id.dateTV);
                pendingWithTV = (TextView) v.findViewById(R.id.pendingWithTV);
                priorityTV = (TextView) v.findViewById(R.id.priorityTV);
                statusTV = (TextView) v.findViewById(R.id.statusTV);
                viewBTN = (Button) v.findViewById(R.id.viewBTN);



            }

        }

        public void addAll(List<TicketItem> list) {

            dataSet.addAll(list);
            notifyDataSetChanged();
        }

        public WFHSummaryAdapter(List<TicketItem> data) {
            this.dataSet = (ArrayList<TicketItem>) data;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ticket_summary_item, parent, false);
            //view.setOnClickListener(MainActivity.myOnClickListener);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
            final TicketItem item = dataSet.get(listPosition);
            if(item.getTicketCode()!=null) {
                holder.ticketIdTV.setText(item.getTicketCode());
            }
            if(item.getDate()!=null) {
                holder.dateTV.setText(item.getDate());
            }

            if(item.getDescription()!=null) {
                holder.descriptionTV.setText(item.getDescription());
            }

            if(item.getTicketPriorityDesc()!=null){
                holder.priorityTV.setText(item.getTicketPriorityDesc());
            }

            if(item.getPendingWith()!=null){
                holder.pendingWithTV.setText(item.getPendingWith());
            }

            if (item.getStatusDesc()!=null){
                holder.statusTV.setText(item.getStatusDesc());
            }

            if(item.getButtons()!=null && item.getButtons().length>0) {
                holder.viewBTN.setText(item.getButtons()[0]);
            }



            holder.viewBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(dataSet.get(listPosition).getButtons()[0].
                            equalsIgnoreCase("Edit")){
                        // Edit
                        CreateTicketAdvanceFragment ticketAdvanceFragment= new CreateTicketAdvanceFragment();
                        ticketAdvanceFragment.setTicketItem(dataSet.get(listPosition));
                        ticketAdvanceFragment.setScreenName(screenName);
                        Fragment fragment1 = ticketAdvanceFragment;
                        mUserActionListener.performUserActionFragment(IAction.RAISE_TICKET_ADV,fragment1, null);
                    }

                    if(dataSet.get(listPosition).getButtons()[0].equalsIgnoreCase("View")){
                        // View Particular ticket
                        ViewTicketFragment viewTicketFragment = new ViewTicketFragment();
                        viewTicketFragment.setTicketItem(dataSet.get(listPosition));
                        Fragment fragment=viewTicketFragment;
                        mUserActionListener.performUserActionFragment(IAction.VIEW_TICKET,
                                fragment,null);
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
