package com.example.rouletteinsight.analysis

data class NumberStat(val number: Int, val count: Int, val percent: Double)

object SpinAnalysis {
    fun frequency(numbersOldestFirst: List<Int>): List<NumberStat> {
        if (numbersOldestFirst.isEmpty()) return emptyList()
        val counts = (0..36).associateWith { n -> numbersOldestFirst.count { it == n } }
        return counts.map { (number, count) ->
            NumberStat(number, count, count * 100.0 / numbersOldestFirst.size)
        }.sortedWith(compareByDescending<NumberStat> { it.count }.thenBy { it.number })
    }

    fun lastSeenGap(numbersOldestFirst: List<Int>, number: Int): Int? {
        val index = numbersOldestFirst.indexOfLast { it == number }
        return if (index < 0) null else numbersOldestFirst.lastIndex - index
    }

    fun recentSequence(numbersOldestFirst: List<Int>, size: Int = 10): List<Int> =
        numbersOldestFirst.takeLast(size)

    // These are descriptive statistics, not reliable predictions of a fair roulette spin.
    fun estimate(numbersOldestFirst: List<Int>, limit: Int = 5): List<NumberStat> =
        frequency(numbersOldestFirst).take(limit)
}
