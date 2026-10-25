package com.supernovea.spendsense.data.local.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.supernovea.spendsense.data.local.dao.BudgetDao;
import com.supernovea.spendsense.data.local.dao.BudgetDao_Impl;
import com.supernovea.spendsense.data.local.dao.CategoryDao;
import com.supernovea.spendsense.data.local.dao.CategoryDao_Impl;
import com.supernovea.spendsense.data.local.dao.DueDao;
import com.supernovea.spendsense.data.local.dao.DueDao_Impl;
import com.supernovea.spendsense.data.local.dao.ExpenseDao;
import com.supernovea.spendsense.data.local.dao.ExpenseDao_Impl;
import com.supernovea.spendsense.data.local.dao.IncomeDao;
import com.supernovea.spendsense.data.local.dao.IncomeDao_Impl;
import com.supernovea.spendsense.data.local.dao.IncomeSourceDao;
import com.supernovea.spendsense.data.local.dao.IncomeSourceDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile ExpenseDao _expenseDao;

  private volatile CategoryDao _categoryDao;

  private volatile BudgetDao _budgetDao;

  private volatile IncomeSourceDao _incomeSourceDao;

  private volatile IncomeDao _incomeDao;

  private volatile DueDao _dueDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `expenses` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `amount_minor` INTEGER NOT NULL, `type` TEXT NOT NULL, `category_id` INTEGER NOT NULL, `note` TEXT, `occurred_at` INTEGER NOT NULL, `occurred_date` INTEGER NOT NULL, `payment_method` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `deleted_at` INTEGER, FOREIGN KEY(`category_id`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_category_id` ON `expenses` (`category_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_occurred_date` ON `expenses` (`occurred_date`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_type_occurred_date` ON `expenses` (`type`, `occurred_date`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_category_id_occurred_date` ON `expenses` (`category_id`, `occurred_date`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `categories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `default_key` TEXT, `group_key` TEXT NOT NULL DEFAULT 'other', `name` TEXT NOT NULL, `icon_key` TEXT NOT NULL, `color_hex` TEXT NOT NULL, `default_type` TEXT NOT NULL, `is_archived` INTEGER NOT NULL DEFAULT 0, `is_default` INTEGER NOT NULL DEFAULT 0, `sort_order` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_categories_name` ON `categories` (`name`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_categories_default_key` ON `categories` (`default_key`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `budgets` (`month_key` TEXT NOT NULL, `total_limit_minor` INTEGER NOT NULL, `waste_limit_minor` INTEGER, PRIMARY KEY(`month_key`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `income_sources` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `default_key` TEXT, `name` TEXT NOT NULL, `icon_key` TEXT NOT NULL, `color_hex` TEXT NOT NULL, `is_archived` INTEGER NOT NULL DEFAULT 0, `is_default` INTEGER NOT NULL DEFAULT 0, `sort_order` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_income_sources_name` ON `income_sources` (`name`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_income_sources_default_key` ON `income_sources` (`default_key`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `incomes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `amount_minor` INTEGER NOT NULL, `source_id` INTEGER NOT NULL, `note` TEXT, `received_at` INTEGER NOT NULL, `received_date` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `deleted_at` INTEGER, `recurring_rule_id` INTEGER, FOREIGN KEY(`source_id`) REFERENCES `income_sources`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_incomes_source_id` ON `incomes` (`source_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_incomes_received_date` ON `incomes` (`received_date`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_incomes_source_id_received_date` ON `incomes` (`source_id`, `received_date`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `dues` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `amount_minor` INTEGER NOT NULL, `due_date` INTEGER NOT NULL, `due_time_minutes` INTEGER, `repeat_interval` TEXT NOT NULL, `status` TEXT NOT NULL, `category_id` INTEGER, `need_waste_type` TEXT NOT NULL, `paid_expense_id` INTEGER, `paid_at` INTEGER, `notes` TEXT, `reminder_lead_days` INTEGER NOT NULL DEFAULT 3, `is_deleted` INTEGER NOT NULL DEFAULT 0, `deleted_at` INTEGER, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, FOREIGN KEY(`category_id`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL , FOREIGN KEY(`paid_expense_id`) REFERENCES `expenses`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_dues_category_id` ON `dues` (`category_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_dues_due_date` ON `dues` (`due_date`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_dues_status` ON `dues` (`status`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_dues_paid_expense_id` ON `dues` (`paid_expense_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_dues_status_due_date` ON `dues` (`status`, `due_date`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '36b7ea5fcd7701b6d6c4e874b8651134')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `expenses`");
        db.execSQL("DROP TABLE IF EXISTS `categories`");
        db.execSQL("DROP TABLE IF EXISTS `budgets`");
        db.execSQL("DROP TABLE IF EXISTS `income_sources`");
        db.execSQL("DROP TABLE IF EXISTS `incomes`");
        db.execSQL("DROP TABLE IF EXISTS `dues`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsExpenses = new HashMap<String, TableInfo.Column>(11);
        _columnsExpenses.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("amount_minor", new TableInfo.Column("amount_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("category_id", new TableInfo.Column("category_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("occurred_at", new TableInfo.Column("occurred_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("occurred_date", new TableInfo.Column("occurred_date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("payment_method", new TableInfo.Column("payment_method", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("deleted_at", new TableInfo.Column("deleted_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExpenses = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysExpenses.add(new TableInfo.ForeignKey("categories", "RESTRICT", "NO ACTION", Arrays.asList("category_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesExpenses = new HashSet<TableInfo.Index>(4);
        _indicesExpenses.add(new TableInfo.Index("index_expenses_category_id", false, Arrays.asList("category_id"), Arrays.asList("ASC")));
        _indicesExpenses.add(new TableInfo.Index("index_expenses_occurred_date", false, Arrays.asList("occurred_date"), Arrays.asList("ASC")));
        _indicesExpenses.add(new TableInfo.Index("index_expenses_type_occurred_date", false, Arrays.asList("type", "occurred_date"), Arrays.asList("ASC", "ASC")));
        _indicesExpenses.add(new TableInfo.Index("index_expenses_category_id_occurred_date", false, Arrays.asList("category_id", "occurred_date"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoExpenses = new TableInfo("expenses", _columnsExpenses, _foreignKeysExpenses, _indicesExpenses);
        final TableInfo _existingExpenses = TableInfo.read(db, "expenses");
        if (!_infoExpenses.equals(_existingExpenses)) {
          return new RoomOpenHelper.ValidationResult(false, "expenses(com.supernovea.spendsense.data.local.entity.ExpenseEntity).\n"
                  + " Expected:\n" + _infoExpenses + "\n"
                  + " Found:\n" + _existingExpenses);
        }
        final HashMap<String, TableInfo.Column> _columnsCategories = new HashMap<String, TableInfo.Column>(10);
        _columnsCategories.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("default_key", new TableInfo.Column("default_key", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("group_key", new TableInfo.Column("group_key", "TEXT", true, 0, "'other'", TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("icon_key", new TableInfo.Column("icon_key", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("color_hex", new TableInfo.Column("color_hex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("default_type", new TableInfo.Column("default_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("is_archived", new TableInfo.Column("is_archived", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("is_default", new TableInfo.Column("is_default", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("sort_order", new TableInfo.Column("sort_order", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCategories = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCategories = new HashSet<TableInfo.Index>(2);
        _indicesCategories.add(new TableInfo.Index("index_categories_name", true, Arrays.asList("name"), Arrays.asList("ASC")));
        _indicesCategories.add(new TableInfo.Index("index_categories_default_key", true, Arrays.asList("default_key"), Arrays.asList("ASC")));
        final TableInfo _infoCategories = new TableInfo("categories", _columnsCategories, _foreignKeysCategories, _indicesCategories);
        final TableInfo _existingCategories = TableInfo.read(db, "categories");
        if (!_infoCategories.equals(_existingCategories)) {
          return new RoomOpenHelper.ValidationResult(false, "categories(com.supernovea.spendsense.data.local.entity.CategoryEntity).\n"
                  + " Expected:\n" + _infoCategories + "\n"
                  + " Found:\n" + _existingCategories);
        }
        final HashMap<String, TableInfo.Column> _columnsBudgets = new HashMap<String, TableInfo.Column>(3);
        _columnsBudgets.put("month_key", new TableInfo.Column("month_key", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("total_limit_minor", new TableInfo.Column("total_limit_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("waste_limit_minor", new TableInfo.Column("waste_limit_minor", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBudgets = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBudgets = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBudgets = new TableInfo("budgets", _columnsBudgets, _foreignKeysBudgets, _indicesBudgets);
        final TableInfo _existingBudgets = TableInfo.read(db, "budgets");
        if (!_infoBudgets.equals(_existingBudgets)) {
          return new RoomOpenHelper.ValidationResult(false, "budgets(com.supernovea.spendsense.data.local.entity.BudgetEntity).\n"
                  + " Expected:\n" + _infoBudgets + "\n"
                  + " Found:\n" + _existingBudgets);
        }
        final HashMap<String, TableInfo.Column> _columnsIncomeSources = new HashMap<String, TableInfo.Column>(8);
        _columnsIncomeSources.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeSources.put("default_key", new TableInfo.Column("default_key", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeSources.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeSources.put("icon_key", new TableInfo.Column("icon_key", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeSources.put("color_hex", new TableInfo.Column("color_hex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeSources.put("is_archived", new TableInfo.Column("is_archived", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeSources.put("is_default", new TableInfo.Column("is_default", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeSources.put("sort_order", new TableInfo.Column("sort_order", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIncomeSources = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesIncomeSources = new HashSet<TableInfo.Index>(2);
        _indicesIncomeSources.add(new TableInfo.Index("index_income_sources_name", true, Arrays.asList("name"), Arrays.asList("ASC")));
        _indicesIncomeSources.add(new TableInfo.Index("index_income_sources_default_key", true, Arrays.asList("default_key"), Arrays.asList("ASC")));
        final TableInfo _infoIncomeSources = new TableInfo("income_sources", _columnsIncomeSources, _foreignKeysIncomeSources, _indicesIncomeSources);
        final TableInfo _existingIncomeSources = TableInfo.read(db, "income_sources");
        if (!_infoIncomeSources.equals(_existingIncomeSources)) {
          return new RoomOpenHelper.ValidationResult(false, "income_sources(com.supernovea.spendsense.data.local.entity.IncomeSourceEntity).\n"
                  + " Expected:\n" + _infoIncomeSources + "\n"
                  + " Found:\n" + _existingIncomeSources);
        }
        final HashMap<String, TableInfo.Column> _columnsIncomes = new HashMap<String, TableInfo.Column>(10);
        _columnsIncomes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("amount_minor", new TableInfo.Column("amount_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("source_id", new TableInfo.Column("source_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("received_at", new TableInfo.Column("received_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("received_date", new TableInfo.Column("received_date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("deleted_at", new TableInfo.Column("deleted_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("recurring_rule_id", new TableInfo.Column("recurring_rule_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIncomes = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysIncomes.add(new TableInfo.ForeignKey("income_sources", "RESTRICT", "NO ACTION", Arrays.asList("source_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesIncomes = new HashSet<TableInfo.Index>(3);
        _indicesIncomes.add(new TableInfo.Index("index_incomes_source_id", false, Arrays.asList("source_id"), Arrays.asList("ASC")));
        _indicesIncomes.add(new TableInfo.Index("index_incomes_received_date", false, Arrays.asList("received_date"), Arrays.asList("ASC")));
        _indicesIncomes.add(new TableInfo.Index("index_incomes_source_id_received_date", false, Arrays.asList("source_id", "received_date"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoIncomes = new TableInfo("incomes", _columnsIncomes, _foreignKeysIncomes, _indicesIncomes);
        final TableInfo _existingIncomes = TableInfo.read(db, "incomes");
        if (!_infoIncomes.equals(_existingIncomes)) {
          return new RoomOpenHelper.ValidationResult(false, "incomes(com.supernovea.spendsense.data.local.entity.IncomeEntity).\n"
                  + " Expected:\n" + _infoIncomes + "\n"
                  + " Found:\n" + _existingIncomes);
        }
        final HashMap<String, TableInfo.Column> _columnsDues = new HashMap<String, TableInfo.Column>(17);
        _columnsDues.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDues.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDues.put("amount_minor", new TableInfo.Column("amount_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDues.put("due_date", new TableInfo.Column("due_date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDues.put("due_time_minutes", new TableInfo.Column("due_time_minutes", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDues.put("repeat_interval", new TableInfo.Column("repeat_interval", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDues.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDues.put("category_id", new TableInfo.Column("category_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDues.put("need_waste_type", new TableInfo.Column("need_waste_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDues.put("paid_expense_id", new TableInfo.Column("paid_expense_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDues.put("paid_at", new TableInfo.Column("paid_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDues.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDues.put("reminder_lead_days", new TableInfo.Column("reminder_lead_days", "INTEGER", true, 0, "3", TableInfo.CREATED_FROM_ENTITY));
        _columnsDues.put("is_deleted", new TableInfo.Column("is_deleted", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY));
        _columnsDues.put("deleted_at", new TableInfo.Column("deleted_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDues.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDues.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDues = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysDues.add(new TableInfo.ForeignKey("categories", "SET NULL", "NO ACTION", Arrays.asList("category_id"), Arrays.asList("id")));
        _foreignKeysDues.add(new TableInfo.ForeignKey("expenses", "SET NULL", "NO ACTION", Arrays.asList("paid_expense_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesDues = new HashSet<TableInfo.Index>(5);
        _indicesDues.add(new TableInfo.Index("index_dues_category_id", false, Arrays.asList("category_id"), Arrays.asList("ASC")));
        _indicesDues.add(new TableInfo.Index("index_dues_due_date", false, Arrays.asList("due_date"), Arrays.asList("ASC")));
        _indicesDues.add(new TableInfo.Index("index_dues_status", false, Arrays.asList("status"), Arrays.asList("ASC")));
        _indicesDues.add(new TableInfo.Index("index_dues_paid_expense_id", false, Arrays.asList("paid_expense_id"), Arrays.asList("ASC")));
        _indicesDues.add(new TableInfo.Index("index_dues_status_due_date", false, Arrays.asList("status", "due_date"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoDues = new TableInfo("dues", _columnsDues, _foreignKeysDues, _indicesDues);
        final TableInfo _existingDues = TableInfo.read(db, "dues");
        if (!_infoDues.equals(_existingDues)) {
          return new RoomOpenHelper.ValidationResult(false, "dues(com.supernovea.spendsense.data.local.entity.DueEntity).\n"
                  + " Expected:\n" + _infoDues + "\n"
                  + " Found:\n" + _existingDues);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "36b7ea5fcd7701b6d6c4e874b8651134", "edd7c9b63d9e9438efa2df74b41a53a9");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "expenses","categories","budgets","income_sources","incomes","dues");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `expenses`");
      _db.execSQL("DELETE FROM `categories`");
      _db.execSQL("DELETE FROM `budgets`");
      _db.execSQL("DELETE FROM `incomes`");
      _db.execSQL("DELETE FROM `income_sources`");
      _db.execSQL("DELETE FROM `dues`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ExpenseDao.class, ExpenseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CategoryDao.class, CategoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BudgetDao.class, BudgetDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IncomeSourceDao.class, IncomeSourceDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IncomeDao.class, IncomeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DueDao.class, DueDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public ExpenseDao expenseDao() {
    if (_expenseDao != null) {
      return _expenseDao;
    } else {
      synchronized(this) {
        if(_expenseDao == null) {
          _expenseDao = new ExpenseDao_Impl(this);
        }
        return _expenseDao;
      }
    }
  }

  @Override
  public CategoryDao categoryDao() {
    if (_categoryDao != null) {
      return _categoryDao;
    } else {
      synchronized(this) {
        if(_categoryDao == null) {
          _categoryDao = new CategoryDao_Impl(this);
        }
        return _categoryDao;
      }
    }
  }

  @Override
  public BudgetDao budgetDao() {
    if (_budgetDao != null) {
      return _budgetDao;
    } else {
      synchronized(this) {
        if(_budgetDao == null) {
          _budgetDao = new BudgetDao_Impl(this);
        }
        return _budgetDao;
      }
    }
  }

  @Override
  public IncomeSourceDao incomeSourceDao() {
    if (_incomeSourceDao != null) {
      return _incomeSourceDao;
    } else {
      synchronized(this) {
        if(_incomeSourceDao == null) {
          _incomeSourceDao = new IncomeSourceDao_Impl(this);
        }
        return _incomeSourceDao;
      }
    }
  }

  @Override
  public IncomeDao incomeDao() {
    if (_incomeDao != null) {
      return _incomeDao;
    } else {
      synchronized(this) {
        if(_incomeDao == null) {
          _incomeDao = new IncomeDao_Impl(this);
        }
        return _incomeDao;
      }
    }
  }

  @Override
  public DueDao dueDao() {
    if (_dueDao != null) {
      return _dueDao;
    } else {
      synchronized(this) {
        if(_dueDao == null) {
          _dueDao = new DueDao_Impl(this);
        }
        return _dueDao;
      }
    }
  }
}
