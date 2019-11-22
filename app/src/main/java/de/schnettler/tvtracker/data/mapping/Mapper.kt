package de.schnettler.tvtracker.data.mapping

interface Mapper<I, O, P> {
    fun mapToDatabase(input: I): O
    fun mapToDomain(input: O): P
}

interface IndexedMapper<I, O, P> {
    fun mapToDatabase(input: I, index: Int): O
    fun mapToDomain(input: O, index: Int): P
}

interface IndexedMapperWithId<I, O, P> {
    fun mapToDatabase(input: I, index: Int, id: Long): O
    fun mapToDomain(input: O, index: Int, id: Long): P
}

class ListMapper<I, O, P>(private val mapper: IndexedMapper<I, O, P>) {
    fun mapToDatabase(input: List<I>?)= input?.mapIndexed { index, it ->
            mapper.mapToDatabase(it, index)
    }

    fun mapToDomain(input: List<O>?) = input?.mapIndexed { index, it ->
        mapper.mapToDomain(it, index)
    }
}

class ListMapperWithId<I, O, P>(private val mapper: IndexedMapperWithId<I, O, P>) {
    fun mapToDatabase(input: List<I>?, id: Long = 0)= input?.mapIndexed { index, it ->
        mapper.mapToDatabase(it, index, id)
    }

    fun mapToDomain(input: List<O>?, id: Long = 0) = input?.mapIndexed { index, it ->
        mapper.mapToDomain(it, index, id)
    }
}