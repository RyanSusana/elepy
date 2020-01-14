<template>
    <div uk-lightbox>
        <a :data-caption="caption" :href="src+'?height=250'" class="">
            <img :alt="alt" :data-src="src" :height="height" :width="width" uk-img>
        </a>
    </div>
</template>

<script>
    export default {
        props: ["height", "width", "src", "alt", "caption"]
    };
</script>
