<template>
    <div contenteditable="true" :id="this._uid"></div>
</template>

<script>
    import EditorJS from '@editorjs/editorjs';


    const editor = new EditorJS();

    export default {
        name: "EditorJsField",
        props: ["field", "value"],

        watch: {},
        data() {
            return {
                editor: null,
            }
        },

        computed: {
            dataAsJson() {
                return JSON.parse(this.value ?? "{}")
            }
        },

        methods: {
            async onChange() {
                let data = await this.editor.saver.save();
                this.$emit("input", JSON.stringify(data));
            }
        },
        mounted() {
            this.editor = new EditorJS({
                    placeholder: 'Type here...',
                    holder: '' + this._uid,
                    data: this.dataAsJson,
                    onChange: this.onChange
                }
            );
            this.editor.isReady.then(_ => {
                this.editor.render(this.dataAsJson)
            })
        }

    }
</script>

<style>

    .ce-block > * {
        margin-left: 20px !important;
    }
</style>