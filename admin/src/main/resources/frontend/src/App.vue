<template>
    <router-view/>
</template>

<style lang="scss">

    @import "../scss/main.scss";

    html,
    body {
        height: 100%;
    }

    .page{
        display: grid;
        grid-template-columns: 1fr 3fr;
    }
</style>

<script>

import SideBar from "./components/SideBar";

export default {
        components: {SideBar},
        data() {
            return {};
        },
        watch: {
            '$route': {
                // eslint-disable-next-line no-unused-vars
                handler: (to, from) => {
                    if (to.meta.title) {
                        document.title = to.meta.title + ' - Elepy'
                    }
                },
                immediate: true
            }
        },

    };
</script>

