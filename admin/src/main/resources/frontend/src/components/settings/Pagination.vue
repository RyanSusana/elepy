<template>
    <div class="uk-flex">
        <div class="amount-result-box uk-light">
            <select
                    @change="emitChange"
                    class="uk-select pagination-select"
                    v-model="pagination.pageSize"
            >
                <option :value="10">10 results</option>
                <option :value="25">25 results</option>
                <option :value="75">75 Results</option>
                <option :value="100">100 Results</option>
            </select>
        </div>
        <ul class="pagination uk-pagination uk-light uk-flex-center uk-flex-middle">
            <li>
                <a v-on:click="setPage(pagination.currentPageNumber - 1)">
                    <span uk-pagination-previous></span>
                </a>
            </li>
            <li>
                <input
                        :value="pagination.currentPageNumber "
                        class="uk-input pagination-input"
                        type="number"
                        v-on:input="setPage($event.target.value)"
                >
            </li>
            <li>of {{lastPageNumber}}</li>
            <li>
                <a v-on:click="setPage(pagination.currentPageNumber + 1)">
                    <span uk-pagination-next></span>
                </a>
            </li>
        </ul>
    </div>
</template>

<style lang="scss" scoped>
    .pagination {
        margin: 0 20px;
        padding: 0 10px;
        border-left: 1px solid #444;
        border-right: 1px solid #444;

        li {
            padding: 0 10px;
        }
    }

    .pagination-select {
        width: 150px;
    }

    .pagination-input {
        width: 70px;
        text-align: right;
    }
</style>


<script>
    export default {
        props: ["value", "lastPageNumber"],

        data() {
            return {
                pagination: {
                    currentPageNumber: 1,
                    pageSize: 25,
                    nextPageNumber: 1
                }
            };
        },
        methods: {
            setPage(page) {
                let last = this.lastPageNumber;
                if (page >= last) page = last;

                if (page <= 0) page = 1;

                this.pagination.currentPageNumber = page;
                this.emitChange();
            },

            emitChange() {
                this.$emit("input", this.paginationString());
                this.$emit("change");
            },
            paginationString() {
                return (
                    "pageSize=" +
                    this.pagination.pageSize +
                    "&pageNumber=" +
                    this.pagination.currentPageNumber
                );
            }
        }
    };
</script>