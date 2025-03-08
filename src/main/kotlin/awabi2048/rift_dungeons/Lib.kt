package awabi2048.rift_dungeons

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.structure.Mirror
import org.bukkit.block.structure.StructureRotation
import java.io.File
import java.util.*

object Lib {
    fun loadStructure(location: Location, path: String, rotation: StructureRotation) {
        val structureManager = Bukkit.getStructureManager()
        structureManager.loadStructure(
            File(
                Main.instance.dataFolder.toString() + File.separator + "structure/$path.nbt".replace(
                    "/",
                    File.separator
                )
            )
        ).place(
            location,
            true,
            rotation,
            Mirror.NONE,
            0,
            1.0f,
            Random(0)
        )
    }

    fun getSession(name: String): DungeonSession? {
        if (!name.startsWith("rd_session.")) return null

        try {
            val uuid = UUID.fromString(name.substringAfter("rd_session."))
            val session = Main.dungeonSessions.find { it.uuid == uuid }

            return session
        } catch (e: Exception) {
            return null
        }
    }
}
