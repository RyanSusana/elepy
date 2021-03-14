<template>
  <div id="revisions" uk-offcanvas="flip: true; bg-close: false;">
    <div class="uk-offcanvas-bar revision-bar uk-text-success">


      <h3><a class="close-button" uk-icon="icon: arrow-left; ratio: 1.5;" uk-toggle="target: #revisions"
             type="button"></a> {{ $t('elepy.ui.revisions') }}
      </h3>
      <div class="">

        <ul>
          <li v-for="record in records">{{ record.newSnapshot[model.idProperty]  }} -- {{ record.timestamp | relativeTime}}</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>

const axios = require("axios/index");
export default {
  name: "RevisionHistory",
  props: ["model"],
  data() {
    return {
      records: [],
    };
  },
  methods: {
    async getHistory() {
      let response = await axios.get("/revisions", {
        params: {
          schema: this.model.path
        }
      });

      this.records = response.data.map(record => {
        return {...record, oldSnapshot: record.oldSnapshot, newSnapshot: record.newSnapshot}
      })
    },
  },
  mounted() {
    this.getHistory();
  }
}
</script>

<style lang="scss" scoped>

.revision-bar {
  background-color: #eee;

  .close-button {
    left: 20px;

    color: #222;
  }

  h3 {
    color: #222;
  }
}

</style>