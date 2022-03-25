package com.miaxis.bp990.view.result;

import android.content.Context;

import com.miaxis.bp990.BR;
import com.miaxis.bp990.R;
import com.miaxis.bp990.base.BaseViewModelFragment;
import com.miaxis.bp990.base.OnFragmentInteractionListener;
import com.miaxis.bp990.databinding.FragmentResultBinding;
import com.miaxis.bp990.view.home.HomeFragmet;

import androidx.lifecycle.ViewModelProvider;

/**
 * @author ZJL
 * @date 2022/3/25 14:17
 * @des
 * @updateAuthor
 * @updateDes
 */
public class ResultFragment extends BaseViewModelFragment<FragmentResultBinding,ResultViewModel> {

    private static ResultFragment instance;
    private OnFragmentInteractionListener mListener;

    public static ResultFragment getInstance(){
        if(instance==null){
            instance=new ResultFragment();
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
        return R.layout.fragment_result;
    }

    @Override
    protected ResultViewModel initViewModel() {
        return new ViewModelProvider(this,getViewModelProviderFactory()).get(ResultViewModel.class);
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
        mListener.backToStack(HomeFragmet.class);
    }
}
