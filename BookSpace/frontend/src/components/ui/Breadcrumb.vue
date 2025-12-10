<script>
import { defineComponent, h } from "vue";
import { ChevronRight, MoreHorizontal } from "lucide-vue-next";

const mergeClass = (base, extra) => [base, extra].filter(Boolean).join(" ");

export const Breadcrumb = defineComponent({
  name: "Breadcrumb",
  inheritAttrs: false,
  setup(_, { slots, attrs }) {
    return () =>
      h(
        "nav",
        {
          ...attrs,
          "aria-label": "breadcrumb",
          "data-slot": "breadcrumb",
        },
        slots.default?.(),
      );
  },
});

export const BreadcrumbList = defineComponent({
  name: "BreadcrumbList",
  inheritAttrs: false,
  setup(_, { slots, attrs }) {
    const base =
      "text-muted-foreground flex flex-wrap items-center gap-1.5 text-sm break-words sm:gap-2.5";
    return () => {
      const { class: className, ...rest } = attrs;
      return h(
        "ol",
        {
          ...rest,
          "data-slot": "breadcrumb-list",
          class: mergeClass(base, className),
        },
        slots.default?.(),
      );
    };
  },
});

export const BreadcrumbItem = defineComponent({
  name: "BreadcrumbItem",
  inheritAttrs: false,
  setup(_, { slots, attrs }) {
    const base = "inline-flex items-center gap-1.5";
    return () => {
      const { class: className, ...rest } = attrs;
      return h(
        "li",
        {
          ...rest,
          "data-slot": "breadcrumb-item",
          class: mergeClass(base, className),
        },
        slots.default?.(),
      );
    };
  },
});

export const BreadcrumbLink = defineComponent({
  name: "BreadcrumbLink",
  inheritAttrs: false,
  props: {
    asChild: {
      type: Boolean,
      default: false,
    },
  },
  setup(props, { slots, attrs }) {
    const base = "hover:text-foreground transition-colors";
    return () => {
      const { class: className, ...rest } = attrs;
      const Tag = props.asChild ? "span" : "a";
      return h(
        Tag,
        {
          ...rest,
          "data-slot": "breadcrumb-link",
          class: mergeClass(base, className),
        },
        slots.default?.(),
      );
    };
  },
});

export const BreadcrumbPage = defineComponent({
  name: "BreadcrumbPage",
  inheritAttrs: false,
  setup(_, { slots, attrs }) {
    const base = "text-foreground font-normal";
    return () => {
      const { class: className, ...rest } = attrs;
      return h(
        "span",
        {
          ...rest,
          "data-slot": "breadcrumb-page",
          role: "link",
          "aria-disabled": "true",
          "aria-current": "page",
          class: mergeClass(base, className),
        },
        slots.default?.(),
      );
    };
  },
});

export const BreadcrumbSeparator = defineComponent({
  name: "BreadcrumbSeparator",
  inheritAttrs: false,
  setup(_, { slots, attrs }) {
    const base = "[&>svg]:size-3.5";
    return () => {
      const { class: className, ...rest } = attrs;
      const content = slots.default?.() ?? [h(ChevronRight)];
      return h(
        "li",
        {
          ...rest,
          "data-slot": "breadcrumb-separator",
          role: "presentation",
          "aria-hidden": "true",
          class: mergeClass(base, className),
        },
        content,
      );
    };
  },
});

export const BreadcrumbEllipsis = defineComponent({
  name: "BreadcrumbEllipsis",
  inheritAttrs: false,
  setup(_, { slots, attrs }) {
    const base = "flex size-9 items-center justify-center";
    return () => {
      const { class: className, ...rest } = attrs;
      return h(
        "span",
        {
          ...rest,
          "data-slot": "breadcrumb-ellipsis",
          role: "presentation",
          "aria-hidden": "true",
          class: mergeClass(base, className),
        },
        slots.default?.() ?? [
          h(MoreHorizontal, { class: "size-4" }),
          h("span", { class: "sr-only" }, "More"),
        ],
      );
    };
  },
});

export default Breadcrumb;
</script>
