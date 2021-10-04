package com.app.writeyourpackagenamehere.utilities;

import androidx.room.Dao;
import androidx.room.Query;

import com.app.writeyourpackagenamehere.models.ItemCategory;

import java.util.List;

@Dao
public interface CategoryInfoDAO {
    @Query("SELECT * FROM ItemCategory WHERE categoryName like :storeIn ")
    List<ItemCategory> getStores(String storeIn);
}
