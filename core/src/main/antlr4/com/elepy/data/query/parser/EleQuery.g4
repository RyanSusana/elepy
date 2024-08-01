
grammar EleQuery;

query
    :
        expression (limitSetting | pageNumberSetting | skipSetting)* EOF
    ;


limitSetting: LIMIT EQUALS? NUMERIC;

skipSetting: SKIP_ROW EQUALS? NUMERIC;

pageNumberSetting: PAGE_NUMBER EQUALS? NUMERIC;
expression
    :   '(' expression ')'
    |   expression booleanOperator expression
    |   filter
    |   searchQuery

    ;

booleanOperator: AND | OR;
filter:  baseFilter | textFilter | numberFilter ;
propertyName: PROPERTY_NAME | ALPHA_NUMERIC_TERM;
searchQuery:  validSearchTerm+ ;
validSearchTerm: STRING |  (NON_ALPHA_NUMERIC_CHAR|LETTER|DIGIT)+ | ALPHA_NUMERIC_TERM | NUMERIC  ;


textFilter: propertyName textFilterType textValue;
textFilterType: NOT_EQUALS | EQUALS | CONTAINS | STARTS_WITH;
textValue: validSearchTerm+;

numberFilter: propertyName numberFilterType numberValue;
numberFilterType: GREATER_THAN_OR_EQUALS | GREATER_THAN | LESSER_THAN_OR_EQUALS | LESSER_THAN ;
numberValue: NUMERIC;

baseFilter: propertyName baseFilterType baseValue;
baseFilterType: EQUALS | NOT_EQUALS;
baseValue: validSearchTerm+ ;


SKIP_ROW: S K I P;
LIMIT: L I M I T | P A G E [ ]* S I Z E;

PAGE_NUMBER: P A G E [ ]* N U M B E R;
GREATER_THAN_OR_EQUALS: '>=' |  G T E;
GREATER_THAN: '>' | G T | G R E A T E R T H A N | M O R E T H A N;

LESSER_THAN_OR_EQUALS: '<=' | L T E | (L E S S | S M A L L E R) (E R)? T H A N;
LESSER_THAN: '<' |  L T;

NOT_EQUALS: '!=' | '<>' | N E (Q)? | N O T [ ]* E Q U A L (S)? ([ ]* T O)? ;
EQUALS: ':' | '=' | '==' | E Q (U A L S?)?;

CONTAINS: C O N T A I N S | I N;
STARTS_WITH: S T A R T S [ ]* W I T H | S W;


AND: A N D| '&&' ;

OR: O R | '||';

PROPERTY_NAME: (LETTER ALPHA_NUMERIC_TERM ('.')+ ALPHA_NUMERIC_TERM)+;
NUMERIC: FLOAT | INTEGER;
STRING: DQ_STRING | SQ_STRING {
                                   String s = getText();
                                   s = s.substring(1, s.length() - 1); // strip the leading and trailing quotes
                                   setText(s);
                                 };

DQ_STRING: '"'~('"')+?'"';
SQ_STRING: '\''~('\'')+?'\'';

ALPHA_NUMERIC_TERM: (LETTER|DIGIT)+;

FLOAT: DIGIT* ('.') DIGIT+;
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


//All whitespace is skipped

WS:   [ \t\r\n]+ -> skip;
NON_ALPHA_NUMERIC_CHAR: (.)+? -> skip;



