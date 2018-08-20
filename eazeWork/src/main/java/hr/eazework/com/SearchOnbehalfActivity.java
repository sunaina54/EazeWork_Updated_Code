package hr.eazework.com;

import android.content.Context;
import android.content.Intent;
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

import hr.eazework.com.model.EmployItem;
import hr.eazework.com.model.EmployResponse;
import hr.eazework.com.model.SearchOnBehalfItem;
import hr.eazework.com.ui.fragment.Leave.CreateNewLeaveFragment;
import hr.eazework.com.ui.fragment.Attendance.OutdoorDutyRequestFragment;
import hr.eazework.com.ui.fragment.Attendance.TourRequestFragment;
import hr.eazework.com.ui.fragment.Attendance.WorkFromHomeRequestFragment;
import hr.eazework.com.ui.fragment.Ticket.CreateTicketFragment;
import hr.eazework.com.ui.util.AppsConstant;
import hr.eazework.com.ui.util.Preferences;
import hr.eazework.com.ui.util.Utility;
import hr.eazework.com.ui.util.custom.AlertCustomDialog;
import hr.eazework.mframe.communication.ResponseData;
import hr.eazework.selfcare.communication.AppRequestJSONString;
import hr.eazework.selfcare.communication.CommunicationConstant;
import hr.eazework.selfcare.communication.CommunicationManager;

