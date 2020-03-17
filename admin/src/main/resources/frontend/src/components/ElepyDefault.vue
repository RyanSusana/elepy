<template>
    <BaseLayout>
        <!-- Navigation -->
        <template #navigation>
            <div class="default-bar">
                <div class="button-box ">
                    <router-link v-if="model!=null"
                                 :to="model.path+'/add'"
                                 class="uk-button uk-button-primary add-button"
                                 id="add-button"
                    >
                        <i uk-icon="icon: plus"></i> Add
                    </router-link>
                </div>
                <div class="uk-flex search-filter-box uk-margin-large-left">
                    <Pagination
                            :lastPageNumber="currentPage.lastPageNumber"
                            @change="getModelData()"
                            v-model="pagination"
                    />
                    <QueryFilter :model="model" @change="getModelData()" v-model="queryFilter"/>
                </div>
            </div>

        </template>

        <!-- TableView -->
        <template #main>
            <slot name="pageDetails">
                <Table
                        v-if="!isLoading"
                        :currentPage="currentPage"
                        :isLoading="isLoading"
                        :model="model"
                        :selected-rows="selectedRows"
                        :updateEnabled="true"
                        v-on:updateData="getModelData()"
                />
            </slot>
        </template>
    </BaseLayout>
</template>

<style lang="scss">

    .default-bar{
        display: flex;

        justify-content: start;
    }
</style>
<script>
    import QueryFilter from "./settings/QueryFilter.vue";

    import EventBus from "../event-bus";
    import Table from "./tables/Table.vue";
    import Utils from "../utils";
    import Pagination from "./settings/Pagination.vue";
    import BaseLayout from "./base/BaseLayout.vue";

    const axios = require("axios/index");

    export default {
        name: "Elepy",
        watch: {
            $route: {
                handler: 'getModelData',
                immediate: true
            }
        },
        data() {
            return {
                queryFilter: "",
                selectedRows: [],
                currentPage: {
                    currentPageNumber: 1,
                    lastPageNumber: 1,
                    values: []
                },
                pagination: "pageSize=25&pageNumber=1",
                isLoading: false
            };
        },

        props: ["model"],
        components: {QueryFilter, Pagination, Table, BaseLayout},
        methods: {
            addData() {
                EventBus.$emit("addData", {});
            },

            getModelData() {
                var ref = this;
                let searchUrl =
                    ref.model.path +
                    "?" +
                    this.pagination +
                    "&" +
                    this.queryFilter;

                EventBus.$emit("startLoading");
                axios
                    .get(searchUrl)
                    .then(function (response) {
                        EventBus.$emit("stopLoading");
                        ref.currentPage = response.data;
                    })
                    .catch(function (error) {
                        EventBus.$emit("stopLoading");
                        Utils.displayError(error);
                    });
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


