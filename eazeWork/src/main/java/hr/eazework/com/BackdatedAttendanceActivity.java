package hr.eazework.com;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hr.eazework.com.BaseActivity;
import hr.eazework.com.FileUtils;
import hr.eazework.com.R;
import hr.eazework.com.TimeModificationActivity;
import hr.eazework.com.model.AdvanceRequestResponseModel;
import hr.eazework.com.model.AttandanceCalenderStatusItem;
import hr.eazework.com.model.AttendanceItem;
import hr.eazework.com.model.AttendanceRejectItem;
import hr.eazework.com.model.AttendanceRejectRequestModel;
import hr.eazework.com.model.AttendanceRejectResponseModel;
import hr.eazework.com.model.AttendanceReqDetail;
import hr.eazework.com.model.EmpAttendanceDetailResponseModel;
import hr.eazework.com.model.EmpAttendanceRequestModel;
import hr.eazework.com.model.EmployeeLeaveModel;
import hr.eazework.com.model.GetTimeModificationRequestDetail;
import hr.eazework.com.model.LoginUserModel;
import hr.eazework.com.model.ModelManager;
import hr.eazework.com.model.RemarkListItem;
import hr.eazework.com.model.SupportDocsItemModel;
import hr.eazework.com.model.TimeModificationItem;
import hr.eazework.com.model.TimeModificationRequestModel;
import hr.eazework.com.model.TimeModificationResponseModel;
import hr.eazework.com.model.TimeModificationSummaryResponseModel;
import hr.eazework.com.model.WFHRequestDetailItem;
import hr.eazework.com.ui.adapter.DocumentUploadAdapter;
import hr.eazework.com.ui.adapter.RemarksAdapter;
import hr.eazework.com.ui.customview.CustomBuilder;
import hr.eazework.com.ui.customview.CustomDialog;
import hr.eazework.com.ui.interfaces.IAction;
import hr.eazework.com.ui.util.AppsConstant;
import hr.eazework.com.ui.util.CalenderUtils;
import hr.eazework.com.ui.util.ImageUtil;
import hr.eazework.com.ui.util.PermissionUtil;
import hr.eazework.com.ui.util.Preferences;
import hr.eazework.com.ui.util.Utility;
import hr.eazework.com.ui.util.custom.AlertCustomDialog;
import hr.eazework.mframe.communication.ResponseData;
import hr.eazework.selfcare.communication.AppRequestJSONString;
import hr.eazework.selfcare.communication.CommunicationConstant;
import hr.eazework.selfcare.communication.CommunicationManager;

import static hr.eazework.com.ui.util.ImageUtil.rotateImage;

/**
 * Created by Dell3 on 05-02-2018.
 */

