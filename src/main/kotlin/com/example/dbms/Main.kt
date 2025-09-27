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
    // インメモリデータベースのインスタンスを作成
    private val database = com.example.dbms.storage.Database()
    
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
                    
                    // Step 3: SQL実行
                    when (ast) {
                        is com.example.dbms.parser.CreateTableStatement -> {
                            database.createTable(ast)
                            println("✅ テーブル作成完了")
                        }
                        is com.example.dbms.parser.InsertStatement -> {
                            database.insertData(ast)
                            println("✅ データ挿入完了")
                        }
                        is com.example.dbms.parser.SelectStatement -> {
                            database.select(ast)
                            println("✅ データ検索完了")
                        }
                        else -> {
                            println("⚠️  この種類のSQL文はまだ実装されていません: ${ast::class.simpleName}")
                        }
                    }
                    
                } catch (e: com.example.dbms.parser.ParseException) {
                    println("構文エラー: ${e.message}")
                } catch (e: Exception) {
                    println("エラー: ${e.message}")
                }
            }
        }
    }
}