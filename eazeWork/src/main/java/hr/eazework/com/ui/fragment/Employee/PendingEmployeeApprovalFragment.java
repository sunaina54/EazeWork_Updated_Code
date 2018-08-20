package hr.eazework.com.ui.fragment.Employee;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import hr.eazework.com.MainActivity;
import hr.eazework.com.R;
import hr.eazework.com.model.AttendanceApprovalResponse;
import hr.eazework.com.model.MemberApprovalModel;
import hr.eazework.com.model.ModelManager;
import hr.eazework.com.ui.customview.CustomBuilder;
import hr.eazework.com.ui.fragment.BaseFragment;
import hr.eazework.com.ui.interfaces.IAction;
import hr.eazework.com.ui.util.Utility;
import hr.eazework.com.ui.util.custom.AlertCustomDialog;
import hr.eazework.mframe.communication.ResponseData;
import hr.eazework.selfcare.communication.AppRequestJSONString;
import hr.eazework.selfcare.communication.CommunicationConstant;
import hr.eazework.selfcare.communication.CommunicationManager;
import hr.eazework.selfcare.communication.IBaseResponse;


public class PendingEmployeeApprovalFragment extends BaseFragment {

    public static final String TAG = "PendingEmployeeApprovalFragment";
    private RelativeLayout search_layout;
    private LinearLayout searchParentLayout, errorLinearLayout;

