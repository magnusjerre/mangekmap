fun String.replacePathVariables(vararg params: Any?): String {
    val splitted = split("/")
    var index = 0
    return splitted.map {
        if (it.startsWith("{"))
            params[index++]
        else
            it
    }.joinToString("/")
}
