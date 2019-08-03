package com.elepy.admin.views;

import com.elepy.http.Request;
import com.elepy.models.Model;
import com.elepy.models.ModelView;
import com.mitchellbosecke.pebble.PebbleEngine;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

abstract class OfficialView implements ModelView<Object> {

    private final String vueTemplate;
    private PebbleEngine engine = new PebbleEngine.Builder().build();

    OfficialView(String vueTemplate) {
        this.vueTemplate = vueTemplate;
    }

    @Override
    public String renderView(Request request, Model elepyModel) {

        Map<String, Object> model = new HashMap<>();
        model.put("model", elepyModel);
        model.put("theView", vueTemplate);
        Writer writer = new StringWriter();
        try {
            engine.getTemplate("admin-templates/model-views/official-model-view.peb").evaluate(writer, model);
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
