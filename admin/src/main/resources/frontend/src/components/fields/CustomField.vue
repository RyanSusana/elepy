<template>

    <component :is="loadedComponent" v-bind="field.props" @input="handleInput" :value="value"
               :field="field"
               v-if="value !==null"></component>

</template>

<script>

    import Vue from "vue"
    import EditorJsField from "./custom/EditorJsField";

    export default {
        name: "CustomField",
        props: ["value", "field"],
        components: {EditorJsField},
        computed: {
            loadedComponent() {
                if(process.env.NODE_ENV !== 'production'){
                    return EditorJsField;
                }
                return Vue.component('loadedComponent-' + this.field.name, () => externalComponent(this.field.scriptLocation));
            }
        },
        methods: {
            handleInput(e) {
                this.$emit("input", e);
            }
        }
    }

    async function externalComponent(url, componentName) {

        const name = componentName || url.split(`/`).reverse()[0].match(/^(.*?)\.umd/)[1];


        if (window[name]) return window[name];

        window[name] = new Promise((resolve, reject) => {
            const script = document.createElement(`script`);
            script.async = true;
            script.addEventListener(`load`, () => {
                resolve(window[name]);
            });
            script.addEventListener(`error`, () => {
                reject(new Error(`Error loading ${url}`));
            });
            script.src = url;
            document.head.appendChild(script);
        });

        return window[name];
    }


</script>

<style scoped>

</style>