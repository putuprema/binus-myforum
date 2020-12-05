package xyz.purema.binusmyforum.domain

interface EntityMapper<Entity, DomainModel> {
    fun mapFromEntity(entity: Entity): DomainModel
    fun mapToEntity(domainModel: DomainModel): Entity
    fun mapFromEntities(entities: List<Entity>): List<DomainModel>
}