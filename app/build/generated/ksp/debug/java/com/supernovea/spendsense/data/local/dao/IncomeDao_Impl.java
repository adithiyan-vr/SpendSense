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
import com.supernovea.spendsense.data.local.entity.IncomeEntity;
import com.supernovea.spendsense.data.local.entity.IncomeSourceEntity;
import com.supernovea.spendsense.data.local.entity.IncomeWithSourceEntity;
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
public final class IncomeDao_Impl implements IncomeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<IncomeEntity> __insertionAdapterOfIncomeEntity;

  private final EntityDeletionOrUpdateAdapter<IncomeEntity> __updateAdapterOfIncomeEntity;

  private final SharedSQLiteStatement __preparedStmtOfSoftDelete;

  private final SharedSQLiteStatement __preparedStmtOfRestore;

  private final SharedSQLiteStatement __preparedStmtOfHardDelete;

  private final SharedSQLiteStatement __preparedStmtOfPurgeDeletedBefore;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public IncomeDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfIncomeEntity = new EntityInsertionAdapter<IncomeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `incomes` (`id`,`amount_minor`,`source_id`,`note`,`received_at`,`received_date`,`created_at`,`updated_at`,`deleted_at`,`recurring_rule_id`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncomeEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAmountMinor());
        statement.bindLong(3, entity.getSourceId());
        if (entity.getNote() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getNote());
        }
        statement.bindLong(5, entity.getReceivedAt());
        statement.bindLong(6, entity.getReceivedDate());
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getUpdatedAt());
        if (entity.getDeletedAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getDeletedAt());
        }
        if (entity.getRecurringRuleId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getRecurringRuleId());
        }
      }
    };
    this.__updateAdapterOfIncomeEntity = new EntityDeletionOrUpdateAdapter<IncomeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `incomes` SET `id` = ?,`amount_minor` = ?,`source_id` = ?,`note` = ?,`received_at` = ?,`received_date` = ?,`created_at` = ?,`updated_at` = ?,`deleted_at` = ?,`recurring_rule_id` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncomeEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAmountMinor());
        statement.bindLong(3, entity.getSourceId());
        if (entity.getNote() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getNote());
        }
        statement.bindLong(5, entity.getReceivedAt());
        statement.bindLong(6, entity.getReceivedDate());
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getUpdatedAt());
        if (entity.getDeletedAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getDeletedAt());
        }
        if (entity.getRecurringRuleId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getRecurringRuleId());
        }
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfSoftDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE incomes SET deleted_at = ?, updated_at = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRestore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE incomes SET deleted_at = NULL, updated_at = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfHardDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM incomes WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfPurgeDeletedBefore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM incomes WHERE deleted_at IS NOT NULL AND deleted_at < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM incomes";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final IncomeEntity income, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfIncomeEntity.insertAndReturnId(income);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<IncomeEntity> incomes,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfIncomeEntity.insertAndReturnIdsList(incomes);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final IncomeEntity income, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfIncomeEntity.handle(income);
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
  public Flow<List<IncomeWithSourceEntity>> observeBetween(final long startDay, final long endDay) {
    final String _sql = "SELECT * FROM incomes WHERE deleted_at IS NULL AND received_date BETWEEN ? AND ? ORDER BY received_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDay);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"income_sources",
        "incomes"}, new Callable<List<IncomeWithSourceEntity>>() {
      @Override
      @NonNull
      public List<IncomeWithSourceEntity> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
            final int _cursorIndexOfSourceId = CursorUtil.getColumnIndexOrThrow(_cursor, "source_id");
            final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
            final int _cursorIndexOfReceivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "received_at");
            final int _cursorIndexOfReceivedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "received_date");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
            final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
            final int _cursorIndexOfRecurringRuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "recurring_rule_id");
            final LongSparseArray<IncomeSourceEntity> _collectionSource = new LongSparseArray<IncomeSourceEntity>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfSourceId);
              _collectionSource.put(_tmpKey, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipincomeSourcesAscomSupernoveaSpendsenseDataLocalEntityIncomeSourceEntity(_collectionSource);
            final List<IncomeWithSourceEntity> _result = new ArrayList<IncomeWithSourceEntity>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final IncomeWithSourceEntity _item;
              final IncomeEntity _tmpIncome;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final long _tmpAmountMinor;
              _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
              final long _tmpSourceId;
              _tmpSourceId = _cursor.getLong(_cursorIndexOfSourceId);
              final String _tmpNote;
              if (_cursor.isNull(_cursorIndexOfNote)) {
                _tmpNote = null;
              } else {
                _tmpNote = _cursor.getString(_cursorIndexOfNote);
              }
              final long _tmpReceivedAt;
              _tmpReceivedAt = _cursor.getLong(_cursorIndexOfReceivedAt);
              final long _tmpReceivedDate;
              _tmpReceivedDate = _cursor.getLong(_cursorIndexOfReceivedDate);
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
              final Long _tmpRecurringRuleId;
              if (_cursor.isNull(_cursorIndexOfRecurringRuleId)) {
                _tmpRecurringRuleId = null;
              } else {
                _tmpRecurringRuleId = _cursor.getLong(_cursorIndexOfRecurringRuleId);
              }
              _tmpIncome = new IncomeEntity(_tmpId,_tmpAmountMinor,_tmpSourceId,_tmpNote,_tmpReceivedAt,_tmpReceivedDate,_tmpCreatedAt,_tmpUpdatedAt,_tmpDeletedAt,_tmpRecurringRuleId);
              final IncomeSourceEntity _tmpSource;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfSourceId);
              _tmpSource = _collectionSource.get(_tmpKey_1);
              _item = new IncomeWithSourceEntity(_tmpIncome,_tmpSource);
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
  public Flow<List<IncomeWithSourceEntity>> observeAll() {
    final String _sql = "SELECT * FROM incomes WHERE deleted_at IS NULL ORDER BY received_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"income_sources",
        "incomes"}, new Callable<List<IncomeWithSourceEntity>>() {
      @Override
      @NonNull
      public List<IncomeWithSourceEntity> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
            final int _cursorIndexOfSourceId = CursorUtil.getColumnIndexOrThrow(_cursor, "source_id");
            final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
            final int _cursorIndexOfReceivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "received_at");
            final int _cursorIndexOfReceivedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "received_date");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
            final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
            final int _cursorIndexOfRecurringRuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "recurring_rule_id");
            final LongSparseArray<IncomeSourceEntity> _collectionSource = new LongSparseArray<IncomeSourceEntity>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfSourceId);
              _collectionSource.put(_tmpKey, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipincomeSourcesAscomSupernoveaSpendsenseDataLocalEntityIncomeSourceEntity(_collectionSource);
            final List<IncomeWithSourceEntity> _result = new ArrayList<IncomeWithSourceEntity>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final IncomeWithSourceEntity _item;
              final IncomeEntity _tmpIncome;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final long _tmpAmountMinor;
              _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
              final long _tmpSourceId;
              _tmpSourceId = _cursor.getLong(_cursorIndexOfSourceId);
              final String _tmpNote;
              if (_cursor.isNull(_cursorIndexOfNote)) {
                _tmpNote = null;
              } else {
                _tmpNote = _cursor.getString(_cursorIndexOfNote);
              }
              final long _tmpReceivedAt;
              _tmpReceivedAt = _cursor.getLong(_cursorIndexOfReceivedAt);
              final long _tmpReceivedDate;
              _tmpReceivedDate = _cursor.getLong(_cursorIndexOfReceivedDate);
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
              final Long _tmpRecurringRuleId;
              if (_cursor.isNull(_cursorIndexOfRecurringRuleId)) {
                _tmpRecurringRuleId = null;
              } else {
                _tmpRecurringRuleId = _cursor.getLong(_cursorIndexOfRecurringRuleId);
              }
              _tmpIncome = new IncomeEntity(_tmpId,_tmpAmountMinor,_tmpSourceId,_tmpNote,_tmpReceivedAt,_tmpReceivedDate,_tmpCreatedAt,_tmpUpdatedAt,_tmpDeletedAt,_tmpRecurringRuleId);
              final IncomeSourceEntity _tmpSource;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfSourceId);
              _tmpSource = _collectionSource.get(_tmpKey_1);
              _item = new IncomeWithSourceEntity(_tmpIncome,_tmpSource);
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
  public Flow<IncomeWithSourceEntity> observeById(final long id) {
    final String _sql = "SELECT * FROM incomes WHERE id = ? AND deleted_at IS NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"income_sources",
        "incomes"}, new Callable<IncomeWithSourceEntity>() {
      @Override
      @Nullable
      public IncomeWithSourceEntity call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
            final int _cursorIndexOfSourceId = CursorUtil.getColumnIndexOrThrow(_cursor, "source_id");
            final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
            final int _cursorIndexOfReceivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "received_at");
            final int _cursorIndexOfReceivedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "received_date");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
            final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
            final int _cursorIndexOfRecurringRuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "recurring_rule_id");
            final LongSparseArray<IncomeSourceEntity> _collectionSource = new LongSparseArray<IncomeSourceEntity>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfSourceId);
              _collectionSource.put(_tmpKey, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipincomeSourcesAscomSupernoveaSpendsenseDataLocalEntityIncomeSourceEntity(_collectionSource);
            final IncomeWithSourceEntity _result;
            if (_cursor.moveToFirst()) {
              final IncomeEntity _tmpIncome;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final long _tmpAmountMinor;
              _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
              final long _tmpSourceId;
              _tmpSourceId = _cursor.getLong(_cursorIndexOfSourceId);
              final String _tmpNote;
              if (_cursor.isNull(_cursorIndexOfNote)) {
                _tmpNote = null;
              } else {
                _tmpNote = _cursor.getString(_cursorIndexOfNote);
              }
              final long _tmpReceivedAt;
              _tmpReceivedAt = _cursor.getLong(_cursorIndexOfReceivedAt);
              final long _tmpReceivedDate;
              _tmpReceivedDate = _cursor.getLong(_cursorIndexOfReceivedDate);
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
              final Long _tmpRecurringRuleId;
              if (_cursor.isNull(_cursorIndexOfRecurringRuleId)) {
                _tmpRecurringRuleId = null;
              } else {
                _tmpRecurringRuleId = _cursor.getLong(_cursorIndexOfRecurringRuleId);
              }
              _tmpIncome = new IncomeEntity(_tmpId,_tmpAmountMinor,_tmpSourceId,_tmpNote,_tmpReceivedAt,_tmpReceivedDate,_tmpCreatedAt,_tmpUpdatedAt,_tmpDeletedAt,_tmpRecurringRuleId);
              final IncomeSourceEntity _tmpSource;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfSourceId);
              _tmpSource = _collectionSource.get(_tmpKey_1);
              _result = new IncomeWithSourceEntity(_tmpIncome,_tmpSource);
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
      final Continuation<? super IncomeWithSourceEntity> $completion) {
    final String _sql = "SELECT * FROM incomes WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<IncomeWithSourceEntity>() {
      @Override
      @Nullable
      public IncomeWithSourceEntity call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
            final int _cursorIndexOfSourceId = CursorUtil.getColumnIndexOrThrow(_cursor, "source_id");
            final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
            final int _cursorIndexOfReceivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "received_at");
            final int _cursorIndexOfReceivedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "received_date");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
            final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deleted_at");
            final int _cursorIndexOfRecurringRuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "recurring_rule_id");
            final LongSparseArray<IncomeSourceEntity> _collectionSource = new LongSparseArray<IncomeSourceEntity>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfSourceId);
              _collectionSource.put(_tmpKey, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipincomeSourcesAscomSupernoveaSpendsenseDataLocalEntityIncomeSourceEntity(_collectionSource);
            final IncomeWithSourceEntity _result;
            if (_cursor.moveToFirst()) {
              final IncomeEntity _tmpIncome;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final long _tmpAmountMinor;
              _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
              final long _tmpSourceId;
              _tmpSourceId = _cursor.getLong(_cursorIndexOfSourceId);
              final String _tmpNote;
              if (_cursor.isNull(_cursorIndexOfNote)) {
                _tmpNote = null;
              } else {
                _tmpNote = _cursor.getString(_cursorIndexOfNote);
              }
              final long _tmpReceivedAt;
              _tmpReceivedAt = _cursor.getLong(_cursorIndexOfReceivedAt);
              final long _tmpReceivedDate;
              _tmpReceivedDate = _cursor.getLong(_cursorIndexOfReceivedDate);
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
              final Long _tmpRecurringRuleId;
              if (_cursor.isNull(_cursorIndexOfRecurringRuleId)) {
                _tmpRecurringRuleId = null;
              } else {
                _tmpRecurringRuleId = _cursor.getLong(_cursorIndexOfRecurringRuleId);
              }
              _tmpIncome = new IncomeEntity(_tmpId,_tmpAmountMinor,_tmpSourceId,_tmpNote,_tmpReceivedAt,_tmpReceivedDate,_tmpCreatedAt,_tmpUpdatedAt,_tmpDeletedAt,_tmpRecurringRuleId);
              final IncomeSourceEntity _tmpSource;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfSourceId);
              _tmpSource = _collectionSource.get(_tmpKey_1);
              _result = new IncomeWithSourceEntity(_tmpIncome,_tmpSource);
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
  public Flow<Long> observeTotalBetween(final long startDay, final long endDay) {
    final String _sql = "SELECT COALESCE(SUM(amount_minor), 0) FROM incomes WHERE deleted_at IS NULL AND received_date BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"incomes"}, new Callable<Long>() {
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
  public Flow<Long> observeTotalAllTime() {
    final String _sql = "SELECT COALESCE(SUM(amount_minor), 0) FROM incomes WHERE deleted_at IS NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"incomes"}, new Callable<Long>() {
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
  public Flow<List<SourceSumTuple>> observeSumBySourceBetween(final long startDay,
      final long endDay) {
    final String _sql = "SELECT source_id as sourceId, COALESCE(SUM(amount_minor), 0) as totalMinor FROM incomes WHERE deleted_at IS NULL AND received_date BETWEEN ? AND ? GROUP BY source_id";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"incomes"}, new Callable<List<SourceSumTuple>>() {
      @Override
      @NonNull
      public List<SourceSumTuple> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSourceId = 0;
          final int _cursorIndexOfTotalMinor = 1;
          final List<SourceSumTuple> _result = new ArrayList<SourceSumTuple>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SourceSumTuple _item;
            final long _tmpSourceId;
            _tmpSourceId = _cursor.getLong(_cursorIndexOfSourceId);
            final long _tmpTotalMinor;
            _tmpTotalMinor = _cursor.getLong(_cursorIndexOfTotalMinor);
            _item = new SourceSumTuple(_tmpSourceId,_tmpTotalMinor);
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
    final String _sql = "SELECT COUNT(*) FROM incomes WHERE deleted_at IS NULL";
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

  private void __fetchRelationshipincomeSourcesAscomSupernoveaSpendsenseDataLocalEntityIncomeSourceEntity(
      @NonNull final LongSparseArray<IncomeSourceEntity> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, false, (map) -> {
        __fetchRelationshipincomeSourcesAscomSupernoveaSpendsenseDataLocalEntityIncomeSourceEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`default_key`,`name`,`icon_key`,`color_hex`,`is_archived`,`is_default`,`sort_order` FROM `income_sources` WHERE `id` IN (");
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
      final int _cursorIndexOfName = 2;
      final int _cursorIndexOfIconKey = 3;
      final int _cursorIndexOfColorHex = 4;
      final int _cursorIndexOfIsArchived = 5;
      final int _cursorIndexOfIsDefault = 6;
      final int _cursorIndexOfSortOrder = 7;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        if (_map.containsKey(_tmpKey)) {
          final IncomeSourceEntity _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final String _tmpDefaultKey;
          if (_cursor.isNull(_cursorIndexOfDefaultKey)) {
            _tmpDefaultKey = null;
          } else {
            _tmpDefaultKey = _cursor.getString(_cursorIndexOfDefaultKey);
          }
          final String _tmpName;
          _tmpName = _cursor.getString(_cursorIndexOfName);
          final String _tmpIconKey;
          _tmpIconKey = _cursor.getString(_cursorIndexOfIconKey);
          final String _tmpColorHex;
          _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex);
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
          _item_1 = new IncomeSourceEntity(_tmpId,_tmpDefaultKey,_tmpName,_tmpIconKey,_tmpColorHex,_tmpIsArchived,_tmpIsDefault,_tmpSortOrder);
          _map.put(_tmpKey, _item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
