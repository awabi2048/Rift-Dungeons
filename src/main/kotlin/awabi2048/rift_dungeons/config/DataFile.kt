package awabi2048.rift_dungeons.config

import awabi2048.rift_dungeons.Main.Companion.instance
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object DataFile {
    lateinit var mobDefinitionFile: YamlConfiguration
    lateinit var mobSpawnRuleFile: YamlConfiguration
    lateinit var configFile: YamlConfiguration

    fun copy() {
        instance.saveResource("mob_definition.yml", false)
        instance.saveResource("mob_spawn_rule.yml", false)
        instance.saveResource("config.yml", false)
    }

    fun load() {
        configFile = YamlUtil.load("config.yml")
        mobDefinitionFile = YamlUtil.load("mob_definition.yml")
        mobSpawnRuleFile = YamlUtil.load("mob_spawn_rule.yml")
    }

    object YamlUtil {
        fun load(filePath: String): YamlConfiguration {
            val settingDataFile = File(instance.dataFolder.path + File.separator + filePath.replace("/", File.separator))
            return YamlConfiguration.loadConfiguration(settingDataFile)
        }

        fun save(filePath: String, yamlSection: YamlConfiguration): Boolean {
            try {
                val settingDataFile =
                    File(instance.dataFolder.toString() + File.separator + filePath.replace("/", File.separator))
                yamlSection.save(settingDataFile)

                return true
            } catch (e: Exception) {
                return false
            }
        }
    }

}
