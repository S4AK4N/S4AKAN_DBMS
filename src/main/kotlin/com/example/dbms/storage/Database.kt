package com.example.dbms.storage

import com.example.dbms.parser.*

class Database {
    // ここにテーブル管理用のMapを定義
    private val tables: MutableMap<String, Table> = mutableMapOf()

    // テーブルの存在のチェック
    fun tableExists(tableName: String): Boolean {
        val normalizedName = tableName.lowercase()
        return tables.containsKey(normalizedName)
    }

    // テーブルの取得
    fun getTable(tableName: String): Table? {
        val normalizedName = tableName.lowercase()
        return tables[normalizedName]
    }

    // テーブルの追加
    // @param statement CREATE TABLE文のAST
    fun createTable(statement: CreateTableStatement){
        val tableName = statement.tableName
        val normalizedName = tableName.lowercase()

        // すでに同名のテーブルが存在する場合はエラー
        if (tableExists(tableName)) {
            throw DatabaseException("テーブル '$tableName' は既に存在します")
        }

        // 新しいテーブルを作成して登録
        val newTable = Table(statement)
        tables[normalizedName] = newTable
        println("テーブル '$tableName' を作成しました")
    }

    // データの挿入
    // @param statement INSERT文のAST
    fun insertData(statement: InsertStatement) {
        val tableName = statement.tableName
        val table = getTable(tableName)
            ?: throw DatabaseException("テーブル '$tableName' は存在しません")
            
        table.insertRow(statement.values)
        println("テーブル '$tableName' にデータを挿入しました")
    }

}
/**
 * データベース例外
 */
class DatabaseException(message: String) : Exception(message)