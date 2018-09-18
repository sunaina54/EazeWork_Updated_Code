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
import hr.eazework.com.model.EmployItem;
import hr.eazework.com.model.SupportDocsItemModel;
import hr.eazework.com.ui.adapter.DocumentUploadAdapter;
import hr.eazework.com.ui.customview.CustomBuilder;
import hr.eazework.com.ui.customview.CustomDialog;
import hr.eazework.com.ui.fragment.Attendance.AttendanceApprovalFragment;
import hr.eazework.com.ui.fragment.BaseFragment;
import hr.eazework.com.ui.interfaces.IAction;
import hr.eazework.com.ui.util.AppsConstant;
import hr.eazework.com.ui.util.ImageUtil;
import hr.eazework.com.ui.util.PermissionUtil;
import hr.eazework.com.ui.util.Preferences;
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
    private String empId;
    private boolean isSubmitClicked = true;
    private Button saveDraftBTN, deleteBTN, submitBTN, rejectBTN, approvalBTN;
    private Preferences preferences;
    private TextView empNameTV, empCodeTV;
    private View progressbar;
    private EditText remarksET,subjectET;
    private RelativeLayout searchLayout;
    private EmployItem employItem;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (screenName != null && screenName.equalsIgnoreCase(AttendanceApprovalFragment.screenName)) {
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
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_create_ticket, container, false);
        context = getContext();

        setupScreen();
        return rootView;
    }

    private void setupScreen(){
        preferences = new Preferences(getContext());
        int textColor = Utility.getTextColorCode(preferences);
        ((TextView) getActivity().findViewById(R.id.tv_header_text)).setTextColor(textColor);

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
        empNameTV = (TextView) rootView.findViewById(R.id.empNameTV);
        empCodeTV = (TextView) rootView.findViewById(R.id.empCodeTV);
        remarksET = (EditText) rootView.findViewById(R.id.remarksET);
        subjectET = (EditText) rootView.findViewById(R.id.subjectET);
        searchLayout = (RelativeLayout) rootView.findViewById(R.id.searchLayout);
        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent theIntent = new Intent(getActivity(), SearchOnbehalfActivity.class);
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
                                        Utility.openCamera(getActivity(), CreateTicketAdvanceFragment  .this, AppsConstant.BACK_CAMREA_OPEN, "ForStore", screenName);
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

        sendAdvanceRequestData();
    }
    public void sendAdvanceRequestData() {
        CommunicationManager.getInstance().sendPostRequest(this,
                AppRequestJSONString.getAdvanceSummaryData(),
                CommunicationConstant.API_GET_ADVANCE_PAGE_INIT, true);
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
                EmployItem item = (EmployItem) data.getSerializableExtra(SearchOnbehalfActivity.SELECTED_TOUR_EMP);
                if (item != null) {
                    String[] empname = item.getName().split("\\(");
                    empNameTV.setText(empname[0]);
                    empId = String.valueOf(item.getEmpID());
                    empCodeTV.setText(item.getEmpCode());
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

    private void doSubmitOperation(){

    }

    public void askLocationPermision() {
        this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void validateResponse(ResponseData response) {

        // ((MainActivity) getActivity()).showHideProgress(false);
        Log.d("TAG", "response data " + response.isSuccess());
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

                }
                break;
          /*  case CommunicationConstant.API_GET_DETAILS_ON_EMP_CHANGE:
                String responseData = response.getResponseData();
                Log.d("TAG", "Emp Change response : " + responseData);
                empChangeResponseModel = GetDetailsOnEmpChangeResponseModel.create(responseData);
                if (empChangeResponseModel != null && empChangeResponseModel.getGetDetailsOnEmpChangeResult() != null
                        && empChangeResponseModel.getGetDetailsOnEmpChangeResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                    //  if (screenName!=null && !screenName.equalsIgnoreCase(AppsConstant.PENDING_APPROVAL)) {
                    if (empChangeResponseModel.getGetDetailsOnEmpChangeResult().getShowTravelYN().equalsIgnoreCase(AppsConstant.YES)) {
                        travelFromLl.setVisibility(View.VISIBLE);
                        travelToLl.setVisibility(View.VISIBLE);
                    }
                    if (empChangeResponseModel.getGetDetailsOnEmpChangeResult().getShowReasonYN().equalsIgnoreCase(AppsConstant.YES)) {
                        reasonLl.setVisibility(View.VISIBLE);
                    }
                    sendTourRequestCustomFieldList();
                    //}
                } else {
                    new AlertCustomDialog(getActivity(), empChangeResponseModel.getGetDetailsOnEmpChangeResult().getErrorMessage());
                }
                break;
            case CommunicationConstant.API_GET_TOUR_CUSTOM_FIELD_LIST:
                String resp = response.getResponseData();
                Log.d("TAG", "Custom List response : " + resp);
                tourCustomListResponse = TourCustomListResponse.create(resp);

                if (tourCustomListResponse != null && tourCustomListResponse.getGetTourRequestCustomFieldListResult() != null
                        && tourCustomListResponse.getGetTourRequestCustomFieldListResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)
                        && tourCustomListResponse.getGetTourRequestCustomFieldListResult().getCustomFields() != null
                        && tourCustomListResponse.getGetTourRequestCustomFieldListResult().getCustomFields().size() > 0) {
                    customFieldsModel = tourCustomListResponse.getGetTourRequestCustomFieldListResult().getCustomFields();
                    refreshCustomFields(customFieldsModel);
                    sendAdvanceRequestData();
                }
                break;
            case CommunicationConstant.API_GET_CORPEMP_PARAM:
                String responseData1 = response.getResponseData();
                try {
                    ((RelativeLayout) rootView.findViewById(R.id.searchLayout)).setVisibility(View.GONE);
                    if (responseData1 != null) {
                        empNameTV.setText("");
                        GetCorpEmpParamResultResponse corpEmpParamResultResponse = GetCorpEmpParamResultResponse.create(responseData1);
                        if (corpEmpParamResultResponse != null && corpEmpParamResultResponse.getGetCorpEmpParamResult() != null &&
                                corpEmpParamResultResponse.getGetCorpEmpParamResult().getErrorCode() != null && corpEmpParamResultResponse.getGetCorpEmpParamResult().getErrorCode().equalsIgnoreCase("0")) {

                            if (corpEmpParamResultResponse.getGetCorpEmpParamResult().getCorpEmpParamList() != null &&
                                    corpEmpParamResultResponse.getGetCorpEmpParamResult().getCorpEmpParamList().size() > 0) {
                               *//* if (corpEmpParamResultResponse.getGetCorpEmpParamResult().getCorpEmpParamList().get(0).getParam() != null && corpEmpParamResultResponse.getGetCorpEmpParamResult().getCorpEmpParamList().get(0).getValue() != null) {

                                    if (corpEmpParamResultResponse.getGetCorpEmpParamResult().getCorpEmpParamList().get(0).getParam().equalsIgnoreCase("TourOnBehalfOfYN")
                                            && corpEmpParamResultResponse.getGetCorpEmpParamResult().getCorpEmpParamList().get(0).getValue().equalsIgnoreCase("Y")) {
                                        ((RelativeLayout) rootView.findViewById(R.id.searchLayout)).setVisibility(View.VISIBLE);
                                    }
                                }*//*

                                for (CorpEmpParamListItem item : corpEmpParamResultResponse.getGetCorpEmpParamResult().getCorpEmpParamList()) {
                                    if (item.getParam().equalsIgnoreCase("TourOnBehalfOfYN") && item.getValue().equalsIgnoreCase("Y")) {
                                        ((RelativeLayout) rootView.findViewById(R.id.searchLayout)).setVisibility(View.VISIBLE);

                                    }

                                    if (item.getParam().equalsIgnoreCase("TourSelfInitYN") && item.getValue().equalsIgnoreCase("Y")) {
                                        employItem.setEmpID(Long.parseLong(loginUserModel.getUserModel().getEmpId()));
                                        empId = loginUserModel.getUserModel().getEmpId();
                                        employItem.setName(loginUserModel.getUserModel().getUserName());
                                        employItem.setEmpCode(loginUserModel.getUserModel().getEmpCode());
                                        empNameTV.setText(employItem.getName());

                                    }
                                }
                            }
                            if (empId != null) {
                                setTravelAndReasonData();
                            }
                        } else {

                        }
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.e("Leave", e.getMessage(), e);
                }

                break;
            case CommunicationConstant.API_GET_TOUR_REQUEST_DETAIL:
                String str = response.getResponseData();
                Log.d("TAG", "Tour Response : " + str);
                tourSummaryResponse = TourSummaryResponse.create(str);
                if (tourSummaryResponse != null && tourSummaryResponse.getGetTourRequestDetailResult() != null
                        && tourSummaryResponse.getGetTourRequestDetailResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)
                        && tourSummaryResponse.getGetTourRequestDetailResult().getTourRequestDetail() != null) {
                    status = tourSummaryResponse.getGetTourRequestDetailResult().getTourRequestDetail().getReqStatus();
                    updateUI(tourSummaryResponse.getGetTourRequestDetailResult().getTourRequestDetail());
                    refreshRemarksList(tourSummaryResponse.getGetTourRequestDetailResult().getTourRequestDetail().getRemarkList());
                    uploadFileList = tourSummaryResponse.getGetTourRequestDetailResult().getTourRequestDetail().getAttachments();
                    refreshDocumentList(tourSummaryResponse.getGetTourRequestDetailResult().getTourRequestDetail().getAttachments());
                }

                break;
            case CommunicationConstant.API_SAVE_TOUR_REQUEST:
                String tourResponse = response.getResponseData();
                Log.d("TAG", "wfh response : " + tourResponse);
                tourResponseModel = TourResponseModel.create(tourResponse);
                if (tourResponseModel != null && tourResponseModel.getSaveTourReqResult() != null
                        && tourResponseModel.getSaveTourReqResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                    if (screenName != null && screenName.equalsIgnoreCase(TimeAndAttendanceSummaryFragment.screenName)) {
                        isSubmitClicked = true;
                        CustomDialog.alertOkWithFinishFragment1(context, tourResponseModel.getSaveTourReqResult().getErrorMessage(), mUserActionListener, IAction.TIME_ATTENDANCE_SUMMARY, true);
                    } else {
                        isSubmitClicked = true;
                        CustomDialog.alertOkWithFinishFragment(context, tourResponseModel.getSaveTourReqResult().getErrorMessage(), mUserActionListener, IAction.HOME_VIEW, true);
                    }
                } else {
                    isSubmitClicked = true;
                    new AlertCustomDialog(getActivity(), tourResponseModel.getSaveTourReqResult().getErrorMessage());
                }
                break;
            case CommunicationConstant.API_REJECT_TOUR_REQUEST:
                String respData = response.getResponseData();
                Log.d("TAG", "reject response : " + respData);
                LeaveRejectResponseModel rejectTourResponse = LeaveRejectResponseModel.create(respData);
                if (rejectTourResponse != null && rejectTourResponse.getRejectTourRequestResult() != null
                        && rejectTourResponse.getRejectTourRequestResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                    CustomDialog.alertOkWithFinishFragment1(context, rejectTourResponse.getRejectTourRequestResult().getErrorMessage(), mUserActionListener, IAction.ATTENDANCE, true);
                } else {
                    new AlertCustomDialog(getActivity(), rejectTourResponse.getRejectTourRequestResult().getErrorMessage());
                }
                break;
            case CommunicationConstant.API_APPROVE_TOUR_REQUEST:
                String tourResp = response.getResponseData();
                Log.d("TAG", "Tour approval response : " + tourResp);
                TourResponseModel tourResponseModel = TourResponseModel.create(tourResp);
                if (tourResponseModel != null && tourResponseModel.getApproveTourRequestResult() != null
                        && tourResponseModel.getApproveTourRequestResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                    CustomDialog.alertOkWithFinishFragment1(context, tourResponseModel.getApproveTourRequestResult().getErrorMessage(), mUserActionListener, IAction.ATTENDANCE, true);
                } else {
                    new AlertCustomDialog(getActivity(), tourResponseModel.getApproveTourRequestResult().getErrorMessage());
                }
                break;*/
            default:
                break;
        }
        super.validateResponse(response);
    }


}

