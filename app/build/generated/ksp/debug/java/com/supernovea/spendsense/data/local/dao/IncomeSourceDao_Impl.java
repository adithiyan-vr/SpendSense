package com.supernovea.spendsense.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.supernovea.spendsense.data.local.entity.IncomeSourceEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class IncomeSourceDao_Impl implements IncomeSourceDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<IncomeSourceEntity> __insertionAdapterOfIncomeSourceEntity;

  private final EntityInsertionAdapter<IncomeSourceEntity> __insertionAdapterOfIncomeSourceEntity_1;

  private final EntityDeletionOrUpdateAdapter<IncomeSourceEntity> __deletionAdapterOfIncomeSourceEntity;

  private final EntityDeletionOrUpdateAdapter<IncomeSourceEntity> __updateAdapterOfIncomeSourceEntity;

  private final SharedSQLiteStatement __preparedStmtOfSetArchived;

  public IncomeSourceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfIncomeSourceEntity = new EntityInsertionAdapter<IncomeSourceEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `income_sources` (`id`,`default_key`,`name`,`icon_key`,`color_hex`,`is_archived`,`is_default`,`sort_order`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncomeSourceEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getDefaultKey() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getDefaultKey());
        }
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getIconKey());
        statement.bindString(5, entity.getColorHex());
        final int _tmp = entity.isArchived() ? 1 : 0;
        statement.bindLong(6, _tmp);
        final int _tmp_1 = entity.isDefault() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        statement.bindLong(8, entity.getSortOrder());
      }
    };
    this.__insertionAdapterOfIncomeSourceEntity_1 = new EntityInsertionAdapter<IncomeSourceEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `income_sources` (`id`,`default_key`,`name`,`icon_key`,`color_hex`,`is_archived`,`is_default`,`sort_order`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncomeSourceEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getDefaultKey() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getDefaultKey());
        }
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getIconKey());
        statement.bindString(5, entity.getColorHex());
        final int _tmp = entity.isArchived() ? 1 : 0;
        statement.bindLong(6, _tmp);
        final int _tmp_1 = entity.isDefault() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        statement.bindLong(8, entity.getSortOrder());
      }
    };
    this.__deletionAdapterOfIncomeSourceEntity = new EntityDeletionOrUpdateAdapter<IncomeSourceEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `income_sources` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncomeSourceEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfIncomeSourceEntity = new EntityDeletionOrUpdateAdapter<IncomeSourceEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `income_sources` SET `id` = ?,`default_key` = ?,`name` = ?,`icon_key` = ?,`color_hex` = ?,`is_archived` = ?,`is_default` = ?,`sort_order` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncomeSourceEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getDefaultKey() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getDefaultKey());
        }
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getIconKey());
        statement.bindString(5, entity.getColorHex());
        final int _tmp = entity.isArchived() ? 1 : 0;
        statement.bindLong(6, _tmp);
        final int _tmp_1 = entity.isDefault() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        statement.bindLong(8, entity.getSortOrder());
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfSetArchived = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE income_sources SET is_archived = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final IncomeSourceEntity source,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfIncomeSourceEntity.insertAndReturnId(source);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<IncomeSourceEntity> sources,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfIncomeSourceEntity_1.insertAndReturnIdsList(sources);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final IncomeSourceEntity source,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfIncomeSourceEntity.handle(source);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final IncomeSourceEntity source,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfIncomeSourceEntity.handle(source);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object setArchived(final long id, final boolean isArchived,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetArchived.acquire();
        int _argIndex = 1;
        final int _tmp = isArchived ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
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
          __preparedStmtOfSetArchived.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<IncomeSourceEntity>> observeAll() {
    final String _sql = "SELECT * FROM income_sources ORDER BY sort_order ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"income_sources"}, new Callable<List<IncomeSourceEntity>>() {
      @Override
      @NonNull
      public List<IncomeSourceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDefaultKey = CursorUtil.getColumnIndexOrThrow(_cursor, "default_key");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIconKey = CursorUtil.getColumnIndexOrThrow(_cursor, "icon_key");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "color_hex");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfIsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "is_default");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sort_order");
          final List<IncomeSourceEntity> _result = new ArrayList<IncomeSourceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncomeSourceEntity _item;
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
            _item = new IncomeSourceEntity(_tmpId,_tmpDefaultKey,_tmpName,_tmpIconKey,_tmpColorHex,_tmpIsArchived,_tmpIsDefault,_tmpSortOrder);
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
  public Flow<List<IncomeSourceEntity>> observeActive() {
    final String _sql = "SELECT * FROM income_sources WHERE is_archived = 0 ORDER BY sort_order ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"income_sources"}, new Callable<List<IncomeSourceEntity>>() {
      @Override
      @NonNull
      public List<IncomeSourceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDefaultKey = CursorUtil.getColumnIndexOrThrow(_cursor, "default_key");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIconKey = CursorUtil.getColumnIndexOrThrow(_cursor, "icon_key");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "color_hex");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfIsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "is_default");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sort_order");
          final List<IncomeSourceEntity> _result = new ArrayList<IncomeSourceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncomeSourceEntity _item;
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
            _item = new IncomeSourceEntity(_tmpId,_tmpDefaultKey,_tmpName,_tmpIconKey,_tmpColorHex,_tmpIsArchived,_tmpIsDefault,_tmpSortOrder);
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
  public Object getById(final long id, final Continuation<? super IncomeSourceEntity> $completion) {
    final String _sql = "SELECT * FROM income_sources WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<IncomeSourceEntity>() {
      @Override
      @Nullable
      public IncomeSourceEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDefaultKey = CursorUtil.getColumnIndexOrThrow(_cursor, "default_key");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIconKey = CursorUtil.getColumnIndexOrThrow(_cursor, "icon_key");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "color_hex");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfIsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "is_default");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sort_order");
          final IncomeSourceEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new IncomeSourceEntity(_tmpId,_tmpDefaultKey,_tmpName,_tmpIconKey,_tmpColorHex,_tmpIsArchived,_tmpIsDefault,_tmpSortOrder);
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
  public Object getByDefaultKey(final String defaultKey,
      final Continuation<? super IncomeSourceEntity> $completion) {
    final String _sql = "SELECT * FROM income_sources WHERE default_key = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, defaultKey);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<IncomeSourceEntity>() {
      @Override
      @Nullable
      public IncomeSourceEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDefaultKey = CursorUtil.getColumnIndexOrThrow(_cursor, "default_key");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIconKey = CursorUtil.getColumnIndexOrThrow(_cursor, "icon_key");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "color_hex");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfIsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "is_default");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sort_order");
          final IncomeSourceEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new IncomeSourceEntity(_tmpId,_tmpDefaultKey,_tmpName,_tmpIconKey,_tmpColorHex,_tmpIsArchived,_tmpIsDefault,_tmpSortOrder);
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
  public Object getByName(final String name,
      final Continuation<? super IncomeSourceEntity> $completion) {
    final String _sql = "SELECT * FROM income_sources WHERE name = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, name);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<IncomeSourceEntity>() {
      @Override
      @Nullable
      public IncomeSourceEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDefaultKey = CursorUtil.getColumnIndexOrThrow(_cursor, "default_key");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIconKey = CursorUtil.getColumnIndexOrThrow(_cursor, "icon_key");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "color_hex");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfIsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "is_default");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sort_order");
          final IncomeSourceEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new IncomeSourceEntity(_tmpId,_tmpDefaultKey,_tmpName,_tmpIconKey,_tmpColorHex,_tmpIsArchived,_tmpIsDefault,_tmpSortOrder);
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
  public Object count(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM income_sources";
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
}
