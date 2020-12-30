<template>
    <tr :id="'row-'+data[model.idProperty]" :row="data[model.idProperty]">
        <td class="uk-background-muted uk-text-center">
            <input :checked="selected" class="uk-checkbox" type="checkbox" v-on:change="selectChange()">
        </td>
        <TableColumnData
                :field="field"
                :value="data[field.name]"
                @click="$emit('rowClicked')"
                class="data-column"
                v-bind:key="field.name"
                v-for="field in fields"
        />
        <td class="data-column">
            <div class="uk-flex uk-flex-none">
                <router-link
                        :to="this.model.path+'/edit/'+this.data[this.model.idProperty]"
                        action="edit"
                        class="uk-icon-button uk-margin-small-right"
                        :uk-icon="canExecute(this.model.defaultActions.update) ? 'pencil' : 'info'"
                        v-if="updateEnabled && canExecute(this.model.defaultActions.find) "

                ></router-link>
                <ActionsButton :actions="this.actions" v-if="this.actions.length >0" :ids="[id]"></ActionsButton>

                <a
                        v-if="canExecute(this.model.defaultActions.delete)"
                        @click="deleteData()"
                        action="delete"
                        class="uk-icon-button uk-button-danger uk-color-light uk-margin-small-left"
                        uk-icon="trash"
                ></a>
            </div>
        </td>
    </tr>
</template>
<style lang="scss">
    .data-column {
        max-width: 25vw;
    }

</style>

<script>
    import TableColumnData from "./TableColumnData.vue";
    import UIkit from "uikit";
    import Utils from "../../utils";
    import EventBus from "../../event-bus.js";
    import ActionsButton from "../base/ActionsButton"
    import {mapGetters} from "vuex";

    const axios = require("axios/index");
    export default {
        props: [
            "id-field",
            "fields",
            "data",
            "selected",
            "actions",
            "model",
            "updateEnabled"
        ],
        components: {TableColumnData, ActionsButton},

        computed: {
            ...mapGetters(["canExecute"]),
            id() {
                return this.data[this.model.idProperty];
            },

        },
        methods: {
            selectChange() {
                this.$emit("tableRowSelected", this.data[this.model.idProperty]);
            },
            deleteData() {
                UIkit.modal
                    .confirm(this.$t('elepy.ui.prompts.delete', [1]), {
                        labels: {
                            ok: this.$t('elepy.ui.yes'),
                            cancel: this.$t('elepy.ui.cancel'),
                        },
                        stack: true
                    })
                    .then(
                        () => {
                            axios({
                                method: "delete",
                                url:
                                    Utils.url +
                                    this.model.path +
                                    "/" +
                                    this.id
                            }).then(response => {
                                EventBus.$emit("updateData");
                                Utils.displayResponse(response);
                            })

                        },

                        function () {

                        }
                    );
            }
        }
    };
</script>
