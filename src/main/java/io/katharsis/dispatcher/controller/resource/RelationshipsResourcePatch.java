package io.katharsis.dispatcher.controller.resource;

import io.katharsis.dispatcher.controller.HttpMethod;
import io.katharsis.repository.RelationshipRepository;
import io.katharsis.request.dto.DataBody;
import io.katharsis.resource.registry.ResourceRegistry;
import io.katharsis.utils.parser.TypeParser;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class RelationshipsResourcePatch extends RelationshipsResourceUpsert {

    public RelationshipsResourcePatch(ResourceRegistry resourceRegistry, TypeParser typeParser) {
        super(resourceRegistry, typeParser);
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.PATCH;
    }

    @Override
    public void processToManyRelationship(Object resource, Class<? extends Serializable> relationshipIdType, String elementName,
                                          Iterable<DataBody> dataBodies, RelationshipRepository relationshipRepositoryForClass) {
        List<Serializable> parsedIds = new LinkedList<>();
        for (DataBody dataBody : dataBodies) {
            Serializable parsedId = typeParser.parse(dataBody.getId(), relationshipIdType);
            parsedIds.add(parsedId);
        }
        //noinspection unchecked
        relationshipRepositoryForClass.setRelations(resource, parsedIds, elementName);
    }

    @Override
    protected void processToOneRelationship(Object resource, Class<? extends Serializable> relationshipIdType,
                                            String elementName, DataBody dataBody, RelationshipRepository relationshipRepositoryForClass) {
        Serializable parsedId = null;
        if (dataBody != null) {
            parsedId = typeParser.parse(dataBody.getId(), relationshipIdType);
        }
        //noinspection unchecked
        relationshipRepositoryForClass.setRelation(resource, parsedId, elementName);
    }
}
