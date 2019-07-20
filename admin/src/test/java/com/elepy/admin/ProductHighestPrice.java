package com.elepy.admin;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelContext;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.routes.ActionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

public class ProductHighestPrice implements ActionHandler<Product> {
    @Override
    public void handleAction(HttpContext context, Crud<Product> dao, ModelContext<Product> modelContext, ObjectMapper objectMapper) throws Exception {

        final List<Product> products = dao.getByIds(context.modelIds());

        final Product max = products
                .stream()
                .max(Comparator.comparing(Product::getPrice))
                .orElseThrow(() -> new ElepyException("No products found", 404));

        context.result(Message.htmlContent(String.format("<div class = 'uk-padding'><p>The highest priced product in this list is '%s', it costs %.2f.</p><p>This message has been brought to you by a custom Elepy action. Done in 7 lines of code! Elepy supports all types actions.</p></div>",

                max.getId(),
                max.getPrice().setScale(2, RoundingMode.HALF_UP))
                )
        );

    }
}
