<template>
    <div class="uk-modal-dialog">
        <button class="uk-modal-close-default" type="button" uk-close></button>
        <div class="uk-modal-header">
            <h2 class="uk-modal-title">{{action.name}}</h2>
        </div>
        <div class="uk-modal-body">
            <p v-if="action.description!=null">{{action.description}}</p>
            <div class="" v-if="action.inputModel!=null">
                <ObjectField :model="action.inputModel" v-model="record"/>
            </div>
        </div>
        <div class="uk-modal-footer uk-text-right">
            <button class="uk-button uk-button-default uk-modal-close" type="button">Cancel</button>

            <ActionButton :action="triggerAction" class="uk-button-primary uk-width-small">{{action.name}}
            </ActionButton>
        </div>
    </div>
</template>
<script>

    import ObjectField from "../fields/ObjectField"
    import ActionButton from "../base/ActionButton";
    import Utils from "../../utils";
    import EventBus from "../../event-bus";
    import axios from "axios";
    import UIkit from "uikit";

    export default {
        name: 'ActionModal',
        components: {ActionButton, ObjectField},
        props: ["action", "recordIds", "merge", "modalId"],

        data() {
            return {
                record: {}
            }
        },

        methods: {

            triggerAction() {
                if (!this.action.warning) {
                    return this.executeAction();
                } else {
                    UIkit.modal
                        .confirm(
                            "<p><span class='uk-text-danger uk-text-bold'>Warning!</span><br>" + this.action.warning + "</p>",
                            {
                                labels: {
                                    ok: this.action.name,
                                    cancel: "Cancel"
                                },
                                stack: true
                            }
                        ).then(() => this.executeAction(), () => {
                    })
                }

            },
            executeAction() {
                return axios({
                    method: this.action.method,
                    data: this.record,
                    params: {
                        ids: this.recordIds.join(',')
                    },
                    url: this.action.path,

                })
                    .then(response => {


                        UIkit.modal(document.getElementById(this.$parent._uid)).hide();

                        this.record = {};
                        Utils.displayResponse(response);
                        EventBus.$emit("updateData");

                    });
            }
        }

    }
</script>
