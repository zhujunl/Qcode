package com.miaxis.bp990;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.king.zxing.CameraScan;
import com.miaxis.bp990.App.App;
import com.miaxis.bp990.base.BaseActivity;
import com.miaxis.bp990.base.BaseViewModelFragment;
import com.miaxis.bp990.base.OnFragmentInteractionListener;
import com.miaxis.bp990.been.Code;
import com.miaxis.bp990.bridge.Status;
import com.miaxis.bp990.databinding.ActivityMainBinding;
import com.miaxis.bp990.manager.ToastManager;
import com.miaxis.bp990.view.face.FaceFragment;
import com.miaxis.bp990.view.home.HomeFragmet;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements OnFragmentInteractionListener, EasyPermissions.PermissionCallbacks{

    private MaterialDialog waitDialog;
    private MaterialDialog resultDialog;
    private MaterialDialog quitDialog;
    private String root;
    private MainViewModel viewModel;
    private int codeType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().initApplication();
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        replaceFragment(HomeFragmet.getInstance());
    }

    @Override
    protected void initView() {
        viewModel=new ViewModelProvider(this,getDefaultViewModelProviderFactory()).get(MainViewModel.class);
        viewModel.result.observe(this, result -> {
            dismissWaitDialog();

            if(result.getCode()== Status.SUCCESS){
                if(result.getPerson()==null){
                    ToastManager.toast("查无此人",ToastManager.ERROR);
                }else {
                    replaceFragment(FaceFragment.getInstance(result.getPerson()));
                }
            }else {
                ToastManager.toast(result.getMessage(),ToastManager.ERROR);
            }
        });
        initDialog();
    }

    @Override
    public void onBackPressed() {
        Fragment visibleFragment = getVisibleFragment();
        if (visibleFragment != null) {
            BaseViewModelFragment fragment = (BaseViewModelFragment) visibleFragment;
            fragment.onBackPressed();
        }
    }

    @Override
    public void setRoot(Fragment fragment) {
        root = fragment.getClass().getName();
    }

    @Override
    public void backToRoot() {
        getSupportFragmentManager().popBackStack(root, 1);
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideInputMethod();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fg, fragment)
                        .addToBackStack(fragment.getClass().getName())
                        .commit();
            }
        });
    }

    @Override
    public void backToStack(Class<? extends Fragment> fragment) {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount > 1) {
            if (fragment != null) {
                getSupportFragmentManager()
                        .popBackStackImmediate(fragment.getName(), 0);
            } else {
                getSupportFragmentManager().popBackStackImmediate(null, 0);
            }
        } else {
            exitApp();
        }
    }


    @Override
    public void showWaitDialog(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                waitDialog.getContentView().setText(message);
                waitDialog.show();
            }
        });
    }

    @Override
    public void dismissWaitDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (waitDialog.isShowing()) {
                    waitDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void showResultDialog(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultDialog.getContentView().setText(message);
                resultDialog.show();
            }
        });
    }

    @Override
    public void dismissResultDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (resultDialog.isShowing()) {
                    resultDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void exitApp() { quitDialog.show(); }

    @Override
    public void checkCamera(Class<?> cls, String title,int codeType) {
        checkCameraPermissions(cls,title,codeType);
    }

    private void initDialog() {
        waitDialog = new MaterialDialog.Builder(this)
                .progress(true, 100)
                .content("请稍后")
                .cancelable(false)
                .autoDismiss(false)
                .build();
        quitDialog = new MaterialDialog.Builder(this)
                .title("确认退出?")
                .positiveText("确认")
                .onPositive((dialog, which) -> {
                    finish();
                    System.exit(0);
                })
                .negativeText("取消")
                .build();
        resultDialog = new MaterialDialog.Builder(this)
                .content("")
                .positiveText("确认")
                .build();
    }



    public static final String KEY_TITLE = "key_title";
    public static final String KEY_IS_QR_CODE = "key_code";
    public static final String KEY_IS_CONTINUOUS = "key_continuous_scan";
    private boolean isContinuousScan;
    public static final int REQUEST_CODE_SCAN = 0X01;
    public static final int REQUEST_CODE_PHOTO = 0X02;
    public static final int RC_CAMERA = 0X01;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data!=null){
            switch (requestCode){
                case REQUEST_CODE_SCAN:
                    try {
                        showWaitDialog("正在查询，请稍候..");
                        String result = CameraScan.parseScanResult(data);
                        byte[] bytes= Base64.decode(result,Base64.DEFAULT);
                        Code code = new Gson().fromJson(new String(bytes),Code.class);
                        if(code.codeType!=this.codeType){
                            dismissWaitDialog();
                            ToastManager.toast("扫描二维码类型不对",ToastManager.ERROR);
                        }else {
                            viewModel.searchPerson(code);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_CODE_PHOTO:
//                    parsePhoto(data);
                    break;
            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    /**
     * 检测拍摄权限
     */
    @AfterPermissionGranted(RC_CAMERA)
    private void checkCameraPermissions(Class<?> cls,String title,int codeType){
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {//有权限
            startScan(cls,title,codeType);
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this,"App扫码需要用到拍摄权限",
                    RC_CAMERA, perms);
        }
    }

    /**
     * 扫码
     * @param cls
     * @param title
     */
    private void startScan(Class<?> cls,String title,int codeType){
//        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(this,R.anim.in,R.anim.out);
        Intent intent = new Intent(this, cls);
        intent.putExtra(KEY_TITLE,title);
        intent.putExtra(KEY_IS_CONTINUOUS,false);
        this.codeType=codeType;
        ActivityCompat.startActivityForResult(this,intent,REQUEST_CODE_SCAN,null);
//        startActivityForResult(intent,REQUEST_CODE_SCAN);
    }

}