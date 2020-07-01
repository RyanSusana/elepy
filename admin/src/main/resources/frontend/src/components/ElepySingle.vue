<template>


    <BaseLayout :back-location="singleMode? null : goBack" @keydown.meta="typeCtrl" @keydown.ctrl="typeCtrl">
        <!-- Navigation -->
        <template #navigation>
            <ActionButton
                    v-if="canSave"
                    :action="save"
                    :actionName="'save'"
                    @shortkey="save"
                    class="uk-button uk-button-primary uk-width-small uk-margin-small-right"
                    v-shortkey.once="['ctrl',  'save']"

            ><i uk-icon="icon: file-edit"></i> Save
            </ActionButton>
            <ActionsButton class="uk-margin-small-right" :actions="model.actions"
                           :ids="[id]"
                           v-if="!isCreating && actions.length >0"></ActionsButton>
            <a action="reset"
               @click="resetToLastSaved"
               class="uk-button uk-button-default uk-margin-small-right"
               v-if="itemIsLoaded && !isCreating && canSave"
            >Reset to last saved</a>


            <a action="clear"
               @click="clear"
               class="uk-button uk-button-danger uk-margin-small-right"
               v-if="itemIsLoaded && canSave"
            >Clear</a>
        </template>

        <!-- TableView -->
        <template #main>
            <div :class="{'unclickable': !canSave}" class="uk-container uk-margin-top uk-margin-large-bottom"
                 tabindex="0" @keydown.meta="typeCtrl"
                 @keydown.ctrl="typeCtrl" v-if="itemIsLoaded">
                <h1>{{model.name}}</h1>
                <ObjectField :model="model" v-model="item"/>
            </div>
        </template>
    </BaseLayout>

</template>


<style lang="scss" scoped>
    .unclickable {

        pointer-events: none;


    }

    .uk-container {
        outline: none;
    }

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

    import ActionsButton from "./base/ActionsButton";

    import isEqual from "lodash/isEqual"
    import ActionButton from "./base/ActionButton";
    import {mapGetters} from "vuex";

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
        components: {ActionButton, ObjectField, ActionsButton, BaseLayout},

        computed: {
            ...mapGetters(['canExecute']),

            canSave() {
                if (this.isCreating) {
                    return this.canExecute(this.model.defaultActions.create)
                } else {
                    return this.canExecute(this.model.defaultActions.update)
                }
            },
            actions() {
                if (this.singleMode) {
                    return this.model.actions.filter(this.canExecute);
                } else {
                    return this.model.actions.filter(action => this.canExecute(action) && (action.singleRecord || action.multipleRecords))
                }
            },
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

            title() {
                if (this.isCreating) {
                    return 'Add to ' + this.model.name;
                } else {
                    return this.model.name + "/" + this.id;
                }
            }
        },

        watch: {
            'title': {
                immediate: true,
                handler: function () {
                    document.title = this.title + " - Elepy"
                }
            }
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
                return UIkit.modal
                    .confirm("Are you sure that you want to save this item?", {
                        labels: {
                            ok: "Yes",
                            cancel: "No"
                        }
                    })
                    .then(
                        () => {
                            return axios({
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
                                        if (this.singleMode) {
                                            return this.getRecord();
                                        } else {
                                            return this.$router.push(this.model.path + "/edit/" + createdRecordId);
                                        }
                                    } else {
                                        return this.getRecord();
                                    }
                                });
                        },
                        () => {
                        }
                    );
            },

            typeCtrl(e) {
                if (e.key === 's') {

                    e.preventDefault();
                    if (this.canSave) {
                        this.save();
                    }
                }
            },
            getRecord() {
                if (this.singleMode) {
                    return axios
                        .get(this.model.path + "?pageSize=1&pageNumber=1")
                        .then(response => {
                            this.item = response.data[0] || {};
                            this.itemCopy = JSON.parse(JSON.stringify(this.item));
                        })
                }


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

                        });
                }

            }
        },
        mounted() {
            this.getRecord();


        }
    };

    String.prototype.isEmpty = function () {
        return this.length === 0 || !this.trim();
    };
</script>


