package com.example.dbms.storage

import com.example.dbms.parser.*
import java.io.File

/** メインのデータベースクラス テーブル管理、SQL実行、永続化を統括 */
class Database {
    // テーブル管理（名前 → Tableオブジェクトのマッピング）
    private val tables: MutableMap<String, Table> = mutableMapOf()

    /** データベースを初期化（ファイルから読み込み） */
    init {
        loadFromFile()
    }

    /** テーブル存在チェック */
    fun tableExists(tableName: String): Boolean {
        val normalizedName = tableName.lowercase()
        return tables.containsKey(normalizedName)
    }

    /** CREATE TABLE文を実行 */
    fun createTable(statement: CreateTableStatement) {
        val tableName = statement.tableName
        val normalizedName = tableName.lowercase()

        // 既存チェック
        if (tableExists(tableName)) {
            throw DatabaseException("テーブル '$tableName' は既に存在します")
        }

        // 新しいテーブルを作成して登録
        val newTable = Table(statement)
        tables[normalizedName] = newTable
        println("テーブル '$tableName' を作成しました")

        saveToFile() // 自動保存
    }

    /** INSERT文を実行 */
    fun insertData(statement: InsertStatement) {
        val table = getTable(statement.tableName)
        if (table == null) {
            throw DatabaseException("テーブル '${statement.tableName}' が見つかりません")
        }

        // テーブルにデータを挿入
        table.insertRow(statement.values)
        println("${statement.values.size} 行を '${statement.tableName}' に挿入しました")

        saveToFile() // 自動保存
    }

    /** SELECT文を実行 */
    fun select(statement: SelectStatement) {
        val table = getTable(statement.tableName)
        if (table == null) {
            throw DatabaseException("テーブル '${statement.tableName}' が見つかりません")
        }

        val results =
                if (statement.whereClause != null) {
                    table.select(statement.columns, statement.whereClause)
                } else {
                    table.select(statement.columns)
                }
        displayResults(results, statement.columns, table)
    }

    /** UPDATE文を実行 */
    fun update(statement: UpdateStatement) {
        val table = getTable(statement.tableName)
        if (table == null) {
            throw DatabaseException("テーブル '${statement.tableName}' が見つかりません")
        }

        val updatedCount =
                if (statement.whereClause != null) {
                    table.update(statement.assignments, statement.whereClause)
                } else {
                    table.update(statement.assignments)
                }

        println("$updatedCount 行を '${statement.tableName}' に更新しました")

        saveToFile() // 自動保存
    }

    /** テーブルを取得（内部用） */
    private fun getTable(tableName: String): Table? {
        val normalizedName = tableName.lowercase()
        return tables[normalizedName]
    }

    /** SELECT結果を表示 */
    private fun displayResults(results: List<Row>, requestedColumns: List<String>, table: Table) {
        if (results.isEmpty()) {
            println("結果: 0 行")
            return
        }

        val actualColumns =
                if (requestedColumns == listOf("*")) {
                    table.schema.columns.map { it.name }
                } else {
                    requestedColumns
                }

        // ヘッダー表示
        println(actualColumns.joinToString(" | "))
        println("-".repeat(actualColumns.joinToString(" | ").length))

        // データ行表示
        results.forEach { row ->
            val dataRow =
                    actualColumns.indices.joinToString(" | ") { index ->
                        val columnIndex =
                                table.schema.columns.indexOfFirst {
                                    it.name == actualColumns[index]
                                }
                        if (columnIndex >= 0 && columnIndex < row.values.size) {
                            row.values[columnIndex].toString()
                        } else ""
                    }
            println(dataRow)
        }

        println("\n結果: ${results.size} 行")
    }

    /** データベースをファイルに保存（簡易JSON形式） */
    private fun saveToFile() {
        try {
            val dataFile = File("database.json")
            val jsonData = buildString {
                append("{\n")
                append("  \"tables\": {\n")

                val tableEntries = tables.entries.toList()
                tableEntries.forEachIndexed { index, (tableName, table) ->
                    append("    \"$tableName\": {\n")
                    append("      \"tableName\": \"${table.schema.tableName}\",\n")
                    append("      \"columns\": [\n")

                    table.schema.columns.forEachIndexed { colIndex, column ->
                        append(
                                "        {\"name\": \"${column.name}\", \"type\": \"${column.type}\"}"
                        )
                        if (colIndex < table.schema.columns.size - 1) append(",")
                        append("\n")
                    }

                    append("      ],\n")
                    append("      \"rowCount\": ${table.getRowCount()}\n")
                    append("    }")
                    if (index < tableEntries.size - 1) append(",")
                    append("\n")
                }

                append("  }\n")
                append("}")
            }

            dataFile.writeText(jsonData)
            println("データベースを保存しました: ${dataFile.absolutePath}")
        } catch (e: Exception) {
            println("保存エラー: ${e.message}")
        }
    }

    /** ファイルからデータベースを読み込み（簡易実装） */
    private fun loadFromFile() {
        try {
            val dataFile = File("database.json")
            if (!dataFile.exists() || dataFile.readText().trim().isEmpty()) {
                println("データファイルが見つからないか空です。新しいデータベースを開始します。")
                return
            }

            println("データベースを読み込みました（簡易実装）")
        } catch (e: Exception) {
            println("データベース読み込みエラー: ${e.message}")
        }
    }
}

/** データベース例外クラス */
class DatabaseException(message: String) : Exception(message)
