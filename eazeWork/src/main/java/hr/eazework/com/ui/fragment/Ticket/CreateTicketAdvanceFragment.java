package hr.eazework.com.ui.fragment.Ticket;

import android.Manifest;
import android.app.Dialog;
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

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hr.eazework.com.FileUtils;
import hr.eazework.com.MainActivity;
import hr.eazework.com.R;
import hr.eazework.com.SearchOnbehalfActivity;
import hr.eazework.com.model.AdvanceRequestResponseModel;
import hr.eazework.com.model.CategoryList;
import hr.eazework.com.model.CategoryListItem;
import hr.eazework.com.model.ContactListModel;
import hr.eazework.com.model.EmployItem;
import hr.eazework.com.model.FeedbackRatingListModel;
import hr.eazework.com.model.GetTicketDetailRequestModel;
import hr.eazework.com.model.GetTicketDetailResponseModel;
import hr.eazework.com.model.GetTicketDetailResultModel;
import hr.eazework.com.model.GetTicketPageInitResultModel;
import hr.eazework.com.model.PageInputModel;
import hr.eazework.com.model.PartialDayDataModel;
import hr.eazework.com.model.PartialDayModel;
import hr.eazework.com.model.PriorityListModel;
import hr.eazework.com.model.RemarkListItem;
import hr.eazework.com.model.SubCategoryRequestModel;
import hr.eazework.com.model.SubCategoryResponseModel;
import hr.eazework.com.model.SupportDocsItemModel;
import hr.eazework.com.model.TicketDetailModel;
import hr.eazework.com.model.TicketItem;
import hr.eazework.com.model.TicketPageInitRequestModel;
import hr.eazework.com.model.TicketPageInitResponseModel;
import hr.eazework.com.model.TicketSubmitRequestModel;
import hr.eazework.com.model.TourReasonListModel;
import hr.eazework.com.model.TourResponseModel;
import hr.eazework.com.model.WFHRequestDetailItem;
import hr.eazework.com.model.WFHRequestDetailModel;
import hr.eazework.com.model.WFHRequestModel;
import hr.eazework.com.ui.adapter.DocumentUploadAdapter;
import hr.eazework.com.ui.adapter.RemarksAdapter;
import hr.eazework.com.ui.customview.CustomBuilder;
import hr.eazework.com.ui.customview.CustomDialog;
import hr.eazework.com.ui.fragment.Attendance.AttendanceApprovalFragment;
import hr.eazework.com.ui.fragment.Attendance.TimeAndAttendanceSummaryFragment;
import hr.eazework.com.ui.fragment.BaseFragment;
import hr.eazework.com.ui.interfaces.IAction;
import hr.eazework.com.ui.util.AppsConstant;
import hr.eazework.com.ui.util.ImageUtil;
import hr.eazework.com.ui.util.PermissionUtil;
import hr.eazework.com.ui.util.Preferences;
import hr.eazework.com.ui.util.SharedPreference;
import hr.eazework.com.ui.util.Utility;
import hr.eazework.com.ui.util.custom.AlertCustomDialog;
import hr.eazework.mframe.communication.ResponseData;
import hr.eazework.selfcare.communication.AppRequestJSONString;
import hr.eazework.selfcare.communication.CommunicationConstant;
import hr.eazework.selfcare.communication.CommunicationManager;

import static android.app.Activity.RESULT_OK;
import static hr.eazework.com.ui.util.ImageUtil.rotateImage;

/**
 * Created by SUNAINA on 14-09-2018.
 */

public class CreateTicketAdvanceFragment extends BaseFragment {
    private Context context;
    private AdvanceRequestResponseModel advanceRequestResponseModel;
    private List<String> extensionList;
    public static final String TAG = "CreateTicketAdvanceFragment";
    private String screenName = "CreateTicketAdvanceFragment";
    private String fromButton;
    private String empId, customerCorpId;
    private boolean isSubmitClicked = true;
    private Button saveDraftBTN, closeBTN, redirectBTN;
    private Preferences preferences;
    private TextView empNameTV, ticketTypeTV, empCodeTV, categoryTV, subCategoryTV, priorityTV, feedbackRatingTV;
    private View progressbar;
    private EditText remarksET, subjectET, descriptionET, feedbackET;
    private RelativeLayout searchLayout;
    // private EmployItem employItem;
    private ContactListModel employItem;
    public static int TICKET_EMP_ADV = 6;
    private LinearLayout errorLinearLayout;
    private RecyclerView expenseRecyclerView;
    private ImageView plus_create_newIV;
    private static int UPLOAD_DOC_REQUEST = 1;
    private ArrayList<SupportDocsItemModel> uploadFileList;
    private Bitmap bitmap = null;
    private String purpose = "";
    private DocumentUploadAdapter docAdapter;
    private static final int PERMISSION_REQUEST_CODE = 3;
    private TicketPageInitResponseModel ticketPageInitResponseModel;
    private SubCategoryResponseModel subCategoryResponseModel;
    private TicketPageInitRequestModel initRequestModel;
    private SubCategoryRequestModel subCategoryRequestModel;
    private String ticketId = "";
    private String ticketFor = "";
    private String categoryCode = "", categoryType = "", subCategoryCode = "";
    private String feedbackCode = "", empCode = "";
    private String priorityCode = "", ticketTypeCode = "";
    private TourResponseModel tourResponseModel;
    private GetTicketDetailResponseModel getTicketDetailResponseModel;

    private ArrayList<CategoryListItem> categoryList, feedbackRatingList, priorityList,
            subCategoryList, ticketTypeList;
    private CategoryListItem categoryListItem, feedbackListItem, priorityListItem,
            subCategoryItem, ticketTypeItem;

    private ArrayList<ContactListModel> contactList;
    private LinearLayout subCategoryLl, remarksLinearLayout1, remarksLinearLayout, remarksDataLl, RatingLl, feedbackLL, ticketTypeLl, feedbackWriteLL, categoryLl;
    private TicketSubmitRequestModel ticketSubmitRequestModel;
    private TicketDetailModel ticketDetailModel;
    private String simpleOrAdvance = "", categoryYN = "", subCategoryYN = "";
    private GetTicketDetailRequestModel requestDetail;
    private RecyclerView remarksRV;

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    private TicketItem ticketItem;

    public TicketItem getTicketItem() {
        return ticketItem;
    }

