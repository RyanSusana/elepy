package com.elepy.tests.dao;

import com.elepy.crud.Crud;
import com.elepy.query.Expression;
import com.elepy.query.FilterType;
import com.elepy.exceptions.ElepyException;
import com.elepy.query.Filters;
import com.elepy.tests.ElepyConfigHelper;
import com.elepy.tests.ElepySystemUnderTest;
import com.elepy.tests.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.elepy.query.FilterType.*;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class FiltersTest implements ElepyConfigHelper {
    protected ElepySystemUnderTest elepy;
    protected Crud<Product> productCrud;

    protected HttpClient httpClient;
    protected ObjectMapper objectMapper;
    @AfterEach
    void tearDown() {
        elepy.stop();
    }

    @BeforeEach
    void before() {
        elepy = ElepySystemUnderTest.create();

        this.configureElepy(elepy);
        elepy.addModel(Product.class);

        elepy.start();

        productCrud = elepy.getCrudFor(Product.class);
        httpClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void canCount_WithNoExpression() {
        seedWithProducts(
                Product.withDescription("Ryan's ball"),
                Product.withDescription("Pablo's ball"));
        assertThat(productCrud.count()).isEqualTo(2);
    }

    @Test
    public void canCount_WithEqualsExpression() {
        seedWithProducts(
                Product.withDescription("test1"),
                Product.withDescription("test2"),
                Product.withDescription("test2"),
                Product.withDescription("test3"),
                Product.withDescription("test3"),
                Product.withDescription("test3"));

        var count1 = productCrud.count(Filters.eq("shortDescription", "test1"));
        var count2 = productCrud.count(Filters.eq("shortDescription", "test2"));
        var count3 = productCrud.count(Filters.eq("shortDescription", "test3"));

        assertThat(count1).isEqualTo(1);
        assertThat(count2).isEqualTo(2);
        assertThat(count3).isEqualTo(3);
    }

    @Test
    public void canSearch() {

        seedWithProducts(
                Product.withDescription("Ryan's ball"),
                Product.withDescription("Pablo's ball"));

        assertThat(executeQuery(Map.of("q", "ryan")))
                .hasSize(1);
    }

    @Test
    @Disabled("Should be tested via Crud Filter")
    public void canSearch_withSingleQuote() {

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
    public void canFilter_BETWEEN_Date_Exclusive() {
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
    public void canFilter_BETWEEN_Date_Inclusive() {
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
    public void canFilter_GREATER_THAN_onNumber() {

        var product = new Product();
        product.setPrice(BigDecimal.TEN);
        product.setShortDescription("Ryan");

        seedWithProducts(product);

        var products = productCrud.find(Filters.eq("shortDescription", "Ryan"));


        assertThat(executeFilter("price", GREATER_THAN, 9))
                .hasSize(1);
        assertThat(executeFilter("price", GREATER_THAN, 10))
                .hasSize(0);
    }

    @Test
    public void canFilter_GREATER_THAN_OR_EQUALS_onNumber() {

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
    public void canFilter_LESSER_THAN_onNumber() {

        var product = new Product();
        product.setPrice(BigDecimal.TEN);

        seedWithProducts(product);


        assertThat(executeFilter("price", LESSER_THAN, 11))
                .hasSize(1);
        assertThat(executeFilter("price", LESSER_THAN, 10))
                .hasSize(0);
    }

    @Test
    public void canFilter_LESSER_THAN_OR_EQUALS_onNumber() {

        var product = new Product();
        product.setPrice(BigDecimal.TEN);

        seedWithProducts(product);


        assertThat(executeFilter("price", LESSER_THAN_OR_EQUALS, 11))
                .hasSize(1);
        assertThat(executeFilter("price", LESSER_THAN_OR_EQUALS, 10))
                .hasSize(1);
        assertThat(executeFilter("price", LESSER_THAN_OR_EQUALS, 9L))
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
        String queryString = "";
        if (options != null) {
            queryString = StreamSupport.stream(options.spliterator(), false)
                    .map(option -> String.format("%s_%s=%s",
                            URLEncoder.encode(option.fieldName, StandardCharsets.UTF_8),
                            URLEncoder.encode(option.filterType.getName(), StandardCharsets.UTF_8),
                            URLEncoder.encode(option.value.toString(), StandardCharsets.UTF_8)))
                    .collect(Collectors.joining("&"));
        }

        URI uri = URI.create(elepy.url() + "/products" + (queryString.isEmpty() ? "" : "?" + queryString));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        return executeRequest(request);
    }

    protected List<Product> executeQuery(Map<String, Object> map) {
        String queryString = map.entrySet().stream()
                .map(entry -> String.format("%s=%s",
                        URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8),
                        URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8)))
                .collect(Collectors.joining("&"));

        URI uri = URI.create(elepy.url() + "/products" + (queryString.isEmpty() ? "" : "?" + queryString));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        return executeRequest(request);
    }

    protected List<Product> executeRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                // Assuming the error message is in a JSON object with a "message" field
                String responseBody = response.body();
                // You might need more robust JSON parsing for error messages
                String errorMessage = "Unknown error";
                try {
                    errorMessage = objectMapper.readTree(responseBody).get("message").asText();
                } catch (Exception e) {
                    // Fallback if message field is not found or body is not JSON
                }
                throw new ElepyException(errorMessage, response.statusCode());
            }

            JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, Product.class);
            return objectMapper.readValue(response.body(), type);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
            throw new RuntimeException("Failed to execute HTTP request", e);
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
