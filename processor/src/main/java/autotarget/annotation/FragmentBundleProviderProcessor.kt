package autotarget.annotation

class FragmentBundleProviderProcessor : BundleProviderProcessor() {

    override fun getBundlesClassName(): String {
        return "FragmentBundles"
    }
}
