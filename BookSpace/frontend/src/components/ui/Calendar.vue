<template>
  <div
    class="bg-card text-card-foreground border border-border p-4 rounded-md shadow-sm"
  >
    <!-- 상단 네비게이션 -->
    <div class="flex items-center justify-between mb-4">
      <button
        @click="onPrevious"
        class="inline-flex items-center rounded-md border border-border bg-muted px-3 py-1.5 text-sm text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
      >
        이전
      </button>

      <span class="font-medium">
        {{ currentMonth }}
      </span>

      <button
        @click="onNext"
        class="inline-flex items-center rounded-md border border-border bg-muted px-3 py-1.5 text-sm text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
      >
        다음
      </button>
    </div>

    <!-- 달력 -->
    <table class="w-full border-collapse text-sm">
      <thead>
        <tr>
          <th
            v-for="weekday in weekdays"
            :key="weekday"
            class="py-1 text-xs font-medium text-muted-foreground"
          >
            {{ weekday }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(week, weekIndex) in calendar" :key="weekIndex">
          <td
            v-for="(day, dayIndex) in week"
            :key="day.date ? day.date : `empty-${dayIndex}`"
            class="h-9 p-0 text-center align-middle"
          >
            <button
              v-if="day.date"
              type="button"
              class="flex h-9 w-9 items-center justify-center rounded-md text-xs transition-colors"
              :class="dayClasses(day)"
              @click="selectDay(day)"
            >
              {{ day.date.getDate() }}
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";

const today = new Date();
const weekdays = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];

const currentMonthDate = ref(
  new Date(today.getFullYear(), today.getMonth(), 1)
);
const selectedDate = ref(null);

const calendar = computed(() => {
  const startOfMonth = new Date(
    currentMonthDate.value.getFullYear(),
    currentMonthDate.value.getMonth(),
    1
  );
  const endOfMonth = new Date(
    currentMonthDate.value.getFullYear(),
    currentMonthDate.value.getMonth() + 1,
    0
  );

  const startDay = startOfMonth.getDay();
  const daysInMonth = endOfMonth.getDate();

  const weeks = [];
  let week = Array(startDay).fill({ date: null, isOutside: true });

  for (let day = 1; day <= daysInMonth; day++) {
    const date = new Date(
      currentMonthDate.value.getFullYear(),
      currentMonthDate.value.getMonth(),
      day
    );
    week.push({
      date,
      isToday: date.toDateString() === today.toDateString(),
      isSelected: selectedDate.value?.toDateString() === date.toDateString(),
      isOutside: false,
    });

    if (week.length === 7) {
      weeks.push(week);
      week = [];
    }
  }

  if (week.length > 0) {
    while (week.length < 7) {
      week.push({ date: null, isOutside: true });
    }
    weeks.push(week);
  }

  return weeks;
});

const currentMonth = computed(() =>
  currentMonthDate.value.toLocaleString("default", {
    month: "long",
    year: "numeric",
  })
);

const onPrevious = () => {
  currentMonthDate.value = new Date(
    currentMonthDate.value.getFullYear(),
    currentMonthDate.value.getMonth() - 1,
    1
  );
};

const onNext = () => {
  currentMonthDate.value = new Date(
    currentMonthDate.value.getFullYear(),
    currentMonthDate.value.getMonth() + 1,
    1
  );
};

const selectDay = (day) => {
  if (!day.isOutside && day.date) {
    selectedDate.value = day.date;
  }
};

// 날짜 셀 색상/상태 클래스
const dayClasses = (day) => {
  if (day.isOutside || !day.date) {
    return "text-muted-foreground opacity-40 cursor-default";
  }

  const isToday = day.isToday;
  const isSelected =
    selectedDate.value?.toDateString() === day.date.toDateString();

  if (isSelected) {
    // 선택된 날짜
    return "bg-primary text-primary-foreground font-semibold";
  }

  if (isToday) {
    // 오늘 날짜 (선택 안 됐을 때)
    return "bg-accent text-accent-foreground font-semibold";
  }

  // 일반 날짜
  return "hover:bg-muted hover:text-foreground";
};
</script>
