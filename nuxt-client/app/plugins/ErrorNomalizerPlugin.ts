export default defineNuxtPlugin((_nuxtApp) => {
  const errorNormalizer = useErrorMormalizer()
  return {
    provide: {
      errorNormalizer, // useNuxtApp().$errorNormalizer
    },
  }
})
