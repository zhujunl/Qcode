package com.miaxis.phone.data.entity;

import java.util.List;

/**
 * @author ZJL
 * @date 2022/3/24 15:53
 * @des
 * @updateAuthor
 * @updateDes
 */
public class PersonManager {
    private static PersonManager instance;
    private Person person;

    public static PersonManager getInstance(){
        if(instance==null){
            instance=new PersonManager();
        }
        return instance;
    }

    public Person getPerson(){
        return person;
    }

    public Person FindPersonById(long id){
        person=PersonModel.FindPersonById(id);
        return person;
    }

    public Person FindPersonByName(String name){
        person=PersonModel.FindPersonByName(name);
        return person;
    }

    public Person FindPersonByCard(String card){
        person=PersonModel.FindPersonByCard(card);
        return person;
    }

    public Person FindPersonByCode(String code){
        person=PersonModel.FindPersonByCode(code);
        return person;
    }

    public List<Person> LoadPersons(){
        return PersonModel.LoadPersons();
    }
}
