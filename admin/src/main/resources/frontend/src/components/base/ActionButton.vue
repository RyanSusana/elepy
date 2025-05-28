<template>
  <button @click="doAction"
          :action="actionName"
          class="uk-button"><span
      v-if="!busy"><slot></slot></span><span uk-spinner v-else></span></button>
</template>

<script>
import { useMainStore } from "@/stores/main";

export default {
  name: "ActionButton",
  props: ['action', 'actionName'],
  setup() {
    const store = useMainStore()
    return {
      store
    }
  },

  data() {
    return {busy: false}
  },

  methods: {
    async doAction() {

      if (this.busy) {
        return;
      }
      this.busy = true;

      try {
        await this.action();
        this.store.clearNavigationWarning()
      } finally {
        this.busy = false;
      }

    }
  }
}
</script>

<style scoped>

</style>