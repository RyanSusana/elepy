<template>
    <div class>
        <div class="uk-modal-container" id="add-modal" uk-modal="bg-close: false; stack: true;">
            <div class="uk-modal-dialog uk-modal-container">
                <a @click="closeModal" class="uk-icon-link uk-modal-close-default" href="#" uk-icon="close"></a>

                <div class="uk-modal-header">
                    <h2 class="uk-modal-title">Add to {{model.name}}</h2>
                </div>

                <div class="uk-modal-body" uk-overflow-auto>
                    <ObjectField :model="model" @input="handleInput()" v-model="item"/>
                </div>

                <div class="uk-modal-footer uk-text-right">
                    <button
                            @click="resetData()"
                            class="uk-button uk-button-default reset-form-button"
                            type="button"
                    >Reset
                    </button>
                    <button
                            @click="closeModal"
                            class="uk-button uk-button-default cancel-button"
                            type="button"
                    >Cancel
                    </button>
                    <button @click="save()" class="uk-button uk-button-primary save-button" type="button">Save</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="scss">
    .uk-modal-body {
        scroll-snap-type: y mandatory;
    }

    .uk-modal-container .uk-modal-dialog {
        width: 92vw;
    }
</style>
<script>
    import ObjectField from "../fields/ObjectField";

    import EventBus from "../../event-bus";

    import UIkit from "uikit";

    import axios from "axios/index";
    import Utils from "../../utils.mjs";

    export default {
        props: ["model"],

        components: {ObjectField},
        methods: {
            handleInput() {
            },
            resetData() {
                this.item = {};
                this.itemCopy = {};
            },
            closeModal() {
                this.resetData();
                EventBus.$emit("updateData");
                UIkit.modal("#add-modal").hide();
            },

            save() {
                axios({
                    method: "POST",
                    data: this.item,
                    url: Utils.url + this.model.path
                })
                    .then(response => {
                        Utils.displayResponse(response);
                        EventBus.$emit("updateData");
                        this.closeModal();
                    })
                    .catch(error => {
                        Utils.displayError(error);
                        EventBus.$emit("updateData");
                    });
            }
        },

        data() {
            return {
                item: {},
                itemCopy: {}
            };
        },
        mounted() {
            document.addEventListener("keydown", e => {
                if (e.key === 's' && (navigator.platform.match("Mac") ? e.metaKey : e.ctrlKey)) {
                    if (document.getElementById("add-modal") != null && document.getElementById("add-modal").className.includes("uk-open")) {
                        e.preventDefault();
                        this.save();
                    }
                }
            }, false);
            EventBus.$on("addData", data => {
                this.item = data;
                this.itemCopy = JSON.parse(JSON.stringify(this.item));

                this.$nextTick(() => UIkit.modal("#add-modal").show());
            });
        }
    };
</script>
