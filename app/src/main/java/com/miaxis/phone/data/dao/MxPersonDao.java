package com.miaxis.phone.data.dao;

import com.miaxis.phone.data.entity.MxPerson;

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
public interface MxPersonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(MxPerson mxPerson);

    @Query("select * from MxPerson where MxPerson.name = :name limit 1")
    List<MxPerson> findPersonByName(String name);

    @Query("select * from MxPerson where MxPerson.cardNumber = :cardNum limit 1")
    List<MxPerson> findPersonByCard(String cardNum);

    @Query("select * from MxPerson where MxPerson.name = :name and MxPerson.cardNumber= :cardNum limit 1")
    List<MxPerson> findPersonByNameAndIdCard(String name, String cardNum);

    @Query("select * from MxPerson order by MxPerson.id desc limit 1")
    List<MxPerson> findLast();

}
