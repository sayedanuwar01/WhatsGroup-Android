package com.app.writeyourpackagenamehere.utilities;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.app.writeyourpackagenamehere.models.ItemCategory;

@Database(entities = {ItemCategory.class}, version = 1)
public abstract class CategoryInfoDatabase extends RoomDatabase {
    public abstract CategoryInfoDAO categoryInfoDAO();
}
