<template>
  <!-- Tabs 컨테이너: Flowbite Tabs의 parent element 역할 -->
  <div ref="rootEl" :class="wrapperClasses" data-slot="tabs">
    <!-- Tab list -->
    <div :class="listClasses" role="tablist" data-slot="tabs-list">
      <button
        v-for="tab in tabs"
        :key="tab.id"
        :id="triggerId(String(tab.id))"
        type="button"
        role="tab"
        :disabled="!!tab.disabled"
        :aria-selected="activeId === String(tab.id)"
        :aria-controls="panelId(String(tab.id))"
        :class="baseClasses"
        @click="onClickTab(tab)"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- Panels -->
    <div>
      <div
        v-for="tab in tabs"
        :key="`${tab.id}-panel`"
        :id="panelId(String(tab.id))"
        role="tabpanel"
        :aria-labelledby="triggerId(String(tab.id))"
        :class="[panelClass, activeId === String(tab.id) ? '' : 'hidden'].join(' ')"
      >
        <!-- 각 탭 컨텐츠는 named slot으로 -->
        <slot :name="String(tab.id)" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from "vue";
import { Tabs } from "flowbite";

const props = defineProps({
  modelValue: { type: [String, Number], default: "" },
  tabs: { type: Array, required: true },
  class: { type: String, default: "" },
  listClass: { type: String, default: "" },
  panelClass: { type: String, default: "" },
  activeClasses: { type: String, default: "" },
  inactiveClasses: { type: String, default: "" },
  instanceId: { type: String, default: "tabs-instance" },
});

const emit = defineEmits(["update:modelValue"]);

const rootEl = ref(null);
let tabsInstance = null;

// 버튼 (안쪽 도형) 기본 스타일
const baseClasses =
  "flex-1 inline-flex items-center justify-center " +
  "h-6 rounded-md text-sm font-md" +
  "transition-[background,color,box-shadow] outline-none " +
  "focus-visible:ring-[3px] focus-visible:ring-ring/50 " +
  "disabled:pointer-events-none disabled:opacity-50";

// 전체 너비 + 바깥 배경
const wrapperClasses = computed(() => ["w-full flex flex-col gap-2", props.class].join(" "));

// 회색 배경이 깔린 트랙
const listClasses = computed(() => ["flex w-full items-center rounded-md bg-muted px-[3px] py-[4px]", props.listClass].join(" "));

// 현재 active id
const activeId = computed(() => {
  const v = String(props.modelValue ?? "");
  if (v) return v;
  return props.tabs?.[0]?.id ? String(props.tabs[0].id) : "";
});

function triggerId(tabId) {
  return `${props.instanceId}-${tabId}-tab`;
}
function panelId(tabId) {
  return `${props.instanceId}-${tabId}-panel`;
}

async function initFlowbiteTabs() {
  await nextTick();
  const el = rootEl.value;
  if (!el) return;

  const items = props.tabs.map((t) => {
    const id = String(t.id);
    return {
      id,
      triggerEl: el.querySelector(`#${triggerId(id)}`),
      targetEl: el.querySelector(`#${panelId(id)}`),
    };
  });

  // 기존 인스턴스가 있으면 교체
  tabsInstance = new Tabs(
    el,
    items,
    {
      defaultTabId: activeId.value,
      activeClasses: props.activeClasses,
      inactiveClasses: props.inactiveClasses,
    },
    { id: props.instanceId, override: true } // 문서에 있는 instanceOptions 패턴 :contentReference[oaicite:3]{index=3}
  );

  // 초기 v-model과 동기화
  tabsInstance.show(activeId.value);
}

onMounted(initFlowbiteTabs);

// 탭 목록이 바뀌거나(조건부 렌더링/라우팅) modelValue가 바뀌면 동기화
watch(
  () => props.tabs.map((t) => `${t.id}:${!!t.disabled}`).join("|"),
  () => initFlowbiteTabs()
);

watch(
  () => props.modelValue,
  (v) => {
    const id = String(v ?? "");
    if (tabsInstance && id) tabsInstance.show(id); // :contentReference[oaicite:5]{index=5}
  }
);

onBeforeUnmount(() => {
  tabsInstance = null;
});

// 클릭 시 v-model 업데이트 (Flowbite도 같은 클릭으로 동작)
function onClickTab(tab) {
  if (tab.disabled) return;
  emit("update:modelValue", tab.id);
}
</script>
