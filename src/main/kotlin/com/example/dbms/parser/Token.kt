package com.example.dbms.parser

enum class TokenType {
    // SQL Keywords
    CREATE,
    TABLE,
    INSERT,
    INTO,
    VALUES,
    SELECT,
    FROM,
    WHERE,
    UPDATE,
    SET,
    DELETE,

    // Data types
    INT,
    VARCHAR,

    // Literals
    IDENTIFIER, // テーブル名、カラム名など
    STRING, // 文字列リテラル
    NUMBER, // 数値リテラル

    // Symbols
    LEFT_PAREN, // (
    RIGHT_PAREN, // )
    COMMA, // ,
    SEMICOLON, // ;
    ASTERISK, // *
    EQUALS, // =
    NOT_EQUALS, // <>
    GREATER_THAN, // >
    LESS_THAN, // <
    GREATER_EQUAL, // >=
    LESS_EQUAL, // <=

    // Special
    EOF, // 入力の終わり
    UNKNOWN // 不明なトークン
}

data class Token(val type: TokenType, val value: String, val position: Int = 0) {
    override fun toString(): String {
        return "Token($type, '$value')"
    }
}
