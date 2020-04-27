package com.elepy.revisions;

import com.elepy.dao.Crud;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Calendar;

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
        revision.setUserId(userId);
        revision.setRevisionType(revisionType);
        revision.setRevisionName(revisionName);
        revision.setRecordSnapshot(objectMapper.writeValueAsString(snapshot));

        revision.setRevisionNumber(0);
        revision.setTimestamp(Calendar.getInstance().getTime());


    }
} 
