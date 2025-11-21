<template>
  <div class="layout">
    <div class="menu">
      <site-menu />
    </div>
    <div class="content">
      <slot />
    </div>
  </div>
</template>

<style>
.layout {
  display: flex;
}

.menu {
  width: 300px;
}

.content {
  /* menuとcontentの隙間 */
  margin-left: 10px;
}
</style>
