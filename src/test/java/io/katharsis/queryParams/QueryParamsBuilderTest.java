package io.katharsis.queryParams;

import io.katharsis.jackson.exception.ParametersDeserializationException;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryParamsBuilderTest {

    private Map<String, Set<String>> queryParams;
    private QueryParamsBuilder sut;

    @Before
    public void prepare() {
        queryParams = new HashMap<>();
        sut = new QueryParamsBuilder(new DefaultQueryParamsParser());
    }

    @Test
    public void onGivenFiltersBuilderShouldReturnRequestParamsWithFilters() throws ParametersDeserializationException {
        // GIVEN
        queryParams.put("FILTER[users][name]", Collections.singleton("John"));

        // WHEN
        QueryParams result = sut.buildQueryParams(queryParams);

        // THEN
        assertThat(result.getFilters()
            .getParams()
            .get("users")).isNotNull();

        assertThat(result.getFilters()
            .getParams()
            .get("users")
            .getParams()
            .get("name")).isEqualTo(Collections.singleton("John"));
    }

    @Test
    public void onGivenSortingBuilderShouldReturnRequestParamsWithSorting() throws ParametersDeserializationException {
        // GIVEN
        queryParams.put("SORT[users][name]", Collections.singleton("ASC"));

        // WHEN
        QueryParams result = sut.buildQueryParams(queryParams);

        // THEN
        assertThat(result.getSorting()
            .getParams()
            .get("users")).isNotNull();

        assertThat(result.getSorting()
            .getParams()
            .get("users")
            .getParams()
            .get("name")).isEqualTo(RestrictedSortingValues.ASC);

    }

    @Test
    public void onGivenGroupingBuilderShouldReturnRequestParamsWithGrouping() throws
        ParametersDeserializationException {
        // GIVEN
        queryParams.put("GROUP[users]", Collections.singleton("name"));

        // WHEN
        QueryParams result = sut.buildQueryParams(queryParams);

        // THEN
        assertThat(result.getGrouping()
            .getParams()
            .get("users")).isNotNull();

        assertThat(result.getGrouping()
            .getParams()
            .get("users")
            .getParams()
            .iterator()
            .next()).isEqualTo("name");
    }


    @Test
    public void onGivenPaginationBuilderShouldReturnRequestParamsWithPagination() throws
        ParametersDeserializationException {
        // GIVEN
        queryParams.put("PAGE[OFFSET]", Collections.singleton("0"));
        queryParams.put("PAGE[LIMIT]", Collections.singleton("10"));

        // WHEN
        QueryParams result = sut.buildQueryParams(queryParams);

        // THEN
        assertThat(result.getPagination()
            .get(RestrictedPaginationKeys.OFFSET)).isEqualTo(0);
        assertThat(result.getPagination()
            .get(RestrictedPaginationKeys.LIMIT)).isEqualTo(10);
    }

    @Test
    public void onGivenIncludedFieldsBuilderShouldReturnRequestParamsWithIncludedFields() throws
        ParametersDeserializationException {
        // GIVEN
        queryParams.put("FIELDS[users]", Collections.singleton("name"));

        // WHEN
        QueryParams result = sut.buildQueryParams(queryParams);

        // THEN
        assertThat(result.getIncludedFields()
            .getParams()
            .get("users")).isNotNull();

        assertThat(result.getIncludedFields()
            .getParams()
            .get("users")
            .getParams()
            .iterator()
            .next()).isEqualTo("name");
    }

    @Test
    public void onGivenIncludedRelationsBuilderShouldReturnRequestParamsWithIncludedRelations() throws
        ParametersDeserializationException {
        // GIVEN
        queryParams.put("INCLUDE[special-users]", Collections.singleton("friends"));

        // WHEN
        QueryParams result = sut.buildQueryParams(queryParams);

        // THEN
        assertThat(result.getIncludedRelations()
            .getParams()
            .get("special-users")).isNotNull();

        assertThat(result.getIncludedRelations()
            .getParams()
            .get("special-users")
            .getParams()
            .iterator()
            .next()
            .getPath()).isEqualTo("friends");
    }
}
