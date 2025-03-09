package awabi2048.rift_dungeons.mob

import org.bukkit.Material
import org.bukkit.entity.EntityType

data class DungeonMob(
    val entityType: EntityType,
    val health: Double,
    val strength: Double,
    val scale: Double,
    val headItem: Material,
    val chestItem: Material,
    val legsItem: Material,
    val feetItem: Material,
    val mainhandItem: Material,
    val offhandItem: Material,
)
