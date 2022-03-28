package com.miaxis.bp990.view.face;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miaxis.bp990.BuildConfig;
import com.miaxis.bp990.R;
import com.miaxis.bp990.RoundBorderView;
import com.miaxis.bp990.RoundFrameLayout;
import com.miaxis.bp990.base.BaseViewModelFragment;
import com.miaxis.bp990.base.OnFragmentInteractionListener;
import com.miaxis.bp990.been.MxPerson;
import com.miaxis.bp990.data.entity.Person;
import com.miaxis.bp990.databinding.FragmentFaceBinding;
import com.miaxis.bp990.event.VerifyEvent;
import com.miaxis.bp990.manager.CameraManager;
import com.miaxis.bp990.util.FileUtil;
import com.miaxis.bp990.util.QRCodeUtil;
import com.miaxis.bp990.view.finger.FingerFragment;
import com.miaxis.bp990.view.home.HomeFragmet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.charset.StandardCharsets;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

/**
 * @author ZJL
 * @date 2022/3/24 17:48
 * @des
 * @updateAuthor
 * @updateDes
 */
public class FaceFragment extends BaseViewModelFragment<FragmentFaceBinding, FaceViewModel> {

    private static FaceFragment instance;
    private OnFragmentInteractionListener mListener;
    private RoundFrameLayout roundFrameLayout;
    private RoundBorderView roundBorderView;
    private final String TAG="FaceFragment";
    private Person person;
    private Bitmap facebit;
    private int status=-1;

    public static FaceFragment getInstance(){
        return new FaceFragment();
    }

