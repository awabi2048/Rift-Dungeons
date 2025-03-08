package awabi2048.rift_dungeons.generation

import awabi2048.rift_dungeons.Lib
import org.bukkit.block.structure.StructureRotation
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.Vector

object StructureCommand: CommandExecutor {
    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>?): Boolean {
        if (p3?.size != 2) return true
        if (p0 !is Player) return true
        
        val name = p3[0]
        val rotation = p3[1].toIntOrNull()?: return true
        val location = p0.location.toBlockLocation().clone().add(Vector(-4, 0, -4))

        val placeLocation = when(rotation) {
            0 -> location
            1 -> location.clone().add(Vector(0, 0, 8))
            2 -> location.clone().add(Vector(8, 0, 8))
            3 -> location.clone().add(Vector(8, 0, 0))
            else -> throw IllegalStateException()
        }

        val structureRotation = when(rotation) {
            0 -> StructureRotation.NONE
            1 -> StructureRotation.COUNTERCLOCKWISE_90
            2 -> StructureRotation.CLOCKWISE_180
            3 -> StructureRotation.CLOCKWISE_90
            else -> throw IllegalStateException()
        }

        Lib.loadStructure(placeLocation, name, structureRotation)
        println("PLACED AT ($placeLocation), $name, $structureRotation")
        return true
    }
}
