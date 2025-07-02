package com.wleowleo.aiadvisor.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Transaction.class, Goal.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    
    public abstract TransactionDao transactionDao();
    public abstract GoalDao goalDao();
    
    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                "ai_advisor_database"
            ).build();
        }
        return INSTANCE;
    }
}
