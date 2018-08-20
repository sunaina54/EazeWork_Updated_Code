package hr.eazework.com.ui.fragment.Team;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;


import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hr.eazework.com.MainActivity;
import hr.eazework.com.R;
import hr.eazework.com.model.AttendanceApprovalResponse;
import hr.eazework.com.model.MenuItemModel;
import hr.eazework.com.model.ModelManager;
import hr.eazework.com.model.TeamMember;
import hr.eazework.com.ui.customview.CustomBuilder;
import hr.eazework.com.ui.fragment.BaseFragment;
import hr.eazework.com.ui.interfaces.IAction;
import hr.eazework.com.ui.util.AppsConstant;
import hr.eazework.com.ui.util.custom.AlertCustomDialog;
import hr.eazework.mframe.communication.ResponseData;
import hr.eazework.selfcare.adapter.TeamMemberListViewAdapter;
import hr.eazework.selfcare.communication.AppRequestJSONString;
import hr.eazework.selfcare.communication.CommunicationConstant;
import hr.eazework.selfcare.communication.CommunicationManager;

/**
 * Created by Manjunath on 21-03-2017.
 */

public class TeamMemberList extends BaseFragment implements AdapterView.OnItemClickListener {

    public static final String TAG = "TeamMemberList";

    private TeamMemberListViewAdapter adapter;
    private String empId = "";
    private ListView listView;
    private ArrayList<TeamMember> list = new ArrayList<>();
    private TeamMember model;
    private RelativeLayout search_layout;
    private LinearLayout searchParentLayout, errorLinearLayout;
    private EditText searchET;
    private ImageView searchIV, filterIV, searchCancelIV, clearTextIV;
    private String searchTag = null;
    private AttendanceApprovalResponse attendanceApprovalResponse;
    private RelativeLayout norecordLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.team_member_list, container, false);
        search_layout = (RelativeLayout) rootView.findViewById(R.id.search_layout);
        search_layout.setVisibility(View.INVISIBLE);
        searchET = (EditText) rootView.findViewById(R.id.searchET);
        searchParentLayout = (LinearLayout) rootView.findViewById(R.id.searchParentLayout);
        searchParentLayout.setVisibility(View.GONE);
        searchIV = (ImageView) rootView.findViewById(R.id.searchIV);
        searchIV.setOnClickListener(this);
        filterIV = (ImageView) rootView.findViewById(R.id.filterIV);
        filterIV.setVisibility(View.GONE);
        searchCancelIV = (ImageView) rootView.findViewById(R.id.searchCancelIV);
        searchCancelIV.setOnClickListener(this);
        clearTextIV = (ImageView) rootView.findViewById(R.id.clearTextIV);
        norecordLayout = (RelativeLayout) rootView.findViewById(R.id.noRecordLayout);
        listView = (ListView) rootView.findViewById(R.id.list_team_members);
        adapter = new TeamMemberListViewAdapter(getContext(), R.layout.team_list_item, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        requestAPI();
        clearTextIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchTag.toString().equalsIgnoreCase(getResources().getString(R.string.search_emp_code))) {
                    searchET.setText("");
                    searchET.setHint("Enter Employee Code");
                }else if (searchTag.toString().equalsIgnoreCase(getResources().getString(R.string.search_emp_name))) {
                    searchET.setText("");
                    searchET.setHint("Enter Employee Name");
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
                        ArrayList<TeamMember> leaveModels = new ArrayList<>();

                        if (searchTag.equalsIgnoreCase(getResources().getString(R.string.search_emp_code))) {
                            TeamMember employeeLeaveModel = model;

                            if (employeeLeaveModel != null) {
                                leaveModels = employeeLeaveModel.getmTeamMemberList();
                            }
                            if (leaveModels == null)
                                leaveModels = new ArrayList<>();

                            ArrayList<TeamMember> filterList = new ArrayList<TeamMember>();
                            for (TeamMember item : leaveModels) {
                                String[] empArray=item.getmName().split("\\(");
                                showLog(TeamMemberList.class,empArray[1]);
                                if (empArray[1].toUpperCase().contains(s.toString().toUpperCase())) {
                                    filterList.add(item);

                                }
                            }
                            populateViewsList(filterList);
                        }


                        if (searchTag.equalsIgnoreCase(getResources().getString(R.string.search_emp_name))) {
                            TeamMember employeeLeaveModel = model;

                            if (employeeLeaveModel != null) {
                                leaveModels = employeeLeaveModel.getmTeamMemberList();
                            }
                            if (leaveModels == null)
                                leaveModels = new ArrayList<>();


                            ArrayList<TeamMember> filterList = new ArrayList<TeamMember>();
                            for (TeamMember item : leaveModels) {
                                String[] empArray=item.getmName().split("\\(");
                                showLog(TeamMemberList.class,empArray[0]);

                                if (empArray[0].toUpperCase().contains(s.toString().toUpperCase())) {
                                    filterList.add(item);

                                }
                            }
                            populateViewsList(filterList);
                        }

                    } else {
                        populateViewsList(model.getmTeamMemberList());

                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return rootView;
    }

    private void requestAPI() {
        MainActivity.isAnimationLoaded = false;
        ((MainActivity)getActivity()).showHideProgress(true);
        empId = TeamMember.getmCurrentEmpId();
        String jsonPostParam = AppRequestJSONString.getTeamList("1", empId);
        CommunicationManager.getInstance().sendPostRequest(this, jsonPostParam, CommunicationConstant.API_GET_TEAM_MEMBER_LIST, false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TeamMember m = list.get(position);
        final String employeeId = m.getmEmpId();
        int teamCount = 0;
        try {
            teamCount = Integer.parseInt(m.getmTeamCount());
        } catch (NumberFormatException e) {
            Log.e(TAG,e.getMessage(),e);
            Crashlytics.logException(e);
        }

        if (teamCount > 0) {
            CustomBuilder builder = new CustomBuilder(getContext(), "Select Action", true);
            builder.setSingleChoiceItems(getTeamItems(), null, new CustomBuilder.OnClickListener() {
                @Override
                public void onClick(CustomBuilder builder, Object selectedObject) {
                    if (selectedObject.toString().equalsIgnoreCase(getString(R.string.msg_view_profile))) {
                        Bundle b = new Bundle();
                        b.putString(AppsConstant.EMP_ID, employeeId);
                        mUserActionListener.performUserAction(IAction.TEAM_MEMBER_PROFILE, null, b);
                    } else if (selectedObject.toString().equalsIgnoreCase(getString(R.string.msg_attendance_detail))) {
                        Bundle b = new Bundle();
                        b.putString(AppsConstant.EMP_ID, employeeId);
                        mUserActionListener.performUserAction(IAction.TEAM_MEMBER_HISTORY, null, b);
                    } else if (selectedObject.toString().equalsIgnoreCase(getString(R.string.msg_team_members))) {
                        Bundle b = new Bundle();
                        TeamMember.setmPreviousEmpId(empId);
                        TeamMember.setmCurrentEmpId(employeeId);
                        TeamMember.loopCount++ ;
                        b.putString(AppsConstant.OPTION_SELECTED, "subTeamMembers");
                        mUserActionListener.performUserAction(IAction.TEAM_MEMBER_LIST, null, b);
                    }
                    builder.dismiss();
                }
            });
            builder.show();

        } else {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(getString(R.string.msg_view_profile));
            MenuItemModel itemModel = ModelManager.getInstance().getMenuItemModel().getItemModel(MenuItemModel.ATTANDANCE_KEY);
            if (itemModel != null && itemModel.isAccess()) {
                arrayList.add(getString(R.string.msg_attendance_detail));
            }
            CustomBuilder actionDialog = new CustomBuilder(getContext(), "Select Action", true);
            actionDialog.setSingleChoiceItems(arrayList, null, new CustomBuilder.OnClickListener() {
                @Override
                public void onClick(CustomBuilder builder, Object selectedObject) {
                    if (selectedObject.toString().equalsIgnoreCase(getString(R.string.msg_view_profile))) {
                        Bundle b = new Bundle();
                        b.putString(AppsConstant.EMP_ID, employeeId);
                        mUserActionListener.performUserAction(IAction.TEAM_MEMBER_PROFILE, null, b);
                    } else if (selectedObject.toString().equalsIgnoreCase(getString(R.string.msg_attendance_detail))) {
                        Bundle b = new Bundle();
                        b.putString(AppsConstant.EMP_ID, employeeId);
                        mUserActionListener.performUserAction(IAction.TEAM_MEMBER_HISTORY, null, b);
                    }
                    builder.dismiss();
                }
            });
            actionDialog.show();

        }

    }

    @Override
    public void validateResponse(ResponseData response) {
        super.validateResponse(response);
        switch (response.getRequestData().getReqApiId()) {
            case CommunicationConstant.API_GET_TEAM_MEMBER_LIST:
                searchParentLayout.setVisibility(View.GONE);
                JSONObject getTeamList;
                try {
                    getTeamList = new JSONObject(response.getResponseData());
                    JSONObject mainJSONObject = getTeamList.optJSONObject("GetTeamMemberListResult");
                    if (mainJSONObject.optInt("ErrorCode", -1) != 0) {
                        String errorMessage = mainJSONObject.optString("ErrorMessage", "Failed");
                        new AlertCustomDialog(getActivity(), errorMessage);
                    } else {
                        String getTeamMemberListResult = getTeamList.optJSONObject("GetTeamMemberListResult").toString();
                        JSONArray array = new JSONObject(getTeamMemberListResult).optJSONArray("list");
                        if(array!=null && array.length()>0) {
                            searchParentLayout.setVisibility(View.VISIBLE);

                        }
                        populateViews(array);
                    }

                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                    Crashlytics.logException(e);
                }
                break;
        }
        MainActivity.isAnimationLoaded = true;
        ((MainActivity)getActivity()).showHideProgress(false);
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

                searchList.add(getResources().getString(R.string.search_emp_code));
                searchList.add(getResources().getString(R.string.search_emp_name));

                CustomBuilder searchCustomBuilder = new CustomBuilder(getContext(), "Search By", false);
                searchCustomBuilder.setSingleChoiceItems(searchList, null, new CustomBuilder.OnClickListener() {
                    @Override
                    public void onClick(CustomBuilder builder, Object selectedObject) {
                        search_layout.setVisibility(View.INVISIBLE);
                        searchCancelIV.setVisibility(View.VISIBLE);
                        searchIV.setVisibility(View.GONE);
                        searchTag = selectedObject.toString();
                        if (selectedObject.toString().equalsIgnoreCase(getResources().getString(R.string.search_emp_code))) {
                            search_layout.setVisibility(View.VISIBLE);
                            searchET.setHint("Enter Employee Code");
                            searchTag = getResources().getString(R.string.search_emp_code);
                        }else if (selectedObject.toString().equalsIgnoreCase(getResources().getString(R.string.search_emp_name))) {
                            search_layout.setVisibility(View.VISIBLE);
                            searchET.setHint("Enter Employee Name");
                            searchTag = getResources().getString(R.string.search_emp_name);
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

    private void populateViews(JSONArray array) {
        model = new TeamMember(array);
        this.list = model.getmTeamMemberList();
        if(adapter != null) {
            adapter.refresh(list);
        }
    }
    private void populateViewsList(ArrayList<TeamMember> list) {
        norecordLayout.setVisibility(View.GONE);
        if(list!=null && list.size()<=0){
            norecordLayout.setVisibility(View.VISIBLE);
        }
        if (adapter != null) {
            adapter.refresh(list);
        }
    }

    public ArrayList<String> getTeamItems() {
        ArrayList<String> list = new ArrayList<>();
        list.add(getString(R.string.msg_view_profile));
        list.add(getString(R.string.msg_team_members));
        MenuItemModel itemModel = ModelManager.getInstance().getMenuItemModel().getItemModel(MenuItemModel.ATTANDANCE_KEY);
        if (itemModel != null && itemModel.isAccess()) {
            list.add(getString(R.string.msg_attendance_detail));
        }
        return list;
    }
}
