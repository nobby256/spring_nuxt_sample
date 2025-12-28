export const usePager = (maxPageSize: number) => {
  const pageNo = ref(1)

  const hasNextPage = () => {
    return pageNo.value < maxPageSize
  }

  const hasPrevPage = () => {
    return pageNo.value > 1
  }

  const nextPage = () => {
    if (hasNextPage()) {
      pageNo.value++
    }
  }

  const prevPage = () => {
    if (hasPrevPage()) {
      pageNo.value--
    }
  }

  return {
    pageNo,
    hasNextPage,
    hasPrevPage,
    nextPage,
    prevPage,
  }
}
