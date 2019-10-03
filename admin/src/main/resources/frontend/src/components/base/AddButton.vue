<template>
    <button
            @click="$emit('click', $event)"
            class="uk-button uk-button-primary add-button"
            id="add-button"
    >
        <i uk-icon="icon: plus"></i> Add
    </button>
</template>


