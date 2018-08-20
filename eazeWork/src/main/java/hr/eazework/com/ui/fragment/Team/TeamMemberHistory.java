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
import hr.eazework.com.model.History;
import hr.eazework.com.model.MemberApprovalModel;
import hr.eazework.com.model.ModelManager;
import hr.eazework.com.ui.customview.CustomBuilder;
import hr.eazework.com.ui.fragment.BaseFragment;
import hr.eazework.com.ui.util.AppsConstant;
import hr.eazework.com.ui.util.AttendanceUtil;
import hr.eazework.com.ui.util.custom.AlertCustomDialog;
import hr.eazework.mframe.communication.ResponseData;
import hr.eazework.selfcare.adapter.ListViewHistoryAdapter;
import hr.eazework.selfcare.communication.AppRequestJSONString;
import hr.eazework.selfcare.communication.CommunicationConstant;
import hr.eazework.selfcare.communication.CommunicationManager;

import static hr.eazework.com.ui.util.CalenderUtils.get3monthsBack;
import static hr.eazework.com.ui.util.CalenderUtils.getToday;

/**
 * Created by Manjunath on 02-04-2017.
 */

public class TeamMemberHistory extends BaseFragment implements AdapterView.OnItemClickListener {
    private ArrayList<History> list = new ArrayList<>();
    protected ListView listView;
    public static String TAG = "TeamMemberHistory";
    private String empId = "";
    private History model = null;
    private RelativeLayout search_layout;
    private LinearLayout searchParentLayout, errorLinearLayout;
    private EditText searchET;
    private ImageView searchIV, filterIV, searchCancelIV, clearTextIV;
    private String searchTag = null;
    private AttendanceApprovalResponse attendanceApprovalResponse;
    private RelativeLayout norecordLayout;
    private ListViewHistoryAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        empId = getArguments().getString(AppsConstant.EMP_ID, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.history_list, container, false);
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
        listView = (ListView) rootView.findViewById(R.id.expandableList);
        adapter = new ListViewHistoryAdapter(getContext(), R.layout.history_list_item, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        requestAPI();
        clearTextIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (searchTag.toString().equalsIgnoreCase(getResources().getString(R.string.date_search))) {
                    searchET.setText("");
                    searchET.setHint("Enter Date(dd/mm/yyyy)");
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
                        ArrayList<History> leaveModels = new ArrayList<>();

                        if (searchTag.equalsIgnoreCase(getResources().getString(R.string.date_search))) {
                            History employeeLeaveModel = model;

                            if (employeeLeaveModel != null) {
                                leaveModels = employeeLeaveModel.getmHistoryList();
                            }
                            if (leaveModels == null)
                                leaveModels = new ArrayList<>();

                            ArrayList<History> filterList = new ArrayList<History>();
                            for (History item : leaveModels) {
                                if (item.getmMarkDate().toUpperCase().contains(s.toString().toUpperCase())) {
                                    filterList.add(item);

                                }
                            }
                            populateViewsList(filterList);
                        }




                    } else {
                        populateViewsList(model.getmHistoryList());

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
        CommunicationManager.getInstance().sendPostRequest(this, AppRequestJSONString.getHistory(get3monthsBack(), getToday(), empId), CommunicationConstant.API_GET_EMP_ATTENDANCE_CALENDER_STATUS, false);
    }


    @Override
    public void validateResponse(ResponseData response) {
        super.validateResponse(response);
        switch (response.getRequestData().getReqApiId()) {
            case CommunicationConstant.API_GET_EMP_ATTENDANCE_CALENDER_STATUS:
                searchParentLayout.setVisibility(View.GONE);
                JSONObject json;
                try {
                    json = new JSONObject(response.getResponseData());
                    JSONObject mainJSONObject = json.optJSONObject("GetEmpAttendanceCalendarStatusResult");
                    if (mainJSONObject.optInt("ErrorCode", -1) != 0) {
                        String errorMessage = mainJSONObject.optString("ErrorMessage", "Failed");
                        new AlertCustomDialog(getActivity(), errorMessage);
                    } else {
                        String getEmpAttendanceCalendarStatusResult = json.optJSONObject("GetEmpAttendanceCalendarStatusResult").toString();
                        JSONArray array = new JSONObject(getEmpAttendanceCalendarStatusResult).optJSONArray("attendCalStatusList");
                        if(array!=null && array.length()>0) {
                            searchParentLayout.setVisibility(View.VISIBLE);

                        }
                        populateViews(array);
                    }

                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    Log.e(TAG, e.getMessage(), e);
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

                searchList.add(getResources().getString(R.string.date_search));

                CustomBuilder searchCustomBuilder = new CustomBuilder(getContext(), "Search By", false);
                searchCustomBuilder.setSingleChoiceItems(searchList, null, new CustomBuilder.OnClickListener() {
                    @Override
                    public void onClick(CustomBuilder builder, Object selectedObject) {
                        search_layout.setVisibility(View.INVISIBLE);
                        searchCancelIV.setVisibility(View.VISIBLE);
                        searchIV.setVisibility(View.GONE);
                        searchTag = selectedObject.toString();
                       if (selectedObject.toString().equalsIgnoreCase(getResources().getString(R.string.date_search))) {
                            search_layout.setVisibility(View.VISIBLE);
                            searchET.setHint("Enter Date(dd/mm/yyyy)");
                            searchTag = getResources().getString(R.string.date_search);
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
        model = new History(array);
        list = model.getmHistoryList();
        if (adapter != null) {
            adapter.refresh(list);
        }
    }

    private void populateViewsList(ArrayList<History> list) {
        norecordLayout.setVisibility(View.GONE);
        if(list!=null && list.size()<=0){
            norecordLayout.setVisibility(View.VISIBLE);
        }
        if (adapter != null) {
            adapter.refresh(list);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        History.setCurrentHistoryItem(model);
        History history = list.get(position);
        AttendanceUtil.initiateHistoryTrackFragment(view, empId, history.getmMarkDate(), position, model, mUserActionListener);
    }


}
