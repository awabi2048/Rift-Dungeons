package awabi2048.rift_dungeons.commands

import awabi2048.rift_dungeons.DungeonSession
import awabi2048.rift_dungeons.Main.Companion.instance
import awabi2048.rift_dungeons.generation.Generator
import awabi2048.rift_dungeons.generation.Style
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object StartDungeonCommand: CommandExecutor {
    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>?): Boolean {
        if (p0 !is Player) {
            p0.sendMessage("このコマンドはプレイヤーからのみ実行可能です。")
            return true
        }

        if (p3?.size != 2) {
            p0.sendMessage("無効なコマンドです。")
            return true
        }

        val style: Style
        val size: Int

        try {
            style = Style.valueOf(p3[0])
            size = p3[1].toInt()
        } catch (e: Exception) {
            p0.sendMessage("無効なコマンドです。")
            return true
        }

        val uuid = UUID.randomUUID()
        val session = DungeonSession(
            uuid = uuid,
            joinedPlayers = setOf(p0),
            generator = Generator(style, size),
            sectionLoadStates = null,
        )

        session.initiate()

        Bukkit.getScheduler().runTaskLater(
            instance,
            Runnable {
                p0.sendMessage("セッション(UUID ${session.uuid})を開始しました。")
                p0.teleport(Location(session.world, 0.0, 0.0, 0.0))
            },
            40L
        )

        return true
    }
}
