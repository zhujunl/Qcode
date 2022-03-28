package com.miaxis.bp990.view.finger;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.miaxis.bp990.BR;
import com.miaxis.bp990.R;
import com.miaxis.bp990.base.BaseViewModelFragment;
import com.miaxis.bp990.base.OnFragmentInteractionListener;
import com.miaxis.bp990.been.MxPerson;
import com.miaxis.bp990.data.entity.Person;
import com.miaxis.bp990.databinding.FragmentFingerBinding;
import com.miaxis.bp990.util.QRCodeUtil;
import com.miaxis.bp990.view.face.FaceFragment;
import com.miaxis.bp990.view.home.HomeFragmet;

import java.nio.charset.StandardCharsets;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

/**
 * @author ZJL
 * @date 2022/3/24 19:23
 * @des
 * @updateAuthor
 * @updateDes
 */
public class FingerFragment extends BaseViewModelFragment<FragmentFingerBinding,FingerViewModel> {

    private static FingerFragment instance;
    private OnFragmentInteractionListener mListener;
    private Person person;
    private MaterialDialog retryDialog;
    private int status;

    public static FingerFragment getInstance(){
        if(instance==null){
            instance=new FingerFragment();
        }
        return instance;
    }

    public static FingerFragment getInstance(Person person){
        if(instance==null){
            instance=new FingerFragment();
        }
        instance.setPerson(person);
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
        return R.layout.fragment_finger;
    }

    @Override
    protected FingerViewModel initViewModel() {
        return new ViewModelProvider(getActivity(),getViewModelProviderFactory()).get(FingerViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewmodel;
    }

    @Override
    protected void initView() {
        viewModel.personlive.setValue(person);
        binding.ivBack.setOnClickListener(v->onBackPressed());
        binding.tvSwitch.setOnClickListener(v->{mListener.replaceFragment(FaceFragment.getInstance(person));});
        binding.title.setText(person.getName());
        viewModel.fingerResultFlag.observe(this, fingerResultFlagObserver);
        viewModel.initFingerResult.observe(this, status -> {
            switch (status) {
                case FAILED:
                    mListener.dismissWaitDialog();
                    Log.e("Fragment_Finger:","Failed");
                    retryDialog = new MaterialDialog.Builder(getContext())
                            .title("初始化指纹模块失败，是否重试？")
                            .positiveText("重试")
                            .onPositive((dialog, which) -> {
                                viewModel.initFingerDevice();
                                dialog.dismiss();
                            })
                            .negativeText("退出")
                            .onNegative((dialog, which) -> {
                                dialog.dismiss();
                                mListener.backToStack(HomeFragmet.class);
                            })
                            .autoDismiss(false)
                            .show();
                    break;
                case LOADING:
                    mListener.showWaitDialog("正在初始化指纹模块");
                    break;
                case SUCCESS:
                    Log.e("Fragment_Finger:","SUCCESS");
                    mListener.dismissWaitDialog();
                    viewModel.verifyFinger();
                    break;
            }
        });
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(HomeFragmet.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.initFingerDevice();
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.releaseFingerDevice();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.releaseFingerDevice();
    }

    private void setPerson(Person person){
        this.person=person;
    }

    private void setStatus(int status){
        this.status=status;
    }


    private Observer<Boolean> fingerResultFlagObserver = flag ->{
        if (flag){
            showResult();
            Log.e("Finger:","比对成功");
        }else {
            binding.tvHint.setText("指纹核验失败，请重按手指");
            Log.e("Finger:","比对失败，请重按手指");
        }
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
}
