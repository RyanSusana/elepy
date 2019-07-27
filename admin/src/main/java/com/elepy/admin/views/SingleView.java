package com.elepy.admin.views;

import com.elepy.admin.concepts.ModelView;
import com.elepy.describers.Model;
import com.elepy.http.Request;
import com.mitchellbosecke.pebble.PebbleEngine;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class SingleView implements ModelView {

    private PebbleEngine engine = new PebbleEngine.Builder().build();

    @Override
    public String renderView(Request request, Model elepyModel) {

        Map<String, Object> model = new HashMap<>();
        model.put("model", elepyModel);
        Writer writer = new StringWriter();
        try {
            engine.getTemplate("admin-templates/model-views/single-model-view.peb").evaluate(writer, model);
        } catch (IOException e) {
            try {
                writer.write(e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return writer.toString();

    }
}
