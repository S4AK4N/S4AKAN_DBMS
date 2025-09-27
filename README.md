# 自作DBMS (My_DBMS)

Kotlinで実装した学習用の小規模データベース管理システム（DBMS）です。
SQL文の字句解析・構文解析から、テーブル操作、永続化まで一貫して実装しています。

## 🚀 **機能**

### **実装済み機能**
- ✅ **CREATE TABLE** - テーブル作成
- ✅ **INSERT** - データ挿入
- ✅ **SELECT** - データ検索（全件取得）
- ✅ **字句解析** - SQL文をTokenに分割
- ✅ **構文解析** - TokenからAST（抽象構文木）を生成
- ✅ **データ型サポート** - INT型、VARCHAR型
- ✅ **永続化** - JSON形式でファイル保存・読み込み
- ✅ **REPL** - インタラクティブなSQL実行環境

### **サポートするSQL構文**
```sql
-- テーブル作成
CREATE TABLE users (id INT, name VARCHAR(50));

-- データ挿入
INSERT INTO users VALUES (1, 'Alice');
INSERT INTO users VALUES (2, 'Bob');

-- データ検索
SELECT * FROM users;
```

## 🏗️ **アーキテクチャ**

```
src/main/kotlin/com/example/dbms/
├── Main.kt                    # REPL（メインエントリーポイント）
├── storage/
│   ├── Database.kt           # データベース管理・SQL実行・永続化
│   └── Table.kt              # テーブル・スキーマ・データ行管理
└── parser/
    ├── Lexer.kt              # 字句解析（SQL → Token）
    ├── Parser.kt             # 構文解析（Token → AST）
    ├── Token.kt              # トークン定義
    └── AST.kt                # 抽象構文木定義
```

## 💻 **使い方**

### **必要環境**
- Java 17以上
- Gradle 9.1.0以上
- Kotlin 2.0.20

### **実行方法**

1. **プロジェクトクローン**
```bash
git clone <repository-url>
cd My_DBMS
```

2. **ビルド**
```bash
gradle build
```

3. **実行**
```bash
gradle run
```

4. **SQLコマンド例**
```
SQL> CREATE TABLE products (id INT, name VARCHAR(100), price INT);
SQL> INSERT INTO products VALUES (1, 'Laptop', 1000);
SQL> INSERT INTO products VALUES (2, 'Mouse', 25);
SQL> SELECT * FROM products;
SQL> quit
```

## 📊 **実行例**

```
=== 自作DBMS ステップ0 ===
インメモリDB with REPL
サポートするSQL: CREATE TABLE, INSERT, SELECT

SQL> CREATE TABLE users (id INT, name VARCHAR(50));
テーブル 'users' を作成しました
データベースを保存しました: /path/to/database.json

SQL> INSERT INTO users VALUES (1, 'Alice');
2 行を 'users' に挿入しました

SQL> SELECT * FROM users;
id | name 
----------
1  | Alice

結果: 1 行
```

## 🗂️ **データ永続化**

- **形式**: JSON
- **ファイル名**: `database.json`
- **場所**: プロジェクトルート
- **自動保存**: CREATE TABLE・INSERT実行時

### **JSON形式例**
```json
{
  "tables": {
    "users": {
      "tableName": "users",
      "columns": [
        {"name": "id", "type": "INT"},
        {"name": "name", "type": "VARCHAR(50)"}
      ],
      "rowCount": 2
    }
  }
}
```

## 🧪 **技術的特徴**

### **字句解析・構文解析**
- 手作りのLexer・Parser実装
- トークンベースの構文解析
- AST（抽象構文木）による意味解析

### **データ型システム**
- `INT`: 整数型
- `VARCHAR(n)`: 可変長文字列型（長さ制限付き）
- 型変換・バリデーション機能

### **エラーハンドリング**
- 詳細なエラーメッセージ
- 型不整合検出
- テーブル重複チェック

## 🚧 **制限事項**

- WHERE句未実装（全件取得のみ）
- UPDATE/DELETE未実装
- JOIN操作未対応
- インデックス機能なし
- トランザクション機能なし

## 📈 **パフォーマンス**

- **小規模データ**（〜1,000行）での使用を想定
- メモリ上での全データ管理
- 永続化は同期処理

## 🛠️ **開発情報**

### **言語・フレームワーク**
- **Kotlin** 2.0.20
- **Gradle** 9.1.0 (Kotlin DSL)
- **Java** 17