    private EditText searchET;
    private ImageView searchIV, filterIV, searchCancelIV, clearTextIV;
    private String searchTag = null;
    private AttendanceApprovalResponse attendanceApprovalResponse;
    private RelativeLayout norecordLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.employee_pending_approval, container, false);

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
        rootView.findViewById(R.id.tv_apply_leave).setOnClickListener(this);
        populateLeaves(null);
        if (Utility.isNetworkAvailable(getContext())) {
            MainActivity.isAnimationLoaded = false;
            CommunicationManager.getInstance().sendPostRequest(this, AppRequestJSONString.getSimpleRequestSessionDataStr(), CommunicationConstant.APT_GET_PENDING_MEMBER_APPROVAL, false);
            showHideProgressView(true);
        } else {
            new AlertCustomDialog(getActivity(), getString(R.string.msg_internet_connection));
        }
        clearTextIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchTag.toString().equalsIgnoreCase(getResources().getString(R.string.request_id_search_attendance))) {
                    searchET.setText("");
                    searchET.setHint("Enter Request ID");
                }else if (searchTag.toString().equalsIgnoreCase(getResources().getString(R.string.date_search))) {
                    searchET.setText("");
                    searchET.setHint("Enter Date(dd/mm/yyyy)");
                } else if (searchTag.toString().equalsIgnoreCase(getResources().getString(R.string.request_for_search))) {
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
                        ArrayList<MemberApprovalModel> leaveModels = new ArrayList<>();
                        if (searchTag.equalsIgnoreCase(getResources().getString(R.string.request_id_search_attendance))) {
                            MemberApprovalModel employeeLeaveModel = ModelManager.getInstance().getMemberApprovalModel();

                            if (employeeLeaveModel != null) {
                                leaveModels = employeeLeaveModel.getmPendingLeaveList();
                            }
                            if (leaveModels == null)
                                leaveModels = new ArrayList<>();

                            ArrayList<MemberApprovalModel> filterList = new ArrayList<MemberApprovalModel>();
                            for (MemberApprovalModel item : leaveModels) {
                                if (item.getmReqCode().toUpperCase().contains(s.toString().toUpperCase())) {
                                    filterList.add(item);

                                }
                            }
                            populateLeaves(filterList);
                        }


                        if (searchTag.equalsIgnoreCase(getResources().getString(R.string.date_search))) {
                            MemberApprovalModel employeeLeaveModel = ModelManager.getInstance().getMemberApprovalModel();

                            if (employeeLeaveModel != null) {
                                leaveModels = employeeLeaveModel.getmPendingLeaveList();
                            }
                            if (leaveModels == null)
                                leaveModels = new ArrayList<>();

                            ArrayList<MemberApprovalModel> filterList = new ArrayList<MemberApprovalModel>();
                            for (MemberApprovalModel item : leaveModels) {
                                if (item.getmReqDate().toUpperCase().contains(s.toString().toUpperCase())) {
                                    filterList.add(item);

                                }
                            }
                            populateLeaves(filterList);
                        }


                        if (searchTag.equalsIgnoreCase(getResources().getString(R.string.request_for_search))) {
                            MemberApprovalModel employeeLeaveModel = ModelManager.getInstance().getMemberApprovalModel();

                            if (employeeLeaveModel != null) {
                                leaveModels = employeeLeaveModel.getmPendingLeaveList();
                            }
                            if (leaveModels == null)
                                leaveModels = new ArrayList<>();
                            ArrayList<MemberApprovalModel> filterList = new ArrayList<MemberApprovalModel>();

                            for (MemberApprovalModel item : leaveModels) {
                                if (item.getmInitiator().toUpperCase().contains(s.toString().toUpperCase())) {
                                    filterList.add(item);
                                }
                            }
                            populateLeaves(filterList);
                        }

                    } else {
                        populateLeaves(null);

                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return rootView;
    }

    private void populateLeaves(ArrayList<MemberApprovalModel> leaveModels) {
        int totalPendingMemmberApprovals = ModelManager.getInstance().getTotalPendingMemmberApprovals();
        ((TextView) rootView.findViewById(R.id.tv_leave_count)).setText("" + totalPendingMemmberApprovals);
        View view = rootView.findViewById(R.id.ll_pending_item_container);
        view.setVisibility(View.VISIBLE);
        norecordLayout.setVisibility(View.GONE);
        if (leaveModels != null && leaveModels.size() <= 0) {
            norecordLayout.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
            return;
        }
        if(leaveModels == null) {
            MemberApprovalModel employeeLeaveModel = ModelManager.getInstance().getMemberApprovalModel();

            if (employeeLeaveModel != null) {
                leaveModels = employeeLeaveModel.getmPendingLeaveList();
            }
            if (leaveModels == null)
                leaveModels = new ArrayList<>();
        }
        if (view instanceof LinearLayout) {
            LinearLayout layout = (LinearLayout) view;
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            layout.removeAllViews();
            if (leaveModels != null && leaveModels.size() > 0) {
                for (MemberApprovalModel leaveModel : leaveModels) {
                    View seperator = new View(getActivity());
                    View lView = inflater.inflate(R.layout.employee_detail_item, layout, false);
                    ((TextView) lView.findViewById(R.id.tv_leave_employee_name)).setVisibility(View.VISIBLE);
                    ((TextView) lView.findViewById(R.id.tv_request_id)).setVisibility(View.VISIBLE);
                    ((TextView) lView.findViewById(R.id.tv_requested_by)).setVisibility(View.VISIBLE);
                    ((TextView) lView.findViewById(R.id.tv_leave_employee_name)).setText(String.format(getString(R.string.leave_employee_name_format), leaveModel.getmName()));

                    ((TextView) lView.findViewById(R.id.tv_leave_from_to)).setText(String.format(getString(R.string.requested_date), leaveModel.getmReqDate()));
                    ((TextView) lView.findViewById(R.id.tv_request_id)).setText(getString(R.string.request_id) + " " + leaveModel.getmReqCode());
                    ((TextView) lView.findViewById(R.id.tv_requested_by)).setText(getString(R.string.requested_by) + " " + leaveModel.getmInitiator());

                    lView.findViewById(R.id.tv_accept).setVisibility(View.VISIBLE);
                    lView.findViewById(R.id.tv_reject).setVisibility(View.VISIBLE);
                    lView.findViewById(R.id.tv_accept).setTag(leaveModel);
                    lView.findViewById(R.id.tv_reject).setTag(leaveModel);
                    lView.findViewById(R.id.tv_accept).setOnClickListener(this);
                    lView.findViewById(R.id.tv_reject).setOnClickListener(this);
                    seperator.setBackgroundResource(R.color.transparent);
                    seperator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2));
                    layout.addView(lView);
                    layout.addView(seperator);
                }
            } else {


                View lView = inflater.inflate(R.layout.employee_detail_item, layout, false);
                ((TextView) lView.findViewById(R.id.tv_leave_type)).setText("There are no employee record pending for your approval");

                ((TextView) lView.findViewById(R.id.tv_leave_from_to)).setVisibility(View.GONE);
                ((TextView) lView.findViewById(R.id.tv_request_id)).setVisibility(View.GONE);
                ((TextView) lView.findViewById(R.id.tv_requested_by)).setVisibility(View.GONE);
                lView.findViewById(R.id.tv_accept).setVisibility(View.GONE);
                lView.findViewById(R.id.tv_reject).setVisibility(View.GONE);

                layout.addView(lView);
            }
        }
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
                searchList.add(getResources().getString(R.string.date_search));
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
                        }  else if (selectedObject.toString().equalsIgnoreCase(getResources().getString(R.string.date_search))) {
                            search_layout.setVisibility(View.VISIBLE);
                            searchET.setHint("Enter Date(dd/mm/yyyy)");
                            searchTag = getResources().getString(R.string.date_search);
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
            case R.id.tv_accept:
                updateEmployeeStatus(v, "approve", "Employee approved", "A");
                break;
            case R.id.tv_reject:
                updateEmployeeStatus(v, "reject", "Employee rejected", "R");
                break;

            default:
                break;
        }
        super.onClick(v);
    }

    public void updateEmployeeStatus(View v, String status, final String toastMessage, final String updateFlag) {
        final MemberApprovalModel acceptModel = (MemberApprovalModel) v.getTag();
        if (Utility.isNetworkAvailable(getContext())) {
            new AlertCustomDialog(getActivity(), "Are you sure you want to " + status + " this request?", getString(R.string.dlg_cancel), getString(R.string.dlg_ok), new AlertCustomDialog.AlertClickListener() {

                @Override
                public void onPositiveBtnListener() {
                    showHideProgressView(true);
                    MainActivity.isAnimationLoaded = false;
                    CommunicationManager.getInstance()
                            .sendPostRequest(
                                    new IBaseResponse() {
                                        @Override
                                        public void validateResponse(ResponseData response) {
                                            showHideProgressView(false);
                                            MainActivity.isAnimationLoaded = true;
                                            try {

                                                JSONObject json = new JSONObject(response.getResponseData());
                                                JSONObject mainJSONObject = json.optJSONObject("UpdateMemberApprovalRejectionResult");
                                                if (mainJSONObject.optInt("ErrorCode", -1) != 0) {
                                                    String errorMessage = mainJSONObject.optString("ErrorMessage", "Failed");
                                                    new AlertCustomDialog(getActivity(), errorMessage);
                                                    refreshList();
                                                } else {
                                                    Utility.displayMessage(getContext(),toastMessage);
                                                    refreshList();
                                                }
                                            } catch (JSONException e) {
                                                Log.e("EmployeeApproval", e.getMessage(), e);
                                                Crashlytics.logException(e);
                                            }
                                        }
                                    },
                                    AppRequestJSONString.getUpdatePendingEmployeeStatusData(updateFlag, acceptModel.getmReqID(), acceptModel.getmReqStatus(), acceptModel.getmApprovalLevel()),
                                    CommunicationConstant.API_GET_MEMBER_PENDING_APPROVAL_STATUS,
                                    true);

                }

                @Override
                public void onNegativeBtnListener() {
                    // TODO Auto-generated method stub

                }
            });
        } else {
            new AlertCustomDialog(getActivity(), getString(R.string.msg_internet_connection));
        }
    }

    public void refreshList() {
        showHideProgressView(true);
        MainActivity.isAnimationLoaded = false;
        String josnSimpleRequestSessionDataStr = AppRequestJSONString.getSimpleRequestSessionDataStr();
        CommunicationManager.getInstance().sendPostRequest(PendingEmployeeApprovalFragment.this, josnSimpleRequestSessionDataStr, CommunicationConstant.APT_GET_PENDING_MEMBER_APPROVAL, false);
    }

    @Override
    public void validateResponse(ResponseData response) {
        showHideProgressView(false);
        MainActivity.isAnimationLoaded = true;
        if (response.isSuccess() && !isSessionValid(response.getResponseData())) {
            mUserActionListener.performUserAction(IAction.LOGIN_VIEW, null, null);
            return;
        }
        if (response.isSuccess()) {
            switch (response.getRequestData().getReqApiId()) {
                case CommunicationConstant.APT_GET_PENDING_MEMBER_APPROVAL:
                    searchParentLayout.setVisibility(View.GONE);
                    try {
                        JSONObject json = new JSONObject(response.getResponseData());
                        JSONObject mainJSONObject = json.optJSONObject("GetPendingMemberReqListResult");
                        MemberApprovalModel employeeLeaveModel = new MemberApprovalModel(mainJSONObject.optJSONArray("list"));
                        ModelManager.getInstance().setMemberApprovalModel(employeeLeaveModel);
                        ModelManager.getInstance().setTotalPendingMemmberApprovals(employeeLeaveModel.getmPendingLeaveList().size());
                        if (ModelManager.getInstance().getMemberApprovalModel() != null &&
                                ModelManager.getInstance().getMemberApprovalModel().getmPendingLeaveList() != null &&
                                ModelManager.getInstance().getMemberApprovalModel().getmPendingLeaveList().size() > 0) {
                            searchParentLayout.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        Log.e("EmployeeApproval",e.getMessage(),e);
                        Crashlytics.logException(e);
                    }
                    populateLeaves(null);
                    break;

                default:
                    break;
            }
        } else {
            new AlertCustomDialog(getActivity(), "Failed");
        }
        super.validateResponse(response);
    }
}
