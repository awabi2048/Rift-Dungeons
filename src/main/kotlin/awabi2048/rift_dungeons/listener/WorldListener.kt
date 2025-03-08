package awabi2048.rift_dungeons.listener

import awabi2048.rift_dungeons.Lib
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldLoadEvent

object WorldListener: Listener {
    @EventHandler
    fun onWorldLoad(event: WorldLoadEvent) {


        val worldName = event.world.name
        val session =  Lib.getSession(worldName)?: return

        session.load()
    }
}
