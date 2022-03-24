package com.miaxis.bp990.view.face;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.miaxis.bp990.BR;
import com.miaxis.bp990.R;
import com.miaxis.bp990.RoundBorderView;
import com.miaxis.bp990.RoundFrameLayout;
import com.miaxis.bp990.base.BaseViewModelFragment;
import com.miaxis.bp990.base.OnFragmentInteractionListener;
import com.miaxis.bp990.data.entity.Person;
import com.miaxis.bp990.databinding.FragmentFaceBinding;
import com.miaxis.bp990.event.VerifyEvent;
import com.miaxis.bp990.manager.CameraManager;
import com.miaxis.bp990.manager.FaceManager;
import com.miaxis.bp990.util.FileUtil;
import com.miaxis.bp990.view.finger.FingerFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
        return BR.viewmodel;
    }

    @Override
    protected void initView() {
        viewModel.personlive.setValue(person);
        binding.rtvCamera.getViewTreeObserver().addOnGlobalLayoutListener(globalListener);
        binding.tvSwitch.setOnClickListener(v->{
            String path=viewModel.path.getValue();
            Bitmap bit=FileUtil.loadBitmap(path);
            binding.ivHeader.setImageBitmap(bit);
            mListener.replaceFragment(FingerFragment.getInstance(viewModel.personlive.getValue()));
        });

        binding.getPicture.setOnClickListener(v -> {CameraManager.getInstance().takeBackPicture((data, camera) -> {
            ShowPic(data);
        });});
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
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
            CameraManager.getInstance().openBackCamera(binding.rtvCamera, cameraListener);
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

    private void ShowPic(byte[] data){
        Bitmap facePicture;
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_picture,null);
        ImageView img=view.findViewById(R.id.dialog_pic);
        Button cancel=view.findViewById(R.id.pic_cancel);
        Button sure=view.findViewById(R.id.pic_sure);
        facePicture= FaceManager.getInstance().adjustPhotoRotation(BitmapFactory.decodeByteArray(data,0,data.length),90);
        img.setImageBitmap(facePicture);
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog alertDialog=builder.create();
        cancel.setOnClickListener(v -> {
            CameraManager.getInstance().startBackPreview();
            alertDialog.dismiss();
        });
        sure.setOnClickListener(v -> {
            viewModel.SavePic(person,facePicture);
            CameraManager.getInstance().startBackPreview();
            alertDialog.dismiss();}
            );
        alertDialog.show();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFaceEvent(VerifyEvent event){
        Log.e(TAG,""+event.getMessage());
    }


}
