<template>
  <div
    :class="['relative flex size-8 shrink-0 overflow-hidden rounded-full', className]"
    data-slot="avatar"
  >
    <img
      v-if="src"
      :src="src"
      :alt="alt"
      data-slot="avatar-image"
      :class="['aspect-square size-full', imageClass]"
      @error="onError"
    />
    <div
      v-else
      data-slot="avatar-fallback"
      :class="['bg-muted flex size-full items-center justify-center rounded-full', fallbackClass]"
    >
      <slot name="fallback">{{ fallback }}</slot>
    </div>
  </div>
</template>

<script>
export default {
  name: "Avatar",
  props: {
    src: String,
    alt: {
      type: String,
      default: "avatar",
    },
    fallback: {
      type: String,
      default: "",
    },
    className: {
      type: String,
      default: "",
    },
    imageClass: {
      type: String,
      default: "",
    },
    fallbackClass: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      hasError: false,
    };
  },
  methods: {
    onError() {
      this.hasError = true;
      this.$emit("error");
    },
  },
  computed: {
    showImage() {
      return this.src && !this.hasError;
    },
  },
};
</script>