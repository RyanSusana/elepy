<template>


  <BaseLayout :back-location="singleMode? null : goBack" @keydown.meta="typeCtrl" @keydown.ctrl="typeCtrl">
    <!-- Navigation -->
    <template #navigation>
      <ActionButton
          v-if="canSave"
          :action="save"
          :actionName="'save'"
          @shortkey="save"
          class="uk-button uk-button-primary  uk-padding uk-padding-remove-vertical uk-margin-small-right"
          v-shortkey.once="['ctrl',  'save']"

      ><i uk-icon="icon: file-edit"></i> {{ $t('elepy.ui.save') }}
      </ActionButton>
      <ActionsButton class="uk-margin-small-right" :actions="model.actions"
                     :ids="[id]"
                     v-if="!isCreating && actions.length >0"></ActionsButton>
      <a action="reset"
         @click="resetToLastSaved"
         class="uk-button uk-button-default uk-margin-small-right"
         v-if="itemIsLoaded && !isCreating && canSave"
      >{{ $t('elepy.ui.reset') }}</a>


      <a action="clear"
         @click="clear"
         class="uk-button uk-button-danger uk-margin-small-right"
         v-if="itemIsLoaded && canSave"
      >{{ $t('elepy.ui.clear') }}</a>

      <a action="clear"

         uk-toggle="target: #revisions"
         class="uk-button uk-button uk-margin-small-right"
         v-if="false && itemIsLoaded && canSave"
      >{{ $t('elepy.ui.revisions') }}</a>

    </template>

    <!-- TableView -->
    <template #main>
      <div :class="{'unclickable': !canSave}"
           class="uk-container uk-container-small uk-margin-large-top uk-margin-large-bottom"
           tabindex="0" @keydown.meta="typeCtrl"
           @keydown.ctrl="typeCtrl" v-if="itemIsLoaded">
        <h1>{{ model.name }}</h1>
        <ObjectField :model="model" v-model="item" :violations="violations" @input="navigationGuard"/>
      </div>
      <RevisionHistory
          :model="model"
      ></RevisionHistory>
    </template>
  </BaseLayout>

</template>


<style lang="scss" scoped>
.unclickable {

  pointer-events: none;


}

.uk-container {
  outline: none;
}

.uk-button.uk-disabled {
  background-color: lightgray;
}

.uk-button-default {
  background-color: #e8e8e8;
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

  height: 100vh;
}
</style>


<script>

import ObjectField from "./fields/ObjectField";
import Utils from "../utils";
import Vue from "vue";
import BaseLayout from "./base/BaseLayout.vue";

import ActionsButton from "./base/ActionsButton";

import isEqual from "lodash/isEqual"
import ActionButton from "./base/ActionButton";
import {mapGetters} from "vuex";
import RevisionHistory from "@/components/modals/RevisionHistory";

const UIkit = require("uikit");
const axios = require("axios/index");

Vue.use(require('vue-shortkey'));
export default {
  name: "ElepySingle",
  data() {
    return {
      item: {},
      itemCopy: null,
      isLoading: false,
      isSaved: false,
      violations: []
    };
  },

  props: ["model", "recordId", "adding", "singleMode"],
  components: {RevisionHistory, ActionButton, ObjectField, ActionsButton, BaseLayout},

  computed: {
    ...mapGetters(['canExecute']),

    canSave() {
      if (this.isCreating) {
        return this.canExecute(this.model.defaultActions.create)
      } else {
        return this.canExecute(this.model.defaultActions.update)
      }
    },
    actions() {
      if (this.singleMode) {
        return this.model.actions.filter(this.canExecute);
      } else {
        return this.model.actions.filter(action => this.canExecute(action) && (action.singleRecord || action.multipleRecords))
      }
    },
    //Return if it should be a PUT or POST
    isCreating() {
      if (this.recordId != null) {
        return false;
      }
      return this.id == null || this.adding === true;
    },
    itemIsLoaded() {
      return this.item != null;
    },
    id() {
      return this.recordId ?? this.item[this.model.idProperty];
    },

    title() {
      if (this.singleMode) {
        return this.model.name;
      }
      if (this.isCreating) {
        return this.$t('elepy.ui.actions.addTo', [this.model.name]);
      } else {
        return this.model.name + "/" + this.id;
      }
    }
  },

  watch: {
    'item': {
      immediate: true,
      deep: true,
      handler: function () {

      }
    },
    'title': {
      immediate: true,
      handler: function () {
        document.title = this.title + " - Elepy"
      }
    }
  },
  methods: {
    navigationGuard() {
      if (isEqual(this.item, this.itemCopy)) {
        this.$store.commit('CLEAR_NAVIGATION_WARNING')
      } else {
        this.$store.commit('SET_NAVIGATION_WARNING', this.$t('elepy.ui.prompts.back'))
      }
    },
    goBack() {
      this.$router.push(this.model.path)
    },
    clear() {
      const id = this.id;
      this.item = {};
      this.navigationGuard();
      this.item[this.model.idProperty] = id;

    },
    async resetToLastSaved() {
      await this.getRecord();
      this.navigationGuard();
    },
    save() {
      return UIkit.modal
          .confirm(this.$t('elepy.ui.prompts.save'), {
            labels: {
              ok: this.$t('elepy.ui.yes'),
              cancel: this.$t('elepy.ui.no'),
            }
          })
          .then(
              () => {
                return axios({
                  method: this.isCreating ? "POST" : "PUT",
                  data: this.item,
                  url:
                      Utils.url +
                      this.model.path +
                      (this.isCreating ? "" : "/" + this.item[this.model.idProperty])
                })
                    .then(response => {
                      Utils.displayResponse(response);
                      if (this.isCreating) {
                        let createdRecord = response.data.properties.createdRecords[0];
                        let createdRecordId = createdRecord[this.model.idProperty];
                        if (this.singleMode) {
                          return this.getRecord();
                        } else {
                          return this.$router.push(this.model.path + "/edit/" + createdRecordId);
                        }
                      } else {
                        return this.getRecord();
                      }
                    }).catch(error => {
                      this.violations = error.response.data.properties.violations
                      Utils.displayError(error);
                    });
              },
              () => {
              }
          );
    },

    typeCtrl(e) {
      if (e.key === 's') {

        e.preventDefault();
        if (this.canSave) {
          this.save();
        }
      }
    },
    getRecord() {
      if (this.singleMode) {
        return axios
            .get(this.model.path + "?pageSize=1&pageNumber=1")
            .then(response => {
              this.item = response.data[0] || {};
              this.itemCopy = JSON.parse(JSON.stringify(this.item));
              this.navigationGuard();
            })
      }


      if (this.isCreating) {
        this.item = {};
        this.itemCopy = {};
        this.navigationGuard();
      } else if (this.recordId != null) {
        return axios
            .get(this.model.path + "/" + this.recordId)
            .then(response => {
              this.item = response.data;
              this.itemCopy = JSON.parse(JSON.stringify(this.item));
              this.navigationGuard()
            })
            .catch(error => {
              this.$router.push(this.model.path);

            });
      }

    }
  },
  mounted() {
    this.getRecord();


  }
};

String.prototype.isEmpty = function () {
  return this.length === 0 || !this.trim();
};
</script>


