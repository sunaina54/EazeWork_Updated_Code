package hr.eazework.com.ui.fragment;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import hr.eazework.Slider.Slider;
import hr.eazework.Slider.adapter.Slide;
import hr.eazework.com.BuildConfig;
import hr.eazework.com.Carasole.EnhancedWrapContentViewPager;
import hr.eazework.com.FileUtils;
import hr.eazework.com.MainActivity;
import hr.eazework.com.R;
import hr.eazework.com.model.AnnouncementItemsModel;
import hr.eazework.com.model.CheckInOutModel;
import hr.eazework.com.model.EmployeeProfileModel;
import hr.eazework.com.model.ExpenseStatusData;
import hr.eazework.com.model.ExpenseStatusModel;
import hr.eazework.com.model.FileInfo;
import hr.eazework.com.model.GeoCoderModel;
import hr.eazework.com.model.GetAnnouncementRequestModel;
import hr.eazework.com.model.GetAnnouncementResult;
import hr.eazework.com.model.GetAnnouncementResultResponseModel;
import hr.eazework.com.model.LeaveBalanceModel;
import hr.eazework.com.model.LoginUserModel;
import hr.eazework.com.model.MainItemModel;
import hr.eazework.com.model.MenuItemModel;
import hr.eazework.com.model.ModelManager;
import hr.eazework.com.model.PendingCountModel;
import hr.eazework.com.model.SalaryMonthModel;
import hr.eazework.com.model.TeamMember;
import hr.eazework.com.model.TicketResultModel;
import hr.eazework.com.model.TypeWiseListModel;
import hr.eazework.com.model.UploadProfilePicModel;
import hr.eazework.com.model.UploadProfilePicResponseModel;
import hr.eazework.com.model.UserModel;
import hr.eazework.com.ui.customview.CustomBuilder;
import hr.eazework.com.ui.customview.CustomDialog;
import hr.eazework.com.ui.interfaces.IAction;
import hr.eazework.com.ui.util.AppsConstant;
import hr.eazework.com.ui.util.AttendanceUtil;
import hr.eazework.com.ui.util.EventDataSource;
import hr.eazework.com.ui.util.GPSTracker;
import hr.eazework.com.ui.util.GeoCoder;
import hr.eazework.com.ui.util.GeoUtil;
import hr.eazework.com.ui.util.ImageUtil;
import hr.eazework.com.ui.util.PermissionUtil;
import hr.eazework.com.ui.util.Preferences;
import hr.eazework.com.ui.util.SharedPreference;
import hr.eazework.com.ui.util.Utility;
import hr.eazework.com.ui.util.custom.AlertCustomDialog;
import hr.eazework.mframe.communication.ResponseData;
import hr.eazework.selfcare.adapter.MainProfileItemListAdapter;
import hr.eazework.selfcare.communication.AppRequestJSONString;
import hr.eazework.selfcare.communication.CommunicationConstant;
import hr.eazework.selfcare.communication.CommunicationManager;


import static android.app.Activity.RESULT_OK;
import static hr.eazework.com.ui.util.Utility.requestToEnableGPS;
import static hr.eazework.com.ui.util.Utility.saveEmpConfig;


public class HomeFragment extends BaseFragment implements OnItemClickListener, OnRefreshListener {

    public static final String TAG = "HomeFragment";

    private boolean mustLoopSlides;
    private int slideShowInterval = 2000;
    private Handler handler = new Handler();
    private int currentPageNumber;
    private int slideCount;
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000;

    private static String screenName = "HomeFragment";
    ArrayList<MainItemModel> itemList;
    private ListView listView;
    private MainProfileItemListAdapter mAdapter;
    private SwipeRefreshLayout refreshLayout;
    private int currentReqType;
    private String latitude = "";
    private String longitude = "";
    private Preferences preferences;
    private boolean isFromSplash = true;
    private boolean isFromLogin = true;
    private Bitmap bitmap = null;
    private String purpose = "";
    private RelativeLayout editProfilePicLayout;
    private ImageView img_user_img;
    private static int UPLOAD_DOC_REQUEST = 1;
    private Context context;
    private static final int PERMISSION_REQUEST_CODE = 3;
    private Slider slider;
    private EnhancedWrapContentViewPager pager;
    private LinearLayout btnViewPagerLayout, pagerlayout;
    private LayoutInflater layoutInflater;
    private GetAnnouncementResultResponseModel announcementRes;
    private ViewPagerAdapter viewPagerAdapter;
    private RelativeLayout homeCarasoleRL;


    @Override
    public void refreshUi() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setShowPlusMenu(true);
        this.setShowEditTeamButtons(false);
        showHideProgressView(true);
        MainActivity.isAnimationLoaded = false;
        getHomeData();
        // getAnnouncementData();

