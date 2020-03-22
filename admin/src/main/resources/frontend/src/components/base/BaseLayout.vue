<template>
    <div class="uk-flex uk-height-1-1 main">
        <div class="uk-width-1-1 uk-height-1-1 table-view uk-height-1-1">
            <div class="uk-background-secondary uk-width-1-1 nav uk-padding-small" uk-sticky>
                <div class="back">

                    <LoadingSpinner v-if="isLoading"/>

                    <a v-if="backEnabled" @click="goBack" class="back-button" action="back"><span
                            uk-icon="icon: arrow-left; ratio:2"></span><span>Back</span></a>

                </div>
                <div>
                    <slot name="navigation"></slot>
                </div>
            </div>
            <div class>
                <slot name="main"></slot>
            </div>
        </div>
    </div>
</template>

<script>
    import LoadingSpinner from "./LoadingSpinner";

    import {mapGetters} from "vuex";

    export default {
        name: "BaseLayout",
        components: {LoadingSpinner},
        props: ["backLocation"],

        computed: {

            ...mapGetters(['isLoading']),

            backEnabled() {
                return this.backLocation != null || this.backLocation === false
            }
        },
        methods: {
            goBack() {
                if (typeof this.backLocation === 'function') {
                    return this.backLocation();
                } else if (typeof this.backLocation === 'string') {
                    return this.$router.push(this.backLocation)
                } else {
                    return this.$router.go(-1);
                }
            }
        }

    }

</script>
<style lang="scss">
    @import "../../../scss/main";

    .back-button {
        color: darken($global-muted-background, 40%);
        text-decoration: none;

        &:hover {


            color: $global-muted-background;
            text-decoration: none;

        }

    }

    .add-button {
        border-radius: 10000px;
    }

    #main-spinner {
        position: absolute;
        top: 20%;
        left: 50%;
        transform: translateX(-50%);
    }

    .nav {

        display: grid;
        grid-template-columns: 100px 3fr;
    }

    .main {
        font-size: 0.8em;
    }

    .table-view {
        overflow-y: scroll;
    }

    #table-data {
        margin-top: 0;
    }
</style>

