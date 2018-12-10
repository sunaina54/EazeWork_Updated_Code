package hr.eazework.com.ui.fragment.Camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import hr.eazework.com.R;
import hr.eazework.com.ui.util.AppsConstant;
import hr.eazework.com.ui.util.PermissionUtil;
import hr.eazework.com.ui.util.Preferences;
import hr.eazework.com.ui.util.Utility;
import hr.eazework.com.ui.util.custom.AlertCustomDialog;
import id.zelory.compressor.Compressor;

import static hr.eazework.com.ui.util.PermissionUtil.REQ_LOCATION_PERMISSION;

/**
 * Created by allsmartlt218 on 13-12-2016.
 */

public class RetakeFragment extends Fragment {

    Bitmap bmp;
    TextView confirm, cancel;
    ImageView imageView;
    String imagePurpose = "";
    String imagePath = "";
    String fieldCode = "";
    String screenName = "";
    private boolean waitingForActivity = false;
    private static Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePath = getArguments().getString("image_taken", "");
        imagePurpose = getArguments().getString("image_purpose", "");
        screenName = getArguments().getString("screen", "");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.retake_fragment, container, false);
        confirm = (TextView) view.findViewById(R.id.btConfirm);
        cancel = (TextView) view.findViewById(R.id.btRetake);
        imageView = (ImageView) view.findViewById(R.id.ivRetake);
        imageView.setImageBitmap(null);
        context = getContext();
        Log.d("TAG", "Image Absolute path 1 : " + imagePath);


        final File path = new File(imagePath);
        /*try {
            path.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        Log.d("File Path", path + "");
        /*if (!imagePurpose.equals(null) && imagePurpose.equals("ForPhoto") &&
                path != null && path.exists()) {
            try {
                bmp = rotateBmpFront(path);
            } catch (IllegalArgumentException e) {
                Crashlytics.logException(e);
                waitingForActivity = true;
                Log.d("Front-Image 1","after bitmap issue"+waitingForActivity+"");
            }
        } else if (path != null && path.exists()) {
            try {
                bmp = rotateBmpBack(path);
            } catch (IllegalArgumentException e) {
                Crashlytics.logException(e);
                waitingForActivity = true;
                Log.d("Front-Image 2","after bitmap issue"+waitingForActivity+"");
            }
        } else if (path == null   || (path != null && !path.exists())) {
            waitingForActivity = true;
            Log.d("Front-Image 3","after bitmap issue "+waitingForActivity+"");
        }
*/
        try {


            if (!imagePurpose.equals(null) && imagePurpose.equals("ForPhoto") &&
                    path != null && path.canRead()) {
                try {
                    bmp = rotateBmpFront(path);
                } catch (IllegalArgumentException e) {
                    Crashlytics.logException(e);
                    waitingForActivity = true;
                    Log.d("Front-Image 1", "after bitmap issue" + waitingForActivity + "");
                }
            } else if (path != null && path.canRead()) {
                try {
                    bmp = rotateBmpBack(path);
                } catch (IllegalArgumentException e) {
                    Crashlytics.logException(e);
                    waitingForActivity = true;
                    Log.d("Front-Image 2", "after bitmap issue" + waitingForActivity + "");
                }
            } else if (path == null || (path != null && !path.canRead())) {
                try {
                    waitingForActivity = true;
                    Log.d("Front-Image 3", "after bitmap issue " + waitingForActivity + "");
                } catch (Exception e) {

                    Log.d("Pic issue:", e.toString());
                }

            }
        }catch(Exception e){

                Log.d("Pic issue:", e.toString());
        }


            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (screenName != null && !screenName.equalsIgnoreCase("")) {
                        confirmActionCamera();
                    } else {
                        confirmAction();
                    }

                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
             /* File  mFile = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM), "pic.jpg");
              if(mFile.exists()){
                  mFile.delete();
              }*/
                    imageView.setImageBitmap(null);
                    imageView.invalidate();
                    goBackAction();
                }
            });

            if (bmp != null) {
                imageView.setImageBitmap(bmp);
                imageView.invalidate();
            }

            return view;
        }

    public void confirmActionCamera() {
        if (bmp != null && imagePurpose != null) {
            String sImagePath = "";
            sImagePath = persistImage(bmp, imagePurpose);
            Intent i = new Intent();
            i.putExtra("response", sImagePath);
            i.putExtra("image_purpose", imagePurpose);
            if (!TextUtils.isEmpty(fieldCode)) {
                i.putExtra("FieldCode", fieldCode);
            }

            getActivity().setResult(Activity.RESULT_OK, i);
            getActivity().finish();

        }
    }

    public void confirmAction() {
        if (Utility.isLocationEnabled(getContext())) {
            if (Utility.isNetworkAvailable(getContext())) {
                if (PermissionUtil.checkLocationPermission(getContext())) {
                    if (bmp != null && imagePurpose != null) {
                        String sImagePath = "";
                        sImagePath = persistImage(bmp, imagePurpose);
                        Intent i = new Intent();
                        i.putExtra("response", sImagePath);
                        i.putExtra("image_purpose", imagePurpose);
                        if (!TextUtils.isEmpty(fieldCode)) {
                            i.putExtra("FieldCode", fieldCode);
                        }

                        getActivity().setResult(Activity.RESULT_OK, i);
                        getActivity().finish();
                    }
                } else {
                    askLocationPermision();
                }
            } else {
                new AlertCustomDialog(getContext(), getString(R.string.msg_internet_connection));
            }
        } else {
            Utility.requestToEnableGPS(getContext(), new Preferences(getContext()));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (waitingForActivity) {
            waitingForActivity = false;
            Utility.displayMessage(context, "Something went wrong please take again");
            goBackAction();
        }
    }

    public void goBackAction() {
        if (CameraActivity.forBelowLollipop) {
            //|| (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)

            CameraActivity activity = (CameraActivity) getActivity();
            if (activity != null) {
                activity.openDefualtCamera();
            }

        } else {
           /* Handler uiHandler = new Handler();
            uiHandler.post(new Runnable()
            {
                @Override
                public void run()
                {*/
            FragmentManager fragmentManager = getFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.flCapture);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(currentFragment);
            fragmentTransaction.commitAllowingStateLoss();
            fragmentManager.popBackStack();
             /*   }
            });*/
            //fragmentManager.popBackStackImmediate();
            /*try {
                fragmentTransaction.remove(currentFragment).commitNow();
            } catch( IllegalStateException e ) {
                fragmentTransaction.remove(currentFragment).commitAllowingStateLoss();
            }*/
//            fragmentManager.executePendingTransactions();
            //   fragmentTransaction.commitNow();
           /* try {
                //fragmentTransaction.commitAllowingStateLoss();
                fragmentTransaction.commitAllowingStateLoss();

            }catch (IllegalStateException e){
                Log.d("Exception Camera Selfie",e.toString());
            }*/
          /*  fragmentManager.executePendingTransactions();
            fragmentTransaction.remove(currentFragment).commitNow();
            fragmentTransaction.commitAllowingStateLoss();
            fragmentManager.popBackStack();*/

            //Old code
            //fragmentTransaction.commitNow();
//            fragmentManager.executePendingTransactions();
            // fragmentTransaction.commitNow();

            //  fragmentManager.popBackStack();


      /* fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
           @Override
           public void onBackStackChanged() {
               fragmentManager.beginTransaction()
                       .replace(R.id.container, new RetakeFragment()).commitNow();
           }
       });*/


        }
    }

    private Bitmap rotateBmpBack(File path) {
        Matrix m = new Matrix();
        m.postRotate(90);
        if (screenName != null && !screenName.equalsIgnoreCase("")) {
            try {

                Bitmap bitmap = BitmapFactory.decodeFile(path.getAbsolutePath());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 15, out);
                byte[] byteArray = out.toByteArray();
                Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                bmp = Bitmap.createBitmap(compressedBitmap, 0, 0, compressedBitmap.getWidth(), compressedBitmap.getHeight(), m, true);

            } catch (Exception e) {
                Log.d("Camera Error", e.toString());
                System.gc();
                Bitmap bitmap = BitmapFactory.decodeFile(path.getAbsolutePath());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 15, out);
                byte[] byteArray = out.toByteArray();
                Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                bmp = Bitmap.createBitmap(compressedBitmap, 0, 0, compressedBitmap.getWidth(), compressedBitmap.getHeight(), m, true);

            }

        } else {
            Bitmap bitmap = new Compressor.Builder(getContext()).setQuality(50)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG).setMaxWidth(240)
                    .setMaxHeight(320).build().compressToBitmap(path);
            bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        }

        return bmp;
    }

    private String persistImage(Bitmap bitmap, String name) {
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM), name + ".jpg");
        OutputStream os = null;
        try {
            os = new FileOutputStream(mediaStorageDir);
            if (screenName != null && !screenName.equalsIgnoreCase("")) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, AppsConstant.IMAGE_QUALITY, os);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, os);
            }
            os.flush();
            os.close();
        } catch (Exception e) {
            Crashlytics.log(1, getClass().getName(), "Error in Writing image file to local device");
            Crashlytics.logException(e);
        } finally {
            return mediaStorageDir.getAbsolutePath();
        }
    }

    public Bitmap rotateBmpFront(File path) {
        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        if (screenName != null && !screenName.equalsIgnoreCase("")) {
            Bitmap bitmap = new Compressor.Builder(getContext()).setQuality(AppsConstant.IMAGE_QUALITY)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG).setMaxWidth(480)
                    .setMaxHeight(640).build().compressToBitmap(path);
            bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            //waitingForActivity=true;
            Log.d("Front-Image 4", "after bitmap issue" + waitingForActivity + "");
        } else {
            Bitmap bitmap = new Compressor.Builder(getContext()).setQuality(60)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG).setMaxWidth(480)
                    .setMaxHeight(640).build().compressToBitmap(path);
            bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            Log.d("Front-Image 5", "after bitmap issue" + waitingForActivity + "");
        }
        return bmp;
    }

    public void askLocationPermision() {
        this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOCATION_PERMISSION);
    }

}
