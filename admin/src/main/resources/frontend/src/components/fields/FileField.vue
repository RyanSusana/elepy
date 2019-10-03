<template>
    <div>
        <progress
                :max="fileUploadSize"
                :value="totalUploaded"
                class="uk-progress"
                id="js-progressbar"
                v-if="totalUploaded !== fileUploadSize"
        ></progress>
        <div
                class="uk-placeholder uk-flex uk-flex-middle uk-flex-center uk-flex-column"
                v-if="!empty"
        >
            <div class="uk-flex uk-flex-middle uk-flex-center">
                <a @click="removeFile()" class="uk-text-danger" uk-icon="close" v-if="field.editable"></a>

                <a :href="value" target="_blank">{{value}}</a>
            </div>

            <ImageLightBox :height="500" :src="value" :width="500" v-if="isImageUrl"/>
        </div>

        <div class="js-upload uk-placeholder uk-text-center" v-show="empty">
            <span uk-icon="icon: cloud-upload"></span>
            <span class="uk-text-middle">&nbsp;Upload a file by dropping it here or</span>
            <div uk-form-custom>
                <input type="file">
                <span class="uk-link">&nbsp;selecting one from your computer</span>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
</style>

<script>
    import Utils from "../../utils.mjs";
    import UIkit from "uikit";
    import ImageLightBox from '../modals/ImageLightBox';

    const axios = require("axios");

    export default {
        components: {ImageLightBox},
        props: ["field", "value"],
        name: "FileField",
        component: {ImageLightBox},
        data() {
            return {
                lastUpload: null,
                totalUploaded: 0,
                fileUploadSize: 0
            };
        },
        computed: {
            isImageUrl: function () {
                if (this.value == null) {
                    return false;
                }
                return /(http(s?):)*\.(?:jpg|gif|png|svg|jpeg)/g.test(this.value);
            },
            empty: function () {
                return this.value == null || this.value.trim() === '';
            }
        },
        methods: {
            handleInput(e) {
                this.$emit("input", e || "");

            },
            removeFile() {
                let vm = this;
                UIkit.modal
                    .confirm(
                        "<p>Do you want to remove this file from Elepy? Doing so will remove it from all other locations.<br><br><strong>" +
                        vm.value +
                        "</strong></p>",
                        {
                            labels: {
                                ok: "Don't remove it from Elepy",
                                cancel: "Remove it from Elepy too"
                            },
                            stack: true
                        }
                    )
                    .then(
                        function () {
                            vm.handleInput(null);
                        },
                        function () {
                            axios({
                                method: "delete",
                                url: Utils.url + "/files/" + vm.value.split("/").slice(-1)
                            })
                                .then(response => {
                                    vm.handleInput(null);
                                    Utils.displayResponse(response);
                                })
                                .catch(function (error) {
                                    if (error.response.status === 404) {
                                        vm.handleInput(null);
                                    } else {
                                        Utils.displayError(error);
                                    }
                                });
                        }
                    );
            }
        },
        mounted() {
            let vm = this;
            UIkit.upload(".js-upload", {
                url:
                    Utils.url +
                    "/uploads" +
                    "?maximumFileSize=" +
                    vm.field.maximumFileSize +
                    "&allowedMimeType=" +
                    encodeURIComponent(vm.field.allowedMimeType),
                name: "files",
                multiple: false,

                msgInvalidMime: "Invalid file type, must be %s",
                mime: vm.field.allowedMimeType,


                error: function (response) {
                    if (response.xhr == null) {
                        UIkit.notification(response, {
                            status: "danger",
                            pos: "bottom-center"
                        });
                    } else {
                        UIkit.notification(JSON.parse(response.xhr.response).message, {
                            status: "danger",
                            pos: "bottom-center"
                        });
                    }
                },
                fail: function (response) {
                    UIkit.notification(response, {
                        status: "danger",
                        pos: "bottom-center"
                    });
                },

                progress: function (e) {
                    if (e.total > vm.field.maximumFileSize) {
                        let error =
                            "The file is too big large, maximum file size is " +
                            vm.field.maximumFileSize / 1024 +
                            "KB";

                        UIkit.notification(error, {
                            status: "danger",
                            pos: "bottom-center"
                        });
                    }
                    vm.fileUploadSize = e.total;
                    vm.totalUploaded = e.loaded;
                },
                completeAll: function () {
                    let file = JSON.parse(arguments[0].response).files[0];
                    //let url = arguments[0].responseURL.split("?")[0];

                    let fileLocation = "/uploads/" + file.uploadName;

                    vm.handleInput(fileLocation);
                }
            });
        }
    };
    String.prototype.replaceAll = function (search, replacement) {
        var target = this;
        return target.split(search).join(replacement);
    };
</script>
