<template>
  <!-- Card -->
  <div
    v-if="component === 'Card'"
    data-slot="card"
    :class="[
      'card-root flex flex-col gap-6 border py-6 shadow-sm rounded-xl',
      className,
    ]"
    v-bind="$attrs"
  >
    <slot />
  </div>

  <!-- CardHeader -->
  <div
    v-else-if="component === 'CardHeader'"
    data-slot="card-header"
    :class="[
      'card-header grid auto-rows-min grid-rows-[auto_auto] items-start gap-2 px-6',
      'has-[div[data-slot=card-action]]:grid-cols-[1fr_auto]',
      className,
    ]"
    v-bind="$attrs"
  >
    <slot />
  </div>

  <!-- CardTitle -->
  <div
    v-else-if="component === 'CardTitle'"
    data-slot="card-title"
    :class="['card-title leading-none font-semibold', className]"
    v-bind="$attrs"
  >
    <slot />
  </div>

  <!-- CardDescription -->
  <div
    v-else-if="component === 'CardDescription'"
    data-slot="card-description"
    :class="['card-description text-sm', className]"
    v-bind="$attrs"
  >
    <slot />
  </div>

  <!-- CardAction -->
  <div
    v-else-if="component === 'CardAction'"
    data-slot="card-action"
    :class="[
      'card-action col-start-2 row-span-2 row-start-1 self-start justify-self-end',
      className,
    ]"
    v-bind="$attrs"
  >
    <slot />
  </div>

  <!-- CardContent -->
  <div
    v-else-if="component === 'CardContent'"
    data-slot="card-content"
    :class="['card-content px-6', className]"
    v-bind="$attrs"
  >
    <slot />
  </div>

  <!-- CardFooter -->
  <div
    v-else-if="component === 'CardFooter'"
    data-slot="card-footer"
    :class="['card-footer flex items-center px-6', className]"
    v-bind="$attrs"
  >
    <slot />
  </div>
</template>

<script>
export default {
  name: "Card",
  props: {
    component: {
      type: String,
      required: true,
      validator: (value) =>
        [
          "Card",
          "CardHeader",
          "CardTitle",
          "CardDescription",
          "CardAction",
          "CardContent",
          "CardFooter",
        ].includes(value),
    },
    className: {
      type: String,
      default: "",
    },
  },
};
</script>

<style scoped>
.card-root {
  /* 라이트/다크모드 모두 global.css의 CSS 변수에 따라 자동 전환 */
  background-color: var(--card);
  color: var(--card-foreground);
  border-color: var(--border);
  border-radius: var(--radius);
}

/* Header 아래에 border가 실제로 있을 때만 padding-bottom 더해주는 용도는
   유틸리티 대신 여기서 간단히 처리해도 OK */
.card-header {
  border-bottom: 1px solid var(--border);
  padding-bottom: 1rem;
}

.card-description {
  color: var(--muted-foreground);
}

.card-footer {
  border-top: 1px solid var(--border);
  padding-top: 1rem;
}
</style>
