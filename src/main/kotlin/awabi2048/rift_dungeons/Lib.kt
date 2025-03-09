package awabi2048.rift_dungeons

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.structure.Mirror
import org.bukkit.block.structure.StructureRotation
import org.joml.AxisAngle4f
import org.joml.Quaternionf
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

    fun toQuaternion(yaw: Float, pitch: Float, roll: Float): Quaternionf {
        val qYaw = Quaternionf(AxisAngle4f(Math.toRadians(yaw.toDouble()).toFloat(), 0f, 1f, 0f))
        val qPitch = Quaternionf(AxisAngle4f(Math.toRadians(pitch.toDouble()).toFloat(), 1f, 0f, 0f))
        val qRoll = Quaternionf(AxisAngle4f(Math.toRadians(roll.toDouble()).toFloat(), 0f, 0f, 1f))

        return qYaw.mul(qPitch).mul(qRoll)
    }
}
