package com.miaxis.bp990.data.dao;

import com.miaxis.bp990.data.entity.Person;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * @author ZJL
 * @date 2022/3/24 14:09
 * @des 
 * @version 
 * @updateAuthor 
 * @updateDes 
 */
@Dao
public interface PersonDao  {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Person person);

    @Query("select * from Person where Person.cardnum = :card")
    Person FindPersonByCard(String card);

    @Query("select * from Person order by Person.id asc")
    List<Person> LoadPerson();

}
