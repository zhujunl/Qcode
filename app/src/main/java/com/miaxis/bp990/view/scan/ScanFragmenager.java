package com.miaxis.bp990.view.scan;

import android.content.Context;

import com.miaxis.bp990.BR;
import com.miaxis.bp990.R;
import com.miaxis.bp990.base.BaseViewModelFragment;
import com.miaxis.bp990.base.OnFragmentInteractionListener;
import com.miaxis.bp990.databinding.FragmentScanBinding;

import androidx.lifecycle.ViewModelProvider;

/**
 * @author ZJL
 * @date 2022/3/25 14:56
 * @des
 * @updateAuthor
 * @updateDes
 */
public class ScanFragmenager extends BaseViewModelFragment<FragmentScanBinding,ScanViewModel> {

    private static ScanFragmenager instance;
    private OnFragmentInteractionListener mListener;

    public static ScanFragmenager getInstance(){
        if(instance==null){
            instance=new ScanFragmenager();
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
        return R.layout.fragment_scan;
    }

    @Override
    protected ScanViewModel initViewModel() {
        return new ViewModelProvider(this,getViewModelProviderFactory()).get(ScanViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewmodel;
    }

    @Override
    protected void initView() {

    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
    }
}
