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
                    // SQLの完全処理パイプライン
                    println("受け取ったSQL: $trimmedInput")
                    
                    // Step 1: 字句解析
                    val lexer = com.example.dbms.parser.Lexer(trimmedInput)
                    val tokens = lexer.tokenize()
                    
                    println("字句解析結果:")
                    tokens.forEach { token -> 
                        if (token.type != com.example.dbms.parser.TokenType.EOF) {
                            println("  $token")
                        }
                    }
                    
                    // Step 2: 構文解析
                    val parser = com.example.dbms.parser.Parser(tokens)
                    val ast = parser.parse()
                    
                    println("構文解析結果:")
                    println("  AST: $ast")
                    
                    // Step 3: SQL実行（未実装）
                    println("(SQL実行機能は未実装)")
                    
                } catch (e: com.example.dbms.parser.ParseException) {
                    println("構文エラー: ${e.message}")
                } catch (e: Exception) {
                    println("エラー: ${e.message}")
                }
            }
        }
    }
}