    public void setTicketItem(TicketItem ticketItem) {
        this.ticketItem = ticketItem;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (screenName != null && screenName.equalsIgnoreCase(TicketApprovalFragment.screenName)) {
            this.setShowPlusMenu(false);
            this.setShowEditTeamButtons(false);
            // getActivity().getSupportActionBar().setTitle(mTitle);
        } else {
            this.setShowPlusMenu(false);
            this.setShowEditTeamButtons(true);
        }
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_create_ticket_advance, container, false);
        context = getContext();
        setupScreen();
        return rootView;
    }

    private void setupScreen() {

        preferences = new Preferences(getContext());
        int textColor = Utility.getTextColorCode(preferences);
        ((TextView) getActivity().findViewById(R.id.tv_header_text)).setTextColor(textColor);

        ticketId = "0";
        ticketFor = SharedPreference.getSharedPreferenceData(AppsConstant.Project_NAME,
                AppsConstant.TICKET_KEY, context);

        ((TextView) ((MainActivity) getActivity()).findViewById(R.id.tv_header_text)).setText(R.string.ticket);
        ((MainActivity) getActivity()).findViewById(R.id.ibRight).setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).findViewById(R.id.ibRight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSubmitClicked) {
                    isSubmitClicked = false;
                    fromButton = "Submit";
                    doSubmitOperation();
                }
            }
        });
        ((MainActivity) getActivity()).findViewById(R.id.ibWrong).setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).findViewById(R.id.ibWrong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserActionListener.performUserAction(IAction.HOME_VIEW, null, null);
            }
        });
        progressbar = (LinearLayout) rootView.findViewById(R.id.ll_progress_container);
        categoryLl = (LinearLayout) rootView.findViewById(R.id.categoryLl);
        subCategoryLl = (LinearLayout) rootView.findViewById(R.id.subCategoryLl);
        remarksLinearLayout1 = (LinearLayout) rootView.findViewById(R.id.remarksLinearLayout1);

        RatingLl = (LinearLayout) rootView.findViewById(R.id.RatingLl);
        feedbackLL = (LinearLayout) rootView.findViewById(R.id.feedbackLL);
        ticketTypeLl = (LinearLayout) rootView.findViewById(R.id.ticketTypeLl);
        feedbackWriteLL = (LinearLayout) rootView.findViewById(R.id.feedbackWriteLL);
        remarksLinearLayout = (LinearLayout) rootView.findViewById(R.id.remarksLinearLayout);
        remarksRV = (RecyclerView) rootView.findViewById(R.id.remarksRV);
        remarksDataLl = (LinearLayout) rootView.findViewById(R.id.remarksDataLl);
        remarksDataLl.setVisibility(View.GONE);
        empNameTV = (TextView) rootView.findViewById(R.id.empNameTV);
        empCodeTV = (TextView) rootView.findViewById(R.id.empCodeTV);

        ticketTypeTV = (TextView) rootView.findViewById(R.id.ticketTypeTV);
        categoryTV = (TextView) rootView.findViewById(R.id.categoryTV);
        subCategoryTV = (TextView) rootView.findViewById(R.id.subCategoryTV);
        priorityTV = (TextView) rootView.findViewById(R.id.priorityTV);
        feedbackRatingTV = (TextView) rootView.findViewById(R.id.ratingTV);
        saveDraftBTN = (Button) rootView.findViewById(R.id.saveDraftBTN);
        closeBTN = (Button) rootView.findViewById(R.id.closeBTN);
        redirectBTN = (Button) rootView.findViewById(R.id.redirectBTN);
        descriptionET = (EditText) rootView.findViewById(R.id.descriptionET);
        remarksET = (EditText) rootView.findViewById(R.id.remarksET);
        subjectET = (EditText) rootView.findViewById(R.id.subjectET);
        feedbackET = (EditText) rootView.findViewById(R.id.feedbackET);
        searchLayout = (RelativeLayout) rootView.findViewById(R.id.searchLayout);
        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent theIntent = new Intent(getActivity(), SearchOnbehalfActivity.class);
                //theIntent.putExtra("ContactList", contactList);
                theIntent.putExtra("SearchType", TICKET_EMP_ADV);
                startActivityForResult(theIntent, TICKET_EMP_ADV);
            }
        });
        errorLinearLayout = (LinearLayout) rootView.findViewById(R.id.errorDocTV);
        errorLinearLayout.setVisibility(View.VISIBLE);
        expenseRecyclerView = (RecyclerView) rootView.findViewById(R.id.expenseRecyclerView);
        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration itemDecoration = new
                DividerItemDecoration(expenseRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(context, R.drawable.gradient_line));
        expenseRecyclerView.addItemDecoration(itemDecoration);

        plus_create_newIV = (ImageView) rootView.findViewById(R.id.plus_create_newIV);

        plus_create_newIV.setOnClickListener(new View.OnClickListener() {
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
                                        PermissionUtil.askAllPermissionCamera(CreateTicketAdvanceFragment.this);
                                    }
                                    if (PermissionUtil.checkCameraPermission(getContext()) && PermissionUtil.checkStoragePermission(getContext())) {
                                        Utility.openCamera(getActivity(), CreateTicketAdvanceFragment.this, AppsConstant.BACK_CAMREA_OPEN, "ForStore", screenName);
                                        customBuilder.dismiss();
                                    }
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

        // Show list
        categoryTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subCategoryTV.setText("Select Sub Category");
                if (categoryList != null && categoryList.size() > 0 && categoryYN != null &&
                        categoryYN.equalsIgnoreCase(AppsConstant.YES)) {

                    CustomBuilder categoryDialog = new CustomBuilder(getContext(), "Select Category", true);
                    categoryDialog.setSingleChoiceItems(categoryList, null, new CustomBuilder.OnClickListener() {
                        @Override
                        public void onClick(CustomBuilder builder, Object selectedObject) {
                            categoryListItem = (CategoryListItem) selectedObject;
                            categoryTV.setText(categoryListItem.getValue());
                            //   categoryType = categoryListItem.getValue();
                            categoryCode = categoryListItem.getCode();

                            if (screenName.equalsIgnoreCase(TicketApprovalFragment.screenName)) {
                                redirectBTN.setVisibility(View.GONE);
                                closeBTN.setVisibility(View.VISIBLE);
                                if (categoryCode != null && !categoryCode.equalsIgnoreCase("")
                                        && getTicketDetailResponseModel != null && getTicketDetailResponseModel.getGetTicketDetailResult() != null
                                        && getTicketDetailResponseModel.getGetTicketDetailResult().getCategoryID() != null
                                        && !getTicketDetailResponseModel.getGetTicketDetailResult().getCategoryID().equalsIgnoreCase("")) {
                                    if (!categoryCode.equalsIgnoreCase(getTicketDetailResponseModel.getGetTicketDetailResult().getCategoryID())) {
                                        redirectBTN.setVisibility(View.VISIBLE);
                                        closeBTN.setVisibility(View.GONE);
                                    }
                                }
                            }
                            if (ticketPageInitResponseModel != null && ticketPageInitResponseModel.getGetTicketPageInitResult() != null
                                    && ticketPageInitResponseModel.getGetTicketPageInitResult().getSubCategoryYN() != null &&
                                    ticketPageInitResponseModel.getGetTicketPageInitResult().getSubCategoryYN().equalsIgnoreCase("Y")) {
                                sendSubCategoryRequestData(categoryCode);

                            }
                            builder.dismiss();
                        }
                    });
                    categoryDialog.show();

                }
            }
        });

        subCategoryTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (categoryCode != null && !categoryCode.equalsIgnoreCase("")) {
                    if (subCategoryList != null && subCategoryList.size() > 0
                            && subCategoryYN.equalsIgnoreCase(AppsConstant.YES)) {

                        CustomBuilder categoryDialog = new CustomBuilder(getContext(), "Select Sub-Category", true);
                        categoryDialog.setSingleChoiceItems(subCategoryList, null, new CustomBuilder.OnClickListener() {
                            @Override
                            public void onClick(CustomBuilder builder, Object selectedObject) {
                                subCategoryItem = (CategoryListItem) selectedObject;
                                subCategoryTV.setText(subCategoryItem.getValue());
                                //   categoryType = categoryListItem.getValue();
                                subCategoryCode = subCategoryItem.getCode();

                                builder.dismiss();
                            }
                        });
                        categoryDialog.show();

                    }
                } else {

                    new AlertCustomDialog(context, "Please Select Category");
                    return;

                }
            }
        });

        ticketTypeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ticketTypeList != null && ticketTypeList.size() > 0) {

                    //  final ArrayList<TourReasonListModel> tourReasonListModel = empChangeResponseModel.getGetDetailsOnEmpChangeResult().getTourReasonList();
                    CustomBuilder ticketTypeDialog = new CustomBuilder(getContext(), "Select Type", true);
                    ticketTypeDialog.setSingleChoiceItems(ticketTypeList, null, new CustomBuilder.OnClickListener() {
                        @Override
                        public void onClick(CustomBuilder builder, Object selectedObject) {
                            ticketTypeItem = (CategoryListItem) selectedObject;
                            ticketTypeTV.setText(ticketTypeItem.getValue());
                            ticketTypeCode = ticketTypeItem.getCode();
                            builder.dismiss();
                        }
                    });
                    ticketTypeDialog.show();

                }
            }
        });

        priorityTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (priorityList != null && priorityList.size() > 0) {

                    //  final ArrayList<TourReasonListModel> tourReasonListModel = empChangeResponseModel.getGetDetailsOnEmpChangeResult().getTourReasonList();
                    CustomBuilder priorityDialog = new CustomBuilder(getContext(), "Select Priority", true);
                    priorityDialog.setSingleChoiceItems(priorityList, null, new CustomBuilder.OnClickListener() {
                        @Override
                        public void onClick(CustomBuilder builder, Object selectedObject) {
                            priorityListItem = (CategoryListItem) selectedObject;
                            priorityTV.setText(priorityListItem.getValue());
                            priorityCode = priorityListItem.getCode();
                            builder.dismiss();
                        }
                    });
                    priorityDialog.show();

                }
            }
        });

