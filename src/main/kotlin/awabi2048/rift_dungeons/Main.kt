package awabi2048.rift_dungeons

import awabi2048.rift_dungeons.commands.StartDungeonCommand
import awabi2048.rift_dungeons.config.DataFile
import awabi2048.rift_dungeons.config.MobData
import awabi2048.rift_dungeons.generation.GenerateCommand
import awabi2048.rift_dungeons.listener.WorldListener
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Main : JavaPlugin() {
    companion object {
        lateinit var instance: JavaPlugin
        var dungeonSessions: MutableSet<DungeonSession> = mutableSetOf()
    }

    override fun onEnable() {
        instance = this

        DataFile.copy()
        DataFile.load()

        getCommand("generate")?.setExecutor(GenerateCommand)
        getCommand("start_dungeons")?.setExecutor(StartDungeonCommand)

        instance.server.pluginManager.registerEvents(WorldListener, instance)
    }

    override fun onDisable() {
        // Plugin shutdown logic
        Bukkit.getWorlds().forEach {
            if (it.name.startsWith("rd_session.")) {
                it.players.forEach {player -> player.kick(Component.text("§cサーバーがシャットダウンしました。"))}
//                it.players.forEach {it.teleport(Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0))}

                Bukkit.unloadWorld(it, true)
                val worldFile = File(Bukkit.getWorldContainer().path + File.separator + it.name)
                println(worldFile)
                worldFile.deleteRecursively()
            }
        }
    }
}
