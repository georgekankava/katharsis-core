package io.katharsis.dispatcher.controller.resource;

import io.katharsis.dispatcher.controller.BaseController;
import io.katharsis.dispatcher.controller.HttpMethod;
import io.katharsis.queryParams.RequestParams;
import io.katharsis.repository.RelationshipRepository;
import io.katharsis.request.dto.DataBody;
import io.katharsis.request.dto.RequestBody;
import io.katharsis.request.path.JsonPath;
import io.katharsis.request.path.PathIds;
import io.katharsis.request.path.RelationshipsPath;
import io.katharsis.resource.ResourceField;
import io.katharsis.resource.exception.RequestBodyException;
import io.katharsis.resource.exception.RequestBodyNotFoundException;
import io.katharsis.resource.exception.ResourceFieldNotFoundException;
import io.katharsis.resource.exception.ResourceNotFoundException;
import io.katharsis.resource.registry.RegistryEntry;
import io.katharsis.resource.registry.ResourceRegistry;
import io.katharsis.response.BaseResponse;
import io.katharsis.response.ResourceResponse;
import io.katharsis.utils.Generics;
import io.katharsis.utils.parser.TypeParser;

import java.io.Serializable;

public abstract class RelationshipsResourceUpsert implements BaseController {

    protected ResourceRegistry resourceRegistry;
    protected TypeParser typeParser;

    public RelationshipsResourceUpsert(ResourceRegistry resourceRegistry, TypeParser typeParser) {
        this.resourceRegistry = resourceRegistry;
        this.typeParser = typeParser;
    }

    /**
     * HTTP method name
     *
     * @return HTTP method name
     */
    public abstract HttpMethod method();

    /**
     * Processes To-Many field
     *
     * @param resource                       source resource
     * @param relationshipIdType             {@link Class} class of the relationship's id field
     * @param elementName                    field's name
     * @param dataBodies                     Data bodies with relationships
     * @param relationshipRepositoryForClass Relationship repository
     */
    protected abstract void processToManyRelationship(Object resource, Class<? extends Serializable> relationshipIdType, String elementName,
                                                      Iterable<DataBody> dataBodies, RelationshipRepository relationshipRepositoryForClass);

    /**
     * Processes To-One field
     *
     * @param resource                       source resource
     * @param relationshipIdType             {@link Class} class of the relationship's id field
     * @param elementName                    field's name
     * @param dataBody                       Data body with a relationship
     * @param relationshipRepositoryForClass Relationship repository
     */
    protected abstract void processToOneRelationship(Object resource, Class<? extends Serializable> relationshipIdType,
                                                     String elementName, DataBody dataBody, RelationshipRepository relationshipRepositoryForClass);

    @Override
    public final boolean isAcceptable(JsonPath jsonPath, String requestType) {
        return !jsonPath.isCollection()
                && jsonPath instanceof RelationshipsPath
                && method().name().equals(requestType);
    }

    @Override
    public final BaseResponse<?> handle(JsonPath jsonPath, RequestParams requestParams, RequestBody requestBody) throws Exception {
        String resourceName = jsonPath.getResourceName();
        PathIds resourceIds = jsonPath.getIds();
        RegistryEntry registryEntry = resourceRegistry.getEntry(resourceName);

        if (registryEntry == null) {
            throw new ResourceNotFoundException(resourceName);
        }
        if (requestBody == null) {
            throw new RequestBodyNotFoundException(HttpMethod.POST, resourceName);
        }

        Serializable castedResourceId = getResourceId(resourceIds, registryEntry);
        ResourceField relationshipField = registryEntry.getResourceInformation().findRelationshipFieldByName(jsonPath
            .getElementName());
        if (relationshipField == null) {
            throw new ResourceFieldNotFoundException(jsonPath.getElementName());
        }
        Object resource = registryEntry.getResourceRepository().findOne(castedResourceId, requestParams);

        Class<?> baseRelationshipFieldClass = relationshipField.getType();
        Class<?> relationshipFieldClass = Generics
            .getResourceClass(relationshipField.getGenericType(), baseRelationshipFieldClass);
        Class<? extends Serializable> relationshipIdType = (Class<? extends Serializable>) resourceRegistry
                .getEntry(relationshipFieldClass).getResourceInformation().getIdField().getType();

        RelationshipRepository relationshipRepositoryForClass = registryEntry.getRelationshipRepositoryForClass(relationshipFieldClass);
        if (Iterable.class.isAssignableFrom(baseRelationshipFieldClass)) {
            if (!requestBody.isMultiple()) {
                throw new RequestBodyException(HttpMethod.POST, resourceName, "Non-multiple data in body");
            }
            Iterable<DataBody> dataBodies = requestBody.getMultipleData();
            processToManyRelationship(resource, relationshipIdType, jsonPath.getElementName(), dataBodies, relationshipRepositoryForClass);
        } else {
            if (requestBody.isMultiple()) {
                throw new RequestBodyException(HttpMethod.POST, resourceName, "Multiple data in body");
            }
            DataBody dataBody = requestBody.getSingleData();
            processToOneRelationship(resource, relationshipIdType, jsonPath.getElementName(), dataBody, relationshipRepositoryForClass);
        }

        return new ResourceResponse();
    }

    private Serializable getResourceId(PathIds resourceIds, RegistryEntry<?> registryEntry) {
        String resourceId = resourceIds.getIds().get(0);
        Class<? extends Serializable> idClass = (Class<? extends Serializable>) registryEntry
                .getResourceInformation()
                .getIdField()
                .getType();
        return typeParser.parse(resourceId, idClass);
    }
}
