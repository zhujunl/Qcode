package com.miaxis.bp990.view.register.faceregister;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;


import com.miaxis.bp990.R;
import com.miaxis.bp990.RoundBorderView;
import com.miaxis.bp990.RoundFrameLayout;
import com.miaxis.bp990.base.BaseViewModelFragment;
import com.miaxis.bp990.base.OnFragmentInteractionListener;
import com.miaxis.bp990.bridge.Status;
import com.miaxis.bp990.databinding.FragmentFaceRegisterBinding;
import com.miaxis.bp990.manager.CameraManager;

import androidx.lifecycle.ViewModelProvider;

/**
 * @author ZJL
 * @date 2022/3/25 9:50
 * @des
 * @updateAuthor
 * @updateDes
 */
public class FaceRegisterFragment extends BaseViewModelFragment<FragmentFaceRegisterBinding,FaceRegisterViewModel> {

    private static FaceRegisterFragment instance;
    private OnFragmentInteractionListener mListener;
    private RoundBorderView roundBorderView;
    private RoundFrameLayout roundFrameLayout;

    public static FaceRegisterFragment  getIntance(){
        if (instance==null){
            instance=new FaceRegisterFragment();
        }
        return instance;
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
        return R.layout.fragment_face_register;
    }

    @Override
    protected FaceRegisterViewModel initViewModel() {
        return new ViewModelProvider(this,getViewModelProviderFactory()).get(FaceRegisterViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.bp990.BR.viewmodel;
    }

    @Override
    protected void initView() {
        viewModel.shootFlag.setValue(Status.FAILED);
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.rtvCamera.getViewTreeObserver().addOnGlobalLayoutListener(globalListener);
        binding.ivTakePhoto.setOnClickListener(v -> {
            binding.ivTakePhoto.setVisibility(View.INVISIBLE);
            viewModel.takePicture();
        });
        binding.ivRetry.setOnClickListener(v -> {
            binding.ivTakePhoto.setVisibility(View.VISIBLE);
            viewModel.retry();
        });
        binding.ivConfirm.setOnClickListener(v -> viewModel.confirm());
        viewModel.confirmFlag.observe(this, aBoolean -> mListener.backToStack(null));
    }

    @Override
    public void onBackPressed()  {
        mListener.backToStack(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CameraManager.getInstance().closeFrontCamera();
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


    };
}
