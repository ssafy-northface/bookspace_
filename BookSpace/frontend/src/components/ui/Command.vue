<template>
  <!-- 오버레이: modelValue가 true일 때만 표시 -->
  <div
    v-if="modelValue"
    class="fixed inset-0 z-50 flex items-center justify-center"
  >
    <!-- 배경 -->
    <div
      class="fixed inset-0 bg-background/80 backdrop-blur-sm"
      @click="close"
    />

    <!-- 팝업 박스 -->
    <div
      class="relative z-10 w-full max-w-2xl rounded-2xl border border-border bg-popover text-popover-foreground shadow-2xl"
    >
      <!-- 스크린 리더용 헤더 -->
      <header class="sr-only">
        <h2>{{ title }}</h2>
        <p>{{ description }}</p>
      </header>

      <!-- 닫기 버튼 -->
      <button
        v-if="showCloseButton"
        type="button"
        class="absolute right-5 top-5 rounded-full p-1.5 text-muted-foreground hover:bg-muted hover:text-foreground transition-colors"
        @click="close"
      >
        ✕
      </button>

      <!-- 안쪽 패딩 통째로 부여 -->
      <div
        data-slot="command"
        class="flex max-h-[480px] w-full flex-col overflow-hidden rounded-2xl px-4 pt-4 pb-3"
      >
        <!-- 입력 영역 -->
        <div
          data-slot="command-input-wrapper"
          class="mb-2 flex h-12 items-center gap-3 rounded-lg border border-border bg-card px-4"
        >
          <SearchIcon class="w-5 h-5 shrink-0 opacity-60" />
          <input
            data-slot="command-input"
            type="text"
            v-model="query"
            :placeholder="placeholder"
            class="placeholder:text-muted-foreground flex h-10 w-full rounded-md bg-transparent text-sm outline-none disabled:cursor-not-allowed disabled:opacity-50"
          />
        </div>

        <!-- 리스트 영역 -->
        <div
          data-slot="command-list"
          class="mt-2 max-h-[360px] overflow-y-auto pb-1"
        >
          <!-- 결과 없음 -->
          <div
            v-if="filteredGroups.length === 0"
            data-slot="command-empty"
            class="py-8 text-center text-sm text-muted-foreground"
          >
            {{ emptyMessage }}
          </div>

          <!-- 그룹 + 아이템들 -->
          <div
            v-else
            v-for="(group, groupIndex) in filteredGroups"
            :key="group.name || `group-${groupIndex}`"
            data-slot="command-group"
            class="text-foreground"
          >
            <!-- 그룹 헤더 -->
            <div
              v-if="group.name"
              data-slot="command-group-heading"
              class="text-muted-foreground px-2 pt-3 pb-1 text-[11px] font-medium"
            >
              {{ group.name }}
            </div>

            <!-- 아이템 리스트 -->
            <div class="space-y-1">
            <button
                v-for="item in group.items"
                :key="item.id || item.label"
                type="button"
                data-slot="command-item"
                class="relative flex w-full cursor-pointer select-none items-center gap-3 rounded-md px-4 py-2.5 text-left text-sm text-foreground hover:bg-accent hover:text-accent-foreground"
                @click="onSelect(item)"
            >
                <!-- 아이콘 (옵션) -->
                <span v-if="item.icon" class="shrink-0">
                <component :is="item.icon" class="w-4 h-4" />
                </span>

                <!-- 라벨 -->
                <span class="flex-1 truncate">
                {{ item.label }}
                </span>

                <!-- 오른쪽 shortcut -->
                <span
                v-if="item.shortcut"
                data-slot="command-shortcut"
                class="ml-auto pl-4 pr-1 text-[11px] tracking-widest text-muted-foreground min-w-[48px] text-right"
                >
                {{ item.shortcut }}
                </span>
            </button>
            </div>


            <!-- 그룹 간 구분선 -->
            <div
              v-if="showSeparators && groupIndex !== filteredGroups.length - 1"
              data-slot="command-separator"
              class="bg-border mx-2 my-3 h-px"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { SearchIcon } from "lucide-vue-next";

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: "Command Palette",
  },
  description: {
    type: String,
    default: "Search for a command to run...",
  },
  placeholder: {
    type: String,
    default: "명령어를 검색하세요...",
  },
  emptyMessage: {
    type: String,
    default: "검색 결과가 없습니다.",
  },
  showCloseButton: {
    type: Boolean,
    default: true,
  },
  showSeparators: {
    type: Boolean,
    default: true,
  },
  /**
   * items: [
   *   { id?, label, value?, group?: '페이지 이동', shortcut?: 'Ctrl+K', icon?: 컴포넌트, disabled?: false }
   * ]
   */
  items: {
    type: Array,
    default: () => [],
  },
});

const emit = defineEmits(["update:modelValue", "select"]);

const query = ref("");

// 열릴 때마다 검색어 초기화
watch(
  () => props.modelValue,
  (val) => {
    if (val) query.value = "";
  }
);

const close = () => {
  emit("update:modelValue", false);
};

const filteredGroups = computed(() => {
  const q = query.value.trim().toLowerCase();

  const groupMap = new Map();

  props.items.forEach((item) => {
    if (q && !item.label.toLowerCase().includes(q)) return;

    const groupName = item.group || "";
    if (!groupMap.has(groupName)) {
      groupMap.set(groupName, []);
    }
    groupMap.get(groupName).push(item);
  });

  return Array.from(groupMap.entries()).map(([name, items]) => ({
    name,
    items,
  }));
});

const onSelect = (item) => {
  if (item.disabled) return;
  emit("select", item);
  close();
};
</script>
