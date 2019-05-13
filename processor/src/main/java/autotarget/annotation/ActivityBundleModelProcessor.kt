package autotarget.annotation

class ActivityBundleModelProcessor : BundleModelProcessor() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : Annotation> getElementAnnotationClass(): Class<T> {
        return ActivityTarget::class.java as Class<T>
    }
}
