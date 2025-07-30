package ru.steps

import io.obswebsocket.community.client.OBSRemoteController

object OBSServer {

    private val timeout = 10000L

    private val controller = OBSRemoteController.builder()
        .host("localhost")
        .port(4455)
        .password("KwFRFrXMiHALModu")
        .connectionTimeout(3)
        .build()

    fun modifyStreamPashka(isEnable: Boolean) {
        controller.connect()
        val scenes = controller.getSceneList(timeout)
        controller.getSceneItemId("Шагаем", "телефонПашка", 0) { item ->
            controller.setSceneItemEnabled("Шагаем", item.sceneItemId, isEnable, timeout)
        }
    }

}