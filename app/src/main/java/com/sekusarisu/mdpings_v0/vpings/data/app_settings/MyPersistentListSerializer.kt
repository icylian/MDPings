package com.sekusarisu.mdpings_v0.vpings.data.app_settings

import com.sekusarisu.mdpings_v0.vpings.domain.Instance
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(ExperimentalSerializationApi::class)
//@Serializer(forClass = PersistentList::class)
class MyPersistentListSerializer(
    private val serializer: KSerializer<Instance>,
) : KSerializer<PersistentList<Instance>> {

    private class PersistentListDescriptor :
        SerialDescriptor by serialDescriptor<List<Instance>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<Instance>) {
        return ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<Instance> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }

}