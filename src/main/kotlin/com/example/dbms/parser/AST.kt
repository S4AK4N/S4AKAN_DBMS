package com.example.dbms.parser

// SQL文の基底クラス
sealed class SqlStatement

// CREATE TABLE文
data class CreateTableStatement(val tableName: String, val columns: List<ColumnDefinition>) :
        SqlStatement()

// カラム定義
data class ColumnDefinition(val name: String, val type: DataType)

// INSERT文
data class InsertStatement(val tableName: String, val values: List<String>) : SqlStatement()

// UPDATE文
data class UpdateStatement(
        val tableName: String,
        val assignments: Map<String, String>, // カラム名 -> 新しい値
        val whereClause: WhereClause?
) : SqlStatement()

// SELECT文
data class SelectStatement(
        val columns: List<String>, // "*" or ["name", "age"]
        val tableName: String,
        val whereClause: WhereClause? = null
) : SqlStatement()

// データ型
sealed class DataType {
    object IntType : DataType() {
        override fun toString(): String = "INT"
    }
    data class VarcharType(val length: Int) : DataType() {
        override fun toString(): String = "VARCHAR($length)"
    }
}

// WHERE句
data class WhereClause(val condition: Condition)

// 条件式
sealed class Condition {
    data class ComparisonCondition(
            val left: String, // カラム名
            val operator: ComparisonOperator,
            val right: String // 値（数値または文字列）
    ) : Condition()
}

// 比較演算子
enum class ComparisonOperator(val symbol: String) {
    EQUALS("="),
    NOT_EQUALS("<>"),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN_OR_EQUAL("<=")
}
