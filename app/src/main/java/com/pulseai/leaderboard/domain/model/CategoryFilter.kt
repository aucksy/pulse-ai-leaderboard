package com.pulseai.leaderboard.domain.model

enum class CategoryFilter(val displayName: String, val badgeText: String) {
    OVERALL("Overall Intelligence", "Overall"),
    CODING("Coding Kings", "Coding"),
    REASONING("Reasoning", "Reasoning"),
    VALUE("Best Value ($/Perf)", "Value"),
    SPEED("Fastest Throughput", "Speed"),
    CONTEXT_1M("1M+ Context", "1M+"),
    OPEN_WEIGHT("Open-Weight", "Open"),
    PROPRIETARY("Proprietary", "Proprietary")
}

enum class SortOrder(val displayName: String) {
    SCORE_DESC("Score (High to Low)"),
    PRICE_ASC("Price (Low to High)"),
    SPEED_DESC("Speed (Fastest)"),
    CONTEXT_DESC("Context (Largest)"),
    VALUE_DESC("Value Index (Highest)"),
    NAME_ASC("Name (A to Z)")
}
