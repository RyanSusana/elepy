<template>
    <div :id="'editor-'+this._uid"></div>
</template>

<script>
    import EditorJS from '@editorjs/editorjs';
    import Header from '@editorjs/header'
    import SimpleImage from '@editorjs/simple-image'
    import Embed from '@editorjs/embed'



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
                    logLevel: 'WARN',
                    holder: 'editor-' + this._uid,
                    tools: {
                        image: SimpleImage,
                        embed: Embed,
                        header: {
                            class: Header,
                            shortcut: 'CMD+SHIFT+H',
                            placeholder: 'Enter a header',
                            levels: [1, 2, 3, 4, 5, 6],
                            defaultLevel: 3
                        },
                    },
                    onChange: this.onChange
                }
            );
            this.editor.isReady.then(_ => {
                if (this.value)
                    this.editor.render(this.dataAsJson)
            });

        }

    }
</script>

<style>
</style>