package com.example.dbms.parser

class Parser(private val tokens: List<Token>) {
    private var current: Int = 0

    fun parse(): SqlStatement {
        return when (peek().type) {
            TokenType.CREATE -> parseCreateTable()
            TokenType.INSERT -> parseInsert()
            TokenType.SELECT -> parseSelect()
            TokenType.UPDATE -> parseUpdate()
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
            val valueToken =
                    when (peek().type) {
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
            whereClause = parseWhereClause()
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

    private fun parseUpdate(): UpdateStatement {
        consume(TokenType.UPDATE, "UPDATEキーワードが必要です")

        val tableName = consume(TokenType.IDENTIFIER, "テーブル名が必要です").value

        consume(TokenType.SET, "SETキーワードが必要です")

        // SET句の解析: column = value, column2 = value2, ...
        val assignments = mutableMapOf<String, String>()
        do {
            val updateColumn = consume(TokenType.IDENTIFIER, "カラム名が必要です").value
            consume(TokenType.EQUALS, "'='が必要です")

            val value =
                    when (peek().type) {
                        TokenType.STRING -> consume(TokenType.STRING, "文字列値が必要です").value
                        TokenType.NUMBER -> consume(TokenType.NUMBER, "数値が必要です").value
                        else -> throw ParseException("SET句で値が必要です. Got: ${peek().value}")
                    }

            assignments[updateColumn] = value
        } while (match(TokenType.COMMA))

        // WHERE句の解析（オプショナル）
        val whereClause =
                if (match(TokenType.WHERE)) {
                    parseWhereClause()
                } else {
                    null
                }

        consume(TokenType.SEMICOLON, "';'が必要です")

        return UpdateStatement(tableName, assignments, whereClause)
    }

    private fun parseWhereClause(): WhereClause {
        // カラム名を取得
        val leftColumn = consume(TokenType.IDENTIFIER, "WHERE句のカラム名が必要です").value

        // 比較演算子を取得
        val operator =
                when (peek().type) {
                    TokenType.EQUALS -> {
                        advance()
                        ComparisonOperator.EQUALS
                    }
                    TokenType.NOT_EQUALS -> {
                        advance()
                        ComparisonOperator.NOT_EQUALS
                    }
                    TokenType.GREATER_THAN -> {
                        advance()
                        ComparisonOperator.GREATER_THAN
                    }
                    TokenType.LESS_THAN -> {
                        advance()
                        ComparisonOperator.LESS_THAN
                    }
                    TokenType.GREATER_EQUAL -> {
                        advance()
                        ComparisonOperator.GREATER_THAN_OR_EQUAL
                    }
                    TokenType.LESS_EQUAL -> {
                        advance()
                        ComparisonOperator.LESS_THAN_OR_EQUAL
                    }
                    else -> throw ParseException("WHERE句で比較演算子が必要です. Got: ${peek().value}")
                }

        // 値を取得（文字列または数値）
        val rightValue =
                when (peek().type) {
                    TokenType.STRING -> consume(TokenType.STRING, "文字列値が必要です").value
                    TokenType.NUMBER -> consume(TokenType.NUMBER, "数値が必要です").value
                    else -> throw ParseException("WHERE句で値が必要です. Got: ${peek().value}")
                }

        val condition = Condition.ComparisonCondition(leftColumn, operator, rightValue)
        return WhereClause(condition)
    }
}

class ParseException(message: String) : Exception(message)
