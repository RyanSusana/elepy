<template>

    <div class="page">
        <SideBar class="sidebar"/>

        <div class="main-view">
            <router-view/>
        </div>
    </div>
</template>

<style lang="scss">

    @import "../../scss/main.scss";

    html,
    body {
        height: 100%;
    }

    .page{
        display: grid;
        grid-template-columns: 350px 1fr;
    }

    .sidebar{
        flex: 1;
    }

    .main-view{
        flex:4;
    }
</style>

<script>

    import SideBar from "../components/SideBar";

    export default {
        components: {SideBar},
        data() {
            return {};
        },


        methods: {},

    };
</script>

