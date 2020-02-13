package com.smanager.wrappers;

public class RegisterUnregisterWrapper {

    private StudentRegisterWrapper registerWrapper;
    private StudentRegisterWrapper unregisterWrapper;

    public RegisterUnregisterWrapper(StudentRegisterWrapper registerWrapper, StudentRegisterWrapper unregisterWrapper) {
        this.registerWrapper = registerWrapper;
        this.unregisterWrapper = unregisterWrapper;
    }

    public StudentRegisterWrapper getRegisterWrapper() {
        return registerWrapper;
    }

    public void setRegisterWrapper(StudentRegisterWrapper registerWrapper) {
        this.registerWrapper = registerWrapper;
    }

    public StudentRegisterWrapper getUnregisterWrapper() {
        return unregisterWrapper;
    }

    public void setUnregisterWrapper(StudentRegisterWrapper unregisterWrapper) {
        this.unregisterWrapper = unregisterWrapper;
    }
}
