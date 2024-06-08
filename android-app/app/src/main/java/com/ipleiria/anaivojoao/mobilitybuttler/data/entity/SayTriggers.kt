package com.ipleiria.anaivojoao.mobilitybuttler.data.entity

enum class SayTriggers(val keyWords: Set<String>) {
    TEMPERATURE(setOf("temperature")),
    MAIL(setOf("mails", "inbox", "mailbox")),
    KITCHEN(setOf("kitchen", "kit", "chen")),
    BEDROOM(setOf("bedroom", "room", "bed"));
    companion object {
        fun getAllCommands() = values().flatMap { set -> set.keyWords.map { it } }
    }
}

fun SayTriggers.isIn(text: String): Boolean =
    keyWords.sumOf { if (text.contains(it)) 1.toInt() else 0 } > 0

