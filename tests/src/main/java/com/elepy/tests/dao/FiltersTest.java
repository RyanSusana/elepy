package com.elepy.tests.dao;

import com.elepy.dao.FilterType;
import com.elepy.exceptions.ElepyException;
import com.elepy.tests.ElepyConfigHelper;
import com.elepy.tests.ElepySystemUnderTest;
import com.elepy.tests.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.elepy.dao.FilterType.*;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class FiltersTest implements ElepyConfigHelper {
    private ElepySystemUnderTest elepy;

    @BeforeEach
    void before() {
        elepy = ElepySystemUnderTest.create();

        this.configureElepy(elepy);
        elepy.addModel(Product.class);

        elepy.start();

        Unirest.setHttpClient(HttpClients.custom().disableCookieManagement().build());
    }

    @Test
    void canSearch() {

        seedWithProducts(
                Product.withDescription("Ryan's ball"),
                Product.withDescription("Pablo's ball"));

        assertThat(executeQuery(Map.of("q", "ryan")))
                .hasSize(1);
    }

    @Test
    @Disabled("Should be tested via Crud Filter")
    void canSearch_withSingleQuote() {

        seedWithProducts(
                Product.withDescription("Ryan's phone"),
                Product.withDescription("Pablo's phone"));

        assertThat(executeQuery(Map.of("q", "\"ryan\"")))
                .hasSize(1);
    }

    @Test
    public void canFilter_EQUALS_onString() {
        var product = new Product();
        product.setShortDescription("Ryan");

        seedWithProducts(product);


        assertThat(executeFilter("shortDescription", EQUALS, "Ryan"))
                .hasSize(1);
        assertThat(executeFilter("shortDescription", EQUALS, "NotRyan"))
                .hasSize(0);
    }

    @Test
    public void canFilter_EQUALS_onNumber() {
        var product = new Product();
        product.setPrice(BigDecimal.TEN);

        seedWithProducts(product);


        assertThat(executeFilter("price", EQUALS, 10))
                .hasSize(1);
        assertThat(executeFilter("price", EQUALS, 20))
                .hasSize(0);
    }

    @Test
    public void canFilter_NOT_EQUALS_onString() {
        var product = new Product();
        product.setShortDescription("Ryan");

        seedWithProducts(product);


        assertThat(executeFilter("shortDescription", NOT_EQUALS, "Ryan"))
                .hasSize(0);
        assertThat(executeFilter("shortDescription", NOT_EQUALS, "NotRyan"))
                .hasSize(1);
    }

    @Test
    public void canFilter_NOT_EQUALS_onNumber() {
        var product = new Product();
        product.setPrice(BigDecimal.TEN);

        seedWithProducts(product);

        assertThat(executeFilter("price", NOT_EQUALS, 20))
                .hasSize(1);
        assertThat(executeFilter("price", NOT_EQUALS, 10))
                .hasSize(0);
    }

    @Test
    public void canFilter_IS_NULL_onString() {
        var product = new Product();
        product.setShortDescription(null);

        seedWithProducts(product);


        assertThat(executeFilter("shortDescription", IS_NULL, "true"))
                .hasSize(1);
    }

    @Test
    public void canFilter_NOT_NULL_onString() {
        var product = new Product();
        product.setShortDescription("not null");

        seedWithProducts(product);


        assertThat(executeFilter("shortDescription", NOT_NULL, "true"))
                .hasSize(1);
    }

    @Test
    public void canFilter_STARTS_WITH_onString() {
        var product = new Product();
        product.setShortDescription("Ryan");

        seedWithProducts(product);


        assertThat(executeFilter("shortDescription", STARTS_WITH, "Rya"))
                .hasSize(1);
        assertThat(executeFilter("shortDescription", STARTS_WITH, "NotRya"))
                .hasSize(0);
    }

    @Test
    public void canFilter_CONTAINS_onString() {
        var product = new Product();
        product.setShortDescription("Ryan");

        seedWithProducts(product);


        assertThat(executeFilter("shortDescription", CONTAINS, "ya"))
                .hasSize(1);
        assertThat(executeFilter("shortDescription", CONTAINS, "Notya"))
                .hasSize(0);
    }

    @Test
    public void canFilter_CONTAINS_onArray() {
        var product = new Product();
        product.setTags(List.of("Ryan", "Made", "This"));

        seedWithProducts(product);


        assertThat(executeFilter("tags", CONTAINS, "Ryan"))
                .hasSize(1);
        assertThat(executeFilter("tags", CONTAINS, "Made"))
                .hasSize(1);
        assertThat(executeFilter("tags", CONTAINS, "This"))
                .hasSize(1);
        assertThat(
                executeFilters(
                        filter("tags", CONTAINS, "Ryan"),
                        filter("tags", CONTAINS, "Made"),
                        filter("tags", CONTAINS, "This")
                )
        ).hasSize(1);
        assertThat(executeFilter("tags", CONTAINS, "NotInArray"))
                .hasSize(0);
    }

    @Test
    void canFilter_BETWEEN_Date_Exclusive() {
        var product = new Product();

        product.setDate(date(2019, 12, 13));
        seedWithProducts(product);


        assertThat(executeFilter("date", GREATER_THAN, date(2019, 12, 12)))
                .hasSize(1);
        assertThat(executeFilter("date", LESSER_THAN, date(2019, 12, 14)))
                .hasSize(1);

        assertThat(executeFilters(
                filter("date", GREATER_THAN, date(2019, 12, 12)),
                filter("date", LESSER_THAN, date(2019, 12, 14))
                )
        ).hasSize(1);

        assertThat(executeFilters(
                filter("date", GREATER_THAN, date(2019, 12, 13)),
                filter("date", LESSER_THAN, date(2019, 12, 13))
                )
        ).hasSize(0);
    }


    @Test
    void canFilter_BETWEEN_Date_Inclusive() {
        var product = new Product();

        product.setDate(date(2019, 12, 13));
        seedWithProducts(product);


        assertThat(executeFilter("date", GREATER_THAN_OR_EQUALS, date(2019, 12, 13)))
                .hasSize(1);
        assertThat(executeFilter("date", LESSER_THAN_OR_EQUALS, date(2019, 12, 13)))
                .hasSize(1);

        assertThat(executeFilters(
                filter("date", GREATER_THAN_OR_EQUALS, date(2019, 12, 13)),
                filter("date", LESSER_THAN_OR_EQUALS, date(2019, 12, 13))
                )
        ).hasSize(1);

        assertThat(executeFilters(
                filter("date", GREATER_THAN, date(2019, 12, 15)),
                filter("date", LESSER_THAN, date(2019, 12, 11))
                )
        ).hasSize(0);
    }

    private Date date(int year, int month, int day) {
        return Date.from(LocalDate.of(year, month, day).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }


    @Test
    void canFilter_GREATER_THAN_onNumber() {

        var product = new Product();
        product.setPrice(BigDecimal.TEN);

        seedWithProducts(product);


        assertThat(executeFilter("price", GREATER_THAN, 9))
                .hasSize(1);
        assertThat(executeFilter("price", GREATER_THAN, 10))
                .hasSize(0);
    }

    @Test
    void canFilter_GREATER_THAN_OR_EQUALS_onNumber() {

        var product = new Product();
        product.setPrice(BigDecimal.TEN);

        seedWithProducts(product);


        assertThat(executeFilter("price", GREATER_THAN_OR_EQUALS, 9))
                .hasSize(1);
        assertThat(executeFilter("price", GREATER_THAN_OR_EQUALS, 10))
                .hasSize(1);
        assertThat(executeFilter("price", GREATER_THAN_OR_EQUALS, 11))
                .hasSize(0);
    }


    @Test
    void canFilter_LESSER_THAN_onNumber() {

        var product = new Product();
        product.setPrice(BigDecimal.TEN);

        seedWithProducts(product);


        assertThat(executeFilter("price", LESSER_THAN, 11))
                .hasSize(1);
        assertThat(executeFilter("price", LESSER_THAN, 10))
                .hasSize(0);
    }

    @Test
    void canFilter_LESSER_THAN_OR_EQUALS_onNumber() {

        var product = new Product();
        product.setPrice(BigDecimal.TEN);

        seedWithProducts(product);


        assertThat(executeFilter("price", LESSER_THAN_OR_EQUALS, 11))
                .hasSize(1);
        assertThat(executeFilter("price", LESSER_THAN_OR_EQUALS, 10))
                .hasSize(1);
        assertThat(executeFilter("price", LESSER_THAN_OR_EQUALS, 9))
                .hasSize(0);
    }

    protected FilterOption filter(String fieldName, FilterType filterType, Object value) {
        return new FilterOption(fieldName, filterType, value);
    }

    protected List<Product> executeFilter(String fieldName, FilterType filterType, Object value) {
        return executeFilters(filter(fieldName, filterType, value));
    }

    protected List<Product> executeFilters(FilterOption... options) {
        return executeFilters(List.of(options));
    }


    protected List<Product> executeFilters(Iterable<FilterOption> options) {
        final var request = Unirest.get(elepy.url() + "/products");
        options.forEach(option -> request.queryString(String.format("%s_%s", option.fieldName, option.filterType.getName()), option.value));
        return executeRequest(request);

    }

    protected List<Product> executeQuery(Map<String, Object> map) {
        return executeRequest(Unirest.get(elepy.url() + "/products").queryString(map));
    }

    protected List<Product> executeRequest(HttpRequest request) {
        try {
            var response = request.asJson();
            if (response.getStatus() >= 400) {
                throw new ElepyException(response.getBody().getObject().getString("message"), response.getStatus());
            }
            final var mapper = new ObjectMapper();
            JavaType type = mapper.getTypeFactory().constructParametricType(List.class, Product.class);
            return ((List<Product>) mapper.readValue(response.getBody().toString(), type));
        } catch (JsonProcessingException | UnirestException e) {
            throw new RuntimeException(e);
        }
    }


    protected void seedWithProducts(Product... products) {
        final var random = new Random();
        for (Product product : products) {
            product.setId(random.nextInt(1_000_000));
        }
        elepy.getCrudFor(Product.class).create(products);
    }

    private static class FilterOption {
        private final String fieldName;
        private final FilterType filterType;
        private final Object value;

        FilterOption(String fieldName, FilterType filterType, Object value) {
            this.fieldName = fieldName;
            this.filterType = filterType;

            if (value instanceof Date)
                this.value = new SimpleDateFormat("yyyy-MM-dd").format(value);
            else
                this.value = value;
        }
    }
} 
