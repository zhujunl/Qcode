package com.miaxis.phone.data.dao;

import com.miaxis.phone.data.entity.Person;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * @author ZJL
 * @date 2022/3/24 15:44
 * @des
 * @updateAuthor
 * @updateDes
 */
@Dao
public interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Person person);

    @Query("select * from Person where Person.id = :id")
    Person FindPersonById(long id);

    @Query("select * from Person where Person.name = :name")
    Person FindPersonByName(String name);

    @Query("select * from Person where Person.cardnum = :cardnum")
    Person FindPersonByCard(String cardnum);

    @Query("select * from Person where Person.codestatus = :code")
    Person FindPersonByCode(String code);

    @Query("select * from Person")
    List<Person> LoadPersons();

}
