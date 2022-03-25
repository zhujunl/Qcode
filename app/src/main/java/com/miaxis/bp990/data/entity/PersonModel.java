package com.miaxis.bp990.data.entity;

import com.miaxis.bp990.data.dao.AppDatabase;

import java.util.List;

/**
 * @author ZJL
 * @date 2022/3/24 14:11
 * @des 
 * @version 
 * @updateAuthor 
 * @updateDes 
 */
public class PersonModel   {

    public static void Save(Person person){
        int result=AppDatabase.getInstance().persondao().DeleteByCard(person.getCardnum());
        AppDatabase.getInstance().persondao().insert(person);
    }

    public static Person FindPersonByCard(String card){
        return AppDatabase.getInstance().persondao().FindPersonByCard(card);
    }

    public static List<Person> LoadPersons(){
        return AppDatabase.getInstance().persondao().LoadPerson();
    }
}