        MenuItemModel model = ModelManager.getInstance().getMenuItemModel();
        if (model == null) {
            getMenuData();
        }
    }

    private void getMenuData() {

        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.getSallarySlipMonthData(),
                CommunicationConstant.API_GET_MENU_DATA, true);
    }

    private void getHomeData() {
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.getHomeData(),
                CommunicationConstant.API_GET_HOME_DATA, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.home_root_container, container, false);
        if (preferences == null) {
            preferences = new Preferences(getContext());
        }
        if (ModelManager.getInstance().getEmployeeProfileModel() != null) {
            MainActivity.updataProfileData(getActivity(), rootView);
        }

        //slider = (Slider) rootView.findViewById(R.id.slider);
        layoutInflater = LayoutInflater.from(context);
        homeCarasoleRL = (RelativeLayout) rootView.findViewById(R.id.homeCarasoleRL);
        pager = (EnhancedWrapContentViewPager) rootView.findViewById(R.id.pager);
        btnViewPagerLayout = (LinearLayout) rootView.findViewById(R.id.btnViewPagerLayout);
        pagerlayout = (LinearLayout) rootView.findViewById(R.id.pagerlayout);
        // viewAnnouncementData();
        getAnnouncementData();
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                btnAction(position);

                // perform functioning

            }

            @Override
            public void onPageSelected(int position) {
                currentPageNumber = position;
                Log.d("current slide", currentPageNumber + "");

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        Log.d("DeviceInfo", Build.VERSION.SDK_INT + " Release " + Build.VERSION.RELEASE + " " + BuildConfig.VERSION_CODE + " " + BuildConfig.VERSION_NAME + " " + Build.MODEL + " " + Build.MANUFACTURER);

        populateHomeData();
        img_user_img = (ImageView) rootView.findViewById(R.id.img_user_img);
        editProfilePicLayout = (RelativeLayout) rootView.findViewById(R.id.editProfilePicLayout);
        img_user_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list = new ArrayList<>();
                list.add("Take a photo");
                list.add("Gallery");
                final CustomBuilder customBuilder = new CustomBuilder(getContext(), "Upload From", false);
                customBuilder.setSingleChoiceItems(list, null, new CustomBuilder.OnClickListener() {
                            @Override
                            public void onClick(CustomBuilder builder, Object selectedObject) {
                                if (selectedObject.toString().equalsIgnoreCase("Take a photo")) {
                                    if (!PermissionUtil.checkCameraPermission(getContext()) || !PermissionUtil.checkStoragePermission(getContext())) {
                                        PermissionUtil.askAllPermissionCamera(HomeFragment.this);
                                    }
                                    if (PermissionUtil.checkCameraPermission(getContext()) && PermissionUtil.checkStoragePermission(getContext())) {
                                        Utility.openCamera(getActivity(), HomeFragment.this, AppsConstant.FRONT_CAMREA_OPEN, "ForPhoto", screenName);
                                    }
                                    customBuilder.dismiss();
                                } else if (selectedObject.toString().equalsIgnoreCase("Gallery")) {
                                    if (PermissionUtil.checkExternalStoragePermission(getActivity())) {
                                        galleryIntent();
                                        customBuilder.dismiss();
                                    } else {
                                        askLocationPermision();
                                    }

                                }
                            }
                        }
                );
                customBuilder.show();
            }
        });
        listView = (ListView) rootView.findViewById(R.id.list_profile_items);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);
        refreshLayout.setOnRefreshListener(this);
        if (Utility.isInclusiveAndAboveMarshmallow()) {
            refreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary_blue_dark, null), getResources().getColor(R.color.accent, null),
                    getResources().getColor(R.color.primary_pink, null), getResources().getColor(R.color.primary_text_grey, null));
        } else {
            refreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary_blue_dark), getResources().getColor(R.color.accent),
                    getResources().getColor(R.color.primary_pink), getResources().getColor(R.color.primary_text_grey));
        }

        mAdapter = new MainProfileItemListAdapter(getContext(), itemList);
        listView.setAdapter(mAdapter);
        rootView.findViewById(R.id.btn_check_in_out).setOnClickListener(this);
        (rootView.findViewById(R.id.btn_check_breack)).setOnClickListener(this);
        listView.setOnItemClickListener(this);
        CheckInOutModel checkInOutModel = ModelManager.getInstance().getCheckInOutModel();

        if (checkInOutModel != null) {
            CheckInOutModel model = checkInOutModel;
            updateInOutData(model.isCheckedIn(), model.isCheckedOut(), model.isBreakIn(), model.isBreakOut());
        }
        hideTimeInButtons();
        if (ModelManager.getInstance().getMenuItemModel() == null) {
            showHideProgressView(true);
        } else {
            showHideProgressView(false);
        }

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (listView == null || listView.getChildCount() == 0) ?
                                0 : listView.getChildAt(0).getTop();
                refreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        LoginUserModel loginUserModel = ModelManager.getInstance().getLoginUserModel();
        if (loginUserModel != null) {
            UserModel userModel = loginUserModel.getUserModel();
            if (userModel != null && !TextUtils.isEmpty(userModel.getUserName())) {
                Crashlytics.setUserName("Username : " + userModel.getUserName() + " EmpID : " + userModel.getEmpId());
                preferences.saveString(preferences.USERNAME, userModel.getUserName());
                preferences.commit();
            }
        }


        rootView.findViewById(R.id.ll_main_sub_layout_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuItemModel menuItemModel = ModelManager.getInstance().getMenuItemModel();
                if (menuItemModel != null) {
                    MenuItemModel model = menuItemModel.getItemModel(MenuItemModel.VIEW_PROFILE_KEY);
                    if (model != null && model.isAccess()) {
                        mUserActionListener.performUserAction(IAction.USER_PROFILE, null, null);
                    }
                }
            }
        });


        return rootView;
    }

    public void askLocationPermision() {
        this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void updateInitialTimeInOutButton(CheckInOutModel checkInOutModel) {
        if (checkInOutModel.isCheckedIn() && !checkInOutModel.isCheckedOut()) {
            ((TextView) rootView.findViewById(R.id.btn_check_in_out)).setText(getString(R.string.msg_check_out));
        } else {
            ((TextView) rootView.findViewById(R.id.btn_check_in_out)).setText(getString(R.string.msg_check_in));
        }
    }

    private void hideTimeInButtons() {
        rootView.findViewById(R.id.btn_check_in_out).setVisibility(View.GONE);
        rootView.findViewById(R.id.btn_check_breack).setVisibility(View.GONE);
    }

    private void showTimeInButtons() {
        rootView.findViewById(R.id.btn_check_in_out).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.btn_check_breack).setVisibility(View.VISIBLE);
    }


    private void updateInOutData(boolean isCheckedIn, boolean isCheckedOut, boolean isBreakIn, boolean isBreakOut) {
        CheckInOutModel model = ModelManager.getInstance().getCheckInOutModel();
        View checkBreakView = rootView.findViewById(R.id.btn_check_breack);
        View checkInOutButton = rootView.findViewById(R.id.btn_check_in_out);
        TextView checkInOutButtonTextView = (TextView) checkInOutButton;
        if (isCheckedIn && !isCheckedOut) {
            if (model != null && model.isMarkAttandanceEnable()) {
                checkInOutButtonTextView.setText(getString(R.string.msg_check_out));

                if (Utility.isInclusiveAndAboveMarshmallow()) {
                    checkInOutButtonTextView.setBackground(getResources().getDrawable(R.drawable.rounded_corner_light_blue, null));
                } else {
                    checkInOutButtonTextView.setBackground(getResources().getDrawable(R.drawable.rounded_corner_light_blue));
                }

                checkInOutButtonTextView.setTag(3);
                checkInOutButton.setVisibility(View.VISIBLE);


                if (model.getAttendanceLevel() == 3) {
                    TextView checkBreakTextView = (TextView) checkBreakView;
                    if (isBreakIn && !isBreakOut) {
                        checkBreakTextView.setText(getString(R.string.msg_break_out));
                        checkBreakView.setTag(2);
                        checkBreakView.setVisibility(View.VISIBLE);
                    } else if (isBreakOut || !isBreakIn) {
                        checkBreakTextView.setText(getString(R.string.msg_break_in));
                        checkBreakView.setTag(1);
                        checkBreakView.setVisibility(View.VISIBLE);
                    }
                } else {
                    checkBreakView.setVisibility(View.GONE);
                }
            } else {
                checkInOutButton.setVisibility(View.GONE);
                checkBreakView.setVisibility(View.GONE);
            }
        } else if (isCheckedOut) {

            if (model != null && model.isMarkAttandanceEnable()) {
                checkBreakView.setVisibility(View.GONE);
                if (model.getAttendanceLevel() == 1) {
                    if (isCheckedIn) {
                        checkInOutButton.setVisibility(View.GONE);
                    } else {
                        checkInOutButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    checkInOutButtonTextView.setText(getString(R.string.msg_cancel_out));
                    if (Utility.isInclusiveAndAboveMarshmallow()) {
                        checkInOutButtonTextView.setBackground(getResources().getDrawable(R.drawable.rounded_corner_accent, null));
                    } else {
                        checkInOutButtonTextView.setBackground(getResources().getDrawable(R.drawable.rounded_corner_accent));
                    }
                    checkInOutButton.setTag(4);
                    checkInOutButton.setVisibility(View.VISIBLE);
                }
            } else {
                checkInOutButton.setVisibility(View.GONE);
            }
        } else {
            String buttonText = getString(R.string.msg_check_in);
            if (model.getAttendanceLevel() == 1) {
                buttonText = getString(R.string.msg_mark);
            }
            setDisplayTimeInOutButton(checkBreakView, checkInOutButton, checkInOutButtonTextView, buttonText);

            if (Utility.isInclusiveAndAboveMarshmallow()) {
                checkInOutButtonTextView.setBackground(getResources().getDrawable(R.drawable.rounded_corner_light_blue, null));
            } else {
                checkInOutButtonTextView.setBackground(getResources().getDrawable(R.drawable.rounded_corner_light_blue));
            }
        }

        for (MainItemModel item : itemList) {
            if (item.getmLeftTitle().equalsIgnoreCase(getString(R.string.msg_attandance))) {
                Calendar calendar = Calendar.getInstance();
                if (model != null && model.isMarkAttandanceEnable()) {

                    if (isCheckedIn && !isCheckedOut) {
                        item.setmRightTitle(getString(R.string.msg_today_in_time));
                        item.setmRightSubTitle(String.format(getString(R.string.in_out_time_format), calendar));
                        item.setmRightSubTitle(model.getCheckInTime());
                    } else if (isCheckedIn && isCheckedOut) {
                        item.setmRightTitle(getString(R.string.msg_today_out_time));
                        item.setmRightSubTitle(String.format(getString(R.string.in_out_time_format), calendar));
                        item.setmRightSubTitle(model.getCheckOutTime());
                    } else {
                        item.setmRightTitle(getString(R.string.msg_today_in_time));
                        item.setmRightSubTitle(isCheckedIn ? ("" + model.getCheckOutTime()) : "--:--");
                    }
                } else {
                    item.setmRightSubTitle("");
                }
            }
        }

        if (!(model != null && model.isMarkAttandanceEnable())) {
            checkBreakView.setVisibility(View.GONE);
            checkInOutButton.setVisibility(View.GONE);
        }
        mAdapter.updateData(itemList);

        MenuItemModel menuItemModel = ModelManager.getInstance().getMenuItemModel();

        if (menuItemModel != null) {
            MenuItemModel itemModel = menuItemModel.getItemModel(MenuItemModel.ATTENDANCE_MARKING);
            if (itemModel != null && itemModel.isAccess()) {
                if (model.getAttendanceLevel() == 1) {
                    if (model.isCheckedIn()) {
                        checkInOutButtonTextView.setVisibility(View.GONE);
                    } else {
                        checkInOutButtonTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    checkInOutButtonTextView.setVisibility(View.VISIBLE);
                }
            } else {
                checkInOutButtonTextView.setVisibility(View.GONE);
            }
        }

    }

    private void setDisplayTimeInOutButton(View checkBreakView, View checkInOutButton, TextView checkInOutButtonTextView, String string) {
        checkBreakView.setVisibility(View.GONE);
        checkInOutButton.setVisibility(View.VISIBLE);
        checkInOutButton.setTag(0);
        checkInOutButtonTextView.setText(string);
    }

    private void updateHomeData() {
        ((MainActivity) getActivity()).updateNavigation();
        SalaryMonthModel salaryData = ModelManager.getInstance().getSalaryMonthModel();
        if (salaryData != null) {
            ArrayList<SalaryMonthModel> months = salaryData.getMonths();
            if (months.size() > 0) {
                for (MainItemModel item : itemList) {
                    if (item.getmLeftTitle().equalsIgnoreCase(getString(R.string.msg_pay_slip))) {
                        item.setmRightSubTitle(months.get(0).getmMontTitle());
                    }
                }
                mAdapter.updateData(itemList);
            }

        }
        CheckInOutModel checkInOutModel = ModelManager.getInstance().getCheckInOutModel();
        if (checkInOutModel != null) {
            updateInOutData(checkInOutModel.isCheckedIn(), checkInOutModel.isCheckedOut(), checkInOutModel.isBreakIn(), checkInOutModel.isBreakOut());
        }


    }

    private void populateHomeData() {
        if (itemList == null) {
            itemList = new ArrayList<MainItemModel>();
        } else {
            itemList.clear();
        }
        MenuItemModel menuItemModel = ModelManager.getInstance().getMenuItemModel();

        if (menuItemModel == null)
            return;

        if (menuItemModel != null) {
            MenuItemModel itemModel = menuItemModel.getItemModel(MenuItemModel.ATTANDANCE_KEY);
            if (itemModel != null && itemModel.isAccess()) {
                if (ModelManager.getInstance().getCheckInOutModel() != null
                        && ModelManager.getInstance().getCheckInOutModel().isMarkAttandanceEnable()) {
                    MainItemModel item = new MainItemModel(itemModel.getmObjectDesc(),
                            getString(R.string.msg_mark_attandance),
                            getString(R.string.msg_today_in_time), "--:--",
                            R.drawable.attendance_red);
                    item.setObjectId(itemModel.getmObjectId());
                    itemList.add(item);
                } else {
                    MainItemModel item = new MainItemModel(getString(R.string.msg_attandance),
                            getString(R.string.msg_mark_attandance),
                            R.drawable.attendance_red, false, true);
                    item.setObjectId(itemModel.getmObjectId());
                    itemList.add(item);
                }


            }
            itemModel = menuItemModel.getItemModel(MenuItemModel.TEAM_KEY);

            if (itemModel != null && itemModel.isAccess()) {
                EmployeeProfileModel employeeProfileModel = ModelManager.getInstance().getEmployeeProfileModel();
                MainItemModel item = new MainItemModel(itemModel.getmObjectDesc(),
                        getString(R.string.msg_team),
                        "View Team",
                        "" + (employeeProfileModel == null ? "0" :
                                employeeProfileModel.getmTeamSize()), R.drawable.team_blue);
                item.setObjectId(itemModel.getmObjectId());
                itemList.add(item);
            }

            itemModel = menuItemModel.getItemModel(MenuItemModel.LEAVE_KEY);
            if (itemModel != null && itemModel.isAccess()) {
                LeaveBalanceModel leaveBalanceModel = ModelManager.getInstance()
                        .getLeaveBalanceModel();
                MainItemModel item = new MainItemModel(itemModel.getmObjectDesc(),
                        getString(R.string.msg_view_leaves),
                        getString(R.string.msg_leave_balance), ""
                        + (leaveBalanceModel == null ? "0" : leaveBalanceModel.getmAvailable()),
                        R.drawable.leave_red);
                item.setObjectId(itemModel.getmObjectId());
                itemList.add(item);
            }

            itemModel = menuItemModel.getItemModel(MenuItemModel.PAY_SLIP_KEY);
            if (itemModel != null && itemModel.isAccess()) {
                SalaryMonthModel salaryData = ModelManager.getInstance().getSalaryMonthModel();

                if (salaryData != null) {
                    MainItemModel item = new MainItemModel(itemModel.getmObjectDesc(),
                            getString(R.string.msg_check_pay_slip),
                            getString(R.string.msg_last_pay_slip),
                            getString(R.string.msg_download), R.drawable.payslip_blue,
                            true);
                    item.setObjectId(itemModel.getmObjectId());
                    itemList.add(item);
                }
            }
            itemModel = menuItemModel.getItemModel(MenuItemModel.APPROVAL_KEY);

            if ((itemModel != null && itemModel.isAccess())) {
                PendingCountModel pendingCountModel = ModelManager.getInstance()
                        .getPendingCountModel();
                MainItemModel item = new MainItemModel(itemModel.getmObjectDesc(),
                        "Check approvals", getString(R.string.msg_pending_approval), "" + (pendingCountModel == null ? "0"
                        : pendingCountModel.getmPendingCount()),
                        R.drawable.manager_approval);
                item.setObjectId(itemModel.getmObjectId());
                itemList.add(item);
            } else {
                if (menuItemModel.getItemModel(MenuItemModel.EXPENSE_KEY) != null && menuItemModel.getItemModel(MenuItemModel.ADVANCE_KEY) != null
                        && menuItemModel.getItemModel(MenuItemModel.EMPLOYEE_APPROVAL_KEY) != null) {
                    if (menuItemModel.getItemModel(MenuItemModel.EXPENSE_KEY).isAccess() ||
                            menuItemModel.getItemModel(MenuItemModel.ADVANCE_KEY).isAccess() ||
                            menuItemModel.getItemModel(MenuItemModel.EMPLOYEE_APPROVAL_KEY).isAccess()) {
                        PendingCountModel pendingCountModel = ModelManager.getInstance()
                                .getPendingCountModel();
                        MainItemModel item = new MainItemModel(itemModel.getmObjectDesc(),
                                "Check approvals", getString(R.string.msg_pending_approval), "" + (pendingCountModel == null ? "0"
                                : pendingCountModel.getmPendingCount()),
                                R.drawable.manager_approval);
                        item.setObjectId(itemModel.getmObjectId());
                        itemList.add(item);
                    }
                }

            }

            itemModel = menuItemModel.getItemModel(MenuItemModel.LOCATION_KEY);
            if (itemModel != null && itemModel.isAccess()) {
                TypeWiseListModel locationCountModel = ModelManager.getInstance()
                        .getLocationCountModel();
                MainItemModel item = new MainItemModel(itemModel.getmObjectDesc(),
                        "View Location Details", "Total", "" + (locationCountModel == null ? "0"
                        : locationCountModel.getList().get(0).getValue()),
                        R.drawable.location_blue);
                item.setObjectId(itemModel.getmObjectId());
                itemList.add(item);
            }


            itemModel = menuItemModel.getItemModel(MenuItemModel.CREATE_ADVANCE_KEY);
            if (itemModel != null && itemModel.isAccess()) {
                ExpenseStatusModel expenseStatusModel = ModelManager.getInstance().getExpenseStatusModel();
                if (expenseStatusModel != null && expenseStatusModel.getExpenseStatusData() != null) {

                    if (expenseStatusModel.getExpenseStatusData().get(0) != null) {
                        ExpenseStatusData expenseStatusData = expenseStatusModel.getExpenseStatusData().get(0);
                        MainItemModel item = new MainItemModel("Advance",
                                getString(R.string.msg_advance_detail), getString(R.string.advance_balance),
                                "" + (expenseStatusData == null ? "0" : expenseStatusData.getCurrencyCode() + " " + expenseStatusData.getAmount()), R.drawable.advance_expense, true);
                        item.setObjectId(itemModel.getmObjectId());
                        itemList.add(item);
                    }

                }
            }

            itemModel = menuItemModel.getItemModel(MenuItemModel.CREATE_EXPENSE_KEY);
            if (itemModel != null && itemModel.isAccess()) {
                ExpenseStatusModel expenseStatusModel = ModelManager.getInstance().getExpenseStatusModel();
                if (expenseStatusModel != null && expenseStatusModel.getExpenseStatusData() != null
                        && expenseStatusModel.getExpenseStatusData().size() > 0) {
                    if (expenseStatusModel.getExpenseStatusData().get(1) != null) {
                        ExpenseStatusData expenseStatusData = expenseStatusModel.getExpenseStatusData().get(1);
                        MainItemModel item = new MainItemModel("Expense",
                                getString(R.string.msg_expense), getString(R.string.expense_balance),
                                "" + (expenseStatusData == null ? "0" : expenseStatusData.getCurrencyCode() + " " + expenseStatusData.getAmount()), R.drawable.expense_claim, true);
                        item.setObjectId(itemModel.getmObjectId());
                        itemList.add(item);
                    }
                }
            }

            itemModel = menuItemModel.getItemModel(MenuItemModel.TICKET_KEY);
            if (itemModel != null && !itemModel.getIsTicketAccess().equalsIgnoreCase("N")) {
                TicketResultModel ticketResultModel = ModelManager.getInstance().getTicketResult();

                MainItemModel item = new MainItemModel("Ticket",
                        getString(R.string.msg_tickets), getString(R.string.open_tickets),
                        "" + (ticketResultModel == null ? "0" : ticketResultModel.getOpenCount()), R.drawable.ticket, true);
                item.setObjectId(itemModel.getmObjectId());
                itemList.add(item);
            }

            ArrayList<String> list = new ArrayList<>();
            String ticketAccess = "";
            itemModel = menuItemModel.getItemModel(MenuItemModel.CREATE_ADVANCE_KEY);
            if (itemModel != null && itemModel.isAccess()) {
                list.add("Advance");
            }
            itemModel = menuItemModel.getItemModel(MenuItemModel.CREATE_EMPLOYEE);
            if (itemModel != null && itemModel.isAccess()) {
                list.add("Employee");
            }
            itemModel = menuItemModel.getItemModel(MenuItemModel.CREATE_EXPENSE_KEY);
            if (itemModel != null && itemModel.isAccess()) {
                list.add("Expense");
            }
            itemModel = menuItemModel.getItemModel(MenuItemModel.CREATE_LEAVE);
            if (itemModel != null && itemModel.isAccess()) {
                list.add("Leave");
            }
            itemModel = menuItemModel.getItemModel(MenuItemModel.CREATE_LOCATION);
            if (itemModel != null && itemModel.isAccess()) {
                list.add("Location");
            }

            itemModel = menuItemModel.getItemModel(MenuItemModel.OD_REQUEST);
            if (itemModel != null && itemModel.isAccess()) {
                list.add("Outdoor Duty");
            }
            itemModel = menuItemModel.getItemModel(MenuItemModel.TOUR_REQUEST);
            if (itemModel != null && itemModel.isAccess()) {
                list.add("Tour");
            }
            itemModel = menuItemModel.getItemModel(MenuItemModel.WORK_FROM_HOME);
            if (itemModel != null && itemModel.isAccess()) {
                list.add("Work From Home");
            }

            itemModel = menuItemModel.getItemModel(MenuItemModel.TICKET_KEY);
            if (itemModel != null && !itemModel.getIsTicketAccess().equalsIgnoreCase("N")) {
                SharedPreference.saveSharedPreferenceData(AppsConstant.Project_NAME,
                        AppsConstant.TICKET_MENU_ACCESS, itemModel.getIsTicketAccess(), context);
                list.add("Ticket");
                // ticketAccess = AppsConstant.TICKET_ACCESS_SIMPLE;
                //  preferences.saveString(AppsConstant.TICKET_ACCESS_KEY,ticketAccess);

            }

          /*  itemModel = menuItemModel.getItemModel(MenuItemModel.OTHER_TICKET_KEY);
            if (itemModel != null && itemModel.isAccess()) {
                list.add(AppsConstant.TICKET_Other);
            }*/

    /*        preferences.remove(AppsConstant.TICKET_ACCESS_KEY);

            itemModel = menuItemModel.getItemModel(MenuItemModel.TICKET_KEY);
            if (itemModel != null && itemModel.isAccess()) {
                list.add("Ticket");
                ticketAccess = AppsConstant.TICKET_ACCESS_SIMPLE;
                preferences.saveString(AppsConstant.TICKET_ACCESS_KEY,ticketAccess);

            }else {
                itemModel = menuItemModel.getItemModel(MenuItemModel.OTHER_TICKET_KEY);
                if (itemModel != null && itemModel.isAccess()) {
                    list.add("Ticket");
                    ticketAccess= AppsConstant.TICKET_ACCESS_ADVANCE;
                    preferences.saveString(AppsConstant.TICKET_ACCESS_KEY,ticketAccess);

                }else if (ticketAccess.equalsIgnoreCase(AppsConstant.TICKET_ACCESS_SIMPLE) &&
                        ticketAccess.equalsIgnoreCase(AppsConstant.TICKET_ACCESS_ADVANCE)) {
                    list.add("Ticket");
                    ticketAccess= AppsConstant.TICKET_ACCESS_BOTH;
                    preferences.saveString(AppsConstant.TICKET_ACCESS_KEY,ticketAccess);

                }
            }*/

           /* itemModel = menuItemModel.getItemModel(MenuItemModel.OTHER_TICKET_KEY);
            if (itemModel != null && itemModel.isAccess()) {
               // list.add("Ticket");
                ticketAccess= AppsConstant.TICKET_ACCESS_ADVANCE;
            }

            if(!ticketAccess.equalsIgnoreCase("")){
                if (ticketAccess.equalsIgnoreCase(AppsConstant.TICKET_ACCESS_SIMPLE) || ticketAccess.equalsIgnoreCase(AppsConstant.TICKET_ACCESS_ADVANCE)){
                    list.add("Ticket");
                }else if (ticketAccess.equalsIgnoreCase(AppsConstant.TICKET_ACCESS_SIMPLE) &&
                        ticketAccess.equalsIgnoreCase(AppsConstant.TICKET_ACCESS_ADVANCE)) {
                    list.add("Ticket");
                }
            }*/

      /*      list.add("Raise Ticket(Simple)");
            list.add("Raise Ticket(Advanced)");*/


            if (list.size() > 0) {
                ((MainActivity) getActivity()).setMenuList(list);
                ((MainActivity) getActivity()).menuPlus.setVisibility(View.VISIBLE);
            } else {
                ((MainActivity) getActivity()).menuPlus.setVisibility(View.GONE);
            }

        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_check_in_out:
            case R.id.btn_check_breack:
                if (Utility.isLocationEnabled(getContext())) {
                    if (Utility.isNetworkAvailable(getContext())) {
                        EmployeeProfileModel employeeProfileModel = ModelManager.getInstance().getEmployeeProfileModel();
                        if (PermissionUtil.checkLocationPermission(getContext())) {
                            if (employeeProfileModel != null) {
                                String selifie = employeeProfileModel.getmSelfieYN();
                                String geofence = employeeProfileModel.getmGeofencingYN();
                                Log.d(MainActivity.TAG, "This is selfie response " + selifie);

                                GPSTracker gps = new GPSTracker(getContext());
                                latitude = gps.getLatitude() + "";
                                longitude = gps.getLongitude() + "";


                                if (v != null) {
                                    currentReqType = (int) (v.getTag() != null ? v.getTag() : 2);
                                    preferences = new Preferences(getContext());
                                    preferences.saveInt("currentReqType", currentReqType);
                                    preferences.commit();
                                    if (currentReqType == 0 || currentReqType == 3) {
                                        if (!TextUtils.isEmpty(selifie) && selifie.equalsIgnoreCase("N") && !TextUtils.isEmpty(geofence) && geofence.equalsIgnoreCase("N")) {
                                            //     performAttandanceAction(currentReqType);
                                            getLocationAddress(latitude, longitude);
                                        } else {
                                            mUserActionListener.performUserAction(IAction.TIME_IN, null, null);
                                        }
                                    } else if (currentReqType == 4) {
                                        EventDataSource dataSource = new EventDataSource(getContext());
                                        LoginUserModel loginUserModel = ModelManager.getInstance().getLoginUserModel();

                                        if (loginUserModel != null) {
                                            UserModel userModel = loginUserModel.getUserModel();
                                            if (userModel != null) {
                                                dataSource.clearTimeOutEntry(userModel.getUserName());
                                            }
                                        }
                                        if (v != null) {
                                            getLocationAddress(latitude, longitude);
                                        }

                                    } else if (currentReqType == 1 || currentReqType == 2) {
                                        getLocationAddress(latitude, longitude);
                                    }
                                }
                            }
                        } else {
                            PermissionUtil.askLocationPermision(this);
                        }
                    } else {
                        new AlertCustomDialog(getActivity(), getString(R.string.msg_internet_connection));
                    }
                } else {
                    requestToEnableGPS(getContext(), preferences);
                }
                break;
            default:
                break;
        }
        super.onClick(v);
    }


    private void getLocationAddress(String lat, String lon) {
        GeoCoderAddress coder = new GeoCoderAddress();
        double latitude = 0;
        double longitude = 0;
        try {
            latitude = Double.parseDouble(lat);
            longitude = Double.parseDouble(lon);
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage(), e);
            Crashlytics.logException(e);
        }
        coder.execute(GeoUtil.getGeoCoderUrl(latitude, longitude));
    }

    private void performAttandanceAction(int currentReqType2, String latitude, String longitude, String geoAddress) {
        AttendanceUtil.performAttendanceAction(getActivity(), this, currentReqType, latitude, longitude, null, null, null, geoAddress, null, null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MainItemModel model = itemList.get(position);
        MenuItemModel menuItemModel = ModelManager.getInstance().getMenuItemModel();

        if (menuItemModel != null) {
            MenuItemModel itemModel = menuItemModel.getItemModel(model.getObjectId());
            if (MenuItemModel.ATTANDANCE_KEY.equalsIgnoreCase(itemModel.getmObjectId())) {
                mUserActionListener.performUserAction(IAction.TIME_ATTENDANCE_SUMMARY, null, null);
            } else if (MenuItemModel.LEAVE_KEY.equalsIgnoreCase(itemModel.getmObjectId())) {
                mUserActionListener.performUserAction(IAction.LEAVE_BALANCE_DETAIL, null, null);
            } else if (MenuItemModel.PAY_SLIP_KEY.equalsIgnoreCase(itemModel.getmObjectId())) {
                SalaryMonthModel salaryData = ModelManager.getInstance()
                        .getSalaryMonthModel();
                if (salaryData == null || salaryData.getMonths() == null
                        || salaryData.getMonths().size() <= 0) {
                    new AlertCustomDialog(getActivity(),
                            "Currently no pay slip available");
                } else {
                    mUserActionListener.performUserAction(IAction.PAY_SLIP_VIEW, null, null);
                }
            } else if (MenuItemModel.APPROVAL_KEY.equalsIgnoreCase(itemModel.getmObjectId())) {
                mUserActionListener.performUserAction(IAction.APPROVE_SCREEN, null, null);
            } else if (MenuItemModel.LOCATION_KEY.equalsIgnoreCase(itemModel.getmObjectId())) {
                mUserActionListener.performUserAction(IAction.STORE_LIST_VIEW, null, null);
            } else if (MenuItemModel.TEAM_KEY.equalsIgnoreCase(itemModel.getmObjectId())) {
                TeamMember.setmCurrentEmpId("");
                mUserActionListener.performUserAction(IAction.TEAM_MEMBER_LIST, null, null);
            } else if (MenuItemModel.CREATE_EXPENSE_KEY.equalsIgnoreCase(itemModel.getmObjectId())) {
                mUserActionListener.performUserAction(IAction.EXPENSE_CLAIM_SUMMARY, null, null);
            } else if (MenuItemModel.CREATE_ADVANCE_KEY.equalsIgnoreCase(itemModel.getmObjectId())) {
                mUserActionListener.performUserAction(IAction.ADVANCE_EXPENSE_SUMMARY, null, null);
            } else if (MenuItemModel.TICKET_KEY.equalsIgnoreCase(itemModel.getmObjectId())) {
                mUserActionListener.performUserAction(IAction.RAISE_TICKET_ADV_SUMMARY, null, null);
            }
        }
    }

    private void updateAttendanceStatusMethods(String attendanceStatusResult) {
        ModelManager.getInstance().setCheckInOutModel(attendanceStatusResult);

        showTimeInButtons();

        if (ModelManager.getInstance().getCheckInOutModel() != null) {
            CheckInOutModel model = ModelManager.getInstance().getCheckInOutModel();
            updateInitialTimeInOutButton(model);
            updateInOutData(model.isCheckedIn(), model.isCheckedOut(), model.isBreakIn(), model.isBreakOut());
        }
    }

    private void updateLocationCountMethods(JSONObject mainJSONObject) {
        JSONArray jsonTypeWiseListForLocationCount = mainJSONObject.optJSONArray("list");
        TypeWiseListModel locationCountModel = new TypeWiseListModel(jsonTypeWiseListForLocationCount);
        ModelManager.getInstance().setLocationCountModel(locationCountModel);

        for (MainItemModel item : itemList) {
            if (item.getmLeftTitle().equalsIgnoreCase("Locations")) {
                TypeWiseListModel countModel = ModelManager.getInstance().getLocationCountModel();
                item.setmRightSubTitle(countModel == null ? "0" : countModel.getList().get(0).getValue());
            }
        }
        mAdapter.updateData(itemList);

    }

    private void updateEmpPendingApprovalCount(JSONObject mainJSONObject) {
        JSONArray pendingCountList = mainJSONObject.optJSONArray("list");
        PendingCountModel pendingCountModel = new PendingCountModel(pendingCountList);
        pendingCountModel.setmPendingCount(mainJSONObject.optString("PendingCount", ""));
        ModelManager.getInstance().setPendingCountModel(pendingCountModel);

        for (MainItemModel item : itemList) {
            if (item.getmLeftTitle().equalsIgnoreCase("Approval")) {
                item.setmRightSubTitle(pendingCountModel == null ? "0" : pendingCountModel.getmPendingCount());
            }
        }
        mAdapter.updateData(itemList);

    }

    private void updateEmpPendingApprovalReqCount(JSONObject mainResponseJson) {
        int totalPendingRequests = mainResponseJson.optInt("ReqCount", 0);
        ModelManager.getInstance().setUserTotalPendingRequests(totalPendingRequests);
    }

    private void updateEmpProfileData(JSONObject responseData) {
        ModelManager.getInstance().setEmployeeProfileModel(responseData);
        populateHomeData();
        updateHomeData();
        saveEmpConfig(preferences);
        ((MainActivity) getActivity()).updateHeaderImage();
        LinearLayout llProfileMessage = (LinearLayout) rootView.findViewById(R.id.llProfileMessage);
        TextView tvProfileMessage = (TextView) rootView.findViewById(R.id.tvProfileMessage);
        EmployeeProfileModel employeeProfileModel = ModelManager.getInstance().getEmployeeProfileModel();
        if (employeeProfileModel != null && employeeProfileModel.getmProfileMsgYN().equalsIgnoreCase("Y")) {
            llProfileMessage.setVisibility(View.VISIBLE);
            tvProfileMessage.setText(employeeProfileModel.getmProfileMsg());
        } else {
            llProfileMessage.setVisibility(View.GONE);
        }

    }

    private void updatePaySlipMethods(JSONObject salaryData) {
        String salaryMonthResult = salaryData.toString();
        ModelManager.getInstance().setSalaryMonthModel(salaryMonthResult);
        updateHomeData();
    }

    private void updateExpenseMethods(JSONObject expenseData) {
        if (expenseData != null) {
            String expenseResult = expenseData.toString();
            ModelManager.getInstance().setExpenseStatusModel(expenseResult);
            populateHomeData();
            updateHomeData();
        }
    }

    private void updateTicketData(JSONObject ticketData) {
        if (ticketData != null) {
            String ticketResult = ticketData.toString();
            ModelManager.getInstance().setTicketResultModel(ticketResult);
            populateHomeData();
            updateHomeData();
        }
    }

    private void updateEmpLeaveBalanceMethods(JSONObject jsonObject) {
        String getEmpLeaveBalanceResult = jsonObject.toString();
        ModelManager.getInstance().setLeaveBalanceModel(getEmpLeaveBalanceResult);
        for (MainItemModel item : itemList) {
            if (item.getmLeftTitle().equalsIgnoreCase(getString(R.string.msg_leaves))) {
                LeaveBalanceModel leaveBalanceModel = ModelManager.getInstance().getLeaveBalanceModel();
                if (leaveBalanceModel != null) {
                    item.setmRightSubTitle("" + leaveBalanceModel.getmAvailable());
                }
            }
        }
        mAdapter.updateData(itemList);
    }

    @Override
    public void validateResponse(ResponseData response) {
        refreshLayout.setRefreshing(false);
        MainActivity.isAnimationLoaded = true;
        showHideProgressView(false);
        String responseData = response.getResponseData();
        if (response.isSuccess() && !isSessionValid(responseData)) {
            mUserActionListener.performUserAction(IAction.LOGIN_VIEW, null, null);
            return;
        }
        switch (response.getRequestData().getReqApiId()) {

            case CommunicationConstant.API_GET_HOME_DATA:

                Log.d("TAG", "Home Data response : " + responseData);
                try {
                    JSONObject mainObj = new JSONObject(responseData);
                    if ((mainObj.optJSONObject("GetHomeDataResult")).getInt("ErrorCode") == 0) {
                        //getAnnouncementData();
                        JSONObject object = mainObj.optJSONObject("GetHomeDataResult");

                        JSONObject attendanceStatusData = object.optJSONObject("AttendanceStatus");
                        updateAttendanceStatusMethods(attendanceStatusData.toString());

                        JSONObject empLeaveBalanceData = object.optJSONObject("EmpLeaveBalance");
                        updateEmpLeaveBalanceMethods(empLeaveBalanceData);

                        JSONObject empLocationCountData = object.optJSONObject("EmpLocationCount");
                        updateLocationCountMethods(empLocationCountData);

                        JSONObject empPendingApprovalCount = object.optJSONObject("EmpPendingApprovalCount");
                        updateEmpPendingApprovalCount(empPendingApprovalCount);

                        JSONObject empPendingApprovalReqCountData = object.optJSONObject("EmpPendingApprovalReqCount");
                        updateEmpPendingApprovalReqCount(empPendingApprovalReqCountData);

                        JSONObject salaryMonthData = object.optJSONObject("SalaryMonth");
                        updatePaySlipMethods(salaryMonthData);

                        JSONObject empProfileData = object.optJSONObject("EmpProfile");
                        updateEmpProfileData(empProfileData);

                        JSONObject expenseData = object.optJSONObject("ExpenseStatus");
                        Log.d("Expense Result", expenseData.toString());
                        updateExpenseMethods(expenseData);

                        JSONObject ticketData = object.optJSONObject("TicketResult");
                        Log.d("Ticket Result", ticketData.toString());
                        updateTicketData(ticketData);
                    }
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                }
                break;

            case CommunicationConstant.API_GET_MENU_DATA:

                try {
                    JSONObject getMenuDataResult = (new JSONObject(responseData)).optJSONObject("GetMenuDataResult");
                    JSONArray menuJsonArray = getMenuDataResult.optJSONArray("menuDataList");
                    ModelManager.getInstance().setMenuItemModel(menuJsonArray);
                    Log.d("Menu response :", getMenuDataResult.optJSONArray("menuDataList").toString());
                    populateHomeData();
                    updateHomeData();

                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    Log.e(TAG, e.getMessage(), e);
                    updateHomeData();
                }
                break;


            case CommunicationConstant.API_MARK_ATTANDANCE:
                try {
                    if (((new JSONObject(responseData)).getJSONObject("MarkAttendanceResult")).getInt("ErrorCode") == 0) {
                        CommunicationManager.getInstance().sendPostRequest(this, AppRequestJSONString.getHomeData(),
                                CommunicationConstant.API_GET_HOME_DATA, true);
                    } else {
                        String errorMessage = ((new JSONObject(responseData)).getJSONObject("MarkAttendanceResult")).optString("ErrorMessage", "Failed");
                        new AlertCustomDialog(getContext(), errorMessage, "Ok", true, new AlertCustomDialog.AlertClickListener() {
                            @Override
                            public void onPositiveBtnListener() {
                                onCreate(null);
                            }

                            @Override
                            public void onNegativeBtnListener() {

                            }
                        });
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                    Crashlytics.logException(e);
                }
                break;
            case CommunicationConstant.API_UPLOAD_PROFILE_PIC:
                String responseStr = response.getResponseData();
                Log.d("TAG", "profile pic response : " + responseStr);
                UploadProfilePicResponseModel uploadProfilePicResponseModel = UploadProfilePicResponseModel.create(responseStr);
                if (uploadProfilePicResponseModel != null && uploadProfilePicResponseModel.getUploadProfilePicResult() != null
                        && uploadProfilePicResponseModel.getUploadProfilePicResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                    //  CustomDialog.alertOkWithFinish(context,uploadProfilePicResponseModel.getTimeModificationResult().getErrorMessage());
                    //CustomDialog.alertOkWithFinishActivity(context, uploadProfilePicResponseModel.getTimeModificationResult().getErrorMessage(), TimeModificationActivity.this, true);

                    //CustomDialog.alertOkWithFinishFragment(context, uploadProfilePicResponseModel.getTimeModificationResult().getErrorMessage(), null, IAction.HOME_VIEW, true);
                    CommunicationManager.getInstance().sendPostRequest(this,
                            AppRequestJSONString.getLogOutData(),
                            CommunicationConstant.API_USER_PROFILE_DETAILS, true);
                } else {
                    new AlertCustomDialog(getActivity(), uploadProfilePicResponseModel.getUploadProfilePicResult().getErrorMessage());
                }
                break;
            case CommunicationConstant.API_USER_PROFILE_DETAILS:
                Log.d("TAG", "Response Profile" + response.getResponseData());
                ModelManager.getInstance().setEmployeeProfileModel(response.getResponseData());
                ModelManager.getInstance().getEmployeeProfileModel().setmImageUrl(ModelManager.getInstance().getEmployeeProfileModel().getmImageUrl());

                break;
            case CommunicationConstant.API_GET_ANNOUNCEMENT:
                String response1 = response.getResponseData();
                Log.d("TAG", "Announcement data : " + response1);
                homeCarasoleRL.setVisibility(View.GONE);
                announcementRes = GetAnnouncementResultResponseModel.create(response1);
                if (announcementRes != null && announcementRes.getGetAnnouncementResult() != null
                        && announcementRes.getGetAnnouncementResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                    if (announcementRes.getGetAnnouncementResult().getAnnouncementItems() != null &&
                            announcementRes.getGetAnnouncementResult().getAnnouncementItems().size() > 0) {
                        homeCarasoleRL.setVisibility(View.VISIBLE);
                        slideCount = announcementRes.getGetAnnouncementResult().getAnnouncementItems().size();
                        refreshAnnouncementResult(announcementRes.getGetAnnouncementResult().getAnnouncementItems());
                    }
                } else {
                    new AlertCustomDialog(getActivity(), announcementRes.getGetAnnouncementResult().getErrorMessage());
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        isFromLogin = preferences.getBoolean(AppsConstant.ISFROMLOGIN, false);
        isFromSplash = preferences.getBoolean(AppsConstant.ISFROMSPLASH, false);
        if (!isFromSplash && !isFromLogin) {
            onCreate(null);
        }

        if (announcementRes != null && announcementRes.getGetAnnouncementResult() != null
                && announcementRes.getGetAnnouncementResult().getAnnouncementItems() != null
                && announcementRes.getGetAnnouncementResult().getAnnouncementItems().size() > 0) {
            refreshAnnouncementResult(announcementRes.getGetAnnouncementResult().getAnnouncementItems());
        }
        //viewAnnouncementData();
    }


    @Override
    public void onRefresh() {
        onCreate(null);

    }

    private class GeoCoderAddress extends GeoCoder {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String result = s;
            GeoCoderModel address = null;
            JSONObject parentObj = null;
            try {
                if (!TextUtils.isEmpty(result)) {
                    parentObj = new JSONObject(result);
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                Crashlytics.log(AppsConstant.PRIORITY_MEDIUM, TAG, result);
                Crashlytics.logException(e);
            }
            if (parentObj != null) {
                address = new GeoCoderModel(parentObj.optJSONArray("results"));
            }
            if (address != null) {
                performAttandanceAction(currentReqType, latitude, longitude, address.getmCompleteAddress());
            }
        }
    }

    private void galleryIntent() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, getString(R.string.chooser_title));
        try {
            startActivityForResult(intent, UPLOAD_DOC_REQUEST);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPLOAD_DOC_REQUEST && resultCode == RESULT_OK) {
            final Uri uri = data.getData();
            String encodeFileToBase64Binary = null;
            if (data != null) {
                if (Utility.calculateBitmapSizeForProfilePic(data.getData(), context) > Utility.maxLimit1) {
                    CustomDialog.alertWithOk(context, Utility.sizeMsg1);
                    return;
                }
                // String path = data.getStringExtra("path");
                //System.out.print(path);

                Uri uploadedFilePath = data.getData();
                String filename = Utility.getFileName(uploadedFilePath, context);
                filename = filename.toLowerCase();
                String fileDesc = Utility.getFileName(uploadedFilePath, context);
                String[] extList = filename.split("\\.");
                System.out.print(extList[1].toString());
                String extension = "." + extList[extList.length - 1];
                //encodeFileToBase64Binary = Utility.fileToBase64Conversion(data.getData(), context);
                // Log.d("TAG", "RAR Base 64 :" + encodeFileToBase64Binary);


                if (filename.contains(".jpg") || filename.contains(".jpeg") ||
                        filename.contains(".JPEG") || filename.contains(".JPG") ||
                        filename.contains(".png") || filename.contains(".PNG")) {


                    // if (filename.contains(".jpg") || filename.contains(".jpeg")) {
                    bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    File mediaFile = null;
                    if (bitmap != null) {
                        encodeFileToBase64Binary = Utility.converBitmapToBase64(bitmap);
                        byte[] imageBytes = ImageUtil.bitmapToByteArray(bitmap);

                        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DCIM), "");
                        mediaFile = new File(mediaStorageDir.getPath() + File.separator + purpose + ".jpg");
                        if (mediaFile != null) {
                            try {
                                FileOutputStream fos = new FileOutputStream(mediaFile);
                                fos.write(imageBytes);
                                img_user_img.setImageBitmap(bitmap);
                                fos.close();
                            } catch (FileNotFoundException e) {
                                Crashlytics.log(1, getClass().getName(), e.getMessage());
                                Crashlytics.logException(e);
                            } catch (IOException e) {
                                Crashlytics.log(1, getClass().getName(), e.getMessage());
                                Crashlytics.logException(e);
                            }
                        }
                    }
                    // }

                  /*  if (Utility.calcBase64SizeInKBytes(encodeFileToBase64Binary) > Utility.maxLimit) {
                        CustomDialog.alertWithOk(context, Utility.sizeMsg);
                        return;
                    }*/

                    UploadProfilePicModel uploadProfilePicModel = new UploadProfilePicModel();
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setBase64Data(encodeFileToBase64Binary);
                    fileInfo.setExtension(".jpg");
                    fileInfo.setLength("0");
                    fileInfo.setName("MyPhoto");
                    uploadProfilePicModel.setFileInfo(fileInfo);

                    CommunicationManager.getInstance().sendPostRequest(this,
                            AppRequestJSONString.uploadProfileRequest(uploadProfilePicModel),
                            CommunicationConstant.API_UPLOAD_PROFILE_PIC, true);
                } else {
                    CustomDialog.alertWithOk(context, getResources().getString(R.string.valid_image));
                    return;
                }
            }
        }


        if (requestCode == AppsConstant.REQ_CAMERA && resultCode == RESULT_OK) {
            final Intent intent = data;
            String path = intent.getStringExtra("response");
            Uri uri = Uri.fromFile(new File(path));
            if (uri == null) {
                Log.d("uri", "null");
            } else {
                bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File mediaFile = null;
                if (bitmap != null) {
                    byte[] imageBytes = ImageUtil.bitmapToByteArray(bitmap);

                    File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DCIM), "");
                    mediaFile = new File(mediaStorageDir.getPath() + File.separator + purpose + ".jpg");
                    if (mediaFile != null) {
                        try {
                            FileOutputStream fos = new FileOutputStream(mediaFile);
                            fos.write(imageBytes);
                            img_user_img.setImageBitmap(bitmap);
                            fos.close();
                        } catch (FileNotFoundException e) {
                            Crashlytics.log(1, getClass().getName(), e.getMessage());
                            Crashlytics.logException(e);
                        } catch (IOException e) {
                            Crashlytics.log(1, getClass().getName(), e.getMessage());
                            Crashlytics.logException(e);
                        }
                    }
                }
            }

            String encodeFileToBase64Binary = Utility.converBitmapToBase64(bitmap);

            UploadProfilePicModel uploadProfilePicModel = new UploadProfilePicModel();
            FileInfo fileInfo = new FileInfo();
            fileInfo.setBase64Data(encodeFileToBase64Binary);
            fileInfo.setExtension(".jpg");
            fileInfo.setLength("0");
            fileInfo.setName("MyPhoto");
            uploadProfilePicModel.setFileInfo(fileInfo);

            CommunicationManager.getInstance().sendPostRequest(this,
                    AppRequestJSONString.uploadProfileRequest(uploadProfilePicModel),
                    CommunicationConstant.API_UPLOAD_PROFILE_PIC, true);

        }

    }


    private void getAnnouncementData() {
        GetAnnouncementRequestModel getAnnouncementRequestModel = new GetAnnouncementRequestModel();
        // Utility.showHidePregress(progressbar, true);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.getAnnouncementData(getAnnouncementRequestModel),
                CommunicationConstant.API_GET_ANNOUNCEMENT, true);
    }


    private void refreshAnnouncementResult(ArrayList<AnnouncementItemsModel> announcementItemsModel) {
      /*  ArrayList<AnnouncementItemsModel> announcementItemsModel1 = new ArrayList<>();
        announcementItemsModel.add(0, new AnnouncementItemsModel(0, "http://cssslider.com/sliders/demo-20/data1/images/picjumbo.com_img_4635.jpg", getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
        announcementItemsModel.add(1, new AnnouncementItemsModel(1, "http://cssslider.com/sliders/demo-20/data1/images/picjumbo.com_img_4635.jpg", getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
*/
        imageList();
        if (viewPagerAdapter == null) {
            pagerlayout.setVisibility(View.GONE);
        } else {

            pagerlayout.setVisibility(View.VISIBLE);
            btnViewPagerLayout.removeAllViews();

            for (int i = 0; i < announcementItemsModel.size(); i++) {
                View view = layoutInflater.inflate(R.layout.button_layout, null);
                Button button = (Button) view.findViewById(R.id.btn1);
                if (i == 0) {
                    button.setBackgroundResource(R.drawable.circle2);
                }
                btnViewPagerLayout.addView(view);
            }

        }


       /* //ArrayList<AnnouncementItemsModel> announcementItemsModel1 = new ArrayList<>();
        //    announcementItemsModel1.add(0,new AnnouncementItemsModel(0,"http://cssslider.com/sliders/demo-20/data1/images/picjumbo.com_img_4635.jpg" , getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));

               List<Slide> slideList = new ArrayList<>();

        //create list of slides

        slideList.add(new Slide(0,"http://cssslider.com/sliders/demo-20/data1/images/picjumbo.com_img_4635.jpg" , getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
        slideList.add(new Slide(1,"http://cssslider.com/sliders/demo-12/data1/images/picjumbo.com_hnck1995.jpg" , getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
        slideList.add(new Slide(2,"http://cssslider.com/sliders/demo-19/data1/images/picjumbo.com_hnck1588.jpg" , getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
        slideList.add(new Slide(3,"http://wowslider.com/sliders/demo-18/data1/images/shanghai.jpg" , getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
//handle slider click listener

        slider.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //do what you want
            }
        });

//add slides to slider
        slider.addSlides(slideList);

      //  slider.addSlidesUpdated(announcementItemsModel);*/
    }

    private void btnAction(int action) {
        for (int i = 0; i < btnViewPagerLayout.getChildCount(); i++) {
            LinearLayout layout = (LinearLayout) btnViewPagerLayout.getChildAt(i);
            Button button = (Button) layout.findViewById(R.id.btn1);
            button.setBackgroundResource(R.drawable.circle);
            if (action == i) {
                button.setBackgroundResource(R.drawable.circle2);
            }
        }

    }


    public class ViewPagerAdapter extends FragmentPagerAdapter {
        ArrayList<AnnouncementItemsModel> announcementList;
        AnnouncementItemsModel announcementItemsModel;

        public ViewPagerAdapter(FragmentManager fm, ArrayList<AnnouncementItemsModel> announcementItemsModels) {
            super(fm);
            announcementList = announcementItemsModels;
        }

        public ViewPagerAdapter(FragmentManager fm, AnnouncementItemsModel announcementItemsModels) {
            super(fm);
            announcementItemsModels = announcementItemsModels;
        }

        @Override
        public Fragment getItem(int index) {

            ImageSliderFragment fragment = new ImageSliderFragment();
            fragment.setAnnouncementItemsModel(announcementList.get(index));

            return fragment;
        }

        @Override
        public int getCount() {

            return announcementList.size();
        }


    }


    private void imageList() {
        if (announcementRes != null && announcementRes.getGetAnnouncementResult() != null
                && announcementRes.getGetAnnouncementResult().getAnnouncementItems() != null
                && announcementRes.getGetAnnouncementResult().getAnnouncementItems().size() > 0) {
            viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), announcementRes.getGetAnnouncementResult().getAnnouncementItems());

            pager.setAdapter(viewPagerAdapter);
            if (announcementRes.getGetAnnouncementResult().
                    getAnnouncementItems().size() > 1) {
                Log.d("list animation:", "true list size");
                setupTimer();

            }

        }

    }

    private void viewAnnouncementData() {
        ArrayList<AnnouncementItemsModel> itemsModel = new ArrayList<>();
        itemsModel.add(new AnnouncementItemsModel(0, "http://cssslider.com/sliders/demo-20/data1/images/picjumbo.com_img_4635.jpg", getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
        itemsModel.add(new AnnouncementItemsModel(1, "http://cssslider.com/sliders/demo-12/data1/images/picjumbo.com_hnck1995.jpg", getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
        itemsModel.add(new AnnouncementItemsModel(2, "http://cssslider.com/sliders/demo-19/data1/images/picjumbo.com_hnck1588.jpg", getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
        GetAnnouncementResult announcementResult = new GetAnnouncementResult();
        announcementResult.setAnnouncementItems(itemsModel);
        announcementRes = new GetAnnouncementResultResponseModel();
        announcementRes.setGetAnnouncementResult(announcementResult);
        slideCount = announcementRes.getGetAnnouncementResult().getAnnouncementItems().size();
        refreshAnnouncementResult(announcementRes.getGetAnnouncementResult().getAnnouncementItems());
    }

    private void setupTimer() {
        mustLoopSlides = true;
        try {
            if (mustLoopSlides) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (currentPageNumber < slideCount)
                                currentPageNumber += 1;
                            else
                                currentPageNumber = 1;

                            pager.setCurrentItem(currentPageNumber - 1, true);

                            handler.removeCallbacksAndMessages(null);
                            handler.postDelayed(this, slideShowInterval);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, slideShowInterval);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
