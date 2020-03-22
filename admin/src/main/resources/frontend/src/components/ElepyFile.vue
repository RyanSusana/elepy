<template>
    <BaseLayout>
        <!-- Navigation -->
        <template #navigation>
            <div class="default-bar">
                <div class="button-box ">

                </div>
                <div class="uk-flex search-filter-box">
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
                        :currentPage="currentPage"
                        :isLoading="isLoading"
                        :model="model"
                        :selected-rows="selectedRows"
                        :updateEnabled="false"
                />
            </slot>
        </template>
    </BaseLayout>
</template>
<style>
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

            translateFile(size) {
                if (size >= 1024 * 1024) {
                    return Math.round(size / 1024 / 1024) + " MB";
                }
                if (size >= 1024) {
                    return Math.round(size / 1024) + " KB";
                }
                return Math.round(size) + " Bytes";
            },
            getModelData() {
                var ref = this;
                let searchUrl =
                    Utils.url +
                    ref.model.path +
                    "?" +
                    this.pagination +
                    "&" +
                    this.queryFilter;

                EventBus.$emit("startLoading");
                axios
                    .get(searchUrl)
                    .then(response => {
                        EventBus.$emit("stopLoading");
                        response.data.values.map(
                            file => (file.size = this.translateFile(file.size))
                        );
                        ref.currentPage = response.data;
                    })
                    .catch(function (error) {
                        EventBus.$emit("stopLoading");

                    });
            }
        },
        mounted() {
            this.getModelData();

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


