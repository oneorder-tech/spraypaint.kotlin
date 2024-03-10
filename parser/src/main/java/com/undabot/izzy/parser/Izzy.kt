package com.undabot.izzy.parser

import com.undabot.izzy.models.Errors
import com.undabot.izzy.models.IzzyResource
import com.undabot.izzy.models.JsonDocument

class Izzy(private val izzyJsonParser: IzzyJsonParser) {

    private val deserializeLinks = DeserializeLinks()
    private val validateJsonDocument = ValidateJsonDocument()
    private val deserializeErrors = DeserializeErrors()
    private val deserializeMeta = DeserializeMeta()
    private val deserializeData = DeserializeData(izzyJsonParser)

    /**
     *  Deserializes the string to a JsonDocument.
     *  @param json json-API compliant json in string format.
     * */

    fun <T : IzzyResource> deserializeToDocument(json: String): JsonDocument<T> {
        val jsonTree = izzyJsonParser.parseToJsonElements(json)
        val jsonTree2 = izzyJsonParser.parseToJsonElements(json)
        validate(jsonTree)

        var resource: T? = null
        var errors: Errors? = null

        if (jsonTree.has(DATA)) {
            resource = deserializeData.forSingleResource(jsonTree)
        } else if (jsonTree.has(ERRORS)) {
            errors = deserializeErrors.from(jsonTree)
        }

        return JsonDocument(
            data = resource,
            links = linksFrom(jsonTree2),
            errors = errors,
            meta = metaFrom(jsonTree2)
        )
    }

    /**
     *  Deserializes the string to a JsonDocument containing a list of data objects.
     *  @param json json-API compliant json in string format.
     * */
    fun <T : IzzyResource> deserializeToCollection(json: String): JsonDocument<List<T>> {
        val jsonTree = izzyJsonParser.parseToJsonElements(json)
        val jsonTree2 = izzyJsonParser.parseToJsonElements(json)
        validate(jsonTree)

        var resourceCollection: List<T>? = null
        var errors: Errors? = null

        if (jsonTree.has(DATA)) {
            resourceCollection = deserializeData.forResourceCollection(jsonTree)
        } else if (jsonTree.has(ERRORS)) {
            errors = deserializeErrors.from(jsonTree)
        }

        return JsonDocument(
            data = resourceCollection,
            links = linksFrom(jsonTree2),
            errors = errors,
            meta = metaFrom(jsonTree2)
        )
    }

    /**
     * Serialises resource into a JSON-API compliant json string
     * @param item item to serialize.
     */
    fun <T : IzzyResource> serializeItem(item: T): String {
        val serializer = ResourceToSerializableDocumentMapper(RelationshipFieldMapper())
        val data = serializer.mapFrom(item)

        val document = JsonDocument(
            data = data.toData(),
            included = data.getIncludedList()
        )
        return izzyJsonParser.documentToJson(document).replace(nullableField(), nullValue())
    }

    /**
     * Serialises resource collection into a JSON-API compliant json string
     * @param item resource collection to serialize.
     */

    fun <T : IzzyResource> serializeItemCollection(item: List<T>): String {
        val mapper = ResourceToSerializableDocumentMapper(RelationshipFieldMapper())
        val document = JsonDocument(item.map { mapper.mapFrom(it) })
        return izzyJsonParser.documentCollectionToJson(document).replace(nullableField(), nullValue())
    }

    @SuppressWarnings("FunctionOnlyReturningConstant")
    private fun nullValue(): String = "null"

    private fun nullableField() = "\"" + DataWrapper.NULLABLE_FIELD + "\""

    private fun linksFrom(jsonTree: JsonElements) = deserializeLinks.from(jsonTree)

    private fun metaFrom(jsonTree: JsonElements) = deserializeMeta.fromRoot(jsonTree)

    private fun validate(jsonTree: JsonElements) = validateJsonDocument.from(jsonTree)
}
