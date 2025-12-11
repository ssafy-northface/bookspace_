<template>
  <button
    :type="type"
    :class="[buttonClass, loading ? 'cursor-wait opacity-80' : '']"
    :disabled="disabled || loading"
    data-slot="button"
    v-bind="$attrs"
  >
    <!-- 로딩일 때 스피너 -->
    <span
      v-if="loading"
      class="w-4 h-4 mr-2 border-2 rounded-full animate-spin border-t-transparent"
    ></span>
    <slot />
  </button>
</template>

<script>
export default {
  name: "Button",
  props: {
    variant: {
      type: String,
      default: "default",
      validator: (v) =>
        [
          "default",
          "destructive",
          "outline",
          "secondary",
          "ghost",
          "link",
        ].includes(v),
    },
    size: {
      type: String,
      default: "default",
      validator: (v) =>
        ["default", "sm", "lg", "icon", "icon-sm", "icon-lg"].includes(v),
    },
    type: {
      type: String,
      default: "button",
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    loading: {
      type: Boolean,
      default: false,
    },
    className: {
      type: String,
      default: "",
    },
  },

  computed: {
    buttonClass() {
      const base =
        "inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium " +
        "transition-all duration-150 ease-out " +
        "disabled:pointer-events-none disabled:opacity-50 " +
        "[&_svg]:pointer-events-none [&_svg:not([class*='size-'])]:size-4 shrink-0 [&_svg]:shrink-0 " +
        "outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] " +
        "aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive " +
        "hover:scale-95";

      const variants = {
        default:
          "bg-primary text-primary-foreground hover:bg-primary/90 active:bg-primary/80",
        destructive:
          "bg-destructive text-white hover:bg-destructive/90 active:bg-destructive/80 " +
          "focus-visible:ring-destructive/20 dark:focus-visible:ring-destructive/40 dark:bg-destructive/60",
        outline:
          "border bg-background shadow-xs hover:bg-accent hover:text-accent-foreground " +
          "active:bg-accent/60 active:text-accent-foreground/80 " +
          "dark:bg-input/30 dark:border-input dark:hover:bg-input/50",
        secondary:
          "bg-secondary text-secondary-foreground hover:bg-secondary/80 active:bg-secondary/70",
        ghost:
          "hover:bg-accent hover:text-accent-foreground active:bg-accent/60 dark:hover:bg-accent/50",
        link: "text-primary underline-offset-4 hover:underline active:text-primary/70",
      };

      const sizes = {
        default: "h-9 px-4 py-2 has-[>svg]:px-3",
        sm: "h-8 rounded-md gap-1.5 px-3 has-[>svg]:px-2.5",
        lg: "h-10 rounded-md px-6 has-[>svg]:px-4",
        icon: "size-9",
        "icon-sm": "size-8",
        "icon-lg": "size-10",
      };

      return [
        base,
        variants[this.variant],
        sizes[this.size],
        this.className,
      ].join(" ");
    },
  },
};
</script>
