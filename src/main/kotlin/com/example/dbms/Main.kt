package com.example.dbms

fun main() {
    println("=== 自作DBMS ステップ0 ===")
    println("インメモリDB with REPL")
    println("サポートするSQL: CREATE TABLE, INSERT, SELECT")
    println()
    
    val repl = REPL()
    repl.start()
}

class REPL {
    fun start() {
        println("SQL> を入力してください (quitで終了)")

        val reader = System.`in`.bufferedReader()
        
        while (true) {
            print("SQL> ")
            System.out.flush()
            
            val input = reader.readLine()
            
            if (input == null) {
                println("\n終了します")
                break
            }
            
            val trimmedInput = input.trim()

            if (trimmedInput.equals("quit", ignoreCase = true)) {
                println("終了します")
                break
            }

            if (trimmedInput.isNotBlank()) {
                try {
                    // SQLパーサのテスト
                    val lexer = com.example.dbms.parser.Lexer(trimmedInput)
                    val tokens = lexer.tokenize()
                    
                    println("受け取ったSQL: $trimmedInput")
                    println("トークン解析結果:")
                    tokens.forEach { token -> 
                        if (token.type != com.example.dbms.parser.TokenType.EOF) {
                            println("  $token")
                        }
                    }
                } catch (e: Exception) {
                    println("エラー: ${e.message}")
                }
            }
        }
    }
}