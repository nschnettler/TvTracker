package de.schnettler.tvtracker.data.mapping

interface Mapper<I, O, P> {
    fun mapToDatabase(input: I, index: Int = 0): O
    fun mapToDomain(input: O, index: Int = 0): P
}

class ListMapper<I, O, P>(
    private val mapper: Mapper<I, O, P>
) {
    fun mapToDatabase(input: List<I>?)= input?.mapIndexed { index, it ->
            mapper.mapToDatabase(it, index)
    }

    fun mapToDomain(input: List<O>?) = input?.mapIndexed { index, it ->
        mapper.mapToDomain(it, index)
    }
}