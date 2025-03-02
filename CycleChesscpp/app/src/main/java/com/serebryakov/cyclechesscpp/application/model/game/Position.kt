package com.serebryakov.cyclechesscpp.application.model.game

typealias Route = MutableList<Position>

class Position(
    var i: Int = -1, // Строки
    var j: Int = -1 // Столбцы
) {
    override operator fun equals(other: Any?): Boolean{
        if(other is Position) return (this.i == other.i && this.j == other.j)
        return false
    }

    fun offset(iOffset: Int = 0, jOffset: Int = 0): Position {
        return Position((this.i + iOffset + size) % size, (this.j + jOffset + size) % size)
    }

    fun offset(offset: Pair<Int, Int>): Position {
        return offset(offset.first, offset.second)
    }

    fun offset(offset: Position): Position {
        return offset(offset.i, offset.j)
    }

    fun isOverBound(offset: Pair<Int, Int>) = isOverBound(offset.first, offset.second)

    fun isOverBound(iOffset: Int = 0, jOffset: Int = 0) =
        (this.i + iOffset >= size) || (this.i + iOffset < 0) || (this.j + jOffset >= size) || (this.j + jOffset < 0)

    override fun hashCode(): Int {
        var result = i
        result = 31 * result + j
        return result
    }

    override fun toString(): String {
        return "${this.i},${this.j}"
    }
}