/*        feedbackRatingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (feedbackRatingList != null && feedbackRatingList.size() > 0) {

                    //  final ArrayList<TourReasonListModel> tourReasonListModel = empChangeResponseModel.getGetDetailsOnEmpChangeResult().getTourReasonList();
                    CustomBuilder feedbackDialog = new CustomBuilder(getContext(), "Select Feedback", true);
                    feedbackDialog.setSingleChoiceItems(feedbackRatingList, null, new CustomBuilder.OnClickListener() {
                        @Override
                        public void onClick(CustomBuilder builder, Object selectedObject) {
                            feedbackListItem = (CategoryListItem) selectedObject;
                            feedbackRatingTV.setText(feedbackListItem.getValue());
                            feedbackCode = feedbackListItem.getCode();
                            builder.dismiss();
                        }
                    });
                    feedbackDialog.show();

                }
            }
        });*/


        saveDraftBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromButton = "Save";
                doSubmitOperation();
            }
        });
        closeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromButton = "Close";
                doSubmitOperation();
            }
        });
        redirectBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromButton = "Redirect";
                doSubmitOperation();
            }
        });
        sendAdvanceRequestData();

        if (ticketItem != null && ticketItem.getTicketID() != null
                && !ticketItem.getTicketID().equalsIgnoreCase("0")) {

            if (screenName.equalsIgnoreCase(TicketApprovalFragment.screenName)) {
                ticketId = ticketItem.getTicketID();
                sendTicketInitRequestData();
                remarksDataLl.setVisibility(View.VISIBLE);
                remarksLinearLayout1.setVisibility(View.VISIBLE);
                sendViewRequestSummaryData();


            } else if (screenName.equalsIgnoreCase(TicketSummaryFragment.screenName)) {
                ticketId = ticketItem.getTicketID();
                sendTicketInitRequestData();
                remarksDataLl.setVisibility(View.VISIBLE);
                remarksLinearLayout1.setVisibility(View.VISIBLE);
                sendViewRequestSummaryData();

            }
        }
        /*else if (pendingTicketItem != null && pendingTicketItem.getTicketID() != null
                && !pendingTicketItem.getTicketID().equalsIgnoreCase("0")) {

            ticketId = pendingTicketItem.getTicketID();
            remarksDataLl.setVisibility(View.VISIBLE);
            remarksLinearLayout1.setVisibility(View.VISIBLE);
            sendViewRequestSummaryData();
        }*/
        else if (ticketId != null && ticketId.equalsIgnoreCase("0")) {
            uploadFileList = new ArrayList<SupportDocsItemModel>();
            remarksLinearLayout1.setVisibility(View.GONE);
            //sendAdvanceRequestData();
            sendTicketInitRequestData();

        }


    }

    public void sendAdvanceRequestData() {
    /*    Utility.showHidePregress(progressbar, true);
        MainActivity.isAnimationLoaded = false;
        ((MainActivity) getActivity()).showHideProgress(true);*/

        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.getAdvanceSummaryData(),
                CommunicationConstant.API_GET_ADVANCE_PAGE_INIT, true);
    }

    public void sendTicketInitRequestData() {
        initRequestModel = new TicketPageInitRequestModel();
        if(screenName.equalsIgnoreCase(TicketApprovalFragment.screenName)){
            initRequestModel.setSelfOrOther("S");
        }else {
            if (ticketFor != null && !ticketFor.equalsIgnoreCase("")) {
                initRequestModel.setSelfOrOther(ticketFor);
            }
        }

        initRequestModel.setTicektID(ticketId);
        Utility.showHidePregress(progressbar, true);
        // MainActivity.isAnimationLoaded = false;
        //((MainActivity) getActivity()).showHideProgress(true);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.getTicketPageInitRequestData(initRequestModel),
                CommunicationConstant.API_GET_TICKET_PAGE_INIT, true);
    }

    public void sendSubCategoryRequestData(String categoryCode) {
        categoryType = "SubCategory";
        subCategoryRequestModel = new SubCategoryRequestModel();
        if (ticketFor != null && !ticketFor.equalsIgnoreCase("")) {
            subCategoryRequestModel.setSelfOrOther(ticketFor);
        }
        PageInputModel pageInputModel = new PageInputModel();
        pageInputModel.setParamCode(categoryCode);
        pageInputModel.setParamType(categoryType);
        subCategoryRequestModel.setPageInput(pageInputModel);
        Utility.showHidePregress(progressbar, true);
        MainActivity.isAnimationLoaded = false;
        ((MainActivity) getActivity()).showHideProgress(true);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.getSubCategoryData(subCategoryRequestModel),
                CommunicationConstant.API_GET_COMMON_LIST, true);
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
        if (requestCode == TICKET_EMP_ADV) {
            if (data != null) {
                ContactListModel item = (ContactListModel) data.getSerializableExtra(SearchOnbehalfActivity.SELECTED_TICKET_EMP_ADV);
                if (item != null) {
                    //String[] empname = item.getName().split("\\(");
                    empNameTV.setText(item.getName());
                    empId = item.getCustomerEmpID();
                    customerCorpId=item.getCustomerCorpID();
                    //  empCodeTV.setText(item.getCustomerCorpID());
                    employItem = item;
                }
            }
        }
        final SupportDocsItemModel fileObj = new SupportDocsItemModel();
        if (requestCode == UPLOAD_DOC_REQUEST && resultCode == RESULT_OK) {
            boolean fileShow = true;
            final Uri uri = data.getData();
            String encodeFileToBase64Binary = null;
            if (data != null) {
                String path = data.getStringExtra("path");
                System.out.print(path);
                Uri uploadedFilePath = data.getData();
                String filename = Utility.getFileName(uploadedFilePath, context);
                filename = filename.toLowerCase();
                String fileDesc = Utility.getFileName(uploadedFilePath, context);
                String[] extList = filename.split("\\.");
                System.out.print(extList[1].toString());
                String extension = "." + extList[extList.length - 1];
                //  encodeFileToBase64Binary = Utility.fileToBase64Conversion(data.getData(), context);
                // Log.d("TAG", "RAR Base 64 :" + encodeFileToBase64Binary);
                extensionList = Arrays.asList(advanceRequestResponseModel.getGetAdvancePageInitResult().getDocValidation().getExtensions());
                if (!extensionList.contains(extension.toLowerCase())) {
                    CustomDialog.alertWithOk(context, advanceRequestResponseModel.getGetAdvancePageInitResult().getDocValidation().getMessage());
                    return;
                }
                if (Utility.calculateBitmapSize(data.getData(), context) > Utility.maxLimit) {
                    CustomDialog.alertWithOk(context, Utility.sizeMsg);
                    return;
                }
                fileObj.setDocPathUri(uploadedFilePath);

                if (filename.contains(".pdf")) {
                    try {
                        encodeFileToBase64Binary = Utility.fileToBase64Conversion(data.getData(), context);
                        fileObj.setDocFile(filename);
                        fileObj.setName(fileDesc);
                        fileObj.setExtension(extension);

                    } catch (Exception e) {
                        System.out.print(e.toString());
                    }
                } else if (filename.contains(".jpg") || filename.contains(".png") ||
                        filename.contains(".jpeg") || filename.contains(".bmp") || filename.contains(".BMP")) {

                    bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    File mediaFile = null;
                    if (bitmap != null) {
                        encodeFileToBase64Binary = Utility.converBitmapToBase64(bitmap);
                        byte[] imageBytes = ImageUtil.bitmapToByteArray(rotateImage(bitmap, 270));

                        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DCIM), "");
                        mediaFile = new File(mediaStorageDir.getPath() + File.separator + purpose + ".jpg");
                        if (mediaFile != null) {
                            try {
                                FileOutputStream fos = new FileOutputStream(mediaFile);
                                fos.write(imageBytes);
                                fileObj.setDocFile(filename);
                                fileObj.setName(fileDesc);
                                fileObj.setExtension(extension);
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
                } else if (filename.contains(".docx") || filename.contains(".doc")) {
                    try {
                        encodeFileToBase64Binary = Utility.fileToBase64Conversion(data.getData(), context);
                        fileObj.setDocFile(filename);
                        fileObj.setName(fileDesc);
                        fileObj.setExtension(extension);
                    } catch (Exception e) {

                    }
                } else if (filename.contains(".xlsx") || filename.contains(".xls")) {
                    try {
                        encodeFileToBase64Binary = Utility.fileToBase64Conversion(data.getData(), context);
                        fileObj.setDocFile(filename);
                        fileObj.setName(fileDesc);
                        fileObj.setExtension(extension);

                    } catch (Exception e) {

                    }
                } else if (filename.contains(".txt")) {
                    try {
                        encodeFileToBase64Binary = Utility.fileToBase64Conversion(data.getData(), context);
                        fileObj.setDocFile(filename);
                        fileObj.setName(fileDesc);
                        fileObj.setExtension(extension);
                    } catch (Exception e) {

                    }
                } else if (filename.contains(".gif")) {
                    encodeFileToBase64Binary = Utility.fileToBase64Conversion(data.getData(), context);
                    fileObj.setDocFile(filename);
                    fileObj.setName(fileDesc);
                    fileObj.setExtension(extension);
                } else if (filename.contains(".rar")) {
                    encodeFileToBase64Binary = Utility.fileToBase64Conversion(data.getData(), context);
                    fileObj.setDocFile(filename);
                    fileObj.setName(fileDesc);
                    fileObj.setExtension(extension);
                } else if (filename.contains(".zip")) {
                    encodeFileToBase64Binary = Utility.fileToBase64Conversion(data.getData(), context);
                    fileObj.setDocFile(filename);
                    fileObj.setName(fileDesc);
                    fileObj.setExtension(extension);
                }


               /* if (Utility.calcBase64SizeInKBytes(encodeFileToBase64Binary) > Utility.maxLimit) {
                    CustomDialog.alertWithOk(context, Utility.sizeMsg);
                    return;
                }*/
                if (fileShow) {
                    if (uploadFileList.size() > 0) {
                        for (int i = 1; i <= uploadFileList.size(); i++) {
                            fileObj.setBase64Data(encodeFileToBase64Binary);
                            fileObj.setFlag("N");
                            String seqNo = String.valueOf(i + 1);
                            Log.d("seqNo", "seqNo");
                            uploadFileList.add(fileObj);
                            break;
                        }
                    } else {
                        fileObj.setBase64Data(encodeFileToBase64Binary);
                        fileObj.setFlag("N");
                        uploadFileList.add(fileObj);
                    }
                }
                refreshList();

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
                    byte[] imageBytes = ImageUtil.bitmapToByteArray(rotateImage(bitmap, 270));

                    File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DCIM), "");
                    mediaFile = new File(mediaStorageDir.getPath() + File.separator + purpose + ".jpg");
                    if (mediaFile != null) {
                        try {
                            FileOutputStream fos = new FileOutputStream(mediaFile);
                            fos.write(imageBytes);
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
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.image_preview_expense);
            final TextView filenameET = (TextView) dialog.findViewById(R.id.filenameET);
            ImageView imageView = (ImageView) dialog.findViewById(R.id.img_preview);
            imageView.setImageBitmap(bitmap);

            int textColor = Utility.getTextColorCode(preferences);
            TextView tv_header_text = (TextView) dialog.findViewById(R.id.tv_header_text);
            tv_header_text.setTextColor(textColor);
            tv_header_text.setText("Supporting Documents");
            int bgColor = Utility.getBgColorCode(context, preferences);
            RelativeLayout fl_actionBarContainer = (RelativeLayout) dialog.findViewById(R.id.fl_actionBarContainer);
            fl_actionBarContainer.setBackgroundColor(bgColor);

            (dialog).findViewById(R.id.ibRight).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (filenameET.getText().toString().equalsIgnoreCase("")) {
                        new AlertCustomDialog(context, "Please enter file name");
                    } else {
                        fileObj.setDocFile(filenameET.getText().toString() + ".jpg");
                        fileObj.setName(filenameET.getText().toString() + ".jpg");

                        boolean fileShow1 = true;

                        if (fileShow1) {
                            String encodeFileToBase64Binary = Utility.converBitmapToBase64(bitmap);
                            // Log.d("TAG","IMAGE SIZE : "+ Utility.calcBase64SizeInKBytes(encodeFileToBase64Binary));

                            if (uploadFileList.size() > 0) {
                                for (int i = 1; i <= uploadFileList.size(); i++) {
                                    fileObj.setBase64Data(encodeFileToBase64Binary);
                                    fileObj.setFlag("N");
                                    fileObj.setExtension(".jpg");
                                    String seqNo = String.valueOf(i + 1);
                                    Log.d("seqNo", "seqNo");
                                    uploadFileList.add(fileObj);
                                    break;
                                }
                            } else {
                                fileObj.setBase64Data(encodeFileToBase64Binary);
                                fileObj.setFlag("N");
                                fileObj.setExtension(".jpg");
                                uploadFileList.add(fileObj);
                            }
                        }
                        refreshList();
                        dialog.dismiss();
                    }
                }
            });
            (dialog).findViewById(R.id.ibWrong).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

    }

    private void refreshList() {
        if (uploadFileList != null && uploadFileList.size() > 0) {
            errorLinearLayout.setVisibility(View.GONE);
            expenseRecyclerView.setVisibility(View.VISIBLE);
            docAdapter = new DocumentUploadAdapter(uploadFileList, context, AppsConstant.EDIT, errorLinearLayout, getActivity());
            expenseRecyclerView.setAdapter(docAdapter);
            docAdapter.notifyDataSetChanged();
        } else {
            errorLinearLayout.setVisibility(View.VISIBLE);
            expenseRecyclerView.setVisibility(View.GONE);
        }
    }


    private void doSubmitOperation() {
        // Prepare request for ticket submission
        ticketSubmitRequestModel = new TicketSubmitRequestModel();
        ticketDetailModel = new TicketDetailModel();
        Log.d("simpleOrAdvance value", simpleOrAdvance);

        if (fromButton.equalsIgnoreCase(AppsConstant.SUBMIT)
                || fromButton.equalsIgnoreCase(AppsConstant.CLOSE)
                || fromButton.equalsIgnoreCase(AppsConstant.REDIRECT)) {

            if (empNameTV.getText().toString().equalsIgnoreCase("")) {
                isSubmitClicked = true;
                new AlertCustomDialog(context, getResources().getString(R.string.select_user));
                return;
            }
            if (categoryCode.equalsIgnoreCase("") &&
                    categoryYN.equalsIgnoreCase(AppsConstant.YES)) {
                isSubmitClicked = true;
                new AlertCustomDialog(context, "Please select category");
                return;
            }

            if (subCategoryCode.equalsIgnoreCase("")
                    && simpleOrAdvance.equalsIgnoreCase(AppsConstant.ADVANCE_VIEW)
                    && subCategoryYN.equalsIgnoreCase(AppsConstant.YES)) {
                isSubmitClicked = true;
                new AlertCustomDialog(context, "Please select sub category");
                return;
            }

            if (ticketTypeCode.equalsIgnoreCase("")
                    && simpleOrAdvance.equalsIgnoreCase(AppsConstant.ADVANCE_VIEW)) {
                isSubmitClicked = true;
                new AlertCustomDialog(context, "Please select type");
                return;
            }

            if (priorityCode.equalsIgnoreCase("")) {
                isSubmitClicked = true;
                new AlertCustomDialog(context, "Please select priority");
                return;
            }

            if (subjectET.getText().toString().equalsIgnoreCase("")) {
                isSubmitClicked = true;
                new AlertCustomDialog(context, "Please enter subject");
                return;
            }

            if (descriptionET.getText().toString().equalsIgnoreCase("")) {
                isSubmitClicked = true;
                new AlertCustomDialog(context, "Please enter description");
                return;
            }
            if (screenName.equalsIgnoreCase(TicketSummaryFragment.screenName)
                    || screenName.equalsIgnoreCase(TicketApprovalFragment.screenName)) {
                String status = "";
                if (getTicketDetailResponseModel != null && getTicketDetailResponseModel.getGetTicketDetailResult() != null
                        && getTicketDetailResponseModel.getGetTicketDetailResult().getStatusDesc() != null
                        && !getTicketDetailResponseModel.getGetTicketDetailResult().getStatusDesc().equalsIgnoreCase("")) {
                    status = getTicketDetailResponseModel.getGetTicketDetailResult().getStatusDesc();

                }
                if (fromButton.equalsIgnoreCase(AppsConstant.SUBMIT) ||
                        fromButton.equalsIgnoreCase(AppsConstant.REDIRECT)) {
                    if (!status.equalsIgnoreCase(AppsConstant.DRAFT)) {
                        if (remarksET.getText().toString().equalsIgnoreCase("")) {
                            isSubmitClicked = true;
                            new AlertCustomDialog(context, "Please enter remarks");
                            return;
                        }
                    }
                }
            }

          /*  if (feedbackCode.equalsIgnoreCase("")
                    && simpleOrAdvance.equalsIgnoreCase(AppsConstant.ADVANCE_VIEW)) {
                isSubmitClicked = true;
                new AlertCustomDialog(context, "Please select rating");
                return;
            }*/

          /*  if (feedbackET.getText().toString().equalsIgnoreCase("")
                    && simpleOrAdvance.equalsIgnoreCase(AppsConstant.ADVANCE_VIEW)) {
                isSubmitClicked = true;
                new AlertCustomDialog(context, "Please enter feedback");
                return;
            }*/


           /* else if (toDate.equalsIgnoreCase("") || toDate.equalsIgnoreCase("--/--/----")) {
                isSubmitClicked = true;
                new AlertCustomDialog(context, "Please Enter To Date");
                return;
            } else if (remarksET.getText().toString().equalsIgnoreCase("")) {
                isSubmitClicked = true;
                new AlertCustomDialog(context, "Please Enter Remarks");
                return;
            } else {*/

            ticketDetailModel.setCustomerEmpID(empId);
            ticketDetailModel.setCustomerCorpID(customerCorpId);

            ticketDetailModel.setTicketID(ticketId);
            ticketDetailModel.setTicketPriorityID(priorityCode);
            ticketDetailModel.setTicketTypeID(ticketTypeCode);
            ticketDetailModel.setCategoryID(categoryCode);
            ticketDetailModel.setSubCategoryID(subCategoryCode);
            ticketDetailModel.setSubject(subjectET.getText().toString());
            ticketDetailModel.setComment(descriptionET.getText().toString());
            if (screenName.equalsIgnoreCase(TicketSummaryFragment.screenName)
                    || screenName.equalsIgnoreCase(TicketApprovalFragment.screenName)) {
                ticketFor = "S";
                ticketDetailModel.setNewRemark(remarksET.getText().toString());
            }
            // ticketDetailModel.setFeedback(feedbackET.getText().toString());
            // ticketDetailModel.setFeedbackCode(feedbackCode);
            ticketDetailModel.setFromButton(fromButton);

            if (uploadFileList != null && uploadFileList.size() > 0) {
                for (int i = 0; i < uploadFileList.size(); i++) {
                    SupportDocsItemModel model = uploadFileList.get(i);
                    model.setSeqNo(i + 1);
                    uploadFileList.set(i, model);
                }
            }
            Utility.showHidePregress(progressbar, true);
            MainActivity.isAnimationLoaded = false;
            ((MainActivity) getActivity()).showHideProgress(true);
            ticketDetailModel.setDocList(uploadFileList);
            ticketSubmitRequestModel.setTicketDetail(ticketDetailModel);
            ticketSubmitRequestModel.setSelfOrOther(ticketFor);

            CommunicationManager.getInstance().sendPostRequest(this,
                    AppRequestJSONString.ticketRequest(ticketSubmitRequestModel),
                    CommunicationConstant.API_SAVE_TICKET, true);

            //}

        }


        if (fromButton.equalsIgnoreCase(AppsConstant.SAVE_AS_DRAFT)) {
            if (empNameTV.getText().toString().equalsIgnoreCase("")) {
                new AlertCustomDialog(context, getResources().getString(R.string.select_user));
                return;
            }
            ticketDetailModel.setCustomerEmpID(empId);
            ticketDetailModel.setCustomerCorpID(customerCorpId);

            ticketDetailModel.setTicketID(ticketId);
            ticketDetailModel.setTicketPriorityID(priorityCode);
            ticketDetailModel.setTicketTypeID(ticketTypeCode);
            ticketDetailModel.setCategoryID(categoryCode);
            ticketDetailModel.setSubCategoryID(subCategoryCode);
            ticketDetailModel.setSubject(subjectET.getText().toString());
            ticketDetailModel.setComment(descriptionET.getText().toString());
            ticketDetailModel.setFromButton(fromButton);
            if (screenName.equalsIgnoreCase(TicketSummaryFragment.screenName)) {
                ticketDetailModel.setNewRemark(remarksET.getText().toString());
            }
            //  ticketDetailModel.setFeedback(feedbackET.getText().toString());
            //   ticketDetailModel.setFeedbackCode(feedbackCode);

            if (uploadFileList != null && uploadFileList.size() > 0) {
                for (int i = 0; i < uploadFileList.size(); i++) {
                    SupportDocsItemModel model = uploadFileList.get(i);
                    model.setSeqNo(i + 1);
                    uploadFileList.set(i, model);
                }
            }


            Utility.showHidePregress(progressbar, true);
            MainActivity.isAnimationLoaded = false;
            ((MainActivity) getActivity()).showHideProgress(true);
            ticketDetailModel.setDocList(uploadFileList);
            ticketSubmitRequestModel.setTicketDetail(ticketDetailModel);
            ticketSubmitRequestModel.setSelfOrOther(ticketFor);
            if (getScreenName().equalsIgnoreCase(AppsConstant.PENDING_APPROVAL)) {
               /* CommunicationManager.getInstance().sendPostRequest(this,
                        AppRequestJSONString.ticketRequest(ticketSubmitRequestModel),
                        CommunicationConstant.API_SAVE_TICKET, true);*/
            } else {
                CommunicationManager.getInstance().sendPostRequest(this,
                        AppRequestJSONString.ticketRequest(ticketSubmitRequestModel),
                        CommunicationConstant.API_SAVE_TICKET, true);
            }


        }

    }

    private void sendViewRequestSummaryData() {
        requestDetail = new GetTicketDetailRequestModel();
        if (ticketItem != null && ticketItem.getTicketID() != null &&
                !ticketItem.getTicketID().equalsIgnoreCase("")) {
            requestDetail.setTicketID(ticketItem.getTicketID());
        }
        requestDetail.setSimpleOrAdvance(ticketItem.getSimpleOrAdvance());

        Utility.showHidePregress(progressbar, true);
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.ticketSummaryDetails(requestDetail),
                CommunicationConstant.API_GET_TICKETS_DETAIL, true);
    }

    @Override
    public void validateResponse(ResponseData response) {

        // ((MainActivity) getActivity()).showHideProgress(false);
        Log.d("TAG", "response data " + response.isSuccess());
  /*      progressbar.setVisibility(View.GONE);
        MainActivity.isAnimationLoaded = true;
        Utility.showHidePregress(progressbar, false);
        ((MainActivity) getActivity()).showHideProgress(false);
        isSubmitClicked = true;
        showHideProgressView(false);*/

        progressbar.setVisibility(View.GONE);
        MainActivity.isAnimationLoaded = true;
        Utility.showHidePregress(progressbar, false);
        ((MainActivity) getActivity()).showHideProgress(false);
        isSubmitClicked = true;
        showHideProgressView(false);
        switch (response.getRequestData().getReqApiId()) {
            case CommunicationConstant.API_GET_ADVANCE_PAGE_INIT:
                String str1 = response.getResponseData();
                Log.d("TAG", "Advance Response : " + str1);

                advanceRequestResponseModel = AdvanceRequestResponseModel.create(str1);
                if (advanceRequestResponseModel != null &&
                        advanceRequestResponseModel.getGetAdvancePageInitResult() != null &&
                        advanceRequestResponseModel.getGetAdvancePageInitResult().getErrorCode().
                                equalsIgnoreCase(AppsConstant.SUCCESS)) {
                    extensionList = Arrays.asList(advanceRequestResponseModel.getGetAdvancePageInitResult().getDocValidation().getExtensions());
                    //   sendTicketInitRequestData();
                }
                break;

            case CommunicationConstant.API_GET_TICKET_PAGE_INIT:
                String ticketInitResp = response.getResponseData();
                Log.d("TAG", "Ticket Init Response : " + ticketInitResp);

                ticketPageInitResponseModel = TicketPageInitResponseModel.create(ticketInitResp);
                if (ticketPageInitResponseModel != null &&
                        ticketPageInitResponseModel.getGetTicketPageInitResult() != null &&
                        ticketPageInitResponseModel.getGetTicketPageInitResult().
                                getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {

                    if (ticketPageInitResponseModel.getGetTicketPageInitResult().getCategoryList() != null
                            && ticketPageInitResponseModel.getGetTicketPageInitResult().getCategoryList().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)
                            && ticketPageInitResponseModel.getGetTicketPageInitResult().getCategoryList().getList() != null
                            && ticketPageInitResponseModel.getGetTicketPageInitResult().getCategoryList().getList().size() > 0) {
                        categoryList = ticketPageInitResponseModel.getGetTicketPageInitResult().getCategoryList().getList();
                    }

                    if (ticketPageInitResponseModel.getGetTicketPageInitResult().getFeedbackRatingList() != null
                            && ticketPageInitResponseModel.getGetTicketPageInitResult().getFeedbackRatingList().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)
                            && ticketPageInitResponseModel.getGetTicketPageInitResult().getFeedbackRatingList().getList() != null
                            && ticketPageInitResponseModel.getGetTicketPageInitResult().getFeedbackRatingList().getList().size() > 0) {
                        feedbackRatingList = ticketPageInitResponseModel.getGetTicketPageInitResult().getFeedbackRatingList().getList();
                    }

                    if (ticketPageInitResponseModel.getGetTicketPageInitResult().getContactList() != null
                            && ticketPageInitResponseModel.getGetTicketPageInitResult().getContactList().size() > 0) {
                        contactList = ticketPageInitResponseModel.getGetTicketPageInitResult().getContactList();

                    }

                    if (ticketPageInitResponseModel.getGetTicketPageInitResult().getPriorityList() != null
                            && ticketPageInitResponseModel.getGetTicketPageInitResult().getPriorityList().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)
                            && ticketPageInitResponseModel.getGetTicketPageInitResult().getPriorityList().getList() != null
                            && ticketPageInitResponseModel.getGetTicketPageInitResult().getPriorityList().getList().size() > 0) {
                        priorityList = ticketPageInitResponseModel.getGetTicketPageInitResult().getPriorityList().getList();
                    }

                    if (ticketPageInitResponseModel.getGetTicketPageInitResult().getTicketTypeList() != null
                            && ticketPageInitResponseModel.getGetTicketPageInitResult().getTicketTypeList().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)
                            && ticketPageInitResponseModel.getGetTicketPageInitResult().getTicketTypeList().getList() != null
                            && ticketPageInitResponseModel.getGetTicketPageInitResult().getTicketTypeList().getList().size() > 0) {
                        ticketTypeList = ticketPageInitResponseModel.getGetTicketPageInitResult().getTicketTypeList().getList();
                    }
                    // if(ticketPageInitResponseModel.getGetTicketPageInitResult().getSimpleOrAdvanceView()!=null) {
                    if (ticketId.equalsIgnoreCase("0")) {
                        updateUI(ticketPageInitResponseModel.getGetTicketPageInitResult());
                    }
                    //}
                }
                break;

            case CommunicationConstant.API_GET_COMMON_LIST:
                String subCategoryResp = response.getResponseData();
                Log.d("TAG", "Sub category Response : " + subCategoryResp);

                subCategoryResponseModel = SubCategoryResponseModel.create(subCategoryResp);
                if (subCategoryResponseModel != null &&
                        subCategoryResponseModel.getGetCommonListResult() != null &&
                        subCategoryResponseModel.getGetCommonListResult().
                                getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {

                    if (subCategoryResponseModel.getGetCommonListResult().getList() != null
                            && subCategoryResponseModel.getGetCommonListResult().getList().size() > 0) {
                        subCategoryList = subCategoryResponseModel.getGetCommonListResult().getList();
                    }


                }
                break;

            case CommunicationConstant.API_SAVE_TICKET:
                String ticketResponse = response.getResponseData();
                Log.d("TAG", "ticket submission response : " + ticketResponse);
                tourResponseModel = TourResponseModel.create(ticketResponse);
                if (tourResponseModel != null && tourResponseModel.getSaveTicketResult() != null
                        && tourResponseModel.getSaveTicketResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                    if (screenName != null && screenName.equalsIgnoreCase(TicketSummaryFragment.screenName)) {
                        isSubmitClicked = true;
                        CustomDialog.alertOkWithFinishFragment1(context, tourResponseModel.getSaveTicketResult().getErrorMessage(), mUserActionListener, IAction.RAISE_TICKET_ADV_SUMMARY, true);
                    } else if (screenName != null && screenName.equalsIgnoreCase(TicketApprovalFragment.screenName)) {
                        isSubmitClicked = true;
                        CustomDialog.alertOkWithFinishFragment1(context, tourResponseModel.getSaveTicketResult().getErrorMessage(), mUserActionListener, IAction.TICKET_APPROVAL, true);
                    } else {
                        isSubmitClicked = true;
                        CustomDialog.alertOkWithFinishFragment(context, tourResponseModel.getSaveTicketResult().getErrorMessage(), mUserActionListener, IAction.HOME_VIEW, true);
                    }
                } else {
                    isSubmitClicked = true;
                    new AlertCustomDialog(getActivity(), tourResponseModel.getSaveTicketResult().getErrorMessage());
                }
                break;

            case CommunicationConstant.API_GET_TICKETS_DETAIL:
                String str = response.getResponseData();
                Log.d("TAG", "Ticket detail Response : " + str);
                getTicketDetailResponseModel = GetTicketDetailResponseModel.create(str);

                if (getTicketDetailResponseModel != null && getTicketDetailResponseModel.
                        getGetTicketDetailResult() != null
                        && getTicketDetailResponseModel.getGetTicketDetailResult().
                        getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {

                    updateUIWithData(getTicketDetailResponseModel.getGetTicketDetailResult());
                    refreshRemarksList(getTicketDetailResponseModel.getGetTicketDetailResult().getRemarks());
                    uploadFileList = getTicketDetailResponseModel.getGetTicketDetailResult().getDocList();
                    refreshDocumentList(getTicketDetailResponseModel.getGetTicketDetailResult().getDocList());
                }

                break;

            default:
                break;
        }
        super.validateResponse(response);
    }


    private void updateUI(GetTicketPageInitResultModel item) {
        // remarksLinearLayout1.setVisibility(View.GONE);
        searchLayout.setVisibility(View.VISIBLE);
        categoryLl.setVisibility(View.VISIBLE);
        subCategoryLl.setVisibility(View.VISIBLE);
        // RatingLl.setVisibility(View.VISIBLE);
        //feedbackLL.setVisibility(View.VISIBLE);
        ticketTypeLl.setVisibility(View.VISIBLE);
        simpleOrAdvance = item.getSimpleOrAdvanceView();
        subCategoryYN = item.getSubCategoryYN();
        categoryYN = item.getCategoryYN();
        if (item.getCategoryYN() != null &&
                !item.getCategoryYN().equalsIgnoreCase("") &&
                item.getCategoryYN().equalsIgnoreCase(AppsConstant.NO)) {
            categoryLl.setVisibility(View.GONE);
        }

        if (item.getSubCategoryYN() != null &&
                !item.getSubCategoryYN().equalsIgnoreCase("") &&
                item.getSubCategoryYN().equalsIgnoreCase(AppsConstant.NO)) {

            subCategoryLl.setVisibility(View.GONE);
        }

        if (item.getSimpleOrAdvanceView() != null
                && item.getSimpleOrAdvanceView().
                equalsIgnoreCase(AppsConstant.SIMPLE_VIEW)) {
            //searchLayout.setVisibility(View.GONE);
            subCategoryLl.setVisibility(View.GONE);
            //   RatingLl.setVisibility(View.GONE);
            //   feedbackLL.setVisibility(View.GONE);
            ticketTypeLl.setVisibility(View.GONE);
            if (ticketFor.equalsIgnoreCase(AppsConstant.SELF)) {
                saveDraftBTN.setVisibility(View.VISIBLE);
            }
        }

        if (contactList != null && ticketFor.equalsIgnoreCase("S")) {
            searchLayout.setVisibility(View.GONE);
            if (contactList.get(0).getEmpCode() != null
                    && !contactList.get(0).getEmpCode().equalsIgnoreCase("")) {
                empCode = contactList.get(0).getEmpCode();
            }
            empNameTV.setText(contactList.get(0).getName() + " " + "(" + empCode + ")");
            empId = contactList.get(0).getCustomerEmpID();
            customerCorpId = contactList.get(0).getCustomerCorpID();

        }


        //setupButtons(item);


    }

    private void setupButtons(GetTicketDetailResultModel item) {
        if (item.getButtons() != null) {
            for (String button : item.getButtons()) {
                if (button.equalsIgnoreCase(AppsConstant.CLOSE)) {
                    closeBTN.setVisibility(View.VISIBLE);
                }

                if (button.equalsIgnoreCase(AppsConstant.SAVE_AS_DRAFT)) {
                    saveDraftBTN.setVisibility(View.VISIBLE);
                }

            }
        }
    }

    public void askLocationPermision() {
        this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    // Edit Mode (via Dashboard flow)
    private void updateUIWithData(GetTicketDetailResultModel item) {
        searchLayout.setVisibility(View.GONE);
        if (item.getStatusDesc().equalsIgnoreCase(AppsConstant.DRAFT)) {
            plus_create_newIV.setVisibility(View.VISIBLE);
            ticketTypeLl.setVisibility(View.GONE);
            remarksLinearLayout1.setVisibility(View.GONE);


            if (ticketPageInitResponseModel != null && ticketPageInitResponseModel.getGetTicketPageInitResult() != null
                    && ticketPageInitResponseModel.getGetTicketPageInitResult().getSubCategoryYN() != null) {
                if (ticketPageInitResponseModel.getGetTicketPageInitResult().
                        getSubCategoryYN().equalsIgnoreCase("N")) {
                    subCategoryYN = "N";
                    subCategoryLl.setVisibility(View.GONE);

                }/*else if(ticketPageInitResponseModel.getGetTicketPageInitResult().
                        getSubCategoryYN().equalsIgnoreCase("Y")){
                    subCategoryYN="Y";
                    subCategoryLl.setVisibility(View.VISIBLE);
                }*/

            }

            if (ticketPageInitResponseModel != null && ticketPageInitResponseModel.getGetTicketPageInitResult() != null
                    && ticketPageInitResponseModel.getGetTicketPageInitResult().getCategoryYN() != null) {
                if (ticketPageInitResponseModel.getGetTicketPageInitResult().getCategoryYN()
                        .equalsIgnoreCase("N")) {
                    categoryYN = "N";
                    categoryLl.setVisibility(View.GONE);
                } else if (ticketPageInitResponseModel.getGetTicketPageInitResult().
                        getCategoryYN().equalsIgnoreCase("Y")) {
                    categoryYN = "Y";
                    categoryLl.setVisibility(View.VISIBLE);
                }
            }

            ticketTypeLl.setVisibility(View.GONE);
        } else {
            if (screenName.equalsIgnoreCase(TicketApprovalFragment.screenName)) {
                if (ticketPageInitResponseModel != null && ticketPageInitResponseModel.getGetTicketPageInitResult() != null
                        && ticketPageInitResponseModel.getGetTicketPageInitResult().getCategoryYN() != null) {
                    if (ticketPageInitResponseModel.getGetTicketPageInitResult().getCategoryYN()
                            .equalsIgnoreCase("N")) {
                        categoryYN = "N";
                        categoryLl.setVisibility(View.GONE);
                    } else if (ticketPageInitResponseModel.getGetTicketPageInitResult().
                            getCategoryYN().equalsIgnoreCase("Y")) {
                        categoryYN = "Y";
                        categoryTV.setEnabled(true);
                        categoryLl.setVisibility(View.VISIBLE);
                    }
                }

                if(ticketPageInitResponseModel!=null && ticketPageInitResponseModel.getGetTicketPageInitResult()!=null
                        && ticketPageInitResponseModel.getGetTicketPageInitResult().getPriorityList()!=null
                        && ticketPageInitResponseModel.getGetTicketPageInitResult().getPriorityList().getList()!=null
                        && ticketPageInitResponseModel.getGetTicketPageInitResult().getPriorityList().getList().size()>0){
                    priorityTV.setEnabled(true);
                    priorityList = ticketPageInitResponseModel.getGetTicketPageInitResult().getPriorityList().getList();
                }
            } else {
                categoryTV.setEnabled(false);
                priorityTV.setEnabled(false);

            }


            plus_create_newIV.setVisibility(View.GONE);
            subCategoryTV.setEnabled(false);
            ticketTypeTV.setEnabled(false);
            subjectET.setEnabled(false);
            descriptionET.setEnabled(false);

            if (item.getCategoryDesc() != null && !item.getCategoryDesc().equalsIgnoreCase("")) {
                categoryLl.setVisibility(View.VISIBLE);
            } else {
                categoryLl.setVisibility(View.GONE);
            }

            if (item.getSubCategoryDesc() != null &&
                    !item.getSubCategoryDesc().equalsIgnoreCase("")) {
                subCategoryLl.setVisibility(View.VISIBLE);
            } else {
                subCategoryLl.setVisibility(View.GONE);
            }

            if (item.getTicketTypeDesc() != null
                    && !item.getTicketTypeDesc().equalsIgnoreCase("")) {
                ticketTypeLl.setVisibility(View.VISIBLE);
            } else {
                ticketTypeLl.setVisibility(View.GONE);
            }

        }

        if (item.getCategoryDesc() != null &&
                !item.getCategoryDesc().equalsIgnoreCase("")) {
            //  categoryLl.setVisibility(View.VISIBLE);
            categoryTV.setText(item.getCategoryDesc());
            categoryCode = item.getCategoryID();

        }

        if (item.getSubCategoryDesc() != null) {
            //    subCategoryLl.setVisibility(View.VISIBLE);
            subCategoryTV.setText(item.getSubCategoryDesc());
            subCategoryCode = item.getSubCategoryID();


        }

        if (item.getTicketTypeDesc() != null) {
            // ticketTypeLl.setVisibility(View.VISIBLE);
            ticketTypeTV.setText(item.getTicketTypeDesc());
            ticketTypeCode = item.getTicketTypeID();

        }


        if (item.getCustomerEmpName() != null) {
            empNameTV.setText(item.getCustomerEmpName());
            empId = item.getCustomerEmpID();
        }


        if (item.getTicketPriorityDesc() != null) {

            priorityTV.setText(item.getTicketPriorityDesc());
            priorityCode = item.getTicketPriorityID();

        }

        if (item.getSubject() != null) {
            subjectET.setText(item.getSubject());
        }

        if (item.getComment() != null) {
            descriptionET.setText(item.getComment());

        }
        setupButtons(item);
    }

    private void disableField(GetTicketDetailResultModel item) {


    }


    private void refreshRemarksList(ArrayList<RemarkListItem> remarksItems) {
        if (remarksItems != null && remarksItems.size() > 0) {
            remarksLinearLayout.setVisibility(View.GONE);
            remarksRV.setLayoutManager(new LinearLayoutManager(getActivity()));
            remarksRV.setVisibility(View.VISIBLE);
            RemarksAdapter adapter = new RemarksAdapter(remarksItems, context, screenName, remarksLinearLayout);
            remarksRV.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            remarksLinearLayout.setVisibility(View.VISIBLE);
            remarksRV.setVisibility(View.GONE);
        }
    }

    private void refreshDocumentList(ArrayList<SupportDocsItemModel> uploadFileList) {
        if (uploadFileList != null && uploadFileList.size() > 0) {
            errorLinearLayout.setVisibility(View.GONE);
            expenseRecyclerView.setVisibility(View.VISIBLE);
            DocumentUploadAdapter adapter = null;
            if (screenName.equalsIgnoreCase(TicketSummaryFragment.screenName)) {
                if (ticketItem != null && ticketItem.getStatusDesc() != null && !ticketItem.getStatusDesc().equalsIgnoreCase("")) {
                    if (ticketItem.getStatusDesc().equalsIgnoreCase("Draft")) {
                        adapter = new DocumentUploadAdapter(uploadFileList, context, AppsConstant.EDIT, errorLinearLayout, getActivity());
                    } else {
                        adapter = new DocumentUploadAdapter(uploadFileList, context, AppsConstant.VIEW, errorLinearLayout, getActivity());

                    }
                }
            } else {
                adapter = new DocumentUploadAdapter(uploadFileList, context, AppsConstant.VIEW, errorLinearLayout, getActivity());
            }
            expenseRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            errorLinearLayout.setVisibility(View.VISIBLE);
            expenseRecyclerView.setVisibility(View.GONE);
        }
    }

    // Edit Mode (via Approver flow)
    private void updateUIWithApprovalData() {


    }
}

