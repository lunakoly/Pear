package ru.luna_koly.pear.json

/**
 * Holds functionality needed
 * for Json representation
 */
object Json {
    /**
     * Any node of a Json tree. Holds izi methods
     * for automatic unsafe casting to simplify
     * the usage of the parsed tree (provided the user
     * knows what the tree looks like)
     */
    interface Object {
        val value get() = (this as Item).value

        operator fun get(key: String): Object {
            return (this as Dictionary)[key]
        }

        operator fun get(index: Int): Object {
            return (this as List)[index]
        }
    }

    /**
     * Wrapper for String
     */
    class Item(override var value: String, private val quoted: Boolean) : Object {
        override fun toString(): String {
            if (quoted)
                return "\"$value\""
            return value
        }
    }

    /**
     * Wrapper for List
     */
    class List : ArrayList<Object>(), Object {
        override fun toString(): String {
            return '[' + joinToString(", ") { it.toString() } + ']'
        }

        override operator fun get(index: Int): Object {
            return super<ArrayList>.get(index)
        }

        fun item(value: String) {
            super.add(Item(value, true))
        }

        fun list(setup: (List.() -> Unit)) {
            super.add(List().apply(setup))
        }

        fun dictionary(setup: (Dictionary.() -> Unit)) {
            super.add(Dictionary().apply(setup))
        }
    }

    /**
     * Wrapper for Dictionary
     */
    class Dictionary : HashMap<String, Object>(), Object {
        override fun toString(): String {
            return '{' +
                    this
                        .map { (key, value) -> "\"$key\": $value" }
                        .joinToString(", ") { it } +
                    '}'
        }

        override operator fun get(key: String): Object {
            return super<HashMap>.get(key)!!
        }

        fun item(key: String, value: String) {
            super.put(key, Item(value, true))
        }

        fun list(key: String, setup: (List.() -> Unit)) {
            super.put(key, List().apply(setup))
        }

        fun dictionary(key: String, setup: (Dictionary.() -> Unit)) {
            super.put(key, Dictionary().apply(setup))
        }
    }

    fun dictionary(setup: Dictionary.() -> Unit) = Dictionary().apply(setup)
    fun list(setup: List.() -> Unit) = List().apply(setup)
}