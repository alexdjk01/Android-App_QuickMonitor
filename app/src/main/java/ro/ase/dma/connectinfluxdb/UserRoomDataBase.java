package ro.ase.dma.connectinfluxdb;


import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, exportSchema = false, version =1)    // the database is created for objects of type User
public abstract class UserRoomDataBase extends RoomDatabase {

    private static final String DataBase_Name = "userDatabase";
    private static  UserRoomDataBase userRoomDataBase;

    public static synchronized UserRoomDataBase getInstance(Context context)
    {
        if (userRoomDataBase == null)
        {
            userRoomDataBase= Room.databaseBuilder(context,UserRoomDataBase.class, DataBase_Name)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }

        return userRoomDataBase;
    }

    public abstract UserDao getUserDao();
}
