package com.example.dbms.storage.engine

import com.example.dbms.storage.Row

/**
 * テーブルの物理データアクセスを抽象化するインターフェース。 将来的にディスクベースの実装やインデックス付き実装へ差し替えられるようにする。
 *
 * SQLの文法要素には依存させず、純粋な行操作の原語層として扱う。
 */
interface StorageEngine {
    fun insert(row: Row)

    /**
     * 行を読み取る。
     * @param predicate nullの場合は全行、それ以外は条件に一致する行のみ返す。
     */
    fun scan(predicate: ((Row) -> Boolean)? = null): List<Row>

    /**
     * 行を更新する。
     * @param predicate nullの場合は全行、それ以外は条件に一致する行のみ対象。
     * @param transform 更新後の行を生成する変換関数。
     * @return 更新された行数。
     */
    fun update(predicate: ((Row) -> Boolean)? = null, transform: (Row) -> Row): Int

    /**
     * 行を削除する。
     * @param predicate nullの場合は全行、それ以外は条件に一致する行のみ削除。
     * @return 削除された行数。
     */
    fun delete(predicate: ((Row) -> Boolean)? = null): Int

    fun rowCount(): Int
}
