package io.github.nekohasekai.pm.manage.menu

import io.github.nekohasekai.nekolib.core.utils.*
import io.github.nekohasekai.nekolib.i18n.LocaleController
import io.github.nekohasekai.pm.*
import io.github.nekohasekai.pm.database.UserBot
import io.github.nekohasekai.pm.manage.BotHandler
import io.github.nekohasekai.pm.manage.MyBots

class BotMenu : BotHandler() {

    companion object {

        const val dataId = DATA_EDIT_BOT

    }

    override fun onLoad() {

        initData(dataId)

        sudo addHandler StartMessagesMenu()
        sudo addHandler IntegrationMenu()
        sudo addHandler DeleteMenu()
        sudo addHandler PreferencesMenu()
        sudo addHandler CommandsMenu()

    }

    fun botMenu(userId: Int, chatId: Long, messageId: Long, isEdit: Boolean, botUserId: Int, userBot: UserBot?) {

        val L = LocaleController.forChat(userId)

        sudo make L.BOT_EDITS.input(botName(botUserId, userBot), botUserName(botUserId, userBot)) withMarkup inlineButton {

            val botId = botUserId.toByteArray()

            dataLine(L.MENU_START_MESSAGES, StartMessagesMenu.dataId, botId)
            dataLine(L.MENU_INTEGRATION, IntegrationMenu.dataId, botId)
            dataLine(L.MENU_OPTIONS, PreferencesMenu.dataId, botId)
            dataLine(L.MENU_COMMANDS, CommandsMenu.dataId, botId)

            if (userBot != null) {

                dataLine(L.MENU_BOT_DELETE, DeleteMenu.dataId, botId)

            }

            dataLine(L.MENU_BACK_TO_BOT_LIST, MyBots.dataId)

        } onSuccess {

            if (!isEdit) findHandler<MyBots>().saveActionMessage(userId, it.id)

        } at messageId edit isEdit sendOrEditTo chatId

    }

    override suspend fun onNewBotCallbackQuery(userId: Int, chatId: Long, messageId: Long, queryId: Long, data: Array<ByteArray>, botUserId: Int, userBot: UserBot?) {

        sudo confirmTo queryId

        botMenu(userId, chatId, messageId, true, botUserId, userBot)

    }

}