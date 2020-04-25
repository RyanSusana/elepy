<template>

    <div class="query-bar">
        <div id="search" class="uk-flex-1">
            <div class="uk-inline uk-width-1-1 uk-margin-small-right">
                <span class="uk-form-icon" uk-icon="icon: search"></span>
                <input
                        class="uk-input"
                        id="search-input"
                        placeholder="Search..."
                        type="text"
                        v-model="search"
                >
            </div>

        </div>

    </div>

</template>

<script>
    import {debounce} from "lodash";

    export default {
        name: "QueryBar",
        props: ["value", "model"],

        watch: {
            search: debounce(function () {
                this.$emit("input", this.search);
            }, 500)
        },
        data() {
            return {
                search: "",
            }
        },


    }
</script>

<style scoped>


</style>