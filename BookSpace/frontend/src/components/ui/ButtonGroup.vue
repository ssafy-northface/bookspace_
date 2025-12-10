<template>
  <!-- ButtonGroup (그룹 컨테이너) -->
  <div
    v-if="component === 'ButtonGroup'"
    role="group"
    data-slot="button-group"
    :data-orientation="orientation"
    :class="groupClasses"
    v-bind="$attrs"
  >
    <slot />
  </div>

  <!-- ButtonGroupText -->
  <div
    v-else-if="component === 'ButtonGroupText'"
    data-slot="button-group-text"
    :class="[
      'bg-muted flex items-center gap-2 rounded-md border px-4 text-sm font-medium shadow-xs [&_svg]:pointer-events-none [&_svg:not([class*=size-])]:size-4',
      className,
    ]"
    v-bind="$attrs"
  >
    <slot />
  </div>

  <!-- ButtonGroupSeparator -->
  <div
    v-else-if="component === 'ButtonGroupSeparator'"
    data-slot="button-group-separator"
    :data-orientation="orientation"
    :class="separatorClasses"
    v-bind="$attrs"
  />
</template>

<script>
export default {
  name: "ButtonGroup",
  props: {
    component: {
      type: String,
      required: true,
      validator: (value) =>
        ["ButtonGroup", "ButtonGroupText", "ButtonGroupSeparator"].includes(
          value
        ),
    },
    orientation: {
      type: String,
      default: "horizontal", // 'horizontal' | 'vertical'
      validator: (v) => ["horizontal", "vertical"].includes(v),
    },
    className: {
      type: String,
      default: "",
    },
  },
  computed: {
    groupClasses() {
      const base =
        "flex w-fit items-stretch " +
        "[&>*]:focus-visible:z-10 [&>*]:focus-visible:relative " +
        "[&>input]:flex-1 " +
        "has-[>[data-slot=button-group]]:gap-2";

      const horizontal =
        "[&>*:not(:first-child)]:rounded-l-none " +
        "[&>*:not(:first-child)]:border-l-0 " +
        "[&>*:not(:last-child)]:rounded-r-none";

      const vertical =
        "flex-col " +
        "[&>*:not(:first-child)]:rounded-t-none " +
        "[&>*:not(:first-child)]:border-t-0 " +
        "[&>*:not(:last-child)]:rounded-b-none";

      const orientationClass =
        this.orientation === "horizontal" ? horizontal : vertical;

      return [base, orientationClass, this.className].join(" ");
    },
    separatorClasses() {
      const base = "bg-input relative m-0 self-stretch";
      const oClass =
        this.orientation === "vertical" ? "h-auto w-px" : "w-full h-px";

      return [base, oClass, this.className].join(" ");
    },
  },
};
</script>
