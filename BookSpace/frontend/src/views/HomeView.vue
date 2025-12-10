<script setup>
import { ref } from "vue";
import Switch from "@/components/ui/Switch.vue";
import Toggle from "@/components/ui/Toggle.vue";
import ToggleGroup from "@/components/ui/ToggleGroup.vue";
import ToggleGroupItem from "@/components/ui/ToggleGroupItem.vue";
import Tabs from "@/components/ui/tabs.vue";
import Textarea from "@/components/ui/Textarea.vue";
import Toast from "@/components/ui/Toast.vue";
import Toaster from "@/components/ui/Toaster.vue";
import Slider from "@/components/ui/Slider.vue";
import Select from "@/components/ui/Select.vue";
import Separator from "@/components/ui/Separator.vue";
import Sheet from "@/components/ui/Sheet.vue";
import Sidebar from "@/components/ui/Sidebar.vue";
import ScrollArea from "@/components/ui/ScrollArea.vue";
import Sonner from "@/components/ui/Sonner.vue";
import Spinner from "@/components/ui/Spinner.vue";
import Skeleton from "@/components/ui/Skeleton.vue";
import { BookOpenIcon, UserIcon } from "@heroicons/vue/24/outline";

import { useToast } from "@/composables/useToast";

const toggle = ref(false);
const toggleSolo = ref(false);
const toggleGroupValue = ref("left");
const sliderValue = ref(30);
const selectValue = ref("option1");
const sheetOpen = ref(false);
const sidebarOpen = ref(true);
const textareaValue = ref("기본 텍스트");
const tabValue = ref("profile");

const options = [
  { value: "option1", label: "Option 1" },
  { value: "option2", label: "Option 2" },
  { value: "option3", label: "Option 3" },
];

const tabs = [
  { name: "profile", title: "Profile" },
  { name: "dashboard", title: "Dashboard" },
  { name: "settings", title: "Settings" },
];

const sidebarItems = [
  { name: "books", label: "도서 목록", icon: BookOpenIcon, link: "/books" },
  { name: "profile", label: "프로필", icon: UserIcon, link: "/profile" },
  { name: "settings", label: "설정", link: "/settings" },
];

const toasts = ref([
  {
    id: 1,
    title: "완료",
    description: "설정이 저장되었습니다.",
    type: "success",
  },
  {
    id: 2,
    title: "알림",
    description: "새로운 알림이 있습니다.",
    type: "info",
  },
]);

const scrollItems = Array.from({ length: 20 }, (_, i) => `아이템 ${i + 1}`);

function closeToast(id) {
  toasts.value = toasts.value.filter((toast) => toast.id !== id);
}

const { toast } = useToast();
function addToast() {
  toast({
    title: "새 토스트",
    description: "Toaster composable로 추가됨",
  });
}
</script>

