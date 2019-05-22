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
    class List(setup: (List.() -> Unit)? = null) : ArrayList<Object>(), Object {
        init {
            setup?.invoke(this)
        }

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
            super.add(List(setup))
        }

        fun dictionary(setup: (Dictionary.() -> Unit)) {
            super.add(Dictionary(setup))
        }
    }

    /**
     * Wrapper for Dictionary
     */
    class Dictionary(setup: (Dictionary.() -> Unit)? = null) : HashMap<String, Object>(), Object {
        init {
            setup?.invoke(this)
        }

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
            super.put(key, List(setup))
        }

        fun dictionary(key: String, setup: (Dictionary.() -> Unit)) {
            super.put(key, Dictionary(setup))
        }
    }
}