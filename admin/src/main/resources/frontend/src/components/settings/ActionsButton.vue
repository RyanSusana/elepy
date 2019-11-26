<template>
    <div class="uk-button-group">
        <button
                @click="executeAction(selectedAction)"
                class="uk-button uk-button-primary action-button"
                :class="{multiple: filteredActions.length>1}"
                :action="selectedAction.name"
        >{{selectedAction.name}}
        </button>
        <div class="uk-inline">
            <button v-if="filteredActions.length>0" action="select" class="uk-button uk-button-primary action-select"
                    type="button">
                <span uk-icon="icon:  triangle-down"></span>
            </button>
            <div uk-dropdown="mode: click; boundary: ! .uk-button-group; pos: bottom-right;">
                <div class="action-list">
                    <div
                            :action="action.name"
                            :key="action.name"
                            class="action-item uk-text-center"
                            v-for="action in filteredActions"
                            v-on:click="selectAction(action)"
                    >{{action.name}}
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import Utils from "../../utils";
    import EventBus from "../../event-bus";

    const axios = require("axios/index");
    export default {
        name: "ActionsButton",
        props: ["actions", "ids"],
        computed: {
            filteredActions() {
                return this.actions.filter(action => action.name !== this.selectedAction.name)
            }
        },
        data() {
            return {
                selectedAction: this.actions[0] || {}
            }
        },
        methods: {
            selectAction(action) {
                this.selectedAction = action;
                this.executeAction(action);
            },
            executeAction(selectedAction) {
                axios({
                    method: selectedAction.method,
                    url:
                        Utils.url +
                        selectedAction.slug +
                        "?ids=" + this.ids.join(',')
                })
                    .then(response => {
                        EventBus.$emit("updateData");
                        Utils.displayResponse(response);
                    })
                    .catch(function (error) {
                        Utils.displayError(error);
                    });
            },
        }
    }
</script>

<style lang="scss" scoped>
    .action-select{
        font-size: 1.2rem;
        min-width: 20px;
    }

    .action-button {
        min-width: 150px;
        font-size: 0.85em !important;
        text-transform: none !important;
    }

    .action-list {
        .action-item {
            padding: 10px;
            cursor: pointer;
            box-sizing: border-box;

            &:nth-child(2n) {
                background: #f2f2f2;
            }

            &:hover {
                box-shadow: inset 0 0 2px #39f;
            }
        }
    }

    .uk-button-group {
        .uk-dropdown {
            padding: 0 !important;
        }
    }

    .action-select {
        padding: 0 !important;
        border-top-left-radius: 0 !important;
        border-bottom-left-radius: 0 !important;
        margin-left: 1px !important;
    }

    .action-button.multiple {
        border-top-right-radius: 0 !important;
        border-bottom-right-radius: 0 !important;
    }


</style>