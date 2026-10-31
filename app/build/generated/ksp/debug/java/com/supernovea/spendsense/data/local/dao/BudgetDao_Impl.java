package com.supernovea.spendsense.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.supernovea.spendsense.data.local.entity.BudgetEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BudgetDao_Impl implements BudgetDao {
  private final RoomDatabase __db;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByMonth;

  private final EntityUpsertionAdapter<BudgetEntity> __upsertionAdapterOfBudgetEntity;

  public BudgetDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__preparedStmtOfDeleteByMonth = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM budgets WHERE month_key = ?";
        return _query;
      }
    };
    this.__upsertionAdapterOfBudgetEntity = new EntityUpsertionAdapter<BudgetEntity>(new EntityInsertionAdapter<BudgetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `budgets` (`month_key`,`total_limit_minor`,`waste_limit_minor`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BudgetEntity entity) {
        statement.bindString(1, entity.getMonthKey());
        statement.bindLong(2, entity.getTotalLimitMinor());
        if (entity.getWasteLimitMinor() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getWasteLimitMinor());
        }
      }
    }, new EntityDeletionOrUpdateAdapter<BudgetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `budgets` SET `month_key` = ?,`total_limit_minor` = ?,`waste_limit_minor` = ? WHERE `month_key` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BudgetEntity entity) {
        statement.bindString(1, entity.getMonthKey());
        statement.bindLong(2, entity.getTotalLimitMinor());
        if (entity.getWasteLimitMinor() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getWasteLimitMinor());
        }
        statement.bindString(4, entity.getMonthKey());
      }
    });
  }

  @Override
  public Object deleteByMonth(final String monthKey, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByMonth.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, monthKey);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteByMonth.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object upsert(final BudgetEntity budget, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfBudgetEntity.upsert(budget);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<BudgetEntity> observeByMonth(final String monthKey) {
    final String _sql = "SELECT * FROM budgets WHERE month_key = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, monthKey);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"budgets"}, new Callable<BudgetEntity>() {
      @Override
      @Nullable
      public BudgetEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMonthKey = CursorUtil.getColumnIndexOrThrow(_cursor, "month_key");
          final int _cursorIndexOfTotalLimitMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "total_limit_minor");
          final int _cursorIndexOfWasteLimitMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "waste_limit_minor");
          final BudgetEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpMonthKey;
            _tmpMonthKey = _cursor.getString(_cursorIndexOfMonthKey);
            final long _tmpTotalLimitMinor;
            _tmpTotalLimitMinor = _cursor.getLong(_cursorIndexOfTotalLimitMinor);
            final Long _tmpWasteLimitMinor;
            if (_cursor.isNull(_cursorIndexOfWasteLimitMinor)) {
              _tmpWasteLimitMinor = null;
            } else {
              _tmpWasteLimitMinor = _cursor.getLong(_cursorIndexOfWasteLimitMinor);
            }
            _result = new BudgetEntity(_tmpMonthKey,_tmpTotalLimitMinor,_tmpWasteLimitMinor);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getByMonth(final String monthKey,
      final Continuation<? super BudgetEntity> $completion) {
    final String _sql = "SELECT * FROM budgets WHERE month_key = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, monthKey);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BudgetEntity>() {
      @Override
      @Nullable
      public BudgetEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMonthKey = CursorUtil.getColumnIndexOrThrow(_cursor, "month_key");
          final int _cursorIndexOfTotalLimitMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "total_limit_minor");
          final int _cursorIndexOfWasteLimitMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "waste_limit_minor");
          final BudgetEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpMonthKey;
            _tmpMonthKey = _cursor.getString(_cursorIndexOfMonthKey);
            final long _tmpTotalLimitMinor;
            _tmpTotalLimitMinor = _cursor.getLong(_cursorIndexOfTotalLimitMinor);
            final Long _tmpWasteLimitMinor;
            if (_cursor.isNull(_cursorIndexOfWasteLimitMinor)) {
              _tmpWasteLimitMinor = null;
            } else {
              _tmpWasteLimitMinor = _cursor.getLong(_cursorIndexOfWasteLimitMinor);
            }
            _result = new BudgetEntity(_tmpMonthKey,_tmpTotalLimitMinor,_tmpWasteLimitMinor);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<BudgetEntity>> observeAll() {
    final String _sql = "SELECT * FROM budgets ORDER BY month_key DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"budgets"}, new Callable<List<BudgetEntity>>() {
      @Override
      @NonNull
      public List<BudgetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMonthKey = CursorUtil.getColumnIndexOrThrow(_cursor, "month_key");
          final int _cursorIndexOfTotalLimitMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "total_limit_minor");
          final int _cursorIndexOfWasteLimitMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "waste_limit_minor");
          final List<BudgetEntity> _result = new ArrayList<BudgetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BudgetEntity _item;
            final String _tmpMonthKey;
            _tmpMonthKey = _cursor.getString(_cursorIndexOfMonthKey);
            final long _tmpTotalLimitMinor;
            _tmpTotalLimitMinor = _cursor.getLong(_cursorIndexOfTotalLimitMinor);
            final Long _tmpWasteLimitMinor;
            if (_cursor.isNull(_cursorIndexOfWasteLimitMinor)) {
              _tmpWasteLimitMinor = null;
            } else {
              _tmpWasteLimitMinor = _cursor.getLong(_cursorIndexOfWasteLimitMinor);
            }
            _item = new BudgetEntity(_tmpMonthKey,_tmpTotalLimitMinor,_tmpWasteLimitMinor);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
