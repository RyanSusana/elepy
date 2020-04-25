
const string = /("|')(?:\\(?:\r\n|[\s\S])|(?!\1)[^\\\r\n])*\1/;
Prism.languages.cql = {

    'string': {
        pattern: string,
        greedy: true
    },

    'or': /\bor\b/i,

};

export default Prism