<template>
  <div class="container py-10 space-y-10">
    <h2 class="text-2xl font-semibold">
      UI 전체 데모 (Vue + Tailwind + Flowbite)
    </h2>
    <p class="text-muted-foreground">
      components/ui의 모든 컴포넌트를 더미 데이터로 렌더링합니다.
    </p>

    <section class="grid gap-6 md:grid-cols-2">
      <div class="space-y-4 rounded-lg border p-4">
        <h3 class="font-semibold">Switch</h3>
        <Switch
          v-model="toggle"
          label="토글"
          trackClass="shadow-xs"
          thumbClass="bg-background dark:bg-primary-foreground"
          focusClass="focus-visible:ring-ring/50 focus-visible:ring-[3px]"
        />
        <p class="text-sm text-muted-foreground">상태: {{ toggle }}</p>
      </div>

      <div class="space-y-4 rounded-lg border p-4">
        <h3 class="font-semibold">Toggle & ToggleGroup</h3>
        <Toggle v-model="toggleSolo" variant="outline">단일 토글</Toggle>
        <p class="text-sm text-muted-foreground">상태: {{ toggleSolo }}</p>

        <div class="space-y-2">
          <p class="text-sm font-medium">Toggle Group</p>
          <ToggleGroup
            v-model="toggleGroupValue"
            type="single"
            variant="outline"
          >
            <ToggleGroupItem value="left">Left</ToggleGroupItem>
            <ToggleGroupItem value="center">Center</ToggleGroupItem>
            <ToggleGroupItem value="right">Right</ToggleGroupItem>
          </ToggleGroup>
          <p class="text-sm text-muted-foreground">
            그룹 값: {{ toggleGroupValue }}
          </p>
        </div>
      </div>
    </section>

    <section class="grid gap-6 md:grid-cols-2">
      <div class="space-y-4 rounded-lg border p-4">
        <h3 class="font-semibold">Slider</h3>
        <Slider v-model="sliderValue" :min="0" :max="100" :step="5" />
        <p class="text-sm text-muted-foreground">
          값: <span class="font-medium">{{ sliderValue }}</span>
        </p>
      </div>

      <div class="space-y-4 rounded-lg border p-4">
        <h3 class="font-semibold">Select & Textarea</h3>
        <Select
          v-model="selectValue"
          :options="options"
          label="옵션 선택"
          optionLabel="name"
          optionValue="value"
        />
        <Textarea v-model="textareaValue" placeholder="내용 입력" />
        <p class="text-sm text-muted-foreground">선택: {{ selectValue }}</p>
      </div>
    </section>

    <section class="grid gap-6 md:grid-cols-2">
      <div class="space-y-4 rounded-lg border p-4">
        <h3 class="font-semibold">Tabs</h3>
        <Tabs v-model="tabValue" :tabs="tabs" variant="underline">
          <template #profile>프로필 콘텐츠</template>
          <template #dashboard>대시보드 콘텐츠</template>
          <template #settings>설정 콘텐츠</template>
        </Tabs>
        <p class="text-sm text-muted-foreground">현재 탭: {{ tabValue }}</p>
      </div>

      <div class="space-y-4 rounded-lg border p-4">
        <h3 class="font-semibold">Spinner & Skeleton</h3>
        <div class="flex items-center gap-4">
          <Spinner color="purple" />
          <Spinner size="lg" color="info" />
        </div>
        <Skeleton class="h-4 w-1/2" />
        <Skeleton class="h-6 w-full" />
      </div>
    </section>

    <section class="grid gap-6 md:grid-cols-2">
      <div class="space-y-4 rounded-lg border p-4">
        <h3 class="font-semibold">ScrollArea</h3>
        <ScrollArea class="h-40 border">
          <ul class="space-y-2 p-3 text-sm">
            <li
              v-for="item in scrollItems"
              :key="item"
              class="rounded bg-muted px-3 py-2"
            >
              {{ item }}
            </li>
          </ul>
        </ScrollArea>
      </div>

      <div class="space-y-4 rounded-lg border p-4">
        <h3 class="font-semibold">Sheet (Drawer)</h3>
        <button
          type="button"
          class="rounded-md border px-3 py-2 text-sm"
          @click="sheetOpen = true"
        >
          열기
        </button>
        <Sheet
          v-model="sheetOpen"
          title="시트 제목"
          description="테스트 패널입니다."
        >
          <p class="text-sm text-muted-foreground">
            드로어에서 내용을 확인하세요. 라우트/폼 등을 배치할 수 있습니다.
          </p>
          <template #footer>
            <button
              type="button"
              class="rounded-md border px-3 py-2 text-sm"
              @click="sheetOpen = false"
            >
              닫기
            </button>
          </template>
        </Sheet>
      </div>
    </section>

    <section class="grid gap-6 lg:grid-cols-3">
      <div class="lg:col-span-1 space-y-4 rounded-lg border p-4">
        <h3 class="font-semibold">Sidebar</h3>
        <Sidebar
          v-model="sidebarOpen"
          :items="sidebarItems"
          :brand="{ name: 'Demo Sidebar' }"
        />
      </div>

      <div class="lg:col-span-2 space-y-4 rounded-lg border p-4">
        <h3 class="font-semibold">Sonner (Toast 리스트)</h3>
        <div class="flex gap-2">
          <button
            type="button"
            class="rounded-md border px-3 py-2 text-sm"
            @click="
              toasts.push({
                id: Date.now(),
                title: '새 토스트',
                description: '방금 추가됨',
                type: 'success',
              })
            "
          >
            토스트 추가
          </button>
        </div>
        <p class="text-sm text-muted-foreground">
          Flowbite Toast로 Sonner 대체 구현
        </p>
        <Sonner :items="toasts" position="bottom-right" @close="closeToast" />
      </div>
    </section>

    <section class="space-y-4 rounded-lg border p-4">
      <h3 class="font-semibold">Toast & Toaster</h3>
      <div class="flex flex-wrap items-center gap-4">
        <Toast title="Standalone Toast" description="컴포넌트 직접 사용 예시" />
        <button
          type="button"
          class="rounded-md border px-3 py-2 text-sm"
          @click="addToast"
        >
          Toaster에 토스트 추가
        </button>
      </div>
      <Toaster />
    </section>

    <Separator />
  </div>
</template>
