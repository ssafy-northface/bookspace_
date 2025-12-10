<script setup>
import Toaster from "./components/ui/Toaster.vue";
</script>

<template>
  <div class="flex flex-col min-h-screen">
    <RouterView />
    <Toaster />
  </div>
</template>
