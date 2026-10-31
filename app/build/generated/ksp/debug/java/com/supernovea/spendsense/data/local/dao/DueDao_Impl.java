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
import com.supernovea.spendsense.data.local.entity.DueEntity;
import com.supernovea.spendsense.data.local.entity.DueWithCategoryEntity;
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
public final class DueDao_Impl implements DueDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DueEntity> __insertionAdapterOfDueEntity;

  private final EntityDeletionOrUpdateAdapter<DueEntity> __updateAdapterOfDueEntity;

  private final SharedSQLiteStatement __preparedStmtOfSoftDelete;

  private final SharedSQLiteStatement __preparedStmtOfRestore;

  private final SharedSQLiteStatement __preparedStmtOfPurgeDeletedBefore;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public DueDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDueEntity = new EntityInsertionAdapter<DueEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `dues` (`id`,`title`,`amount_minor`,`due_date`,`due_time_minutes`,`repeat_interval`,`status`,`category_id`,`need_waste_type`,`paid_expense_id`,`paid_at`,`notes`,`reminder_lead_days`,`is_deleted`,`deleted_at`,`created_at`,`updated_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DueEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindLong(3, entity.getAmountMinor());
        statement.bindLong(4, entity.getDueDate());
        if (entity.getDueTimeMinutes() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getDueTimeMinutes());
        }
        statement.bindString(6, entity.getRepeatInterval());
        statement.bindString(7, entity.getStatus());
        if (entity.getCategoryId() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getCategoryId());
        }
        statement.bindString(9, entity.getNeedWasteType());
        if (entity.getPaidExpenseId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getPaidExpenseId());
        }
        if (entity.getPaidAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getPaidAt());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getNotes());
        }
        statement.bindLong(13, entity.getReminderLeadDays());
        final int _tmp = entity.isDeleted() ? 1 : 0;
        statement.bindLong(14, _tmp);
        if (entity.getDeletedAt() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getDeletedAt());
        }
        statement.bindLong(16, entity.getCreatedAt());
        statement.bindLong(17, entity.getUpdatedAt());
      }
    };
    this.__updateAdapterOfDueEntity = new EntityDeletionOrUpdateAdapter<DueEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `dues` SET `id` = ?,`title` = ?,`amount_minor` = ?,`due_date` = ?,`due_time_minutes` = ?,`repeat_interval` = ?,`status` = ?,`category_id` = ?,`need_waste_type` = ?,`paid_expense_id` = ?,`paid_at` = ?,`notes` = ?,`reminder_lead_days` = ?,`is_deleted` = ?,`deleted_at` = ?,`created_at` = ?,`updated_at` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DueEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindLong(3, entity.getAmountMinor());
        statement.bindLong(4, entity.getDueDate());
        if (entity.getDueTimeMinutes() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getDueTimeMinutes());
        }
        statement.bindString(6, entity.getRepeatInterval());
        statement.bindString(7, entity.getStatus());
        if (entity.getCategoryId() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getCategoryId());
        }
        statement.bindString(9, entity.getNeedWasteType());
        if (entity.getPaidExpenseId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getPaidExpenseId());
        }
        if (entity.getPaidAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getPaidAt());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getNotes());
        }
        statement.bindLong(13, entity.getReminderLeadDays());
        final int _tmp = entity.isDeleted() ? 1 : 0;
        statement.bindLong(14, _tmp);
        if (entity.getDeletedAt() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getDeletedAt());
        }
        statement.bindLong(16, entity.getCreatedAt());
        statement.bindLong(17, entity.getUpdatedAt());
        statement.bindLong(18, entity.getId());
      }
    };
    this.__preparedStmtOfSoftDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE dues SET is_deleted = 1, deleted_at = ?, updated_at = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRestore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE dues SET is_deleted = 0, deleted_at = NULL, updated_at = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfPurgeDeletedBefore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM dues WHERE is_deleted = 1 AND deleted_at IS NOT NULL AND deleted_at < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM dues";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final DueEntity due, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDueEntity.insertAndReturnId(due);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<DueEntity> dues,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfDueEntity.insertAndReturnIdsList(dues);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final DueEntity due, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDueEntity.handle(due);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object softDelete(final long id, final long deletedAt, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSoftDelete.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, deletedAt);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, updatedAt);
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
  public Object restore(final long id, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRestore.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, updatedAt);
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
  public Object purgeDeletedBefore(final long cutoffEpochMillis,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfPurgeDeletedBefore.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cutoffEpochMillis);
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
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
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
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DueWithCategoryEntity>> observeAllActive() {
    final String _sql = "SELECT * FROM dues WHERE is_deleted = 0 AND deleted_at IS NULL ORDER BY due_date ASC, id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"categories",
        "dues"}, new Callable<List<DueWithCategoryEntity>>() {
      @Override
      @NonNull
      public List<DueWithCategoryEntity> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
            final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
            final int _cursorIndexOfDueTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "due_time_minutes");
            final int _cursorIndexOfRepeatInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "repeat_interval");
            final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
            final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
            final int _cursorIndexOfNeedWasteType = CursorUtil.getColumnIndexOrThrow(_cursor, "need_waste_type");
            final int _cursorIndexOfPaidExpenseId = CursorUtil.getColumnIndexOrThrow(_cursor, "paid_expense_id");
            final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paid_at");
            final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
            final int _cursorIndexOfReminderLeadDays = CursorUtil.getColumnIndexOrThrow(_cursor, "reminder_lead_days");
            final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_deleted");
            final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
            final LongSparseArray<CategoryEntity> _collectionCategory = new LongSparseArray<CategoryEntity>();
            while (_cursor.moveToNext()) {
              final Long _tmpKey;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpKey = null;
              } else {
                _tmpKey = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              if (_tmpKey != null) {
                _collectionCategory.put(_tmpKey, null);
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcategoriesAscomSupernoveaSpendsenseDataLocalEntityCategoryEntity(_collectionCategory);
            final List<DueWithCategoryEntity> _result = new ArrayList<DueWithCategoryEntity>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final DueWithCategoryEntity _item;
              final DueEntity _tmpDue;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpTitle;
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
              final long _tmpAmountMinor;
              _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
              final long _tmpDueDate;
              _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
              final Integer _tmpDueTimeMinutes;
              if (_cursor.isNull(_cursorIndexOfDueTimeMinutes)) {
                _tmpDueTimeMinutes = null;
              } else {
                _tmpDueTimeMinutes = _cursor.getInt(_cursorIndexOfDueTimeMinutes);
              }
              final String _tmpRepeatInterval;
              _tmpRepeatInterval = _cursor.getString(_cursorIndexOfRepeatInterval);
              final String _tmpStatus;
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
              final Long _tmpCategoryId;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpCategoryId = null;
              } else {
                _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              final String _tmpNeedWasteType;
              _tmpNeedWasteType = _cursor.getString(_cursorIndexOfNeedWasteType);
              final Long _tmpPaidExpenseId;
              if (_cursor.isNull(_cursorIndexOfPaidExpenseId)) {
                _tmpPaidExpenseId = null;
              } else {
                _tmpPaidExpenseId = _cursor.getLong(_cursorIndexOfPaidExpenseId);
              }
              final Long _tmpPaidAt;
              if (_cursor.isNull(_cursorIndexOfPaidAt)) {
                _tmpPaidAt = null;
              } else {
                _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
              }
              final String _tmpNotes;
              if (_cursor.isNull(_cursorIndexOfNotes)) {
                _tmpNotes = null;
              } else {
                _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
              }
              final int _tmpReminderLeadDays;
              _tmpReminderLeadDays = _cursor.getInt(_cursorIndexOfReminderLeadDays);
              final boolean _tmpIsDeleted;
              final int _tmp;
              _tmp = _cursor.getInt(_cursorIndexOfIsDeleted);
              _tmpIsDeleted = _tmp != 0;
              final Long _tmpDeletedAt;
              if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
                _tmpDeletedAt = null;
              } else {
                _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              _tmpDue = new DueEntity(_tmpId,_tmpTitle,_tmpAmountMinor,_tmpDueDate,_tmpDueTimeMinutes,_tmpRepeatInterval,_tmpStatus,_tmpCategoryId,_tmpNeedWasteType,_tmpPaidExpenseId,_tmpPaidAt,_tmpNotes,_tmpReminderLeadDays,_tmpIsDeleted,_tmpDeletedAt,_tmpCreatedAt,_tmpUpdatedAt);
              final CategoryEntity _tmpCategory;
              final Long _tmpKey_1;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpKey_1 = null;
              } else {
                _tmpKey_1 = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              if (_tmpKey_1 != null) {
                _tmpCategory = _collectionCategory.get(_tmpKey_1);
              } else {
                _tmpCategory = null;
              }
              _item = new DueWithCategoryEntity(_tmpDue,_tmpCategory);
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
  public Flow<List<DueWithCategoryEntity>> observePending() {
    final String _sql = "SELECT * FROM dues WHERE is_deleted = 0 AND deleted_at IS NULL AND status != 'PAID' AND status != 'SKIPPED' ORDER BY due_date ASC, id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"categories",
        "dues"}, new Callable<List<DueWithCategoryEntity>>() {
      @Override
      @NonNull
      public List<DueWithCategoryEntity> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
            final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
            final int _cursorIndexOfDueTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "due_time_minutes");
            final int _cursorIndexOfRepeatInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "repeat_interval");
            final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
            final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
            final int _cursorIndexOfNeedWasteType = CursorUtil.getColumnIndexOrThrow(_cursor, "need_waste_type");
            final int _cursorIndexOfPaidExpenseId = CursorUtil.getColumnIndexOrThrow(_cursor, "paid_expense_id");
            final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paid_at");
            final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
            final int _cursorIndexOfReminderLeadDays = CursorUtil.getColumnIndexOrThrow(_cursor, "reminder_lead_days");
            final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_deleted");
            final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
            final LongSparseArray<CategoryEntity> _collectionCategory = new LongSparseArray<CategoryEntity>();
            while (_cursor.moveToNext()) {
              final Long _tmpKey;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpKey = null;
              } else {
                _tmpKey = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              if (_tmpKey != null) {
                _collectionCategory.put(_tmpKey, null);
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcategoriesAscomSupernoveaSpendsenseDataLocalEntityCategoryEntity(_collectionCategory);
            final List<DueWithCategoryEntity> _result = new ArrayList<DueWithCategoryEntity>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final DueWithCategoryEntity _item;
              final DueEntity _tmpDue;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpTitle;
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
              final long _tmpAmountMinor;
              _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
              final long _tmpDueDate;
              _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
              final Integer _tmpDueTimeMinutes;
              if (_cursor.isNull(_cursorIndexOfDueTimeMinutes)) {
                _tmpDueTimeMinutes = null;
              } else {
                _tmpDueTimeMinutes = _cursor.getInt(_cursorIndexOfDueTimeMinutes);
              }
              final String _tmpRepeatInterval;
              _tmpRepeatInterval = _cursor.getString(_cursorIndexOfRepeatInterval);
              final String _tmpStatus;
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
              final Long _tmpCategoryId;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpCategoryId = null;
              } else {
                _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              final String _tmpNeedWasteType;
              _tmpNeedWasteType = _cursor.getString(_cursorIndexOfNeedWasteType);
              final Long _tmpPaidExpenseId;
              if (_cursor.isNull(_cursorIndexOfPaidExpenseId)) {
                _tmpPaidExpenseId = null;
              } else {
                _tmpPaidExpenseId = _cursor.getLong(_cursorIndexOfPaidExpenseId);
              }
              final Long _tmpPaidAt;
              if (_cursor.isNull(_cursorIndexOfPaidAt)) {
                _tmpPaidAt = null;
              } else {
                _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
              }
              final String _tmpNotes;
              if (_cursor.isNull(_cursorIndexOfNotes)) {
                _tmpNotes = null;
              } else {
                _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
              }
              final int _tmpReminderLeadDays;
              _tmpReminderLeadDays = _cursor.getInt(_cursorIndexOfReminderLeadDays);
              final boolean _tmpIsDeleted;
              final int _tmp;
              _tmp = _cursor.getInt(_cursorIndexOfIsDeleted);
              _tmpIsDeleted = _tmp != 0;
              final Long _tmpDeletedAt;
              if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
                _tmpDeletedAt = null;
              } else {
                _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              _tmpDue = new DueEntity(_tmpId,_tmpTitle,_tmpAmountMinor,_tmpDueDate,_tmpDueTimeMinutes,_tmpRepeatInterval,_tmpStatus,_tmpCategoryId,_tmpNeedWasteType,_tmpPaidExpenseId,_tmpPaidAt,_tmpNotes,_tmpReminderLeadDays,_tmpIsDeleted,_tmpDeletedAt,_tmpCreatedAt,_tmpUpdatedAt);
              final CategoryEntity _tmpCategory;
              final Long _tmpKey_1;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpKey_1 = null;
              } else {
                _tmpKey_1 = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              if (_tmpKey_1 != null) {
                _tmpCategory = _collectionCategory.get(_tmpKey_1);
              } else {
                _tmpCategory = null;
              }
              _item = new DueWithCategoryEntity(_tmpDue,_tmpCategory);
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
  public Flow<List<DueWithCategoryEntity>> observeUpcomingBefore(final long endEpochDay) {
    final String _sql = "SELECT * FROM dues WHERE is_deleted = 0 AND deleted_at IS NULL AND status != 'PAID' AND status != 'SKIPPED' AND due_date <= ? ORDER BY due_date ASC, id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, endEpochDay);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"categories",
        "dues"}, new Callable<List<DueWithCategoryEntity>>() {
      @Override
      @NonNull
      public List<DueWithCategoryEntity> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
            final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
            final int _cursorIndexOfDueTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "due_time_minutes");
            final int _cursorIndexOfRepeatInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "repeat_interval");
            final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
            final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
            final int _cursorIndexOfNeedWasteType = CursorUtil.getColumnIndexOrThrow(_cursor, "need_waste_type");
            final int _cursorIndexOfPaidExpenseId = CursorUtil.getColumnIndexOrThrow(_cursor, "paid_expense_id");
            final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paid_at");
            final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
            final int _cursorIndexOfReminderLeadDays = CursorUtil.getColumnIndexOrThrow(_cursor, "reminder_lead_days");
            final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_deleted");
            final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
            final LongSparseArray<CategoryEntity> _collectionCategory = new LongSparseArray<CategoryEntity>();
            while (_cursor.moveToNext()) {
              final Long _tmpKey;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpKey = null;
              } else {
                _tmpKey = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              if (_tmpKey != null) {
                _collectionCategory.put(_tmpKey, null);
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcategoriesAscomSupernoveaSpendsenseDataLocalEntityCategoryEntity(_collectionCategory);
            final List<DueWithCategoryEntity> _result = new ArrayList<DueWithCategoryEntity>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final DueWithCategoryEntity _item;
              final DueEntity _tmpDue;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpTitle;
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
              final long _tmpAmountMinor;
              _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
              final long _tmpDueDate;
              _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
              final Integer _tmpDueTimeMinutes;
              if (_cursor.isNull(_cursorIndexOfDueTimeMinutes)) {
                _tmpDueTimeMinutes = null;
              } else {
                _tmpDueTimeMinutes = _cursor.getInt(_cursorIndexOfDueTimeMinutes);
              }
              final String _tmpRepeatInterval;
              _tmpRepeatInterval = _cursor.getString(_cursorIndexOfRepeatInterval);
              final String _tmpStatus;
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
              final Long _tmpCategoryId;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpCategoryId = null;
              } else {
                _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              final String _tmpNeedWasteType;
              _tmpNeedWasteType = _cursor.getString(_cursorIndexOfNeedWasteType);
              final Long _tmpPaidExpenseId;
              if (_cursor.isNull(_cursorIndexOfPaidExpenseId)) {
                _tmpPaidExpenseId = null;
              } else {
                _tmpPaidExpenseId = _cursor.getLong(_cursorIndexOfPaidExpenseId);
              }
              final Long _tmpPaidAt;
              if (_cursor.isNull(_cursorIndexOfPaidAt)) {
                _tmpPaidAt = null;
              } else {
                _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
              }
              final String _tmpNotes;
              if (_cursor.isNull(_cursorIndexOfNotes)) {
                _tmpNotes = null;
              } else {
                _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
              }
              final int _tmpReminderLeadDays;
              _tmpReminderLeadDays = _cursor.getInt(_cursorIndexOfReminderLeadDays);
              final boolean _tmpIsDeleted;
              final int _tmp;
              _tmp = _cursor.getInt(_cursorIndexOfIsDeleted);
              _tmpIsDeleted = _tmp != 0;
              final Long _tmpDeletedAt;
              if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
                _tmpDeletedAt = null;
              } else {
                _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              _tmpDue = new DueEntity(_tmpId,_tmpTitle,_tmpAmountMinor,_tmpDueDate,_tmpDueTimeMinutes,_tmpRepeatInterval,_tmpStatus,_tmpCategoryId,_tmpNeedWasteType,_tmpPaidExpenseId,_tmpPaidAt,_tmpNotes,_tmpReminderLeadDays,_tmpIsDeleted,_tmpDeletedAt,_tmpCreatedAt,_tmpUpdatedAt);
              final CategoryEntity _tmpCategory;
              final Long _tmpKey_1;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpKey_1 = null;
              } else {
                _tmpKey_1 = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              if (_tmpKey_1 != null) {
                _tmpCategory = _collectionCategory.get(_tmpKey_1);
              } else {
                _tmpCategory = null;
              }
              _item = new DueWithCategoryEntity(_tmpDue,_tmpCategory);
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
  public Flow<DueWithCategoryEntity> observeById(final long id) {
    final String _sql = "SELECT * FROM dues WHERE id = ? AND is_deleted = 0 AND deleted_at IS NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"categories",
        "dues"}, new Callable<DueWithCategoryEntity>() {
      @Override
      @Nullable
      public DueWithCategoryEntity call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
            final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
            final int _cursorIndexOfDueTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "due_time_minutes");
            final int _cursorIndexOfRepeatInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "repeat_interval");
            final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
            final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
            final int _cursorIndexOfNeedWasteType = CursorUtil.getColumnIndexOrThrow(_cursor, "need_waste_type");
            final int _cursorIndexOfPaidExpenseId = CursorUtil.getColumnIndexOrThrow(_cursor, "paid_expense_id");
            final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paid_at");
            final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
            final int _cursorIndexOfReminderLeadDays = CursorUtil.getColumnIndexOrThrow(_cursor, "reminder_lead_days");
            final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_deleted");
            final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
            final LongSparseArray<CategoryEntity> _collectionCategory = new LongSparseArray<CategoryEntity>();
            while (_cursor.moveToNext()) {
              final Long _tmpKey;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpKey = null;
              } else {
                _tmpKey = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              if (_tmpKey != null) {
                _collectionCategory.put(_tmpKey, null);
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcategoriesAscomSupernoveaSpendsenseDataLocalEntityCategoryEntity(_collectionCategory);
            final DueWithCategoryEntity _result;
            if (_cursor.moveToFirst()) {
              final DueEntity _tmpDue;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpTitle;
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
              final long _tmpAmountMinor;
              _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
              final long _tmpDueDate;
              _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
              final Integer _tmpDueTimeMinutes;
              if (_cursor.isNull(_cursorIndexOfDueTimeMinutes)) {
                _tmpDueTimeMinutes = null;
              } else {
                _tmpDueTimeMinutes = _cursor.getInt(_cursorIndexOfDueTimeMinutes);
              }
              final String _tmpRepeatInterval;
              _tmpRepeatInterval = _cursor.getString(_cursorIndexOfRepeatInterval);
              final String _tmpStatus;
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
              final Long _tmpCategoryId;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpCategoryId = null;
              } else {
                _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              final String _tmpNeedWasteType;
              _tmpNeedWasteType = _cursor.getString(_cursorIndexOfNeedWasteType);
              final Long _tmpPaidExpenseId;
              if (_cursor.isNull(_cursorIndexOfPaidExpenseId)) {
                _tmpPaidExpenseId = null;
              } else {
                _tmpPaidExpenseId = _cursor.getLong(_cursorIndexOfPaidExpenseId);
              }
              final Long _tmpPaidAt;
              if (_cursor.isNull(_cursorIndexOfPaidAt)) {
                _tmpPaidAt = null;
              } else {
                _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
              }
              final String _tmpNotes;
              if (_cursor.isNull(_cursorIndexOfNotes)) {
                _tmpNotes = null;
              } else {
                _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
              }
              final int _tmpReminderLeadDays;
              _tmpReminderLeadDays = _cursor.getInt(_cursorIndexOfReminderLeadDays);
              final boolean _tmpIsDeleted;
              final int _tmp;
              _tmp = _cursor.getInt(_cursorIndexOfIsDeleted);
              _tmpIsDeleted = _tmp != 0;
              final Long _tmpDeletedAt;
              if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
                _tmpDeletedAt = null;
              } else {
                _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              _tmpDue = new DueEntity(_tmpId,_tmpTitle,_tmpAmountMinor,_tmpDueDate,_tmpDueTimeMinutes,_tmpRepeatInterval,_tmpStatus,_tmpCategoryId,_tmpNeedWasteType,_tmpPaidExpenseId,_tmpPaidAt,_tmpNotes,_tmpReminderLeadDays,_tmpIsDeleted,_tmpDeletedAt,_tmpCreatedAt,_tmpUpdatedAt);
              final CategoryEntity _tmpCategory;
              final Long _tmpKey_1;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpKey_1 = null;
              } else {
                _tmpKey_1 = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              if (_tmpKey_1 != null) {
                _tmpCategory = _collectionCategory.get(_tmpKey_1);
              } else {
                _tmpCategory = null;
              }
              _result = new DueWithCategoryEntity(_tmpDue,_tmpCategory);
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
      final Continuation<? super DueWithCategoryEntity> $completion) {
    final String _sql = "SELECT * FROM dues WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<DueWithCategoryEntity>() {
      @Override
      @Nullable
      public DueWithCategoryEntity call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
            final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
            final int _cursorIndexOfDueTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "due_time_minutes");
            final int _cursorIndexOfRepeatInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "repeat_interval");
            final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
            final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
            final int _cursorIndexOfNeedWasteType = CursorUtil.getColumnIndexOrThrow(_cursor, "need_waste_type");
            final int _cursorIndexOfPaidExpenseId = CursorUtil.getColumnIndexOrThrow(_cursor, "paid_expense_id");
            final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paid_at");
            final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
            final int _cursorIndexOfReminderLeadDays = CursorUtil.getColumnIndexOrThrow(_cursor, "reminder_lead_days");
            final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_deleted");
            final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
            final LongSparseArray<CategoryEntity> _collectionCategory = new LongSparseArray<CategoryEntity>();
            while (_cursor.moveToNext()) {
              final Long _tmpKey;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpKey = null;
              } else {
                _tmpKey = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              if (_tmpKey != null) {
                _collectionCategory.put(_tmpKey, null);
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcategoriesAscomSupernoveaSpendsenseDataLocalEntityCategoryEntity(_collectionCategory);
            final DueWithCategoryEntity _result;
            if (_cursor.moveToFirst()) {
              final DueEntity _tmpDue;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpTitle;
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
              final long _tmpAmountMinor;
              _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
              final long _tmpDueDate;
              _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
              final Integer _tmpDueTimeMinutes;
              if (_cursor.isNull(_cursorIndexOfDueTimeMinutes)) {
                _tmpDueTimeMinutes = null;
              } else {
                _tmpDueTimeMinutes = _cursor.getInt(_cursorIndexOfDueTimeMinutes);
              }
              final String _tmpRepeatInterval;
              _tmpRepeatInterval = _cursor.getString(_cursorIndexOfRepeatInterval);
              final String _tmpStatus;
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
              final Long _tmpCategoryId;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpCategoryId = null;
              } else {
                _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              final String _tmpNeedWasteType;
              _tmpNeedWasteType = _cursor.getString(_cursorIndexOfNeedWasteType);
              final Long _tmpPaidExpenseId;
              if (_cursor.isNull(_cursorIndexOfPaidExpenseId)) {
                _tmpPaidExpenseId = null;
              } else {
                _tmpPaidExpenseId = _cursor.getLong(_cursorIndexOfPaidExpenseId);
              }
              final Long _tmpPaidAt;
              if (_cursor.isNull(_cursorIndexOfPaidAt)) {
                _tmpPaidAt = null;
              } else {
                _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
              }
              final String _tmpNotes;
              if (_cursor.isNull(_cursorIndexOfNotes)) {
                _tmpNotes = null;
              } else {
                _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
              }
              final int _tmpReminderLeadDays;
              _tmpReminderLeadDays = _cursor.getInt(_cursorIndexOfReminderLeadDays);
              final boolean _tmpIsDeleted;
              final int _tmp;
              _tmp = _cursor.getInt(_cursorIndexOfIsDeleted);
              _tmpIsDeleted = _tmp != 0;
              final Long _tmpDeletedAt;
              if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
                _tmpDeletedAt = null;
              } else {
                _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              _tmpDue = new DueEntity(_tmpId,_tmpTitle,_tmpAmountMinor,_tmpDueDate,_tmpDueTimeMinutes,_tmpRepeatInterval,_tmpStatus,_tmpCategoryId,_tmpNeedWasteType,_tmpPaidExpenseId,_tmpPaidAt,_tmpNotes,_tmpReminderLeadDays,_tmpIsDeleted,_tmpDeletedAt,_tmpCreatedAt,_tmpUpdatedAt);
              final CategoryEntity _tmpCategory;
              final Long _tmpKey_1;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpKey_1 = null;
              } else {
                _tmpKey_1 = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              if (_tmpKey_1 != null) {
                _tmpCategory = _collectionCategory.get(_tmpKey_1);
              } else {
                _tmpCategory = null;
              }
              _result = new DueWithCategoryEntity(_tmpDue,_tmpCategory);
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
  public Object getPendingDues(
      final Continuation<? super List<DueWithCategoryEntity>> $completion) {
    final String _sql = "SELECT * FROM dues WHERE is_deleted = 0 AND deleted_at IS NULL AND status != 'PAID' AND status != 'SKIPPED'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<List<DueWithCategoryEntity>>() {
      @Override
      @NonNull
      public List<DueWithCategoryEntity> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
            final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
            final int _cursorIndexOfDueTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "due_time_minutes");
            final int _cursorIndexOfRepeatInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "repeat_interval");
            final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
            final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
            final int _cursorIndexOfNeedWasteType = CursorUtil.getColumnIndexOrThrow(_cursor, "need_waste_type");
            final int _cursorIndexOfPaidExpenseId = CursorUtil.getColumnIndexOrThrow(_cursor, "paid_expense_id");
            final int _cursorIndexOfPaidAt = CursorUtil.getColumnIndexOrThrow(_cursor, "paid_at");
            final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
            final int _cursorIndexOfReminderLeadDays = CursorUtil.getColumnIndexOrThrow(_cursor, "reminder_lead_days");
            final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_deleted");
            final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
            final LongSparseArray<CategoryEntity> _collectionCategory = new LongSparseArray<CategoryEntity>();
            while (_cursor.moveToNext()) {
              final Long _tmpKey;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpKey = null;
              } else {
                _tmpKey = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              if (_tmpKey != null) {
                _collectionCategory.put(_tmpKey, null);
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcategoriesAscomSupernoveaSpendsenseDataLocalEntityCategoryEntity(_collectionCategory);
            final List<DueWithCategoryEntity> _result = new ArrayList<DueWithCategoryEntity>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final DueWithCategoryEntity _item;
              final DueEntity _tmpDue;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpTitle;
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
              final long _tmpAmountMinor;
              _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
              final long _tmpDueDate;
              _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
              final Integer _tmpDueTimeMinutes;
              if (_cursor.isNull(_cursorIndexOfDueTimeMinutes)) {
                _tmpDueTimeMinutes = null;
              } else {
                _tmpDueTimeMinutes = _cursor.getInt(_cursorIndexOfDueTimeMinutes);
              }
              final String _tmpRepeatInterval;
              _tmpRepeatInterval = _cursor.getString(_cursorIndexOfRepeatInterval);
              final String _tmpStatus;
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
              final Long _tmpCategoryId;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpCategoryId = null;
              } else {
                _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              final String _tmpNeedWasteType;
              _tmpNeedWasteType = _cursor.getString(_cursorIndexOfNeedWasteType);
              final Long _tmpPaidExpenseId;
              if (_cursor.isNull(_cursorIndexOfPaidExpenseId)) {
                _tmpPaidExpenseId = null;
              } else {
                _tmpPaidExpenseId = _cursor.getLong(_cursorIndexOfPaidExpenseId);
              }
              final Long _tmpPaidAt;
              if (_cursor.isNull(_cursorIndexOfPaidAt)) {
                _tmpPaidAt = null;
              } else {
                _tmpPaidAt = _cursor.getLong(_cursorIndexOfPaidAt);
              }
              final String _tmpNotes;
              if (_cursor.isNull(_cursorIndexOfNotes)) {
                _tmpNotes = null;
              } else {
                _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
              }
              final int _tmpReminderLeadDays;
              _tmpReminderLeadDays = _cursor.getInt(_cursorIndexOfReminderLeadDays);
              final boolean _tmpIsDeleted;
              final int _tmp;
              _tmp = _cursor.getInt(_cursorIndexOfIsDeleted);
              _tmpIsDeleted = _tmp != 0;
              final Long _tmpDeletedAt;
              if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
                _tmpDeletedAt = null;
              } else {
                _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              _tmpDue = new DueEntity(_tmpId,_tmpTitle,_tmpAmountMinor,_tmpDueDate,_tmpDueTimeMinutes,_tmpRepeatInterval,_tmpStatus,_tmpCategoryId,_tmpNeedWasteType,_tmpPaidExpenseId,_tmpPaidAt,_tmpNotes,_tmpReminderLeadDays,_tmpIsDeleted,_tmpDeletedAt,_tmpCreatedAt,_tmpUpdatedAt);
              final CategoryEntity _tmpCategory;
              final Long _tmpKey_1;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpKey_1 = null;
              } else {
                _tmpKey_1 = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              if (_tmpKey_1 != null) {
                _tmpCategory = _collectionCategory.get(_tmpKey_1);
              } else {
                _tmpCategory = null;
              }
              _item = new DueWithCategoryEntity(_tmpDue,_tmpCategory);
              _result.add(_item);
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
  public Flow<Long> observePendingAmountBetween(final long startEpochDay, final long endEpochDay) {
    final String _sql = "SELECT COALESCE(SUM(amount_minor), 0) FROM dues WHERE is_deleted = 0 AND deleted_at IS NULL AND status != 'PAID' AND status != 'SKIPPED' AND due_date BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startEpochDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endEpochDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"dues"}, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final long _tmp;
            _tmp = _cursor.getLong(0);
            _result = _tmp;
          } else {
            _result = 0L;
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
  public Object countActive(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM dues WHERE is_deleted = 0 AND deleted_at IS NULL";
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
