package com.app.writeyourpackagenamehere.utilities;

import static com.app.writeyourpackagenamehere.utilities.Utils.gAllCategories;

import android.content.ContentValues;
import android.content.Context;

import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.app.writeyourpackagenamehere.models.ItemCategory;

import java.util.List;

public class LocalDatabaseRepo {
    private static CategoryInfoDatabase categoryInfoDatabase;
    private CategoryInfoDAO categoryInfoDAO;
    private static final Object LOCK = new Object();

    private static RoomDatabase.Callback dbCallback = new RoomDatabase.Callback(){
        public void onCreate (SupportSQLiteDatabase db){

        }
        public void onOpen (SupportSQLiteDatabase db){
            db.execSQL("Delete From ItemCategory");

            for(int i = 0; i < gAllCategories.size(); i++){
                ItemCategory itemCategory = gAllCategories.get(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put("categoryName", itemCategory.getCategoryName());
                contentValues.put("categoryId", itemCategory.getCategoryId());
                db.insert("ItemCategory", OnConflictStrategy.IGNORE, contentValues);
            }
        }
    };

    public synchronized static CategoryInfoDatabase getStoreInfoDatabase(Context context){
        if(categoryInfoDatabase == null) {
            synchronized (LOCK) {
                if (categoryInfoDatabase == null) {
                    categoryInfoDatabase = Room.databaseBuilder(context,
                        CategoryInfoDatabase.class, "category info db")
                        .addCallback(dbCallback).build();
                }
            }
        }
        return categoryInfoDatabase;
    }

    public List<ItemCategory> getCategoryInfo(Context context, String categoryStr) {
        if (categoryInfoDAO == null) {
            categoryInfoDAO = LocalDatabaseRepo.getStoreInfoDatabase(context).categoryInfoDAO();
        }
        return categoryInfoDAO.getStores(categoryStr+"%");
    }
}
