<template>
    <BaseLayout>
        <!-- Navigation -->
        <template #navigation>
            <div class="default-bar action-bar">
                <div class="button-box">
                    <router-link v-if="model!=null && canExecute(model.defaultActions.create)"
                                 :to="model.path+'/add'"
                                 class="uk-button uk-button-primary add-button uk-margin-small-right"
                                 id="add-button"
                    >
                        <i uk-icon="icon: plus"></i> Add
                    </router-link>
                    <ActionButton class="uk-button-danger uk-button uk-margin-small-right uk-width-small"
                                  :class="{'disabled': selectedRows.length === 0 }" :action="deleteData"
                                  v-if="canExecute(model.defaultActions.delete)"
                                  action-name="delete"><i uk-icon="icon: trash"></i>
                        Delete
                    </ActionButton>
                    <ActionsButton id="multi-actions" class="uk-margin-small-right actions"
                                   :actions="multipleActions"
                                   :ids="selectedRows"
                                   v-if="multipleActions.length > 0"/>
                    <ActionsButton id="single-actions" class="uk-margin-small-right actions"
                                   :actions="singleOnlyActions"
                                   :disabled="selectedRows.length !== 1"
                                   :ids="selectedRows"
                                   v-if="singleOnlyActions.length > 0"/>
                    <ActionsButton id="no-record-actions" class="uk-margin-small-right actions"
                                   :actions="noRecordActions"
                                   :ids="selectedRows"
                                   v-if="noRecordActions.length > 0"/>
                </div>

            </div>

        </template>

        <!-- TableView -->
        <template #main>
            <slot name="pageDetails">

                <div class="uk-container uk-container-expand">
                    <div class="uk-margin-top">

                        <div class="uk-flex uk-margin-small-top uk-flex-between uk-flex-middle">

                            <QueryBar class="uk-width-large" v-model="query" :model="model"/>

                            <Pagination v-model="pagination" :amountOfRecords="amountOfRecords"></Pagination>
                        </div>

                        <Table
                                v-if="!isLoading"
                                :currentPage="currentPage"
                                :isLoading="isLoading"
                                :model="model"
                                :updateEnabled="true"
                                v-on:updateData="getModelData()"
                        />

                    </div>

                </div>

            </slot>
        </template>
    </BaseLayout>
</template>

<style lang="scss">

    .default-bar {
        display: flex;

        justify-content: start;
    }
</style>
<script>
    import QueryFilter from "./settings/QueryFilter.vue";

    import Utils from "../utils"
    import EventBus from "../event-bus";
    import Table from "./tables/Table.vue";
    import Pagination from "./settings/Pagination.vue";
    import BaseLayout from "./base/BaseLayout.vue";
    import ActionsButton from "./base/ActionsButton";
    import {mapState, mapGetters} from "vuex";
    import ActionButton from "./base/ActionButton";

    import axios from "axios";
    import QueryBar from "./settings/QueryBar";

    export default {
        name: "Elepy",
        watch: {
            $route: {
                handler: 'getModelData',
                immediate: true
            },

            pagination: function () {
                return this.getModelData();
            }
            ,
            query: function () {
                return this.getModelData();
            }
        },
        computed: {
            ...mapState(["selectedRows"]),
            ...mapGetters(["canExecute"]),

            allActions() {
                return this.model.actions.filter(this.canExecute);
            },

            multipleActions() {
                return this.allActions.filter(action => action.multipleRecords === true)
            },
            singleOnlyActions() {
                return this.allActions.filter(action => action.multipleRecords === false && action.singleRecord === true)
            },

            noRecordActions() {
                return this.allActions.filter(action => !action.singleRecord && !action.multipleRecords)
            }

        },
        data() {
            return {
                amountOfRecords: 0,
                currentPage: [],
                pagination: "pageSize=25&pageNumber=1",
                query: "",
                isLoading: false
            };
        },

        props: ["model"],
        components: {QueryBar, ActionButton, ActionsButton, QueryFilter, Pagination, Table, BaseLayout},
        methods: {
            deleteData() {
                return UIkit.modal
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
                                    this.model.path +
                                    "?ids=" +
                                    this.selectedRows.join(",")
                            })
                                .then(response => {
                                    this.$store.commit("SET_SELECTED_ROWS", []);
                                    this.getModelData();
                                    Utils.displayResponse(response);
                                })
                                .catch(function (error) {

                                });
                        },
                        function () {
                        }
                    );
            },
            async getModelData() {
                let searchUrl =
                    this.model.path +
                    "?" +
                    this.pagination +
                    "&q=" +
                    this.query;
                EventBus.$emit("startLoading");

                try {
                    this.currentPage = (await axios.get(searchUrl)).data
                    this.amountOfRecords = (await axios.get(searchUrl + "&count=true")).data;

                } finally {
                    EventBus.$emit("stopLoading");
                }


            }
        },
        mounted() {
            EventBus.$on("updateData", () => {
                this.getModelData();
            });
            EventBus.$on("startLoading", () => {
                if (this.timer) {
                    clearTimeout(this.timer);
                    this.timer = null;
                }
                this.timer = setTimeout(() => {
                    this.isLoading = true;
                }, 400);
            });
            EventBus.$on("stopLoading", () => {
                if (this.timer) {
                    clearTimeout(this.timer);
                    this.timer = null;
                }
                this.timer = setTimeout(() => {
                    this.isLoading = false;
                }, 400);
            });
        }
    };
</script>


