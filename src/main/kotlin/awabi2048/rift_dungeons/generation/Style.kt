package awabi2048.rift_dungeons.generation

enum class Style {
    CAVE,
    MINESHAFT,
    STRONGHOLD,
    LIBRARY,
    JAPANESE_CASTLE,
    ANCIENT_RUINS;

    val structureSize: Int
        get() {
            return when (this) {
                CAVE -> 31
                MINESHAFT -> 9
                STRONGHOLD -> 9
                LIBRARY -> 9
                JAPANESE_CASTLE -> 29
                ANCIENT_RUINS -> 29
            }
        }
}
