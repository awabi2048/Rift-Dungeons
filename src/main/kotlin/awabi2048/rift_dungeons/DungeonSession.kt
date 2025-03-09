package awabi2048.rift_dungeons

import awabi2048.rift_dungeons.Main.Companion.dungeonSessions
import awabi2048.rift_dungeons.Main.Companion.instance
import awabi2048.rift_dungeons.generation.Generator
import net.kyori.adventure.text.TextComponent
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Barrel
import org.bukkit.entity.Damageable
import org.bukkit.entity.EntityType
import org.bukkit.entity.Interaction
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.generator.ChunkGenerator
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Transformation
import org.bukkit.util.Vector
import org.joml.Matrix4f
import org.joml.Quaternionf
import java.util.*
import javax.annotation.Nonnull
import kotlin.math.pow
import kotlin.math.roundToInt

class DungeonSession(
    val uuid: UUID,
    val joinedPlayers: Set<Player>,
    val generator: Generator,
    var sectionLoadStates: MutableMap<Vector, Boolean>?,
) {

    val size = generator.size
    val style = generator.style

    private val mobSpawner = MobSpawner(style)

    val world: World?
        get() {
            return Bukkit.getWorld("rd_session.$uuid")
        }

    val activePlayers: Set<Player>?
        get() {
            return world?.players?.toSet()
        }

    fun initiate() {
        class EmptyChunkGenerator : ChunkGenerator() {
            @Nonnull
            fun chunkDataGeneration(
                @Nonnull world: World?,
                @Nonnull random: Random?,
                x: Int,
                z: Int,
                @Nonnull biome: BiomeGrid?,
            ): ChunkData {
                return createChunkData(world!!)
            }
        }

        //
        sectionLoadStates = mutableMapOf()

        dungeonSessions.add(this)

        // ワールド作成
        val creator = WorldCreator("rd_session.$uuid")
        creator.generator(EmptyChunkGenerator())
        creator.createWorld()

        // ワールドの設定
        world?.setGameRule(GameRule.MOB_GRIEFING, false)
        world?.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        world?.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        world?.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
    }

    fun load() {
        // 地形生成を開始
        generator.generate(Location(world, 0.0, 0.0, 0.0))

        // tick を開始
        object : BukkitRunnable() {
            override fun run() {
                tick()
            }
        }.runTaskTimer(instance, 20L, 20L)

        println(sectionLoadStates)
    }

    fun tick() {
        activePlayers?.forEach { player ->
            val nearbyEntities = player.location.getNearbyEntities(48.0, 48.0, 48.0)
                .filter { it.type == EntityType.ARMOR_STAND }

            nearbyEntities.filter { (it.customName() as TextComponent).content() == "CHEST" }
                .forEach {
                    it.remove()
                    loadLootChest(it.location.toBlockLocation())
                }

            nearbyEntities.filter { (it.customName() as TextComponent).content() == "ITEM" }
                .forEach {
                    it.remove()
                    loadLootItem(it.location.toBlockLocation())
                }

            nearbyEntities.filter { (it.customName() as TextComponent).content() == "MOB" }
                .forEach {
                    it.remove()
                    loadMob(it.location.toBlockLocation())
                }
        }
    }

    private fun loadMob(location: Location) {
        mobSpawner.spawn(location)
    }

    private fun loadLootChest(location: Location) {
        location.block.setType(Material.BARREL, true)
    }

    private fun loadLootItem(location: Location) {
        // アイテムをばら撒く
        val itemDisplay = location.world.spawnEntity(location.toCenterLocation(), EntityType.ITEM_DISPLAY) as ItemDisplay
        itemDisplay.setItemStack(ItemStack(Material.STONE_SWORD))

        val quaternion = Quaternionf().rotateY(Math.toRadians(90.0).toFloat())

        val transformation = itemDisplay.transformation

        itemDisplay.transformation = Transformation(
            transformation.translation,
            quaternion,
            transformation.scale,
            transformation.rightRotation
        )

        val interaction = location.world.spawnEntity(location, EntityType.INTERACTION) as Interaction
        interaction.interactionWidth = 0.75f
        interaction.interactionHeight = 0.2f
        interaction.addScoreboardTag("rd.interaction.loot_item")
    }
}
