package com.elepy.revisions;

import com.elepy.dao.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Calendar;
import java.util.List;

public class Revisions {

    private Crud<Revision> revisionCrud;
    private ObjectMapper objectMapper;

    public void createRevision(String revisionName,
                               RevisionType revisionType,
                               String userId,
                               String schemaPath,
                               Object snapshot) throws JsonProcessingException {

        final var revision = new Revision();

        revision.setSchemaPath(schemaPath);
        revision.setRecordSnapshot(objectMapper.writeValueAsString(snapshot));

        revision.setRevisionNumber(0);
        revision.setTimestamp(Calendar.getInstance().getTime());


//        final var paginationSettings = new PageSettings(1, Integer.MAX_VALUE, List.of(new SortingSpecification("revisionNumber", SortOption.DESCENDING)));
//        Query qry  =  new Query("",  List.of());
//        revisionCrud.search(qry, paginationSettings);


    }
} 
