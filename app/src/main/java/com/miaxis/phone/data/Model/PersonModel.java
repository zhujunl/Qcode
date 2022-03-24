package com.miaxis.phone.data.Model;

import com.miaxis.phone.data.db.AppDataBase;
import com.miaxis.phone.data.entity.MxPerson;

import java.util.List;

/**
 * @author ZJL
 * @date 2022/3/24 15:53
 * @des
 * @updateAuthor
 * @updateDes
 */
public class PersonModel {

    public static long Save(MxPerson mxPerson){
       return AppDataBase.getInstance().getMxPersonDao().insert(mxPerson);
    }

    public static List<MxPerson> findPersonByName(String name){
        return AppDataBase.getInstance().getMxPersonDao().findPersonByName(name);
    }

    public static List<MxPerson> findPersonByCard(String cardNum){
        return AppDataBase.getInstance().getMxPersonDao().findPersonByCard(cardNum);
    }

    public static List<MxPerson> findPersonByNameAndIdCard(String name, String idCardNumber){
        return AppDataBase.getInstance().getMxPersonDao().findPersonByNameAndIdCard(name,idCardNumber);
    }

}
