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


.page {
  display: grid;
  grid-template-columns: 275px 1fr;
  height: 100%;
}

.main-view {

  overflow-y: scroll;
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

