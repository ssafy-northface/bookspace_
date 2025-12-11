
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
  <div class="container py-10 space-y-10">홈 view</div>
</template>