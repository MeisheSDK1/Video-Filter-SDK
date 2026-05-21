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

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/8/9
 * 描    述：表的属性
 * Properties of the table
 * 修订历史：
 * ================================================
 */
public class TableEntity {

    /**
     * The Table name.
     * 表名
     */
    public String tableName;
    private List<ColumnEntity> list;    //所有的表字段 All of the table fields

    public TableEntity(String tableName) {
        this.tableName = tableName;
        list = new ArrayList<>();
    }

    /**
     * Add column table entity.
     * 添加列表实体
     * @param columnEntity the column entity 列的实体
     * @return the table entity  表的实体
     */
    public TableEntity addColumn(ColumnEntity columnEntity) {
        list.add(columnEntity);
        return this;
    }

    /**
     * 建表语句  @return the string
     * Build table statements
     */
    public String buildTableString() {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sb.append(tableName).append('(');
        for (ColumnEntity entity : list) {
            if (entity.compositePrimaryKey != null) {
                sb.append("PRIMARY KEY (");
                for (String primaryKey : entity.compositePrimaryKey) {
                    sb.append(primaryKey).append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append(")");
            } else {
                sb.append(entity.columnName).append(" ").append(entity.columnType);
                if (entity.isNotNull) {
                    sb.append(" NOT NULL");
                }
                if (entity.isPrimary) {
                    sb.append(" PRIMARY KEY");
                }
                if (entity.isAutoincrement) {
                    sb.append(" AUTOINCREMENT");
                }
                sb.append(",");
            }
        }
        if (sb.toString().endsWith(",")) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(')');
        return sb.toString();
    }

    /**
     * 获取数据库表中列的名字
     * Gets the name of the column in the database table
     * @param columnIndex 列在表中的序号
     * @return 返回列的名字 column name
     */
    public String getColumnName(int columnIndex) {
        return list.get(columnIndex).columnName;
    }

    /**
     * 获取数据库表中列的个数  @return the column count
     * Gets the number of columns in the database table
     */
    public int getColumnCount() {
        return list.size();
    }

    /**
     * Gets column index.
     * 获取列索引
     * @param columnName the column name
     * @return the column index
     */
    public int getColumnIndex(String columnName) {
        int columnCount = getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            if (list.get(i).columnName.equals(columnName)) return i;
        }
        return -1;
    }
}
