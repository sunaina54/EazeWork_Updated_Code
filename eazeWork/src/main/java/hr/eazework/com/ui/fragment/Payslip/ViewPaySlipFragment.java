package hr.eazework.com.ui.fragment.Payslip;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import hr.eazework.com.MainActivity;
import hr.eazework.com.R;
import hr.eazework.com.model.ModelManager;
import hr.eazework.com.model.SalaryMonthModel;
import hr.eazework.com.model.SalarySlipDataModel;
import hr.eazework.com.model.SalarySlipItemModel;
import hr.eazework.com.ui.fragment.BaseFragment;
import hr.eazework.com.ui.interfaces.IAction;
import hr.eazework.com.ui.util.BUNDLE_KEYS;
import hr.eazework.com.ui.util.PermissionUtil;
import hr.eazework.com.ui.util.custom.AlertCustomDialog;
import hr.eazework.mframe.communication.FileDownloadListener;
import hr.eazework.mframe.communication.FileDownloader;
import hr.eazework.mframe.communication.ResponseData;
import hr.eazework.selfcare.communication.AppRequestJSONString;
import hr.eazework.selfcare.communication.CommunicationConstant;
import hr.eazework.selfcare.communication.CommunicationManager;

import static hr.eazework.com.ui.util.PermissionUtil.REQ_LOCATION_PERMISSION;


public class ViewPaySlipFragment extends BaseFragment implements FileDownloadListener {

    public static final String TAG = "ViewPaySlipFragment";
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setShowPaySlipHeader(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.setShowPlusMenu(true);
        rootView = inflater.inflate(R.layout.view_pay_slip_layout, container, false);
        rootView.findViewById(R.id.btn_save).setOnClickListener(this);
        SalaryMonthModel monthString = (SalaryMonthModel) getArguments().getSerializable(BUNDLE_KEYS.SELECTED_MONTH);
        refreshUi(monthString.getmMonthName());


        ((MainActivity) getActivity()).setPaySlipHeader(getString(R.string.msg_pay_slip) + " " + monthString.getmMontTitle());
        return rootView;
    }

    public void refreshUi(String monthString) {
        MainActivity.isAnimationLoaded = false;
        showHideProgressView(true);
        CommunicationManager.getInstance().sendPostRequest(this, AppRequestJSONString.getSallarySlipDataData(monthString), CommunicationConstant.API_GET_SALARY_SLIP_DATA, false);
    }

    public void setupView(SalarySlipDataModel salarySlipDataModel) {
        ((TextView) rootView.findViewById(R.id.tv_net_salary)).setText("Net Salary :  " + salarySlipDataModel.getmNetSalary());
        ((TextView) rootView.findViewById(R.id.tv_earning_gross_pay_count)).setText(" " + salarySlipDataModel.getmGrossPay());
        ((TextView) rootView.findViewById(R.id.payable_day_count)).setText("" + salarySlipDataModel.getmPayableDays());
        ((TextView) rootView.findViewById(R.id.tv_deduction_gross_pay_count)).setText(" " + salarySlipDataModel.getmGrossDeduction());
        ((TextView) rootView.findViewById(R.id.lwp_count)).setText(salarySlipDataModel.getmLWP());
        populateView(salarySlipDataModel.getDataItems());
    }

    public void populateView(ArrayList<SalarySlipItemModel> arrayList) {
        LinearLayout earningLayout = (LinearLayout) rootView.findViewById(R.id.ll_earnings_container);
        LinearLayout deductionLayout = (LinearLayout) rootView.findViewById(R.id.ll_deduction_container);
        deductionLayout.removeAllViews();
        earningLayout.removeAllViews();
        for (SalarySlipItemModel item : arrayList) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.payslip_item, null, false);
            ((TextView) view.findViewById(R.id.tv_item_title)).setText(item.getmCorpHeadName());
            ((TextView) view.findViewById(R.id.tv_item_value)).setText(" " + item.getmAmount());
            if (item.ismIsEarning()) {
                earningLayout.addView(view);
            } else {
                deductionLayout.addView(view);
            }
        }
    }

    @Override
    public void validateResponse(ResponseData response) {
        MainActivity.isAnimationLoaded = true;
        showHideProgressView(false);
        if (response.isSuccess() && !isSessionValid(response.getResponseData())) {
            mUserActionListener.performUserAction(IAction.LOGIN_VIEW, null, null);
            return;
        }
        switch (response.getRequestData().getReqApiId()) {
            case CommunicationConstant.API_GET_SALARY_SLIP_DATA:
                try {
                    JSONObject json = new JSONObject(response.getResponseData());
                    JSONObject mainJSONObject = json
                            .optJSONObject("SalarySlipDataResult");
                    if (mainJSONObject.optInt("ErrorCode", -1) == 0) {
                        SalarySlipDataModel salarySlipDataModel = new SalarySlipDataModel(mainJSONObject);
                        ModelManager.getInstance().setSalarySlipDataModel(salarySlipDataModel);
                        setupView(salarySlipDataModel);
                    } else {
                        String errorMessage = mainJSONObject.optString("ErrorMessage", "Error");
                        new AlertCustomDialog(getActivity(), errorMessage);
                    }

                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                    Crashlytics.logException(e);
                }
                break;

            default:
                break;
        }
        super.validateResponse(response);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
               /* if (Build.VERSION.SDK_INT >= 23)
                {
                    if (!checkPermission()) {
                        requestPermission();
                        return;
                    }
                }*/

               if(PermissionUtil.checkExternalStoragePermission(getActivity())) {

                   showHideProgressView(true);
                   SalarySlipDataModel salarySlipDataModel = ModelManager.getInstance().getSalarySlipDataModel();
                   if (salarySlipDataModel != null) {
                       String salarySlipPath = salarySlipDataModel.getmSalarySlipPath();
                       String array[] = (CommunicationConstant.getMobileCareURl() + salarySlipPath).split("/");
                       new FileDownloader(getContext(), this).execute(CommunicationConstant.getMobileCareURl() + salarySlipPath, "EazeWorkPaySlip" + array[array.length - 1]);
                   }
               }else{
                  askLocationPermision();
               }

                break;

            default:
                break;
        }
        super.onClick(v);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
  /*  @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }*/
    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getActivity(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    public void askLocationPermision() {
        this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onFileDownloadComplete(boolean status, final Uri location) {

        showHideProgressView(false);
        if (status) {
            new AlertCustomDialog(getActivity(), "Successfully downloaded.", "Close", "Open", new AlertCustomDialog.AlertClickListener() {

                @Override
                public void onPositiveBtnListener() {
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(location, "application/pdf");
                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    Intent intent = Intent.createChooser(target, "Open File");
                    try {
                        getActivity().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // Instruct the user to install a PDF reader here, or
                        // something
                    } catch (Exception e){
                        new AlertCustomDialog(getActivity(), "We are not able to open your file. You can find your downloaded file in your internal memory under EazeWork folder");
                    }
                }

                @Override
                public void onNegativeBtnListener() {

                }
            });
        } else {
            new AlertCustomDialog(getActivity(), "Failed please try again.");
        }

    }
}
