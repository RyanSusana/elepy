<template>


    <BaseLayout :back-location="singleMode? null : goBack">
        <!-- Navigation -->
        <template #navigation>
            <a action="save"
               @click="save()"
               @shortkey="save()"
               class="uk-button uk-button-primary uk-margin-small-right"
               v-shortkey.once="['ctrl',  'save']"

            ><i uk-icon="icon: file-edit"></i> Save</a>
            <ActionsButton class="uk-margin-small-right" :actions="model.actions"
                           :ids="[id]"
                           v-if="!isCreating && model.actions.length >0"></ActionsButton>
            <a action="reset"
               @click="resetToLastSaved"
               class="uk-button uk-button-default uk-margin-small-right"
               v-if="itemIsLoaded && !isCreating"
            >Reset to last saved</a>


            <a action="clear"
               @click="clear"
               class="uk-button uk-button-danger uk-margin-small-right"
               v-if="itemIsLoaded "
            >Clear</a>
        </template>

        <!-- TableView -->
        <template #main>
            <div class="uk-container uk-margin-top" v-if="itemIsLoaded">
                <h1>{{model.name}}</h1>
                <ObjectField :model="model" v-model="item"/>
            </div>
        </template>
    </BaseLayout>

</template>


<style lang="scss" scoped>
    .uk-button.uk-disabled {
        background-color: lightgray;
    }

    .uk-button-default {
        background-color: #e8e8e8;
    }

    .add-button {
        border-radius: 10000px;
    }

    #main-spinner {
        position: absolute;
        top: 20%;
        left: 50%;
        transform: translateX(-50%);
    }

    .nav {
        box-shadow: 0 0px 5px #444;
    }

    .main {
        font-size: 0.8em;

        height: 100vh;
        overflow-y: scroll;
    }
</style>


<script>

    import ObjectField from "./fields/ObjectField";
    import Utils from "../utils";
    import Vue from "vue";
    import BaseLayout from "./base/BaseLayout.vue";

    import ActionsButton from "./settings/ActionsButton";

    import isEqual from "lodash/isEqual"

    const UIkit = require("uikit");
    const axios = require("axios/index");

    Vue.use(require('vue-shortkey'));
    export default {
        name: "ElepySingle",
        data() {
            return {
                item: {},
                itemCopy: null,
                isLoading: false,
                isSaved: false
            };
        },

        props: ["model", "recordId", "adding", "singleMode"],
        components: {ObjectField, ActionsButton, BaseLayout},

        computed: {
            //Return if it should be a PUT or POST
            isCreating() {
                if (this.recordId != null) {
                    return false;
                }
                return this.id == null || this.adding === true;
            },
            itemIsLoaded() {
                return this.item != null;
            },
            id() {
                return this.recordId ?? this.item[this.model.idProperty];
            },


        },

        methods: {
            goBack() {

                if (isEqual(this.item, this.itemCopy)) {
                    this.$router.push(this.model.path)
                } else {
                    UIkit.modal.confirm("Are you sure you want to go back? Any unsaved changes will be lost.", {
                        labels: {
                            ok: "Yes",
                            cancel: "No"
                        }
                    }).then(
                        () => this.$router.push(this.model.path),
                        () => {
                        }
                    );
                }

            },
            clear() {
                const id = this.id;
                this.item = {};
                this.item[this.model.idProperty] = id;
            },
            resetToLastSaved() {
                this.getRecord();
            },
            save() {
                UIkit.modal
                    .confirm("Are you sure that you want to save this item?", {
                        labels: {
                            ok: "Yes",
                            cancel: "No"
                        }
                    })
                    .then(
                        () => {
                            axios({
                                method: this.isCreating ? "POST" : "PUT",
                                data: this.item,
                                url:
                                    Utils.url +
                                    this.model.path +
                                    (this.isCreating ? "" : "/" + this.item[this.model.idProperty])
                            })
                                .then(response => {
                                    Utils.displayResponse(response);
                                    if (this.isCreating) {
                                        let createdRecord = response.data.properties.createdRecords[0];
                                        let createdRecordId = createdRecord[this.model.idProperty];
                                        if (!this.singleMode)
                                            this.$router.push(this.model.path + "/edit/" + createdRecordId);
                                    } else {
                                        this.getRecord();
                                    }
                                })
                                .catch(error => {
                                    Utils.displayError(error);
                                });
                        },
                        () => {
                        }
                    );
            },

            getRecord() {
                if (this.isCreating) {
                    this.item = {};
                    this.itemCopy = {};
                } else if (this.recordId != null) {
                    return axios
                        .get(this.model.path + "/" + this.recordId)
                        .then(response => {
                            this.item = response.data;
                            this.itemCopy = JSON.parse(JSON.stringify(this.item));
                        })
                        .catch(error => {
                            this.$router.push(this.model.path);
                            Utils.displayError(error);
                        });
                } else {
                    return axios
                        .get(this.model.path + "?pageSize=1&pageNumber=1")
                        .then(response => {
                            this.item = response.data.values[0] || {};
                            this.itemCopy = JSON.parse(JSON.stringify(this.item));
                        })
                        .catch(error => {
                            Utils.displayError(error);
                        });
                }

            }
        },
        mounted() {
            this.getRecord();
            document.addEventListener("keydown", e => {
                if (e.key === 's' && (navigator.platform.match("Mac") ? e.metaKey : e.ctrlKey)) {
                    e.preventDefault();
                    this.save();
                }
            }, false);
        }
    };

    String.prototype.isEmpty = function () {
        return this.length === 0 || !this.trim();
    };
</script>