public class SearchOnbehalfActivity extends BaseActivity {
    private RelativeLayout searchLayout;
    private EditText searchET;
    private SearchOnBehalfItem request;
    private Preferences preferences;
    private Context context;
    private RelativeLayout mainLayout;
    private RelativeLayout backLayout;
    private TextView tv_header_text;
    private RecyclerView recyclerView;
    private EmployResponse empResponse;
    public static String SELECTED_EMP="seletedEmp";
    public static String SELECTED_WFH_EMP="selectedWFHEmp";
    public static String SELECTED_OD_EMP="selectedODEmp";
    public static String SELECTED_TOUR_EMP="selectedTourEmp";
    public static String SELECTED_TICKET_EMP="selectedTicketEmp";
    private ImageView ibRightIV,clearTextIV;
    private LinearLayout progressContainer,noRecordLayout,rl_edit_team_member;
    private String screenName="";
    private int SELECTED_TYPE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_onbehalf);
        setupScreen();
    }
    private void setupScreen(){
        context=this;
        SELECTED_TYPE=getIntent().getIntExtra("SearchType",0);
        progressContainer=(LinearLayout) findViewById(R.id.ll_progress_container);
        noRecordLayout=(LinearLayout) findViewById(R.id.noRecordLayout);
        preferences = new Preferences(context);
        int textColor = Utility.getTextColorCode(preferences);
        int bgColor = Utility.getBgColorCode(context, preferences);
        searchLayout=(RelativeLayout)findViewById(R.id.searchLayout);
        ibRightIV=(ImageView)findViewById(R.id.ibRight) ;
        ibRightIV.setVisibility(View.GONE);
        searchET=(EditText)findViewById(R.id.searchET);
        clearTextIV = (ImageView)findViewById(R.id.clearTextIV);
        clearTextIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchET.setText("");
                searchET.setHint("Enter User name");
            }
        });
        tv_header_text=(TextView)findViewById(R.id.tv_header_text);
        tv_header_text.setText("Search Employee");
        tv_header_text.setTextColor(textColor);
        rl_edit_team_member=(LinearLayout) findViewById(R.id.rl_edit_team_member);
        rl_edit_team_member.setBackgroundColor(bgColor);

        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str=searchET.getText().toString();
                if(str.equalsIgnoreCase("")){
                    new AlertCustomDialog(context,"Please enter search text");
                    return;
                }
                searchOnBehalf(str);
            }
        });
        backLayout=(RelativeLayout)findViewById(R.id.backLayout);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void refreshRecycle(ArrayList<EmployItem> list){
        recyclerView.setVisibility(View.GONE);
        noRecordLayout.setVisibility(View.GONE);

        if(list!=null && list.size()>0){
            recyclerView.setVisibility(View.VISIBLE);
            CustomAdapter adapter = new CustomAdapter(list);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter.notifyDataSetChanged();
        }else{
            noRecordLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void validateResponse(ResponseData response) {
        progressContainer.setVisibility(View.GONE);
        switch (response.getRequestData().getReqApiId()) {
            case CommunicationConstant.API_SEARCH_ONBEHALF:
                Log.d("TAG",response.getResponseData());
                empResponse=EmployResponse.create(response.getResponseData()) ;
                if(empResponse!=null && empResponse.getGetLeaveEmpListResult()!=null &&
                        !empResponse.getGetLeaveEmpListResult().getErrorCode()
                        .equalsIgnoreCase(AppsConstant.SUCCESS) ){
                    new AlertCustomDialog(context,empResponse.getGetLeaveEmpListResult().getErrorMessage());
                    return;
                }
                if(empResponse!=null && empResponse.getGetLeaveEmpListResult()!=null &&
                        empResponse.getGetLeaveEmpListResult().getErrorCode()
                                .equalsIgnoreCase(AppsConstant.SUCCESS)){
                    noRecordLayout.setVisibility(View.GONE);
                        refreshRecycle(empResponse.getGetLeaveEmpListResult().getLeaveEmps());

                }else {

                }
                break;
            case CommunicationConstant.API_GET_WFH_EMP_LIST:
                Log.d("TAG","WFH employee "+response.getResponseData());
                empResponse=EmployResponse.create(response.getResponseData()) ;
                if(empResponse!=null && empResponse.getGetWFHEmpListResult()!=null &&
                        !empResponse.getGetWFHEmpListResult().getErrorCode()
                                .equalsIgnoreCase(AppsConstant.SUCCESS) ){
                    new AlertCustomDialog(context,empResponse.getGetWFHEmpListResult().getErrorMessage());
                    return;
                }
                if(empResponse!=null && empResponse.getGetWFHEmpListResult()!=null &&
                        empResponse.getGetWFHEmpListResult().getErrorCode()
                                .equalsIgnoreCase(AppsConstant.SUCCESS)){
                    noRecordLayout.setVisibility(View.GONE);
                    refreshRecycle(empResponse.getGetWFHEmpListResult().getEmployees());

                }else {

                }
                break;
            case CommunicationConstant.API_GET_OD_EMP_LIST:
                Log.d("TAG","WFH employee "+response.getResponseData());
                empResponse=EmployResponse.create(response.getResponseData()) ;
                if(empResponse!=null && empResponse.getGetODEmpListResult()!=null &&
                        !empResponse.getGetODEmpListResult().getErrorCode()
                                .equalsIgnoreCase(AppsConstant.SUCCESS) ){
                    new AlertCustomDialog(context,empResponse.getGetODEmpListResult().getErrorMessage());
                    return;
                }
                if(empResponse!=null && empResponse.getGetODEmpListResult()!=null &&
                        empResponse.getGetODEmpListResult().getErrorCode()
                                .equalsIgnoreCase(AppsConstant.SUCCESS)){
                    noRecordLayout.setVisibility(View.GONE);
                    refreshRecycle(empResponse.getGetODEmpListResult().getEmployees());

                }else {

                }
                break;
            case CommunicationConstant.API_GET_TOUR_EMP_LIST:
                Log.d("TAG","WFH employee "+response.getResponseData());
                empResponse=EmployResponse.create(response.getResponseData()) ;
                if(empResponse!=null && empResponse.getGetTourEmpListResult()!=null &&
                        !empResponse.getGetTourEmpListResult().getErrorCode()
                                .equalsIgnoreCase(AppsConstant.SUCCESS) ){
                    new AlertCustomDialog(context,empResponse.getGetTourEmpListResult().getErrorMessage());
                    return;
                }
                if(empResponse!=null && empResponse.getGetTourEmpListResult()!=null &&
                        empResponse.getGetTourEmpListResult().getErrorCode()
                                .equalsIgnoreCase(AppsConstant.SUCCESS)){
                    noRecordLayout.setVisibility(View.GONE);
                    refreshRecycle(empResponse.getGetTourEmpListResult().getEmployees());

                }else {

                }
                break;
            default:
                break;
        }
        super.validateResponse(response);
    }


    private  void searchOnBehalf(String str){
        progressContainer.setVisibility(View.VISIBLE);
        request=new SearchOnBehalfItem();
        request.setFromCount("1");
        request.setToCount("-1");
        request.setMatchStr(str);
        if(SELECTED_TYPE==CreateNewLeaveFragment.LEAVE_EMP) { //leave employee
            CommunicationManager.getInstance().sendPostRequest(this,
                    AppRequestJSONString.searchOnBehalfRequest(request),
                    CommunicationConstant.API_SEARCH_ONBEHALF, true);
        }else if(WorkFromHomeRequestFragment.WFH_EMP == SELECTED_TYPE) {
            CommunicationManager.getInstance().sendPostRequest(this,
                    AppRequestJSONString.searchOnBehalfRequest(request),
                    CommunicationConstant.API_GET_WFH_EMP_LIST, true);
        }else if(OutdoorDutyRequestFragment.OD_EMP == SELECTED_TYPE) {
            CommunicationManager.getInstance().sendPostRequest(this,
                    AppRequestJSONString.searchOnBehalfRequest(request),
                    CommunicationConstant.API_GET_OD_EMP_LIST, true);
        }else if(TourRequestFragment.TOUR_EMP == SELECTED_TYPE) {
            CommunicationManager.getInstance().sendPostRequest(this,
                    AppRequestJSONString.searchOnBehalfRequest(request),
                    CommunicationConstant.API_GET_TOUR_EMP_LIST, true);
        }
    }

    private class CustomAdapter extends
            RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
        private ArrayList<EmployItem> dataSet;


        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView empNameTV,empIdTV,empCodeTV;
            private Button selectBT;
            public MyViewHolder(View v) {
                super(v);

                empCodeTV=(TextView)v.findViewById(R.id.empCodeTV);
                empIdTV=(TextView)v.findViewById(R.id.empIdTV);
                empNameTV=(TextView)v.findViewById(R.id.empNameTV);
                selectBT=(Button)v.findViewById(R.id.selectBT);


            }
        }

        public void addAll(List<EmployItem> list) {

            dataSet.addAll(list);
            notifyDataSetChanged();
        }

        public CustomAdapter(List<EmployItem> data) {
            this.dataSet = (ArrayList<EmployItem>) data;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.employ_list_item, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }


        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            final EmployItem item = dataSet.get(listPosition);
            if(item.getEmpCode()!=null){
                holder.empCodeTV.setText(item.getEmpCode());

            }
            if(item.getName()!=null)
                holder.empNameTV.setText(item.getName());
            holder.empIdTV.setText(item.getEmpID()+"");
            holder.selectBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent theIntent=new Intent();
                    theIntent.putExtra(SELECTED_EMP,item);
                    theIntent.putExtra(SELECTED_WFH_EMP,item);
                    theIntent.putExtra(SELECTED_OD_EMP,item);
                    theIntent.putExtra(SELECTED_TOUR_EMP,item);
                    theIntent.putExtra(SELECTED_TICKET_EMP,item);
                    setResult(OutdoorDutyRequestFragment.OD_EMP,theIntent);
                    setResult(WorkFromHomeRequestFragment.WFH_EMP,theIntent);
                    setResult(CreateNewLeaveFragment.LEAVE_EMP,theIntent);
                    setResult(TourRequestFragment.TOUR_EMP,theIntent);
                    setResult(CreateTicketFragment.TICKET_EMP,theIntent);
                    finish();
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