    public static FaceFragment getInstance(Person person){
        if (instance==null){
            instance=new FaceFragment();
        }
        instance.setPerson(person);
        return instance;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener){
            mListener=(OnFragmentInteractionListener) context;
        }else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_face;
    }

    @Override
    protected FaceViewModel initViewModel() {
        return new ViewModelProvider(getActivity(),getViewModelProviderFactory()).get(FaceViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.bp990.BR.viewmodel;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        if (!TextUtils.isEmpty(person.getFingerprint0())&&!TextUtils.isEmpty(person.getFingerprint1())){
            binding.tvSwitch.setVisibility(View.VISIBLE);
        }
        viewModel.personFive.setValue(person);
        facebit=FileUtil.loadBitmap(person.getFacepath());
        viewModel.verifyFailedFlag.observe(this, aBoolean -> {
            if(aBoolean){
                viewModel.stopFaceVerify();
                showResult();
            }else {
                binding.tvHint.setText("人脸核验未通过");
            }
        });
        viewModel.personFive.observe(this, person -> {
            viewModel.startFaceVerify(facebit);
        });
        binding.title.setText(person.getName());
        if(BuildConfig.DEBUG)binding.ivHeader.setImageBitmap(facebit);
        binding.rtvCamera.getViewTreeObserver().addOnGlobalLayoutListener(globalListener);
        binding.tvSwitch.setOnClickListener(v->{
            String path=viewModel.path.getValue();
            Bitmap bit=FileUtil.loadBitmap(path);
            binding.ivHeader.setImageBitmap(bit);
            mListener.replaceFragment(FingerFragment.getInstance(viewModel.personFive.getValue()));
        });

        binding.ivBack.setOnClickListener(v->onBackPressed());
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(HomeFragmet.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.stopFaceVerify();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void setPerson(Person person){
        this.person=person;
    }

    private void setStatus(int status){
        this.status=status;
    }

    private ViewTreeObserver.OnGlobalLayoutListener globalListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            binding.rtvCamera.getViewTreeObserver().removeOnGlobalLayoutListener(globalListener);
            ViewGroup.LayoutParams layoutParams = binding.rtvCamera.getLayoutParams();
            layoutParams.width = binding.flCamera.getWidth();
            layoutParams.height = binding.flCamera.getHeight();
            binding.rtvCamera.setLayoutParams(layoutParams);
            binding.rtvCamera.turnRound();
            CameraManager.getInstance().resetRetryTime();
            CameraManager.getInstance().openFrontCamera(binding.rtvCamera, cameraListener);
        }
    };

    private CameraManager.OnCameraOpenListener cameraListener = previewSize -> {
        FrameLayout.LayoutParams textureViewLayoutParams = (FrameLayout.LayoutParams) binding.rtvCamera.getLayoutParams();
        int newHeight = textureViewLayoutParams.width * previewSize.width / previewSize.height;
        int newWidth = textureViewLayoutParams.width;

        roundFrameLayout = new RoundFrameLayout(getContext());
        int sideLength = Math.min(newWidth, newHeight);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(sideLength, sideLength);
        roundFrameLayout.setLayoutParams(layoutParams);
        FrameLayout parentView = (FrameLayout) binding.rtvCamera.getParent();
        parentView.removeView(binding.rtvCamera);
        parentView.addView(roundFrameLayout);

        roundFrameLayout.addView(binding.rtvCamera);
        FrameLayout.LayoutParams newTextureViewLayoutParams = new FrameLayout.LayoutParams(newWidth, newHeight);
        newTextureViewLayoutParams.topMargin = -(newHeight - newWidth) / 2;
        binding.rtvCamera.setLayoutParams(newTextureViewLayoutParams);

        View siblingView = roundFrameLayout != null ? roundFrameLayout : binding.rtvCamera;
        roundBorderView = new RoundBorderView(getContext());
        ((FrameLayout) siblingView.getParent()).addView(roundBorderView, siblingView.getLayoutParams());

        new Handler(Looper.getMainLooper()).post(() -> {
            roundFrameLayout.setRadius(Math.min(roundFrameLayout.getWidth(), roundFrameLayout.getHeight()) / 2);
            roundFrameLayout.turnRound();
            roundBorderView.setRadius(Math.min(roundBorderView.getWidth(), roundBorderView.getHeight()) / 2);
            roundBorderView.turnRound();
        });
    };

    ImageView qr;
    TextView code;
    private void showResult(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        View v= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_result,null);
        qr=v.findViewById(R.id.h_code);
        code=v.findViewById(R.id.h_code_s);
        String json = new Gson().toJson(new MxPerson(person.getName(), person.getCardnum(), person.getCodestatus()));
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        String encode = Base64.encodeToString(bytes, 0, bytes.length, Base64.DEFAULT);
        int color=getPersonStauts(person.getCodestatus());
        Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(encode,
                650,650,"UTF-8","H","1",
                color, Color.WHITE, BitmapFactory.decodeResource(getResources(),R.drawable.logo),
                0.2F,null);
        builder.setTitle("核验通过")
                //                .setMessage("健康状态："+getPersonStauts(person.getCodestatus()))
                .setView(v)
                .setCancelable(false)
                .setPositiveButton("确认", (dialog, which) -> {
                    mListener.replaceFragment(HomeFragmet.getInstance());
                    dialog.dismiss();
                });
        AlertDialog alert=builder.create();
        alert.show();

        code.setTextColor(color);
        qr.setImageBitmap(bitmap);
    }

    private int getPersonStauts(int status){
        int color=0xFF2FA664;
        switch (status){
            case 1:
                qr.setBackground(getResources().getDrawable(R.drawable.shape_bg_qr_y));
                code.setText("黄码");
                color=0xFFFFEB3B;
                break;
            case 2:
                qr.setBackground(getResources().getDrawable(R.drawable.shape_bg_qr_r));
                code.setText("红码");
                color=0xFFF44336;
                break;
            default:
                qr.setBackground(getResources().getDrawable(R.drawable.shape_bg_qr_g));
                color=0xFF2FA664;
                code.setText("绿码");
                break;
        }
        return color;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFaceEvent(VerifyEvent event){
        Log.e(TAG,""+event.getMessage());
        binding.tvHint.setText(event.getMessage());
    }


}
