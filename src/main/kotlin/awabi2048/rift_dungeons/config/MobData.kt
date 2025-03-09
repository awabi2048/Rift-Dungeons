package awabi2048.rift_dungeons.config

import awabi2048.rift_dungeons.mob.DungeonMob
import awabi2048.rift_dungeons.generation.Style
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

object MobData {
    val mobDefinitions: Map<String, DungeonMob>
        get() {
            val mobIds = DataFile.mobDefinitionFile.getKeys(false)

            val re = mobIds.associateWith { id ->
                val data = DataFile.mobDefinitionFile.getConfigurationSection(id)?: throw IllegalStateException()
                val entityType = EntityType.valueOf(data.getString("entity_type")?: throw IllegalStateException())

                val health = data.getInt("health")
                val strength = data.getInt("strength")
                val scale = data.getDouble("scale")

                val headItem = Material.valueOf(data.getString("equipment.head")?: throw IllegalStateException())
                val chestItem = Material.valueOf(data.getString("equipment.chest")?: throw IllegalStateException())
                val legsItem = Material.valueOf(data.getString("equipment.legs")?: throw IllegalStateException())
                val feetItem = Material.valueOf(data.getString("equipment.feet")?: throw IllegalStateException())
                val mainhandItem = Material.valueOf(data.getString("equipment.mainhand")?: throw IllegalStateException())
                val offhandItem = Material.valueOf(data.getString("equipment.offhand")?: throw IllegalStateException())

                DungeonMob(
                    entityType = entityType,
                    health = health.toDouble(),
                    strength = strength.toDouble(),
                    scale = scale,
                    headItem = headItem,
                    chestItem = chestItem,
                    legsItem = legsItem,
                    feetItem = feetItem,
                    mainhandItem = mainhandItem,
                    offhandItem = offhandItem
                )
            }

            return re
        }

    val mobSpawnRules: Map<Style, List<String>>
        get() {
            val map = Style.entries.associateWith {
                DataFile.mobSpawnRuleFile.getStringList(it.name.lowercase())
            }

            return map
        }

    fun spawnFromId(id: String, location: Location): LivingEntity {
        val mobData = mobDefinitions[id]?: throw IllegalArgumentException()
        val mob = location.world.spawnEntity(location, mobData.entityType) as LivingEntity

        mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = mobData.strength
        mob.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = mobData.health
        mob.getAttribute(Attribute.GENERIC_SCALE)?.baseValue = mobData.scale

        mob.equipment?.setItem(EquipmentSlot.HAND, ItemStack(mobData.mainhandItem))
        mob.equipment?.setItem(EquipmentSlot.OFF_HAND, ItemStack(mobData.offhandItem))
        mob.equipment?.setItem(EquipmentSlot.HEAD, ItemStack(mobData.headItem))
        mob.equipment?.setItem(EquipmentSlot.CHEST, ItemStack(mobData.chestItem))
        mob.equipment?.setItem(EquipmentSlot.LEGS, ItemStack(mobData.legsItem))
        mob.equipment?.setItem(EquipmentSlot.FEET, ItemStack(mobData.feetItem))

        return mob
    }
}
