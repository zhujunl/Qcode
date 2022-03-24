package com.miaxis.phone.data.entity;

import com.miaxis.phone.data.dao.AppDatabase;

import java.util.List;

/**
 * @author ZJL
 * @date 2022/3/24 15:53
 * @des
 * @updateAuthor
 * @updateDes
 */
public class PersonModel {

    public static void Save(Person person){
        AppDatabase.getInstance().persondao().insert(person);
    }

    public static Person FindPersonById(long id){
        return AppDatabase.getInstance().persondao().FindPersonById(id);
    }

    public static Person FindPersonByName(String name){
        return AppDatabase.getInstance().persondao().FindPersonByName(name);
    }

    public static Person FindPersonByCard(String cardnum){
        return AppDatabase.getInstance().persondao().FindPersonByCard(cardnum);
    }

    public static Person FindPersonByCode(String code){
        return AppDatabase.getInstance().persondao().FindPersonByCode(code);
    }

    public static List<Person> LoadPersons(){
        return AppDatabase.getInstance().persondao().LoadPersons();
    }
}
