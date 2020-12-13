<template>
    <VueEditor
            :value="value"
            @input="handleInput"
            useCustomImageHandler
            v-on:image-added="handleImageAdded"
            v-on:image-removed="handleImageRemoved"
    ></VueEditor>
</template>

<style lang="scss">
    .ql-editor {
        background-color: white;
    }
</style>

<script>
    import {VueEditor} from "vue2-editor";
    import Utils from "../../utils.mjs";
    import axios from "axios";

    export default {
        components: {VueEditor},

        props: ["value", "field"],
        data() {
            return {
                content: this.value
            };
        },
        methods: {
            handleInput(e) {
                this.content = e;
                this.$emit("input", e);
            },
            handleImageAdded(file, Editor, cursorLocation, resetUploader) {
                let formData = new FormData();
                formData.append("files", file);

                axios({
                    url: Utils.url + "/elepy/uploads",
                    method: "POST",
                    data: formData
                })
                    .then(result => {
                        let file = result.data.files[0];
                        Editor.insertEmbed(cursorLocation, "image", file.fullPath);
                        resetUploader();
                    });
            },

            handleImageRemoved(image) {
                axios({
                    url: Utils.url + image,
                    method: "DELETE",
                }).then(() => {
                })
                    .catch(error => {

                    });
            }
        }
    };
</script>
