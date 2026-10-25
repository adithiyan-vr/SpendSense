package com.supernovea.spendsense.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.supernovea.spendsense.data.local.entity.CategoryEntity;
import com.supernovea.spendsense.data.local.entity.ExpenseEntity;
import com.supernovea.spendsense.data.local.entity.ExpenseWithCategoryEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class ExpenseDao_Impl implements ExpenseDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ExpenseEntity> __insertionAdapterOfExpenseEntity;

  private final EntityDeletionOrUpdateAdapter<ExpenseEntity> __updateAdapterOfExpenseEntity;

  private final SharedSQLiteStatement __preparedStmtOfSoftDelete;

  private final SharedSQLiteStatement __preparedStmtOfRestore;

  private final SharedSQLiteStatement __preparedStmtOfHardDelete;

  private final SharedSQLiteStatement __preparedStmtOfPurgeDeletedBefore;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public ExpenseDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfExpenseEntity = new EntityInsertionAdapter<ExpenseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `expenses` (`id`,`amount_minor`,`type`,`category_id`,`note`,`occurred_at`,`occurred_date`,`payment_method`,`created_at`,`updated_at`,`deleted_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExpenseEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAmountMinor());
        statement.bindString(3, entity.getType());
        statement.bindLong(4, entity.getCategoryId());
        if (entity.getNote() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getNote());
        }
        statement.bindLong(6, entity.getOccurredAt());
        statement.bindLong(7, entity.getOccurredDate());
        statement.bindString(8, entity.getPaymentMethod());
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getUpdatedAt());
        if (entity.getDeletedAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getDeletedAt());
        }
      }
    };
    this.__updateAdapterOfExpenseEntity = new EntityDeletionOrUpdateAdapter<ExpenseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `expenses` SET `id` = ?,`amount_minor` = ?,`type` = ?,`category_id` = ?,`note` = ?,`occurred_at` = ?,`occurred_date` = ?,`payment_method` = ?,`created_at` = ?,`updated_at` = ?,`deleted_at` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExpenseEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAmountMinor());
        statement.bindString(3, entity.getType());
        statement.bindLong(4, entity.getCategoryId());
        if (entity.getNote() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getNote());
        }
        statement.bindLong(6, entity.getOccurredAt());
        statement.bindLong(7, entity.getOccurredDate());
        statement.bindString(8, entity.getPaymentMethod());
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getUpdatedAt());
        if (entity.getDeletedAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getDeletedAt());
        }
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfSoftDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE expenses SET deleted_at = ?, updated_at = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRestore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE expenses SET deleted_at = NULL, updated_at = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfHardDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM expenses WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfPurgeDeletedBefore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM expenses WHERE deleted_at IS NOT NULL AND deleted_at < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM expenses";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ExpenseEntity expense, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfExpenseEntity.insertAndReturnId(expense);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<ExpenseEntity> expenses,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfExpenseEntity.insertAndReturnIdsList(expenses);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final ExpenseEntity expense, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfExpenseEntity.handle(expense);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object softDelete(final long id, final long now,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSoftDelete.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, now);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, now);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfSoftDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object restore(final long id, final long now,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRestore.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, now);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfRestore.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object hardDelete(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfHardDelete.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfHardDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object purgeDeletedBefore(final long cutoff,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfPurgeDeletedBefore.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cutoff);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfPurgeDeletedBefore.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ExpenseWithCategoryEntity>> observeBetween(final long startDay,
      final long endDay) {
    final String _sql = "SELECT * FROM expenses WHERE deleted_at IS NULL AND occurred_date BETWEEN ? AND ? ORDER BY occurred_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDay);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"categories",
        "expenses"}, new Callable<List<ExpenseWithCategoryEntity>>() {
      @Override
      @NonNull
      public List<ExpenseWithCategoryEntity> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
            final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
            final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
            final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
            final int _cursorIndexOfOccurredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "occurred_at");
            final int _cursorIndexOfOccurredDate = CursorUtil.getColumnIndexOrThrow(_cursor, "occurred_date");
            final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "payment_method");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
            final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
            final LongSparseArray<CategoryEntity> _collectionCategory = new LongSparseArray<CategoryEntity>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfCategoryId);
              _collectionCategory.put(_tmpKey, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcategoriesAscomSupernoveaSpendsenseDataLocalEntityCategoryEntity(_collectionCategory);
            final List<ExpenseWithCategoryEntity> _result = new ArrayList<ExpenseWithCategoryEntity>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final ExpenseWithCategoryEntity _item;
              final ExpenseEntity _tmpExpense;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final long _tmpAmountMinor;
              _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
              final String _tmpType;
              _tmpType = _cursor.getString(_cursorIndexOfType);
              final long _tmpCategoryId;
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
              final String _tmpNote;
              if (_cursor.isNull(_cursorIndexOfNote)) {
                _tmpNote = null;
              } else {
                _tmpNote = _cursor.getString(_cursorIndexOfNote);
              }
              final long _tmpOccurredAt;
              _tmpOccurredAt = _cursor.getLong(_cursorIndexOfOccurredAt);
              final long _tmpOccurredDate;
              _tmpOccurredDate = _cursor.getLong(_cursorIndexOfOccurredDate);
              final String _tmpPaymentMethod;
              _tmpPaymentMethod = _cursor.getString(_cursorIndexOfPaymentMethod);
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              final Long _tmpDeletedAt;
              if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
                _tmpDeletedAt = null;
              } else {
                _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
              }
              _tmpExpense = new ExpenseEntity(_tmpId,_tmpAmountMinor,_tmpType,_tmpCategoryId,_tmpNote,_tmpOccurredAt,_tmpOccurredDate,_tmpPaymentMethod,_tmpCreatedAt,_tmpUpdatedAt,_tmpDeletedAt);
              final CategoryEntity _tmpCategory;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfCategoryId);
              _tmpCategory = _collectionCategory.get(_tmpKey_1);
              _item = new ExpenseWithCategoryEntity(_tmpExpense,_tmpCategory);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ExpenseWithCategoryEntity>> observeAll() {
    final String _sql = "SELECT * FROM expenses WHERE deleted_at IS NULL ORDER BY occurred_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"categories",
        "expenses"}, new Callable<List<ExpenseWithCategoryEntity>>() {
      @Override
      @NonNull
      public List<ExpenseWithCategoryEntity> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
            final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
            final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
            final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
            final int _cursorIndexOfOccurredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "occurred_at");
            final int _cursorIndexOfOccurredDate = CursorUtil.getColumnIndexOrThrow(_cursor, "occurred_date");
            final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "payment_method");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
            final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
            final LongSparseArray<CategoryEntity> _collectionCategory = new LongSparseArray<CategoryEntity>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfCategoryId);
              _collectionCategory.put(_tmpKey, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcategoriesAscomSupernoveaSpendsenseDataLocalEntityCategoryEntity(_collectionCategory);
            final List<ExpenseWithCategoryEntity> _result = new ArrayList<ExpenseWithCategoryEntity>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final ExpenseWithCategoryEntity _item;
              final ExpenseEntity _tmpExpense;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final long _tmpAmountMinor;
              _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
              final String _tmpType;
              _tmpType = _cursor.getString(_cursorIndexOfType);
              final long _tmpCategoryId;
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
              final String _tmpNote;
              if (_cursor.isNull(_cursorIndexOfNote)) {
                _tmpNote = null;
              } else {
                _tmpNote = _cursor.getString(_cursorIndexOfNote);
              }
              final long _tmpOccurredAt;
              _tmpOccurredAt = _cursor.getLong(_cursorIndexOfOccurredAt);
              final long _tmpOccurredDate;
              _tmpOccurredDate = _cursor.getLong(_cursorIndexOfOccurredDate);
              final String _tmpPaymentMethod;
              _tmpPaymentMethod = _cursor.getString(_cursorIndexOfPaymentMethod);
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              final Long _tmpDeletedAt;
              if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
                _tmpDeletedAt = null;
              } else {
                _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
              }
              _tmpExpense = new ExpenseEntity(_tmpId,_tmpAmountMinor,_tmpType,_tmpCategoryId,_tmpNote,_tmpOccurredAt,_tmpOccurredDate,_tmpPaymentMethod,_tmpCreatedAt,_tmpUpdatedAt,_tmpDeletedAt);
              final CategoryEntity _tmpCategory;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfCategoryId);
              _tmpCategory = _collectionCategory.get(_tmpKey_1);
              _item = new ExpenseWithCategoryEntity(_tmpExpense,_tmpCategory);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<ExpenseWithCategoryEntity> observeById(final long id) {
    final String _sql = "SELECT * FROM expenses WHERE id = ? AND deleted_at IS NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"categories",
        "expenses"}, new Callable<ExpenseWithCategoryEntity>() {
      @Override
      @Nullable
      public ExpenseWithCategoryEntity call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
            final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
            final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
            final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
            final int _cursorIndexOfOccurredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "occurred_at");
            final int _cursorIndexOfOccurredDate = CursorUtil.getColumnIndexOrThrow(_cursor, "occurred_date");
            final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "payment_method");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
            final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
            final LongSparseArray<CategoryEntity> _collectionCategory = new LongSparseArray<CategoryEntity>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfCategoryId);
              _collectionCategory.put(_tmpKey, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcategoriesAscomSupernoveaSpendsenseDataLocalEntityCategoryEntity(_collectionCategory);
            final ExpenseWithCategoryEntity _result;
            if (_cursor.moveToFirst()) {
              final ExpenseEntity _tmpExpense;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final long _tmpAmountMinor;
              _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
              final String _tmpType;
              _tmpType = _cursor.getString(_cursorIndexOfType);
              final long _tmpCategoryId;
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
              final String _tmpNote;
              if (_cursor.isNull(_cursorIndexOfNote)) {
                _tmpNote = null;
              } else {
                _tmpNote = _cursor.getString(_cursorIndexOfNote);
              }
              final long _tmpOccurredAt;
              _tmpOccurredAt = _cursor.getLong(_cursorIndexOfOccurredAt);
              final long _tmpOccurredDate;
              _tmpOccurredDate = _cursor.getLong(_cursorIndexOfOccurredDate);
              final String _tmpPaymentMethod;
              _tmpPaymentMethod = _cursor.getString(_cursorIndexOfPaymentMethod);
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              final Long _tmpDeletedAt;
              if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
                _tmpDeletedAt = null;
              } else {
                _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
              }
              _tmpExpense = new ExpenseEntity(_tmpId,_tmpAmountMinor,_tmpType,_tmpCategoryId,_tmpNote,_tmpOccurredAt,_tmpOccurredDate,_tmpPaymentMethod,_tmpCreatedAt,_tmpUpdatedAt,_tmpDeletedAt);
              final CategoryEntity _tmpCategory;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfCategoryId);
              _tmpCategory = _collectionCategory.get(_tmpKey_1);
              _result = new ExpenseWithCategoryEntity(_tmpExpense,_tmpCategory);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getById(final long id,
      final Continuation<? super ExpenseWithCategoryEntity> $completion) {
    final String _sql = "SELECT * FROM expenses WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<ExpenseWithCategoryEntity>() {
      @Override
      @Nullable
      public ExpenseWithCategoryEntity call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
            final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
            final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
            final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
            final int _cursorIndexOfOccurredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "occurred_at");
            final int _cursorIndexOfOccurredDate = CursorUtil.getColumnIndexOrThrow(_cursor, "occurred_date");
            final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "payment_method");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
            final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
            final LongSparseArray<CategoryEntity> _collectionCategory = new LongSparseArray<CategoryEntity>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfCategoryId);
              _collectionCategory.put(_tmpKey, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcategoriesAscomSupernoveaSpendsenseDataLocalEntityCategoryEntity(_collectionCategory);
            final ExpenseWithCategoryEntity _result;
            if (_cursor.moveToFirst()) {
              final ExpenseEntity _tmpExpense;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final long _tmpAmountMinor;
              _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
              final String _tmpType;
              _tmpType = _cursor.getString(_cursorIndexOfType);
              final long _tmpCategoryId;
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
              final String _tmpNote;
              if (_cursor.isNull(_cursorIndexOfNote)) {
                _tmpNote = null;
              } else {
                _tmpNote = _cursor.getString(_cursorIndexOfNote);
              }
              final long _tmpOccurredAt;
              _tmpOccurredAt = _cursor.getLong(_cursorIndexOfOccurredAt);
              final long _tmpOccurredDate;
              _tmpOccurredDate = _cursor.getLong(_cursorIndexOfOccurredDate);
              final String _tmpPaymentMethod;
              _tmpPaymentMethod = _cursor.getString(_cursorIndexOfPaymentMethod);
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              final Long _tmpDeletedAt;
              if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
                _tmpDeletedAt = null;
              } else {
                _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
              }
              _tmpExpense = new ExpenseEntity(_tmpId,_tmpAmountMinor,_tmpType,_tmpCategoryId,_tmpNote,_tmpOccurredAt,_tmpOccurredDate,_tmpPaymentMethod,_tmpCreatedAt,_tmpUpdatedAt,_tmpDeletedAt);
              final CategoryEntity _tmpCategory;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfCategoryId);
              _tmpCategory = _collectionCategory.get(_tmpKey_1);
              _result = new ExpenseWithCategoryEntity(_tmpExpense,_tmpCategory);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
            _statement.release();
          }
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TypeSumTuple>> observeSumByTypeBetween(final long startDay, final long endDay) {
    final String _sql = "SELECT type, COALESCE(SUM(amount_minor), 0) as totalMinor FROM expenses WHERE deleted_at IS NULL AND occurred_date BETWEEN ? AND ? GROUP BY type";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"expenses"}, new Callable<List<TypeSumTuple>>() {
      @Override
      @NonNull
      public List<TypeSumTuple> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfType = 0;
          final int _cursorIndexOfTotalMinor = 1;
          final List<TypeSumTuple> _result = new ArrayList<TypeSumTuple>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TypeSumTuple _item;
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final long _tmpTotalMinor;
            _tmpTotalMinor = _cursor.getLong(_cursorIndexOfTotalMinor);
            _item = new TypeSumTuple(_tmpType,_tmpTotalMinor);
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

  @Override
  public Object count(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM expenses WHERE deleted_at IS NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipcategoriesAscomSupernoveaSpendsenseDataLocalEntityCategoryEntity(
      @NonNull final LongSparseArray<CategoryEntity> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, false, (map) -> {
        __fetchRelationshipcategoriesAscomSupernoveaSpendsenseDataLocalEntityCategoryEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`default_key`,`group_key`,`name`,`icon_key`,`color_hex`,`default_type`,`is_archived`,`is_default`,`sort_order` FROM `categories` WHERE `id` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "id");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfDefaultKey = 1;
      final int _cursorIndexOfGroupKey = 2;
      final int _cursorIndexOfName = 3;
      final int _cursorIndexOfIconKey = 4;
      final int _cursorIndexOfColorHex = 5;
      final int _cursorIndexOfDefaultType = 6;
      final int _cursorIndexOfIsArchived = 7;
      final int _cursorIndexOfIsDefault = 8;
      final int _cursorIndexOfSortOrder = 9;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        if (_map.containsKey(_tmpKey)) {
          final CategoryEntity _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final String _tmpDefaultKey;
          if (_cursor.isNull(_cursorIndexOfDefaultKey)) {
            _tmpDefaultKey = null;
          } else {
            _tmpDefaultKey = _cursor.getString(_cursorIndexOfDefaultKey);
          }
          final String _tmpGroupKey;
          _tmpGroupKey = _cursor.getString(_cursorIndexOfGroupKey);
          final String _tmpName;
          _tmpName = _cursor.getString(_cursorIndexOfName);
          final String _tmpIconKey;
          _tmpIconKey = _cursor.getString(_cursorIndexOfIconKey);
          final String _tmpColorHex;
          _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex);
          final String _tmpDefaultType;
          _tmpDefaultType = _cursor.getString(_cursorIndexOfDefaultType);
          final boolean _tmpIsArchived;
          final int _tmp;
          _tmp = _cursor.getInt(_cursorIndexOfIsArchived);
          _tmpIsArchived = _tmp != 0;
          final boolean _tmpIsDefault;
          final int _tmp_1;
          _tmp_1 = _cursor.getInt(_cursorIndexOfIsDefault);
          _tmpIsDefault = _tmp_1 != 0;
          final int _tmpSortOrder;
          _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
          _item_1 = new CategoryEntity(_tmpId,_tmpDefaultKey,_tmpGroupKey,_tmpName,_tmpIconKey,_tmpColorHex,_tmpDefaultType,_tmpIsArchived,_tmpIsDefault,_tmpSortOrder);
          _map.put(_tmpKey, _item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
