package com.elepy.dao;

import com.elepy.dao.parser.EleQueryLexer;
import com.elepy.dao.parser.EleQueryParser;
import com.elepy.dao.parser.QueryListener;
import com.elepy.dao.parser.cql.CQLParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.Serializable;
import java.util.List;

public class Queries {

    public static Query parse(String input) {

        //Lex (with Antlr's generated lexer)
        CharStream inputStream = CharStreams.fromString(input);
        EleQueryLexer lexer = new EleQueryLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        //Parse (with Antlr's generated parser)
        EleQueryParser parser = new EleQueryParser(tokens);
        ParseTree parseTree = parser.query();

        //Extract AST from the Antlr parse tree
        QueryListener listener = new QueryListener();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, parseTree);

        return listener.getQuery();
    }

    public static Query parseCQL(String input) {
        return CQLParser.parse(input);
    }

    public static Query create(Expression expression) {
        return new Query(expression);
    }


} 
