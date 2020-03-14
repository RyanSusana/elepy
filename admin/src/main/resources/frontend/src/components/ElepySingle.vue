<template>
    <div class="uk-flex uk-height-1-1 main">
        <div class="uk-width-1-1 uk-height-1-1 uk-height-1-1">
            <div class="uk-background-secondary uk-width-1-1 nav uk-padding-small" uk-sticky>
                <div class="uk-container" v-if="itemIsLoaded">
                    <a
                            @click="save()"
                            @shortkey="save()"
                            class="uk-button uk-button-primary uk-margin-small-right"
                            v-shortkey.once="['ctrl',  'save']"

                    >Save</a>

                    <a
                            @click="resetToLastSaved"
                            class="uk-button uk-button-default uk-margin-small-right"
                            v-if="itemIsLoaded && !isCreating"
                    >Reset to last saved</a>

                    <ActionsButton class="uk-margin-small-right" :actions="model.actions"
                                   :ids="[this.id]"
                                    v-if="!isCreating && model.actions.length >0"></ActionsButton>
                    <a
                            @click="clear"
                            class="uk-button uk-button-danger uk-margin-small-right"
                            v-if="itemIsLoaded "
                    >Clear</a>

                </div>
            </div>
            <div class="uk-container uk-margin-top" v-if="itemIsLoaded">
                <h1>{{model.name}}</h1>
                <ObjectField :model="model" v-model="item"/>
            </div>
        </div>
    </div>
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
    }
</style>


<script>

    import ObjectField from "./fields/ObjectField";
    import Utils from "../utils";
    import Vue from "vue";

    import EventBus from "../event-bus";
    import ActionsButton from "./settings/ActionsButton";

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

        props: ["model"],
        components: {ObjectField, ActionsButton},

        computed: {
            //Return if it should be a PUT or POST
            isCreating() {
                return this.id == null;
            },
            itemIsLoaded() {
                return this.item !== null;
            },
            id() {
                return this.item[this.model.idProperty];
            },


        },

        methods: {
            clear() {
                const id = this.id;
                this.item = {};
                this.item[this.model.idProperty] = id;
            },
            resetToLastSaved() {
                this.getFirstRecord();
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
                                    this.getFirstRecord();
                                })
                                .catch(error => {
                                    Utils.displayError(error);
                                    this.getFirstRecord();
                                });
                        },
                        () => {
                        }
                    );
            },

            getFirstRecord() {
                axios
                    .get(Utils.url + this.model.path + "?pageSize=1&pageNumber=1")
                    .then(response => {
                        this.item = response.data.values[0] || {};
                        this.itemCopy = JSON.parse(JSON.stringify(this.item));
                    })
                    .catch(error => {
                        Utils.displayError(error);
                    });
            }
        },
        mounted() {
            this.getFirstRecord();
            EventBus.$on("updateData", () => {
                this.getFirstRecord();
            });
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


