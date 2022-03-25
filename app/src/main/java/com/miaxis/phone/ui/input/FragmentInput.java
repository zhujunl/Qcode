package com.miaxis.phone.ui.input;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import com.miaxis.common.activity.BaseBindingFragment;
import com.miaxis.common.utils.ImageUtils;
import com.miaxis.phone.App;
import com.miaxis.phone.R;
import com.miaxis.phone.databinding.FragmentInputBinding;
import com.miaxis.phone.ui.ctid.FragmentCtid;
import com.miaxis.phone.ui.health_code.FragmentHealthCode;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class FragmentInput extends BaseBindingFragment<FragmentInputBinding> implements EasyPermissions.PermissionCallbacks {

    private String faceString;

    public static FragmentInput newInstance(int tag) {
        return new FragmentInput(tag);
    }

    private int tag;//0网证  其他 健康码

    public FragmentInput(int tag) {
        this.tag = tag;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_input;
    }

    @Override
    protected void initView(@NonNull FragmentInputBinding binding, @Nullable Bundle savedInstanceState) {
        binding.layoutTitle.tvTitle.setText("身份信息输入");
        binding.layoutTitle.ivBack.setOnClickListener(v -> onBackPressed());
        binding.btnQuery.setText(tag == 0 ? "网证查询" : "健康码查询");
        binding.btnQuery.setOnClickListener(v -> query());
        if (tag == 0) {
            binding.ivFace.setVisibility(View.VISIBLE);
            binding.ivFace.setOnClickListener(v -> selectImage());
        } else {
            binding.ivFace.setVisibility(View.INVISIBLE);
            binding.ivFace.setOnClickListener(null);
        }
    }

    private void query() {
        hideInputMethod();
        String name = binding.etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            showErrorToast("请先输入姓名");
            return;
        }
        String idNumber = binding.etIdNumber.getText().toString().trim();
        if (TextUtils.isEmpty(idNumber)) {
            showErrorToast("请先输入身份证号码");
            return;
        }
        if (idNumber.length() != 18) {
            showErrorToast("身份证号码位数不正确");
            return;
        }
        if (binding.ivFace.getVisibility() == View.VISIBLE && TextUtils.isEmpty(faceString)) {
            showErrorToast("请先采集人脸照片");
            return;
        }
        showLoading("请稍候", "正在处理中...");
        Disposable subscribe = Observable.create(emitter -> {
            long wait = 500;
            if (tag == 0) {
                wait+=700;
            }
            SystemClock.sleep(wait);
            emitter.onNext("");
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object -> {
                    dismissLoading();
                    replaceParent(R.id.fl_root,
                            tag == 0? FragmentCtid.newInstance(name,idNumber)
                            :FragmentHealthCode.newInstance(name,idNumber,0));
                }, throwable -> {
                    dismissLoading();
                });

    }

    private Uri cramuri;
    private File file2;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // 调用相机后返回
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        // 得到图片的全路径
                        Uri uri = data.getData();
                        startPhotoZoom(uri);
                    }
                }
                break;
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    startPhotoZoom(cramuri);
                }
                break;
            case 3:
                binding.ivFace.setImageURI(Uri.fromFile(file2));
                String realPathFromUri = ImageUtils.getRealPathFromUri(getContext(), Uri.fromFile(file2));
                //转base64
                faceString = ImageUtils.getImgStr(realPathFromUri);
        }
    }

    //裁剪并压缩图片
    public void startPhotoZoom(Uri uri) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file2 = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".jpg");
        }
        Intent intent = new Intent("com.android.camera.action.CROP"); // 裁剪图片意图
        intent.setDataAndType(uri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);//裁剪框 X 比值
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 400);//裁剪后输出宽度
        intent.putExtra("outputY", 400);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file2));
        intent.putExtra("return-data", false); //是否在Intent中返回数据
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        startActivityForResult(intent, 3);
    }

    private static final int RC_EXTERNAL_STORAGE_PERM = 100;
    private File currentImageFile;

    @AfterPermissionGranted(RC_EXTERNAL_STORAGE_PERM)
    private boolean requiresPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            return true;
        } else {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.tips_camera), RC_EXTERNAL_STORAGE_PERM, perms);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        selectImage();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        new AppSettingsDialog.Builder(this)
                .setTitle(getResources().getString(R.string.tips_limit))
                .setPositiveButton(getResources().getString(R.string.tips_limit_true))
                .setNegativeButton(getResources().getString(R.string.tips_limit_false))
                .setRationale(getResources().getString(R.string.tips_limit_camera))
                .setRequestCode(RC_EXTERNAL_STORAGE_PERM)
                .build()
                .show();
    }

    public void selectImage() {
        boolean requiresPermission = requiresPermission();
        if (!requiresPermission) {
            return;
        }

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            currentImageFile = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".jpg");
        }
        // 启动系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cramuri = FileProvider.getUriForFile(getContext(), "com.miaxis.phone.fileprovider", currentImageFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            cramuri = Uri.fromFile(currentImageFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cramuri);
        startActivityForResult(intent, 2);
    }

}