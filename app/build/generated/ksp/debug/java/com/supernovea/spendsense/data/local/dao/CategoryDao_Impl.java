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
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.supernovea.spendsense.data.local.entity.CategoryEntity;
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
public final class CategoryDao_Impl implements CategoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CategoryEntity> __insertionAdapterOfCategoryEntity;

  private final EntityDeletionOrUpdateAdapter<CategoryEntity> __deletionAdapterOfCategoryEntity;

  private final EntityDeletionOrUpdateAdapter<CategoryEntity> __updateAdapterOfCategoryEntity;

  public CategoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCategoryEntity = new EntityInsertionAdapter<CategoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `categories` (`id`,`default_key`,`group_key`,`name`,`icon_key`,`color_hex`,`default_type`,`is_archived`,`is_default`,`sort_order`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CategoryEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getDefaultKey() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getDefaultKey());
        }
        statement.bindString(3, entity.getGroupKey());
        statement.bindString(4, entity.getName());
        statement.bindString(5, entity.getIconKey());
        statement.bindString(6, entity.getColorHex());
        statement.bindString(7, entity.getDefaultType());
        final int _tmp = entity.isArchived() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final int _tmp_1 = entity.isDefault() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        statement.bindLong(10, entity.getSortOrder());
      }
    };
    this.__deletionAdapterOfCategoryEntity = new EntityDeletionOrUpdateAdapter<CategoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `categories` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CategoryEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfCategoryEntity = new EntityDeletionOrUpdateAdapter<CategoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `categories` SET `id` = ?,`default_key` = ?,`group_key` = ?,`name` = ?,`icon_key` = ?,`color_hex` = ?,`default_type` = ?,`is_archived` = ?,`is_default` = ?,`sort_order` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CategoryEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getDefaultKey() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getDefaultKey());
        }
        statement.bindString(3, entity.getGroupKey());
        statement.bindString(4, entity.getName());
        statement.bindString(5, entity.getIconKey());
        statement.bindString(6, entity.getColorHex());
        statement.bindString(7, entity.getDefaultType());
        final int _tmp = entity.isArchived() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final int _tmp_1 = entity.isDefault() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        statement.bindLong(10, entity.getSortOrder());
        statement.bindLong(11, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final CategoryEntity category,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCategoryEntity.insertAndReturnId(category);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<CategoryEntity> categories,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfCategoryEntity.insertAndReturnIdsList(categories);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final CategoryEntity category,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfCategoryEntity.handle(category);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final CategoryEntity category,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCategoryEntity.handle(category);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CategoryEntity>> observeAll() {
    final String _sql = "SELECT * FROM categories ORDER BY sort_order ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"categories"}, new Callable<List<CategoryEntity>>() {
      @Override
      @NonNull
      public List<CategoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDefaultKey = CursorUtil.getColumnIndexOrThrow(_cursor, "default_key");
          final int _cursorIndexOfGroupKey = CursorUtil.getColumnIndexOrThrow(_cursor, "group_key");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIconKey = CursorUtil.getColumnIndexOrThrow(_cursor, "icon_key");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "color_hex");
          final int _cursorIndexOfDefaultType = CursorUtil.getColumnIndexOrThrow(_cursor, "default_type");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfIsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "is_default");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sort_order");
          final List<CategoryEntity> _result = new ArrayList<CategoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CategoryEntity _item;
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
            _item = new CategoryEntity(_tmpId,_tmpDefaultKey,_tmpGroupKey,_tmpName,_tmpIconKey,_tmpColorHex,_tmpDefaultType,_tmpIsArchived,_tmpIsDefault,_tmpSortOrder);
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
  public Flow<List<CategoryEntity>> observeActive() {
    final String _sql = "SELECT * FROM categories WHERE is_archived = 0 ORDER BY sort_order ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"categories"}, new Callable<List<CategoryEntity>>() {
      @Override
      @NonNull
      public List<CategoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDefaultKey = CursorUtil.getColumnIndexOrThrow(_cursor, "default_key");
          final int _cursorIndexOfGroupKey = CursorUtil.getColumnIndexOrThrow(_cursor, "group_key");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIconKey = CursorUtil.getColumnIndexOrThrow(_cursor, "icon_key");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "color_hex");
          final int _cursorIndexOfDefaultType = CursorUtil.getColumnIndexOrThrow(_cursor, "default_type");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfIsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "is_default");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sort_order");
          final List<CategoryEntity> _result = new ArrayList<CategoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CategoryEntity _item;
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
            _item = new CategoryEntity(_tmpId,_tmpDefaultKey,_tmpGroupKey,_tmpName,_tmpIconKey,_tmpColorHex,_tmpDefaultType,_tmpIsArchived,_tmpIsDefault,_tmpSortOrder);
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
  public Object getById(final long id, final Continuation<? super CategoryEntity> $completion) {
    final String _sql = "SELECT * FROM categories WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CategoryEntity>() {
      @Override
      @Nullable
      public CategoryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDefaultKey = CursorUtil.getColumnIndexOrThrow(_cursor, "default_key");
          final int _cursorIndexOfGroupKey = CursorUtil.getColumnIndexOrThrow(_cursor, "group_key");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIconKey = CursorUtil.getColumnIndexOrThrow(_cursor, "icon_key");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "color_hex");
          final int _cursorIndexOfDefaultType = CursorUtil.getColumnIndexOrThrow(_cursor, "default_type");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfIsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "is_default");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sort_order");
          final CategoryEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new CategoryEntity(_tmpId,_tmpDefaultKey,_tmpGroupKey,_tmpName,_tmpIconKey,_tmpColorHex,_tmpDefaultType,_tmpIsArchived,_tmpIsDefault,_tmpSortOrder);
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
      final Continuation<? super CategoryEntity> $completion) {
    final String _sql = "SELECT * FROM categories WHERE name = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, name);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CategoryEntity>() {
      @Override
      @Nullable
      public CategoryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDefaultKey = CursorUtil.getColumnIndexOrThrow(_cursor, "default_key");
          final int _cursorIndexOfGroupKey = CursorUtil.getColumnIndexOrThrow(_cursor, "group_key");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIconKey = CursorUtil.getColumnIndexOrThrow(_cursor, "icon_key");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "color_hex");
          final int _cursorIndexOfDefaultType = CursorUtil.getColumnIndexOrThrow(_cursor, "default_type");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfIsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "is_default");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sort_order");
          final CategoryEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new CategoryEntity(_tmpId,_tmpDefaultKey,_tmpGroupKey,_tmpName,_tmpIconKey,_tmpColorHex,_tmpDefaultType,_tmpIsArchived,_tmpIsDefault,_tmpSortOrder);
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
      final Continuation<? super CategoryEntity> $completion) {
    final String _sql = "SELECT * FROM categories WHERE default_key = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, defaultKey);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CategoryEntity>() {
      @Override
      @Nullable
      public CategoryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDefaultKey = CursorUtil.getColumnIndexOrThrow(_cursor, "default_key");
          final int _cursorIndexOfGroupKey = CursorUtil.getColumnIndexOrThrow(_cursor, "group_key");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIconKey = CursorUtil.getColumnIndexOrThrow(_cursor, "icon_key");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "color_hex");
          final int _cursorIndexOfDefaultType = CursorUtil.getColumnIndexOrThrow(_cursor, "default_type");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfIsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "is_default");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sort_order");
          final CategoryEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new CategoryEntity(_tmpId,_tmpDefaultKey,_tmpGroupKey,_tmpName,_tmpIconKey,_tmpColorHex,_tmpDefaultType,_tmpIsArchived,_tmpIsDefault,_tmpSortOrder);
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
    final String _sql = "SELECT COUNT(*) FROM categories";
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
