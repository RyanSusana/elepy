<template>
    <div :class="{'is-loading': isLoading}">

        <div class="elepy-table">
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
                        </div>
                    </th>
                    <th
                            :key="field.name"
                            class="data-head"
                            v-for="field in tableFields"
                    >{{field.label}}
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
                        v-for="data in currentPage"
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
    .table-controls {
        margin: 0 150px;
    }

    .is-loading {
        opacity: 0.3;
    }

    .elepy-table {
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

    .options {
        min-width: 70px;
    }
</style>
<script>
    import TableRow from "./TableRow.vue";
    import Utils from "../../utils";
    import QueryFilter from "../settings/QueryFilter.vue";
    import Pagination from "../settings/Pagination";

    import {mapGetters, mapState} from "vuex"
    import EventBus from "../../event-bus";

    import axios from "axios";

    export default {
        props: ["model", "current-page", "isLoading", "updateEnabled"],


        components: {TableRow, QueryFilter, Pagination},
        computed: {
            ...mapState(["selectedRows"]),
            ...mapGetters(["canExecute"]),
            singleActions() {
                return this.model.actions.filter(action => action.singleRecord && this.canExecute(action));
            },
            tableFields() {
                return this.model.properties.filter(function (field) {
                    return (
                        field.type !== "OBJECT" &&
                        field.type !== "CUSTOM" &&
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
                return this.currentPage.rowIds(this.model.idProperty);
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

            updateData() {
                EventBus.$emit("updateData");
            },
            selectAll() {
                this.deselectAll();
                this.$store.commit("SELECT_ROWS", this.currentPageIds);
            },
            deselectAll() {
                this.$store.commit("SET_SELECTED_ROWS", this.selectedRows.filter(
                    el => this.currentPageIds.indexOf(el) < 0
                ));
            },
            tableRowSelected(id) {
                if (!this.isSelected(id)) {
                    this.$store.commit("SELECT_ROWS", [id])
                } else {
                    this.$store.commit("SET_SELECTED_ROWS", this.selectedRows.filter(i => i !== id));
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

