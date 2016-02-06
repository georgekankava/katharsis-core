package io.katharsis.queryParams;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultQueryParamsParserTest {

    private Map<String, Set<String>> queryParams;
    private QueryParamsParser parser = new DefaultQueryParamsParser();

    @Before
    public void prepare() {
        queryParams = new HashMap<>();
    }

    @Test
    public void onGivenFiltersParserShouldReturnOnlyRequestParamsWithFilters() {
        // GIVEN
        queryParams.put("FILTER[users][name]", Collections.singleton("John"));
        queryParams.put("random[users][name]", Collections.singleton("John"));

        // WHEN
        Map<String, Set<String>> result = parser.parseFiltersParameters(queryParams);

        // THEN
        assertThat(result.entrySet().size()).isEqualTo(1);
        assertThat(result.entrySet().iterator().next().getKey().startsWith("FILTER"));
        assertThat(result.entrySet().iterator().next().getValue().equals(Collections.singleton("John")));
    }

    @Test
    public void onGivenSortingParserShouldReturnOnlyRequestParamsWithSorting() {
        // GIVEN
        queryParams.put("SORT[users][name]", Collections.singleton("ASC"));
        queryParams.put("random[users][name]", Collections.singleton("DESC"));

        // WHEN
        Map<String, Set<String>> result = parser.parseSortingParameters(queryParams);

        // THEN
        assertThat(result.entrySet().size()).isEqualTo(1);
        assertThat(result.entrySet().iterator().next().getKey().startsWith("SORT"));
        assertThat(result.entrySet().iterator().next().getValue().equals(Collections.singleton("ASC")));
    }

    @Test
    public void onGivenGroupingParserShouldReturnOnlyRequestParamsWithGrouping() {
        // GIVEN
        queryParams.put("GROUP[users]", Collections.singleton("name"));
        queryParams.put("random[users]", Collections.singleton("surname"));

        // WHEN
        Map<String, Set<String>> result = parser.parseGroupingParameters(queryParams);

        // THEN
        assertThat(result.entrySet().size()).isEqualTo(1);
        assertThat(result.entrySet().iterator().next().getKey().startsWith("GROUP"));
        assertThat(result.entrySet().iterator().next().getValue().equals(Collections.singleton("name")));
    }

    @Test
    public void onGivenPaginationParserShouldReturnOnlyRequestParamsWithPagination() {
        // GIVEN
        queryParams.put("PAGE[OFFSET]", Collections.singleton("1"));
		queryParams.put("PAGE[LIMIT]", Collections.singleton("10"));
        queryParams.put("random[OFFSET]", Collections.singleton("2"));
        queryParams.put("random[LIMIT]", Collections.singleton("20"));

        // WHEN
        Map<String, Set<String>> result = parser.parsePaginationParameters(queryParams);

        // THEN
        assertThat(result.entrySet().size()).isEqualTo(2);
        assertThat(result.get("PAGE[OFFSET]").equals(Collections.singleton("1")));
        assertThat(result.get("PAGE[LIMIT]").equals(Collections.singleton("10")));
    }

    ////////
    @Test
    public void onGivenIncludedFieldsParserShouldReturnOnlyRequestParamsWithIncludedFields() {
        // GIVEN
        queryParams.put("FIELDS[users]", Collections.singleton("name"));
        queryParams.put("random[users]", Collections.singleton("surname"));

        // WHEN
        Map<String, Set<String>> result = parser.parseIncludedFieldsParameters(queryParams);

        // THEN
        assertThat(result.entrySet().size()).isEqualTo(1);
		assertThat(result.entrySet().iterator().next().getKey().startsWith("FIELDS"));
        assertThat(result.entrySet().iterator().next().getValue().equals(Collections.singleton("name")));
    }

    @Test
    public void onGivenIncludedRelationsParserShouldReturnOnlyRequestParamsWithIncludedRelations() {
        // GIVEN
        queryParams.put("INCLUDE[user]", Collections.singleton("name"));
        queryParams.put("random[user]", Collections.singleton("surname"));

        // WHEN
        Map<String, Set<String>> result = parser.parseIncludedRelationsParameters(queryParams);

        // THEN
        assertThat(result.entrySet().size()).isEqualTo(1);
        assertThat(result.entrySet().iterator().next().getKey().startsWith("INCLUDE"));
        assertThat(result.entrySet().iterator().next().getValue().equals(Collections.singleton("name")));
    }

}
