package com.miaxis.bp990.data.entity;

import java.util.List;

/**
 * @author ZJL
 * @date 2022/3/24 14:14
 * @des 
 * @version 
 * @updateAuthor 
 * @updateDes 
 */
public class PersonManager   {
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

   public List<Person> LoadPerson(){
      return PersonModel.LoadPersons();
   }

   public Person FindPersonByCard(String card){
      person=PersonModel.FindPersonByCard(card);
      return person;
   }

   public void Save(Person person){
      PersonModel.Save(person);
   }
}
