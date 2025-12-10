<template>
  <div class="relative">
    <!-- 모바일 메뉴 버튼 -->
    <button
      type="button"
      class="lg:hidden mb-2 inline-flex items-center gap-2 rounded-md border px-3 py-2 text-sm"
      @click="open = !open"
    >
      <span class="text-lg">☰</span>
      <span>메뉴</span>
    </button>

    <!-- 모바일 오버레이 -->
    <div
      v-if="open"
      class="fixed inset-0 z-30 bg-black/30 lg:hidden"
      @click="open = false"
    />

    <!-- 사이드바 -->
    <FwbSidebar
      v-if="open"
      data-slot="sidebar"
      :class="[
        'bg-sidebar text-sidebar-foreground fixed inset-y-0 left-0 z-40 w-64 shadow-lg transition-transform lg:relative lg:translate-x-0',
        props.class,
      ]"
    >
      <div class="p-4">
        <FwbSidebarLogo :name="brand.name" :img="brand.img" class="mb-4" />

        <FwbSidebarItemGroup>
          <FwbSidebarItem
            v-for="item in items"
            :key="item.name || item.label"
            :link="item.link || '#'"
            :tag="item.tag || 'router-link'"
            @click="onSelect(item)"
          >
            <template #default>
              {{ item.label || item.name }}
            </template>

            <template v-if="item.icon" #icon>
              <component :is="item.icon" class="size-4" />
            </template>

            <template v-if="item.suffix" #suffix>
              <component :is="item.suffix" />
            </template>
          </FwbSidebarItem>
        </FwbSidebarItemGroup>
      </div>
    </FwbSidebar>
  </div>
</template>

<script setup>
import { computed } from "vue";
import {
  FwbSidebar,
  FwbSidebarItem,
  FwbSidebarItemGroup,
  FwbSidebarLogo,
} from "flowbite-vue";

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: true,
  },
  items: {
    type: Array,
    default: () => [],
  },
  brand: {
    type: Object,
    default: () => ({ name: "Sidebar" }),
  },
  class: {
    type: String,
    default: "",
  },
});

const emit = defineEmits(["update:modelValue", "select"]);

const open = computed({
  get() {
    return props.modelValue;
  },
  set(val) {
    emit("update:modelValue", val);
  },
});

function onSelect(item) {
  emit("select", item);
}
</script>
