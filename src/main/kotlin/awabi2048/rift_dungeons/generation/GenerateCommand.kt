package awabi2048.rift_dungeons.generation

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object GenerateCommand : CommandExecutor {
    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>?): Boolean {
        if (p0 !is Player) {
            return true
        }

        if (p3?.size != 2) return true

        val style = p3[0].uppercase()
        val size = p3[1].toIntOrNull()?: return true

        val generator = Generator(Style.valueOf(style), size)
        generator.generate(p0.location.toBlockLocation())

        return true
    }
}
