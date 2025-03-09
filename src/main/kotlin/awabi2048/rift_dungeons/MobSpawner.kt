package awabi2048.rift_dungeons

import awabi2048.rift_dungeons.config.DataFile
import awabi2048.rift_dungeons.config.MobData
import awabi2048.rift_dungeons.generation.Style
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import kotlin.math.pow

class MobSpawner(private val style: Style) {
    fun spawn(location: Location): LivingEntity {

        val spawnCandidate = MobData.mobSpawnRules[style]?: throw IllegalStateException()
        val spawnId = spawnCandidate.randomOrNull()?: throw IllegalStateException()

        val multiplier =  (1 + location.length() / 100).pow(0.5)
        val mob = MobData.spawnFromId(spawnId, location)

        mob.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue *= multiplier
        mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)!!.baseValue *= multiplier
        mob.getAttribute(Attribute.GENERIC_SCALE)!!.baseValue *= (95..105).random() / 100.0
        mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)!!.baseValue = multiplier - 1

        mob.health = mob.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value

        return mob
    }
}
