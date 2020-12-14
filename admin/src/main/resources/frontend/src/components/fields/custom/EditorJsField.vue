<template>
    <div class="editor">
        <div :id="'editor-'+this._uid" class="elepy-editor-js"></div>
    </div>
</template>

<script>
    import EditorJS from '@editorjs/editorjs';
    import Header from '@editorjs/header'
    import Embed from '@editorjs/embed'
    import ImageTool from '@editorjs/image';
    import axios from "axios";
    import Utils from "../../../utils";


    export default {
        name: "EditorJsField",
        props: {
            field: {
                type: Object,
                required: true
            },
            value: {
                type: String,
            }
        },
        data() {
            return {
                editor: null,
                initiated: false
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
            },
            uploadFile(file) {
                let formData = new FormData();
                formData.append("files", file);

                return axios({
                    url: Utils.url + "/elepy/uploads",
                    method: "POST",
                    data: formData
                })
                    .then(result => {
                        let file = result.data.files[0];
                        console.log("Success: " + file.fullPath)
                        return {
                            success: 1,
                            file: {
                                url: file.fullPath,
                            }
                        }
                    });
            },
            uploadUrl(url) {
                return {
                    success: 1,
                    file: {
                        url
                    }
                }
            },
        },
        mounted() {
            console.log(this.field);


            this.editor = new EditorJS({
                    minHeight: 50,
                    placeholder: 'Type here...',
                    logLevel: 'WARN',
                    holder: 'editor-' + this._uid,
                    tools: {

                        header: {
                            class: Header,
                            shortcut: 'CMD+SHIFT+H',
                            placeholder: 'Enter a header',
                            levels: [1, 2, 3, 4, 5, 6],
                            defaultLevel: 3
                        },
                        image: {
                            class: ImageTool,
                            config: {
                                uploader: {
                                    uploadByFile: this.uploadFile,
                                    uploadByUrl: this.uploadUrl,
                                }
                            }
                        },
                        embed: {
                            class: Embed,
                            config: this.field.props.embed
                        },

                    },
                    onChange: this.onChange
                }
            );
            this.editor.isReady.then(_ => {
                if (this.value){
                    this.editor.render(this.dataAsJson)
                }
            });
        }

    }
</script>

<style lang="scss">
    .editor {
        font-family: -apple-system, BlinkMacSystemFont, Segoe UI, Roboto, Oxygen, Ubuntu, Cantarell, Fira Sans, Droid Sans, Helvetica Neue, sans-serif;

        background: #f8f8f8;

        padding-top: 50px;

        margin: 0 auto;
        font-size: 16px;
    }


</style>