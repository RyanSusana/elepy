<template>
    <div :class="{'is-loading': isLoading}">
        <div class="uk-padding uk-padding-remove-top elepy-table">
            <table class="uk-table uk-table-hover uk-table-divider uk-table-middle" id="table-data">
                <thead>
                <tr>
                    <th class="uk-table-shrink">
                        <div class="uk-flex uk-flex-middle options">
                            <input
                                    :checked="allSelected"
                                    class="uk-checkbox"
                                    id="select-deselect-all-checkbox"
                                    name
                                    type="checkbox"
                                    v-on:change="allSelected? deselectAll():selectAll()"
                            >
                            <div class="uk-margin-small-left uk-text-small">{{selectedRows.length}}</div>
                            <div class>
                                <div class="uk-inline multi-action">
                                    <a class="uk-icon-link" uk-icon="icon: more-vertical; ratio: 1"></a>
                                    <div uk-drop="mode: click; pos:bottom-left">
                                        <div class="uk-card uk-card-body uk-card-default uk-padding-small">
                                            <ul class="uk-nav uk-nav-defalt">
                                                <li class="uk-nav-header">{{selectedRows.length}} {{model.name}}
                                                    selected
                                                </li>

                                                <li class="uk-nav-divider"></li>
                                                <li class="uk-flex uk-flex-middle uk-text-warning">
                                                    <span uk-icon="icon: close;"></span>
                                                    <a
                                                            @click="selectedRows = []"
                                                            action="clear"
                                                            class="uk-margin-small-left uk-text-warning"
                                                    >Clear Selection</a>
                                                </li>
                                                <li class="uk-flex uk-flex-middle uk-text-danger">
                                                    <span uk-icon="icon: trash;"></span>
                                                    <a
                                                            @click="deleteData()"
                                                            action="delete"
                                                            class="uk-margin-small-left uk-text-danger"
                                                    >Delete Selected {{model.name}}</a>
                                                </li>
                                                <li class="uk-nav-divider" v-if="multiActions.length > 0"></li>
                                                <li :key="action.name" class="uk-active" v-for="action in multiActions">
                                                    <a :action="action.name" @click="executeAction(action)">{{action.name}}</a>
                                                </li>
                                            </ul>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </th>
                    <th
                            :key="field.name"
                            class="data-head"
                            v-for="field in tableFields"
                    >{{field.prettyName}}
                    </th>
                    <th>Actions</th>
                </tr>
                </thead>

                <tbody>
                <TableRow
                        :actions="singleActions"
                        :data="data"
                        :fields="tableFields"
                        :idProperty="model.idProperty"
                        :key="data[model.idProperty]"
                        :model="model"
                        :selected="isSelected(data[model.idProperty])"
                        :updateEnabled="updateEnabled"
                        v-for="data in currentPage.values"
                        v-on:click="selectedData = JSON.parse(JSON.stringify(data)); editData()"
                        v-on:editClicked="editData(data)"
                        v-on:tableRowSelected="tableRowSelected"
                        v-on:updateData="updateData()"
                />
                </tbody>
            </table>
        </div>
    </div>
</template>
<style lang="scss">
    .is-loading {
        opacity: 0.3;
    }
    .elepy-table{
        min-height: 50vh;
    }

    .uk-nav-divider {
        border-top: 1px solid #e5e5e5;
        margin: 5px 5px !important;
    }

    .multi-action {
        margin-left: 5px;
        text-transform: none;
        z-index: 100;

        .uk-button {
            padding: 0 10px;
        }
    }
    .options
    {
        min-width: 70px;
    }
</style>
<script>
    import TableRow from "./TableRow.vue";
    import Utils from "../../utils";
    import UIkit from "uikit";

    import EventBus from "../../event-bus";

    const axios = require("axios/index");

    export default {
        props: ["model", "current-page", "isLoading", "updateEnabled"],
        data() {
            return {
                selectedRows: []
            };
        },
        components: {TableRow},
        computed: {
            singleActions() {
                return this.model.actions;
            },
            multiActions() {
                return this.model.actions.filter(
                    action => action["actionType"] === "MULTIPLE"
                );
            },
            tableFields() {
                return this.model.properties.filter(function (field) {
                    return (
                        field.type != "OBJECT" &&
                        !field.type.includes("ARRAY") &&
                        field.importance >= 0 &&
                        field.importance <= 100
                    );
                });
            },
            allSelected() {
                return this.currentPageIds.every(elem => {
                    return this.selectedRows.includes(elem);
                });
            },

            currentPageIds() {
                return this.currentPage.values.rowIds(this.model.idProperty);
            }
        },
        methods: {
            editData(data) {
                EventBus.$emit("editingDataChanged", data);
            },
            executeAction(selectedAction) {
                axios({
                    method: selectedAction.method,
                    url:
                        Utils.url +
                        selectedAction.path +
                        "?ids=" +
                        this.selectedRows.join(",")
                })
                    .then(response => {
                        this.$emit("updateData");
                        Utils.displayResponse(response);
                    })
                    .catch(function (error) {

                    });
            },
            deleteData() {
                UIkit.modal
                    .confirm(
                        "Are you sure that you want to delete " +
                        this.selectedRows.length +
                        " items?",
                        {
                            labels: {
                                ok: "Yes",
                                cancel: "Cancel"
                            },
                            stack: true
                        }
                    )
                    .then(
                        () => {
                            axios({
                                method: "delete",
                                url:
                                    Utils.url +
                                    this.model.path +
                                    "?ids=" +
                                    this.selectedRows.join(",")
                            })
                                .then(response => {
                                    this.$emit("updateData");
                                    this.selectedRows = [];
                                    Utils.displayResponse(response);
                                })
                                .catch(function (error) {

                                });
                        },
                        function () {
                        }
                    );
            },
            updateData() {
                EventBus.$emit("updateData");
            },
            selectAll() {
                this.deselectAll();
                this.selectedRows = this.selectedRows.concat(this.currentPageIds);
            },
            deselectAll() {
                this.selectedRows = this.selectedRows.filter(
                    el => this.currentPageIds.indexOf(el) < 0
                );
            },
            tableRowSelected(id) {
                if (!this.isSelected(id)) {
                    this.selectedRows.push(id);
                } else {
                    this.selectedRows = this.selectedRows.filter(i => i !== id);
                }
            },
            isSelected(id) {
                return this.selectedRows.includes(id);
            }
        }
    };
    Array.prototype.rowIds = function (idProperty) {
        return this.map(row => row[idProperty]);
    };
</script>

