AutoTarget
=====
[![Download](https://api.bintray.com/packages/mordag/android/autotarget-core/images/download.svg) ](https://bintray.com/mordag/android/autotarget-core/_latestVersion)

AutoTarget is an annotation processor to generate a service which can be used to handle your app navigation.

Download
--------
You can use Gradle to download this libray:

```gradle
repositories {
  jcenter()
}

dependencies {
   def autotarget_version = "1.0.0-rc1"

  //Bundles all artifacts for the library including some additional helper classes
  implementation "org.autotarget:autotarget-core:$autotarget_version"

  //Only for annotation classes
  implementation "org.autotarget:autotarget-annotation:$autotarget_version"
  
  //requires autotarget-core to work
  kapt "org.autotarget:autotarget-processor:$autotarget_version"
}
```

How do I use AutoTarget? (Step-by-step introduction for 1.0.0-rc1)
-------------------

1. Add the annotations

Currently the library is only supporting activities and fragments. If you want to access a certain activity by another activity you have to use the @ActivityTarget annotation. Because activities sometimes require certain bundle values to show the right information, you can use the annotation @TargetParameter. This annotation allows you to define the required and optional bundle values for this activity. The annotation expects an Array and therefore you can define n TargetParameterItem.

```kotlin

@ActivityTarget
@TargetParameter([TargetParameterItem(key="MY_KEY", type=String::class, name="myDemoValue", required=true)])
class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...
    }
}
```

Fragments have a similar usage except you can to define certain config values within the @FragmentTarget annotation. Those values are for the fragment container, the TAG and a state. This annotation allows you to define the required and optional bundle values for this fragment. The annotation expects an Array and therefore you can define n TargetParameterItem.

```kotlin

@FragmentTarget(R.id.fragment_container)
@TargetParameter([TargetParameterItem(key="MY_KEY", type=String::class, name="myDemoValue", required=true)])
class DemoFragment : Fragment() {
     ...
}
```

The library is supporting two ways which will create and display fragments. You can either implement the FragmentDispatcher interface to your activity or rely on the internal implementation. The FragmentDispatcher interface allows you to implement your own solution in how fragments will be created.

The relevant TargetParameterItem annotation can have a key, the type of the parameter, a name, a group array and a required flag. The parameter name is for the generated method to improve the readability for that specific value. The group array can be used to group optional parameters. The required flag defines if a certain parameter is always required when accessing the specific target or not. Required parameter values are part of every method that shows a new target object.

```kotlin

@FragmentTarget(R.id.fragment_container)
@TargetParameter([TargetParameterItem(key="MY_KEY", type=String::class, name="myDemoValue", group=["deeplinking"], required=true)
class DemoFragment : Fragment() {
     ...
}
```

2. Call your target!

To call your DemoActivity, you need to create a reference of the TargetService class. You need to use one of the execute methods provided by this class. Activity based calls require a ActivityTarget object. This can be received by using the generated class ActivityTargets. This class holds all relevant methods to generate the needed ActivityTarget object for you.

```kotlin

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var contextProvider: ContextProvider

    //the TargetService has a dependency to the ContextProvider object
    @Inject
    lateinit var targetService: TargetService

    override fun onResume() {
        super.onResume()
        contextProvider.context = this //the TargetService always requires a indirect reference to the current active Activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            targetService.execute(ActivityTargets.showDemoActivity("Test successful!"))
        }
    }
}

```

Using Fragments, the implemention is quite similar. You also need to use the TargetService. To access the generated methods for your Fragments, you need to use the FragmentTargets class.

```kotlin

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var contextProvider: ContextProvider

    //The TargetService has a dependency to the ContextProvider object
    @Inject
    lateinit var targetService: TargetService

    override fun onResume() {
        super.onResume()
        contextProvider.context = this //the TargetService always requires a indirect reference to the current active Activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            targetService.execute(FragmentsTarget.showDemoFragment())
        }
    }
}

```
3. Retrieve the parameter values in your new target

When you are trying to retrieve the bundle parameter in your new activity or fragment normally you would need to use the intent.extras or fragment.arguments. This library is offering type-safe bundle models that convert the bundle object in your new activity/fragment into something you can use directly in your code without worring about keys, right types or nullability issues.

To access the bundle models you have use the generated classes ActivityBundles or FragmentBundles. Each class has methods that take the activity or fragment bundle object. The return type is a new model class that filters out all the relevant bundle parameter and assigns it to typed parameter in that model.

For an activity you can use the implementation like that.

```kotlin

@ActivityTarget
@TargetParameter([TargetParameterItem(key="MY_KEY", type=String::class, name="myDemoValue", required=true)])
class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundleModel = ActivityBundles.getNextActivityBundleModel(intent.extras!!)
        
        val textView = findViewById<TextView>(R.id.textView)
        textView.text = bundleModel.myDemoValue
    }
}

```
Using bundle models for fragments is quite similar.

```kotlin

@FragmentTarget(R.id.fragment_container)
@TargetParameter([TargetParameterItem(key="MY_KEY", type=String::class, name="myDemoValue", group=["deeplinking"], required=true)
class DemoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ...

        val bundleModel = FragmentBundles.getFragmentDemoBundleModel(arguments!!)
        
        val textView = findViewById<TextView>(R.id.textView)
        textView.text = bundleModel.myDemoValue
    }
}

```

Status
------
Version 1.0.0 is currently under development in the master branch.

Comments/bugs/questions/pull requests are always welcome!

Compatibility
-------------
 * AutoTarget requires at minimum Android 16.

Author
------
Alexander Eggers - [@mordag][2] on GitHub

License
-------
Apache 2.0. See the [LICENSE][1] file for details.


[1]: https://github.com/Mordag/autotarget/blob/master/LICENSE
[2]: https://github.com/Mordag
[3]: https://github.com/Mordag/archknife
