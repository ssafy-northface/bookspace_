<template>
  <div
    class="relative flex aspect-video justify-center text-xs bg-card text-card-foreground rounded-md border border-border p-4"
    :data-chart="chartId"
    data-slot="chart"
  >
    <canvas ref="chartRef"></canvas>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from "vue";
import Chart from "chart.js/auto";

const props = defineProps({
  id: {
    type: String,
    default: null,
  },
  config: {
    type: Object,
    default: () => ({}), // type 정도만 올 수 있게
  },
  data: {
    type: Object,
    required: true,
  },
  options: {
    type: Object,
    default: () => ({}),
  },
});

const chartRef = ref(null);
const chartInstance = ref(null);
const chartId = `chart-${props.id || Math.random().toString(36).substring(2)}`;

// CSS 변수에서 테마 색 가져오기
function getThemeColors() {
  const style = getComputedStyle(document.documentElement);
  const chartColors = [
    style.getPropertyValue("--chart-1").trim(),
    style.getPropertyValue("--chart-2").trim(),
    style.getPropertyValue("--chart-3").trim(),
    style.getPropertyValue("--chart-4").trim(),
    style.getPropertyValue("--chart-5").trim(),
  ].filter(Boolean);

  return {
    chartColors,
    text: style.getPropertyValue("--foreground").trim(),
    muted: style.getPropertyValue("--muted-foreground").trim(),
    border: style.getPropertyValue("--border").trim(),
    background: style.getPropertyValue("--background").trim(),
  };
}

// Chart 옵션 생성 (테마 적용)
function buildChartConfig() {
  const theme = getThemeColors();

  // 데이터셋에 색 자동 채우기 (사용자가 이미 지정했으면 그대로 사용)
  const datasets =
    props.data?.datasets?.map((ds, i) => {
      const color = theme.chartColors[i % theme.chartColors.length] || "#4b9cff";
      return {
        ...ds,
        backgroundColor: ds.backgroundColor ?? color,
        borderColor: ds.borderColor ?? color,
      };
    }) ?? [];

  const data = {
    ...props.data,
    datasets,
  };

  const baseOptions = {
    plugins: {
      legend: {
        labels: {
          color: theme.text,
        },
      },
      tooltip: {
        backgroundColor: theme.background,
        borderColor: theme.border,
        borderWidth: 1,
        titleColor: theme.text,
        bodyColor: theme.muted,
      },
    },
    scales: {
      x: {
        ticks: {
          color: theme.muted,
        },
        grid: {
          color: theme.border,
        },
      },
      y: {
        ticks: {
          color: theme.muted,
        },
        grid: {
          color: theme.border,
        },
      },
    },
  };

  // 얕은 병합 + scale/legend 부분은 한 번 더 합치기
  const mergedOptions = {
    ...baseOptions,
    ...props.options,
    plugins: {
      ...baseOptions.plugins,
      ...(props.options.plugins || {}),
      legend: {
        ...baseOptions.plugins.legend,
        ...(props.options.plugins?.legend || {}),
        labels: {
          ...baseOptions.plugins.legend.labels,
          ...(props.options.plugins?.legend?.labels || {}),
        },
      },
      tooltip: {
        ...baseOptions.plugins.tooltip,
        ...(props.options.plugins?.tooltip || {}),
      },
    },
    scales: {
      ...baseOptions.scales,
      ...(props.options.scales || {}),
      x: {
        ...baseOptions.scales.x,
        ...(props.options.scales?.x || {}),
        ticks: {
          ...baseOptions.scales.x.ticks,
          ...(props.options.scales?.x?.ticks || {}),
        },
        grid: {
          ...baseOptions.scales.x.grid,
          ...(props.options.scales?.x?.grid || {}),
        },
      },
      y: {
        ...baseOptions.scales.y,
        ...(props.options.scales?.y || {}),
        ticks: {
          ...baseOptions.scales.y.ticks,
          ...(props.options.scales?.y?.ticks || {}),
        },
        grid: {
          ...baseOptions.scales.y.grid,
          ...(props.options.scales?.y?.grid || {}),
        },
      },
    },
  };

  const type = props.config.type || "bar";

  return {
    type,
    data,
    options: mergedOptions,
  };
}

function initChart() {
  if (!chartRef.value) return;

  const cfg = buildChartConfig();

  if (chartInstance.value) {
    chartInstance.value.destroy();
  }

  chartInstance.value = new Chart(chartRef.value, cfg);
}

onMounted(() => {
  initChart();
});

// props.data나 props.options가 바뀌면 차트 업데이트
watch(
  () => [props.data, props.options, props.config.type],
  () => {
    initChart();
  },
  { deep: true }
);

onUnmounted(() => {
  if (chartInstance.value) {
    chartInstance.value.destroy();
  }
});
</script>

<style scoped>
/* 필요하면 캔버스 주변 레이아웃/크기 조정용 스타일만 추가 */
</style>
