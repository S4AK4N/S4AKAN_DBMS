# 自作DBMS (My_DBMS)

Kotlinで実装した学習用の小規模データベース管理システム（DBMS）です。
SQL文の字句解析・構文解析から、テーブル操作、永続化まで一貫して実装しています。

## 🚀 機能

### 実装済み機能

- ✅ **CREATE TABLE** — テーブル作成
- ✅ **INSERT** — データ挿入
- ✅ **SELECT** — `WHERE` 句による比較演算（=, <>, >, <, >=, <=）に対応した検索
- ✅ **UPDATE** — 任意カラムの更新（`WHERE` 句あり／なし）
- ✅ **DELETE** — 行削除（`WHERE` 句あり／なし）
- ✅ **字句解析 / 構文解析** — SQLをトークン化しASTへ変換
- ✅ **データ型サポート** — INT型、VARCHAR型
- ✅ **永続化** — JSON形式での保存・読み込み
- ✅ **REPL** — インタラクティブなSQL実行環境
- ✅ **ストレージエンジン抽象化** — `RowFilter` / `RowUpdater` による行操作の拡張性

### サポートするSQL構文

```sql
-- テーブル作成
CREATE TABLE users (id INT, name VARCHAR(50));

-- データ挿入
INSERT INTO users VALUES (1, 'Alice');
INSERT INTO users VALUES (2, 'Bob');

-- データ更新
UPDATE users SET name = 'Charlie' WHERE id = 2;

-- データ削除
DELETE FROM users WHERE id = 1;

-- 条件付き検索
SELECT * FROM users WHERE id >= 2;
```

## 🏗️ アーキテクチャ

```text
src/main/kotlin/com/example/dbms/
├── Main.kt                        # REPL（メインエントリーポイント）
├── storage/
│   ├── Database.kt               # SQL実行と永続化
│   ├── Table.kt                  # テーブルスキーマと行操作の仲介
│   └── engine/
│       ├── StorageEngine.kt      # 行操作の共通インターフェース
│       ├── InMemoryStorageEngine.kt
│       ├── RowFilter.kt          # 行フィルタリングの契約
│       └── RowUpdater.kt         # 行更新の契約
└── parser/
    ├── Lexer.kt                  # 字句解析（SQL → Token）
    ├── Parser.kt                 # 構文解析（Token → AST）
    ├── Token.kt                  # トークン定義
    └── AST.kt                    # 抽象構文木定義
```

## 💻 使い方

### 必要環境

- Java 17 以上
- Gradle 9.1.0 以上（プロジェクト同梱の Gradle Wrapper を推奨）
- Kotlin 2.0.20

### 実行方法

1. **プロジェクトクローン**

   ```bash
   git clone <repository-url>
   cd My_DBMS
   ```

1. **ビルド**

   ```bash
   ./gradlew build
   ```

1. **実行**

   ```bash
   ./gradlew run
   ```

1. **REPL 内 SQL 例**

   ```text
   SQL> CREATE TABLE products (id INT, name VARCHAR(100), price INT);
   SQL> INSERT INTO products VALUES (1, 'Laptop', 1000);
   SQL> INSERT INTO products VALUES (2, 'Mouse', 25);
   SQL> UPDATE products SET price = 950 WHERE id = 1;
   SQL> DELETE FROM products WHERE price < 100;
   SQL> SELECT * FROM products WHERE price >= 900;
   SQL> quit
   ```

### テスト

```bash
./gradlew test
```

## 📊 実行例

```text
=== 自作DBMS ステップ0 ===
インメモリDB with REPL
サポートするSQL: CREATE TABLE, INSERT, SELECT, UPDATE, DELETE

SQL> CREATE TABLE users (id INT, name VARCHAR(50));
テーブル 'users' を作成しました
データベースを保存しました: /path/to/My_DBMS/database.json

SQL> INSERT INTO users VALUES (1, 'Alice');
2 行を 'users' に挿入しました
データベースを保存しました: /path/to/My_DBMS/database.json

SQL> INSERT INTO users VALUES (2, 'Bob');
2 行を 'users' に挿入しました
データベースを保存しました: /path/to/My_DBMS/database.json

SQL> UPDATE users SET name = 'Charlie' WHERE id = 2;
1 行を 'users' に更新しました
データベースを保存しました: /path/to/My_DBMS/database.json

SQL> DELETE FROM users WHERE id = 1;
1 行を 'users' から削除しました
データベースを保存しました: /path/to/My_DBMS/database.json

SQL> SELECT * FROM users WHERE id >= 1;
id | name
---------
2 | Charlie

結果: 1 行

SQL> quit
終了します
```

## 🗂️ データ永続化

- **形式**: JSON
- **ファイル名**: `database.json`
- **保存先**: プロジェクトルート
- **自動保存タイミング**: CREATE TABLE / INSERT / UPDATE / DELETE 実行時

### JSON 形式例

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

## 🧪 技術的特徴

### 字句解析・構文解析

- 手作りの Lexer / Parser 実装
- トークンベースの構文解析
- AST（抽象構文木）による意味解析

### データ型システム

- `INT`: 整数型
- `VARCHAR(n)`: 可変長文字列型（長さ制限付き）
- 型変換・バリデーション機能

### ストレージエンジン

- `StorageEngine` インターフェースで行操作を抽象化
- `RowFilter` で条件評価、`RowUpdater` で行の更新手続きをカプセル化
- `InMemoryStorageEngine` によるメモリ常駐実装（将来の差し替えを想定）

### エラーハンドリング

- 詳細なエラーメッセージ
- 型不整合や存在しないカラムの検出
- テーブル重複作成の防止

## 🚧 制限事項

- `WHERE` 句は単一条件のみ対応（AND/OR や複合条件は未対応）
- ORDER BY / GROUP BY / 集約関数 / JOIN は未実装
- インデックス・トランザクション・ユーザー管理など高度な機能は未実装

## 📈 パフォーマンス

- 小規模データ（〜1,000 行程度）を想定
- 全データをメモリ上で管理
- 永続化は同期書き込み

## 🛠️ 開発情報

### 言語・フレームワーク

- **Kotlin** 2.0.20
- **Gradle** 9.1.0 (Kotlin DSL)
- **Java** 17
