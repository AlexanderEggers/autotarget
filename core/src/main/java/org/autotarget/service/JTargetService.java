package org.autotarget.service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class JTargetService extends TargetService {

    @Inject
    public JTargetService() {
        //do nothing here
    }

    public void execute(ActivityTarget target) {
        super.execute(target, 0, 0);
    }

    public void execute(ActivityTarget target, int flags) {
        super.execute(target, flags, 0);
    }

    public void execute(FragmentTarget target) {
        super.execute(target, target.getContainerId(), true, false);
    }

    public void execute(FragmentTarget target, int containerId) {
        super.execute(target, containerId, true, false);
    }

    public void execute(FragmentTarget target, int containerId, boolean addToBackStack) {
        super.execute(target, containerId, addToBackStack, false);
    }
}
