Changelog
==========

Version 0.7.1 *(2018-08-21)*
----------------------------
- **BUGFIX:** Added data intent to TargetService.finishWithResult(...).

Version 0.7.0 *(2018-08-19)*
----------------------------
- **NEW:** Replaced JTargetService with JvmOverloads that will generate the
needed methods. The only needed class is the TargetService from now on.
- **NEW:** Tweaking processor implementation to improve error handling.
- **BUGFIX:** Made several methods open to improve testability.
- **MISC:** Updated Javapoet to version 1.11.1.
- **MISC:** Updated Kotlin to version 1.2.60.

Version 0.6.0 *(2018-05-31)*
----------------------------
- Removed ContextProvider and ContextInjector and replaced them with the ContextProvider from the archknife-context artifact to simplify the usage of archknife and autotarget when using them in one project.

Version 0.5.0 *(2018-05-12)*
----------------------------
- Moved library from org.autotarget.* to autotarget.*
- Updated Kotlin to version 1.2.41

Version 0.4.1 *(2018-04-27)*
----------------------------
- Fixed HasFragmentFlow implementation if the actual implementation of this interface has not done proper or simplify not done yet. The two different states (implemented/not implemented yet) are determined by the return value of the interface's method. Keep in mind that the interface HasFragmentFlow is fully optional and should only be used if you want to implement your own way in how Fragments are added to the underlying Activity.

Version 0.4.0 *(2018-04-25)*
----------------------------
- Added new class JTargetService which is extending the already existing TargetService class. JTargetService is optimized for the usage within Java-based source code.
- Tweaking the usage of containerId which is needed for the fragment implementation. The developer has the optional to either set a global containerId for the fragment or set a custom containerId via the execute method which is provided by the TargetService/JTargetService. The global containerId is possible to be overriden by the method-based container id.
- The method of the interface HasFragmentFlow includes the containerId provided by the TargetService/JTargetService.

Version 0.3.0 *(2018-04-24)*
----------------------------
- Renamed project to AutoTarget. Due to this change all dependencies have been changed. Take a look at the readme.
- Renamed IntentService to TargetService
- Renamed public methods of the IntentService from navigate(...) to execute(...)
- Added @FragmentTarget to the library which can be used to navigate to Fragments using the TargetService

Version 0.2.0 *(2018-04-23)*
----------------------------
- Revamped most of the processor code
- Renamed @ForActivity into @ActivityTarget and @IntentParameter into @TargetParameter
- Changed the usage of the library to improve build performance. Take a look at the example project to see the required changes that are needed.

Version 0.1.0 *(2018-04-03)*
----------------------------

- Initial library release.
