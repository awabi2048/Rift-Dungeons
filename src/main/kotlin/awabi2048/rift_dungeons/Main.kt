package awabi2048.rift_dungeons

import awabi2048.rift_dungeons.generation.GenerateCommand
import awabi2048.rift_dungeons.generation.StructureCommand
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    companion object {
        lateinit var instance: JavaPlugin
    }

    override fun onEnable() {
        instance = this
        getCommand("generate")?.setExecutor(GenerateCommand)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
