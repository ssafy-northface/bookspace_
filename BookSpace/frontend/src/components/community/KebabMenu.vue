<template>
  <div class="relative">
    <button
      type="button"
      class="flex items-center justify-center w-8 h-8 rounded-full hover:bg-muted"
      @click="toggle"
    >
      <MoreVertical class="w-4 h-4 text-muted-foreground" />
    </button>

    <div
      v-if="open"
      class="absolute right-0 z-10 w-20 p-2 mt-1 border rounded-md shadow-md bg-card border-border"
    >
      <button class="w-full py-2 text-sm hover:bg-muted" @click="onEdit">
        수정하기
      </button>
      <button
        class="w-full py-2 text-sm text-destructive hover:bg-muted"
        @click="onDelete"
      >
        삭제하기
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { MoreVertical } from "lucide-vue-next";

const emit = defineEmits(["edit", "delete"]);

const open = ref(false);

const toggle = () => {
  open.value = !open.value;
};

const close = () => {
  open.value = false;
};

const onEdit = () => {
  emit("edit");
  close();
};

const onDelete = () => {
  emit("delete");
  close();
};
</script>
