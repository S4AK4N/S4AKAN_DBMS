# TODO リスト

## 🎯 **短期目標 (1-2週間)**

### **優先度: 高**
- [ ] **WHERE句実装**
  - [ ] ASTにWhereClause追加
  - [ ] Parserでwhere句解析
  - [ ] Table.selectメソッドでフィルタリング
  - [ ] 比較演算子サポート (`=`, `<>`, `>`, `<`)
  - [ ] 実装例: `SELECT * FROM users WHERE id = 1`

- [ ] **エラーメッセージ改善**
  - [ ] 詳細な行・列位置情報
  - [ ] 日本語エラーメッセージ統一
  - [ ] サジェスト機能（typo修正提案）

- [ ] **ユニットテスト追加**
  - [ ] Lexer単体テスト
  - [ ] Parser単体テスト
  - [ ] Database操作テスト

### **優先度: 中**
- [ ] **SELECT文拡張**
  - [ ] 特定カラム選択 (`SELECT id, name FROM users`)
  - [ ] LIMIT句 (`SELECT * FROM users LIMIT 5`)
  - [ ] ORDER BY句 (`SELECT * FROM users ORDER BY id`)

- [ ] **データ永続化改善**
  - [ ] 実際のデータ保存・復元（現在は簡易実装）
  - [ ] バックアップ機能
  - [ ] 自動バックアップ（時刻ベース）

## 🚀 **中期目標 (1-2ヶ月)**

### **CRUD完成**
- [ ] **UPDATE文実装**
  ```sql
  UPDATE users SET name = 'Alice' WHERE id = 1
  ```
  - [ ] ASTにUpdateStatement追加
  - [ ] Parser拡張
  - [ ] Table.updateメソッド実装

- [ ] **DELETE文実装**
  ```sql
  DELETE FROM users WHERE id = 1
  ```
  - [ ] ASTにDeleteStatement追加
  - [ ] Table.deleteメソッド実装

- [ ] **DROP TABLE実装**
  ```sql
  DROP TABLE users
  ```

### **データ型拡張**
- [ ] **新しいデータ型**
  - [ ] FLOAT/DOUBLE型
  - [ ] DATE型
  - [ ] BOOLEAN型
  - [ ] NULL値サポート

- [ ] **制約機能**
  - [ ] PRIMARY KEY制約
  - [ ] NOT NULL制約
  - [ ] UNIQUE制約
  - [ ] DEFAULT値

## 🌟 **長期目標 (3-6ヶ月)**

### **複数テーブル操作**
- [ ] **JOIN実装**
  ```sql
  SELECT u.name, o.amount 
  FROM users u JOIN orders o ON u.id = o.user_id
  ```
  - [ ] INNER JOIN
  - [ ] LEFT/RIGHT JOIN
  - [ ] クロステーブル結合

### **高度な機能**
- [ ] **インデックス機能**
  - [ ] B+Tree実装
  - [ ] CREATE INDEX文
  - [ ] 検索パフォーマンス向上

- [ ] **集計関数**
  - [ ] COUNT()
  - [ ] SUM()
  - [ ] AVG()
  - [ ] GROUP BY句

- [ ] **サブクエリ**
  ```sql
  SELECT * FROM users WHERE id IN (SELECT user_id FROM orders)
  ```

### **システム機能**
- [ ] **トランザクション**
  - [ ] BEGIN/COMMIT/ROLLBACK
  - [ ] ACID特性保証

- [ ] **同期処理**
  - [ ] 複数クライアント対応
  - [ ] ロック機能

## 🛠️ **技術改善**

### **パフォーマンス最適化**
- [ ] **メモリ効率改善**
  - [ ] Row型の最適化（List<Any> → 専用データ構造）
  - [ ] ページング機能
  - [ ] 遅延読み込み

- [ ] **I/O最適化**
  - [ ] 非同期ファイル操作
  - [ ] バイナリ形式での永続化
  - [ ] 差分保存

### **コード品質**
- [ ] **リファクタリング**
  - [ ] 設計パターン適用（Strategy, Observer等）
  - [ ] 例外処理統一
  - [ ] ロギング機能追加

- [ ] **ドキュメント整備**
  - [ ] KDoc追加
  - [ ] アーキテクチャ図作成
  - [ ] API仕様書

## 🎨 **ユーザビリティ**

### **REPL改善**
- [ ] **コマンド履歴**
  - [ ] 上下矢印キーで履歴
  - [ ] 履歴ファイル保存

- [ ] **自動補完**
  - [ ] テーブル名補完
  - [ ] カラム名補完
  - [ ] SQL構文補完

- [ ] **結果表示改善**
  - [ ] 表の整列表示
  - [ ] ページネーション
  - [ ] カラー表示

### **設定機能**
- [ ] **設定ファイル**
  - [ ] database.properties
  - [ ] ログレベル設定
  - [ ] 表示設定

## 🧪 **テスト・品質保証**

### **テストカバレッジ向上**
- [ ] **統合テスト**
  - [ ] エンドツーエンドテスト
  - [ ] パフォーマンステスト
  - [ ] 負荷テスト

- [ ] **エラーケーステスト**
  - [ ] 不正SQL処理
  - [ ] メモリ不足対応
  - [ ] ファイルI/Oエラー

## 📚 **学習・研究**

### **データベース理論深堀り**
- [ ] **ストレージエンジン研究**
  - [ ] InnoDB構造理解
  - [ ] LSMTree実装
  - [ ] WAL（Write-Ahead Logging）

- [ ] **分散システム**
  - [ ] レプリケーション
  - [ ] シャーディング
  - [ ] 一貫性モデル

---

## 📊 **進捗管理**

### **完了済み** ✅
- [x] 基本CRUD（CREATE TABLE, INSERT, SELECT）
- [x] 字句解析・構文解析
- [x] JSON永続化（簡易版）
- [x] REPL基本機能
- [x] INT/VARCHAR型サポート
- [x] エラーハンドリング基本

### **進行中** 🚧
- [ ] （現在なし）

### **次回作業予定** 📋
1. WHERE句実装開始
2. ユニットテスト環境構築
3. データ永続化の完全実装

---

**最終更新**: 2025年9月27日  
**次回レビュー**: 2025年10月4日