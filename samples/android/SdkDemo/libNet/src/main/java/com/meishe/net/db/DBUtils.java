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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.meishe.net.utils.OkLogger;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/5/11
 * 描    述：
 * 修订历史：
 * ================================================
 * 数据库工具类
 * Database utility class
 */
public class DBUtils {

    /**
     * 是否需要升级表  @param db the db
     * Whether you need to upgrade the table
     * @param table the table
     * @return the boolean
     */
    public static boolean isNeedUpgradeTable(SQLiteDatabase db, TableEntity table) {
        if (!isTableExists(db, table.tableName)) return true;

        Cursor cursor = db.rawQuery("select * from " + table.tableName, null);
        if (cursor == null) return false;
        try {
            int columnCount = table.getColumnCount();
            if (columnCount == cursor.getColumnCount()) {
                for (int i = 0; i < columnCount; i++) {
                    if (table.getColumnIndex(cursor.getColumnName(i)) == -1) {
                        return true;
                    }
                }
            } else {
                return true;
            }
            return false;
        } finally {
            cursor.close();
        }
    }

    /**
     * SQLite数据库中一个特殊的名叫 SQLITE_MASTER 上执行一个SELECT查询以获得所有表的索引。每一个 SQLite 数据库都有一个叫 SQLITE_MASTER 的表， 它定义数据库的模式。
     * SQLITE_MASTER 表看起来如下：
     * CREATE TABLE sqlite_master (
     * type TEXT,
     * name TEXT,
     * tbl_name TEXT,
     * rootpage INTEGER,
     * sql TEXT
     * );
     * 对于表来说，type 字段永远是 ‘table’，name 字段永远是表的名字。
     * A special SQLite database named SQLITE_MASTER performs a SELECT query to get the indexes of all the tables. Each SQLite database has a table called SQLITE_MASTER, which defines the schema for the database.
     * * SQLITE_MASTER table looks as follows:
     * * CREATE TABLE sqlite_master (
     * * type TEXT,
     * * name TEXT,
     * * tbl_name TEXT,
     * * rootpage INTEGER,
     * * sql TEXT
     * *);
     * * For a table, the Type field is always' table 'and the name field is always the name of the table.
     * @param db        the db 数据库
     * @param tableName the table name 表名
     * @return the boolean
     */
    public static boolean isTableExists(SQLiteDatabase db, String tableName) {
        if (tableName == null || db == null || !db.isOpen()) return false;

        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table", tableName});
            if (!cursor.moveToFirst()) {
                return false;
            }
            count = cursor.getInt(0);
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return count > 0;
    }

    /**
     * Is field exists boolean.
     * 字段是否存在
     * @param db        the db 数据库
     * @param tableName the table name 表名
     * @param fieldName the field name 字段名
     * @return the boolean
     */
    public static boolean isFieldExists(SQLiteDatabase db, String tableName, String fieldName) {
        if (tableName == null || db == null || fieldName == null || !db.isOpen()) return false;

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0", null);
            return cursor != null && cursor.getColumnIndex(fieldName) != -1;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
