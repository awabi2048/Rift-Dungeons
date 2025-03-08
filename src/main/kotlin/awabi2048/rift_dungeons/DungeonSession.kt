package awabi2048.rift_dungeons

import awabi2048.rift_dungeons.Main.Companion.dungeonSessions
import awabi2048.rift_dungeons.Main.Companion.instance
import awabi2048.rift_dungeons.generation.Generator
import net.kyori.adventure.text.TextComponent
import org.bukkit.*
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Barrel
import org.bukkit.entity.EntityType
import org.bukkit.entity.Interaction
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.generator.ChunkGenerator
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import org.joml.Matrix4f
import java.util.*
import javax.annotation.Nonnull

class DungeonSession(
    val uuid: UUID,
    val joinedPlayers: Set<Player>,
    val generator: Generator,
    var sectionLoadStates: MutableMap<Vector, Boolean>?,
) {

    val size = generator.size
    val style = generator.style

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
                    it.location.toBlockLocation().block.setType(Material.BARREL, true)
                    (it.location.toBlockLocation().block as Barrel).facing = BlockFace.UP
                }

            nearbyEntities.filter { (it.customName() as TextComponent).content() == "ITEM" }
                .forEach {
                    it.remove()
                    it.location.toBlockLocation().block.setType(Material.EMERALD_BLOCK, true)
                }

            nearbyEntities.filter { (it.customName() as TextComponent).content() == "MOB" }
                .forEach {
                    it.remove()
                    world?.spawnEntity(it.location, EntityType.ZOMBIE)
                }
        }
    }

    private fun loadMob(location: Location) {

    }

    private fun loadLootChest(location: Location) {
        location.block.setType(Material.BARREL, true)
        (location.block as Barrel).facing = BlockFace.UP

        val interaction = location.world.spawnEntity(location, EntityType.INTERACTION) as Interaction
        interaction.interactionWidth = 1.05f
        interaction.interactionHeight = 1.05f
        interaction.isResponsive = true
        interaction.addScoreboardTag("rd.interaction.loot_chest")
    }

    private fun loadLootItem(location: Location) {
        // アイテムをばら撒く
        val itemDisplay = location.world.spawnEntity(location, EntityType.ITEM_DISPLAY) as ItemDisplay
        itemDisplay.setItemStack(ItemStack(Material.STONE_SWORD))
        itemDisplay.transformation.leftRotation =
    }
}
