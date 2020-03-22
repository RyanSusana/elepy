<template>

    <div class="uk-inline">
        <div class="loading-spinner">
            <span class="uk-position-center" uk-spinner="ratio: 1.5"></span>
            <a class="uk-position-center" v-if="loadingItems.length > 1"> {{loadingItems.length}}</a>
        </div>
        <div uk-drop="mode: click">
            <div class="uk-card uk-padding-small uk-card-default">
                <ul class="uk-list">
                    <li v-for="item in loadingItems" :key="item.id"
                        class="uk-muted">{{item.description}}...
                    </li>
                </ul>
            </div>
        </div>
    </div>

</template>

<script>

    import {mapGetters, mapState} from "vuex";

    export default {
        name: "LoadingSpinner",

        computed: {

            ...mapGetters(['isLoading']),
            ...mapState(['loadingItems']),
        }
    }
</script>

<style lang="scss" scoped>
    @import "../../../scss/main";

    .uk-list {
        margin: 0;

        & > li {
            margin-top: 5px !important;
            color: $global-muted-color;
        }
    }

    .loading-spinner {
        position: relative;

        width: 40px;
        height: 40px;
        cursor: pointer;


        .uk-icon, a {
            color: rgba($global-muted-background, 0.6);
            text-decoration: none;
            transition: .2s all;
        }

        &:hover {
            .uk-icon, a {
                color: var(--primary-hover-color);
            }
        }


        & > * {
            position: absolute;

        }

    }

</style>