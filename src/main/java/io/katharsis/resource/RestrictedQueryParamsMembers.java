package io.katharsis.resource;

public enum RestrictedQueryParamsMembers {
    /**
     * Set of collection's fields used for filtering
     */
    FILTER,
    /**
     * Set of collection's fields used for sorting
     */
    SORT,
    /**
     * Field to group by the collection
     */
    GROUP,
    /**
     * Pagination properties
     */
    PAGE,
    /**
     * List of specified fields to include in models
     */
    FIELDS,
    /**
     * Additional resources that should be attached to response
     */
    INCLUDE
}
