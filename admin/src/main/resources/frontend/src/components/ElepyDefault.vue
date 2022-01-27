<template>
  <BaseLayout>
    <!-- Navigation -->
    <template #navigation>
      <div class="default-bar action-bar">
        <div class="button-box">
          <router-link v-if="model!=null && canExecute(model.defaultActions.create)"
                       :to="model.path+'/add'"
                       class="uk-button uk-button-primary add-button uk-margin-small-right"
                       id="add-button"
          >
            <i uk-icon="icon: plus"></i> {{ $t('elepy.ui.actions.add') }}
          </router-link>
          <ActionButton class="uk-button-danger uk-button uk-margin-small-right"
                        :class="{'disabled': selectedRows.length === 0 }" :action="deleteData"
                        v-if="canExecute(model.defaultActions.delete)"
                        action-name="delete"><i uk-icon="icon: trash"></i>
            {{ $t('elepy.ui.actions.delete') }}
          </ActionButton>
          <ActionsButton id="multi-actions" class="uk-margin-small-right actions"
                         :actions="multipleActions"
                         :ids="selectedRows"
                         v-if="multipleActions.length > 0"/>
          <ActionsButton id="single-actions" class="uk-margin-small-right actions"
                         :actions="singleOnlyActions"
                         :disabled="selectedRows.length !== 1"
                         :ids="selectedRows"
                         v-if="singleOnlyActions.length > 0"/>
          <ActionsButton id="no-record-actions" class="uk-margin-small-right actions"
                         :actions="noRecordActions"
                         :ids="selectedRows"
                         v-if="noRecordActions.length > 0"/>
        </div>

      </div>

    </template>

    <!-- TableView -->
    <template #main>
      <slot name="pageDetails">

        <div class="uk-container uk-container-expand">
          <div class="uk-margin-top">

            <div class="uk-flex uk-margin-small-top uk-flex-between uk-flex-middle">


              <Search :model="model" v-model="query"></Search>

            </div>

            <Table
                v-if="!isLoading"
                :currentPage="currentPage"
                :isLoading="isLoading"
                :model="model"
                :updateEnabled="true"
                v-on:updateData="getModelData()"
            />

            <div class="uk-flex uk-flex-center uk-margin-large-bottom">
              <Pagination v-model="pagination" :amountOfRecords="amountOfRecords"></Pagination>
            </div>
          </div>

        </div>

      </slot>
    </template>
  </BaseLayout>
</template>

<style lang="scss">

.default-bar {
  display: flex;

  justify-content: start;
}
</style>
<script>
import Utils from "../utils"
import EventBus from "../event-bus";
import Table from "./tables/Table.vue";
import Pagination from "./settings/Pagination.vue";
import BaseLayout from "./base/BaseLayout.vue";
import ActionsButton from "./base/ActionsButton";
import {mapGetters, mapState} from "vuex";
import ActionButton from "./base/ActionButton";

import axios from "axios";
import QueryBar from "./settings/QueryBar";
import Search from "@/search/Search";

export default {
  name: "Elepy",
  watch: {
    $route: {
      handler: 'getModelData',
      immediate: true
    },

    pagination: function () {
      return this.getModelData();
    }
    ,
    query: function () {
      return this.getModelData();
    }
  },
  computed: {
    ...mapState(["selectedRows"]),
    ...mapGetters(["canExecute"]),

    allActions() {
      return this.model.actions.filter(this.canExecute);
    },

    multipleActions() {
      return this.allActions.filter(action => action.multipleRecords === true)
    },
    singleOnlyActions() {
      return this.allActions.filter(action => action.multipleRecords === false && action.singleRecord === true)
    },

    noRecordActions() {
      return this.allActions.filter(action => !action.singleRecord && !action.multipleRecords)
    }

  },
  data() {
    return {
      amountOfRecords: 0,
      currentPage: [],
      pagination: "pageSize=25&pageNumber=1",
      query: "",
      isLoading: false
    };
  },

  props: ["model"],
  components: {Search, QueryBar, ActionButton, ActionsButton, Pagination, Table, BaseLayout},
  methods: {
    deleteData() {
      return UIkit.modal
          .confirm(
              this.$t('elepy.ui.prompts.delete', [this.selectedRows.length]),
              {
                labels: {
                  ok: this.$t('elepy.ui.yes'),
                  cancel: this.$t('elepy.ui.cancel'),
                },
                stack: true
              }
          )
          .then(
              () => {
                axios({
                  method: "delete",
                  url:
                      this.model.path +
                      "?ids=" +
                      this.selectedRows.join(",")
                })
                    .then(response => {
                      this.$store.commit("SET_SELECTED_ROWS", []);
                      this.getModelData();
                      Utils.displayResponse(response);
                    })
              },
              function () {
              }
          );
    },
    async getModelData() {
      document.title = this.model.name + ' - Elepy'
      let searchUrl =
          this.model.path +
          "?" +
          this.pagination +
          "&q=" +
          this.query;
      EventBus.$emit("startLoading");

      try {
        this.currentPage = (await axios.get(searchUrl)).data
        this.amountOfRecords = (await axios.get(searchUrl + "&count=true")).data;

      } finally {
        EventBus.$emit("stopLoading");
      }


    }
  },
  mounted() {
    EventBus.$on("updateData", () => {
      this.getModelData();
    });
    EventBus.$on("startLoading", () => {
      if (this.timer) {
        clearTimeout(this.timer);
        this.timer = null;
      }
      this.timer = setTimeout(() => {
        this.isLoading = true;
      }, 400);
    });
    EventBus.$on("stopLoading", () => {
      if (this.timer) {
        clearTimeout(this.timer);
        this.timer = null;
      }
      this.timer = setTimeout(() => {
        this.isLoading = false;
      }, 400);
    });
  }
};
</script>


