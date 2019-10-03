<template>
    <div class="uk-flex uk-height-1-1 main">
        <div class="uk-width-1-1 uk-height-1-1 table-view uk-height-1-1">
            <div class="uk-background-secondary uk-width-1-1 nav" uk-sticky>
                <div class="uk-flex uk-flex-middle uk-flex-center uk-padding-small">
                    <slot name="navigation"></slot>
                </div>
            </div>
            <div class>
                <slot name="main"></slot>
            </div>
        </div>
    </div>
</template>
<style lang="scss">
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
        box-shadow: 0 0px 5px #444;
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

