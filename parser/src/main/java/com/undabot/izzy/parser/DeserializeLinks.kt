package com.undabot.izzy.parser

import com.undabot.izzy.models.Link
import com.undabot.izzy.models.Links

class DeserializeLinks(private val deserializeLink: DeserializeLink = DeserializeLink()) {

    fun from(jsonElements: JsonElements): Links? =
        when (hasLinks(jsonElements)) {
            true -> parseLinksFrom(jsonElements)
            false -> null
        }

    private fun parseLinksFrom(jsonElements: JsonElements) = Links(
        self = linkFrom(jsonElements, "self"),
        first = linkFrom(jsonElements, "first"),
        last = linkFrom(jsonElements, "last"),
        prev = linkFrom(jsonElements, "prev"),
        next = linkFrom(jsonElements, "next"),
        related = linkFrom(jsonElements, "related")
    )

    private fun hasLinks(jsonElements: JsonElements) =
        jsonElements.has(LINKS) && jsonElements.hasNonNull(LINKS)

    private fun linkFrom(jsonElements: JsonElements, forKey: String): Link? {
        val link = jsonElements.jsonElement(LINKS).jsonElement(forKey).asString()

        return if (!link.isNullOrEmpty())
            Link(href = link, meta = null)
        else null
    }

}
