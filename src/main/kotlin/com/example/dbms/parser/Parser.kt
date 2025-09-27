package com.example.dbms.parser

class Parser(private val tokens: List<Token>){
    private var current: Int = 0

    fun parse(): SqlStatement {
        return when (peek().type) {
            TokenType.CREATE -> parseCreateTable()
            TokenType.INSERT -> parseInsert()
            TokenType.SELECT -> parseSelect()
            else -> throw Exception("不明なSQL文: ${peek().value}")
        }
    }

    private fun parseCreateTable(): CreateTableStatement {
        consume(TokenType.CREATE, "CREATEキーワードが必要です")
        consume(TokenType.TABLE, "TABLEキーワードが必要です")
        
        val tableName = consume(TokenType.IDENTIFIER, "テーブル名が必要です").value
        
        consume(TokenType.LEFT_PAREN, "'('が必要です")
        
        val columns = mutableListOf<ColumnDefinition>()
        
        do {
            val columnName = consume(TokenType.IDENTIFIER, "カラム名が必要です").value
            val dataType = parseDataType()
            columns.add(ColumnDefinition(columnName, dataType))
        } while (match(TokenType.COMMA))
        
        consume(TokenType.RIGHT_PAREN, "')'が必要です")
        consume(TokenType.SEMICOLON, "';'が必要です")
        
        return CreateTableStatement(tableName, columns)
    }

    private fun parseDataType(): DataType {
        return when (peek().type) {
            TokenType.INT -> {
                consume(TokenType.INT, "INT型が必要です")
                DataType.IntType
            }
            TokenType.VARCHAR -> {
                consume(TokenType.VARCHAR, "VARCHAR型が必要です")
                consume(TokenType.LEFT_PAREN, "'('が必要です")
                val lengthToken = consume(TokenType.NUMBER, "VARCHARの長さが必要です")
                val length = lengthToken.value.toInt()
                consume(TokenType.RIGHT_PAREN, "')'が必要です")
                DataType.VarcharType(length)
            }
            else -> throw Exception("不明なデータ型: ${peek().value}")
        }
    }

    private fun parseInsert(): InsertStatement {
        consume(TokenType.INSERT, "INSERTキーワードが必要です")
        consume(TokenType.INTO, "INTOキーワードが必要です")
        
        val tableName = consume(TokenType.IDENTIFIER, "テーブル名が必要です").value
        
        consume(TokenType.VALUES, "VALUESキーワードが必要です")
        consume(TokenType.LEFT_PAREN, "'('が必要です")
        
        val values = mutableListOf<String>()
        
        do {
            val valueToken = when (peek().type) {
                TokenType.STRING -> consume(TokenType.STRING, "文字列リテラルが必要です")
                TokenType.NUMBER -> consume(TokenType.NUMBER, "数値リテラルが必要です")
                else -> throw Exception("不明な値: ${peek().value}")
            }
            values.add(valueToken.value)
        } while (match(TokenType.COMMA))
        
        consume(TokenType.RIGHT_PAREN, "')'が必要です")
        consume(TokenType.SEMICOLON, "';'が必要です")
        
        return InsertStatement(tableName, values)
    }

    private fun parseSelect(): SelectStatement {
        consume(TokenType.SELECT, "SELECTキーワードが必要です")
        
        val columns = mutableListOf<String>()
        if (match(TokenType.ASTERISK)) {
            columns.add("*")
        } else {
            do {
                val columnName = consume(TokenType.IDENTIFIER, "カラム名が必要です").value
                columns.add(columnName)
            } while (match(TokenType.COMMA))
        }
        
        consume(TokenType.FROM, "FROMキーワードが必要です")
        val tableName = consume(TokenType.IDENTIFIER, "テーブル名が必要です").value
        
        var whereClause: WhereClause? = null
        if (match(TokenType.WHERE)) {
            val condition = consume(TokenType.IDENTIFIER, "WHERE条件が必要です").value
            whereClause = WhereClause(condition)
        }
        
        consume(TokenType.SEMICOLON, "';'が必要です")
        
        return SelectStatement(columns, tableName, whereClause)
    }

    // ユーティリティメソッド
    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }
    
    private fun match(type: TokenType): Boolean {
        if (check(type)) {
            advance()
            return true
        }
        return false
    }

    private fun check(type: TokenType): Boolean {
        if (isAtEnd()) return false
        return peek().type == type
    }

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun isAtEnd(): Boolean {
        return peek().type == TokenType.EOF
    }

    private fun peek(): Token {
        return tokens[current]
    }

    private fun previous(): Token {
        return tokens[current - 1]
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        throw ParseException("$message. Got: ${peek().value}")
    }
}

class ParseException(message: String) : Exception(message)