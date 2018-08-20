package hr.eazework.com.ui.fragment.Team;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import hr.eazework.com.FileUtils;
import hr.eazework.com.R;
import hr.eazework.com.model.EmployeeDetailModel;
import hr.eazework.com.model.FileInfo;
import hr.eazework.com.model.LoginUserModel;
import hr.eazework.com.model.ModelManager;
import hr.eazework.com.model.UploadProfilePicModel;
import hr.eazework.com.model.UploadProfilePicResponseModel;
import hr.eazework.com.model.UserModel;
import hr.eazework.com.ui.customview.CustomBuilder;
import hr.eazework.com.ui.customview.CustomDialog;
import hr.eazework.com.ui.fragment.BaseFragment;
import hr.eazework.com.ui.util.AppsConstant;
import hr.eazework.com.ui.util.ImageUtil;
import hr.eazework.com.ui.util.PermissionUtil;
import hr.eazework.com.ui.util.Utility;
import hr.eazework.com.ui.util.custom.AlertCustomDialog;
import hr.eazework.mframe.communication.ResponseData;
import hr.eazework.selfcare.communication.AppRequestJSONString;
import hr.eazework.selfcare.communication.CommunicationConstant;
import hr.eazework.selfcare.communication.CommunicationManager;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Manjunath on 07-04-2017.
 */

