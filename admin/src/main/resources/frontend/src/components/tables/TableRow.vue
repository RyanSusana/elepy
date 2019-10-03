<template>
    <tr :id="'row-'+data[model.idProperty]" :row="data[model.idProperty]">
        <td class="uk-background-muted uk-text-center">
            <input :checked="selected" class="uk-checkbox" type="checkbox" v-on:change="selectChange()">
        </td>
        <TableColumnData
                :field="field"
                :value="data[field.name]"
                @click="$emit('rowClicked')"
                class="data-column"
                v-bind:key="field.name"
                v-for="field in fields"
        />
        <td class="data-column">
            <div class="uk-flex uk-flex-none">
                <a
                        @click="editData()"
                        action="edit"
                        class="uk-icon-button uk-margin-small-right"
                        uk-icon="pencil"
                        v-if="updateEnabled"
                ></a>

                <div class="uk-button-group" v-if="this.actions.length >0">
                    <button
                            @click="executeAction()"
                            class="uk-button uk-button-primary action-button"
                    >{{selectedAction.name}}
                    </button>
                    <div class="uk-inline">
                        <button action="select" class="uk-button uk-button-primary action-select" type="button">
                            <span uk-icon="icon:  triangle-down"></span>
                        </button>
                        <div uk-dropdown="mode: click; boundary: ! .uk-button-group; pos: bottom-right;">
                            <div class="action-list">
                                <div
                                        :action="action.name"
                                        :key="action.name"
                                        class="action-item uk-text-center"
                                        v-for="action in actions"
                                        v-on:click="selectedAction = action"
                                >{{action.name}}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <a
                        @click="deleteData()"
                        action="delete"
                        class="uk-icon-button uk-button-danger uk-color-light uk-margin-small-left"
                        uk-icon="trash"
                ></a>
            </div>
        </td>
    </tr>
</template>
<style lang="scss">
    .data-column {
        max-width: 25vw;
    }

    .action-button {
        min-width: 150px;
        font-size: 0.85em !important;
        text-transform: none !important;
    }

    .action-list {
        .action-item {
            padding: 10px;
            cursor: pointer;
            box-sizing: border-box;

            &:nth-child(2n) {
                background: #f2f2f2;
            }

            &:hover {
                box-shadow: inset 0 0 2px #39f;
            }
        }
    }

    .uk-button-group {
        .uk-dropdown {
            padding: 0 !important;
        }
    }

    .action-select {
        padding: 0 !important;
        border-top-left-radius: 0 !important;
        border-bottom-left-radius: 0 !important;
        margin-left: 1px !important;
    }

    .action-button {
        border-top-right-radius: 0 !important;
        border-bottom-right-radius: 0 !important;
    }
</style>

<script>
    import TableColumnData from "./TableColumnData.vue";
    import UIkit from "uikit";
    import Utils from "../../utils";
    import EventBus from "../../event-bus.js";

    const axios = require("axios/index");
    export default {
        props: [
            "id-field",
            "fields",
            "data",
            "selected",
            "actions",
            "model",
            "updateEnabled"
        ],
        components: {TableColumnData},

        data() {
            return {
                selectedAction: {}
            };
        },

        created() {
            this.selectedAction = this.actions[0];
        },
        methods: {
            selectChange() {
                this.$emit("tableRowSelected", this.data[this.idProperty]);
            },
            editData() {
                EventBus.$emit("editData", this.data);
            },
            deleteData() {
                UIkit.modal
                    .confirm("Are you sure that you want to delete this item?", {
                        labels: {
                            ok: "Yes",
                            cancel: "Cancel"
                        },
                        stack: true
                    })
                    .then(
                        () => {
                            axios({
                                method: "delete",
                                url:
                                    Utils.url +
                                    this.model.slug +
                                    "/" +
                                    this.data[this.model.idProperty]
                            })
                                .then(response => {
                                    EventBus.$emit("updateData");
                                    Utils.displayResponse(response);
                                })
                                .catch(function (error) {
                                    Utils.displayError(error);
                                });
                        },
                        function () {
                        }
                    );
            },
            executeAction() {
                axios({
                    method: this.selectedAction.method,
                    url:
                        Utils.url +
                        this.selectedAction.slug +
                        "?id=" +
                        this.data[this.model.idProperty]
                })
                    .then(response => {
                        this.$emit("updateData");
                        Utils.displayResponse(response);
                    })
                    .catch(function (error) {
                        Utils.displayError(error);
                    });
            }
        }
    };
</script>
