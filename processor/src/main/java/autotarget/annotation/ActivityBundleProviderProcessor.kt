package autotarget.annotation

class ActivityBundleProviderProcessor : BundleProviderProcessor() {

    override fun getBundlesClassName(): String {
        return "ActivityBundles"
    }
}
