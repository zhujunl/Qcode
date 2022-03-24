package com.miaxis.bp990.view.home;

import android.content.Context;
import android.util.Log;

import com.miaxis.bp990.App.App;
import com.miaxis.bp990.BR;
import com.miaxis.bp990.R;
import com.miaxis.bp990.base.BaseViewModelFragment;
import com.miaxis.bp990.base.OnFragmentInteractionListener;
import com.miaxis.bp990.been.IDCardRecord;
import com.miaxis.bp990.data.entity.Person;
import com.miaxis.bp990.databinding.FragmentHomeBinding;
import com.miaxis.bp990.manager.CardManager;
import com.miaxis.bp990.view.face.FaceFragment;
import com.miaxis.bp990.view.finger.FingerFragment;

import androidx.lifecycle.ViewModelProvider;

/**
 * @author ZJL
 * @date 2022/3/24 18:13
 * @des
 * @updateAuthor
 * @updateDes
 */
public class HomeFragmet extends BaseViewModelFragment<FragmentHomeBinding,HomeViewModel> {

    private static HomeFragmet instance;
    private OnFragmentInteractionListener mListener;

    public static HomeFragmet getInstance(){
        return new HomeFragmet();
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
        return R.layout.fragment_home;
    }

    @Override
    protected HomeViewModel initViewModel() {
        return new ViewModelProvider(getActivity(),getViewModelProviderFactory()).get(HomeViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewmodel;
    }

    @Override
    protected void initView() {
        binding.card.setOnClickListener(v->{
            CardManager.getInstance().init(App.getInstance(), new CardManager.IDCardListener() {
            @Override
            public void onIDCardInitResult(boolean result) {
                Log.e("TAG","CardManager"+result);
            }

            @Override
            public void onIDCardReceive(IDCardRecord idCardRecord, String message) {
                if (idCardRecord!=null){

                    Log.e("TAG","CardManager"+idCardRecord);
                }
            }
        });});
        binding.face.setOnClickListener(v->{
            mListener.replaceFragment(FaceFragment.getInstance());
        });
        binding.finger.setOnClickListener(v->{mListener.replaceFragment(FingerFragment.getInstance()); });
        binding.add.setOnClickListener(v->{
            Person person=new Person();
            person.setName("1111");
            mListener.replaceFragment(FaceFragment.getInstance(person));
        });
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
    }
}