public class UserProfile extends BaseFragment {
    public static String TAG = "UserProfile";
    public static String screenName="UserProfile";
    private Bitmap bitmap = null;
    private String purpose = "";
    private RelativeLayout editProfilePicLayout;
    private Context context;
    private static int UPLOAD_DOC_REQUEST = 1;
    private ImageView img_user_img;
    private static final int PERMISSION_REQUEST_CODE = 3;
    private View progressbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void updataProfileData(EmployeeDetailModel model,Context context, View rootView, String name, String department, String Designation, String empID, String profilePhoto) {

        Utility.setCorpBackground(context, rootView);

        if(name!=null && !name.equalsIgnoreCase("")) {
            ((TextView) rootView.findViewById(R.id.tv_profile_name)).setText(name);
        }else {
            ((TextView) rootView.findViewById(R.id.tv_profile_name)).setVisibility(View.GONE);
        }
        ((TextView) rootView.findViewById(R.id.tv_department)).setVisibility(View.GONE);

        if(model.getmDeptNameYN().equalsIgnoreCase("Y")){
            ((TextView) rootView.findViewById(R.id.tv_department)).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.tv_department)).setText(context.getString(R.string.msg_department) + " " + department);
        }

        ((TextView) rootView.findViewById(R.id.tv_role)).setVisibility(View.GONE);
        if(model.getmDesignationYN().equalsIgnoreCase("Y"))
        ((TextView) rootView.findViewById(R.id.tv_role)).setText(context.getString(R.string.msg_role) + " " + Designation);

        if(empID!=null && !empID.equalsIgnoreCase(""))
            ((TextView) rootView.findViewById(R.id.tv_employee)).setText(context.getString(R.string.msg_employee_id) + " " + empID);
        else
            ((TextView) rootView.findViewById(R.id.tv_employee)).setVisibility(View.GONE);

        Picasso.with(context).load(CommunicationConstant.getMobileCareURl() + profilePhoto)
                .fit()
                .into((ImageView) rootView.findViewById(R.id.img_user_img));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.team_member_profile, container, false);
        context=getContext();
        progressbar = (LinearLayout) rootView.findViewById(R.id.ll_progress_container);
        progressbar.bringToFront();
        img_user_img= (ImageView) rootView.findViewById(R.id.img_user_img);
        editProfilePicLayout= (RelativeLayout) rootView.findViewById(R.id.editProfilePicLayout);
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
                                        PermissionUtil.askAllPermissionCamera(UserProfile.this);
                                    }
                                    if (PermissionUtil.checkCameraPermission(getContext()) && PermissionUtil.checkStoragePermission(getContext())) {
                                        Utility.openCamera(getActivity(), UserProfile.this, AppsConstant.FRONT_CAMREA_OPEN, "ForPhoto", screenName);

                                    }
                                } else if (selectedObject.toString().equalsIgnoreCase("Gallery")) {
                                    if(PermissionUtil.checkExternalStoragePermission(getActivity())) {
                                        galleryIntent();
                                        customBuilder.dismiss();
                                    }else{
                                        askLocationPermision();
                                    }
                                }
                            }
                        }
                );
                customBuilder.show();

            }
        });

        requestAPI();
        Utility.setCorpBackground(getContext(), rootView);
        return rootView;

    }
    public void askLocationPermision() {
        this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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
    private void requestAPI() {

      //  showHideProgressView(true);
        Utility.showHidePregress(progressbar, true);
        LoginUserModel loginUserModel = ModelManager.getInstance().getLoginUserModel();
        if (loginUserModel != null) {
            UserModel userModel = loginUserModel.getUserModel();
            if (userModel != null) {
                CommunicationManager.getInstance().sendPostRequest(this,
                        AppRequestJSONString.getEmployeeDetails(userModel.getEmpId()),
                        CommunicationConstant.API_GET_EMPLOYEE_DETAIL, true);
            }
        }

    }


    @Override
    public void validateResponse(ResponseData response) {
        super.validateResponse(response);
        Utility.showHidePregress(progressbar, false);
        switch (response.getRequestData().getReqApiId()) {
            case CommunicationConstant.API_GET_EMPLOYEE_DETAIL:
                JSONObject json;
                try {
                    json = new JSONObject(response.getResponseData());
                    JSONObject mainJSONObject = json.optJSONObject("GetEmployeeDetailsResult");
                    if (mainJSONObject.optInt("ErrorCode", -1) != 0) {
                        String errorMessage = mainJSONObject.optString("ErrorMessage", "Failed");
                        new AlertCustomDialog(getActivity(), errorMessage);
                    } else {
                        String getEmployeeDetailsResult = json.optJSONObject("GetEmployeeDetailsResult").toString();
                        JSONObject array = new JSONObject(getEmployeeDetailsResult).optJSONObject("employeeDetails");
                        populateViews(array);
                    }

                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    Log.e(TAG, e.getMessage(), e);
                }
                break;
            case CommunicationConstant.API_UPLOAD_PROFILE_PIC:
                String responseStr = response.getResponseData();
                Log.d("TAG", "profile pic response : " + responseStr);
                UploadProfilePicResponseModel uploadProfilePicResponseModel = UploadProfilePicResponseModel.create(responseStr);
                if (uploadProfilePicResponseModel != null && uploadProfilePicResponseModel.getUploadProfilePicResult() != null
                        && uploadProfilePicResponseModel.getUploadProfilePicResult().getErrorCode().equalsIgnoreCase(AppsConstant.SUCCESS)) {
                    //true
                } else {
                    new AlertCustomDialog(getActivity(), uploadProfilePicResponseModel.getUploadProfilePicResult().getErrorMessage());
                }
                break;
            default:
                break;
        }
    }


    private void populateViews(JSONObject array) {
        ModelManager.getInstance().setEmployeeDetailModel(array.toString());
        Log.d("TAG","Profile Response : "+array.toString());
        EmployeeDetailModel model = ModelManager.getInstance().getEmployeeDetailModel();
        if (model != null) {
            updataProfileData(model,getContext(), rootView, array.optString("FirstName", "") + " " + array.optString("MiddleName","") + " " + array.optString("LastName", ""),
                    array.optString("DeptName", ""), array.optString("Designation", ""),
                    array.optString("EmpCode", ""),
                    array.optString("EmpImageUrl", ""));

            LinearLayout empPersonalFieldLayout = (LinearLayout) rootView.findViewById(R.id.ll_earnings_container);
            LinearLayout empOfficialFieldLayout = (LinearLayout) rootView.findViewById(R.id.ll_deduction_container);
            empOfficialFieldLayout.removeAllViews();
            empPersonalFieldLayout.removeAllViews();

            Activity activity = getActivity();
            if(model.getmEmail()!=null && !model.getmEmail().equalsIgnoreCase(""))
            Utility.addElementToView(activity,empPersonalFieldLayout,"Email",model.getmEmail());
            if(model.getmDateOfBirth()!=null && !model.getmDateOfBirth().equalsIgnoreCase(""))
            Utility.addElementToView(activity,empPersonalFieldLayout,"Date Of Birth",model.getmDateOfBirth());

            if(model.getmDateOfJoining()!=null && !model.getmDateOfJoining().equalsIgnoreCase(""))
            Utility.addElementToView(activity,empPersonalFieldLayout,"Date Of Joining",model.getmDateOfJoining());

            if(model.getmMaritalStatusYN().equalsIgnoreCase("Y"))
            Utility.addElementToView(activity,empPersonalFieldLayout,"Marital Status",model.getmMaritalStatusDesc());

            if(model.getmCompanyNameYN().equalsIgnoreCase("y")){
                Utility.addElementToView(activity,empOfficialFieldLayout,"Company Name",model.getmCompanyName());
            }

            if(model.getmMangerName()!=null && !model.getmMangerName().equalsIgnoreCase("")){
                Utility.addElementToView(activity, empOfficialFieldLayout, "Manager", model.getmMangerName());
            }
            if(model.getmFnMangerNameYN().equalsIgnoreCase("Y")){
                Utility.addElementToView(activity, empOfficialFieldLayout, "Functional Manager", model.getmFunctionalManager());
            }
            if(model.getmOfficeLocation()!=null && !model.getmOfficeLocation().equalsIgnoreCase("")) {
                Utility.addElementToView(activity, empOfficialFieldLayout, "Office Location", model.getmOfficeLocation());
            }
            if (model.getmWorkLocationYN().equalsIgnoreCase("Y")) {
                Utility.addElementToView(activity, empOfficialFieldLayout, "Work Location", model.getmWorkLocation());
            }

            if(model.getmDeptNameYN().equalsIgnoreCase("Y")){
                Utility.addElementToView(activity,empOfficialFieldLayout,"Department",model.getmDeptName());
            }
            if(model.getmSubDepartmentYN().equalsIgnoreCase("y")) {
                Utility.addElementToView(activity,empOfficialFieldLayout,"Sub-Department",model.getmSubDepartment());
            }
                if(model.getmDivNameYN().equalsIgnoreCase("y")) {
                Utility.addElementToView(activity,empOfficialFieldLayout,"Division",model.getmDivName());
            }
            if(model.getmSubDivisionNameYN().equalsIgnoreCase("y")) {
                Utility.addElementToView(activity,empOfficialFieldLayout,"Sub-Division",model.getmSubDivisionName());
            }
            Utility.addElementToView(activity, empOfficialFieldLayout, "Employment Status", model.getmEmploymentStatusDesc());
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPLOAD_DOC_REQUEST && resultCode == RESULT_OK) {
            final Uri uri = data.getData();
            String encodeFileToBase64Binary = null;
            if (data != null) {
                if (Utility.calculateBitmapSizeForProfilePic(data.getData(),context) > Utility.maxLimit1) {
                    CustomDialog.alertWithOk(context, Utility.sizeMsg1);
                    return;
                }
                String path = data.getStringExtra("path");
                System.out.print(path);
                Uri uploadedFilePath = data.getData();
                String filename = Utility.getFileName(uploadedFilePath, context);
                filename = filename.toLowerCase();
                String fileDesc = Utility.getFileName(uploadedFilePath, context);
                String[] extList = filename.split("\\.");
                System.out.print(extList[1].toString());
                String extension = "." + extList[extList.length - 1];
                //encodeFileToBase64Binary = Utility.fileToBase64Conversion(data.getData(), context);
               // Log.d("TAG", "RAR Base 64 :" + encodeFileToBase64Binary);


                if (filename.contains(".jpg") || filename.contains(".jpeg")
                        || filename.contains(".JPEG") || filename.contains(".JPG")
                        || filename.contains(".png") || filename.contains(".PNG")) {

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

                /*    if (Utility.calcBase64SizeInKBytes(encodeFileToBase64Binary) > Utility.maxLimit) {
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
}
