
grammar Query;

query
    :
        expression EOF
    ;
expression
    :   '(' expression ')'
    |   expression AND expression
    |   expression OR expression
    |   searchQuery
    |   filter
    ;

filter: propertyName filterType filterValue;
filterType: GREATER_THAN_OR_EQUALS | GREATER_THAN | LESSER_THAN_OR_EQUALS | LESSER_THAN | NOT_EQUALS | EQUALS;
propertyName: PROPERTY_NAME | TERM;
searchQuery:  TERM | SENTENCE;



filterValue: (SENTENCE) | NUMERIC+;



GREATER_THAN_OR_EQUALS: '>=';
GREATER_THAN: '>';

LESSER_THAN_OR_EQUALS: '<=';
LESSER_THAN: '<';

NOT_EQUALS: '!=';
EQUALS: '=';

PROPERTY_NAME: (LETTER TERM ('.')+ TERM)+;


AND: A N D | '&&' ;

OR: ':' O R | '||';


NUMERIC: FLOAT | INTEGER;
SENTENCE: (WORD  ' '+ WORD (' ')* )+;

TERM: (LETTER|DIGIT)+;
WORD      :
         (LETTER)+
    ;

ALPHANUMERIC: (DIGIT | LETTER)+;

FLOAT: DIGIT+ ('.') DIGIT+;
INTEGER: DIGIT+;
DIGIT: [0-9];
LETTER: [a-zA-Z];
fragment A:('a'|'A');
fragment B:('b'|'B');
fragment C:('c'|'C');
fragment D:('d'|'D');
fragment E:('e'|'E');
fragment F:('f'|'F');
fragment G:('g'|'G');
fragment H:('h'|'H');
fragment I:('i'|'I');
fragment J:('j'|'J');
fragment K:('k'|'K');
fragment L:('l'|'L');
fragment M:('m'|'M');
fragment N:('n'|'N');
fragment O:('o'|'O');
fragment P:('p'|'P');
fragment Q:('q'|'Q');
fragment R:('r'|'R');
fragment S:('s'|'S');
fragment T:('t'|'T');
fragment U:('u'|'U');
fragment V:('v'|'V');
fragment W:('w'|'W');
fragment X:('x'|'X');
fragment Y:('y'|'Y');
fragment Z:('z'|'Z');

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;


