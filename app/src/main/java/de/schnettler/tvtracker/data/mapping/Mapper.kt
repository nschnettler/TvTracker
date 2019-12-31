package de.schnettler.tvtracker.data.mapping

interface Mapper<I, O, P> {
    fun mapToDatabase(input: I): O
    fun mapToDomain(input: O): P
}

interface IndexedMapper<I, O, P> {
    fun mapToDatabase(input: I, index: Int): O
    fun mapToDomain(input: O): P
}

interface IndexedMapperWithId<I, O, P> {
    fun mapToDatabase(input: I, index: Int, vararg ids: Long): O
    fun mapToDomain(input: O): P
}

interface MapperWithId<I, O, P> {
    fun mapToDatabase(input: I, vararg ids: Long): O
    fun mapToDomain(input: O): P
}

class ListMapper<I, O, P>(private val mapper: IndexedMapper<I, O, P>) {
    fun mapToDatabase(input: List<I>?)= input?.mapIndexed { index, it ->
            mapper.mapToDatabase(it, index)
    }

    fun mapToDomain(input: List<O>?) = input?.map {it ->
        mapper.mapToDomain(it)
    }
}

class ListMapperWithId<I, O, P>(private val mapper: IndexedMapperWithId<I, O, P>) {
    fun mapToDatabase(input: List<I>?, vararg ids: Long)= input?.mapIndexed { index, it ->
        mapper.mapToDatabase(it, index, *ids)
    }

    fun mapToDomain(input: List<O>?) = input?.map { it ->
        mapper.mapToDomain(it)
    }
}