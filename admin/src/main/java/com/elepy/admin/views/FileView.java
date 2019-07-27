package com.elepy.admin.views;

import com.elepy.admin.concepts.ModelView;
import com.elepy.describers.Model;
import com.elepy.http.Request;
import com.elepy.uploads.FileReference;

public class FileView implements ModelView<FileReference> {
    @Override
    public String renderView(Request request, Model model) {
        return "";
    }
}
