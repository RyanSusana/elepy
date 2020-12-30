<template>
    <div class>
        <div
                @click="showCode = true"
                class="compiled-markdown uk-background-muted"
                v-html="!content ? $t('elepy.ui.placeholders.markdown') :compileMarkdown(value)"
                v-show="!showCode"
        ></div>
        <pre class="language-md" v-show="showCode"><code
                @blur="handleInput"
                autocapitalize="off"
                autocorrect="off"
                class="language-md"
                contenteditable="true"
                id="code-editor"
                spellcheck="false"
                v-html="value"
        ></code></pre>
    </div>
</template>


<script>
    import marked from "marked";

    import Prism from "../../prism.js";

    import Misbehave from "misbehave";

    export default {
        props: ["field", "value"],
        data() {
            return {
                content: "" + this.value == null ? "" : this.value,
                contentHtml: "",
                showCode: false
            };
        },

        computed: {
            contentToShow() {
                return this.contentHtml;
            }
        },
        methods: {
            handleInput(e) {
                this.content = e.target.innerText;
                this.contentHtml = e.target.innerHTML;

                this.showCode = false;
                this.$emit("input", this.content);
            },
            compileMarkdown(item) {
                if (item == null) {
                    return "";
                }
                return marked(item, {sanitize: false});
            }
        },
        mounted() {
            var code = document.querySelector("#code-editor");

            new Misbehave(code, {
                oninput: () => {
                    Prism.highlightElement(code);
                }
            });
            Prism.highlightElement(code);
        }
    };
</script>
<style>
    @import url("https://fonts.googleapis.com/css?family=Roboto+Mono:400,400i,700,700i");

    .compiled-markdown {
        padding: 1em;
        margin: 0.5em 0;
        min-height: 1em;
        border: dashed #888 1px;
        box-sizing: border-box;
    }

    [contenteditable]:focus {
        outline: 0px solid transparent;
    }

    code[class*="language-"] {
        min-height: 1.5em;
        display: block;
    }

    /* PrismJS 1.16.0
    https://prismjs.com/download.html#themes=prism&languages=markup+css+markdown */
    /**
     * prism.js default theme for JavaScript, CSS and HTML
     * Based on dabblet (http://dabblet.com)
     * @author Lea Verou
     */

    code[class*="language-"],
    pre[class*="language-"] {
        color: black;
        background: none;
        text-shadow: 0 1px white;
        font-family: "Roboto Mono", Consolas, Monaco, "Andale Mono", "Ubuntu Mono",
        monospace;
        font-size: 1em;
        text-align: left;
        white-space: pre;
        word-spacing: normal;
        word-break: normal;
        word-wrap: normal;
        line-height: 1.5;

        -moz-tab-size: 4;
        -o-tab-size: 4;
        tab-size: 4;

        -webkit-hyphens: none;
        -moz-hyphens: none;
        -ms-hyphens: none;
        hyphens: none;
    }

    pre[class*="language-"]::-moz-selection,
    pre[class*="language-"] ::-moz-selection,
    code[class*="language-"]::-moz-selection,
    code[class*="language-"] ::-moz-selection {
        text-shadow: none;
        background: #b3d4fc;
    }

    pre[class*="language-"]::selection,
    pre[class*="language-"] ::selection,
    code[class*="language-"]::selection,
    code[class*="language-"] ::selection {
        text-shadow: none;
        background: #b3d4fc;
    }

    @media print {
        code[class*="language-"],
        pre[class*="language-"] {
            text-shadow: none;
        }
    }

    /* Code blocks */
    pre[class*="language-"] {
        padding: 1em;
        margin: 0.5em 0;
        overflow: auto;
        border-radius: 0;
    }

    :not(pre) > code[class*="language-"],
    pre[class*="language-"] {
        background: #fff;
    }

    /* Inline code */
    :not(pre) > code[class*="language-"] {
        padding: 0.1em;
        border-radius: 0;
        white-space: normal;
    }

    .token.comment,
    .token.prolog,
    .token.doctype,
    .token.cdata {
        color: slategray;
    }

    .token.punctuation {
        color: #999;
    }

    .namespace {
        opacity: 0.7;
    }

    .token.property,
    .token.tag,
    .token.boolean,
    .token.number,
    .token.constant,
    .token.symbol,
    .token.deleted {
        color: #905;
    }

    .token.selector,
    .token.attr-name,
    .token.string,
    .token.char,
    .token.builtin,
    .token.inserted {
        color: #690;
    }

    .token.operator,
    .token.entity,
    .token.url,
    .language-css .token.string,
    .style .token.string {
        color: #39f;
        background: hsla(0, 0%, 100%, 0.5);
    }

    .token.atrule,
    .token.attr-value,
    .token.keyword {
        color: #07a;
    }

    .token.function,
    .token.class-name {
        color: #dd4a68;
    }

    .token.regex,
    .token.important,
    .token.variable {
        color: #39f;
    }

    .token.important {
        font-weight: bold;
        font-size: 1.2em;
    }

    .token.bold {
        font-weight: bold;
    }

    .token.italic {
        font-style: italic;
    }

    .token.entity {
        cursor: help;
    }
</style>