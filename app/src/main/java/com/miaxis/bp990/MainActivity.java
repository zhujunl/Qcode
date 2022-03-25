package com.miaxis.bp990;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Base64;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.king.zxing.CameraScan;
import com.miaxis.bp990.App.App;
import com.miaxis.bp990.base.BaseActivity;
import com.miaxis.bp990.base.BaseViewModelFragment;
import com.miaxis.bp990.base.OnFragmentInteractionListener;
import com.miaxis.bp990.been.Code;
import com.miaxis.bp990.data.entity.Person;
import com.miaxis.bp990.data.entity.PersonManager;
import com.miaxis.bp990.databinding.ActivityMainBinding;
import com.miaxis.bp990.manager.ToastManager;
import com.miaxis.bp990.view.face.FaceFragment;
import com.miaxis.bp990.view.home.HomeFragmet;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements OnFragmentInteractionListener, EasyPermissions.PermissionCallbacks{

    private MaterialDialog waitDialog;
    private MaterialDialog resultDialog;
    private MaterialDialog quitDialog;
    private String root;


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
        initDialog();
        binding.sacn.setOnClickListener(v->{
            checkCameraPermissions(QRCodeActivity.class,"扫码");
        });
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
    public void checkCamera(Class<?> cls, String title) {
        checkCameraPermissions(cls,title);
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
                        Disposable sub=Observable.create((ObservableOnSubscribe<Person>) p->{
                            Person penson=PersonManager.getInstance().FindPersonByCard(code.cardNumber);
                            penson.setName(code.name);
                            SystemClock.sleep(1000);
                            p.onNext(penson);
                        } ).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(person -> {
                                    dismissWaitDialog();
                                    if (person!=null) {
                                        replaceFragment(FaceFragment.getInstance(person,code.codeStatus));
                                    }else {
                                        ToastManager.toast("暂无",ToastManager.ERROR);
                                    }
                                }, throwable -> {
                                    ToastManager.toast("暂无",ToastManager.ERROR);
                                });
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
    private void checkCameraPermissions(Class<?> cls,String title){
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {//有权限
            startScan(cls,title);
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
    private void startScan(Class<?> cls,String title){
//        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(this,R.anim.in,R.anim.out);
        Intent intent = new Intent(this, cls);
        intent.putExtra(KEY_TITLE,title);
        intent.putExtra(KEY_IS_CONTINUOUS,false);
        ActivityCompat.startActivityForResult(this,intent,REQUEST_CODE_SCAN,null);
//        startActivityForResult(intent,REQUEST_CODE_SCAN);
    }

}