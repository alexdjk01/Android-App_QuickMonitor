package ro.ase.dma.connectinfluxdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao            // this is the Dao containing database query
public interface UserDao {

    @Query("select * from users")
    List<User> getAll();

    @Query("select * from users where ID=:userId")
    User getUserById(long userId);

    @Query("select * from users where userEmail=:email")
    User getUserByEmail(String email);

    @Query("delete from users where ID = :userId")
    void deleteUserById(long userId);

    @Insert
    long insert(User user);

    @Update
    int update(User user);

    @Delete
    int delete(User user);



}
