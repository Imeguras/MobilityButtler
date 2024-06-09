package com.ipleiria.anaivojoao.mobilitybuttler.data.entity


enum class VoiceCommandEntity(val keyWords: Set<String>, var params: String? = null) {
    EXIT(setOf("exit")),
    SAY(setOf("say", "tell")),

    MOVE(setOf("go", "move")),
    BACK(setOf("back", "previous")),
    REMEMBER(setOf("remember me", "remind me", "don't forget")),
    BACKGROUND(setOf());

    companion object {
        fun processCommand(output: String): VoiceCommandEntity =
            values().firstOrNull { command ->
                command.keyWords.firstOrNull { word -> output.trim().contains(word) } != null
            }?.applyParams(output) ?: BACKGROUND

        fun getAllCommands() = values().flatMap { set -> set.keyWords.map { it } }
    }
}

fun VoiceCommandEntity.isIn(text: String): Boolean =
    keyWords.sumOf { if (text.contains(it)) 1.toInt() else 0 } > 0


fun VoiceCommandEntity.applyParams(text: String): VoiceCommandEntity = this.apply {
    params = text
    keyWords.forEach { params = params?.replace(it, "", true) }
}