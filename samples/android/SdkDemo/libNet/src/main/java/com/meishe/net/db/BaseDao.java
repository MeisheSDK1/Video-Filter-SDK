/*
 * Copyright 2016 jeasonlzy.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.meishe.net.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import com.meishe.net.utils.OkLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================、
 * Dao基类
 * The Dao base class
 *
 * @param <T> the type parameter
 */
public abstract class BaseDao<T> {

    protected static String TAG;
    protected Lock lock;
    protected SQLiteOpenHelper helper;
    protected SQLiteDatabase database;

    public BaseDao(SQLiteOpenHelper helper) {
        TAG = getClass().getSimpleName();
        lock = DBHelper.lock;
        this.helper = helper;
        this.database = openWriter();
    }

    /**
     * Open reader sq lite database.
     * 打开reader sqlite数据库
     *
     * @return the sq lite database
     */
    public SQLiteDatabase openReader() {
        return helper.getReadableDatabase();
    }

    /**
     * Open writer sqlite database.
     * 打开writer sqlite数据库
     *
     * @return the sqlite database
     */
    public SQLiteDatabase openWriter() {
        try {
            return helper.getWritableDatabase();
        } catch (Exception e) {
            OkLogger.e("e=" + e);
        }
        return null;
    }

    /**
     * Close database.
     * 关闭数据库
     *
     * @param database the database 数据库
     * @param cursor   the cursor 游标
     */
    protected final void closeDatabase(SQLiteDatabase database, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) cursor.close();
        if (database != null && database.isOpen()) database.close();
    }

    private void checkDatabase() {
        if (this.database == null) {
            database = openWriter();
        }
    }

    /**
     * 插入一条记录  @param t the t
     * insert a record
     *
     * @return the boolean
     */
    public boolean insert(T t) {
        if (t == null) return false;
        long start = System.currentTimeMillis();
        lock.lock();
        try {
            checkDatabase();
            database.beginTransaction();
            database.insert(getTableName(), null, getContentValues(t));
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            try {
                if (database != null) {
                    database.endTransaction();
                }
            } catch (Exception e) {
                OkLogger.printStackTrace(e);
            }
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " insertT");
        }
        return false;
    }

    /**
     * 插入一条记录  @param database the database
     * insert a record
     *
     * @param t the t
     * @return the long
     */
    public long insert(SQLiteDatabase database, T t) {
        return database.insert(getTableName(), null, getContentValues(t));
    }

    /**
     * 插入多条记录  @param ts the ts
     * Insert multiple records
     *
     * @return the boolean
     */
    public boolean insert(List<T> ts) {
        if (ts == null) return false;
        long start = System.currentTimeMillis();
        lock.lock();
        try {
            checkDatabase();
            database.beginTransaction();
            for (T t : ts) {
                database.insert(getTableName(), null, getContentValues(t));
            }
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            try {
                if (database != null) {
                    database.endTransaction();
                }
            } catch (Exception e) {
                OkLogger.printStackTrace(e);
            }
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " insertList");
        }
        return false;
    }

    /**
     * Insert boolean.
     * 插入
     *
     * @param database the database 数据库
     * @param ts       the ts
     * @return the boolean
     */
    public boolean insert(SQLiteDatabase database, List<T> ts) {
        try {
            for (T t : ts) {
                database.insert(getTableName(), null, getContentValues(t));
            }
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
            return false;
        }
    }

    /**
     * 删除所有数据  @return the boolean
     * Delete all data
     */
    public boolean deleteAll() {
        return delete(null, null);
    }

    /**
     * 删除所有数据  @param database the database
     * Delete all data
     *
     * @return the long
     */
    public long deleteAll(SQLiteDatabase database) {
        return delete(database, null, null);
    }

    /**
     * 根据条件删除数据库中的数据  @param whereClause the where clause
     * Deletes data from the database based on conditions
     *
     * @param whereArgs the where args
     * @return the boolean
     */
    public boolean delete(String whereClause, String[] whereArgs) {
        long start = System.currentTimeMillis();
        lock.lock();
        try {
            checkDatabase();
            database.beginTransaction();
            database.delete(getTableName(), whereClause, whereArgs);
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            try {
                if (database != null) {
                    database.endTransaction();
                }
            } catch (Exception e) {
                OkLogger.printStackTrace(e);
            }
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " delete");
        }
        return false;
    }

    /**
     * 根据条件删除数据库中的数据  @param database the database
     * Deletes data from the database based on conditions
     *
     * @param whereClause the where clause
     * @param whereArgs   the where args
     * @return the long
     */
    public long delete(SQLiteDatabase database, String whereClause, String[] whereArgs) {
        return database.delete(getTableName(), whereClause, whereArgs);
    }

    /**
     * Delete list boolean.
     * 删除列表
     *
     * @param where the where
     * @return the boolean
     */
    public boolean deleteList(List<Pair<String, String[]>> where) {
        long start = System.currentTimeMillis();
        lock.lock();
        try {
            checkDatabase();
            database.beginTransaction();
            for (Pair<String, String[]> pair : where) {
                database.delete(getTableName(), pair.first, pair.second);
            }
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            try {
                if (database != null) {
                    database.endTransaction();
                }
            } catch (Exception e) {
                OkLogger.printStackTrace(e);
            }
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " deleteList");
        }
        return false;
    }

    /**
     * replace 语句有如下行为特点
     * 1. replace语句会删除原有的一条记录， 并且插入一条新的记录来替换原记录。
     * 2. 一般用replace语句替换一条记录的所有列， 如果在replace语句中没有指定某列， 在replace之后这列的值被置空 。
     * 3. replace语句根据主键的值确定被替换的是哪一条记录
     * 4. 如果执行replace语句时， 不存在要替换的记录， 那么就会插入一条新的记录。
     * 5. replace语句不能根据where子句来定位要被替换的记录
     * 6. 如果新插入的或替换的记录中， 有字段和表中的其他记录冲突， 那么会删除那条其他记录。
     * The replace statement has the following behavior characteristics
     * 1.replace removes an original record and inserts a new record to replace the original record.
     * 2. Generally, all columns of a record are replaced by a replace statement. If a column is not specified in the replace statement, the value of the column is empty after the replace.
     * 3.Replace statement determines which record is replaced based on the value of the primary key
     * 4. If there is no record to replace when the replace statement is executed, a new record is inserted.
     * 5.replace statement cannot locate the record to be replaced based on the WHERE clause
     * 6. If a newly inserted or replaced record has a field that conflicts with another record in the table, that other record is deleted
     *
     * @param t the t
     * @return the boolean
     */
    public boolean replace(T t) {
        if (t == null) return false;
        long start = System.currentTimeMillis();
        lock.lock();
        try {
            checkDatabase();
            database.beginTransaction();
            database.replace(getTableName(), null, getContentValues(t));
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            try {
                if (database != null && database.inTransaction()) {
                    database.endTransaction();
                }
            } catch (Exception e) {
                OkLogger.printStackTrace(e);
            }
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " replaceT");
        }
        return false;
    }

    /**
     * Replace long.
     * 替换
     *
     * @param database the database 数据库
     * @param t        the t
     * @return the long
     */
    public long replace(SQLiteDatabase database, T t) {
        return database.replace(getTableName(), null, getContentValues(t));
    }

    /**
     * Replace boolean.
     * 替换
     *
     * @param contentValues the content values
     * @return the boolean
     */
    public boolean replace(ContentValues contentValues) {
        long start = System.currentTimeMillis();
        lock.lock();
        checkDatabase();
        try {
            database.beginTransaction();
            database.replace(getTableName(), null, contentValues);
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            try {
                if (database != null) {
                    database.endTransaction();
                }
            } catch (Exception e) {
                OkLogger.printStackTrace(e);
            }
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " replaceContentValues");
        }
        return false;
    }

    /**
     * Replace long.
     * 替换
     *
     * @param database      the database 数据库
     * @param contentValues the content values
     * @return the long
     */
    public long replace(SQLiteDatabase database, ContentValues contentValues) {
        return database.replace(getTableName(), null, contentValues);
    }

    /**
     * Replace boolean.
     * 替换
     *
     * @param ts the ts
     * @return the boolean
     */
    public boolean replace(List<T> ts) {
        if (ts == null) return false;
        long start = System.currentTimeMillis();
        lock.lock();
        checkDatabase();
        try {
            database.beginTransaction();
            for (T t : ts) {
                database.replace(getTableName(), null, getContentValues(t));
            }
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            try {
                if (database != null) {
                    database.endTransaction();
                }
            } catch (Exception e) {
                OkLogger.printStackTrace(e);
            }
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " replaceList");
        }
        return false;
    }

    /**
     * Replace boolean.
     * 替换
     *
     * @param database the database
     * @param ts       the ts
     * @return the boolean
     */
    public boolean replace(SQLiteDatabase database, List<T> ts) {
        try {
            for (T t : ts) {
                database.replace(getTableName(), null, getContentValues(t));
            }
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
            return false;
        }
    }

    /**
     * 更新一条记录  @param t the t
     * Update a record
     *
     * @param whereClause the where clause
     * @param whereArgs   the where args
     * @return the boolean
     */
    public boolean update(T t, String whereClause, String[] whereArgs) {
        if (t == null) return false;
        long start = System.currentTimeMillis();
        lock.lock();
        checkDatabase();
        try {
            database.beginTransaction();
            database.update(getTableName(), getContentValues(t), whereClause, whereArgs);
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            try {
                if (database != null) {
                    database.endTransaction();
                }
            } catch (Exception e) {
                OkLogger.printStackTrace(e);
            }
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " updateT");
        }
        return false;
    }

    /**
     * 更新一条记录  @param database the database
     * Update a record
     *
     * @param t           the t
     * @param whereClause the where clause
     * @param whereArgs   the where args
     * @return the long
     */
    public long update(SQLiteDatabase database, T t, String whereClause, String[] whereArgs) {
        return database.update(getTableName(), getContentValues(t), whereClause, whereArgs);
    }

    /**
     * 更新一条记录  @param contentValues the content values
     * Update a record
     *
     * @param whereClause the where clause
     * @param whereArgs   the where args
     * @return the boolean
     */
    public boolean update(ContentValues contentValues, String whereClause, String[] whereArgs) {
        long start = System.currentTimeMillis();
        lock.lock();
        checkDatabase();
        try {
            database.beginTransaction();
            database.update(getTableName(), contentValues, whereClause, whereArgs);
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            try {
                if (database != null && database.inTransaction()) {
                    database.endTransaction();
                }
            } catch (Exception e) {
                OkLogger.printStackTrace(e);
            }
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " updateContentValues");
        }
        return false;
    }

    /**
     * 更新一条记录  @param database the database
     * Update a record
     *
     * @param contentValues the content values
     * @param whereClause   the where clause
     * @param whereArgs     the where args
     * @return the long
     */
    public long update(SQLiteDatabase database, ContentValues contentValues, String whereClause, String[] whereArgs) {
        return database.update(getTableName(), contentValues, whereClause, whereArgs);
    }

    /**
     * 查询并返回所有对象的集合  @param database the database
     * Query and return a collection of all objects
     *
     * @return the list
     */
    public List<T> queryAll(SQLiteDatabase database) {
        return query(database, null, null);
    }

    /**
     * 按条件查询对象并返回集合  @param database the database
     * Query the object conditionally and return the collection
     *
     * @param selection     the selection
     * @param selectionArgs the selection args
     * @return the list
     */
    public List<T> query(SQLiteDatabase database, String selection, String[] selectionArgs) {
        return query(database, null, selection, selectionArgs, null, null, null, null);
    }

    /**
     * 查询满足条件的一个结果  @param database the database
     * Query a result that satisfies a condition
     *
     * @param selection     the selection 查询条件
     * @param selectionArgs the selection args
     * @return the t
     */
    public T queryOne(SQLiteDatabase database, String selection, String[] selectionArgs) {
        List<T> query = query(database, null, selection, selectionArgs, null, null, null, "1");
        if (query.size() > 0) return query.get(0);
        return null;
    }

    /**
     * 按条件查询对象并返回集合  @param database the database
     * Query the object conditionally and return the collection
     *
     * @param columns       the columns  列
     * @param selection     the selection 选择
     * @param selectionArgs the selection args 选择参数
     * @param groupBy       the group by 分组
     * @param having        the having 具有
     * @param orderBy       the order by 排序
     * @param limit         the limit 限制
     * @return the list
     */
    public List<T> query(SQLiteDatabase database, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        List<T> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.query(getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            while (!cursor.isClosed() && cursor.moveToNext()) {
                list.add(parseCursorToBean(cursor));
            }
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            closeDatabase(null, cursor);
        }
        return list;
    }

    /**
     * 查询并返回所有对象的集合  @return the list
     * Query and return a collection of all objects
     */
    public List<T> queryAll() {
        return query(null, null);
    }

    /**
     * 按条件查询对象并返回集合  @param selection the selection
     * Query the object conditionally and return the collection
     *
     * @param selectionArgs the selection args 选择参数
     * @return the list
     */
    public List<T> query(String selection, String[] selectionArgs) {
        return query(null, selection, selectionArgs, null, null, null, null);
    }

    /**
     * 查询满足条件的一个结果  @param selection the selection
     * Query a result that satisfies a condition
     *
     * @param selectionArgs the selection args 选择参数
     * @return the t
     */
    public T queryOne(String selection, String[] selectionArgs) {
        long start = System.currentTimeMillis();
        List<T> query = query(null, selection, selectionArgs, null, null, null, "1");
        OkLogger.v(TAG, System.currentTimeMillis() - start + " queryOne");
        return query.size() > 0 ? query.get(0) : null;
    }

    /**
     * 按条件查询对象并返回集合  @param columns the columns
     * Query the object conditionally and return the collection
     *
     * @param selection     the selection列
     * @param selection     the selection 选择
     * @param selectionArgs the selection args 选择参数
     * @param groupBy       the group by 分组
     * @param having        the having 具有
     * @param orderBy       the order by 排序
     * @param limit         the limit 限制
     * @return the list
     */
    public List<T> query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        long start = System.currentTimeMillis();
        lock.lock();
        checkDatabase();
        List<T> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            database.beginTransaction();
            cursor = database.query(getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            while (!cursor.isClosed() && cursor.moveToNext()) {
                list.add(parseCursorToBean(cursor));
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            try {
                closeDatabase(null, cursor);
                if (database != null && database.inTransaction()) {
                    database.endTransaction();
                }
            } catch (Exception e) {
                OkLogger.printStackTrace(e);
            }
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " query");
        }
        return list;
    }

    /**
     * The interface Action.
     * 功能接口
     */
    public interface Action {
        /**
         * Call.
         * 调用
         *
         * @param database the database
         */
        void call(SQLiteDatabase database);
    }

    /**
     * 用于给外界提供事物开启的模板  @param action the action
     * A template used to provide things to the outside world to open
     */
    public void startTransaction(Action action) {
        lock.lock();
        try {
            database.beginTransaction();
            action.call(database);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            if (database != null) {
                database.endTransaction();
            }
            lock.unlock();
        }
    }

    /**
     * 获取对应的表名  @return the table name
     * Gets the corresponding table name
     */
    public abstract String getTableName();

    /**
     * Un init.
     * 初始化
     */
    public abstract void unInit();

    /**
     * 将Cursor解析成对应的JavaBean  @param cursor the cursor
     * Parse the Cursor into the corresponding Javabeans
     *
     * @return the t
     */
    public abstract T parseCursorToBean(Cursor cursor);

    /**
     * 需要替换的列  @param t the t
     * Columns that need to be replaced
     *
     * @return the content values
     */
    public abstract ContentValues getContentValues(T t);
}
