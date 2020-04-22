
grammar EleQuery;

query
    :
        expression EOF
    ;
expression
    :   '(' expression ')'
    |   expression booleanOperator expression
    |   filter
    |   searchQuery

    ;

booleanOperator: AND | OR;
filter:  baseFilter | textFilter | numberFilter ;
propertyName: PROPERTY_NAME | TERM;
searchQuery:  (TERM|ANY_CHAR|NUMERIC)+ | STRING ;


textFilter: propertyName textFilterType textValue;
textFilterType: NOT_EQUALS | EQUALS | CONTAINS | STARTS_WITH;
textValue: STRING | TERM | NUMERIC;

numberFilter: propertyName numberFilterType numberValue;
numberFilterType: GREATER_THAN_OR_EQUALS | GREATER_THAN | LESSER_THAN_OR_EQUALS | LESSER_THAN ;
numberValue: NUMERIC;

baseFilter: propertyName baseFilterType baseValue;
baseFilterType: EQUALS | NOT_EQUALS;
baseValue: STRING | TERM | NUMERIC;


GREATER_THAN_OR_EQUALS: '>=' |  G T E;
GREATER_THAN: '>' | G T;

LESSER_THAN_OR_EQUALS: '<=' | L T E;
LESSER_THAN: '<' |  L T;

NOT_EQUALS: '!=' | '<>' | N E (Q)? ;
EQUALS: '=' | E Q;

CONTAINS: C O N T A I N S | I N;
STARTS_WITH: S T A R T S [ ]* W I T H | S W;


AND: A N D| '&&' ;

OR: O R | '||';

PROPERTY_NAME: (LETTER TERM ('.')+ TERM)+;
NUMERIC: FLOAT | INTEGER;
STRING: DQ_STRING | SQ_STRING {
                                   String s = getText();
                                   s = s.substring(1, s.length() - 1); // strip the leading and trailing quotes
                                   setText(s);
                                 };

DQ_STRING: '"'~('"')+?'"';
SQ_STRING: '\''~('\'')+?'\'';

TERM: (LETTER|DIGIT)+;

FLOAT: DIGIT+ ('.') DIGIT+;
INTEGER: DIGIT+;
DIGIT: [0-9];
LETTER: [a-zA-Z];

fragment A : [aA];
fragment B : [bB];
fragment C : [cC];
fragment D : [dD];
fragment E : [eE];
fragment F : [fF];
fragment G : [gG];
fragment H : [hH];
fragment I : [iI];
fragment J : [jJ];
fragment K : [kK];
fragment L : [lL];
fragment M : [mM];
fragment N : [nN];
fragment O : [oO];
fragment P : [pP];
fragment Q : [qQ];
fragment R : [rR];
fragment S : [sS];
fragment T : [tT];
fragment U : [uU];
fragment V : [vV];
fragment W : [wW];
fragment X : [xX];
fragment Y : [yY];
fragment Z : [zZ];

ANY_CHAR: ~[ \t\r\n]+;
//All whitespace is skipped

WS: [ \t\r\n]+ -> skip;


