<template>
    <div id="pagination"
         class="uk-flex uk-flex-middle uk-flex-between uk-width-xlarge uk-background-muted uk-padding-small">
        <ul class="uk-pagination uk-margin-remove">

            <li><a @click="previousPage" v-if="currentPageNumber!==1"><span uk-pagination-previous></span></a></li>

        </ul>
        <ul class="uk-pagination uk-margin-remove">
            <li v-if="currentPageNumber!==1"><a @click="setPage(1)">1</a></li>

            <li class="uk-disabled" v-if="!currentPageIsInBeginning"><span>...</span></li>
            <li v-for="page in previousValues"><a @click="setPage(page)">{{page}}</a></li>

            <li class="uk-active"><span class="current-page"
                                        :class="{'last-page': lastPageNumber = currentPageNumber}">{{currentPageNumber}}</span>
            </li>

            <li v-for="page in nextValues"><a v-on:click="setPage(page)">{{page}}</a></li>
            <li v-if="!currentPageIsInEnd" class="uk-disabled"><span>...</span></li>

            <li v-if="currentPageNumber!==lastPageNumber"><a @click="setPage(lastPageNumber)" class="last-page">{{lastPageNumber}}</a>
            </li>

        </ul>

        <ul class="uk-pagination uk-margin-remove">
            <li><a v-if="currentPageNumber !== lastPageNumber" @click="nextPage"><span uk-pagination-next></span></a>
            </li>

        </ul>
    </div>
</template>

<style lang="scss" scoped>
    .uk-pagination {
        user-select: none;
    }

</style>


<script>

    import {clamp, range} from "lodash"

    export default {
        props: ["value", "amountOfRecords"],

        watch: {
            amountOfRecords: function () {
                this.currentPageNumber = 1;
            }
        },
        data() {
            return {
                currentPageNumber: 1,
                pageSize: 25,
            };
        },

        computed: {

            currentPageIsInBeginning() {
                return this.currentPageNumber <= 4;
            },
            previousValues() {

                return range(Math.max(this.currentPageNumber - 3, 2), this.currentPageNumber, 1)

            },
            nextValues() {
                return range(this.currentPageNumber + 1, Math.min(this.currentPageNumber + 4, this.lastPageNumber), 1)

            },
            currentPageIsInEnd() {
                return this.currentPageNumber >= (this.lastPageNumber - 3);
            },

            lastPageNumber() {
                return Math.max(1, Math.ceil((this.amountOfRecords ?? this.pageSize) / this.pageSize));
            },

        },
        methods: {
            previousPage() {
                return this.setPage(this.currentPageNumber - 1);
            },
            nextPage() {
                return this.setPage(this.currentPageNumber + 1);
            },
            setPage(page) {
                this.currentPageNumber = clamp(page, 1, this.lastPageNumber);

                this.emitChange();
            },

            emitChange() {
                this.$emit("input", this.paginationString());
                this.$emit("change");
            },
            paginationString() {
                return (
                    "pageSize=" +
                    this.pageSize +
                    "&pageNumber=" +
                    this.currentPageNumber
                );
            }
        }
    };
</script>