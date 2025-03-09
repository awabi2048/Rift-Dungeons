package awabi2048.rift_dungeons.generation

import awabi2048.rift_dungeons.Lib
import awabi2048.rift_dungeons.Main.Companion.instance
import org.bukkit.Axis
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.structure.StructureRotation
import org.bukkit.util.Vector
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

class Generator(val style: Style, val size: Int) {

    // ブロック幅の座標（開始位置がX = 0）
    private val blockCoordinates = (-size / 2..size / 2).filter { it != 0 }

    fun generate(baseLocation: Location) {

        val generationData: MutableMap<Vector, Structure> = mutableMapOf()

        // まず点を生成
        val points = generatePoints()

        // それぞれの点と隣接点との関係からその位置で置かれるべきStructureを取得
        points.forEach {
            // 隣接点とどのように繋がっているかを取得
            val paths = listOf(
                // 東西南北の4方向のうち、
                Vector(1, 0, 0),
                Vector(-1, 0, 0),
                Vector(0, 0, 1),
                Vector(0, 0, -1),
            ).filter { branch -> // その先に点が生成されているものを選び、Setにする
                it.clone().add(branch) in points
            }.toSet()

            // Structureに変換: 開始地点のストラクチャーは特別に処理
            val structure = if (it == Vector(0, 0, -1)) {
                Structure(StructureType.START, 0)
            } else getStructure(paths)

            generationData[it] = structure
        }

        val generationDelay = 2L

        repeat(generationData.size) {
            Bukkit.getScheduler().runTaskLater(
                instance,
                Runnable {
                    placeStructure(baseLocation, generationData.keys.toList()[it], generationData.values.toList()[it])
                },
                generationDelay * it
            )
        }
    }

    private fun generatePoints(): Set<Vector> {
        // 第1・2象限にランダムな点を打つ
        val randomPoints =
            List(size) { Vector(blockCoordinates.random() * 2, 0, (1..size).random()) }

        // RandomPointsからそれぞれXZ軸のいずれかに垂線を下ろし、その上に点列を配置
        val perpendicularPoints: MutableSet<Vector> = mutableSetOf()

        randomPoints.forEach {
            val axis = setOf(Axis.X, Axis.Z).random()
            val signedPerpendicularLength = when (axis) {
                Axis.X -> it.blockX
                Axis.Z -> it.blockZ
                else -> throw IllegalStateException()
            }

            val range = min(0, signedPerpendicularLength)..max(0, signedPerpendicularLength)

            for (i in range) {
                val point = when (axis) {
                    Axis.X -> Vector(i, 0, it.blockZ)
                    Axis.Z -> Vector(it.blockX, 0, i)
                    else -> throw IllegalStateException()
                }

                perpendicularPoints.add(point)
            }
        }

        // 下ろした垂線の足をつなぐように軸上に点列を配置
        val pointXRange = perpendicularPoints.minOf { it.blockX }..randomPoints.maxOf { it.blockX }
        val pointZRange = perpendicularPoints.minOf { it.blockZ }..randomPoints.maxOf { it.blockZ }

        val onAxisPoints: MutableSet<Vector> = mutableSetOf()

        for (x in pointXRange) {
            onAxisPoints.add(Vector(x, 0, 0))
        }

        for (z in pointZRange) {
            onAxisPoints.add(Vector(0, 0, z))
        }

        // 上記の過程で得られた点を総合、開始地点を足す
        val resultPoints = (randomPoints + perpendicularPoints + onAxisPoints + Vector(0, 0, -1)).distinct()

        return resultPoints.toSet()
    }

    private fun getStructure(paths: Set<Vector>): Structure {

        val branches = paths.size

        val randomRotation = (0..3).random()

        // 十字路
        if (branches == 4) {
            return Structure(StructureType.CROSSROADS, randomRotation)
        }

        // T 字路
        if (branches == 3) {
            val emptyDirection = (listOf(
                Vector(1, 0, 0),
                Vector(-1, 0, 0),
                Vector(0, 0, 1),
                Vector(0, 0, -1),
            ) - paths)[0]

            val rotation = when (emptyDirection) {
                Vector(0, 0, 1) -> 0
                Vector(1, 0, 0) -> 1
                Vector(0, 0, -1) -> 2
                Vector(-1, 0, 0) -> 3
                else -> throw IllegalStateException()
            }

            return Structure(StructureType.T_JUNCTION, rotation)
        }

        // L 字路 or 直線路
        if (branches == 2) {
            val list = paths.toList()

            // x成分の絶対値が等しい: ともに0かx軸に並行
            if (list[0].blockX.absoluteValue == list[1].blockX.absoluteValue) {

                val x = list[0].blockX
                val z = list[0].blockZ

                // x成分 ≠ 0 → x軸に並行 (-- ← こんな感じ)
                return if (x != 0) {

                    Structure(StructureType.STRAIGHT, (randomRotation % 2) * 2 + 1)

                } else if (z != 0) { // z成分 ≠ 0 → z軸に並行 ( | ← これ)

                    Structure(StructureType.STRAIGHT, (randomRotation % 2) * 2)

                } else throw IllegalStateException("")

            } else { // それ以外 → L字路

                val x = listOf(list[0].blockX, list[1].blockX).find { it != 0 }!!
                val z = listOf(list[0].blockZ, list[1].blockZ).find { it != 0 }!!

                val rotation =
                    if (x == 1 && z == 1) {
                        0
                    } else if (x == 1 && z == -1) {
                        1
                    } else if (x == -1 && z == -1) {
                        2
                    } else if (x == -1 && z == 1) {
                        3
                    } else throw IllegalStateException()

                return Structure(StructureType.L_CORNER, rotation)
            }
        }

        // 行き止まり
        if (branches == 1) {
            val path = paths.toList()[0]
            val rotation = when (path) {
                Vector(0, 0, 1) -> 0
                Vector(1, 0, 0) -> 1
                Vector(0, 0, -1) -> 2
                Vector(-1, 0, 0) -> 3
                else -> throw IllegalStateException()
            }
            return Structure(StructureType.CLOSURE, rotation)
        }

        // どれにも当てはまらなければおかしいのでthrow
        throw IllegalStateException()
    }

    private fun placeStructure(baseLocation: Location, normalizedPosition: Vector, structure: Structure) {
         val index = listOf("a", "b").random()
//        val index = "a"
        val structurePath = "${style.name.lowercase()}.${structure.type}.$index"

        val basePlaceLocation = baseLocation.clone().add(normalizedPosition.clone().multiply(style.structureSize))
            .add(Vector(-style.structureSize / 2, 0, -style.structureSize / 2))

        val placeLocation = when (structure.rotation) {
            0 -> basePlaceLocation
            1 -> basePlaceLocation.clone().add(Vector(0, 0, style.structureSize - 1))
            2 -> basePlaceLocation.clone().add(Vector(style.structureSize - 1, 0, style.structureSize - 1))
            3 -> basePlaceLocation.clone().add(Vector(style.structureSize - 1, 0, 0))
            else -> throw IllegalStateException()
        }

        val rotation = when (structure.rotation) {
            0 -> StructureRotation.NONE
            1 -> StructureRotation.COUNTERCLOCKWISE_90
            2 -> StructureRotation.CLOCKWISE_180
            3 -> StructureRotation.CLOCKWISE_90
            else -> throw IllegalStateException()
        }

        Lib.loadStructure(placeLocation, structurePath, rotation)
    }
}