public class BackdatedAttendanceActivity extends BaseActivity {
    private String screenName = "TimeModificationActivity";
    private String status;
    public static int TIMEMODIFICATIONREQUESTCODE = 11;
    private Context context;
    private Preferences preferences;
    private TextView empNameTV, tv_header_text, dateTV, statusTV, tv_in_day, tv_in_date, tv_in_time, tv_out_day, tv_out_date, tv_out_time, remarksET;
    private LinearLayout rl_edit_team_member, statusLinearLayout, requestedOutTimeLl, requestedInTimeLl, remarksLinearLayout, remarksLl;
    private String currentDate, inDate = "", outDate = "", inTime = "", outTime = "", empId, reqInTime, reqOutTime, attendId;
    private LinearLayout inDateLl, outDateLl, inTimeLl, outTimeLl;
    private RelativeLayout backLayout, rightRL;
    private List<String> extensionList;
    private DatePickerDialog datePickerDialog1, datePickerDialog2;
    private TimePickerDialog timePickerDialog1, timePickerDialog2;
    private TimeModificationResponseModel timeModificationResponseModel;
    public static AttandanceCalenderStatusItem attandanceCalenderStatusItem;
    public static AttendanceItem employeeLeaveModel;
    private RecyclerView remarksRV;
    private AttendanceReqDetail reqDetail;
    private Button rejectBTN, approvalBTN;
    private String fromButton;
    private LinearLayout markedTimeLl;
    private TimeModificationSummaryResponseModel summaryResponseModel;
    private View progressbar;
    private ImageView quickHelpIV;
    private String currentScreen = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.attendance_details_layout);
        progressbar =(LinearLayout)findViewById(R.id.ll_progress_container);
        progressbar.bringToFront();
        preferences = new Preferences(context);
        int textColor = Utility.getTextColorCode(preferences);
        int bgColor = Utility.getBgColorCode(context, preferences);
        tv_header_text = (TextView) findViewById(R.id.tv_header_text);
        tv_header_text.setText(context.getResources().getString(R.string.Attendance_Request));
        tv_header_text.setTextColor(textColor);
        rl_edit_team_member = (LinearLayout) findViewById(R.id.rl_edit_team_member);
        rl_edit_team_member.setBackgroundColor(bgColor);

        backLayout = (RelativeLayout) findViewById(R.id.backLayout);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rightRL = (RelativeLayout) findViewById(R.id.rightRL);
        rightRL.setVisibility(View.VISIBLE);
        rightRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromButton = "Submit";
                doSubmitOperation();
            }
        });
        markedTimeLl = (LinearLayout) findViewById(R.id.markedTimeLl);
        markedTimeLl.setVisibility(View.GONE);
        empNameTV = (TextView) findViewById(R.id.empNameTV);
        LoginUserModel loginUserModel = ModelManager.getInstance().getLoginUserModel();
        empId = loginUserModel.getUserModel().getEmpId();
        empNameTV.setText(loginUserModel.getUserModel().getUserName());

        remarksLl = (LinearLayout) findViewById(R.id.remarksLl);
        remarksLl.setVisibility(View.GONE);
        dateTV = (TextView) findViewById(R.id.dateTV);
        statusTV = (TextView) findViewById(R.id.statusTV);
        tv_in_day = (TextView) findViewById(R.id.tv_in_day);
        tv_in_date = (TextView) findViewById(R.id.tv_in_date);
        tv_in_time = (TextView) findViewById(R.id.tv_in_time);
        tv_out_day = (TextView) findViewById(R.id.tv_out_day);
        tv_out_date = (TextView) findViewById(R.id.tv_out_date);
        tv_out_time = (TextView) findViewById(R.id.tv_out_time);
        remarksET = (TextView) findViewById(R.id.remarksET);
        inDateLl = (LinearLayout) findViewById(R.id.inDateLl);
        outDateLl = (LinearLayout) findViewById(R.id.outDateLl);
        inTimeLl = (LinearLayout) findViewById(R.id.inTimeLl);
        outTimeLl = (LinearLayout) findViewById(R.id.outTimeLl);

        datePickerDialog1 = CalenderUtils.pickDateFromCalender(context, tv_in_date, tv_in_day, AppsConstant.DATE_FORMATE);
        datePickerDialog2 = CalenderUtils.pickDateFromCalender(context, tv_out_date, tv_out_day, AppsConstant.DATE_FORMATE);

        inDateLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog1.show();
            }
        });
        outDateLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog2.show();
            }
        });

        timePickerDialog1 = Utility.setTime(context, tv_in_time);
        timePickerDialog2 = Utility.setTime(context, tv_out_time);
        inTimeLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog1.show();
            }
        });
        outTimeLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog2.show();
            }
        });

        remarksLinearLayout = (LinearLayout) findViewById(R.id.remarksLinearLayout);
        remarksRV = (RecyclerView) findViewById(R.id.remarksRV);

        if (attandanceCalenderStatusItem != null) {
            sendEmpAttendanceRequest();
            updateUI();
        }

        if (employeeLeaveModel != null && employeeLeaveModel.getReqID() != null
                && !employeeLeaveModel.getReqID().equalsIgnoreCase("0")) {
            sendViewRequestSummaryData();
            rightRL.setVisibility(View.GONE);
        }

        rejectBTN = (Button) findViewById(R.id.rejectBTN);
        rejectBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rejectAttendanceRequest();
            }
        });

        approvalBTN = (Button) findViewById(R.id.approvalBTN);
        approvalBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromButton = "Approve";
                doSubmitOperation();
            }
        });

        quickHelpIV = (ImageView) findViewById(R.id.quickHelpIV);
        currentScreen=AppsConstant.Backdated_Attendance_Request;
        quickHelpIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CurrentScreen", currentScreen);
                Intent intent = new Intent(context, GetQuickHelpActivity.class);
                intent.putExtra("CurrentScreen", currentScreen);
                startActivity(intent);
            }
        });
    }

    private void sendViewRequestSummaryData() {
        GetTimeModificationRequestDetail getTimeModificationRequestDetail = new GetTimeModificationRequestDetail();
        getTimeModificationRequestDetail.setReqID(employeeLeaveModel.getReqID());
        getTimeModificationRequestDetail.setAction(AppsConstant.EDIT_ACTION);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.timeModificationSummaryDetails(getTimeModificationRequestDetail),
                CommunicationConstant.API_GET_ATTENDANCE_DETAIL, true);
    }

    private void sendEmpAttendanceRequest() {
        EmpAttendanceRequestModel empAttendanceRequestModel = new EmpAttendanceRequestModel();
        empAttendanceRequestModel.setAttendID(attandanceCalenderStatusItem.getAttendID());
        empAttendanceRequestModel.setForEmpID(empId);
        empAttendanceRequestModel.setMarkDate(attandanceCalenderStatusItem.getMarkDate());
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.empAttendanceDetail(empAttendanceRequestModel),
                CommunicationConstant.API_GET_EMP_ATTENDANCE_DETAIL, true);
    }

    private void doSubmitOperation() {
        inDate = tv_in_date.getText().toString();
        outDate = tv_out_date.getText().toString();
        inTime = tv_in_time.getText().toString();
        outTime = tv_out_time.getText().toString();
        if (inDate.equalsIgnoreCase("") || inDate.equalsIgnoreCase("--/--/----")) {
            new AlertCustomDialog(context, context.getResources().getString(R.string.enter_in_date));
            return;
        } else if (inTime.equalsIgnoreCase("") || inTime.equalsIgnoreCase("--:-- AM")) {
            new AlertCustomDialog(context, getResources().getString(R.string.enter_in_time));
            return;
        } else if (outDate.equalsIgnoreCase("") || outDate.equalsIgnoreCase("--/--/----")) {
            new AlertCustomDialog(context, context.getResources().getString(R.string.enter_out_date));
            return;
        } else if (outTime.equalsIgnoreCase("") || outTime.equalsIgnoreCase("--:-- AM")) {
            new AlertCustomDialog(context, getResources().getString(R.string.enter_out_time));
            return;
        } else if (remarksET.getText().toString().equalsIgnoreCase("")) {
            new AlertCustomDialog(context, context.getResources().getString(R.string.enter_remarks));
            return;
        } else {
            if (fromButton.equalsIgnoreCase(AppsConstant.SUBMIT)) {
                reqInTime = inDate + " " + inTime;
                reqOutTime = outDate + " " + outTime;
                TimeModificationItem timeModificationItem = new TimeModificationItem();
                timeModificationItem.setForEmpID(empId);
                timeModificationItem.setAttendID(attendId);
                // timeModificationItem.setBreakID("0");
                timeModificationItem.setReqTime(reqInTime);
                timeModificationItem.setReqOutTime(reqOutTime);
                timeModificationItem.setRemark(remarksET.getText().toString());

                TimeModificationRequestModel timeModificationRequestModel = new TimeModificationRequestModel();
                timeModificationRequestModel.setRequest(timeModificationItem);
                Utility.showHidePregress(progressbar,true);

                CommunicationManager.getInstance().sendPostRequest(this,
                        AppRequestJSONString.timeModificationRequest(timeModificationRequestModel),
                        CommunicationConstant.API_BACKDATED_ATTENDANCE_REQUEST, true);
            }

            if (fromButton.equalsIgnoreCase(AppsConstant.APPROVE)) {
                reqInTime = inDate + " " + inTime;
                reqOutTime = outDate + " " + outTime;
                TimeModificationItem timeModificationItem = new TimeModificationItem();
                timeModificationItem.setForEmpID(empId);
                timeModificationItem.setAttendID(attendId);
                timeModificationItem.setReqTime(reqInTime);
                timeModificationItem.setReqOutTime(reqOutTime);
                timeModificationItem.setStatus(status);
                timeModificationItem.setReqId(employeeLeaveModel.getReqID());
                timeModificationItem.setApprovalLevel(reqDetail.getApprovalLevel());
                timeModificationItem.setStatus(reqDetail.getStatus());
                timeModificationItem.setRemark(remarksET.getText().toString());

                TimeModificationRequestModel timeModificationRequestModel = new TimeModificationRequestModel();
                timeModificationRequestModel.setRequest(timeModificationItem);
                Utility.showHidePregress(progressbar,true);
                CommunicationManager.getInstance().sendPostRequest(this,
                        AppRequestJSONString.timeModificationRequest(timeModificationRequestModel),
                        CommunicationConstant.API_APPROVE_ATTENDANCE_REQUEST, true);
            }
        }

    }

    @Override
    public void validateResponse(ResponseData response) {
        Log.d("TAG", "response data " + response.isSuccess());
        Utility.showHidePregress(progressbar,false);

        switch (response.getRequestData().getReqApiId()) {
            case CommunicationConstant.API_GET_EMP_ATTENDANCE_DETAIL:
                String responseData1 = response.getResponseData();
                Log.d("TAG", "time modification response : " + responseData1);
                EmpAttendanceDetailResponseModel empAttendanceDetailResponseModel = EmpAttendanceDetailResponseModel.create(responseData1);
                if (empAttendanceDetailResponseModel != null && empAttendanceDetailResponseModel.getGetEmpAttendanceDetailResult() != null
                        && empAttendanceDetailResponseModel.getGetEmpAttendanceDetailResult()
                        .getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS) && empAttendanceDetailResponseModel.getGetEmpAttendanceDetailResult().getAttendanceCalStatus()!=null) {

                    tv_in_time.setText(empAttendanceDetailResponseModel.getGetEmpAttendanceDetailResult().getAttendanceCalStatus().getTimeIn());
                    outTime = tv_in_time.getText().toString();

                    tv_out_time.setText(empAttendanceDetailResponseModel.getGetEmpAttendanceDetailResult().getAttendanceCalStatus().getTimeOut());
                    outTime = tv_out_time.getText().toString();

                    tv_in_date.setText(empAttendanceDetailResponseModel.getGetEmpAttendanceDetailResult().getAttendanceCalStatus().getInDate());
                    inDate=tv_in_date.getText().toString();

                    tv_out_date.setText(empAttendanceDetailResponseModel.getGetEmpAttendanceDetailResult().getAttendanceCalStatus().getOutDate());
                    outDate=tv_out_date.getText().toString();
                } else {
                    new AlertCustomDialog(context, timeModificationResponseModel.getBackDateAttendanceResult().getErrorMessage());
                }
                break;
            case CommunicationConstant.API_BACKDATED_ATTENDANCE_REQUEST:
                String responseData = response.getResponseData();
                Log.d("TAG", "time modification response : " + responseData);
                timeModificationResponseModel = TimeModificationResponseModel.create(responseData);
                if (timeModificationResponseModel != null && timeModificationResponseModel.getBackDateAttendanceResult() != null
                        && timeModificationResponseModel.getBackDateAttendanceResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                    CustomDialog.alertOkWithFinishActivity(context, timeModificationResponseModel.getBackDateAttendanceResult().getErrorMessage(), BackdatedAttendanceActivity.this, true);

                    //CustomDialog.alertOkWithFinishFragment(context, timeModificationResponseModel.getTimeModificationResult().getErrorMessage(), null, IAction.HOME_VIEW, true);
                } else {
                    new AlertCustomDialog(context, timeModificationResponseModel.getBackDateAttendanceResult().getErrorMessage());
                }
                break;
            case CommunicationConstant.API_GET_ATTENDANCE_DETAIL:
                String str = response.getResponseData();
                Log.d("TAG", "Time Modification Summary Response : " + str);
                summaryResponseModel = TimeModificationSummaryResponseModel.create(str);
                if (summaryResponseModel != null && summaryResponseModel.getGetAttendanceReqDetailResult() != null
                        && summaryResponseModel.getGetAttendanceReqDetailResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)
                        && summaryResponseModel.getGetAttendanceReqDetailResult().getAttendanceReqDetail() != null) {
                    reqDetail = summaryResponseModel.getGetAttendanceReqDetailResult().getAttendanceReqDetail();
                    remarksLl.setVisibility(View.VISIBLE);
                    updateUIForApproval(summaryResponseModel.getGetAttendanceReqDetailResult().getAttendanceReqDetail());
                    refreshRemarksList(summaryResponseModel.getGetAttendanceReqDetailResult().getAttendanceReqDetail().getRemarkList());
                }

                break;
            case CommunicationConstant.API_REJECT_ATTENDANCE_REQUEST:
                String respDataAttendance = response.getResponseData();
                Log.d("TAG", "reject attendance response : " + respDataAttendance);
                AttendanceRejectResponseModel attendanceRejectResponseModel = AttendanceRejectResponseModel.create(respDataAttendance);
                if (attendanceRejectResponseModel != null && attendanceRejectResponseModel.getRejectRequestResult() != null
                        && attendanceRejectResponseModel.getRejectRequestResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                    //CustomDialog.alertOkWithFinishFragment(context, attendanceRejectResponseModel.getRejectRequestResult().getErrorMessage(), mUserActionListener, IAction.HOME_VIEW, true);
                    // CustomDialog.alertOkWithFinish(context, attendanceRejectResponseModel.getRejectRequestResult().getErrorMessage());
                    CustomDialog.alertOkWithFinishActivity(context, attendanceRejectResponseModel.getRejectRequestResult().getErrorMessage(), BackdatedAttendanceActivity.this, true);

                } else {
                    new AlertCustomDialog(context, attendanceRejectResponseModel.getRejectRequestResult().getErrorMessage());
                }
                break;
            case CommunicationConstant.API_APPROVE_ATTENDANCE_REQUEST:
                String respDataAttend = response.getResponseData();
                Log.d("TAG", "reject attendance response : " + respDataAttend);
                AttendanceRejectResponseModel attendanceApproveResponseModel = AttendanceRejectResponseModel.create(respDataAttend);
                if (attendanceApproveResponseModel != null && attendanceApproveResponseModel.getApproveRequestResult() != null
                        && attendanceApproveResponseModel.getApproveRequestResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                    //CustomDialog.alertOkWithFinishFragment(context, attendanceApproveResponseModel.getApproveRequestResult().getErrorMessage(), mUserActionListener, IAction.HOME_VIEW, true);
                    CustomDialog.alertOkWithFinish(context, attendanceApproveResponseModel.getApproveRequestResult().getErrorMessage());
                    CustomDialog.alertOkWithFinishActivity(context, attendanceApproveResponseModel.getApproveRequestResult().getErrorMessage(), BackdatedAttendanceActivity.this, true);

                } else {
                    new AlertCustomDialog(context, attendanceApproveResponseModel.getApproveRequestResult().getErrorMessage());
                }
                break;

            default:
                break;
        }
        super.validateResponse(response);
    }

    private void updateUI() {
        attendId = attandanceCalenderStatusItem.getAttendID();
     /*   tv_in_date.setText(attandanceCalenderStatusItem.getInDate());
        inDate = attandanceCalenderStatusItem.getInDate();
        tv_out_date.setText(attandanceCalenderStatusItem.getOutDate());
        outDate = attandanceCalenderStatusItem.getOutDate();*/
        if (attandanceCalenderStatusItem.getTimeIn().equalsIgnoreCase("null")) {
            tv_in_time.setText("--:-- AM");
            inTime = tv_in_time.getText().toString();

        } else {
            tv_in_time.setText(attandanceCalenderStatusItem.getTimeIn());
            inTime = tv_in_time.getText().toString();
        }
      /*  if (attandanceCalenderStatusItem.getTimeIn().equalsIgnoreCase("null")) {
            tv_out_time.setText("--:-- AM");
            outTime = tv_out_time.getText().toString();
        } else {
            tv_out_time.setText(attandanceCalenderStatusItem.getTimeOut());
            outTime = tv_out_time.getText().toString();
        }*/
        datePickerDialog1 = CalenderUtils.pickDateFromCalender(context, tv_in_date, tv_in_day, AppsConstant.DATE_FORMATE);
        datePickerDialog2 = CalenderUtils.pickDateFromCalender(context, tv_out_date, tv_out_day, AppsConstant.DATE_FORMATE);
        dateTV.setText(attandanceCalenderStatusItem.getMarkDate());
        statusTV.setText(attandanceCalenderStatusItem.getStatusDesc());


    }

    private void updateUIForApproval(AttendanceReqDetail item) {
        status=item.getStatus();
        attendId = item.getAttendID();
        empId = item.getEmpID() + "";
        dateTV.setText(item.getMarkDate());
        statusTV.setText(item.getDayStatusDesc());
        empNameTV.setText(item.getName());
        String[] dateTime = item.getReqTime().split(" ");
        inDate = dateTime[0];
        if (dateTime.length == 3) {
            inTime = dateTime[1] + " " + dateTime[2];
        } else {
            inTime = dateTime[1];
        }
        tv_in_date.setText(inDate);
        tv_in_time.setText(inTime);

        String[] outTimeDate = item.getReqOutTime().split(" ");
        outDate = outTimeDate[0];
        if (outTimeDate.length == 3) {
            outTime = outTimeDate[1] + " " + outTimeDate[2];
        } else {
            outTime = outTimeDate[1];
        }
        tv_out_date.setText(outDate);
        tv_out_time.setText(outTime);
        datePickerDialog1 = CalenderUtils.pickDateFromCalender(context, tv_in_date, tv_in_day, AppsConstant.DATE_FORMATE);
        datePickerDialog2 = CalenderUtils.pickDateFromCalender(context, tv_out_date, tv_out_day, AppsConstant.DATE_FORMATE);

        remarksET.setText("");
        setupButtons(item);
    }

    private void refreshRemarksList(ArrayList<RemarkListItem> remarksItems) {
        if (remarksItems != null && remarksItems.size() > 0) {
            remarksLinearLayout.setVisibility(View.GONE);
            remarksRV.setLayoutManager(new LinearLayoutManager(context));
            remarksRV.setVisibility(View.VISIBLE);
            RemarksAdapter adapter = new RemarksAdapter(remarksItems, context, screenName, remarksLinearLayout);
            remarksRV.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            remarksLinearLayout.setVisibility(View.VISIBLE);
            remarksRV.setVisibility(View.GONE);
        }
    }

    private void rejectAttendanceRequest() {
        if (summaryResponseModel != null && summaryResponseModel.getGetAttendanceReqDetailResult() != null
                && summaryResponseModel.getGetAttendanceReqDetailResult().getAttendanceReqDetail() != null) {
            if (remarksET.getText().toString().equalsIgnoreCase("")) {
                new AlertCustomDialog(context, context.getResources().getString(R.string.enter_remarks));
                return;
            }
            AttendanceRejectRequestModel rejectRequestModel = new AttendanceRejectRequestModel();
            AttendanceRejectItem attendanceRejectItem = new AttendanceRejectItem();
            attendanceRejectItem.setReqID(reqDetail.getReqID());
            attendanceRejectItem.setRemark(remarksET.getText().toString());
            attendanceRejectItem.setApprovalLevel(reqDetail.getApprovalLevel());
            attendanceRejectItem.setStatus(status);
            rejectRequestModel.setRequest(attendanceRejectItem);
            CommunicationManager.getInstance().sendPostRequest(this,
                    AppRequestJSONString.rejectAttendanceRequest(rejectRequestModel),
                    CommunicationConstant.API_REJECT_ATTENDANCE_REQUEST, true);
        }
    }

    private void setupButtons(AttendanceReqDetail item) {
        if (item.getButtons() != null) {
            for (String button : item.getButtons()) {
                if (button.equalsIgnoreCase(AppsConstant.REJECT)) {
                    rejectBTN.setVisibility(View.VISIBLE);
                }
                if (button.equalsIgnoreCase(AppsConstant.APPROVE)) {
                    approvalBTN.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
