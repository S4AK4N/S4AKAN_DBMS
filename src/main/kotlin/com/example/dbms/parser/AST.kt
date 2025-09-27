package com.example.dbms.parser

// SQL文の基底クラス
sealed class SqlStatement

// CREATE TABLE文
data class CreateTableStatement(
    val tableName: String,
    val columns: List<ColumnDefinition>
) : SqlStatement()

// カラム定義
data class ColumnDefinition(
    val name: String,
    val type: DataType
)

// INSERT文
data class InsertStatement(
    val tableName: String,
    val values: List<String>
) : SqlStatement()

// SELECT文
data class SelectStatement(
    val columns: List<String>, // "*" or ["name", "age"] 
    val tableName: String,
    val whereClause: WhereClause? = null
) : SqlStatement()


// データ型
sealed class DataType{
    object IntType : DataType(){
        override fun toString(): String = "INT"
    }
    data class VarcharType(val length: Int) : DataType(){
        override fun toString(): String = "VARCHAR($length)"
    }
}

// WHERE句
data class WhereClause(
    val condition: String // 簡易実装
)
