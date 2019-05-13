package autotarget.annotation

class FragmentBundleModelProcessor : BundleModelProcessor() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : Annotation> getElementAnnotationClass(): Class<T> {
        return FragmentTarget::class.java as Class<T>
    }
}
