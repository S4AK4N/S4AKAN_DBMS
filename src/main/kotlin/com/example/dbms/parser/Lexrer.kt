package com.example.dbms.parser

class Lexer(private val input: String) {
    private var position: Int = 0
    private val length: Int = input.length

    companion object {
        private val KEYWORDS =
                mapOf(
                        "CREATE" to TokenType.CREATE,
                        "TABLE" to TokenType.TABLE,
                        "INSERT" to TokenType.INSERT,
                        "INTO" to TokenType.INTO,
                        "VALUES" to TokenType.VALUES,
                        "SELECT" to TokenType.SELECT,
                        "FROM" to TokenType.FROM,
                        "WHERE" to TokenType.WHERE,
                        "UPDATE" to TokenType.UPDATE,
                        "SET" to TokenType.SET,
                        "INT" to TokenType.INT,
                        "VARCHAR" to TokenType.VARCHAR,
                )
    }

    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()

        while (!isAtEnd()) {
            skipWhitespace()
            if (isAtEnd()) break

            val token = nextToken()
            if (token.type != TokenType.UNKNOWN) {
                tokens.add(token)
            }
        }

        tokens.add(Token(TokenType.EOF, "", position))
        return tokens
    }

    private fun nextToken(): Token {
        val start = position
        val char = advance()

        return when (char) {
            '(' -> Token(TokenType.LEFT_PAREN, "(", start)
            ')' -> Token(TokenType.RIGHT_PAREN, ")", start)
            ',' -> Token(TokenType.COMMA, ",", start)
            ';' -> Token(TokenType.SEMICOLON, ";", start)
            '*' -> Token(TokenType.ASTERISK, "*", start)
            '=' -> Token(TokenType.EQUALS, "=", start)
            '<' -> {
                if (peek() == '>') {
                    advance()
                    Token(TokenType.NOT_EQUALS, "<>", start)
                } else if (peek() == '=') {
                    advance()
                    Token(TokenType.LESS_EQUAL, "<=", start)
                } else {
                    Token(TokenType.LESS_THAN, "<", start)
                }
            }
            '>' -> {
                if (peek() == '=') {
                    advance()
                    Token(TokenType.GREATER_EQUAL, ">=", start)
                } else {
                    Token(TokenType.GREATER_THAN, ">", start)
                }
            }
            '\'' -> scanString()
            in '0'..'9' -> scanNumber()
            in 'a'..'z', in 'A'..'Z', '_' -> scanIdentifierOrKeyword()
            else -> Token(TokenType.UNKNOWN, char.toString(), start)
        }
    }

    private fun scanString(): Token {
        val start = position - 1
        val value = StringBuilder()

        while (!isAtEnd() && peek() != '\'') {
            value.append(advance())
        }

        if (isAtEnd()) {
            return Token(TokenType.UNKNOWN, value.toString(), start)
        }

        advance() // 閉じるシングルクォートを消費
        return Token(TokenType.STRING, value.toString(), start)
    }

    private fun scanNumber(): Token {
        val start = position - 1
        while (!isAtEnd() && peek().isDigit()) {
            advance()
        }
        val numberStr = input.substring(start, position)
        return Token(TokenType.NUMBER, numberStr, start)
    }

    private fun scanIdentifierOrKeyword(): Token {
        val start = position - 1
        while (!isAtEnd() && (peek().isLetterOrDigit() || peek() == '_')) {
            advance()
        }
        val text = input.substring(start, position)
        val upperText = text.uppercase()
        val type = KEYWORDS[upperText] ?: TokenType.IDENTIFIER
        return Token(type, text, start)
    }

    private fun advance(): Char {
        return input[position++]
    }

    private fun peek(): Char {
        return if (isAtEnd()) '\u0000' else input[position]
    }

    private fun isAtEnd(): Boolean {
        return position >= length
    }

    private fun skipWhitespace() {
        while (!isAtEnd() && peek().isWhitespace()) {
            advance()
        }
    }
}
