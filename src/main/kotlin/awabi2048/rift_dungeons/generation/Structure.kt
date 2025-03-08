package awabi2048.rift_dungeons.generation

import org.bukkit.util.Vector


// rotation → N を 0 として左回り
data class Structure(val type: StructureType, val rotation: Int) {

    val branches: Set<Vector>
        get() {
            val set: MutableSet<Vector> = mutableSetOf()

            if (type == StructureType.CROSSROADS) {
                set.addAll(
                    listOf(
                        Vector(1.0, 0.0, 0.0),
                        Vector(-1.0, 0.0, 0.0),
                        Vector(0.0, 0.0, 1.0),
                        Vector(0.0, 0.0, -1.0),
                    )
                )
            }

            if (type == StructureType.STRAIGHT) {
                if (rotation == 0 || rotation == 2) {
                    set.addAll(
                        listOf(
                            Vector(0.0, 0.0, 1.0),
                            Vector(0.0, 0.0, -1.0),
                        )
                    )
                }

                if (rotation == 1 || rotation == 3) {
                    set.addAll(
                        listOf(
                            Vector(1.0, 0.0, 0.0),
                            Vector(-1.0, 0.0, 0.0),
                        )
                    )
                }
            }

            if (type == StructureType.CLOSURE || type == StructureType.START) {
                set += when (rotation) {
                    0 -> Vector(0.0, 0.0, 1.0)
                    1 -> Vector(-1.0, 0.0, 0.0)
                    2 -> Vector(0.0, 0.0, -1.0)
                    3 -> Vector(1.0, 0.0, 0.0)
                    else -> throw IllegalStateException()
                }
            }

            if (type == StructureType.T_JUNCTION) {
                set += when (rotation) {
                    0 -> listOf(
                        Vector(0.0, 0.0, -1.0),
                        Vector(1.0, 0.0, 0.0),
                        Vector(-1.0, 0.0, 0.0),
                    )

                    1 -> listOf(
                        Vector(0.0, 0.0, -1.0),
                        Vector(0.0, 0.0, 1.0),
                        Vector(1.0, 0.0, 0.0),
                    )

                    2 -> listOf(
                        Vector(0.0, 0.0, 1.0),
                        Vector(1.0, 0.0, 0.0),
                        Vector(-1.0, 0.0, 0.0),
                    )

                    3 -> listOf(
                        Vector(0.0, 0.0, -1.0),
                        Vector(-1.0, 0.0, 0.0),
                        Vector(0.0, 0.0, 1.0),
                    )

                    else -> throw IllegalStateException()
                }
            }

            if (type == StructureType.L_CORNER) {
                set += when(rotation) {
                    0 -> listOf(
                        Vector(1.0, 0.0, 0.0),
                        Vector(0.0, 0.0, 1.0),
                    )

                    1 -> listOf(
                        Vector(-1.0, 0.0, 0.0),
                        Vector(0.0, 0.0, 1.0),
                    )

                    2 -> listOf(
                        Vector(-1.0, 0.0, 0.0),
                        Vector(0.0, 0.0, -1.0),
                    )

                    3 -> listOf(
                        Vector(1.0, 0.0, 0.0),
                        Vector(0.0, 0.0, -1.0),
                    )

                    else -> throw IllegalStateException()
                }
            }

            return set
        }

}

