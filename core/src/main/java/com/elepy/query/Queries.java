package com.elepy.query;

import com.elepy.data.query.parser.EleQueryLexer;
import com.elepy.data.query.parser.EleQueryParser;
import com.elepy.query.parser.QueryListener;
import com.elepy.query.parser.cql.CQLParser;
import com.elepy.utils.StringUtils;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Queries {

    public static Query parse(String input) {

        if (StringUtils.isEmpty(input)) {
            return Queries.empty();
        }
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

    public static Query empty() {
        return create(Filters.search(""));
    }

} 
