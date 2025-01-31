package tk.zt64.plugins

import android.content.Context
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.discord.widgets.servers.WidgetServerSettingsBans

@AliucordPlugin
class BanCount: Plugin() {
    override fun start(context: Context) {
        patcher.patch(WidgetServerSettingsBans::class.java.getDeclaredMethod("configureUI", WidgetServerSettingsBans.Model::class.java), Hook {
            val model = it.args[0] as WidgetServerSettingsBans.Model

            with(it.thisObject as WidgetServerSettingsBans) {
                actionBarTitleLayout.setSubtitle("${model.guildName} (${Utils.pluralise(model.totalBannedUsers, "ban")})")
            }
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}